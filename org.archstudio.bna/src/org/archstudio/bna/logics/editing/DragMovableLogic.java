package org.archstudio.bna.logics.editing;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.archstudio.bna.IBNAModel;
import org.archstudio.bna.IThing;
import org.archstudio.bna.facets.IHasSelected;
import org.archstudio.bna.facets.IRelativeMovable;
import org.archstudio.bna.logics.AbstractThingLogic;
import org.archstudio.bna.logics.events.DragMoveEvent;
import org.archstudio.bna.logics.events.DragMoveEventsLogic;
import org.archstudio.bna.logics.events.IDragMoveListener;
import org.archstudio.bna.logics.tracking.ThingValueTrackingLogic;
import org.archstudio.bna.utils.Assemblies;
import org.archstudio.bna.utils.BNAUtils;
import org.archstudio.bna.utils.UserEditableUtils;
import org.archstudio.sysutils.SystemUtils;
import org.eclipse.draw2d.geometry.Point;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DragMovableLogic extends AbstractThingLogic implements IDragMoveListener {

	protected ThingValueTrackingLogic tvtl;
	protected final Map<IRelativeMovable, Point> movingThings = Maps.newHashMap();

	public DragMovableLogic() {
	}

	@Override
	protected void init() {
		super.init();
		tvtl = getBNAWorld().getThingLogicManager().addThingLogic(ThingValueTrackingLogic.class);
		// this logic relies on events from the DragMoveEventsLogic
		getBNAWorld().getThingLogicManager().addThingLogic(DragMoveEventsLogic.class);
	}

	@Override
	protected void destroy() {
		tvtl = null;
		movingThings.clear();
		super.destroy();
	}

	@Override
	public void dragStarted(DragMoveEvent evt) {
		IBNAModel model = getBNAModel();
		if (model != null) {
			model.beginBulkChange();
			try {
				movingThings.clear();
				IRelativeMovable movingThing = SystemUtils.firstOrNull(UserEditableUtils.getEditableForAllQualities(
						Iterables.filter(evt.getInitialThings(), IRelativeMovable.class),
						IRelativeMovable.USER_MAY_MOVE));
				List<IRelativeMovable> selectedThings = Lists.newArrayList(Iterables.filter(
						BNAUtils.getThings(model, tvtl.getThingIDs(IHasSelected.SELECTED_KEY, Boolean.TRUE)),
						IRelativeMovable.class));

				if (selectedThings.contains(movingThing)) {
					for (IRelativeMovable rmt : selectedThings) {
						movingThings.put(rmt, rmt.getReferencePoint());
					}
				}
				else {
					movingThings.put(movingThing, movingThing.getReferencePoint());
				}
				for (IThing thing : movingThings.keySet()) {
					model.bringToFront(Assemblies.getRelatedParts(model, thing));
				}
			}
			finally {
				model.endBulkChange();
			}
		}
	}

	@Override
	public void dragMoved(DragMoveEvent evt) {
		IBNAModel model = getBNAModel();
		if (model != null) {
			model.beginBulkChange();
			try {
				Point worldDelta = evt.getAdjustedLocation().getWorldPoint(new Point());
				worldDelta.translate(evt.getInitialLocation().getWorldPoint(new Point()).negate());
				for (Entry<IRelativeMovable, Point> e : movingThings.entrySet()) {
					e.getKey().setReferencePoint(e.getValue().getTranslated(worldDelta));
				}
			}
			finally {
				model.endBulkChange();
			}
		}
	}

	@Override
	public void dragFinished(DragMoveEvent evt) {
		movingThings.clear();
	}
}
