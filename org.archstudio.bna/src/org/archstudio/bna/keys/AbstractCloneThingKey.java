package org.archstudio.bna.keys;

import java.awt.Dimension;
import java.awt.Insets;

import javax.annotation.Nullable;

import org.archstudio.bna.IThing.IThingKey;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public abstract class AbstractCloneThingKey<D, V> extends AbstractGenericThingKey<D, V> {

	public static final Function<Dimension, Dimension> dimension() {
		return new Function<Dimension, Dimension>() {
			@Override
			public Dimension apply(Dimension input) {
				return input != null ? new Dimension(input.width, input.height) : null;
			}
		};
	}

	public static final Function<Insets, Insets> insets() {
		return new Function<Insets, Insets>() {
			@Override
			public Insets apply(Insets input) {
				return input != null ? new Insets(input.top, input.left, input.bottom, input.right) : null;
			}
		};
	}

	public static final Function<Point, Point> point() {
		return new Function<Point, Point>() {
			@Override
			public Point apply(Point input) {
				return input != null ? new Point(input.x, input.y) : null;
			}
		};
	}

	public static final Function<Rectangle, Rectangle> rectangle() {
		return new Function<Rectangle, Rectangle>() {
			@Override
			public Rectangle apply(Rectangle input) {
				return input != null ? new Rectangle(input.x, input.y, input.width, input.height) : null;
			}
		};
	}

	public static final Function<RGB, RGB> rgb() {
		return new Function<RGB, RGB>() {
			@Override
			public RGB apply(RGB input) {
				return input != null ? new RGB(input.red, input.green, input.blue) : null;
			}
		};
	}

	public static final Function<IThingKey<?>, IThingKey<?>> iThingKey() {
		return Functions.identity();
	}

	protected final Function<? super V, V> cloneFunction;

	protected AbstractCloneThingKey(D keyData, boolean isFireEventOnChange, Function<? super V, V> cloneFunction) {
		super(keyData, isFireEventOnChange);
		this.cloneFunction = cloneFunction;
	}

	@Override
	public final @Nullable
	V preWrite(@Nullable V value) {
		return cloneFunction.apply(value);
	}

	@Override
	public final @Nullable
	V postRead(@Nullable V value) {
		return cloneFunction.apply(value);
	}
}
