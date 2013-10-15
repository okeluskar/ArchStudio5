package org.archstudio.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ org.archstudio.prolog.test.AllTests.class, org.archstudio.xadl.AllTests.class,
		org.archstudio.xarchadt.common.AllTests.class, org.archstudio.xarchadt.variability.AllTests.class })
public class AllTests {

}
