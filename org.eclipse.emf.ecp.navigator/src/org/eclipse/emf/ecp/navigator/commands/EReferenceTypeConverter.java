package org.eclipse.emf.ecp.navigator.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

public class EReferenceTypeConverter extends AbstractParameterValueConverter {

	public EReferenceTypeConverter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * . {@inheritDoc} This creates the EClass object back from its string representation.
	 */
	@Override
	public Object convertToObject(String parameterValue) throws ParameterValueConversionException {

		

		return parameterValue;

	}

	/**
	 * . ({@inheritDoc}) This creates a string representation of EClass object to put it in command parameters map.
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
