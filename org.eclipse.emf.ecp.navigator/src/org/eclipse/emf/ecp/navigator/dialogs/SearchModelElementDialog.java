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
package org.eclipse.emf.ecp.navigator.dialogs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.common.dialogs.ModelElementSelectionDialog;
import org.eclipse.emf.ecp.common.model.ECPWorkspaceManager;
import org.eclipse.emf.ecp.common.model.workSpaceModel.ECPProject;

/**
 * Dialog to select model elements.
 * 
 * @author mkagel
 */
public class SearchModelElementDialog extends ModelElementSelectionDialog {

	/**
	 * The constructor.
	 * 
	 * @param project the project, which contains all the model elements that can be searched for
	 */
	public SearchModelElementDialog(ECPProject project) {
		super(project);
	}

	/**
	 * Fills the content provider with all elements matching the items filter.
	 * 
	 * @param contentProvider the content provider which gets added the items
	 * @param itemsFilter the used items filter
	 * @param progressMonitor a progress monitor stating the progress
	 */
	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
		IProgressMonitor progressMonitor) {

		progressMonitor.beginTask("Searching", getModelElements().size());
		for (EObject modelElement : getModelElements()) {
			ECPProject project = ECPWorkspaceManager.getECPProject(modelElement);
			if (!(project.getMetaModelElementContext().isNonDomainElement(modelElement.eClass()))) {
				contentProvider.add(modelElement, itemsFilter);
				progressMonitor.worked(1);
			}
		}
		progressMonitor.done();
	}
}
