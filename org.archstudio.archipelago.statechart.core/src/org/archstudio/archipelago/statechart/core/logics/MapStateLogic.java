package org.archstudio.archipelago.statechart.core.logics;

import static org.archstudio.sysutils.SystemUtils.castOrNull;

import java.awt.Dimension;
import java.util.List;

import org.archstudio.archipelago.core.ArchipelagoConstants;
import org.archstudio.archipelago.core.ArchipelagoUtils;
import org.archstudio.archipelago.statechart.core.Activator;
import org.archstudio.archipelago.statechart.core.StatechartConstants;
import org.archstudio.archipelago.statechart.core.StatechartTreePlugin;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.facets.IHasColor;
import org.archstudio.bna.facets.IHasFontData;
import org.archstudio.bna.facets.IHasLineWidth;
import org.archstudio.bna.facets.IHasMutableAlpha;
import org.archstudio.bna.facets.IHasMutableColor;
import org.archstudio.bna.facets.IHasMutableReferencePoint;
import org.archstudio.bna.facets.IHasMutableSelected;
import org.archstudio.bna.facets.IHasMutableSize;
import org.archstudio.bna.facets.IHasMutableText;
import org.archstudio.bna.facets.IHasMutableWorld;
import org.archstudio.bna.facets.IHasSecondaryColor;
import org.archstudio.bna.facets.IHasText;
import org.archstudio.bna.facets.IHasToolTip;
import org.archstudio.bna.facets.IHasWorld;
import org.archstudio.bna.logics.coordinating.MirrorValueLogic;
import org.archstudio.bna.logics.events.WorldThingInternalEventsLogic;
import org.archstudio.bna.logics.information.HighlightLogic;
import org.archstudio.bna.things.labels.BoundedLabelThing;
import org.archstudio.bna.things.shapes.RectangleThing;
import org.archstudio.bna.utils.Assemblies;
import org.archstudio.bna.utils.BNAPath;
import org.archstudio.bna.utils.BNAUtils;
import org.archstudio.bna.utils.UserEditableUtils;
import org.archstudio.myx.fw.Services;
import org.archstudio.swtutils.constants.FontStyle;
import org.archstudio.sysutils.Finally;
import org.archstudio.xadl.bna.facets.IHasObjRef;
import org.archstudio.xadl.bna.facets.IHasXArchID;
import org.archstudio.xadl.bna.logics.mapping.AbstractXADLToBNAPathLogic;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.XArchADTModelEvent;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.google.common.base.Function;

/**
 * Maps States to BNA Rectangle Assemblies.
 */
public class MapStateLogic extends AbstractXADLToBNAPathLogic<RectangleThing> implements IPropertyChangeListener {

	protected final MirrorValueLogic mirrorLogic;
	protected final Services services;
	protected final Dimension defaultSize;
	protected final int defaultCount;

	protected RGB defaultColor;
	protected FontData defaultFont;
	protected int defaultLineWidth;

	public MapStateLogic(IBNAWorld world, Services services, IXArchADT xarch, ObjRef rootObjRef, String objRefPath,
			Dimension defaultSize, int defaultCount, String description) {
		super(world, xarch, rootObjRef, objRefPath);
		mirrorLogic = logics.addThingLogic(MirrorValueLogic.class);
		logics.addThingLogic(WorldThingInternalEventsLogic.class);
		this.services = services;
		this.defaultSize = defaultSize;
		this.defaultCount = defaultCount;

		syncValue("id", null, null, BNAPath.create(), IHasXArchID.XARCH_ID_KEY, true);
		syncValue("name", null, "[no name]", BNAPath.create(Assemblies.BOUNDED_TEXT_KEY), IHasText.TEXT_KEY, true);
		syncValue("name", null, "[no name]", BNAPath.create(), IHasToolTip.TOOL_TIP_KEY, true);
		addBNAUpdater("subStatechart", new IBNAUpdater() {

			@Override
			public void updateBNA(ObjRef objRef, String xadlPath, XArchADTModelEvent evt, RectangleThing rootThing) {
				updateSubstructure(objRef, xadlPath, evt, rootThing);
			}
		});

		loadPreferences();

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		org.archstudio.archipelago.core.Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		setProgressInfo(description);
	}

	@Override
	public void dispose() {
		BNAUtils.checkLock();

		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		org.archstudio.archipelago.core.Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}

	protected void loadPreferences() {
		defaultColor = PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
				StatechartConstants.PREF_STATE_COLOR);
		defaultFont = PreferenceConverter.getFontData(Activator.getDefault().getPreferenceStore(),
				StatechartConstants.PREF_STATE_FONT);
		defaultLineWidth = org.archstudio.archipelago.core.Activator.getDefault().getPreferenceStore()
				.getInt(ArchipelagoConstants.PREF_LINE_WIDTH);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		loadPreferences();

		try (Finally lock = BNAUtils.lock(); Finally bulkChange = model.beginBulkChange();) {
			for (RectangleThing thing : getAddedThings()) {
				if (event.getProperty().equals(StatechartConstants.PREF_STATE_COLOR)) {
					RGB oldColor = toRGB(event.getOldValue());
					if (thing.getColor().equals(oldColor)) {
						thing.setColor(defaultColor);
					}
				}

				BoundedLabelThing label = Assemblies.BOUNDED_TEXT_KEY.get(thing, model);
				label.set(IHasFontData.FONT_NAME_KEY, defaultFont.getName());
				label.set(IHasFontData.FONT_SIZE_KEY, defaultFont.getHeight());
				label.set(IHasFontData.FONT_STYLE_KEY, FontStyle.fromSWT(defaultFont.getStyle()));
				thing.set(IHasLineWidth.LINE_WIDTH_KEY, defaultLineWidth);
			}
		}
	}

	private RGB toRGB(Object value) {
		if (value instanceof RGB) {
			return (RGB) value;
		}
		String[] rgbs = ((String) value).split(",");
		return new RGB(Integer.valueOf(rgbs[0]), Integer.valueOf(rgbs[1]), Integer.valueOf(rgbs[2]));
	}

	@Override
	protected RectangleThing addThing(List<ObjRef> relLineageRefs, ObjRef objRef) {

		Point newPointSpot = ArchipelagoUtils.findOpenSpotForNewThing(world);

		RectangleThing thing = Assemblies.addWorld(world, null, Assemblies.createRectangle(world, null, null));
		thing.setBoundingBox(new Rectangle(newPointSpot.x, newPointSpot.y, defaultSize.width, defaultSize.height));
		thing.setRoundCorners(new Dimension(30, 30));
		thing.setColor(defaultColor);
		thing.setCount(defaultCount);
		BoundedLabelThing label = Assemblies.BOUNDED_TEXT_KEY.get(thing, model);
		label.set(IHasFontData.FONT_NAME_KEY, defaultFont.getName());
		label.set(IHasFontData.FONT_SIZE_KEY, defaultFont.getHeight());
		label.set(IHasFontData.FONT_STYLE_KEY, FontStyle.fromSWT(defaultFont.getStyle()));
		thing.set(IHasLineWidth.LINE_WIDTH_KEY, defaultLineWidth);

		mirrorLogic.mirrorValue(thing, IHasColor.COLOR_KEY, thing, IHasSecondaryColor.SECONDARY_COLOR_KEY,
				new Function<RGB, RGB>() {

					@Override
					@Nullable
					public RGB apply(@Nullable RGB input) {
						return BNAUtils.adjustBrightness(input, 1.2f);
					}
				});

		UserEditableUtils.addEditableQualities(thing, IHasMutableSelected.USER_MAY_SELECT,
				IHasMutableSize.USER_MAY_RESIZE, IHasMutableReferencePoint.USER_MAY_MOVE,
				HighlightLogic.USER_MAY_HIGHLIGHT, IHasMutableColor.USER_MAY_COPY_COLOR,
				IHasMutableColor.USER_MAY_EDIT_COLOR, IHasMutableAlpha.USER_MAY_CHANGE_ALPHA);
		UserEditableUtils.addEditableQualities(Assemblies.BOUNDED_TEXT_KEY.get(thing, model),
				IHasMutableText.USER_MAY_EDIT_TEXT);

		return thing;
	}

	protected void updateSubstructure(ObjRef objRef, String xadlPath, XArchADTModelEvent evt, RectangleThing rootThing) {
		IHasMutableWorld worldThing = castOrNull(
				BNAPath.resolve(model, rootThing, BNAPath.create(Assemblies.WORLD_KEY)), IHasMutableWorld.class);
		if (worldThing != null) {
			ObjRef innerStructureRef = null;

			ObjRef subStructureRef = (ObjRef) xarch.get(objRef, "subStatechart");
			if (subStructureRef != null) {
				innerStructureRef = (ObjRef) xarch.get(subStructureRef, "innerStatechart");
			}
			// If innerStructureRef is null, then we need to remove the world from the worldThing.
			// Otherwise, we need to add it and hook it up.
			if (innerStructureRef == null) {
				worldThing.remove(IHasWorld.WORLD_KEY);
				worldThing.remove(IHasObjRef.OBJREF_KEY);
			}
			else {
				ObjRef documentRootRef = xarch.getDocumentRootRef(objRef);
				IBNAWorld internalWorld = StatechartTreePlugin.setupEditor(services, xarch, documentRootRef,
						innerStructureRef);
				if (internalWorld != null) {
					worldThing.setWorld(internalWorld);
					worldThing.set(IHasObjRef.OBJREF_KEY, subStructureRef);
				}
			}
		}
	}
}
