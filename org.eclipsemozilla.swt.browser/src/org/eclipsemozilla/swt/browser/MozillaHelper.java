/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipsemozilla.swt.browser;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.mozilla.xpcom.GREVersionRange;

public class MozillaHelper {

	static final String XULRUNNER_PATH = "org.eclipse.swt.browser.XULRunnerPath"; //$NON-NLS-1$

	/**
	 * Use the first contributed Xulrunner that satisfies version ranges
	 * constraints <br/>
	 * 
	 * Note: other potential Xulrunner contributions will be ignored (such as
	 * registered Xulruneers), but the SWT System property
	 * org.eclipse.swt.browser.XULRunnerPath will be honored
	 * 
	 * @param range
	 */
	public static boolean definedContributedXulRunner(GREVersionRange[] range) {
		// if a system property is already defined, do nothing. SWT will pick it
		// up as is.
		if (hasSystemDefinedXulrunner()) {
			return false;
		}

		boolean isContributed = false;
		if (range == null) {
			range = new GREVersionRange[1];
			range[0] = new GREVersionRange("1.8.1.1", false, "1.8.1.*", true);
		}
		File grePath = getContributedXulRunner(range);
		if (grePath != null) {
			System.setProperty(XULRUNNER_PATH, grePath.getAbsolutePath());
			isContributed = true;
		}
		return isContributed;
	}

	/**
	 * Return the first contributed Xulrunner that satisfies a version range
	 * 
	 * @param range
	 *            a GRE versions range
	 * @return an existing directory pointing to a Xulrunner root, that matches
	 *         the desired version range
	 */
	static File getContributedXulRunner(GREVersionRange[] range) {
		XulRunnerExtensionReader xer = new XulRunnerExtensionReader();
		Map contributedXulRunners = xer.getXulRunners();
		for (Iterator iter = contributedXulRunners.keySet().iterator(); iter.hasNext();) {
			File rootDir = (File) iter.next();
			String version = (String) contributedXulRunners.get(rootDir);
			for (int i = 0; i < range.length; i++) {
				if (range[i].check(version)) {
					return rootDir;
				}
			}
		}
		return null;
	}

	/**
	 * @return true if a Xulrunner rootdir contributed by the pre existing
	 *         System.property expected by SWT:
	 *         org.eclipse.swt.browser.XULRunnerPath , false otherwise
	 */
	static boolean hasSystemDefinedXulrunner() {
		// if a System property has already been defined, like from the command
		// line, we take it as is, and ignore any other contributions
		String sysPropXulPath = System.getProperty(XULRUNNER_PATH);
		if (sysPropXulPath != null) {
			File sysPropXulRoot = new File(sysPropXulPath);
			if (sysPropXulRoot.isDirectory()) {
				return true;
			}
		}
		return false;
	}
}
