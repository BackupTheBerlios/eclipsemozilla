/*******************************************************************************
 * Copyright (c) 2006, 2007 nexB Inc. and EasyEclipse.org. All rights reserved. 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     nexB Inc. and EasyEclipse.org - initial API and implementation
 *******************************************************************************/
package org.eclipsemozilla.swt.browser;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * See bug for why we use do start early
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=201774
 */
public class SwtBrowserPlugin extends Plugin implements IStartup {

	public static final String PLUGIN_ID = "org.eclipsemozilla.swt.browser";

	// The shared instance.
	private static SwtBrowserPlugin plugin;

	public SwtBrowserPlugin() {
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SwtBrowserPlugin getDefault() {
		return plugin;
	}

	public void earlyStartup() {
		// See bug for why we do start early
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=201774
		MozillaHelper.definedContributedXulRunner(null);
	}

	public static void logInfo(String message) {
		log(IStatus.INFO, message, null);
	}

	public static void logWarning(String message) {
		log(IStatus.WARNING, message, null);
	}

	public static void logWarning(String message, Throwable e) {
		log(IStatus.WARNING, message, e);
	}

	public static void logError(String message) {
		logError(message, null);
	}

	public static void logError(String message, Throwable e) {
		log(IStatus.ERROR, message, e);
	}

	public static void log(int level, String message, Throwable e) {
		if (message == null) {
			message = ""; //$NON-NLS-1$
		}
		Status status = new Status(level, PLUGIN_ID, IStatus.OK, message, e);
		logStatus(status);
	}

	public static synchronized void logStatus(IStatus status) {
		getDefault().getLog().log(status);
	}
}
