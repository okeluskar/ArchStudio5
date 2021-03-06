package org.archstudio.bna.things.graphs;

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
 * org.archstudio.bna/Package[name=org.archstudio.bna.things.graphs]/Thing[name=NumericSurfaceGraph].
 */
 
@SuppressWarnings("all")
@NonNullByDefault
public abstract class NumericSurfaceGraphThingBase extends org.archstudio.bna.things.AbstractThing
	implements org.archstudio.bna.IThing,
			org.archstudio.bna.facets.IHasMutableBoundingBox,
			org.archstudio.bna.facets.IHasMutableReferencePoint,
			org.archstudio.bna.facets.IHasMutableSelected {

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data> DATA_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("data", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Boolean> FLIP_DATA_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("flipData", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Double> GRID_ALPHA_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("gridAlpha", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Double> MAJOR_CONTOUR_ALPHA_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("majorContourAlpha", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Double> MINOR_CONTOUR_ALPHA_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("minorContourAlpha", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> X_MAJOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("xMajorAxis", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> X_MINOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("xMinorAxis", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Integer> X_ROTATION_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("xRotation", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> Y_MAJOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("yMajorAxis", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> Y_MINOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("yMinorAxis", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<java.lang.Integer> Y_ROTATION_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("yRotation", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> Z_MAJOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("zMajorAxis", NumericSurfaceGraphThingBase.class));

	public static final IThingKey<org.archstudio.bna.things.graphs.NumericAxis> Z_MINOR_AXIS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("zMinorAxis", NumericSurfaceGraphThingBase.class));

	public NumericSurfaceGraphThingBase(@Nullable Object id) {
		super(id);
	}

	@Override
	public IThingPeer<? extends NumericSurfaceGraphThing> createPeer(IBNAView view, ICoordinateMapper cm) {
		return new NumericSurfaceGraphThingPeer<>((NumericSurfaceGraphThing)this, view, cm);
	}

	@Override
	protected void initProperties() {
		initProperty(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY, new org.eclipse.swt.graphics.Rectangle(0, 0, 30, 20));
		addShapeModifyingKey(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.DATA_KEY, new org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.FLIP_DATA_KEY, false);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.GRID_ALPHA_KEY, 0.25d);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MAJOR_CONTOUR_ALPHA_KEY, 0d);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MINOR_CONTOUR_ALPHA_KEY, 0d);
		initProperty(org.archstudio.bna.facets.IHasRotatingOffset.ROTATING_OFFSET_KEY, 0);
		initProperty(org.archstudio.bna.facets.IHasSelected.SELECTED_KEY, false);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MAJOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MINOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_ROTATION_KEY, 30);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MAJOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MINOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_ROTATION_KEY, 20);
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MAJOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		initProperty(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MINOR_AXIS_KEY, new org.archstudio.bna.things.graphs.NumericAxis());
		super.initProperties();
	}

	public org.eclipse.swt.graphics.Rectangle getBoundingBox() {
		return get(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY);
	}

	/*package*/ org.eclipse.swt.graphics.Rectangle getRawBoundingBox() {
		return getRaw(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY);
	}

	public void setBoundingBox(org.eclipse.swt.graphics.Rectangle boundingBox) {
		set(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY, boundingBox);
	}

	/*package*/ org.eclipse.swt.graphics.Rectangle setRawBoundingBox(org.eclipse.swt.graphics.Rectangle boundingBox) {
		return setRaw(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY, boundingBox);
	}

	public org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data getData() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.DATA_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data getRawData() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.DATA_KEY);
	}

	public void setData(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data data) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.DATA_KEY, data);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data setRawData(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Data data) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.DATA_KEY, data);
	}

	public boolean isFlipData() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.FLIP_DATA_KEY);
	}

	/*package*/ boolean isRawFlipData() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.FLIP_DATA_KEY);
	}

	public void setFlipData(boolean flipData) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.FLIP_DATA_KEY, flipData);
	}

	/*package*/ boolean isRawFlipData(boolean flipData) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.FLIP_DATA_KEY, flipData);
	}

	public double getGridAlpha() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.GRID_ALPHA_KEY);
	}

	/*package*/ double getRawGridAlpha() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.GRID_ALPHA_KEY);
	}

	public void setGridAlpha(double gridAlpha) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.GRID_ALPHA_KEY, gridAlpha);
	}

	/*package*/ double setRawGridAlpha(double gridAlpha) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.GRID_ALPHA_KEY, gridAlpha);
	}

	public double getMajorContourAlpha() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MAJOR_CONTOUR_ALPHA_KEY);
	}

	/*package*/ double getRawMajorContourAlpha() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MAJOR_CONTOUR_ALPHA_KEY);
	}

	public void setMajorContourAlpha(double majorContourAlpha) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MAJOR_CONTOUR_ALPHA_KEY, majorContourAlpha);
	}

	/*package*/ double setRawMajorContourAlpha(double majorContourAlpha) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MAJOR_CONTOUR_ALPHA_KEY, majorContourAlpha);
	}

	public double getMinorContourAlpha() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MINOR_CONTOUR_ALPHA_KEY);
	}

	/*package*/ double getRawMinorContourAlpha() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MINOR_CONTOUR_ALPHA_KEY);
	}

	public void setMinorContourAlpha(double minorContourAlpha) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MINOR_CONTOUR_ALPHA_KEY, minorContourAlpha);
	}

	/*package*/ double setRawMinorContourAlpha(double minorContourAlpha) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.MINOR_CONTOUR_ALPHA_KEY, minorContourAlpha);
	}

	public int getRotatingOffset() {
		return get(org.archstudio.bna.facets.IHasRotatingOffset.ROTATING_OFFSET_KEY);
	}

	/*package*/ int getRawRotatingOffset() {
		return getRaw(org.archstudio.bna.facets.IHasRotatingOffset.ROTATING_OFFSET_KEY);
	}

	public void setRotatingOffset(int rotatingOffset) {
		set(org.archstudio.bna.facets.IHasRotatingOffset.ROTATING_OFFSET_KEY, rotatingOffset);
	}

	/*package*/ int setRawRotatingOffset(int rotatingOffset) {
		return setRaw(org.archstudio.bna.facets.IHasRotatingOffset.ROTATING_OFFSET_KEY, rotatingOffset);
	}

	public boolean isSelected() {
		return get(org.archstudio.bna.facets.IHasSelected.SELECTED_KEY);
	}

	/*package*/ boolean isRawSelected() {
		return getRaw(org.archstudio.bna.facets.IHasSelected.SELECTED_KEY);
	}

	public void setSelected(boolean selected) {
		set(org.archstudio.bna.facets.IHasSelected.SELECTED_KEY, selected);
	}

	/*package*/ boolean isRawSelected(boolean selected) {
		return setRaw(org.archstudio.bna.facets.IHasSelected.SELECTED_KEY, selected);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getXMajorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MAJOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawXMajorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MAJOR_AXIS_KEY);
	}

	public void setXMajorAxis(org.archstudio.bna.things.graphs.NumericAxis xMajorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MAJOR_AXIS_KEY, xMajorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawXMajorAxis(org.archstudio.bna.things.graphs.NumericAxis xMajorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MAJOR_AXIS_KEY, xMajorAxis);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getXMinorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MINOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawXMinorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MINOR_AXIS_KEY);
	}

	public void setXMinorAxis(org.archstudio.bna.things.graphs.NumericAxis xMinorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MINOR_AXIS_KEY, xMinorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawXMinorAxis(org.archstudio.bna.things.graphs.NumericAxis xMinorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_MINOR_AXIS_KEY, xMinorAxis);
	}

	public int getXRotation() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_ROTATION_KEY);
	}

	/*package*/ int getRawXRotation() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_ROTATION_KEY);
	}

	public void setXRotation(int xRotation) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_ROTATION_KEY, xRotation);
	}

	/*package*/ int setRawXRotation(int xRotation) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.X_ROTATION_KEY, xRotation);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getYMajorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MAJOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawYMajorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MAJOR_AXIS_KEY);
	}

	public void setYMajorAxis(org.archstudio.bna.things.graphs.NumericAxis yMajorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MAJOR_AXIS_KEY, yMajorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawYMajorAxis(org.archstudio.bna.things.graphs.NumericAxis yMajorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MAJOR_AXIS_KEY, yMajorAxis);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getYMinorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MINOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawYMinorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MINOR_AXIS_KEY);
	}

	public void setYMinorAxis(org.archstudio.bna.things.graphs.NumericAxis yMinorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MINOR_AXIS_KEY, yMinorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawYMinorAxis(org.archstudio.bna.things.graphs.NumericAxis yMinorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_MINOR_AXIS_KEY, yMinorAxis);
	}

	public int getYRotation() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_ROTATION_KEY);
	}

	/*package*/ int getRawYRotation() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_ROTATION_KEY);
	}

	public void setYRotation(int yRotation) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_ROTATION_KEY, yRotation);
	}

	/*package*/ int setRawYRotation(int yRotation) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Y_ROTATION_KEY, yRotation);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getZMajorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MAJOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawZMajorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MAJOR_AXIS_KEY);
	}

	public void setZMajorAxis(org.archstudio.bna.things.graphs.NumericAxis zMajorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MAJOR_AXIS_KEY, zMajorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawZMajorAxis(org.archstudio.bna.things.graphs.NumericAxis zMajorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MAJOR_AXIS_KEY, zMajorAxis);
	}

	public org.archstudio.bna.things.graphs.NumericAxis getZMinorAxis() {
		return get(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MINOR_AXIS_KEY);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis getRawZMinorAxis() {
		return getRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MINOR_AXIS_KEY);
	}

	public void setZMinorAxis(org.archstudio.bna.things.graphs.NumericAxis zMinorAxis) {
		set(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MINOR_AXIS_KEY, zMinorAxis);
	}

	/*package*/ org.archstudio.bna.things.graphs.NumericAxis setRawZMinorAxis(org.archstudio.bna.things.graphs.NumericAxis zMinorAxis) {
		return setRaw(org.archstudio.bna.things.graphs.NumericSurfaceGraphThing.Z_MINOR_AXIS_KEY, zMinorAxis);
	}

}
