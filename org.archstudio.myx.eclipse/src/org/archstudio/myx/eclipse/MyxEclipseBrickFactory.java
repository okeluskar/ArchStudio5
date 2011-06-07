package org.archstudio.myx.eclipse;

import org.archstudio.myx.fw.IMyxBrick;
import org.archstudio.myx.fw.IMyxBrickDescription;
import org.archstudio.myx.fw.IMyxBrickFactory;
import org.archstudio.myx.fw.IMyxBrickInitializationData;
import org.archstudio.myx.fw.IMyxName;
import org.archstudio.myx.fw.MyxBrickCreationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class MyxEclipseBrickFactory implements IMyxBrickFactory {
	protected final IConfigurationElement configurationElement;

	public MyxEclipseBrickFactory(IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	public IMyxBrick create(IMyxName name, IMyxBrickDescription brickDescription, IMyxBrickInitializationData initializationData) throws MyxBrickCreationException {
		try {
			IMyxBrick myxBrick = (IMyxBrick) configurationElement.createExecutableExtension("class");
			return myxBrick;
		}
		catch (CoreException ce) {
			throw new MyxBrickCreationException(ce);
		}
	}
}