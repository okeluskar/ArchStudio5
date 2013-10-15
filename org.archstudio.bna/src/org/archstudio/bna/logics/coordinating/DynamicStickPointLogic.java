package org.archstudio.bna.logics.coordinating;

import org.archstudio.bna.BNAModelEvent;
import org.archstudio.bna.BNAModelEvent.EventType;
import org.archstudio.bna.IBNAModel;
import org.archstudio.bna.IBNAModelListener;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.IThing;
import org.archstudio.bna.IThing.IThingKey;
import org.archstudio.bna.ThingEvent;
import org.archstudio.bna.constants.StickyMode;
import org.archstudio.bna.facets.IHasShapeKeys;
import org.archstudio.bna.facets.IIsSticky;
import org.archstudio.bna.keys.IThingMetakey;
import org.archstudio.bna.keys.IThingRefKey;
import org.archstudio.bna.keys.IThingRefMetakey;
import org.archstudio.bna.keys.ThingMetakey;
import org.archstudio.bna.keys.ThingRefMetakey;
import org.archstudio.bna.logics.AbstractThingLogic;
import org.archstudio.bna.logics.tracking.ThingReferenceTrackingLogic;
import org.eclipse.swt.graphics.Point;

public class DynamicStickPointLogic extends AbstractThingLogic implements IBNAModelListener {

	private static final String STICKY_MODE_KEY_NAME = ".stickyMode";
	private static final String STICKY_THING_ID_KEY_NAME = ".&stickyThingID";

	protected final ThingReferenceTrackingLogic referenceLogic;
	protected final StickPointLogic stickLogic;

	public DynamicStickPointLogic(IBNAWorld world) {
		super(world);
		referenceLogic = logics.addThingLogic(ThingReferenceTrackingLogic.class);
		stickLogic = logics.addThingLogic(StickPointLogic.class);
	}

	synchronized public IThingKey<StickyMode> getStickyModeKey(IThingKey<Point> pointKey) {
		return ThingMetakey.create(STICKY_MODE_KEY_NAME, pointKey);
	}

	synchronized public IThingRefKey<IIsSticky> getStickyThingKey(IThingKey<Point> pointKey) {
		return ThingRefMetakey.create(STICKY_THING_ID_KEY_NAME, pointKey);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	synchronized public void bnaModelChanged(BNAModelEvent evt) {
		ThingEvent thingEvent = evt.getThingEvent();
		if (thingEvent != null) {
			IThing thing = thingEvent.getTargetThing();
			IThingKey<?> key = thingEvent.getPropertyName();

			if (thingEvent != null) {
				if (key instanceof IThingMetakey) {
					IThingMetakey keyKey = (IThingMetakey) key;

					// a thing's sticky mode has changed, update the stuck point
					// || a thing's sticky thing ID has changed, update the stuck point
					if (STICKY_MODE_KEY_NAME.equals(keyKey.getName())
							|| STICKY_THING_ID_KEY_NAME.equals(keyKey.getName())) {
						if (thing instanceof IHasShapeKeys) {
							updateStuckPoint(evt.getSource(), (IHasShapeKeys) thing, keyKey.getKey());
						}
					}
				}
			}

			if (evt.getEventType() == EventType.THING_ADDED) {
				if (thing instanceof IIsSticky) {
					// a sticky thing's been added, update any points stuck to it
					IIsSticky stickyThing = (IIsSticky) thing;
					for (ThingReferenceTrackingLogic.Reference reference : referenceLogic.getReferences(stickyThing
							.getID())) {
						IThingRefKey refKey = reference.getFromKey();
						if (refKey instanceof IThingRefMetakey) {
							IThingRefMetakey refKeyKey = (IThingRefMetakey) refKey;
							if (STICKY_THING_ID_KEY_NAME.equals(refKeyKey.getName())) {
								// a dynamic sticky point thing is referring to this sticky thing
								IThing pointThing = model.getThing(reference.getFromThingID());
								if (pointThing instanceof IHasShapeKeys) {
									updateStuckPoint(evt.getSource(), (IHasShapeKeys) pointThing, refKeyKey.getKey());
								}
							}
						}
					}
				}
			}
		}
	}

	private void updateStuckPoint(IBNAModel model, IHasShapeKeys pointThing, IThingKey<Point> pointKey) {
		StickyMode stickyMode = pointThing.get(getStickyModeKey(pointKey));
		IIsSticky stickyThing = getStickyThingKey(pointKey).get(pointThing, model);
		if (stickyMode != null && stickyThing != null) {
			stickLogic.stick(pointThing, pointKey, stickyMode, stickyThing);
		}
		else {
			stickLogic.unstick(pointThing, pointKey);
		}
	}
}
