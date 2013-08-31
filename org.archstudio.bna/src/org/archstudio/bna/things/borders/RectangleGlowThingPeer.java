package org.archstudio.bna.things.borders;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.media.opengl.GL2;

import org.archstudio.bna.IBNAView;
import org.archstudio.bna.ICoordinateMapper;
import org.archstudio.bna.IResources;
import org.archstudio.bna.things.AbstractRectangleThingPeer;
import org.archstudio.bna.utils.BNAUtils;
import org.eclipse.swt.graphics.Rectangle;

public class RectangleGlowThingPeer<T extends RectangleGlowThing> extends AbstractRectangleThingPeer<T> {

	public RectangleGlowThingPeer(T thing) {
		super(thing);
	}

	@Override
	public void draw(IBNAView view, ICoordinateMapper cm, GL2 gl, Rectangle clip, IResources r) {
		Rectangle lbb = BNAUtils.getLocalBoundingBox(cm, t);
		if (!clip.intersects(lbb)) {
			return;
		}

		Dimension corner = t.getCornerSize();
		Shape localShape = new RoundRectangle2D.Float(lbb.x, lbb.y, lbb.width, lbb.height, Math.min(lbb.width,
				corner.width), Math.min(lbb.height, corner.height));

		BNAUtils.renderShapeGlow(t, view, cm, gl, clip, r, localShape);
	}
}
