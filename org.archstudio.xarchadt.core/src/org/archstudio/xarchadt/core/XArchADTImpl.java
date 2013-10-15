package org.archstudio.xarchadt.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.xpath.XPathException;

import org.archstudio.sysutils.SystemUtils;
import org.archstudio.xadl3.xadlcore_3_0.Xadlcore_3_0Package;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.IXArchADTFeature;
import org.archstudio.xarchadt.IXArchADTFeature.FeatureType;
import org.archstudio.xarchadt.IXArchADTFeature.ValueType;
import org.archstudio.xarchadt.IXArchADTFileListener;
import org.archstudio.xarchadt.IXArchADTModelListener;
import org.archstudio.xarchadt.IXArchADTPackageMetadata;
import org.archstudio.xarchadt.IXArchADTSubstitutionHint;
import org.archstudio.xarchadt.IXArchADTTypeMetadata;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.XArchADTFileEvent;
import org.archstudio.xarchadt.XArchADTFileEvent.EventType;
import org.archstudio.xarchadt.XArchADTModelEvent;
import org.archstudio.xarchadt.core.internal.BasicXArchADTFeature;
import org.archstudio.xarchadt.core.internal.BasicXArchADTPackageMetadata;
import org.archstudio.xarchadt.core.internal.BasicXArchADTTypeMetadata;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.emf.xpath.EcoreXPathContextFactory;
import org.eclipse.e4.emf.xpath.XPathContextFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.ElementHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.GenericXMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

public class XArchADTImpl implements IXArchADT {

	protected final List<IXArchADTModelListener> modelListeners = Lists.newCopyOnWriteArrayList();
	protected final List<IXArchADTFileListener> fileListeners = Lists.newCopyOnWriteArrayList();
	protected final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	protected final Lock rLock = rwLock.readLock();
	protected final Lock wLock = rwLock.writeLock();

	// All the resources here indexed by URI
	private final ResourceSet resourceSet;

	private static final Map<Object, Object> LOAD_OPTIONS_MAP = new HashMap<Object, Object>();
	static {
		// This allows root elements in other schema
		LOAD_OPTIONS_MAP.put(XMLResource.OPTION_EXTENDED_META_DATA, true);
		// optimizations from http://www.eclipse.org/modeling/emf/docs/performance/EMFPerformanceTips.html
		LOAD_OPTIONS_MAP.put(XMLResource.OPTION_DEFER_ATTACHMENT, true);
		LOAD_OPTIONS_MAP.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);
		LOAD_OPTIONS_MAP.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
		LOAD_OPTIONS_MAP.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<Object, Object>());
	}

	private static final ElementHandlerImpl elementHandlerImpl = new ElementHandlerImpl(false);
	private static final Map<Object, Object> SAVE_OPTIONS_MAP = new HashMap<Object, Object>();
	static {
		// This voodoo supports substitution groups properly.
		SAVE_OPTIONS_MAP.put(XMLResource.OPTION_ELEMENT_HANDLER, elementHandlerImpl);
	}

	private static final String EMF_EXTENSION_POINT_ID = "org.eclipse.emf.ecore.generated_package";

	public XArchADTImpl() {
		resourceSet = new ResourceSetImpl();
		resourceSet.eAdapters().add(new ContentAdapter());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("xml", new GenericXMLResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap()
				.put("xml", new GenericXMLResourceFactoryImpl());

		registerAllSchemaPackages();
	}

	/**
	 * This method causes the EPackage.eINSTANCE variable of each EMF-generated bundle (plugin) in Eclipse to be
	 * touched. This causes the package to spontaneously register itself with the EPackage Registry, which is how we
	 * find out what EPackages are available on the system. This is an EMF-ism that isn't easily avoided. If you are
	 * running outside of ArchStudio then these bundles will not be available. What you have to do then is just read the
	 * eINSTANCE variable for all the EPackages you want to have available to you.
	 */
	private void registerAllSchemaPackages() {
		if (Platform.isRunning()) {
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			// The Extension Registry can be null if we're running outside of Eclipse
			for (IConfigurationElement configurationElement : reg.getConfigurationElementsFor(EMF_EXTENSION_POINT_ID)) {
				String packageClassName = configurationElement.getAttribute("class");
				if (packageClassName != null) {
					String bundleName = configurationElement.getDeclaringExtension().getContributor().getName();
					try {
						Class<?> packageClass = Platform.getBundle(bundleName).loadClass(packageClassName);
						Field instanceField = packageClass.getDeclaredField("eINSTANCE");
						/* EPackage ePackage = (EPackage) */instanceField.get(packageClass);
					}
					catch (ClassNotFoundException cnfe) {
					}
					catch (NoSuchFieldException nsfe) {
					}
					catch (IllegalAccessException iae) {
					}
				}
			}
		}
	}

	// Map ObjRef <-> EObjects
	private final Map<ObjRef, EObject> objRefToEObject = new MapMaker().weakKeys().makeMap();
	private final Map<EObject, ObjRef> eObjectToObjRef = new MapMaker().softValues().makeMap();
	private final ReentrantReadWriteLock objRefToEObjectLockRWLock = new ReentrantReadWriteLock();
	private final Lock objRefToEObjectLockRLock = objRefToEObjectLockRWLock.readLock();
	private final Lock objRefToEObjectLockWLock = objRefToEObjectLockRWLock.writeLock();

	@Nullable
	protected ObjRef putNullable(@Nullable EObject eObject) {
		return eObject != null ? put(eObject) : null;
	}

	protected ObjRef put(EObject eObject) {
		objRefToEObjectLockRLock.lock();
		try {
			ObjRef objRef = eObjectToObjRef.get(eObject);
			if (objRef == null) {
				objRefToEObjectLockRLock.unlock();
				try {
					objRefToEObjectLockWLock.lock();
					try {
						objRef = new ObjRef();
						eObjectToObjRef.put(eObject, objRef);
						objRefToEObject.put(objRef, eObject);
					}
					finally {
						objRefToEObjectLockWLock.unlock();
					}
				}
				finally {
					objRefToEObjectLockRLock.lock();
				}
			}
			return objRef;
		}
		finally {
			objRefToEObjectLockRLock.unlock();
		}
	}

	protected List<ObjRef> putAll(Iterable<? extends EObject> eObjects) {
		List<ObjRef> objRefs = Lists.newArrayList();
		for (EObject eObject : eObjects) {
			objRefs.add(put(eObject));
		}
		return objRefs;
	}

	protected EObject get(ObjRef objRef) {
		objRefToEObjectLockRLock.lock();
		try {
			EObject eObject = objRefToEObject.get(objRef);
			if (eObject == null) {
				throw new IllegalArgumentException("No such object: " + objRef);
			}
			return eObject;
		}
		finally {
			objRefToEObjectLockRLock.unlock();
		}
	}

	protected List<EObject> getAll(Iterable<ObjRef> objRefs) {
		List<EObject> eObjects = Lists.newArrayList();
		for (ObjRef objRef : objRefs) {
			eObjects.add(get(objRef));
		}
		return eObjects;
	}

	@Nullable
	protected Serializable check(@Nullable Object object) {
		if (object instanceof EObject) {
			return put((EObject) object);
		}
		return (Serializable) object;
	}

	@Nullable
	protected Object uncheck(@Nullable Serializable serializable) {
		if (serializable instanceof ObjRef) {
			return get((ObjRef) serializable);
		}
		return serializable;
	}

	// Dynamically maps feature names (either uppercase or lowercase) of an EClass to EStructuralFeatures.
	private static final LoadingCache<EClass, Map<String, EStructuralFeature>> caselessFeatureCache = CacheBuilder
			.newBuilder().build(new CacheLoader<EClass, Map<String, EStructuralFeature>>() {

				Map<String, EStructuralFeature> structuralFeatures = Maps.newHashMap();
				Map<Integer, EStructuralFeature> featureIDs = Maps.newHashMap();

				@Override
				public Map<String, EStructuralFeature> load(@Nullable EClass eClass) throws Exception {
					structuralFeatures = Maps.newHashMap();
					featureIDs = Maps.newHashMap();
					apply0(eClass);
					return structuralFeatures;
				}

				private void apply0(@Nullable EClass eClass) {
					if (eClass == null) {
						throw new NullPointerException();
					}

					for (EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()) {
						EStructuralFeature conflictingEStructuralFeature = featureIDs.put(
								eStructuralFeature.getFeatureID(), eStructuralFeature);
						if (conflictingEStructuralFeature != null) {
							throw new RuntimeException("Unexpected structural feature");
						}
						String name = eStructuralFeature.getName();
						structuralFeatures.put(SystemUtils.capFirst(name), eStructuralFeature);
						structuralFeatures.put(SystemUtils.uncapFirst(name), eStructuralFeature);
					}
					for (EClass eSuperClass : eClass.getESuperTypes()) {
						apply0(eSuperClass);
					}
				}
			});

	private static final EStructuralFeature getEFeature(EClass eClass, String featureName, boolean many) {
		EStructuralFeature eFeature = caselessFeatureCache.getUnchecked(eClass).get(featureName);
		if (eFeature == null) {
			throw new IllegalArgumentException(SystemUtils.message(//
					"EClass '$0' does not contain EFeature '$1' in EPackage '$2'.",//
					eClass.getName(), featureName, eClass.getEPackage().getNsURI()));
		}
		if (eFeature.isMany() && !many) {
			throw new IllegalArgumentException(SystemUtils.message(//
					"EFeature '$0' is many in EClass '$1:$2'.",//
					featureName, eClass.getEPackage().getNsURI(), eClass.getName()));
		}
		if (!eFeature.isMany() && many) {
			throw new IllegalArgumentException(SystemUtils.message(//
					"EFeature '$0' is single in EClass '$1:$2'.",//
					featureName, eClass.getEPackage().getNsURI(), eClass.getName()));
		}
		return eFeature;
	}

	protected static final EStructuralFeature getEFeature(EObject eObject, String featureName, boolean many) {
		return getEFeature(eObject.eClass(), featureName, many);
	}

	@SuppressWarnings({ "unchecked" })
	private static final EList<Object> getEList(EObject eObject, String featureName) {
		try {
			return (EList<Object>) eObject.eGet(getEFeature(eObject.eClass(), featureName, true));
		}
		catch (ClassCastException e) {
			throw new RuntimeException(SystemUtils.message(//
					"EObject of type '$0:$1' does not have an EList for feature '$2'", //
					eObject.eClass().getEPackage().getNsURI(), eObject.eClass().getName(), featureName), e);
		}
	}

	@Override
	public boolean isValidObjRef(ObjRef objRef) {
		try {
			return get(objRef) != null;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void set(ObjRef baseObjRef, String typeOfThing, @Nullable Serializable value) {
		wLock.lock();
		try {
			EObject baseEObject = get(baseObjRef);
			baseEObject.eSet(getEFeature(baseEObject, typeOfThing, false), uncheck(value));
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public @Nullable
	Serializable get(ObjRef baseObjRef, String typeOfThing) {
		rLock.lock();
		try {
			return get(baseObjRef, typeOfThing, true);
		}
		finally {
			rLock.unlock();
		}
	}

	public @Nullable
	Serializable get(ObjRef baseObjRef, String typeOfThing, boolean resolve) {
		rLock.lock();
		try {
			EObject baseEObject = get(baseObjRef);
			return check(baseEObject.eGet(getEFeature(baseEObject, typeOfThing, false), resolve));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public @Nullable
	Serializable resolve(ObjRef objRef) {
		rLock.lock();
		try {
			EObject baseEObject = get(objRef);
			return check(EcoreUtil.resolve(baseEObject, baseEObject.eResource()));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public void clear(ObjRef baseObjRef, String typeOfThing) {
		wLock.lock();
		try {
			EObject baseEObject = get(baseObjRef);
			baseEObject.eUnset(getEFeature(baseEObject, typeOfThing, false));
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public void add(ObjRef baseObjRef, String typeOfThing, Serializable thingToAdd) {
		wLock.lock();
		try {
			getEList(get(baseObjRef), typeOfThing).add(uncheck(thingToAdd));
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public void add(ObjRef baseObjRef, String typeOfThing, Collection<? extends Serializable> thingsToAdd) {
		wLock.lock();
		try {
			for (Serializable thingToAdd : thingsToAdd) {
				getEList(get(baseObjRef), typeOfThing).add(uncheck(thingToAdd));
			}
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public List<Serializable> getAll(ObjRef baseObjRef, String typeOfThing) {
		rLock.lock();
		try {
			EList<Object> list = getEList(get(baseObjRef), typeOfThing);
			List<Serializable> result = Lists.newArrayListWithCapacity(list.size());
			for (Object object : list) {
				result.add(check(object));
			}
			return result;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public void remove(ObjRef baseObjRef, String typeOfThing, Serializable thingToRemove) {
		wLock.lock();
		try {
			getEList(get(baseObjRef), typeOfThing).remove(uncheck(thingToRemove));
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public void remove(ObjRef baseObjRef, String typeOfThing, Collection<? extends Serializable> thingsToRemove) {
		wLock.lock();
		try {
			for (Serializable thingToRemove : thingsToRemove) {
				getEList(get(baseObjRef), typeOfThing).remove(uncheck(thingToRemove));
			}
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef getByID(ObjRef documentRef, String id) {
		rLock.lock();
		try {
			return putNullable(get(documentRef).eResource().getEObject(id));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef getByID(String id) {
		rLock.lock();
		try {
			for (Resource r : resourceSet.getResources()) {
				EObject objectByID = r.getEObject(id);
				if (objectByID != null) {
					return put(objectByID);
				}
			}
			return null;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef getParent(ObjRef targetObjRef) {
		rLock.lock();
		try {
			return putNullable(get(targetObjRef).eContainer());
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public boolean hasAncestor(ObjRef childObjRef, ObjRef ancestorObjRef) {
		rLock.lock();
		try {
			return EcoreUtil.isAncestor(get(ancestorObjRef), get(childObjRef));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<ObjRef> getAllAncestors(ObjRef targetObjRef) {
		rLock.lock();
		try {
			EObject eObject = get(targetObjRef);
			List<ObjRef> ancestorObjRefs = Lists.newArrayList();
			while (eObject != null) {
				ancestorObjRefs.add(put(eObject));
				eObject = eObject.eContainer();
			}
			return ancestorObjRefs;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<ObjRef> getLineage(ObjRef targetObjRef) {
		rLock.lock();
		try {
			return Lists.reverse(getAllAncestors(targetObjRef));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public boolean isAttached(ObjRef targetObjRef) {
		rLock.lock();
		try {
			EObject eObject = get(targetObjRef);
			Resource resource = eObject.eResource();
			if (resource != null) {
				return EcoreUtil.isAncestor(resource, eObject);
			}
			return false;
		}
		finally {
			rLock.unlock();
		}
	}

	private static IXArchADTFeature.ValueType getValueType(EStructuralFeature feature) {
		Class<?> c = feature.getEType().getInstanceClass();
		if (c == null) {
			return ValueType.OBJECT;
		}
		else if (Enumerator.class.isAssignableFrom(c)) {
			return ValueType.ENUMERATION;
		}
		else if (String.class.isAssignableFrom(c)) {
			return ValueType.STRING;
		}
		else {
			return ValueType.OBJECT;
		}
	}

	private static final LoadingCache<String, EPackage> ePackageCache = CacheBuilder.newBuilder().build(
			new CacheLoader<String, EPackage>() {

				@Override
				public EPackage load(@Nullable String nsURI) throws Exception {
					EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
					if (ePackage != null) {
						return ePackage;
					}
					throw new NullPointerException(SystemUtils.message("No EPackage with nsURI: $0", nsURI));
				}
			});

	private static final LoadingCache<EClass, Map<String, IXArchADTFeature>> featureMetadataCache = CacheBuilder
			.newBuilder().build(new CacheLoader<EClass, Map<String, IXArchADTFeature>>() {

				@Override
				public Map<String, IXArchADTFeature> load(@Nullable EClass eClass) throws Exception {
					if (eClass == null) {
						throw new NullPointerException();
					}

					List<IXArchADTFeature> features = Lists.newArrayList();
					features.addAll(Collections2.transform(eClass.getEAllAttributes(),
							new Function<EAttribute, IXArchADTFeature>() {

								@Override
								public IXArchADTFeature apply(@Nullable EAttribute eFeature) {
									if (eFeature == null) {
										throw new NullPointerException();
									}
									return new BasicXArchADTFeature(eFeature.getName(), eFeature.getEType()
											.getEPackage().getNsURI(), eFeature.getEType().getName(),
											FeatureType.ATTRIBUTE, getValueType(eFeature), false);
								};
							}));
					features.addAll(Collections2.transform(eClass.getEAllReferences(),
							new Function<EReference, IXArchADTFeature>() {

								@Override
								public IXArchADTFeature apply(@Nullable EReference eFeature) {
									if (eFeature == null) {
										throw new NullPointerException();
									}
									return new BasicXArchADTFeature(eFeature.getName(), eFeature.getEType()
											.getEPackage().getNsURI(), eFeature.getEType().getName(),
											eFeature.isMany() ? FeatureType.ELEMENT_MULTIPLE
													: FeatureType.ELEMENT_SINGLE, getValueType(eFeature), !eFeature
													.isContainment());
								};
							}));
					// EClass allows multiple features by the same name, see #16
					Map<String, IXArchADTFeature> featureMap = Maps.newHashMap();
					for (IXArchADTFeature f : features) {
						featureMap.put(f.getName(), f);
					}
					return featureMap;
				}
			});

	/**
	 * This is a map that generates type metadata for an EClass on demand, caching it after it's generated.
	 */
	private static final LoadingCache<EClass, IXArchADTTypeMetadata> typeMetadataCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<EClass, IXArchADTTypeMetadata>() {

				@Override
				public IXArchADTTypeMetadata load(@Nullable EClass eClass) throws Exception {
					if (eClass == null) {
						throw new NullPointerException();
					}
					return new BasicXArchADTTypeMetadata(eClass.getEPackage().getNsURI(), eClass.getName(),
							featureMetadataCache.get(eClass), eClass.isAbstract());
				}
			});

	/**
	 * This is a map that generates package metadata for an EPackage on demand, caching it after it's generated.
	 */
	private static final LoadingCache<EPackage, IXArchADTPackageMetadata> packageMetatadataCache = CacheBuilder
			.newBuilder().build(new CacheLoader<EPackage, IXArchADTPackageMetadata>() {

				@Override
				public IXArchADTPackageMetadata load(@Nullable EPackage ePackage) throws Exception {
					if (ePackage == null) {
						throw new NullPointerException();
					}
					return new BasicXArchADTPackageMetadata(ePackage.getNsURI(), Iterables.transform(
							Iterables.filter(ePackage.getEClassifiers(), EClass.class),
							new Function<EClass, IXArchADTTypeMetadata>() {

								@Override
								public IXArchADTTypeMetadata apply(@Nullable EClass eClass) {
									return typeMetadataCache.getUnchecked(eClass);
								};
							}));
				}
			});

	private static final EClass getEClass(EPackage ePackage, String typeName) {
		EClassifier eClassifier = ePackage.getEClassifier(typeName);
		if (eClassifier == null) {
			eClassifier = ePackage.getEClassifier(SystemUtils.capFirst(typeName));
		}
		if (eClassifier == null) {
			throw new IllegalArgumentException(SystemUtils.message(
					"EPackage '$0' does not contain an EClass named '$1'", ePackage, typeName));
		}
		if (eClassifier instanceof EClass) {
			return (EClass) eClassifier;
		}
		throw new IllegalArgumentException("Classifier was not an instance of EClass");
	}

	@Override
	public IXArchADTPackageMetadata getPackageMetadata(String nsURI) {
		rLock.lock();
		try {
			return packageMetatadataCache.getUnchecked(ePackageCache.getUnchecked(nsURI));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public IXArchADTTypeMetadata getTypeMetadata(String nsURI, String typeName) {
		rLock.lock();
		try {
			return typeMetadataCache.getUnchecked(getEClass(ePackageCache.getUnchecked(nsURI), typeName));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public IXArchADTTypeMetadata getTypeMetadata(ObjRef objRef) {
		rLock.lock();
		try {
			return typeMetadataCache.getUnchecked(get(objRef).eClass());
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<IXArchADTPackageMetadata> getAvailablePackageMetadata() {
		rLock.lock();
		try {
			return Lists.newArrayList(Collections2.transform(EPackage.Registry.INSTANCE.keySet(),
					new Function<String, IXArchADTPackageMetadata>() {

						@Override
						public IXArchADTPackageMetadata apply(@Nullable String nsURI) {
							return packageMetatadataCache.getUnchecked(ePackageCache.getUnchecked(nsURI));
						}
					}));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public boolean isAssignable(String sourceNsURI, String sourceTypeName, String targetNsURI, String targetTypeName) {
		rLock.lock();
		try {
			EClass eSourceClass = getEClass(ePackageCache.getUnchecked(sourceNsURI), sourceTypeName);
			EClass eTargetClass = getEClass(ePackageCache.getUnchecked(targetNsURI), targetTypeName);
			if (eSourceClass.equals(EcorePackage.Literals.EOBJECT)) {
				// This is a special case - all EWhatevers are inherently assignable
				// to EObject.  This comes up when a schema has an 'any' element.
				return true;
			}
			return eSourceClass.isSuperTypeOf(eTargetClass);
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public boolean isInstanceOf(@Nullable ObjRef baseObjRef, String sourceNsURI, String sourceTypeName) {
		rLock.lock();
		try {
			if (baseObjRef == null) {
				return false;
			}
			EObject baseEObject = get(baseObjRef);
			return getEClass(ePackageCache.getUnchecked(sourceNsURI), sourceTypeName).isSuperTypeOf(
					baseEObject.eClass());
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<URI> getOpenURIs() {
		rLock.lock();
		try {
			List<URI> uriList = new ArrayList<URI>();
			for (Resource r : resourceSet.getResources()) {
				uriList.add(r.getURI());
			}
			return uriList;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef getDocumentRootRef(URI uri) {
		rLock.lock();
		try {
			Resource r = resourceSet.getResource(uri, false);
			return r != null && r.getContents().size() > 0 ? putNullable(r.getContents().get(0)) : null;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef getDocumentRootRef(ObjRef objRef) {
		rLock.lock();
		try {
			Resource eResource = get(objRef).eResource();
			return eResource != null ? putNullable(eResource.getContents().get(0)) : null;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public void close(URI uri) {
		wLock.lock();
		try {
			Resource r = resourceSet.getResource(uri, false);
			setResourceFinishedLoading(r, false);
			r.unload();
			fireXArchADTFileEvent(new XArchADTFileEvent(EventType.XARCH_CLOSED_EVENT, uri));
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public ObjRef create(String nsURI, String typeOfThing) {
		wLock.lock();
		try {
			EPackage ePackage = ePackageCache.getUnchecked(nsURI);
			EClass eClass = getEClass(ePackage, typeOfThing);
			EObject eObject = ePackage.getEFactoryInstance().create(eClass);
			return put(eObject);
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public ObjRef createDocument(URI uri) {
		wLock.lock();
		try {
			return createDocument(uri, Xadlcore_3_0Package.eINSTANCE.getNsURI());
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public ObjRef createDocument(URI uri, String nsURI) {
		wLock.lock();
		try {
			Resource r = resourceSet.getResource(uri, false);
			if (r != null && r.getContents().size() >= 1) {
				return put(r.getContents().get(0));
			}
			r = resourceSet.createResource(uri, "xml");
			if (r instanceof ResourceImpl) {
				((ResourceImpl) r).setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
			}

			ObjRef documentRootRef = create(nsURI, "DocumentRoot");
			EObject documentRootObject = get(documentRootRef);
			r.getContents().add(documentRootObject);

			setResourceFinishedLoading(r, true);

			fireXArchADTFileEvent(new XArchADTFileEvent(EventType.XARCH_CREATED_EVENT, uri, documentRootRef));
			return documentRootRef;
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public ObjRef load(URI uri) throws SAXException, IOException {
		wLock.lock();
		try {
			for (Resource r : resourceSet.getResources()) {
				if (r.getURI().equals(uri)) {
					// Possibly already open
					if (r.isLoaded() && r.getContents().size() >= 1) {
						// Already open
						return put(r.getContents().get(0));
					}
				}
			}

			Resource newResource = null;
			try {
				newResource = resourceSet.getResource(uri, false);
				if (newResource == null) {
					newResource = resourceSet.createResource(uri);
					if (newResource instanceof ResourceImpl) {
						((ResourceImpl) newResource).setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
					}
				}
				if (!newResource.isLoaded()) {
					newResource.load(LOAD_OPTIONS_MAP);
				}
			}
			catch (WrappedException e) {
				// This catches problems with the model itself, such as unresolved dependencies
				e.printStackTrace();
				// We still want to open the model (and eventually correct these problems)
				newResource = resourceSet.getResource(uri, false);
			}
			setResourceFinishedLoading(newResource, true);

			ObjRef rootElementRef = put(newResource.getContents().get(0));
			fireXArchADTFileEvent(new XArchADTFileEvent(EventType.XARCH_OPENED_EVENT, uri, rootElementRef));
			return rootElementRef;
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public ObjRef load(URI uri, byte[] content) throws SAXException, IOException {
		wLock.lock();
		try {
			Resource r = resourceSet.createResource(uri);
			if (r instanceof ResourceImpl) {
				((ResourceImpl) r).setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
			}
			r.load(new ByteArrayInputStream(content), LOAD_OPTIONS_MAP);
			setResourceFinishedLoading(r, true);

			ObjRef rootElementRef = put(r.getContents().get(0));
			fireXArchADTFileEvent(new XArchADTFileEvent(EventType.XARCH_OPENED_EVENT, uri, rootElementRef));
			return rootElementRef;
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	public void renameXArch(String oldURI, String newURI) {
		wLock.lock();
		try {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}
		finally {
			wLock.unlock();
		}
	}

	@Override
	@Nullable
	public URI getURI(ObjRef ref) {
		rLock.lock();
		try {
			EObject obj = get(ref);
			if (obj.eResource() == null) {
				return null;
			}
			return obj.eResource().getURI();
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public void save(URI uri) throws IOException {
		rLock.lock();
		try {
			Resource r = resourceSet.getResource(uri, false);
			r.save(SAVE_OPTIONS_MAP);
			fireXArchADTFileEvent(new XArchADTFileEvent(EventType.XARCH_SAVED_EVENT, uri, getDocumentRootRef(uri)));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public byte[] serialize(URI uri) {
		rLock.lock();
		try {
			Resource r = resourceSet.getResource(uri, false);

			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				r.save(baos, SAVE_OPTIONS_MAP);
				return baos.toByteArray();
			}
			catch (IOException ioe) {
				throw new RuntimeException("This shouldn't happen", ioe);
			}
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public String getTagName(ObjRef objRef) {
		rLock.lock();
		try {
			EObject eObject = get(objRef);
			EStructuralFeature sf = elementHandlerImpl.getRoot(ExtendedMetaData.INSTANCE, eObject.eClass());
			if (sf != null) {
				return sf.getName();
			}
			else if (eObject.eContainer() != null) {
				return eObject.eContainingFeature().getName();
			}
			return null;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public String getContainingFeatureName(ObjRef ref) {
		rLock.lock();
		try {
			EObject eobject = get(ref);
			EStructuralFeature containingFeature = eobject.eContainingFeature();
			if (containingFeature == null) {
				return null;
			}
			return containingFeature.getName();
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public String getTagsOnlyPathString(ObjRef targetObjRef) {
		rLock.lock();
		try {
			List<String> tags = new ArrayList<String>();
			EObject eObject = get(targetObjRef);
			while (eObject != null) {
				EStructuralFeature sf = elementHandlerImpl.getRoot(ExtendedMetaData.INSTANCE, eObject.eClass());
				if (sf != null) {
					tags.add(sf.getName());
				}
				else if (eObject.eContainer() != null) {
					tags.add(eObject.eContainingFeature().getName());
				}
				else {
					break;
				}
				eObject = eObject.eContainer();
			}
			return Joiner.on("/").join(Lists.reverse(tags));
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	@Nullable
	public ObjRef resolveHref(ObjRef documentRef, String href) {
		rLock.lock();
		try {// TODO Do this right (e.g., open the target URL if necessary)
			if (href.startsWith("#")) {
				String id = href.substring(1);
				return getByID(documentRef, id);
			}
			throw new IllegalArgumentException("resolveHref only supports local hrefs right now.");
		}
		finally {
			rLock.unlock();
		}
	}

	protected void fireXArchADTModelEvent(XArchADTModelEvent evt) {
		for (IXArchADTModelListener l : modelListeners) {
			l.handleXArchADTModelEvent(evt);
		}
	}

	public void addXArchADTModelListener(IXArchADTModelListener l) {
		wLock.lock();
		try {
			modelListeners.add(l);
		}
		finally {
			wLock.unlock();
		}
	}

	public void removeXArchADTModelListener(IXArchADTModelListener l) {
		wLock.lock();
		try {
			modelListeners.remove(l);
		}
		finally {
			wLock.unlock();
		}
	}

	protected synchronized void fireXArchADTFileEvent(XArchADTFileEvent evt) {
		for (IXArchADTFileListener l : fileListeners) {
			l.handleXArchADTFileEvent(evt);
		}
	}

	public void addXArchADTFileListener(IXArchADTFileListener l) {
		wLock.lock();
		try {
			fileListeners.add(l);
		}
		finally {
			wLock.unlock();
		}
	}

	public void removeXArchADTFileListener(IXArchADTFileListener l) {
		wLock.lock();
		try {
			fileListeners.remove(l);
		}
		finally {
			wLock.unlock();
		}
	}

	// This stuff is necessary to suppress the event flurry when a document is loaded.
	private final Set<Resource> resourcesFinishedLoading = new CopyOnWriteArraySet<Resource>();

	private void setResourceFinishedLoading(Resource r, boolean isFinishedLoading) {
		if (isFinishedLoading) {
			resourcesFinishedLoading.add(r);
		}
		else {
			resourcesFinishedLoading.remove(r);
		}
	}

	class ContentAdapter extends EContentAdapter {

		@Override
		public void notifyChanged(@Nullable Notification notification) {
			if (notification == null) {
				throw new NullPointerException();
			}

			super.notifyChanged(notification);
			if (notification.isTouch()) {
				return;
			}

			Object notifier = notification.getNotifier();
			if (!(notifier instanceof EObject)) {
				return;
			}
			if (!resourcesFinishedLoading.contains(((EObject) notifier).eResource())) {
				return;
			}

			String featureName;
			Object feature = notification.getFeature();
			if (feature instanceof EStructuralFeature) {
				featureName = ((EStructuralFeature) feature).getName();
			}
			else {
				return;
			}

			ObjRef srcRef = put((EObject) notifier);
			List<ObjRef> srcAncestors = getAllAncestors(srcRef);
			String srcPath = getTagsOnlyPathString(srcRef);
			Object oldValue = notification.getOldValue();
			Object newValue = notification.getNewValue();

			String oldNewValueTagName = null;
			if (oldValue instanceof EObject) {
				oldNewValueTagName = getTagName(put((EObject) oldValue));
			}
			else if (newValue instanceof EObject) {
				oldNewValueTagName = getTagName(put((EObject) newValue));
			}
			if (oldNewValueTagName == null) {
				oldNewValueTagName = featureName;
			}

			String oldValuePath = null;
			if (oldValue instanceof EObject) {
				oldValue = put((EObject) oldValue);
				oldValuePath = srcPath + (srcPath.length() > 0 ? "/" : "") + oldNewValueTagName;
			}

			String newValuePath = null;
			if (newValue instanceof FeatureMap.Entry) {
				newValue = ((FeatureMap.Entry) newValue).getValue();
			}
			if (newValue instanceof EObject) {
				newValue = put((EObject) newValue);
				newValuePath = srcPath + (srcPath.length() > 0 ? "/" : "") + oldNewValueTagName;
			}

			XArchADTModelEvent.EventType evtType;
			switch (notification.getEventType()) {
			case Notification.ADD:
				evtType = XArchADTModelEvent.EventType.ADD;
				break;
			case Notification.REMOVE:
				evtType = XArchADTModelEvent.EventType.REMOVE;
				break;
			case Notification.SET:
				if (notification.getNewValue() != null) {
					evtType = XArchADTModelEvent.EventType.SET;
					break;
				}
			case Notification.UNSET:
				evtType = XArchADTModelEvent.EventType.CLEAR;
				break;
			default:
				return;
			}

			fireXArchADTModelEvent(new XArchADTModelEvent(evtType, srcRef, srcAncestors, srcPath, featureName,
					oldValue, oldValuePath, newValue, newValuePath));
		}
	}

	protected List<IXArchADTSubstitutionHint> allSubstitutionHints = null;

	@Override
	public List<IXArchADTSubstitutionHint> getAllSubstitutionHints() {
		rLock.lock();
		try {
			if (allSubstitutionHints == null) {
				List<EPackage> allEPackages = Lists.newArrayList();
				for (String packageNsURI : Lists.newArrayList(EPackage.Registry.INSTANCE.keySet())) {
					allEPackages.add(EPackage.Registry.INSTANCE.getEPackage(packageNsURI));
				}
				allSubstitutionHints = Lists.newArrayList();
				allSubstitutionHints.addAll(ExtensionHintUtils.parseExtensionHints(allEPackages));
				allSubstitutionHints.addAll(SubstitutionHintUtils.parseSubstitutionHints(allEPackages));
			}

			return allSubstitutionHints;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<IXArchADTSubstitutionHint> getSubstitutionHintsForSource(String sourceNsURI, String sourceTypeName) {
		rLock.lock();
		try {
			List<IXArchADTSubstitutionHint> matchingHints = new ArrayList<IXArchADTSubstitutionHint>();
			for (IXArchADTSubstitutionHint hint : getAllSubstitutionHints()) {
				if (sourceNsURI.equals(hint.getSourceNsURI()) && sourceTypeName.equals(hint.getSourceTypeName())) {
					matchingHints.add(hint);
				}
			}
			return matchingHints;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<IXArchADTSubstitutionHint> getSubstitutionHintsForTarget(String targetNsURI, String targetTypeName) {
		rLock.lock();
		try {
			List<IXArchADTSubstitutionHint> matchingHints = new ArrayList<IXArchADTSubstitutionHint>();
			for (IXArchADTSubstitutionHint hint : getAllSubstitutionHints()) {
				if (targetNsURI.equals(hint.getTargetNsURI()) && targetTypeName.equals(hint.getTargetTypeName())) {
					matchingHints.add(hint);
				}
			}
			return matchingHints;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public String getXPath(ObjRef toObjRef) {
		rLock.lock();
		try {
			StringBuffer sb = new StringBuffer();
			for (ObjRef objRef : Lists.reverse(getAllAncestors(toObjRef))) {
				EObject eObject = get(objRef);
				EStructuralFeature feature = eObject.eContainingFeature();
				if (feature == null) {
					continue;
				}
				sb.append("/");
				sb.append(feature.getName());
				if (feature.isMany()) {
					sb.append("[");
					sb.append(((EList<?>) eObject.eContainer().eGet(feature)).indexOf(eObject) + 1);
					sb.append("]");
				}
			}
			return sb.toString();
		}
		finally {
			rLock.unlock();
		}
	}

	XPathContextFactory<EObject> ecoreXPathContextFactory = EcoreXPathContextFactory.newInstance();

	@Override
	public List<ObjRef> resolveObjRefs(ObjRef contextObjRef, String xPath) throws XPathException {
		rLock.lock();
		try {
			Iterator<EObject> it = ecoreXPathContextFactory.newContext(get(contextObjRef)).iterate(xPath);
			List<ObjRef> result = Lists.newArrayList();
			while (it.hasNext()) {
				result.add(put(it.next()));
			}
			return result;
		}
		finally {
			rLock.unlock();
		}
	}

	@Override
	public List<Serializable> resolveSerializables(ObjRef contextObjRef, String xPath) throws XPathException {
		rLock.lock();
		try {
			Iterator<EObject> it = ecoreXPathContextFactory.newContext(get(contextObjRef)).iterate(xPath);
			List<Serializable> result = Lists.newArrayList();
			while (it.hasNext()) {
				result.add(check(it.next()));
			}
			return result;
		}
		finally {
			rLock.unlock();
		}
	}
}