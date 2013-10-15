package org.archstudio.bna.things.glass;

import org.archstudio.bna.facets.IHasMutableRotatingOffset;
import org.archstudio.bna.facets.IHasMutableSelected;
import org.archstudio.bna.facets.IHasRotatingOffset;
import org.archstudio.bna.facets.IHasSelected;
import org.archstudio.bna.facets.IIsSticky;
import org.archstudio.bna.things.AbstractPreciselyAnchoredShapeThing;

public class PreciselyAnchoredShapeGlassThing extends AbstractPreciselyAnchoredShapeThing implements IIsSticky,
		IHasMutableSelected, IHasMutableRotatingOffset {

	public PreciselyAnchoredShapeGlassThing(Object id) {
		super(id);
	}

	@Override
	protected void initProperties() {
		super.initProperties();
		setSelected(false);
		incrementRotatingOffset();
	}

	@Override
	public int getRotatingOffset() {
		return get(IHasRotatingOffset.ROTATING_OFFSET_KEY);
	}

	@Override
	public boolean shouldIncrementRotatingOffset() {
		return isSelected();
	}

	@Override
	public void incrementRotatingOffset() {
		Integer io = get(IHasRotatingOffset.ROTATING_OFFSET_KEY);
		int i = io == null ? 0 : io + 1;
		set(IHasRotatingOffset.ROTATING_OFFSET_KEY, i);
	}

	@Override
	public boolean isSelected() {
		return Boolean.TRUE.equals(get(IHasSelected.SELECTED_KEY));
	}

	@Override
	public void setSelected(boolean selected) {
		set(IHasSelected.SELECTED_KEY, selected);
	}

}
