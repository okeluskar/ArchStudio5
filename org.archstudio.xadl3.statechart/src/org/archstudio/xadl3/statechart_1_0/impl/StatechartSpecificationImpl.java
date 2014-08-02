/**
 */
package org.archstudio.xadl3.statechart_1_0.impl;

import java.util.Collection;

import org.archstudio.xadl3.statechart_1_0.Statechart;
import org.archstudio.xadl3.statechart_1_0.StatechartSpecification;
import org.archstudio.xadl3.statechart_1_0.Statechart_1_0Package;
import org.archstudio.xadl3.xadlcore_3_0.Extension;
import org.archstudio.xadl3.xadlcore_3_0.impl.ExtensionImpl;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Statechart Specification</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.archstudio.xadl3.statechart_1_0.impl.StatechartSpecificationImpl#getStatechart <em>Statechart</em>}</li>
 * <li>{@link org.archstudio.xadl3.statechart_1_0.impl.StatechartSpecificationImpl#getExt <em>Ext</em>}</li>
 * <li>{@link org.archstudio.xadl3.statechart_1_0.impl.StatechartSpecificationImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class StatechartSpecificationImpl extends ExtensionImpl implements StatechartSpecification {
	/**
	 * The cached value of the '{@link #getStatechart() <em>Statechart</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getStatechart()
	 * @generated
	 * @ordered
	 */
	protected Statechart statechart;

	/**
	 * The cached value of the '{@link #getExt() <em>Ext</em>}' containment reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getExt()
	 * @generated
	 * @ordered
	 */
	protected EList<Extension> ext;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected StatechartSpecificationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Statechart_1_0Package.Literals.STATECHART_SPECIFICATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Statechart getStatechart() {
		return statechart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setStatechart(Statechart newStatechart) {
		Statechart oldStatechart = statechart;
		statechart = newStatechart;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET,
					Statechart_1_0Package.STATECHART_SPECIFICATION__STATECHART, oldStatechart, statechart));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Extension> getExt() {
		if (ext == null) {
			ext = new EObjectContainmentEList<Extension>(Extension.class, this,
					Statechart_1_0Package.STATECHART_SPECIFICATION__EXT);
		}
		return ext;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, Statechart_1_0Package.STATECHART_SPECIFICATION__ID,
					oldId, id));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case Statechart_1_0Package.STATECHART_SPECIFICATION__EXT:
			return ((InternalEList<?>) getExt()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case Statechart_1_0Package.STATECHART_SPECIFICATION__STATECHART:
			return getStatechart();
		case Statechart_1_0Package.STATECHART_SPECIFICATION__EXT:
			return getExt();
		case Statechart_1_0Package.STATECHART_SPECIFICATION__ID:
			return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case Statechart_1_0Package.STATECHART_SPECIFICATION__STATECHART:
			setStatechart((Statechart) newValue);
			return;
		case Statechart_1_0Package.STATECHART_SPECIFICATION__EXT:
			getExt().clear();
			getExt().addAll((Collection<? extends Extension>) newValue);
			return;
		case Statechart_1_0Package.STATECHART_SPECIFICATION__ID:
			setId((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case Statechart_1_0Package.STATECHART_SPECIFICATION__STATECHART:
			setStatechart((Statechart) null);
			return;
		case Statechart_1_0Package.STATECHART_SPECIFICATION__EXT:
			getExt().clear();
			return;
		case Statechart_1_0Package.STATECHART_SPECIFICATION__ID:
			setId(ID_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case Statechart_1_0Package.STATECHART_SPECIFICATION__STATECHART:
			return statechart != null;
		case Statechart_1_0Package.STATECHART_SPECIFICATION__EXT:
			return ext != null && !ext.isEmpty();
		case Statechart_1_0Package.STATECHART_SPECIFICATION__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //StatechartSpecificationImpl
