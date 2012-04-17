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

/**
 * Standard widgets to edit a integer attribute.
 * 
 * @author helming
 * @author emueller
 */
public class MEIntControl extends MEPrimitiveAttributeControl<Integer> implements IValidatableControl {

	@Override
	protected int getPriority() {
		return 1;
	}

	@Override
	protected Integer convertStringToModel(String s) {
		return Integer.parseInt(s);
	}

	@Override
	protected boolean validateString(String s) {
		// TODO: perform validation
		return true;
	}

	@Override
	protected String convertModelToString(Integer t) {
		return Integer.toString(t);
	}
	
	@Override
	protected void postValidate(String text) {
		try {
			setUnvalidatedString(Integer.toString(Integer.parseInt(text)));
		} catch (NumberFormatException e) {
			setUnvalidatedString(Integer.toString(getDefaultValue()));
		}
	}

	@Override
	protected Integer getDefaultValue() {
		return 0;
	}
	
}
