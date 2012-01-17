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
package org.eclipse.emf.ecp.validation.providers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider for the Validation View.
 * 
 * @author Carmen Carlan
 * 
 */
public class ValidationLabelProvider extends ColumnLabelProvider {
	private AdapterFactoryLabelProvider adapterFactoryLabelProvider;
	private ComposedAdapterFactory adapterFactory;

	/**
	 * Default constructor.
	 */
	public ValidationLabelProvider() {
		super();
		adapterFactory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object object) {
		if (object instanceof IStatus) {
			BasicDiagnostic inputElement = (BasicDiagnostic) BasicDiagnostic.toDiagnostic((IStatus) object);
			EObject target = (EObject) inputElement.getData().get(0);
			if (target instanceof EObject) {
				return adapterFactoryLabelProvider.getImage(target);
			}
		}
		return super.getImage(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object object) {
		if (object instanceof IStatus) {
			BasicDiagnostic inputElement = (BasicDiagnostic) BasicDiagnostic.toDiagnostic((IStatus) object);
			EObject target = (EObject) inputElement.getData().get(0);
			if (target instanceof EObject) {
				return adapterFactoryLabelProvider.getText(target);
			}
		}
		return super.getText(object);
	}

	@Override
	public void dispose(ColumnViewer viewer, ViewerColumn column) {
		if (adapterFactory!=null) {
			adapterFactory.dispose();
		}
		super.dispose(viewer, column);
	}
	

}
