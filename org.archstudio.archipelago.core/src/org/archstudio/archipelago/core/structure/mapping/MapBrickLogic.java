package org.archstudio.archipelago.core.structure.mapping;

import java.util.List;

import org.archstudio.archipelago.core.ArchipelagoServices;
import org.archstudio.archipelago.core.ArchipelagoUtils;
import org.archstudio.archipelago.core.structure.StructureEditorSupport;
import org.archstudio.bna.BNAModelEvent;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.IThing;
import org.archstudio.bna.IThing.IThingKey;
import org.archstudio.bna.facets.IHasColor;
import org.archstudio.bna.facets.IHasMutableSelected;
import org.archstudio.bna.facets.IHasMutableSize;
import org.archstudio.bna.facets.IHasMutableText;
import org.archstudio.bna.facets.IHasMutableWorld;
import org.archstudio.bna.facets.IHasSecondaryColor;
import org.archstudio.bna.facets.IHasText;
import org.archstudio.bna.facets.IHasToolTip;
import org.archstudio.bna.facets.IHasWorld;
import org.archstudio.bna.facets.IRelativeMovable;
import org.archstudio.bna.keys.ThingKey;
import org.archstudio.bna.things.glass.RectangleGlassThing;
import org.archstudio.bna.utils.Assemblies;
import org.archstudio.bna.utils.BNAPath;
import org.archstudio.bna.utils.BNAUtils;
import org.archstudio.bna.utils.UserEditableUtils;
import org.archstudio.sysutils.SystemUtils;
import org.archstudio.xadl.XadlUtils;
import org.archstudio.xadlbna.logics.mapping.AbstractXADLToBNAPathLogic;
import org.archstudio.xadlbna.things.IHasXArchID;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.XArchADTModelEvent;
import org.archstudio.xarchadt.XArchADTPath;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

/**
 * Maps xADL Bricks (i.e., Components or Connectors) to BNA Rectangle Assemblies.
 */
public class MapBrickLogic extends AbstractXADLToBNAPathLogic<RectangleGlassThing> {
	protected final ArchipelagoServices AS;
	protected final Dimension defaultSize;
	protected final RGB defaultColor;

	// TODO: Are these really necessary?
	enum RootThingType {
		COMPONENT,
		CONNECTOR
	}
	
	protected static final IThingKey<RootThingType> BRICK_ROOT_THING_TYPE_KEY = ThingKey.create("rootThingType", false);

	public MapBrickLogic(ArchipelagoServices AS, IXArchADT xarch, ObjRef rootObjRef, String objRefPath, Dimension defaultSize, RGB defaultColor) {
		super(xarch, rootObjRef, objRefPath);
		this.AS = AS;
		this.defaultSize = defaultSize;
		this.defaultColor = defaultColor;
	}

	@Override
	public void init() {
		super.init();
		syncAttribute("id", null, null, BNAPath.create(), IHasXArchID.XARCH_ID_KEY, true);
		syncAttribute("name", null, "[no name]", BNAPath.create(Assemblies.TEXT_KEY), IHasText.TEXT_KEY, true);
		syncAttribute("name", null, "[no name]", BNAPath.create(), IHasToolTip.TOOL_TIP_KEY, true);
		
		IBNAUpdater fakeBNAUpdater = new IBNAUpdater() {
			@Override
			public void updateBNA(ObjRef objRef, XArchADTPath xadlPath, XArchADTModelEvent evt, IThing rootThing) {
				/*
				System.err.println("Fake BNA Updater:");
				System.err.println("  ObjRef = " + objRef);
				System.err.println("  xadlPath = " + ((xadlPath == null) ? "null" : xadlPath.toDumpString()));
				System.err.println("  evt = " + evt);
				System.err.println("  rootThing = " + rootThing);
				*/
				
				IThing rectangleGlassThing = rootThing;
				IThing worldThing = BNAPath.resolve(getBNAModel(), rootThing, BNAPath.create(Assemblies.WORLD_KEY));

				if ((rectangleGlassThing != null) && (worldThing != null)) {
					ObjRef innerStructureRef = null;
					String innerStructureXArchID = null;

					ObjRef subStructureRef = (ObjRef)xarch.get(objRef, "subStructure");
					if (subStructureRef != null) {
						innerStructureRef = (ObjRef)xarch.get(subStructureRef, "innerStructureLink");
						if (innerStructureRef != null) {
							innerStructureXArchID = XadlUtils.getID(xarch, innerStructureRef);
						}
					}
					// If innerStructureXArchID is null, then we need to remove the world from the worldThing.
					// Otherwise, we need to add it and hook it up.
					if (innerStructureXArchID != null) {
						ObjRef documentRootRef = AS.xarch.getDocumentRootRef(objRef);
						IBNAWorld internalWorld = StructureEditorSupport.setupWorld(AS, documentRootRef, innerStructureRef);
						if (internalWorld != null) {
							((IHasMutableWorld)worldThing).setWorld(internalWorld);
						}
					}
					else {
						((IHasMutableWorld)worldThing).remove(IHasWorld.WORLD_KEY);
					}
				}
			}
		};
		
		IXADLUpdater fakeXadlUpdater = new IXADLUpdater() {
			@Override
			public <ET extends IThing, EK extends IThingKey<EV>, EV> void updateXADL(IThing rootThing, BNAPath relativeBNAPath, BNAModelEvent<ET, EK, EV> evt, ObjRef objRef) {
			}
		};
		
		addBNAUpdater(fakeBNAUpdater);
	}

	@Override
	protected RectangleGlassThing addThing(List<ObjRef> relativeAncestorRefs, ObjRef objRef) {
		
		Point newPointSpot = ArchipelagoUtils.findOpenSpotForNewThing(getBNAWorld().getBNAModel());

		RectangleGlassThing thing = Assemblies.createRectangleWithWorld(getBNAWorld(), null, null);
		thing.setBoundingBox(new Rectangle(newPointSpot, defaultSize));
		Assemblies.BACKGROUND_KEY.get(thing, getBNAModel()).set(IHasColor.COLOR_KEY, defaultColor);
		Assemblies.BACKGROUND_KEY.get(thing, getBNAModel()).set(IHasSecondaryColor.SECONDARY_COLOR_KEY,
				BNAUtils.adjustBrightness(defaultColor, 1.5f));

		UserEditableUtils.addEditableQualities(thing, IHasMutableSelected.USER_MAY_SELECT,
				IHasMutableSize.USER_MAY_RESIZE, IRelativeMovable.USER_MAY_MOVE);
		UserEditableUtils.addEditableQualities(Assemblies.TEXT_KEY.get(thing, getBNAModel()),
				IHasMutableText.USER_MAY_EDIT_TEXT);

		if (XadlUtils.isComponent(xarch, objRef)) {
			thing.set(BRICK_ROOT_THING_TYPE_KEY, RootThingType.COMPONENT);
		}
		else if (XadlUtils.isConnector(xarch, objRef)) {
			thing.set(BRICK_ROOT_THING_TYPE_KEY, RootThingType.CONNECTOR);
		}

		return thing;
	}
	
	protected static RootThingType getRootThingType(IThing t) {
		if (t instanceof RectangleGlassThing) {
			return t.get(BRICK_ROOT_THING_TYPE_KEY);
		}
		return null;
	}
	
	public static boolean isComponentRootThing(IThing t) {
		return SystemUtils.nullEquals(getRootThingType(t), RootThingType.COMPONENT);
	}

	public static boolean isConnectorRootThing(IThing t) {
		return SystemUtils.nullEquals(getRootThingType(t), RootThingType.CONNECTOR);
	}
	
	public static boolean isBrickRootThing(IThing t) {
		return isComponentRootThing(t) || isConnectorRootThing(t);
	}

}
