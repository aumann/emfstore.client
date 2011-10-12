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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This is the standard Control to enum values.
 * 
 * @author shterev
 * @author Nikolay Kasyanov
 */
public class MEEnumControl extends AbstractMEControl {

	private EAttribute attribute;

	private ComboViewer combo;

	private static final int PRIORITY = 1;

	/**
	 * returns a Combo created by ComboViewer. {@inheritDoc}
	 * 
	 * @return Control
	 */
	@Override
	public Control createControl(Composite parent, int style) {
		Object feature = getItemPropertyDescriptor().getFeature(getModelElement());
		this.attribute = (EAttribute) feature;

		final IItemLabelProvider labelProvider = getItemPropertyDescriptor().getLabelProvider(getModelElement());

		combo = new ComboViewer(parent);
		combo.setContentProvider(new ArrayContentProvider());
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return labelProvider.getText(element);
			}

		});
		combo.setInput(attribute.getEType().getInstanceClass().getEnumConstants());

		EMFDataBindingContext dbc = new EMFDataBindingContext();
		IObservableValue model = EMFEditObservables.observeValue(getEditingDomain(), getModelElement(), attribute);
		IObservableValue comboObservable = ViewersObservables.observeSingleSelection(combo);
		dbc.bindValue(comboObservable, model);
		return combo.getControl();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.editor.mecontrols.AbstractMEControl#canRender(org.eclipse.emf.edit.provider.IItemPropertyDescriptor,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public int canRender(IItemPropertyDescriptor itemPropertyDescriptor, EObject modelElement) {
		Object feature = itemPropertyDescriptor.getFeature(modelElement);
		if (feature instanceof EAttribute
			&& (EEnum.class).isAssignableFrom(((EAttribute) feature).getEType().getClass())) {

			return PRIORITY;
		}
		return AbstractMEControl.DO_NOT_RENDER;
	}
}
