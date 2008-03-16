/*******************************************************************************
 * Copyright (c) 2008 by EclipseMozilla.org.
 * 
 * Based on portions Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Thomas Derflinger tderflinger@gmail.com - refactoring based on the Eclipse ATF base.
 *******************************************************************************/
package org.eclipsemozilla.mozeditor.ui.browser;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipsemozilla.mozeditor.Activator;
import org.eclipsemozilla.mozeditor.ui.browser.toolbar.NavigationBar;
import org.eclipsemozilla.mozeditor.ui.browser.toolbar.StatusBar;
import org.eclipsemozilla.swt.browser.MozillaHelper;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMKeyEvent;
import org.mozilla.xpcom.XPCOMException;

/**
 * This is the Mozilla browser encapsulated as an editor. <br>
 * It contains a navigation bar and a status bar.
 * 
 */
public class MozBrowserEditor extends EditorPart {

	public final static String ID = "org.eclipsemozilla.mozeditor.ui.browser.MozBrowserEditor";

	public static final String DEFAULT_URL = "about:blank";

	protected Browser browser = null;

	protected NavigationBar navBar = null;

	protected StatusBar statusBar = null;

	// actions
	protected Action backAction = null;
	protected Action forwardAction = null;
	protected Action refreshAction = null;
	protected Action stopAction = null;
	protected Action goAction = null;

	// currently loaded document
	// during a new load, this document will point to the old document until
	// the load is completed.
	protected nsIDOMDocument document = null;
	protected boolean loading = true;

	/*
	 * This enables the use of the mouse to click on an element in the browser
	 * and set it as the Selection.
	 */
	protected boolean controlSelectEnabled = false;

	protected MozillaBrowserListener browserListener = new MozillaBrowserListener(
			this);

	/*
	 * DOMDocumentContainer Support
	 */
	protected ListenerList domDocumentListeners = new ListenerList(); // nofified
	// of
	// document
	// loading
	// and
	// loaded
	protected ListenerList domMutationListeners = new ListenerList(); // notifies

	// changes
	// in
	// the
	// document's
	// structure

	/**
	 * Singleton that provides the main modifier key for the platform. For Linux
	 * and Windows, that is the 'Ctrl' key. For Mac OS X, it is 'Cmd' (Apple)
	 * key.
	 */
	protected static class OSModifierKey {
		private static OSModifierKey instance = new OSModifierKey();
		private long osModifierKeyCode;

		private OSModifierKey() {
			if (SWT.getPlatform() == "carbon")
				osModifierKeyCode = nsIDOMKeyEvent.DOM_VK_META;
			else
				osModifierKeyCode = nsIDOMKeyEvent.DOM_VK_CONTROL;
		}

		public static OSModifierKey getInstance() {
			return instance;
		}

		public long getKeyCode() {
			return osModifierKeyCode;
		}
	}

	@Override
	public void setPartName(String partName) {
		// TODO Auto-generated method stub
		super.setPartName(partName);
	}

	@Override
	public void setTitleToolTip(String toolTip) {
		// TODO Auto-generated method stub
		super.setTitleToolTip(toolTip);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	public Browser getMozillaBrowser() {
		return browser;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * This method is used to programatically change the URL pointed to by the
	 * embedded browser. It defaults to "about:blank" in the case of an error.
	 * 
	 * @TODO: Handle errors by instead displaying, for example, a 404 message
	 */
	public void goToURL(String url) {

		try {
			browser.setUrl(url);
		} catch (XPCOMException xpcome) {
			// might be a bad URL so try opening with "about:blank"
			browser.setUrl(DEFAULT_URL);
		}

	}

	public void clearCache() {

		// if( cacheService == null )
		// cacheService =
		// (nsICacheService)Mozilla.getInstance().getServiceManager().getServiceByContractID(
		// "@mozilla.org/network/cache-service;1",
		// nsICacheService.NS_ICACHESERVICE_IID );
		//		
		// /*
		// * for now since the NSI interface for nsICache in Java does not
		// provide access to
		// * the nsICache.STORE_ON_DISK and nsICache.STORE_IN_MEMORY, need to
		// pass the actual
		// * values. (Got values from LXR)
		// *
		// * const nsCacheStoragePolicy STORE_IN_MEMORY = 1;
		// * const nsCacheStoragePolicy STORE_ON_DISK = 2;
		// */
		// cacheService.evictEntries( 1 );
		// cacheService.evictEntries( 2 );

	}

	/*
	 * This method creates all the actions that are added to the NavigationBar.
	 * These actions control the navigation aspects of the Browser, plus others.
	 */
	protected void createActions() {

		backAction = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				browser.back();
			}
		};

		backAction.setImageDescriptor(Activator
				.getImageDescriptor("icons/browser/e_back.gif"));
		backAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/browser/d_back.gif"));
		backAction.setEnabled(false);

		forwardAction = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				browser.forward();
			}
		};

		forwardAction.setImageDescriptor(Activator
				.getImageDescriptor("icons/browser/e_forward.gif"));
		forwardAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/browser/d_forward.gif"));
		forwardAction.setEnabled(false);

		refreshAction = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				browser.refresh();
			}
		};

		refreshAction.setImageDescriptor(Activator
				.getImageDescriptor("icons/browser/e_refresh.gif"));
		refreshAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/browser/d_refresh.gif"));

		stopAction = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				browser.stop();
			}
		};

		stopAction.setImageDescriptor(Activator
				.getImageDescriptor("icons/browser/e_stop.gif"));
		stopAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/browser/d_stop.gif"));
		stopAction.setEnabled(false);

		goAction = new Action(null, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				goToURL(navBar.getLocationURL());
			}
		};

		goAction.setImageDescriptor(Activator
				.getImageDescriptor("icons/browser/e_go.gif"));
		goAction.setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/browser/dgo.gif"));

	}

	@Override
	public void createPartControl(Composite parent) {

		// In some cases createPartcontrol is called be the
		// early startup code sets the XULRunner path
		MozillaHelper.definedContributedXulRunner(null);

		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.verticalSpacing = 1;
		displayArea.setLayout(gridLayout);

		GridData data;

		// Navigation Bar
		navBar = new NavigationBar(displayArea, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		navBar.setLayoutData(data);

		// separator
		Label upperBarSeparator = new Label(displayArea, SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.LINE_SOLID);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		upperBarSeparator.setLayoutData(data);

		// Actions for the Nav Bar
		createActions(); // add the actions to the navBar
		navBar.setBackAction(backAction);
		navBar.setForwardAction(forwardAction);
		navBar.setRefreshAction(refreshAction);
		navBar.setStopAction(stopAction);
		navBar.setGoAction(goAction);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		// Browser
		browser = new Browser(displayArea, SWT.FILL | SWT.MOZILLA);

		// setting up the network observer (needs to be setup and connected even
		// if the view is not active so that all net calls are registered).
		// netMonAdapter = new MozNetworkMonitorAdapter( this );
		// netMonAdapter.connect();
		// TODO: for later, when I want to add debugging, I could enable the
		// network monitor

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		this.browser.setLayoutData(data);

		// toolbars = ToolbarExtensionManager.create(navBar, displayArea, this
		// );
		// configure toolbar via extension

		// separator
		Label lowerBarSeparator = new Label(displayArea, SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.LINE_SOLID);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		lowerBarSeparator.setLayoutData(data);

		// Status Bar
		statusBar = new StatusBar(displayArea, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		statusBar.setLayoutData(data);

		/*
		 * This object is an instance of an inner class that takes care of
		 * adapting events from the browser that deal with Loading and Progress
		 */
		browserListener.init();

		browser.setUrl("http://www.eclipsemozilla.org");

	}

	@Override
	public void setFocus() {
		if (!browser.isDisposed())
			browser.setFocus();
	}

}
