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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.common.commands.ECPCommand;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * The standard widget for multi line text fields.
 * 
 * @author helming
 */
public class MERichTextControl extends AbstractMEControl implements IValidatableControl{
	private EAttribute attribute;

	private AdapterImpl eAdapter;

	private static final int PRIORITY = 2;

	private Label labelWidgetImage;  //Label for diagnostic image

	private Composite composite;
	
	private ToolBar toolBar;

	private Text text;


	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.editor.mecontrols.AbstractMEControl#createControl(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	@Override
	public Control createControl(Composite parent, int style) {
		Object feature = getItemPropertyDescriptor().getFeature(getModelElement());
		this.attribute = (EAttribute) feature;
		composite = getToolkit().createComposite(parent, style);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);

		GridLayoutFactory.fillDefaults().numColumns(3).spacing(2, 0).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

		labelWidgetImage = getToolkit().createLabel(composite, "     ");
		labelWidgetImage.setBackground(composite.getBackground());

		createText();
		eAdapter = new AdapterImpl() {
			@Override
			public void notifyChanged(Notification msg) {
				if (msg.getFeature() != null && msg.getFeature().equals(MERichTextControl.this.attribute)) {
					load();
				}
				super.notifyChanged(msg);
			}
		};
		getModelElement().eAdapters().add(eAdapter);
		load();
		return composite;
	}

	private void createText() {
		
		text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);

		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		text.setSize(10, 100);
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				save();
				super.focusLost(e);
			}

		});
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		spec.heightHint = 200;
		text.setLayoutData(spec);
	}

	/**
	 * Returns the {@link ToolBar}.
	 * 
	 * @return the toolbar
	 */
	public ToolBar getToolbar() {
		return toolBar;
	}

	private void save() {
		new ECPCommand(getModelElement()) {
			@Override
			protected void doRun() {
				getModelElement().eSet(attribute, text.getText());
			}
		}.run(true);
	}

	private void load() {

		String txt = "";
		final StringBuffer value = new StringBuffer();
		new ECPCommand(getModelElement()) {
			@Override
			protected void doRun() {
				if (getModelElement().eGet(attribute) == null) {
					value.append("");
				} else {
					value.append(getModelElement().eGet(attribute));
				}
			}
		}.run(true);
		txt = value.toString();
		text.setText(txt);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		getModelElement().eAdapters().remove(eAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void applyCustomLayoutData() {
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).hint(250, 150).grab(true, false).applyTo(composite);
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
		if (feature instanceof EAttribute && ((EAttribute) feature).getEType().getInstanceClass().equals(String.class)) {

			if (itemPropertyDescriptor.isMultiLine(feature)) {
				return PRIORITY;
			}
		}
		return AbstractMEControl.DO_NOT_RENDER;
	}
	
	/**.
	 * {@inheritDoc}}
	 * */
	public void handleValidation(Diagnostic diagnostic) {
		if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.WARNING) {
			Image image = org.eclipse.emf.ecp.editor.Activator.getImageDescriptor("icons/validation_error.png").createImage();
			this.labelWidgetImage.setImage(image);
			this.labelWidgetImage.setToolTipText(diagnostic.getMessage());
		}
	}
	
	/**.
	 * {@inheritDoc}}
	 * */
	public void resetValidation() {
		this.labelWidgetImage.setImage(null);
		this.labelWidgetImage.setToolTipText("");
	}
}
