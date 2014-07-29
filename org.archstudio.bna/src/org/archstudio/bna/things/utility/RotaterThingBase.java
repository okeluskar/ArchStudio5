package org.archstudio.bna.things.utility;

import org.archstudio.bna.IBNAView;
import org.archstudio.bna.ICoordinateMapper;
import org.archstudio.bna.IThingPeer;
import org.archstudio.bna.keys.IThingKey;
import org.archstudio.bna.keys.IThingRefKey;
import org.archstudio.bna.keys.ThingKey;
import org.archstudio.bna.keys.ThingRefKey;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/*
 * DO NOT EDIT THIS FILE, it is automatically generated. ANY MODIFICATIONS WILL BE OVERWRITTEN.
 *
 * To modify, update the thingdefinition extension at
 * org.archstudio.bna/Package[name=org.archstudio.bna.things.utility]/Thing[name=Rotater].
 */
 
@SuppressWarnings("all")
@NonNullByDefault
public abstract class RotaterThingBase extends org.archstudio.bna.things.AbstractThing
	implements org.archstudio.bna.IThing,
			org.archstudio.bna.facets.IHasMutableAnchorPoint,
			org.archstudio.bna.facets.IHasMutableAngle,
			org.archstudio.bna.facets.IHasMutableRadius {

	public static final IThingKey<java.lang.Integer> ADJUSTMENT_INCREMENT_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("adjustmentIncrement", RotaterThingBase.class));

	public RotaterThingBase(@Nullable Object id) {
		super(id);
	}

	@Override
	public IThingPeer<? extends RotaterThing> createPeer(IBNAView view, ICoordinateMapper cm) {
		return new RotaterThingPeer<>((RotaterThing)this, view, cm);
	}

	@Override
	protected void initProperties() {
		initProperty(org.archstudio.bna.things.utility.RotaterThing.ADJUSTMENT_INCREMENT_KEY, 15);
		initProperty(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY, new java.awt.geom.Point2D.Double(0, 0));
		addShapeModifyingKey(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY);
		initProperty(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY, 0);
		addShapeModifyingKey(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY);
		initProperty(org.archstudio.bna.facets.IHasRadius.RADIUS_KEY, 50);
		super.initProperties();
	}

	public int getAdjustmentIncrement() {
		return get(org.archstudio.bna.things.utility.RotaterThing.ADJUSTMENT_INCREMENT_KEY);
	}

	/*package*/ int getRawAdjustmentIncrement() {
		return getRaw(org.archstudio.bna.things.utility.RotaterThing.ADJUSTMENT_INCREMENT_KEY);
	}

	public void setAdjustmentIncrement(int adjustmentIncrement) {
		set(org.archstudio.bna.things.utility.RotaterThing.ADJUSTMENT_INCREMENT_KEY, adjustmentIncrement);
	}

	/*package*/ int setRawAdjustmentIncrement(int adjustmentIncrement) {
		return setRaw(org.archstudio.bna.things.utility.RotaterThing.ADJUSTMENT_INCREMENT_KEY, adjustmentIncrement);
	}

	public java.awt.geom.Point2D getAnchorPoint() {
		return get(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY);
	}

	/*package*/ java.awt.geom.Point2D getRawAnchorPoint() {
		return getRaw(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY);
	}

	public void setAnchorPoint(java.awt.geom.Point2D anchorPoint) {
		set(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY, anchorPoint);
	}

	/*package*/ java.awt.geom.Point2D setRawAnchorPoint(java.awt.geom.Point2D anchorPoint) {
		return setRaw(org.archstudio.bna.facets.IHasAnchorPoint.ANCHOR_POINT_KEY, anchorPoint);
	}

	public int getAngle() {
		return get(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY);
	}

	/*package*/ int getRawAngle() {
		return getRaw(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY);
	}

	public void setAngle(int angle) {
		set(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY, angle);
	}

	/*package*/ int setRawAngle(int angle) {
		return setRaw(org.archstudio.bna.facets.IHasAngle.ANGLE_KEY, angle);
	}

	public int getRadius() {
		return get(org.archstudio.bna.facets.IHasRadius.RADIUS_KEY);
	}

	/*package*/ int getRawRadius() {
		return getRaw(org.archstudio.bna.facets.IHasRadius.RADIUS_KEY);
	}

	public void setRadius(int radius) {
		set(org.archstudio.bna.facets.IHasRadius.RADIUS_KEY, radius);
	}

	/*package*/ int setRawRadius(int radius) {
		return setRaw(org.archstudio.bna.facets.IHasRadius.RADIUS_KEY, radius);
	}

}
