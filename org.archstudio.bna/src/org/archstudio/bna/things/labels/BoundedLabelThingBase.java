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
 * org.archstudio.bna/Package[name=org.archstudio.bna.things.labels]/Thing[name=BoundedLabel].
 */
 
@SuppressWarnings("all")
@NonNullByDefault
public abstract class BoundedLabelThingBase extends org.archstudio.bna.things.AbstractThing
	implements org.archstudio.bna.IThing,
			org.archstudio.bna.facets.IHasMutableBoundingBox,
			org.archstudio.bna.facets.IHasMutableColor,
			org.archstudio.bna.facets.IHasMutableFontData,
			org.archstudio.bna.facets.IHasMutableHorizontalAlignment,
			org.archstudio.bna.facets.IHasMutableText,
			org.archstudio.bna.facets.IHasMutableVerticalAlignment {

	public BoundedLabelThingBase(@Nullable Object id) {
		super(id);
	}

	@Override
	public IThingPeer<? extends BoundedLabelThing> createPeer(IBNAView view, ICoordinateMapper cm) {
		return new BoundedLabelThingPeer<>((BoundedLabelThing)this, view, cm);
	}

	@Override
	protected void initProperties() {
		initProperty(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY, new org.eclipse.swt.graphics.Rectangle(0, 0, 30, 20));
		addShapeModifyingKey(org.archstudio.bna.facets.IHasBoundingBox.BOUNDING_BOX_KEY);
		initProperty(org.archstudio.bna.facets.IHasColor.COLOR_KEY, new org.eclipse.swt.graphics.RGB(0, 0, 0));
		initProperty(org.archstudio.bna.facets.IHasFontData.DONT_INCREASE_FONT_SIZE_KEY, false);
		initProperty(org.archstudio.bna.facets.IHasFontData.FONT_NAME_KEY, "Dialog");
		initProperty(org.archstudio.bna.facets.IHasFontData.FONT_SIZE_KEY, 12);
		initProperty(org.archstudio.bna.facets.IHasFontData.FONT_STYLE_KEY, org.archstudio.swtutils.constants.FontStyle.NORMAL);
		initProperty(org.archstudio.bna.facets.IHasHorizontalAlignment.HORIZONTAL_ALIGNMENT_KEY, org.archstudio.swtutils.constants.HorizontalAlignment.CENTER);
		initProperty(org.archstudio.bna.facets.IHasText.TEXT_KEY, "Text");
		initProperty(org.archstudio.bna.facets.IHasVerticalAlignment.VERTICAL_ALIGNMENT_KEY, org.archstudio.swtutils.constants.VerticalAlignment.MIDDLE);
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

	public boolean isDontIncreaseFontSize() {
		return get(org.archstudio.bna.facets.IHasFontData.DONT_INCREASE_FONT_SIZE_KEY);
	}

	/*package*/ boolean isRawDontIncreaseFontSize() {
		return getRaw(org.archstudio.bna.facets.IHasFontData.DONT_INCREASE_FONT_SIZE_KEY);
	}

	public void setDontIncreaseFontSize(boolean dontIncreaseFontSize) {
		set(org.archstudio.bna.facets.IHasFontData.DONT_INCREASE_FONT_SIZE_KEY, dontIncreaseFontSize);
	}

	/*package*/ boolean isRawDontIncreaseFontSize(boolean dontIncreaseFontSize) {
		return setRaw(org.archstudio.bna.facets.IHasFontData.DONT_INCREASE_FONT_SIZE_KEY, dontIncreaseFontSize);
	}

	public java.lang.String getFontName() {
		return get(org.archstudio.bna.facets.IHasFontData.FONT_NAME_KEY);
	}

	/*package*/ java.lang.String getRawFontName() {
		return getRaw(org.archstudio.bna.facets.IHasFontData.FONT_NAME_KEY);
	}

	public void setFontName(java.lang.String fontName) {
		set(org.archstudio.bna.facets.IHasFontData.FONT_NAME_KEY, fontName);
	}

	/*package*/ java.lang.String setRawFontName(java.lang.String fontName) {
		return setRaw(org.archstudio.bna.facets.IHasFontData.FONT_NAME_KEY, fontName);
	}

	public int getFontSize() {
		return get(org.archstudio.bna.facets.IHasFontData.FONT_SIZE_KEY);
	}

	/*package*/ int getRawFontSize() {
		return getRaw(org.archstudio.bna.facets.IHasFontData.FONT_SIZE_KEY);
	}

	public void setFontSize(int fontSize) {
		set(org.archstudio.bna.facets.IHasFontData.FONT_SIZE_KEY, fontSize);
	}

	/*package*/ int setRawFontSize(int fontSize) {
		return setRaw(org.archstudio.bna.facets.IHasFontData.FONT_SIZE_KEY, fontSize);
	}

	public org.archstudio.swtutils.constants.FontStyle getFontStyle() {
		return get(org.archstudio.bna.facets.IHasFontData.FONT_STYLE_KEY);
	}

	/*package*/ org.archstudio.swtutils.constants.FontStyle getRawFontStyle() {
		return getRaw(org.archstudio.bna.facets.IHasFontData.FONT_STYLE_KEY);
	}

	public void setFontStyle(org.archstudio.swtutils.constants.FontStyle fontStyle) {
		set(org.archstudio.bna.facets.IHasFontData.FONT_STYLE_KEY, fontStyle);
	}

	/*package*/ org.archstudio.swtutils.constants.FontStyle setRawFontStyle(org.archstudio.swtutils.constants.FontStyle fontStyle) {
		return setRaw(org.archstudio.bna.facets.IHasFontData.FONT_STYLE_KEY, fontStyle);
	}

	public org.archstudio.swtutils.constants.HorizontalAlignment getHorizontalAlignment() {
		return get(org.archstudio.bna.facets.IHasHorizontalAlignment.HORIZONTAL_ALIGNMENT_KEY);
	}

	/*package*/ org.archstudio.swtutils.constants.HorizontalAlignment getRawHorizontalAlignment() {
		return getRaw(org.archstudio.bna.facets.IHasHorizontalAlignment.HORIZONTAL_ALIGNMENT_KEY);
	}

	public void setHorizontalAlignment(org.archstudio.swtutils.constants.HorizontalAlignment horizontalAlignment) {
		set(org.archstudio.bna.facets.IHasHorizontalAlignment.HORIZONTAL_ALIGNMENT_KEY, horizontalAlignment);
	}

	/*package*/ org.archstudio.swtutils.constants.HorizontalAlignment setRawHorizontalAlignment(org.archstudio.swtutils.constants.HorizontalAlignment horizontalAlignment) {
		return setRaw(org.archstudio.bna.facets.IHasHorizontalAlignment.HORIZONTAL_ALIGNMENT_KEY, horizontalAlignment);
	}

	public java.lang.String getText() {
		return get(org.archstudio.bna.facets.IHasText.TEXT_KEY);
	}

	/*package*/ java.lang.String getRawText() {
		return getRaw(org.archstudio.bna.facets.IHasText.TEXT_KEY);
	}

	public void setText(java.lang.String text) {
		set(org.archstudio.bna.facets.IHasText.TEXT_KEY, text);
	}

	/*package*/ java.lang.String setRawText(java.lang.String text) {
		return setRaw(org.archstudio.bna.facets.IHasText.TEXT_KEY, text);
	}

	public org.archstudio.swtutils.constants.VerticalAlignment getVerticalAlignment() {
		return get(org.archstudio.bna.facets.IHasVerticalAlignment.VERTICAL_ALIGNMENT_KEY);
	}

	/*package*/ org.archstudio.swtutils.constants.VerticalAlignment getRawVerticalAlignment() {
		return getRaw(org.archstudio.bna.facets.IHasVerticalAlignment.VERTICAL_ALIGNMENT_KEY);
	}

	public void setVerticalAlignment(org.archstudio.swtutils.constants.VerticalAlignment verticalAlignment) {
		set(org.archstudio.bna.facets.IHasVerticalAlignment.VERTICAL_ALIGNMENT_KEY, verticalAlignment);
	}

	/*package*/ org.archstudio.swtutils.constants.VerticalAlignment setRawVerticalAlignment(org.archstudio.swtutils.constants.VerticalAlignment verticalAlignment) {
		return setRaw(org.archstudio.bna.facets.IHasVerticalAlignment.VERTICAL_ALIGNMENT_KEY, verticalAlignment);
	}

}
