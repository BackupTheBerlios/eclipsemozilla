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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipsemozilla.mozeditor.ui.browser.MozBrowserEditor;
import org.eclipsemozilla.mozeditor.ui.browser.MozBrowserEditorInput;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	protected static final String ERROR_MSG = "Error opening Mozilla Browser!";
	
	private IWorkbenchAction exitAction;

	private IWorkbenchWindow myWindow;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		this.myWindow = window;
	}

	protected void showError( IStatus status ){
		try{
			ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, ERROR_MSG, status );
		}
		catch( Exception e ){
			//in case of an NPE getting the Active Workbench Window
		}
	}

	
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		
		fileMenu.add(new Action("&Open Browser Tab") {

			@Override
			public void run() {

				IWorkbenchWindow activeWindow = myWindow;

				if (activeWindow == null) {
					showError(
							new Status(
									IStatus.ERROR,
									EclipseMozillaApplication.ID,
									IStatus.ERROR,
									"Error opening Mozilla Browser... failed to retrieve active workbench window!",
									null));
				}
					
				IWorkbenchPage activePage = activeWindow.getActivePage();
				if (activePage == null) {
					showError(
							new Status(
									IStatus.ERROR,
									EclipseMozillaApplication.ID,
									IStatus.ERROR,
									"Error opening Mozilla Browser... could not retrieve active page!",
									null));
				}

				MozBrowserEditorInput editorInput = new MozBrowserEditorInput(
						"http://www.eclipsemozilla.org");

				try {
					activePage.openEditor(editorInput, MozBrowserEditor.ID );
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		fileMenu.add(exitAction);
	}

}
