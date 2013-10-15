package org.archstudio.bna.logics.navigating;

import static org.archstudio.sysutils.SystemUtils.castOrNull;

import java.util.List;

import org.archstudio.bna.IBNAMouseWheelListener;
import org.archstudio.bna.IBNAView;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.ICoordinate;
import org.archstudio.bna.IMutableCoordinateMapper;
import org.archstudio.bna.IThing;
import org.archstudio.bna.facets.IHasBoundingBox;
import org.archstudio.bna.logics.AbstractThingLogic;
import org.archstudio.bna.utils.BNAUtils;
import org.archstudio.bna.utils.IBNAMouseClickListener;
import org.archstudio.bna.utils.IBNAMouseListener;
import org.archstudio.bna.utils.IBNAMouseMoveListener;
import org.archstudio.sysutils.SystemUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class MousePanAndZoomLogic extends AbstractThingLogic implements IBNAMouseWheelListener, IBNAMouseListener,
		IBNAMouseMoveListener, IBNAMouseClickListener {

	public static final int DEFAULT_PAN_BUTTON = 2; // middle button

	synchronized private static final void union(Rectangle bounds, Rectangle lastBounds) {
		if (bounds.isEmpty()) {
			bounds.x = lastBounds.x;
			bounds.y = lastBounds.y;
			bounds.width = lastBounds.width;
			bounds.height = lastBounds.height;
		}
		else if (!lastBounds.isEmpty()) {
			bounds.union(lastBounds);
		}
	}

	protected int panButton = DEFAULT_PAN_BUTTON;
	protected ICoordinate startMouseCoordinate = null;

	public MousePanAndZoomLogic(IBNAWorld world) {
		super(world);
	}

	@Override
	synchronized public void mouseDown(IBNAView view, MouseEvent e, List<IThing> t, ICoordinate location) {
		//Only handle events for the top world
		if (view.getParentView() != null) {
			return;
		}

		if (e.button == panButton) {
			startMouseCoordinate = location;
			Composite composite = view.getComposite();
			Cursor handCursor = e.display.getSystemCursor(SWT.CURSOR_HAND);
			composite.setCursor(handCursor);
		}
	}

	@Override
	synchronized public void mouseUp(IBNAView view, MouseEvent e, List<IThing> t, ICoordinate location) {
		//Only handle events for the top world
		if (view.getParentView() != null) {
			return;
		}

		if (startMouseCoordinate != null) {
			startMouseCoordinate = null;
			Composite composite = view.getComposite();
			composite.setCursor(null);
		}
	}

	@Override
	synchronized public void mouseMove(IBNAView view, MouseEvent e, List<IThing> t, ICoordinate location) {
		if (startMouseCoordinate != null) {
			IMutableCoordinateMapper mcm = castOrNull(view.getCoordinateMapper(), IMutableCoordinateMapper.class);
			if (mcm != null) {
				mcm.align(location.getLocalPoint(), startMouseCoordinate.getWorldPoint());
			}
		}
	}

	@Override
	synchronized public void mouseWheel(IBNAView view, final MouseEvent e, List<IThing> t, final ICoordinate location) {
		//Only handle events for the top world
		if (view.getParentView() != null) {
			return;
		}

		final IMutableCoordinateMapper mcm = castOrNull(view.getCoordinateMapper(), IMutableCoordinateMapper.class);
		if (mcm != null) {
			double oldScale = mcm.getLocalScale();
			int oldPower = BNAUtils.round(Math.log(oldScale) / Math.log(Math.sqrt(2)));
			int newPower = SystemUtils.bound(-10, oldPower + (e.count < 0 ? -1 : 1), 10);
			if (newPower != oldPower) {
				final double newScale = Math.pow(Math.sqrt(2), newPower);
				mcm.setLocalScaleAndAlign(newScale, location.getLocalPoint(), location.getWorldPoint());
			}
		}
	}

	@Override
	synchronized public void mouseClick(IBNAView view, MouseEvent evt, List<IThing> t, ICoordinate location) {
	}

	@Override
	synchronized public void mouseDoubleClick(IBNAView view, MouseEvent evt, List<IThing> t, ICoordinate location) {
		if (evt.button == panButton) {
			final IMutableCoordinateMapper mcm = castOrNull(view.getCoordinateMapper(), IMutableCoordinateMapper.class);
			if (mcm != null) {
				Rectangle r = new Rectangle(0, 0, 0, 0);
				for (IThing thing : model.getAllThings()) {
					if (thing instanceof IHasBoundingBox) {
						union(r, ((IHasBoundingBox) thing).getBoundingBox());
					}
				}
				if (!r.isEmpty()) {
					mcm.setLocalScaleAndAlign(1, location.getLocalPoint(), new Point(r.x + r.width / 2, r.y + r.height
							/ 2));
				}
			}
		}
	}
}
