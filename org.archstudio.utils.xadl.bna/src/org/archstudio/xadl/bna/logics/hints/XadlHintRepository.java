package org.archstudio.xadl.bna.logics.hints;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.archstudio.bna.IBNAWorld;
import org.archstudio.bna.IThing;
import org.archstudio.bna.logics.hints.EncodedValue;
import org.archstudio.bna.logics.hints.IEncodedValue;
import org.archstudio.bna.logics.hints.IHintRepository;
import org.archstudio.bna.logics.hints.IHintRepositoryChangeListener;
import org.archstudio.bna.logics.hints.IPropertyCoder;
import org.archstudio.bna.logics.hints.MasterPropertyCoder;
import org.archstudio.bna.logics.hints.PropertyDecodeException;
import org.archstudio.bna.utils.Assemblies;
import org.archstudio.sysutils.SystemUtils;
import org.archstudio.xadl.XadlUtils;
import org.archstudio.xadl.bna.facets.IHasObjRef;
import org.archstudio.xadl3.hints_3_0.Hint;
import org.archstudio.xadl3.hints_3_0.HintsExtension;
import org.archstudio.xadl3.hints_3_0.Hints_3_0Package;
import org.archstudio.xadl3.hints_3_0.Value;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.IXArchADTModelListener;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.XArchADTModelEvent;
import org.archstudio.xarchadt.XArchADTProxy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.Maps;

public class XadlHintRepository implements IHintRepository, IXArchADTModelListener {

	private static class HintValueImpl implements HintValue {

		private final boolean present;
		private final Object value;

		public HintValueImpl(boolean present, Object value) {
			this.present = present;
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return present;
		}

		@Override
		public Object getValue() {
			return value;
		}
	}

	private final IXArchADT xarch;
	private final IPropertyCoder coder;

	public XadlHintRepository(IXArchADT xarch) {
		this.xarch = xarch;
		this.coder = new MasterPropertyCoder();
	}

	@Override
	public @Nullable
	Object getContextForThing(IBNAWorld world, IThing thing) {
		IThing t = thing;
		ObjRef objRef = null;
		do {
			objRef = t.get(IHasObjRef.OBJREF_KEY);
			if (objRef != null) {
				break;
			}
			t = Assemblies.getRootWithPart(world.getBNAModel(), t);
		} while (t != null);
		if (objRef != null && t != null) {
			return objRef;
		}
		return null;
	}

	private @Nullable
	Hint getHint(HintsExtension hints, String name) {
		for (Hint hint : hints.getHint()) {
			if (name.equals(hint.getName())) {
				return hint;
			}
		}
		return null;
	}

	private Hint createHint(HintsExtension hints, String name) {
		Hint hint = getHint(hints, name);
		if (hint == null) {
			hint = XArchADTProxy.create(xarch, Hints_3_0Package.Literals.HINT);
			hint.setName(name);
			hints.getHint().add(hint);
		}
		return hint;
	}

	@Override
	public boolean storeHint(Object context, String hintName, @Nullable Serializable hintValue) {
		HintsExtension hints = XArchADTProxy.proxy(xarch, XadlUtils.createExt(xarch, (ObjRef) context,
				Hints_3_0Package.eNS_URI, Hints_3_0Package.Literals.HINTS_EXTENSION.getName()));
		return encode(createHint(hints, hintName), hintValue);
	}

	@Override
	public boolean removeHint(Object context, String hintName) {
		HintsExtension hints = XArchADTProxy.proxy(xarch, XadlUtils.getExt(xarch, (ObjRef) context,
				Hints_3_0Package.eNS_URI, Hints_3_0Package.Literals.HINTS_EXTENSION.getName()));
		if (hints != null) {
			for (Hint hint : hints.getHint()) {
				if (hintName.equals(hint.getName())) {
					hints.getHint().remove(hint);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public @Nullable
	HintValue getHint(Object context, String hintName) throws PropertyDecodeException {
		HintsExtension hints = XArchADTProxy.proxy(xarch, XadlUtils.getExt(xarch, (ObjRef) context,
				Hints_3_0Package.eNS_URI, Hints_3_0Package.Literals.HINTS_EXTENSION.getName()));
		if (hints != null) {
			Hint hint = getHint(hints, hintName);
			if (hint != null) {
				return new HintValueImpl(true, decode(hint));
			}
		}
		return new HintValueImpl(false, null);
	}

	@Override
	public Map<String, HintValue> getHints(Object context) {
		HintsExtension hints = XArchADTProxy.proxy(xarch, XadlUtils.getExt(xarch, (ObjRef) context,
				Hints_3_0Package.eNS_URI, Hints_3_0Package.Literals.HINTS_EXTENSION.getName()));
		Map<String, HintValue> hintValues = Maps.newHashMap();
		if (hints != null) {
			for (Hint hint : hints.getHint()) {
				try {
					hintValues.put(hint.getName(), new HintValueImpl(true, decode(hint)));
				}
				catch (PropertyDecodeException e) {
					e.printStackTrace();
				}
			}
		}
		return hintValues;
	}

	private boolean encode(Hint hint, @Nullable Serializable hintValue) {
		String newValue = null;
		if (hintValue != null) {
			IEncodedValue encodedValue = coder.encode(coder, hintValue);
			newValue = encodedValue.getType() + ":" + encodedValue.getData();
		}
		String oldValue = hint.getHint();
		if (!SystemUtils.nullEquals(oldValue, newValue)) {
			hint.setHint(newValue);
			return true;
		}
		return false;
	}

	private @Nullable
	Serializable decode(@Nullable Hint hint) throws PropertyDecodeException {
		if (hint != null) {
			String value = hint.getHint();
			if (value == null) {
				Value deprecatedValue = hint.getValue();
				if (deprecatedValue != null) {
					value = deprecatedValue.getType() + ":" + deprecatedValue.getData();
					hint.setHint(value);
					hint.setValue(null);
				}
			}
			if (value != null) {
				String[] hintParts = value.split(":", 2);
				return (Serializable) coder.decode(coder, new EncodedValue(hintParts[0], hintParts[1]));
			}
		}
		return null;
	}

	CopyOnWriteArrayList<IHintRepositoryChangeListener> changeListeners = new CopyOnWriteArrayList<IHintRepositoryChangeListener>();

	@Override
	public void addHintRepositoryChangeListener(IHintRepositoryChangeListener l) {
		changeListeners.add(l);
	}

	@Override
	public void removeHintRepositoryChangeListener(IHintRepositoryChangeListener l) {
		changeListeners.remove(l);
	}

	protected void fireHintRepositoryChangeEvent(Object context, String name) {
		for (IHintRepositoryChangeListener l : changeListeners) {
			l.hintRepositoryChanged(this, context, name);
		}
	}

	@Override
	public void handleXArchADTModelEvent(XArchADTModelEvent evt) {
		EObject src = XArchADTProxy.proxy(xarch, evt.getSource());
		if (src instanceof Hint) {
			Hint hint = (Hint) src;
			if (hint.eContainer() != null && hint.eContainer().eContainer() != null) {
				fireHintRepositoryChangeEvent(XArchADTProxy.unproxy(hint.eContainer().eContainer()), hint.getName());
			}
		}
	}
}
