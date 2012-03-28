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
 * Standard widget to edit a double attribute.
 * 
 * @author helming
 * @author emueller
 */
public class MEDoubleControl extends MEPrimitiveAttributeControl<Double> implements IValidatableControl {
	
	@Override
	protected int getPriority() {
		return 1;
	}

	@Override
	protected Double convertStringToModel(String s) {
		return Double.parseDouble(s);
	}

	@Override
	protected boolean validateString(String s) {
		/*
		 * Do not perform any validation here, since a double can be represented with
		 * characters which include 'E', 'f' or 'd'.  Furthermore if values become to
		 * be, 'Infinity' is also a valid value.
		 */
		return true;
	}

	@Override
	protected String convertModelToString(Double t) {
		return Double.toString(t);
	}

	@Override
	protected void postValidate(String text) {
		try {
			setUnvalidatedString(Double.toString(Double.parseDouble(text)));
		} catch (NumberFormatException e) {
			setUnvalidatedString(Double.toString(getDefaultValue()));
		}
	}

	@Override
	protected Double getDefaultValue() {
		return 0.0;
	}
}
