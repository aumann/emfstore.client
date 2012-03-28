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
 * Standard widgets to edit a single line text attribute.
 * 
 * @author helming
 * @author emueller
 */
public class METextControl extends MEPrimitiveAttributeControl<String> implements IValidatableControl {

	@Override
	protected int getPriority() {
		return 1;
	}

	@Override
	protected String convertStringToModel(String s) {
		return s;
	}

	@Override
	protected boolean validateString(String s) {
		return true;
	}

	@Override
	protected String convertModelToString(String t) {
		return t;
	}

	@Override
	protected void postValidate(String text) {
		// do nothing
	}

	@Override
	protected String getDefaultValue() {
		return "";
	}

}
