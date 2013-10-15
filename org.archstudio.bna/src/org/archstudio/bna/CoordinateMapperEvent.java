package org.archstudio.bna;

import static org.archstudio.sysutils.SystemUtils.simpleName;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class CoordinateMapperEvent {

	public enum EventType {
		WORLD_BOUNDS(false, false), //
		LOCAL_ORIGIN(true, false), //
		LOCAL_SCALE(false, true), //
		LOCAL_SCALE_AND_ORIGIN(true, true), //
		OTHER(true, true);

		private final boolean originModified;
		private final boolean scaleModified;

		private EventType(boolean originModified, boolean scaleModified) {
			this.originModified = originModified;
			this.scaleModified = scaleModified;
		}

		public boolean isOriginModified() {
			return originModified;
		}

		public boolean isScaleModified() {
			return scaleModified;
		}
	}

	private final ICoordinateMapper source;
	private final EventType eventType;
	private final Rectangle newWorldBounds;
	private final Point newLocalOrigin;
	private final double newLocalScale;

	public CoordinateMapperEvent(ICoordinateMapper source, EventType eventType, Rectangle newWorldBounds,
			Point newLocalOrigin, double newLocalScale) {
		this.source = source;
		this.eventType = eventType;
		this.newWorldBounds = new Rectangle(newWorldBounds.x, newWorldBounds.y, newWorldBounds.width,
				newWorldBounds.height);
		this.newLocalOrigin = new Point(newLocalOrigin.x, newLocalOrigin.y);
		this.newLocalScale = newLocalScale;
	}

	public ICoordinateMapper getSource() {
		return source;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Rectangle getNewWorldBounds() {
		return newWorldBounds;
	}

	public Point getNewLocalOrigin() {
		return newLocalOrigin;
	}

	public double getNewLocalScale() {
		return newLocalScale;
	}

	@Override
	public String toString() {
		return simpleName(this.getClass()) + "["//
				+ "eventType=" + eventType + ","//
				+ "newLocalOrigin=" + newLocalOrigin + ","//
				+ "newLocalScale=" + newLocalScale + ","//
				+ "newWorldBounds=" + newWorldBounds + ","//
				+ "source=" + source + "]";
	}
}
