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
package org.eclipse.emf.ecp.navigatoreditorbridge;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.common.model.ECPModelelementContext;
import org.eclipse.emf.ecp.common.model.ECPWorkspaceManager;
import org.eclipse.emf.ecp.common.model.NoWorkspaceException;
import org.eclipse.emf.ecp.editor.MEEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Opener for the meeditor.
 * 
 * @author helming
 */
public class ModelElementOpener implements org.eclipse.emf.ecp.common.util.ModelElementOpener {

	/**
	 * Default constructor.
	 */
	public ModelElementOpener() {

	}

	/**
	 * {@inheritDoc}
	 */
	public int canOpen(EObject modelElement) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void openModelElement(EObject modelElement) {
		// IHandlerService handlerService = (IHandlerService)
		// PlatformUI.getWorkbench().getService(IHandlerService.class);

		// IEvaluationContext context = handlerService.getCurrentState();
		// context.addVariable(ActionHelper.ME_TO_OPEN_EVALUATIONCONTEXT_VARIABLE, modelElement);

		// try {
		// context.addVariable(ActionHelper.MECONTEXT_EVALUATIONCONTEXT_VARIABLE, ECPWorkspaceManager.getInstance()
		// .getWorkSpace().getProject(modelElement));
		// handlerService.executeCommand(ActionHelper.MEEDITOR_OPENMODELELEMENT_COMMAND_ID, null);

		// } catch (ExecutionException e) {
		// DialogHandler.showExceptionDialog(e);
		// } catch (NotDefinedException e) {
		// DialogHandler.showExceptionDialog(e);
		// } catch (NotEnabledException e) {
		// DialogHandler.showExceptionDialog(e);
		// } catch (NotHandledException e) {
		// DialogHandler.showExceptionDialog(e);
		// } catch (NoWorkspaceException e) {
		// DialogHandler.showExceptionDialog(e);
		// }

		if (modelElement != null) {
			ECPModelelementContext context = null;
			try {
				context = ECPWorkspaceManager.getInstance().getWorkSpace().getProject(modelElement);
			} catch (NoWorkspaceException e1) {
				Activator.getDefault().logException(e1.getMessage(),e1);
			}
			MEEditorInput input = new MEEditorInput(modelElement, context);

			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(input, "org.eclipse.emf.ecp.editor", true);
			} catch (PartInitException e) {
				Activator.getDefault().logException(e.getMessage(),e);
			}
		}
	}
}
