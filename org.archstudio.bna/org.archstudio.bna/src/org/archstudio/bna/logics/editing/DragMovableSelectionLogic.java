package org.archstudio.bna.logics.editing;

import org.archstudio.bna.IThing;
import org.archstudio.bna.facets.IHasSelected;
import org.archstudio.bna.facets.IHasUserEditable;
import org.archstudio.bna.logics.AbstractThingLogic;
import org.archstudio.bna.logics.tracking.SelectionTrackingLogic;
import org.eclipse.swt.graphics.Point;

public class DragMovableSelectionLogic extends AbstractThingLogic implements IDragMoveListener {

	protected DragMovableLogic dml = null;
	protected SelectionTrackingLogic stl = null;

	public DragMovableSelectionLogic(DragMovableLogic dml, SelectionTrackingLogic stl) {
		this.dml = dml;
		this.stl = stl;
	}

	protected void init() {
		dml.addDragMoveListener(this);
	}

	protected void destroy() {
		dml.removeDragMoveListener(this);
	}

	public void dragMoved(DragMoveEvent evt) {
		IThing movedThing = evt.getMovedThing();
		if (movedThing instanceof IHasSelected) {
			if (((IHasSelected) movedThing).isSelected()) {
				IHasSelected[] otherSelectedThings = stl.getSelectedThings();
				for (IHasSelected otherSelectedThing : otherSelectedThings) {
					if (!otherSelectedThing.equals(movedThing)) {
						if ((otherSelectedThing instanceof IDragMovable)
								&& (otherSelectedThing instanceof IHasUserEditable)) {
							if (((IHasUserEditable) otherSelectedThing).isUserEditable()) {
								IDragMovable rmt = (IDragMovable) otherSelectedThing;
								Point rp = rmt.getReferencePoint();
								rp.x += evt.getDX();
								rp.y += evt.getDY();
								rmt.setReferencePoint(rp);
								//((IRelativeMovable)otherSelectedThing).moveRelative(evt.getDX(), evt.getDY());
							}
						}
					}
				}
			}
		}
	}

	public void dragFinished(DragMoveEvent evt) {
	}
}