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

package org.eclipse.emf.ecp.editor.mecontrols;

import org.eclipse.emf.common.util.Diagnostic;

/**
 * Controls which are implementing this interface handle the live validation result by themselves.
 * @author Haunolder
 *
 */
public interface IValidatableControl {

	/**
	 * Handle live validation.
	 * @param diagnostic of type Diagnostic
	 * **/
	void handleValidation(Diagnostic diagnostic);
	
	/**
	 * Reset the MEControl to validation status 'ok'.
	 * **/
	void resetValidation();
}
