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
package org.eclipse.emf.ecp.navigator.handler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.common.commands.ECPCommand;
import org.eclipse.emf.ecp.common.model.ECPWorkspaceManager;
import org.eclipse.emf.ecp.common.model.workSpaceModel.ECPProject;
import org.eclipse.emf.ecp.common.util.PreferenceHelper;
import org.eclipse.emf.ecp.common.util.UiUtil;
import org.eclipse.emf.ecp.navigator.Activator;
import org.eclipse.emf.emfstore.common.CommonUtil;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Handles the import of ModelElements into a project.
 */
public class ImportModelHandler extends AbstractHandler {

	/**
	 * These filter extensions are used to filter which files are displayed.
	 */
	public static final String[] FILTER_EXTS = { "*.ecm", "*.*" };

	/**
	 * These filter names are used to filter which files are displayed.
	 */
	public static final String[] FILTER_NAMES = { "EMFStore Project Files (*.ecm)", "All Files (*.*)" };

	private static final String IMPORT_MODEL_PATH = "org.eclipse.emf.emfstore.client.ui.importModelPath";

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final EObject selectedModelElement = UiUtil.getSelectedModelelement();
		final ECPProject project = ECPWorkspaceManager.getECPProject(selectedModelElement);

		if (project == null || selectedModelElement == null) {
			return null;
		}

		final String fileName = getFileName();
		if (fileName == null) {
			return null;
		}

		final URI fileURI = URI.createFileURI(fileName);

		// create resource set and resource
		ResourceSet resourceSet = new ResourceSetImpl();

		final Resource resource = resourceSet.getResource(fileURI, true);

		final ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell());

		new ECPCommand(project.getRootObject()) {
			@Override
			protected void doRun() {
				importFile(project, fileURI, resource, progressDialog);
			}

		}.run(false);

		return null;
	}

	private void importFile(ECPProject project, final URI fileURI, final Resource resource,
		final ProgressMonitorDialog progressDialog) {

		try {
			progressDialog.open();
			progressDialog.getProgressMonitor().beginTask("Import model...", 100);

			Set<EObject> importElements = validation(resource);

			if (importElements.size() > 0) {
				int i = 0;
				for (EObject eObject : importElements) {
					// run the import command
					runImport(project, fileURI, EcoreUtil.copy(eObject), i);
					progressDialog.getProgressMonitor().worked(10);
					i++;
				}
			}
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (RuntimeException e) {
			// TODO: ChainSaw logging done
			Activator.getDefault().logException(e.getMessage(), e);
			// END SUPRESS CATCH EXCEPTION
		} finally {
			progressDialog.getProgressMonitor().done();
			progressDialog.close();
		}
	}

	// Validates if the EObjects can be imported
	private Set<EObject> validation(Resource resource) {
		Set<EObject> childrenSet = new HashSet<EObject>();
		Set<EObject> rootNodes = new HashSet<EObject>();

		EList<EObject> rootContent = resource.getContents();

		for (EObject rootNode : rootContent) {
			TreeIterator<EObject> contents = rootNode.eAllContents();
			// 1. Run: Put all children in set
			while (contents.hasNext()) {
				EObject content = contents.next();
				if (!(content != null)) {
					continue;
				}
				childrenSet.add(content);
			}
		}

		// 2. Run: Check if RootNodes are children -> set.contains(RootNode) -- no: RootNode in rootNode-Set -- yes:
		// Drop RootNode, will be imported as a child
		for (EObject rootNode : rootContent) {

			if (!(rootNode != null)) {
				// No report to Console, because Run 1 will do this
				continue;
			}

			if (!childrenSet.contains(rootNode)) {
				rootNodes.add(rootNode);
			}
		}

		// 3. Check if RootNodes are SelfContained -- yes: import -- no: error
		Set<EObject> notSelfContained = new HashSet<EObject>();
		for (EObject rootNode : rootNodes) {
			if (!CommonUtil.isSelfContained(rootNode)) {
				// TODO: Report to Console //System.out.println(rootNode + " is not selfcontained");
				notSelfContained.add(rootNode);
			}
		}
		rootNodes.removeAll(notSelfContained);

		return rootNodes;
	}

	private String getFileName() {

		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);
		dialog.setFilterNames(FILTER_NAMES);
		dialog.setFilterExtensions(FILTER_EXTS);
		String initialPath = PreferenceHelper.getPreference(IMPORT_MODEL_PATH, System.getProperty("user.home"));
		dialog.setFilterPath(initialPath);

		String fileName = dialog.open();

		if (fileName == null) {
			return null;
		}

		final File file = new File(dialog.getFilterPath(), dialog.getFileName());

		PreferenceHelper.setPreference(IMPORT_MODEL_PATH, file.getParent());

		return file.getAbsolutePath();
	}

	/**
	 * Runs the import command.
	 * 
	 * @param projectSpace - the projectSpace where the element should be imported in.
	 * @param uri - the uri of the resource.
	 * @param element - the modelElement to import.
	 * @param resourceIndex - the index of the element inside the eResource.
	 */
	private void runImport(final ECPProject project, final org.eclipse.emf.common.util.URI uri, final EObject element,
		final int resourceIndex) {

		// TODO: ChainSaw: ModelElementWrapperDescriptor ain't in scope here
		// try to find a wrapper for the element which will be added to the project
		// EObject wrapper = ModelElementWrapperDescriptor.getInstance().wrapForImport(project, element, uri,
		// resourceIndex);

		// // if no wrapper could be created, use the element itself to add it to the project
		// if (wrapper == null) {
		// wrapper = element;
		// }

		// add the wrapper or the element itself to the project
		// copy wrapper to reset model element ids

		project.addModelElementToRoot(element);
	}
}
