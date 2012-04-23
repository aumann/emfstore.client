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
package org.eclipse.emf.ecp.common.utilities;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.common.Activator;
import org.eclipse.emf.ecp.common.model.ECPWorkspaceManager;
import org.eclipse.emf.ecp.common.observer.ModelElementOpenObserver;
import org.eclipse.emf.ecp.common.util.ModelElementOpener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Hodaie This class contains some utility method for commands and handlers.
 */
public final class ActionHelper {
	// TODO: move constants
	/**
	 * The ID of the meeditor.
	 */
	public static final String MEEDITOR_ID = "org.eclipse.emf.ecp.editor";
	/**
	 * Constant for the open model element command.
	 */
	public static final String MEEDITOR_OPENMODELELEMENT_COMMAND_ID = "org.eclipse.emf.ecp.editor.openModelElement";

	/**
	 * Constant for the modelelement context.
	 */
	public static final String MECONTEXT_EVALUATIONCONTEXT_VARIABLE = "meContext";

	/**
	 * Constant for the modelelement to be opened.
	 */
	public static final String ME_TO_OPEN_EVALUATIONCONTEXT_VARIABLE = "meToOpen";
	//private static final String FEATURE_TO_MARK_EVALUATIONCONTEXT_VARIABLE = "featureToMark";

	private ActionHelper() {

	}

	/**
	 * This opens the model element.
	 * 
	 * @param me ModelElement to open
	 * @param sourceView the view that requested the open model element
	 */
	public static void openModelElement(final EObject me, final String sourceView) {
		if (me == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "The element was deleted",
				"The model element you are trying to open was deleted!");
			return;
		}
		IConfigurationElement[] modelelementopener = Platform.getExtensionRegistry().getConfigurationElementsFor(
			"org.eclipse.emf.ecp.common.modelelementopener");
		ModelElementOpener bestCandidate = null;
		int bestValue = -1;
		String name = "";
		for (IConfigurationElement element : modelelementopener) {
			modelelementopener = null;
			try {
				ModelElementOpener modelelementOpener = (ModelElementOpener) element.createExecutableExtension("class");
				int value = modelelementOpener.canOpen(me);
				if (value > bestValue) {
					bestCandidate = modelelementOpener;
					bestValue = value;
					name = element.getAttribute("name");
				}
			} catch (CoreException e) {
				Activator.getDefault().logException(e.getMessage(), e);
			}
		}
		ECPWorkspaceManager.getObserverBus().notify(ModelElementOpenObserver.class).onOpen(me, sourceView, name);
		// BEGIN SUPRESS CATCH EXCEPTION
		try {
			bestCandidate.openModelElement(me);
		} catch (RuntimeException e) {
			Activator.getDefault().logException(e);
		}
		// END SUPRESS CATCH EXCEPTION

	}

}
