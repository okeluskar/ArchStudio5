package org.archstudio.bna.facets;

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
 * org.archstudio.bna/Package[name=org.archstudio.bna.facets]/Facet[name=Points].
 */

@SuppressWarnings("all")
@NonNullByDefault
public interface IHasPoints extends org.archstudio.bna.IThing {

	public static final IThingKey<java.util.List<java.awt.geom.Point2D>> POINTS_KEY = ThingKey.create(com.google.common.collect.Lists.newArrayList("points", IHasPoints.class));

	public java.util.List<java.awt.geom.Point2D> getPoints();

}
