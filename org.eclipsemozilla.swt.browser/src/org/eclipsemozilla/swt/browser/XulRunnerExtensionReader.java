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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class XulRunnerExtensionReader {

	public static final String EXTENSION_POINT_ID = "xulrunner"; //$NON-NLS-1$

	public static final String XULRUNNER_ELEMENT = "xulrunner"; //$NON-NLS-1$

	public static final String XULRUNNER_BUNDLEID_ATTRIBUTE = "bundleId"; //$NON-NLS-1$

	public static final String XULRUNNER_PATH_ATTRIBUTE = "path"; //$NON-NLS-1$

	public static final String XULRUNNER_VERSION_ATTRIBUTE = "version"; //$NON-NLS-1$

	public XulRunnerExtensionReader() {
		super();
	}

	public Map getXulRunners() {
		IExtensionPoint point = getExtensionPoint();
		if (point == null) {
			SwtBrowserPlugin.logInfo("No plugin contributing a private Xulrunner. " //$NON-NLS-1$
					+ "Using the default platform registered Xulrunner."); //$NON-NLS-1$
			return null;
		}

		Map xulRunnerRoots = new HashMap();
		IConfigurationElement[] configElements = point.getConfigurationElements();
		for (int i = 0; i < configElements.length; i++) {
			IConfigurationElement element = configElements[i];
			String contributorId = element.getContributor().getName();
			String xulRunner = configElements[i].getName();
			if (!XULRUNNER_ELEMENT.equals(xulRunner)) {
				continue;
			}
			
			String bundleId = element.getAttribute(XULRUNNER_BUNDLEID_ATTRIBUTE);
			if (isNullOrEmpty(bundleId)) {
				SwtBrowserPlugin.logError("Invalid extension contributed by:" + contributorId //$NON-NLS-1$
						+ " Missing or empty bundleId attribute in extension: " + SwtBrowserPlugin.PLUGIN_ID + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ EXTENSION_POINT_ID);
				continue;
			}

			String path = element.getAttribute(XULRUNNER_PATH_ATTRIBUTE);
			if (isNullOrEmpty(path)) {
				SwtBrowserPlugin.logError("Invalid extension contributed by:" + contributorId //$NON-NLS-1$
						+ " Missing or empty path attribute in extension: " + SwtBrowserPlugin.PLUGIN_ID + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ EXTENSION_POINT_ID);
				continue;
			}
			String version = element.getAttribute(XULRUNNER_VERSION_ATTRIBUTE);
			if (isNullOrEmpty(version)) {
				SwtBrowserPlugin.logError("Invalid extension contributed by:" + contributorId //$NON-NLS-1$
						+ " Missing or empty version attribute in extension: " + SwtBrowserPlugin.PLUGIN_ID + "." //$NON-NLS-1$ //$NON-NLS-2$
						+ EXTENSION_POINT_ID);
				continue;
			}

			File xulRunnerRootDir = getXulRunnerRootDir(bundleId, path);

			if (xulRunnerRootDir != null && version.length() > 0) {
				xulRunnerRoots.put(xulRunnerRootDir, version);
			} else {
				SwtBrowserPlugin
						.logError("Invalid extension contributed by:" + contributorId //$NON-NLS-1$
								+ " Missing, empty or invalid bundleID, version or path attribute in extension: " + SwtBrowserPlugin.PLUGIN_ID + "." //$NON-NLS-1$ //$NON-NLS-2$
								+ EXTENSION_POINT_ID);
			}
		}
		return xulRunnerRoots;
	}
	
	private boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0 ;
	}

	private IExtensionPoint getExtensionPoint() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(SwtBrowserPlugin.PLUGIN_ID, EXTENSION_POINT_ID);
		return point;
	}

	/**
	 * Returns a java File pointing to a directory inside a plugin installation
	 * location
	 * 
	 * @param pluginId
	 * @param pluginRelativePath
	 * @return a java File pointing to a xulrunner root directory
	 */
	File getXulRunnerRootDir(String bundleId, String rootRelativePath) {
		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle == null) {
			return null;
		}
		try {
			URL filePath = bundle.getEntry(rootRelativePath);
			if (filePath == null) {
				return null;
			}
			URL localPath = FileLocator.resolve(filePath);
			File xulRunnerRoot = new File(FileLocator.toFileURL(localPath).getFile());
			if (xulRunnerRoot.isDirectory()) {
				return xulRunnerRoot;
			}
		} catch (IOException e) {
			String message = "Invalid Xulrunner installed in bundle:" + bundleId //$NON-NLS-1$
					+ " Invalid extension location contribution in extension: " + SwtBrowserPlugin.PLUGIN_ID + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ EXTENSION_POINT_ID + "with location: " + rootRelativePath + " : " + e.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$;

			Status status = new Status(IStatus.ERROR, bundleId, IStatus.ERROR, message, e);
			SwtBrowserPlugin.logStatus(status);
		}
		return null;
	}
}
