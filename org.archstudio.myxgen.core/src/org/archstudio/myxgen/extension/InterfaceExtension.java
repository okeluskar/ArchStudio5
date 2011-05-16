package org.archstudio.myxgen.extension;

import org.archstudio.myx.fw.EMyxInterfaceDirection;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.pde.core.plugin.IPluginModelBase;

public class InterfaceExtension extends AbstractExtension {

	protected final String name;
	protected final EMyxInterfaceDirection direction;
	protected final boolean single;
	protected final EServiceObjectDelegate serviceObjectDelegate;
	protected final boolean generateGetter;
	protected final String className;
	protected final String description;

	public InterfaceExtension(IPluginModelBase pluginModel, IConfigurationElement element) {
		super(pluginModel, element);
		this.name = element.getAttribute("name");
		this.direction = EMyxInterfaceDirection.valueOf(element.getAttribute("direction").toUpperCase());
		this.single = Boolean.valueOf(element.getAttribute("single"));
		this.serviceObjectDelegate = EServiceObjectDelegate.valueOf(element.getAttribute("delegate"));
		this.generateGetter = Boolean.valueOf(element.getAttribute("getter"));
		this.className = element.getAttribute("class");
		String description = null;
		for (IConfigurationElement child : element.getChildren()) {
			if ("description".equals(child.getName())) {
				description = child.getValue();
				break;
			}
		}
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public EMyxInterfaceDirection getDirection() {
		return direction;
	}

	public boolean isSingle() {
		return single;
	}

	public EServiceObjectDelegate getServiceObjectDelegate() {
		return serviceObjectDelegate;
	}

	public boolean isGenerateGetter() {
		return generateGetter;
	}

	public String getClassName() {
		return className;
	}

	public String getDescription() {
		return description;
	}
}