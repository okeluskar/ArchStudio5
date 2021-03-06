package org.archstudio.archipelago.statechart.core.logics;

import java.awt.Dimension;
import java.util.List;

import org.archstudio.archipelago.core.ArchipelagoUtils;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.facets.IHasColor;
import org.archstudio.bna.facets.IHasMutableAlpha;
import org.archstudio.bna.facets.IHasMutableReferencePoint;
import org.archstudio.bna.facets.IHasMutableSelected;
import org.archstudio.bna.facets.IHasSecondaryColor;
import org.archstudio.bna.facets.IHasToolTip;
import org.archstudio.bna.logics.coordinating.MirrorValueLogic;
import org.archstudio.bna.things.shapes.EllipseThing;
import org.archstudio.bna.utils.Assemblies;
import org.archstudio.bna.utils.BNAPath;
import org.archstudio.bna.utils.BNAUtils;
import org.archstudio.bna.utils.UserEditableUtils;
import org.archstudio.myx.fw.Services;
import org.archstudio.xadl.bna.facets.IHasXArchID;
import org.archstudio.xadl.bna.logics.mapping.AbstractXADLToBNAPathLogic;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.ObjRef;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.google.common.base.Function;

public class MapInitialStateLogic extends AbstractXADLToBNAPathLogic<EllipseThing> {
	protected final MirrorValueLogic mirrorLogic;
	protected final Services services;
	protected final Dimension defaultSize;

	public MapInitialStateLogic(IBNAWorld world, Services services, IXArchADT xarch, ObjRef rootObjRef,
			String objRefPath, Dimension defaultSize, String description) {
		super(world, xarch, rootObjRef, objRefPath);
		mirrorLogic = logics.addThingLogic(MirrorValueLogic.class);
		this.services = services;
		this.defaultSize = defaultSize;

		syncValue("id", null, null, BNAPath.create(), IHasXArchID.XARCH_ID_KEY, true);
		syncValue("name", null, "[no name]", BNAPath.create(), IHasToolTip.TOOL_TIP_KEY, true);

		setProgressInfo(description);
	}

	@Override
	protected EllipseThing addThing(List<ObjRef> relLineageRefs, ObjRef objRef) {

		Point newPointSpot = ArchipelagoUtils.findOpenSpotForNewThing(world);

		EllipseThing thing = Assemblies.createEllipse(world, null, null);
		thing.setBoundingBox(new Rectangle(newPointSpot.x, newPointSpot.y, defaultSize.width, defaultSize.height));
		thing.setColor(new RGB(32, 32, 32));
		Assemblies.BOUNDED_TEXT_KEY.get(thing, model).setText("");

		mirrorLogic.mirrorValue(thing, IHasColor.COLOR_KEY, thing, IHasSecondaryColor.SECONDARY_COLOR_KEY,
				new Function<RGB, RGB>() {

					@Override
					@Nullable
					public RGB apply(@Nullable RGB input) {
						return BNAUtils.adjustBrightness(input, 2f);
					}
				});

		UserEditableUtils.addEditableQualities(thing, IHasMutableSelected.USER_MAY_SELECT,
				IHasMutableReferencePoint.USER_MAY_MOVE, IHasMutableAlpha.USER_MAY_CHANGE_ALPHA);

		return thing;
	}

}
