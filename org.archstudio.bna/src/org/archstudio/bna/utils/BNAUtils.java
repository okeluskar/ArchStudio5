package org.archstudio.bna.utils;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.archstudio.bna.IBNAModel;
import org.archstudio.bna.IBNAView;
import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.ICoordinateMapper;
import org.archstudio.bna.IThing;
import org.archstudio.bna.IThingPeer;
import org.archstudio.bna.facets.IHasAnchorPoint;
import org.archstudio.bna.facets.IHasBoundingBox;
import org.archstudio.bna.facets.IHasSelected;
import org.archstudio.bna.facets.IHasStickyShape;
import org.archstudio.bna.facets.IHasWorld;
import org.archstudio.bna.facets.peers.IHasInnerViewPeer;
import org.archstudio.swtutils.SWTWidgetUtils;
import org.archstudio.swtutils.constants.Orientation;
import org.archstudio.sysutils.ExpandableFloatBuffer;
import org.archstudio.sysutils.ExpandableIntBuffer;
import org.archstudio.sysutils.FastMap;
import org.archstudio.sysutils.Finally;
import org.archstudio.sysutils.SystemUtils;
import org.archstudio.sysutils.UIDGenerator;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.google.common.util.concurrent.CycleDetectingLockFactory.Policies;

public class BNAUtils {

	private static final ReentrantReadWriteLock LOCK = CycleDetectingLockFactory.newInstance(Policies.DISABLED)
			.newReentrantReadWriteLock(BNAUtils.class.getName(), true);

	public static final double TO_POINTS_ERROR = 0.5d;
	public static final double TO_POINTS_ERROR_SQ = TO_POINTS_ERROR * TO_POINTS_ERROR;
	public static final double REAL_EQUAL_THRESHOLD = 0.0000001d;
	public static final int LINE_CLICK_DISTANCE = 5;

	@SuppressWarnings("unchecked")
	public static final <T> T castOrNull(IThing thing, Class<T> thingClass) {
		return thingClass.isInstance(thing) ? (T) thing : null;
	}

	private static final Finally UNLOCK = new Finally() {

		@Override
		public void close() {
			LOCK.writeLock().unlock();
		}
	};

	public static Finally lock() {
		LOCK.writeLock().lock();
		return UNLOCK;
	}

	/**
	 * Asserts that the current thread has the lock. The lock is required for <b><i>any</i></b> BNA related method call.
	 */
	public static void checkLock() {
		if (!LOCK.writeLock().isHeldByCurrentThread()) {
			throw new IllegalStateException("Thread does not hold current lock");
		}
	}

	public static Rectangle normalizeRectangle(Rectangle rectangleResult) {
		if (rectangleResult.width < 0) {
			rectangleResult.x -= rectangleResult.width;
			rectangleResult.width = -rectangleResult.width;
		}
		if (rectangleResult.height < 0) {
			rectangleResult.y -= rectangleResult.height;
			rectangleResult.height = -rectangleResult.height;
		}
		return rectangleResult;
	}

	public static String generateUID(String prefix) {
		return UIDGenerator.generateUID(prefix);
	}

	public static boolean isWithin(Rectangle outsideRect, int x, int y) {
		Rectangle out = normalizeRectangle(outsideRect);
		int x1 = out.x;
		int x2 = out.x + out.width;
		int y1 = out.y;
		int y2 = out.y + out.height;

		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	public static boolean isWithin(Rectangle outsideRect, Rectangle insideRect) {
		Rectangle in = normalizeRectangle(insideRect);

		return isWithin(outsideRect, in.x, in.y) && isWithin(outsideRect, in.x + in.width, in.y)
				&& isWithin(outsideRect, in.x, in.y + in.height)
				&& isWithin(outsideRect, in.x + in.width, in.y + in.height);
	}

	public static boolean realEq(double v1, double v2) {
		return Math.abs(v2 - v1) < REAL_EQUAL_THRESHOLD;
	}

	private static FastMap<Class<?>, Comparator<?>> likeHelpers = new FastMap<>(true);
	static {
		likeHelpers.put(Point2D.Float.class, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D o1, Point2D o2) {
				return realEq(o1.getX(), o2.getX()) && realEq(o1.getY(), o2.getY()) ? 0 : -1;
			}
		});
		likeHelpers.put(Point2D.Double.class, new Comparator<Point2D>() {
			@Override
			public int compare(Point2D o1, Point2D o2) {
				return realEq(o1.getX(), o2.getX()) && realEq(o1.getY(), o2.getY()) ? 0 : -1;
			}
		});
	}

	public static final boolean like(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			if (o1 != null || o2 != null) {
				return false;
			}
			return true;
		}
		@SuppressWarnings("unchecked")
		Comparator<Object> c = (Comparator<Object>) likeHelpers.get(o1.getClass());
		if (c == null) {
			return o1.equals(o2);
		}
		return c.compare(o1, o2) == 0;
	}

	public static final Point clone(Point p) {
		return new Point(p.x, p.y);
	}

	public static final Rectangle clone(Rectangle r) {
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	public static Dimension clone(Dimension d) {
		return new Dimension(d.width, d.height);
	}

	public static final List<Point> worldToLocalPoint(final ICoordinateMapper cm, List<Point> worldPoints) {
		return Lists.transform(worldPoints, new Function<Point, Point>() {

			@Override
			public Point apply(Point input) {
				return cm.worldToLocal(new Point(input.x, input.y));
			}
		});
	}

	public static final List<Point2D> worldToLocalPoint2D(final ICoordinateMapper cm,
			List<? extends Point2D> worldPoints) {
		return Lists.transform(worldPoints, new Function<Point2D, Point2D>() {

			@Override
			public Point2D apply(Point2D input) {
				return cm.worldToLocal(input);
			}
		});
	}

	public static Dimension worldToLocalCornerSize(ICoordinateMapper cm, Rectangle worldRectangle,
			int worldCornerWidth, int worldCornerHeight) {
		Rectangle cornerWorldRect = new Rectangle(worldRectangle.x, worldRectangle.y, Math.min(worldCornerWidth,
				worldRectangle.width), Math.min(worldCornerHeight, worldRectangle.height));
		Rectangle localCornerWorldRect = cm.worldToLocal(cornerWorldRect);
		return new Dimension(localCornerWorldRect.width, localCornerWorldRect.height);
	}

	public static final List<Point> localToWorld(ICoordinateMapper cm, List<Point> localPointsResult) {
		for (Point p : localPointsResult) {
			cm.localToWorld(p);
		}
		return localPointsResult;
	}

	public static boolean wasControlPressed(MouseEvent evt) {
		return (evt.stateMask & SWT.CONTROL) != 0;
	}

	public static boolean wasShiftPressed(MouseEvent evt) {
		return (evt.stateMask & SWT.SHIFT) != 0;
	}

	public static boolean wasClick(MouseEvent downEvt, MouseEvent upEvt) {
		if (downEvt.button == upEvt.button) {
			int dx = upEvt.x - downEvt.x;
			int dy = upEvt.y - downEvt.y;
			if (dx == 0 && dy == 0) {
				return true;
			}
		}
		return false;
	}

	public static void setRectangle(Rectangle r, int x, int y, int width, int height) {
		r.x = x;
		r.y = y;
		r.width = width;
		r.height = height;
	}

	public static Rectangle createAlignedRectangle(Point p, int width, int height, Orientation o) {
		Rectangle r = new Rectangle(0, 0, 0, 0);
		alignRectangle(r, p, width, height, o);
		return r;
	}

	public static void alignRectangle(Rectangle r, Point p, int width, int height, Orientation o) {
		switch (o) {
		case NONE:
			setRectangle(r, p.x - width / 2, p.y - height / 2, width, height);
			break;
		case NORTHWEST:
			setRectangle(r, p.x - width, p.y - height, width, height);
			break;
		case NORTH:
			setRectangle(r, p.x - width / 2, p.y - height, width, height);
			break;
		case NORTHEAST:
			setRectangle(r, p.x, p.y - height, width, height);
			break;
		case EAST:
			setRectangle(r, p.x, p.y - height / 2, width, height);
			break;
		case SOUTHEAST:
			setRectangle(r, p.x, p.y, width, height);
			break;
		case SOUTH:
			setRectangle(r, p.x - width / 2, p.y, width, height);
			break;
		case SOUTHWEST:
			setRectangle(r, p.x - width, p.y, width, height);
			break;
		case WEST:
			setRectangle(r, p.x - width, p.y - height / 2, width, height);
			break;
		}
	}

	private static float[] deg2rad = null;

	public static float degreesToRadians(int degrees) {
		while (degrees < 0) {
			degrees += 360;
		}
		degrees = degrees % 360;
		if (deg2rad == null) {
			deg2rad = new float[360];
			for (int i = 0; i < 360; i++) {
				deg2rad[i] = i * (float) Math.PI / 180f;
			}
		}
		return deg2rad[degrees];
	}

	public static Point[] toPointArray(int[] points) {
		Point[] pa = new Point[points.length / 2];
		for (int i = 0; i < points.length; i += 2) {
			pa[i / 2] = new Point(points[i], points[i + 1]);
		}
		return pa;
	}

	public static int[] createIsocolesTriangle(Rectangle sbb, Orientation facing) {
		int ft = 6;
		int fb = 16;
		int x1 = sbb.x;
		int y1 = sbb.y;
		int xm = sbb.x + sbb.width / 2;
		int ym = sbb.y + sbb.height / 2;
		int xq = x1 + sbb.width * ft / fb;
		int yq = y1 + sbb.height * ft / fb;
		int xqg = x1 + sbb.width - sbb.width * ft / fb;
		int yqg = y1 + sbb.height - sbb.height * ft / fb;
		int x2 = x1 + sbb.width;
		int y2 = y1 + sbb.height;

		int px1, px2, px3;
		int py1, py2, py3;

		switch (facing) {
		case NORTHWEST:
			px1 = xq;
			py1 = y2;
			px2 = x1;
			py2 = y1;
			px3 = x2;
			py3 = yq;
			break;
		case NORTH:
			px1 = x1;
			py1 = y2;
			px2 = xm;
			py2 = y1;
			px3 = x2;
			py3 = y2;
			break;
		case NORTHEAST:
			px1 = x1;
			py1 = yq;
			px2 = x2;
			py2 = y1;
			px3 = xqg;
			py3 = y2;
			break;
		case EAST:
			px1 = x1;
			py1 = y1;
			px2 = x2;
			py2 = ym;
			px3 = x1;
			py3 = y2;
			break;
		case SOUTHEAST:
			px1 = xqg;
			py1 = y1;
			px2 = x2;
			py2 = y2;
			px3 = x1;
			py3 = yqg;
			break;
		case SOUTH:
			px1 = x1;
			py1 = y1;
			px2 = xm;
			py2 = y2;
			px3 = x2;
			py3 = y1;
			break;
		case SOUTHWEST:
			px1 = xq;
			py1 = y1;
			px2 = x1;
			py2 = y2;
			px3 = x2;
			py3 = yqg;
			break;
		case WEST:
			px1 = x2;
			py1 = y1;
			px2 = x1;
			py2 = ym;
			px3 = x2;
			py3 = y2;
			break;
		default:
			throw new IllegalArgumentException("Invalid facing");
		}

		return new int[] { px1, py1, px2, py2, px3, py3 };
	}

	public static Rectangle insetRectangle(Rectangle r, Rectangle insets) {
		Rectangle i = new Rectangle(r.x + insets.x, r.y + insets.y, r.width + insets.width, r.height + insets.height);
		if (i.width < 0) {
			return null;
		}
		if (i.height < 0) {
			return null;
		}
		if (!r.contains(i.x, i.y)) {
			return null;
		}
		return i;
	}

	public static boolean isEdgePoint(Point p, Rectangle r) {
		int x2 = r.x + r.width;
		int y2 = r.y + r.height;
		if (p.x == r.x && p.y >= r.y && p.y <= y2) {
			// It's on the left rail
			return true;
		}
		if (p.x == x2 && p.y >= r.y && p.y <= y2) {
			// It's on the right rail
			return true;
		}
		if (p.y == r.y && p.x >= r.x && p.x <= x2) {
			// it's on the top rail
			return true;
		}
		if (p.y == y2 && p.x >= r.x && p.x <= x2) {
			// it's on the bottom rail
			return true;
		}
		return false;
	}

	public static Orientation getOrientationOfEdgePoint(Point p, Rectangle r) {
		int x2 = r.x + r.width;
		int y2 = r.y + r.height;
		if (p.x == r.x && p.y == r.y) {
			return Orientation.NORTHWEST;
		}
		else if (p.x == r.x && p.y == y2) {
			return Orientation.SOUTHWEST;
		}
		else if (p.x == x2 && p.y == r.y) {
			return Orientation.NORTHEAST;
		}
		else if (p.x == x2 && p.y == y2) {
			return Orientation.SOUTHEAST;
		}
		else if (p.x == r.x && p.y >= r.y && p.y <= y2) {
			return Orientation.WEST;
		}
		if (p.x == x2 && p.y >= r.y && p.y <= y2) {
			return Orientation.EAST;
		}
		if (p.y == r.y && p.x >= r.x && p.x <= x2) {
			return Orientation.NORTH;
		}
		if (p.y == y2 && p.x >= r.x && p.x <= x2) {
			return Orientation.SOUTH;
		}

		// it's not on a rail
		return Orientation.NONE;

	}

	public static Point findClosestEdgePoint(Point p, Rectangle r) {
		Point np = new Point(p.x, p.y);

		boolean midx = false;
		boolean midy = false;

		if (p.x < r.x) {
			// It's to the left of the rectangle
			np.x = r.x;
		}
		else if (p.x < r.x + r.width) {
			// It's in the middle of the rectangle
			midx = true;
		}
		else {
			// It's beyond the right of the rectangle
			np.x = r.x + r.width;
		}

		if (p.y < r.y) {
			np.y = r.y;
		}
		else if (p.y < r.y + r.height) {
			midy = true;
		}
		else {
			np.y = r.y + r.height;
		}

		if (midx && midy) {
			// It was within the rectangle
			int dl = Math.abs(p.x - r.x);
			int dr = Math.abs(p.x - (r.x + r.width));
			int dt = Math.abs(p.y - r.y);
			int db = Math.abs(p.y - (r.y + r.height));

			if (dt <= db && dt <= dl && dt <= dr) {
				// it's closest to the top rail.
				np.y = r.y;
				return np;
			}
			else if (db <= dt && db <= dl && db <= dr) {
				// it's closest to the bottom rail
				np.y = r.y + r.height;
				return np;
			}
			else if (dl <= dt && dl <= db && dl <= dr) {
				// it's closest to the left rail
				np.x = r.x;
				return np;
			}
			else {
				np.x = r.x + r.width;
				return np;
			}
		}
		return np;
	}

	public static Point scaleAndMoveBorderPoint(Point p, Rectangle oldRect, Rectangle newRect) {
		if (oldRect == null || newRect == null) {
			return new Point(p.x, p.y);
		}

		//int ox1 = oldRect.x;
		//int ox2 = oldRect.x + oldRect.width;
		//int oy1 = oldRect.y;
		//int oy2 = oldRect.y + oldRect.height;
		int ow = oldRect.width;
		int oh = oldRect.height;

		//int nx1 = newRect.x;
		//int nx2 = newRect.x + newRect.width;
		//int ny1 = newRect.y;
		//int ny2 = newRect.y + newRect.height;
		int nw = newRect.width;
		int nh = newRect.height;

		int dw = nw - ow;
		int dh = nh - oh;

		double sx = (double) nw / (double) ow;
		double sy = (double) nh / (double) oh;

		//int dx = nx1 - ox1;
		//int dy = ny1 - oy1;

		Point p2 = new Point(p.x, p.y);

		if (p.y == oldRect.y) {
			// It's on the top rail

			// Keep it on the top rail
			p2.y = newRect.y;

			// Old distance from the left rail
			int dist = p.x - oldRect.x;

			if (dw != 0) {
				// Scale that distance
				dist = SystemUtils.round(dist * sx);
			}

			// Also perform translation
			p2.x = newRect.x + dist;
		}
		else if (p.y == oldRect.y + oldRect.height // - 1
				|| p.y == oldRect.y + oldRect.height) {
			// It's on the bottom rail

			// Keep it on the bottom rail
			p2.y = newRect.y + newRect.height/* - 1 */;

			// Old distance from the left rail
			int dist = p.x - oldRect.x;

			if (dw != 0) {
				// Scale that distance
				dist = SystemUtils.round(dist * sx);
			}

			// Also perform translation
			p2.x = newRect.x + dist;
		}
		else if (p.x == oldRect.x) {
			// It's on the left rail

			// Keep it on the left rail
			p2.x = newRect.x;

			// Old distance from the top rail
			int dist = p.y - oldRect.y;

			if (dh != 0) {
				// Scale that distance
				dist = SystemUtils.round(dist * sy);
			}

			// Also perform translation
			p2.y = newRect.y + dist;
		}
		else if (p.x == oldRect.x + oldRect.width // - 1
				|| p.x == oldRect.x + oldRect.width) {
			// It's on the right rail

			// Keep it on the right rail
			p2.x = newRect.x + newRect.width/* - 1 */;

			// Old distance from the top rail
			int dist = p.y - oldRect.y;

			if (dh != 0) {
				// Scale that distance
				dist = SystemUtils.round(dist * sy);
			}

			// Also perform translation
			p2.y = newRect.y + dist;
		}

		// Normalize
		if (p2.x < newRect.x) {
			p2.x = newRect.x;
		}
		if (p2.x >= newRect.x + newRect.width) {
			p2.x = newRect.x + newRect.width/* - 1 */;
		}
		if (p2.y < newRect.y) {
			p2.y = newRect.y;
		}
		if (p2.y >= newRect.y + newRect.height) {
			p2.y = newRect.y + newRect.height/* - 1 */;
		}

		return p2;
	}

	public static RGB getRGBForSystemColor(Device d, int systemColorID) {
		Color c = d.getSystemColor(systemColorID);
		if (c == null) {
			return null;
		}
		return c.getRGB();
	}

	public static boolean isPointOnRectangle(Point p, Rectangle r) {
		return isPointOnRectangle(p.x, p.y, r.x, r.y, r.width, r.height);
	}

	public static boolean isPointOnRectangle(int x, int y, int rx, int ry, int rw, int rh) {
		if (x == rx || x == rx + rw) {
			if (y >= ry && y <= ry + rh) {
				return true;
			}
		}
		if (y == ry || y == ry + rh) {
			if (x >= rx && x <= rx + rw) {
				return true;
			}
		}
		return false;
	}

	public static class PointToRectangleDistanceData {

		public Orientation closestSide;
		public double dist;
	}

	public static PointToRectangleDistanceData getPointToRectangleDistance(Point p, Rectangle r) {
		double closestDist = Double.MAX_VALUE;
		Orientation closestSide = Orientation.NONE;

		double dist;
		// Check north distance
		dist = Line2D.ptSegDist(r.x, r.y, r.x + r.width, r.y, p.x, p.y);
		if (dist < closestDist) {
			closestDist = dist;
			closestSide = Orientation.NORTH;
		}
		dist = Line2D.ptSegDist(r.x, r.y, r.x, r.y + r.height, p.x, p.y);
		if (dist < closestDist) {
			closestDist = dist;
			closestSide = Orientation.WEST;
		}
		dist = Line2D.ptSegDist(r.x + r.width, r.y, r.x + r.width, r.y + r.height, p.x, p.y);
		if (dist < closestDist) {
			closestDist = dist;
			closestSide = Orientation.EAST;
		}
		dist = Line2D.ptSegDist(r.x, r.y + r.height, r.x + r.width, r.y + r.height, p.x, p.y);
		if (dist < closestDist) {
			closestDist = dist;
			closestSide = Orientation.SOUTH;
		}
		PointToRectangleDistanceData dd = new PointToRectangleDistanceData();
		dd.closestSide = closestSide;
		dd.dist = closestDist;
		return dd;
	}

	//	public static Rectangle clone(Rectangle r) {
	//		return r == null ? null : new Rectangle(r.x, r.y, r.width, r.height);
	//	}
	//
	//	public static final Point clone(Point p) {
	//		return p == null ? null : new Point(p.x, p.y);
	//	}
	//
	//	public static final Point[] clone(Point[] points) {
	//		if (points == null) {
	//			return null;
	//		}
	//		Point[] newPoints = new Point[points.length];
	//		for (int i = 0; i < points.length; i++) {
	//			newPoints[i] = clone(points[i]);
	//		}
	//		return newPoints;
	//	}

	/**
	 * @deprecated Use {@link SWTWidgetUtils#async(Widget,Runnable)} instead
	 */
	@Deprecated
	public static void asyncExec(final Widget w, final Runnable r) {
		SWTWidgetUtils.async(w, r);
	}

	/**
	 * @deprecated Use {@link SWTWidgetUtils#async(Display,Runnable)} instead
	 */
	@Deprecated
	public static void asyncExec(final Display d, final Runnable r) {
		SWTWidgetUtils.async(d, r);
	}

	/**
	 * @deprecated Use {@link SWTWidgetUtils#sync(Widget,Runnable)} instead
	 */
	@Deprecated
	public static void syncExec(final Widget w, final Runnable r) {
		SWTWidgetUtils.sync(w, r);
	}

	/**
	 * @deprecated Use {@link SWTWidgetUtils#sync(Display,Runnable)} instead
	 */
	@Deprecated
	public static void syncExec(final Display d, final Runnable r) {
		SWTWidgetUtils.sync(d, r);
	}

	//	public static Widget getParentsComposite(IBNAView view) {
	//		if (view == null) {
	//			return null;
	//		}
	//		Composite c = view.getParentComposite();
	//		if (c != null) {
	//			return c;
	//		}
	//		return getParentComposite(view.getParentView());
	//	}

	public static @Nullable
	Point2D getCentralPoint(IThing t) {
		if (t instanceof IHasBoundingBox) {
			Rectangle r = ((IHasBoundingBox) t).getBoundingBox();
			return new Point2D.Double(r.x + r.width / 2, r.y + r.height / 2);
		}
		if (t instanceof IHasAnchorPoint) {
			return ((IHasAnchorPoint) t).getAnchorPoint();
		}
		if (t instanceof IHasStickyShape) {
			java.awt.Rectangle r = ((IHasStickyShape) t).getStickyShape().getBounds();
			return new Point2D.Double(r.getCenterX(), r.getCenterY());
		}
		return null;
	}

	private static final Predicate<IThing> isSelectedPredicate = new Predicate<IThing>() {

		@Override
		public boolean apply(IThing t) {
			if (t instanceof IHasSelected) {
				return ((IHasSelected) t).isSelected();
			}
			return false;
		};
	};

	public static final Collection<IThing> getSelectedThings(IBNAModel m) {
		return Collections2.filter(m.getAllThings(), isSelectedPredicate);
	}

	public static final int sizeOfSelectedThings(IBNAModel m) {
		return Iterables.size(getSelectedThings(m));
	}

	public static boolean infinitelyRecurses(IBNAView view) {
		IBNAWorld world = view.getBNAWorld();
		if (world == null) {
			return false;
		}

		IBNAView view2 = view.getParentView();
		while (view2 != null) {
			if (world.equals(view2.getBNAWorld())) {
				return true;
			}
			view2 = view2.getParentView();
		}
		return false;
	}

	public static final Dimension getSize(Rectangle r) {
		return new Dimension(r.width, r.height);
	}

	public static double getDistance(Point p1, Point p2) {
		if (p1 != null && p2 != null) {
			double dx = p2.x - p1.x;
			double dy = p2.y - p1.y;
			return Math.sqrt(dx * dx + dy * dy);
		}
		return Double.POSITIVE_INFINITY;
	}

	public static double getDistance(Point2D p1, Point2D p2) {
		if (p1 != null && p2 != null) {
			return p1.distance(p2);
		}
		return Double.POSITIVE_INFINITY;
	}

	public static Point2D getClosestPointOnShape(Shape shape, double nearX, double nearY, double refX, double refY) {

		// search for the point closest to (nearX,nearY) that intersects the referenceLine
		Line2D referenceLine = new Line2D.Double(refX, refY, nearX, nearY);
		double[] coords = new double[6];
		double closestDistanceSq = Double.POSITIVE_INFINITY;
		Point2D closestIntersection = new Point2D.Double(refX, refY);
		{
			Point2D lastPoint = new Point2D.Double();
			Line2D lineSegment = new Line2D.Double();
			for (PathIterator i = shape.getPathIterator(new AffineTransform(), 0.5f); !i.isDone(); i.next()) {
				switch (i.currentSegment(coords)) {
				case PathIterator.SEG_MOVETO:
					lastPoint.setLocation(coords[0], coords[1]);
					continue;
				case PathIterator.SEG_CLOSE:
					continue;
				case PathIterator.SEG_LINETO:
					lineSegment.setLine(lastPoint.getX(), lastPoint.getY(), coords[0], coords[1]);
					lastPoint.setLocation(coords[0], coords[1]);
					break;
				default:
					throw new UnsupportedOperationException();
				}

				Point2D intersection = getLineIntersection(lineSegment, referenceLine);
				if (lineSegment.ptSegDistSq(intersection) < REAL_EQUAL_THRESHOLD) {
					double distanceSq = intersection.distanceSq(nearX, nearY);
					if (distanceSq < closestDistanceSq) {
						closestDistanceSq = distanceSq;
						closestIntersection = intersection;
					}
				}
			}
		}

		return closestIntersection;
	}

	private static Point2D.Double getLineIntersection(Line2D l, Line2D r) {
		// see: http://en.wikipedia.org/wiki/Line-line_intersection
		double x1 = l.getX1();
		double y1 = l.getY1();
		double x2 = l.getX2();
		double y2 = l.getY2();
		double x3 = r.getX1();
		double y3 = r.getY1();
		double x4 = r.getX2();
		double y4 = r.getY2();
		double x1y2 = x1 * y2;
		double y1x2 = y1 * x2;
		double x3y4 = x3 * y4;
		double y3x4 = y3 * x4;
		double xNumerator = (x1y2 - y1x2) * (x3 - x4) - (x1 - x2) * (x3y4 - y3x4);
		double yNumerator = (x1y2 - y1x2) * (y3 - y4) - (y1 - y2) * (x3y4 - y3x4);
		double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		return new Point2D.Double(xNumerator / denominator, yNumerator / denominator);
	}

	private static Point2D getClosestPointOnLineSegment(Line2D l, double x3, double y3) {
		// see: http://paulbourke.net/geometry/pointline/
		double x1 = l.getX1();
		double y1 = l.getY1();
		double x2 = l.getX2();
		double y2 = l.getY2();
		double u = ((x3 - x1) * (x2 - x1) + (y3 - y1) * (y2 - y1)) / l.getP2().distanceSq(l.getP1());
		double x = x1 + u * (x2 - x1);
		double y = y1 + u * (y2 - y1);
		Point2D p1 = l.getP1();
		Point2D p2 = l.getP2();

		// determine if the calculated intersection is between the segment points
		if (l.ptSegDistSq(x, y) < REAL_EQUAL_THRESHOLD) {
			return new Point2D.Double(x, y);
		}
		// its not, so use the closest end point
		double dp1 = p1.distanceSq(x3, y3);
		double dp2 = p2.distanceSq(x3, y3);
		Point2D p = dp1 < dp2 ? p1 : p2;
		return p;
	}

	public static Point2D getClosestPointOnShape(Shape shape, double nearX, double nearY) {

		// search for the closest line segment
		double[] pathCoords = new double[6];
		double closestDistanceSq = Double.POSITIVE_INFINITY;
		Line2D closestLineSeg = new Line2D.Double();
		Point2D lastPoint = new Point2D.Double(0, 0);
		for (PathIterator i = shape.getPathIterator(new AffineTransform(), 0.5f); !i.isDone(); i.next()) {
			Line2D lineSegment = new Line2D.Double();
			switch (i.currentSegment(pathCoords)) {
			case PathIterator.SEG_MOVETO:
				lastPoint.setLocation(pathCoords[0], pathCoords[1]);
				continue;
			case PathIterator.SEG_CLOSE:
				continue;
			case PathIterator.SEG_LINETO:
				lineSegment.setLine(lastPoint.getX(), lastPoint.getY(), pathCoords[0], pathCoords[1]);
				lastPoint.setLocation(pathCoords[0], pathCoords[1]);
				break;
			default:
				throw new UnsupportedOperationException();
			}

			double distanceSq = lineSegment.ptSegDistSq(nearX, nearY);
			if (distanceSq < closestDistanceSq) {
				closestDistanceSq = distanceSq;
				closestLineSeg.setLine(lineSegment);
			}
		}

		return getClosestPointOnLineSegment(closestLineSeg, nearX, nearY);
	}

	public static final IBNAView getInternalView(IBNAView outerView, IThing worldThing) {
		if (worldThing instanceof IHasWorld) {
			IThingPeer<?> worldThingPeer = outerView.getThingPeer(worldThing);
			if (worldThingPeer instanceof IHasInnerViewPeer) {
				return ((IHasInnerViewPeer<?>) worldThingPeer).getInnerView();
			}
		}
		return null;
	}

	public static final IBNAView getInternalView(IBNAView outerView, Object worldThingID) {
		return getInternalView(outerView, outerView.getBNAWorld().getBNAModel().getThing(worldThingID));
	}

	public static final void expandRectangle(Rectangle r, Point toIncludePoint) {
		if (toIncludePoint.x < r.x) {
			r.width += r.x - toIncludePoint.x;
			r.x = toIncludePoint.x;
		}
		else if (toIncludePoint.x > r.x + r.width) {
			r.width = toIncludePoint.x - r.x;
		}
		if (toIncludePoint.y < r.y) {
			r.height += r.y - toIncludePoint.y;
			r.y = toIncludePoint.y;
		}
		else if (toIncludePoint.y > r.y + r.height) {
			r.height = toIncludePoint.y - r.y;
		}
	}

	public static int[] toXYIntArrayFromPoint2D(List<Point2D> points) {
		int[] xyArray = new int[2 * points.size()];
		for (int i = 0, length = points.size(), xy = 0; i < length; i++) {
			Point2D p = points.get(i);
			xyArray[xy++] = SystemUtils.round(p.getX());
			xyArray[xy++] = SystemUtils.round(p.getY());
		}
		return xyArray;
	}

	public static int[] toXYIntArrayFromPoint(List<Point> points) {
		int[] xyArray = new int[2 * points.size()];
		for (int i = 0, length = points.size(), xy = 0; i < length; i++) {
			Point p = points.get(i);
			xyArray[xy++] = p.x;
			xyArray[xy++] = p.y;
		}
		return xyArray;
	}

	public static int[] toXYIntArrayFromPoint(ICoordinateMapper cm, List<Point> points, Point anchorPoint) {
		int[] xyArray = new int[2 * points.size()];
		for (int i = 0, length = points.size(), xy = 0; i < length; i++) {
			Point p = points.get(i);
			p.x += anchorPoint.x;
			p.y += anchorPoint.y;
			p = cm.worldToLocal(p);
			xyArray[xy++] = p.x;
			xyArray[xy++] = p.y;
		}
		return xyArray;
	}

	private static final Function<IThing, Object> thingToIDFunction = new Function<IThing, Object>() {

		@Override
		public Object apply(IThing input) {
			return input.getID();
		}
	};

	public static final List<Object> getThingIDs(Iterable<? extends IThing> things) {
		return Lists.newArrayList(Iterables.transform(things, thingToIDFunction));
	}

	public static final void union(Rectangle result, Rectangle other) {
		if (result.isEmpty()) {
			result.x = other.x;
			result.y = other.y;
			result.width = other.width;
			result.height = other.height;
		}
		else if (!other.isEmpty()) {
			result.union(other);
		}
	}

	public static RGB adjustBrightness(RGB rgb, float factor) {
		float r = rgb.red * factor;
		float g = rgb.green * factor;
		float b = rgb.blue * factor;

		return new RGB(//
				SystemUtils.bound(0, SystemUtils.round(r), 255),// 
				SystemUtils.bound(0, SystemUtils.round(g), 255),// 
				SystemUtils.bound(0, SystemUtils.round(b), 255));
	}

	public static final Rectangle toRectangle(java.awt.Rectangle bounds) {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public static final Rectangle toRectangle(java.awt.geom.Rectangle2D bounds) {
		int x1 = SystemUtils.floor(bounds.getMinX());
		int y1 = SystemUtils.floor(bounds.getMinY());
		int x2 = SystemUtils.ceil(bounds.getMaxX());
		int y2 = SystemUtils.ceil(bounds.getMaxY());
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	public static final Rectangle2D toRectangle2D(Rectangle bounds) {
		return new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public static Point toPoint(double x, double y) {
		return new Point(SystemUtils.round(x), SystemUtils.round(y));
	}

	public static Point toPoint(java.awt.Point point) {
		return new Point(point.x, point.y);
	}

	public static Point toPoint(Point2D p) {
		return new Point(SystemUtils.round(p.getX()), SystemUtils.round(p.getY()));
	}

	public static final Point2D toPoint2D(Point p) {
		return new Point2D.Float(p.x, p.y);
	}

	public static Dimension toDimension(Point p) {
		return new Dimension(p.x, p.y);
	}

	public static Dimension toDimension(Rectangle2D r) {
		return new Dimension(SystemUtils.round(r.getWidth()), SystemUtils.round(r.getHeight()));
	}

	public static Dimension toDimension(Rectangle lbb) {
		return new Dimension(lbb.width, lbb.height);
	}

	public static void toXYFloatBuffer(Shape localShape, ExpandableFloatBuffer xyFloatBuffer) {
		float firstX = 0, firstY = 0;
		float lastX = 0, lastY = 0;
		PathIterator p = localShape.getPathIterator(null, TO_POINTS_ERROR);
		double[] coords = new double[6];
		while (!p.isDone()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_CLOSE:
				if (Math.abs(firstX - lastX) > TO_POINTS_ERROR || Math.abs(firstY - lastY) > TO_POINTS_ERROR) {
					xyFloatBuffer.put(lastX = firstX);
					xyFloatBuffer.put(lastY = firstY);
				}
				break;
			case PathIterator.SEG_MOVETO:
				xyFloatBuffer.put(firstX = lastX = (float) coords[0]);
				xyFloatBuffer.put(firstY = lastY = (float) coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				float nextX = (float) coords[0];
				float nextY = (float) coords[1];
				if (Math.abs(nextX - lastX) > TO_POINTS_ERROR || Math.abs(nextY - lastY) > TO_POINTS_ERROR) {
					xyFloatBuffer.put(lastX = nextX);
					xyFloatBuffer.put(lastY = nextY);
				}
				break;
			default:
				throw new IllegalArgumentException();
			}
			p.next();
		}
	}

	public static void toXYIntBuffer(Shape localShape, ExpandableIntBuffer xyIntBuffer) {
		int firstX = 0, firstY = 0;
		int lastX = 0, lastY = 0;
		PathIterator p = localShape.getPathIterator(null, TO_POINTS_ERROR);
		double[] coords = new double[6];
		while (!p.isDone()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_CLOSE:
				if (Math.abs(firstX - lastX) > TO_POINTS_ERROR || Math.abs(firstY - lastY) > TO_POINTS_ERROR) {
					xyIntBuffer.put(lastX = firstX);
					xyIntBuffer.put(lastY = firstY);
				}
				break;
			case PathIterator.SEG_MOVETO:
				xyIntBuffer.put(firstX = lastX = SystemUtils.round(coords[0]));
				xyIntBuffer.put(firstY = lastY = SystemUtils.round(coords[1]));
				break;
			case PathIterator.SEG_LINETO:
				int nextX = SystemUtils.round(coords[0]);
				int nextY = SystemUtils.round(coords[1]);
				if (Math.abs(nextX - lastX) > TO_POINTS_ERROR || Math.abs(nextY - lastY) > TO_POINTS_ERROR) {
					xyIntBuffer.put(lastX = nextX);
					xyIntBuffer.put(lastY = nextY);
				}
				break;
			default:
				throw new IllegalArgumentException();
			}
			p.next();
		}
	}

	public static final Path toPath(Path path, Shape localShape) {
		PathIterator p = localShape.getPathIterator(null);
		double[] coords = new double[6];
		while (!p.isDone()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_CLOSE:
				path.close();
				break;
			case PathIterator.SEG_MOVETO:
				path.moveTo((float) coords[0], (float) coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo((float) coords[0], (float) coords[1]);
				break;
			case PathIterator.SEG_QUADTO:
				path.quadTo((float) coords[0], (float) coords[1], (float) coords[2], (float) coords[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				path.cubicTo((float) coords[0], (float) coords[1], (float) coords[2], (float) coords[3],
						(float) coords[4], (float) coords[5]);
				break;
			default:
				throw new IllegalArgumentException();
			}
			p.next();
		}
		return path;
	}

	public static Path2D worldToLocal(ICoordinateMapper cm, Shape shape) {
		Path2D p = new Path2D.Double();
		PathIterator i = shape.getPathIterator(null);
		double[] coords = new double[6];
		Point2D.Double d = new Point2D.Double();
		Point2D d2;
		while (!i.isDone()) {
			switch (i.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				d.x = coords[0];
				d.y = coords[1];
				d2 = cm.worldToLocal(d);
				p.moveTo(d2.getX(), d2.getY());
				break;
			case PathIterator.SEG_LINETO:
				d.x = coords[0];
				d.y = coords[1];
				d2 = cm.worldToLocal(d);
				p.lineTo(d2.getX(), d2.getY());
				break;
			case PathIterator.SEG_QUADTO:
				for (int j = 0; j < 4; j += 2) {
					d.x = coords[j];
					d.y = coords[j + 1];
					d2 = cm.worldToLocal(d);
					coords[j] = d2.getX();
					coords[j + 1] = d2.getY();
				}
				p.quadTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				for (int j = 0; j < 6; j += 2) {
					d.x = coords[j];
					d.y = coords[j + 1];
					d2 = cm.worldToLocal(d);
					coords[j] = d2.getX();
					coords[j + 1] = d2.getY();
				}
				p.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_CLOSE:
				p.closePath();
				break;
			default:
				throw new IllegalArgumentException();
			}
			i.next();
		}
		return p;
	}

}