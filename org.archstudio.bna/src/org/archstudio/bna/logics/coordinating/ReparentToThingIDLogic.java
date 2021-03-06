package org.archstudio.bna.logics.coordinating;

import org.archstudio.bna.IBNAModelListener;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.IThing;
import org.archstudio.bna.keys.IThingKey;
import org.archstudio.bna.keys.IThingRefKey;
import org.archstudio.bna.keys.ThingRefKey;
import org.archstudio.bna.logics.AbstractKeyCoordinatingThingLogic;
import org.archstudio.bna.utils.BNAUtils;

public class ReparentToThingIDLogic extends AbstractKeyCoordinatingThingLogic implements IBNAModelListener {

	private static final IThingRefKey<IThing> REPARENT_TO_THING_KEY = ThingRefKey.create(ReparentToThingIDLogic.class);

	public ReparentToThingIDLogic(IBNAWorld world) {
		super(world, false);
		track(REPARENT_TO_THING_KEY);
	}

	public IThingRefKey<IThing> getReparentToThingKey() {
		BNAUtils.checkLock();

		return REPARENT_TO_THING_KEY;
	}

	@Override
	protected void update(IThing thing, IThingKey<?> key) {
		IThing parentThing = REPARENT_TO_THING_KEY.get(thing, model);
		if (parentThing != null) {
			model.reparent(parentThing, thing);
		}
	}
}
