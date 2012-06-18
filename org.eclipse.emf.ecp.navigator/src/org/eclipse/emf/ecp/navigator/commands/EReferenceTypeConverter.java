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
package org.eclipse.emf.ecp.navigator.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

/**
 * Converts a reference to a string.
 * @author Jonas Helming
 *
 */
public class EReferenceTypeConverter extends AbstractParameterValueConverter {



	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToObject(java.lang.String)
	 */
	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
		return parameterValue;

	}


	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object parameterValue) throws ParameterValueConversionException {
		// We need and string representation of an EClass that can be
		// turned back to an EClass object.
		// It was tested with serialization method MRIUtil but
		// it has the problem that referenced of this EClass instance are not
		// serialized and we needed them.

		return (String) parameterValue;
	}

}
