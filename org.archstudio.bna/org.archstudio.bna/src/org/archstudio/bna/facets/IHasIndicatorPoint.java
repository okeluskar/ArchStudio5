package org.archstudio.bna.facets;

import org.archstudio.bna.IThing;
import org.archstudio.bna.keys.ThingKey;
import org.eclipse.draw2d.geometry.Point;

public interface IHasIndicatorPoint extends IThing {
	public static final IThingKey<Point> INDICATOR_POINT_KEY = ThingKey.create("indicatorPoint");

	public Point getIndicatorPoint();
}