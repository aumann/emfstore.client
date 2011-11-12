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
package org.eclipse.emf.ecp.common.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

// ZH Implement a proper ExceptionDialog
/**
 * This abstract calls is to show exceptions to the user in a unified way.
 * 
 * @author Helming
 */
public final class DialogHandler {

	private DialogHandler() {

	}

	/**
	 * This method opens a standard error dialog displaying an exception to the user.
	 * 
	 * @param e the exception to be shown.
	 */
	public static void showExceptionDialog(Exception e) {
		showExceptionDialog("Unexpected exception occured", e);
	}

	/**
	 * This method opens a standard error dialog displaying an exception to the user.
	 * 
	 * @param message the message to be shown.
	 */
	public static void showErrorDialog(String message) {
		showExceptionDialog(message, null);
	}

	/**
	 * This method opens a standard error dialog displaying an exception to the user.
	 * 
	 * @param cause the exception to be shown.
	 * @param message the message to be shown.
	 */
	public static void showExceptionDialog(String message, Exception cause) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(message);
		String title = "Error";
		if (cause != null) {
			stringBuilder.append(": ");
			stringBuilder.append(cause.getMessage());
			title = cause.getClass().getName();
		}
		String string = stringBuilder.toString();
		MessageDialog.openError(shell, title, string);
		Activator.getDefault().logWarning("An unexpected error in a ECP plugin occured.", cause);
	}

}
