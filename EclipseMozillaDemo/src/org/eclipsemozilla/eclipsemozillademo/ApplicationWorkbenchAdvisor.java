/*******************************************************************************
 * Copyright (c) 2008 EclipseMozilla.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thomas Derflinger - tderflinger@gmail.com - initial API and implementation
 *******************************************************************************/
package org.eclipsemozilla.eclipsemozillademo;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipsemozilla.mozeditor.ui.browser.MozBrowserEditor;
import org.eclipsemozilla.mozeditor.ui.browser.MozBrowserEditorInput;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	protected static final String ERROR_MSG = "Error opening Mozilla Browser!";

	protected void showError(IStatus status) {
		try {
			ErrorDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), null, ERROR_MSG,
					status);
		} catch (Exception e) {
			// in case of an NPE getting the Active Workbench Window
		}
	}

	@Override
	public void postStartup() {

		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		;

		if (activeWindow == null) {
			showError(new Status(
					IStatus.ERROR,
					EclipseMozillaApplication.ID,
					IStatus.ERROR,
					"Error opening Mozilla Browser... failed to retrieve active workbench window!",
					null));
		}

		IWorkbenchPage activePage = activeWindow.getActivePage();
		if (activePage == null) {
			showError(new Status(
					IStatus.ERROR,
					EclipseMozillaApplication.ID,
					IStatus.ERROR,
					"Error opening Mozilla Browser... could not retrieve active page!",
					null));
		}

		MozBrowserEditorInput editorInput = new MozBrowserEditorInput(
				"http://www.eclipsemozilla.org");

		try {
			activePage.openEditor(editorInput, MozBrowserEditor.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.postStartup();
	}

	private static final String PERSPECTIVE_ID = "EclipseMozillaDemo.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

}
