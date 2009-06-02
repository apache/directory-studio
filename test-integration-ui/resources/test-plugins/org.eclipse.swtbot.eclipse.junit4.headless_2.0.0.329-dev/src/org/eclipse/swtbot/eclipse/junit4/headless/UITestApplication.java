/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtbot.eclipse.junit4.headless;

import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.testing.ITestHarness;
import org.eclipse.ui.testing.TestableObject;

/**
 * This is just a copy of org.eclipse.test.UITestApplication from the eclipse test plugin, with {@link #runTests()}
 * overridden to return a custom {@link EclipseTestRunner} that runs in a non-UI thread.
 * 
 * A Workbench that runs a test suite specified in the
 * command line arguments.
 */
public class UITestApplication  implements IPlatformRunnable, ITestHarness, IApplication {

	private static final String DEFAULT_APP_3_0 = "org.eclipse.ui.ide.workbench"; //$NON-NLS-1$
	private static final String DEFAULT_APP_PRE_3_0 = "org.eclipse.ui.workbench"; //$NON-NLS-1$

	private boolean fInDeprecatedMode = false;
	private TestableObject fTestableObject;
	private int fTestRunnerResult = -1;
	private IApplicationContext appContext;


	/* (non-Javadoc)
	 * @see org.eclipse.core.boot.IPlatformRunnable
	 */
	public Object run(final Object args) throws Exception {
		// Get the application to test
		Object application = getApplication((String[])args);
		Assert.assertNotNull(application);

		Object result;
		if (fInDeprecatedMode && (application instanceof IPlatformRunnable))
			result = runDeprecatedApplication((IPlatformRunnable)application, args);
		else
			result = runApplication(application, args);
		if (!IPlatformRunnable.EXIT_OK.equals(result))
			System.err.println("UITestRunner: Unexpected result from running application " + application + ": " + result); //$NON-NLS-1$ //$NON-NLS-2$
		return new Integer(fTestRunnerResult);
	}


	/*
	 * return the application to run, or null if not even the default application
	 * is found.
	 */
	private Object getApplication(String[] args) throws CoreException {
		// Assume we are in 3.0 mode.
		// Find the name of the application as specified by the PDE JUnit launcher.
		// If no application is specified, the 3.0 default workbench application
		// is returned.
		IExtension extension =
			Platform.getExtensionRegistry().getExtension(
					Platform.PI_RUNTIME,
					Platform.PT_APPLICATIONS,
					getApplicationToRun(args));

		// If no 3.0 extension can be found, search the registry
		// for the pre-3.0 default workbench application, i.e. org.eclipse ui.workbench
		// Set the deprecated flag to true
		if (extension == null) {
			extension = Platform.getExtensionRegistry().getExtension(
					Platform.PI_RUNTIME,
					Platform.PT_APPLICATIONS,
					DEFAULT_APP_PRE_3_0);
			fInDeprecatedMode = true;
		}

		Assert.assertNotNull(extension);

		// If the extension does not have the correct grammar, return null.
		// Otherwise, return the application object.
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length > 0) {
			IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
			if (runs.length > 0) {
				Object runnable = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
				if (runnable instanceof IPlatformRunnable)
					return runnable;
				if (runnable instanceof IApplication)
					return runnable;
			}
		}
		return null;
	}

	/**
	 * The -testApplication argument specifies the application to be run.
	 * If the PDE JUnit launcher did not set this argument, then return
	 * the name of the default application.
	 * In 3.0, the default is the "org.eclipse.ui.ide.worbench" application.
	 * 
	 */
	private String getApplicationToRun(String[] args) {
		for (int i = 0; i < args.length; i++)
			if (args[i].equals("-testApplication") && (i < args.length -1)) //$NON-NLS-1$
				return args[i+1];
		return DEFAULT_APP_3_0;
	}

	/**
	 * In 3.0 mode
	 * 
	 */
	private Object runApplication(Object application, Object args) throws Exception {
		fTestableObject = PlatformUI.getTestableObject();
		fTestableObject.setTestHarness(this);
		if (application instanceof IPlatformRunnable)
			return ((IPlatformRunnable) application).run(args);
		return ((IApplication) application).start(appContext);

	}

	/*
	 * If we are in pre-3.0 mode, then the application to run is
	 * "org.eclipse.ui.workbench" Therefore, we safely cast the runnable object
	 * to IWorkbenchWindow. We add a listener to it, so that we know when the
	 * window opens so that we can start running the tests. When the tests are
	 * done, we explicitly call close() on the workbench.
	 */
	private Object runDeprecatedApplication(
			IPlatformRunnable object,
			final Object args)
	throws Exception {

		Assert.assertTrue(object instanceof IWorkbench);

		final IWorkbench workbench = (IWorkbench) object;
		// the 'started' flag is used so that we only run tests when the window
		// is opened
		// for the first time only.
		final boolean[] started = { false };
		workbench.addWindowListener(new IWindowListener() {
			public void windowOpened(IWorkbenchWindow w) {
				if (started[0])
					return;
				w.getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						started[0] = true;
						try {
							fTestRunnerResult = EclipseTestRunner.run((String[]) args);
						} catch (IOException e) {
							e.printStackTrace();
						}
						workbench.close();
					}
				});
			}
			public void windowActivated(IWorkbenchWindow window) {
			}
			public void windowDeactivated(IWorkbenchWindow window) {
			}
			public void windowClosed(IWorkbenchWindow window) {
			}
		});
		return ((IPlatformRunnable) workbench).run(args);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.testing.ITestHarness#runTests()
	 */
	public void runTests() {
		fTestableObject.testingStarting();
		try {
			fTestRunnerResult = EclipseTestRunner.run(Platform.getCommandLineArgs());
		} catch (IOException e) {
			e.printStackTrace();
		}
		fTestableObject.testingFinished();
	}


	public Object start(IApplicationContext context) throws Exception {
		appContext = context;
		String[] args = (String[]) appContext.getArguments().get("application.args"); //$NON-NLS-1$
		if (args == null)
			args = new String[0];
		return run(args);
	}


	public void stop() {
		// TODO Auto-generated method stub

	}

}
