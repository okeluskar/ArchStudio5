package org.archstudio.bna.things.labels;

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
 * org.archstudio.bna/Package[name=org.archstudio.bna.things.labels]/Thing[name=DirectionalLabel].
 */
 
@SuppressWarnings("all")
@NonNullByDefault
public abstract class DirectionalLabelThingBase extends org.archstudio.bna.things.AbstractThing
	implements org.archstudio.bna.IThing,
			org.archstudio.bna.facets.IHasMutableBoundingBox,
			org.archstudio.bna.facets.IHasMutableColor,
			org.archstudio.bna.facets.IHasMutableFlow,
			org.archstudio.bna.facets.IHasMutableLocalInsets,
			org.archstudio.bna.facets.IHasMutableOrientation,
			org.archstudio.bna.facets.IHasMutableReferencePoint {

	public DirectionalLabelThingBase(@Nullable Object id) {
		super(id);
	}

	@Override
	public IThingPeer<? extends DirectionalLabelThing> createPeer(IBNAView view, ICoordinateMapper cm) {
		return new DirectionalLabelThingPeer<>((DirectionalLabelThing)this, view, cm);
	}

	@Override
	protected void initProperties() {
		initProperty(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY, new org.eclipse.swt.graphics.Rectangle(0, 0, 30, 20));
		addShapeModifyingKey(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY);
		initProperty(org.archstudio.bna.facets.IHasColor.COLOR_KEY, new org.eclipse.swt.graphics.RGB(0, 0, 0));
		initProperty(org.archstudio.bna.facets.IHasFlow.FLOW_KEY, org.archstudio.swtutils.constants.Flow.NONE);
		initProperty(org.archstudio.bna.facets.IHasLocalInsets.LOCAL_INSETS_KEY, new java.awt.Insets(0, 0, 0, 0));
		initProperty(org.archstudio.bna.facets.IHasOrientation.ORIENTATION_KEY, org.archstudio.swtutils.constants.Orientation.NONE);
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

	public @Nullable org.eclipse.swt.graphics.RGB getColor() {
		return get(org.archstudio.bna.facets.IHasColor.COLOR_KEY);
	}

	/*package*/ @Nullable org.eclipse.swt.graphics.RGB getRawColor() {
		return getRaw(org.archstudio.bna.facets.IHasColor.COLOR_KEY);
	}

	public void setColor(@Nullable org.eclipse.swt.graphics.RGB color) {
		set(org.archstudio.bna.facets.IHasColor.COLOR_KEY, color);
	}

	/*package*/ @Nullable org.eclipse.swt.graphics.RGB setRawColor(@Nullable org.eclipse.swt.graphics.RGB color) {
		return setRaw(org.archstudio.bna.facets.IHasColor.COLOR_KEY, color);
	}

	public org.archstudio.swtutils.constants.Flow getFlow() {
		return get(org.archstudio.bna.facets.IHasFlow.FLOW_KEY);
	}

	/*package*/ org.archstudio.swtutils.constants.Flow getRawFlow() {
		return getRaw(org.archstudio.bna.facets.IHasFlow.FLOW_KEY);
	}

	public void setFlow(org.archstudio.swtutils.constants.Flow flow) {
		set(org.archstudio.bna.facets.IHasFlow.FLOW_KEY, flow);
	}

	/*package*/ org.archstudio.swtutils.constants.Flow setRawFlow(org.archstudio.swtutils.constants.Flow flow) {
		return setRaw(org.archstudio.bna.facets.IHasFlow.FLOW_KEY, flow);
	}

	public java.awt.Insets getLocalInsets() {
		return get(org.archstudio.bna.facets.IHasLocalInsets.LOCAL_INSETS_KEY);
	}

	/*package*/ java.awt.Insets getRawLocalInsets() {
		return getRaw(org.archstudio.bna.facets.IHasLocalInsets.LOCAL_INSETS_KEY);
	}

	public void setLocalInsets(java.awt.Insets localInsets) {
		set(org.archstudio.bna.facets.IHasLocalInsets.LOCAL_INSETS_KEY, localInsets);
	}

	/*package*/ java.awt.Insets setRawLocalInsets(java.awt.Insets localInsets) {
		return setRaw(org.archstudio.bna.facets.IHasLocalInsets.LOCAL_INSETS_KEY, localInsets);
	}

	public org.archstudio.swtutils.constants.Orientation getOrientation() {
		return get(org.archstudio.bna.facets.IHasOrientation.ORIENTATION_KEY);
	}

	/*package*/ org.archstudio.swtutils.constants.Orientation getRawOrientation() {
		return getRaw(org.archstudio.bna.facets.IHasOrientation.ORIENTATION_KEY);
	}

	public void setOrientation(org.archstudio.swtutils.constants.Orientation orientation) {
		set(org.archstudio.bna.facets.IHasOrientation.ORIENTATION_KEY, orientation);
	}

	/*package*/ org.archstudio.swtutils.constants.Orientation setRawOrientation(org.archstudio.swtutils.constants.Orientation orientation) {
		return setRaw(org.archstudio.bna.facets.IHasOrientation.ORIENTATION_KEY, orientation);
	}

}
