/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
////Disabled until bug 353491 is fixed 
///**
// * <copyright> Copyright (c) 2008-2009 Jonas Helming, Maximilian Koegel. All rights reserved. This program and the
// * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
// * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html </copyright>
// */
//package org.eclipse.emf.ecp.validation.pref;
//
//import org.eclipse.emf.emfstore.client.model.ProjectSpace;
//import org.eclipse.emf.emfstore.client.model.WorkspaceManager;
//import org.eclipse.emf.emfstore.client.model.preferences.PreferenceManager;
//import org.eclipse.emf.emfstore.client.model.util.EMFStoreCommand;
//import org.eclipse.emf.emfstore.client.ui.dialogs.login.LoginDialog;
//import org.eclipse.emf.emfstore.server.model.accesscontrol.OrgUnitProperty;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.layout.GridLayoutFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.TabFolder;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.ui.IWorkbenchPropertyPage;
//import org.eclipse.ui.dialogs.PropertyPage;
//
///**
// * A property page for the ShortcutActions.
// * 
// * @author groeber
// */
//public class ValidationPropertyPage extends PropertyPage implements
//		IWorkbenchPropertyPage {
//
//	/**
//	 * Recording command to save the properties in the model.
//	 * 
//	 */
//	private final class SavePropertiesCommand extends EMFStoreCommand {
//
//		@Override
//		protected void doRun() {
//
//			PreferenceManager.INSTANCE.setProperty(projectSpace, ValidationPropertyKey.ENABLELIVEVALIDATION,
//					cb.getSelection());
//			return;
//		}
//	}
//
//	public boolean liveValidationEnabled = false;
//	private ProjectSpace projectSpace;
//	// private Project project;
//	private Button cb;
//
//	@Override
//	protected Control createContents(Composite parent) {
//
//		GridLayoutFactory.fillDefaults().applyTo(parent);
//		noDefaultAndApplyButton();
//
//		if (!init()) {
//			Label label = new Label(parent, SWT.WRAP);
//			label.setText("Could not determine the current project!");
//			return label;
//		}
//		loadProperties();
//
//		TabFolder folder = new TabFolder(parent, SWT.TOP);
//		TabItem generalTab = new TabItem(folder, SWT.NONE);
//
//		generalTab.setControl(createLiveValidationTab(folder));
//		generalTab.setText("Live Validation");
//
//		return parent;
//	}
//
//	private Composite createLiveValidationTab(TabFolder parent) {
//		final Composite composite = new Composite(parent, SWT.NULL);
//		GridLayout gridLayout = new GridLayout(1, false);
//		composite.setLayout(gridLayout);
//
//		cb = new Button(composite, SWT.CHECK);
//		cb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//		cb.setSelection(liveValidationEnabled);
//		cb.setText("Enable live Validation");
//
//		return composite;
//	}
//
//	private void loadProperties() {
//		try {
//
//			OrgUnitProperty userProp = PreferenceManager.INSTANCE.getProperty(projectSpace, ValidationPropertyKey.ENABLELIVEVALIDATION);
//		
//
//			if (userProp != null) {
//				liveValidationEnabled =userProp.getBooleanProperty();
//			}
//		} catch (IllegalStateException e) {
//			if (e.getMessage().contains(
//					"No default value for key ENABLELIVEVALIDATION")) {
//				// Ignore this case: "first run property wasn't set"
//			} else
//				throw e;
//		}
//	}
//
//	private boolean init() {
//
//		projectSpace = WorkspaceManager.getInstance().getCurrentWorkspace()
//				.getActiveProjectSpace();
//		return true;
//	}
//
//	public boolean performOk() {
//		final EMFStoreCommand command = new SavePropertiesCommand();
//		command.run();
//		if (projectSpace.getUsersession().isLoggedIn()) {
//			new EMFStoreCommand() {
//
//				@Override
//				protected void doRun() {
//					projectSpace.transmitProperties();
//				}
//			}.run();
//		} else {
//			new EMFStoreCommand() {
//
//				@Override
//				protected void doRun() {
//					boolean yes = MessageDialog
//							.openQuestion(
//									getShell(),
//									"Transmit properties",
//									"You are currently not logged in! Do you wish to log in and thereby transmit your properties?");
//					if (yes) {
//						LoginDialog loginDialog = new LoginDialog(Display
//								.getCurrent().getActiveShell(), projectSpace
//								.getUsersession().getServerInfo());
//						loginDialog.open();
//					}
//				}
//			}.run();
//
//		}
//		return true;
//	}
//}
