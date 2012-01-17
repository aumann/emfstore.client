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
package org.eclipse.emf.ecp.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.common.TableViewerColumnSorter;
import org.eclipse.emf.ecp.common.commands.ECPCommand;
import org.eclipse.emf.ecp.common.model.ECPWorkspaceManager;
import org.eclipse.emf.ecp.common.model.NoWorkspaceException;
import org.eclipse.emf.ecp.common.model.workSpaceModel.ECPProject;
import org.eclipse.emf.ecp.common.model.workSpaceModel.ECPWorkspace;
import org.eclipse.emf.ecp.common.model.workSpaceModel.WorkSpaceModelPackage;
import org.eclipse.emf.ecp.common.observer.FocusEventObserver;
import org.eclipse.emf.ecp.common.utilities.ActionHelper;
import org.eclipse.emf.ecp.validation.filter.FilterTableViewer;
import org.eclipse.emf.ecp.validation.filter.ValidationFilter;
import org.eclipse.emf.ecp.validation.providers.ConstraintLabelProvider;
import org.eclipse.emf.ecp.validation.providers.SeverityLabelProvider;
import org.eclipse.emf.ecp.validation.providers.ValidationContentProvider;
import org.eclipse.emf.ecp.validation.providers.ValidationFilterLabelProvider;
import org.eclipse.emf.ecp.validation.providers.ValidationLabelProvider;
import org.eclipse.emf.ecp.validation.refactoring.strategy.RefactoringResult;
import org.eclipse.emf.ecp.validation.refactoring.strategy.RefactoringStrategy;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.ViewPart;

/**
 * The Validation View.
 * 
 * @author Carmen Carlan
 * 
 */
public class ValidationView extends ViewPart {

	private TableViewer tableViewer;

	private DialogSettings settings;

	private String filename;

	private final String viewId = "org.eclipse.emf.ecp.validation.customValidationView";

	private AdapterImpl workspaceListenerAdapter;

	private Shell shell;

	private Table table;

	private ArrayList<ValidationFilter> validationFilters;

	private TableItem tableItem;

	private ECPWorkspace workspace;
	
	private ValidationLabelProvider labelProvider;

	/**
	 * Default constructor.
	 */
	public ValidationView() {
		IPath path = org.eclipse.emf.ecp.common.Activator.getDefault().getStateLocation();
		filename = path.append("settings.txt").toOSString();
		settings = new DialogSettings("Top");
		try {
			settings.load(filename);
		} catch (IOException e) {
			// Do nothing.
		}

		try {
			workspace = ECPWorkspaceManager.getInstance().getWorkSpace();
		} catch (NoWorkspaceException e) {
			Activator.getDefault().logException(e.getMessage(), e);
			return;
		}
		workspaceListenerAdapter = new AdapterImpl() {

			@Override
			public void notifyChanged(Notification msg) {
				if ((msg.getFeatureID(ECPWorkspace.class)) == WorkSpaceModelPackage.ECP_WORKSPACE__PROJECTS) {
					if (msg.getOldValue() != null
						&& (msg.getOldValue() instanceof List<?> || msg.getOldValue() instanceof ECPProject)) {
						tableViewer.setInput(new ArrayList<IStatus>());
					}

				}
				super.notifyChanged(msg);
			}
		};
		workspace.eAdapters().add(workspaceListenerAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		tableViewer = new FilterTableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
			| SWT.FULL_SELECTION);
		createTable();
		this.shell = parent.getShell();
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager menuManager = bars.getToolBarManager();
		OpenFilterDialogAction openFilterDialogAction = new OpenFilterDialogAction();
		openFilterDialogAction.setImageDescriptor(Activator.getImageDescriptor("icons/openfilterlist.png"));
		openFilterDialogAction.setToolTipText("Add one or more filters to be applied to the validation view.");
		menuManager.add(openFilterDialogAction);
		hookDoubleClickAction();
		tableViewer.getTable().addMenuDetectListener(new MenuDetectListenerImplementation());
	}

	/**
	 * Creates the table which will be displayed in the view.
	 */
	private void createTable() {
		// CREATE TABLE
		table = tableViewer.getTable();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 5;
		table.setLayoutData(gridData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableViewerColumn column;

		// severity column
		column = new TableViewerColumn(tableViewer, SWT.CENTER, 0);
		column.getColumn().setText("Severity");
		column.getColumn().setWidth(50);
		setLabelProviderAndComparator(column, new SeverityLabelProvider());

		// constraint column
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 1);
		column.getColumn().setText("Constraint");
		column.getColumn().setWidth(200);
		setLabelProviderAndComparator(column, new ConstraintLabelProvider());

		// affected model element column
		column = new TableViewerColumn(tableViewer, SWT.LEFT, 2);
		column.getColumn().setText("Affected ModelElement");
		column.getColumn().setWidth(200);
		labelProvider =  new ValidationLabelProvider();
		setLabelProviderAndComparator(column,labelProvider);

		// content provider
		tableViewer.setContentProvider(new ValidationContentProvider());
		TableCursor tableCursor = new TableCursor(table, SWT.NONE);
		tableCursor.setVisible(false);
		table.addKeyListener(new KeyListener() {

			public void keyReleased(KeyEvent e) {
				if (e.stateMask == SWT.ALT && e.keyCode == 'r') {
					tableItem = ((Table) e.getSource()).getSelection()[0];
					startRefactoring();
				}
			}

			public void keyPressed(KeyEvent e) {
				// nothing to do here
			}
		});
	}

	/**
	 * Sets the LabelProvider and Comparator for a specific column
	 * 
	 * @param column
	 * @param labelProvider
	 */
	private void setLabelProviderAndComparator(TableViewerColumn column, ColumnLabelProvider labelProvider) {
		column.setLabelProvider(labelProvider);
		column.getViewer().setComparator(new TableViewerColumnSorter(tableViewer, column, labelProvider));
	}

	/**
	 * Attaches a double-click action to the view.
	 */
	private void hookDoubleClickAction() {
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IStatus constraintStatus = (IStatus) selection.getFirstElement();
				BasicDiagnostic inputElement = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(constraintStatus);
				EObject me = (EObject) inputElement.getData().get(0);
				Iterator<Diagnostic> iterator = inputElement.getChildren().iterator();
				if (me instanceof EObject) {
					EStructuralFeature errorLocation = null;
					errorLocation = getErrorLocation(iterator, errorLocation);
					if (errorLocation != null) {
						ActionHelper.openModelElement(me, errorLocation, viewId, workspace.getProject(me));
					} else {
						ActionHelper.openModelElement(me, viewId);
					}
				}
			}

		});
	}

	/**
	 * Gets the refactoring strategies defined for the plug-in.
	 * 
	 * @param status
	 * @return
	 */
	private ArrayList<RefactoringStrategy> getRefactoringStrategiesFromExtensionPoint(IStatus status) {
		ArrayList<RefactoringStrategy> refactoringStrategies = new ArrayList<RefactoringStrategy>();
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
			"org.eclipse.emf.ecp.bulidInValidation.refactoring.strategies");
		for (IConfigurationElement element : config) {
			try {
				if (element.getAttribute("applicableFor").equals(status.getCode())) {
					final Object object = element.createExecutableExtension("strategy");
					RefactoringStrategy strategy = (RefactoringStrategy) object;
					strategy.setConstraintStatus(status);
					refactoringStrategies.add(strategy);
				}
			} catch (CoreException e) {
				Activator.getDefault().logWarning("Exception loading refactoring strategies from the extension point",
					e);
			}

		}
		return refactoringStrategies;
	}

	/**
	 * Gets the location of the EObject, to whom the specific error belongs.
	 * 
	 * @param iterator
	 * @param errorLocation
	 * @return
	 */
	private EStructuralFeature getErrorLocation(Iterator<Diagnostic> iterator, EStructuralFeature errorLocation) {
		while (iterator.hasNext()) {
			Diagnostic nextDiagnostic = iterator.next();
			EObject next = (EObject) nextDiagnostic.getData().get(0);
			if (next instanceof EStructuralFeature) {
				errorLocation = (EStructuralFeature) next;
				break;
			}
		}
		return errorLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		ECPWorkspaceManager.getObserverBus().notify(FocusEventObserver.class).onFocusEvent(viewId);
	}

	/**
	 * Updates the validation view table for the new live validation.
	 * 
	 * @param validationResults
	 *            validation results.
	 */
	public void updateTable(Diagnostic diagnostic) {
		tableViewer.setInput(diagnostic);
		// this is added to fix the bug regarding context menu not being shown
		// correctly in navigator, after validation viewer was shown.
		tableViewer.getTable().setFocus();

	}

	/**
	 * Gets the filters we defined for the plug-in.
	 * 
	 * @return
	 */
	private ArrayList<ValidationFilter> getFiltersFromExtensionPoint() {
		if (validationFilters == null) {
			validationFilters = new ArrayList<ValidationFilter>();
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"org.eclipse.emf.ecp.validation.filters");
			for (IConfigurationElement element : config) {
				try {
					Object object = element.createExecutableExtension("filter");
					if (object instanceof ValidationFilter) {
						ValidationFilter validationFilter = (ValidationFilter) object;
						if (validationFilter.init()) {
							validationFilters.add(validationFilter);
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return validationFilters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		workspace.eAdapters().remove(workspaceListenerAdapter);
		if (labelProvider!=null) {
			labelProvider.dispose();
		}
		super.dispose();
	}

	/**
	 * Starts refactoring.
	 */
	private void startRefactoring() {
		IStatus constraintStatus = (IStatus) tableItem.getData();
		List<?> abstractRefactoringStrategies = getRefactoringStrategiesFromExtensionPoint(constraintStatus);
		if (abstractRefactoringStrategies.isEmpty()) {
			return;
		}
		RefactoringResult refactoringResult = RefactoringResult.ABORT;
		if (abstractRefactoringStrategies.size() == 1) {
			RefactoringStrategy refactoringStrategy = (RefactoringStrategy) abstractRefactoringStrategies.get(0);
			refactoringStrategy.setShell(shell);
			refactoringResult = refactoringStrategy.startRefactoring();
		} else {
			// otherwise show list dialog
			ListDialog listDialog = new ListDialog(shell);
			listDialog.setInput(abstractRefactoringStrategies);
			listDialog.setLabelProvider(new RefactoringStrategyLabelProvider());
			listDialog.setContentProvider(new SimpleContentProvider());
			listDialog.setTitle("Choose a refactoring strategy");
			listDialog.open();
			Object[] result = listDialog.getResult();
			if (result != null && result.length > 0) {
				RefactoringStrategy refactoringStrategy = (RefactoringStrategy) result[0];
				refactoringStrategy.setShell(shell);
				refactoringResult = refactoringStrategy.startRefactoring();
			}
		}
		if (refactoringResult == RefactoringResult.NO_VIOLATION
			|| refactoringResult == RefactoringResult.SUCCESS_CREATE) {
			tableItem.dispose();
		}
	}

	/**
	 * Empty the table from the view.
	 * 
	 * @param status
	 */
	private void removeAllTableItemsForEObject(final IStatus status) {
		BasicDiagnostic inputElement1 = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(status);
		EObject deletee = (EObject) inputElement1.getData().get(0);
		for (TableItem tableItem : tableViewer.getTable().getItems()) {
			IStatus constraintStatus = (IStatus) tableItem.getData();
			BasicDiagnostic inputElement2 = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(constraintStatus);
			EObject modelElement = (EObject) inputElement2.getData().get(0);
			if (deletee == modelElement) {
				tableItem.dispose();
			}
		}
	}

	private final class MenuDetectListenerImplementation implements MenuDetectListener {

		public void menuDetected(MenuDetectEvent e) {
			// get the table
			Table table = (Table) e.getSource();
			if (table.getSelection() == null || table.getSelection().length == 0) {
				return;
			}
			// get the first table item that was selected (no multiple select)
			tableItem = table.getSelection()[0];
			// extract the violation status
			final IStatus status = (IStatus) tableItem.getData();
			// create the menu
			Menu leftClickMenu = new Menu(shell, SWT.POP_UP);
			// add refactoring menu item if refactoring strategies are available
			List<RefactoringStrategy> refactoringStrategies = getRefactoringStrategiesFromExtensionPoint(status);
			if (refactoringStrategies.size() != 0) {
				final MenuItem refactorMenuItem = new MenuItem(leftClickMenu, SWT.NONE);
				// add the menu item
				refactorMenuItem.setData(tableItem);
				refactorMenuItem.setText("Perform refactoring");
				refactorMenuItem.setImage(Activator.getImageDescriptor("icons/bell.png").createImage());
				// refactorMenuItem.setData(data)
				refactorMenuItem.addSelectionListener(new RefactoringSelectionListener());
			}
			// // ignore constraint menu item
			// MenuItem ignoreMenuItem = new MenuItem(leftClickMenu, SWT.NONE);
			// ignoreMenuItem.setData(refactoringStrategies);
			// ignoreMenuItem.setText("Ignore violation");
			// ignoreMenuItem.setImage(Activator.getImageDescriptor(
			// "icons/bell_delete.png").createImage());
			// delete model element menu item
			MenuItem deleteMenuItem = new MenuItem(leftClickMenu, SWT.NONE);
			deleteMenuItem.setData(refactoringStrategies);
			deleteMenuItem.setText("Delete underlying element");
			deleteMenuItem.setImage(Activator.getImageDescriptor("icons/delete.png").createImage());
			deleteMenuItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					BasicDiagnostic inputElement = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(status);
					EObject target = (EObject) inputElement.getData().get(0);
					if (MessageDialog.openQuestion(shell, "Confirm deletion", "Do you really wish to delete "
						+ target.getClass().getSimpleName() + "?")) {
						new ECPCommand(target) {

							@Override
							protected void doRun() {
								BasicDiagnostic inputElement = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(status);
								EObject target = (EObject) inputElement.getData().get(0);
								EcoreUtil.delete(target);
							}
						}.run(false);
					}
					removeAllTableItemsForEObject(status);
				}
			});
			// set menu to visible
			leftClickMenu.setVisible(true);
		}

		private final class RefactoringSelectionListener implements SelectionListener {
			public void widgetSelected(SelectionEvent e) {
				// only show selection dialog if there is more than one
				// refactoring
				startRefactoring();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here
			}
		}
	}

	/**
	 * The filter dialog action.
	 * 
	 */
	private final class OpenFilterDialogAction extends Action {

		@Override
		public void run() {
			ValidationFilterList validationFilterList = new ValidationFilterList(shell, getFiltersFromExtensionPoint(),
				new SimpleContentProvider(), new ValidationFilterLabelProvider(), "Test");
			validationFilterList.setTitle("Choose one or more filters");
			validationFilterList.setInitialSelections(tableViewer.getFilters());
			validationFilterList.open();
			if (validationFilterList.getReturnCode() == IStatus.OK) {
				removeAllFilters();
				for (Object object : validationFilterList.getResult()) {
					if (object instanceof ValidationFilter) {
						ValidationFilter validationFilter = (ValidationFilter) object;
						applyFilter(validationFilter);
					}
				}
			}
		}

		private void applyFilter(ValidationFilter validationFilter) {
			tableViewer.addFilter(validationFilter);
		}

		private void removeAllFilters() {
			tableViewer.resetFilters();
		}
	}

	private final class RefactoringStrategyLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			return Activator.getImageDescriptor("icons/bell.png").createImage();
		}

		@Override
		public String getText(Object element) {
			return ((RefactoringStrategy) ((Object[]) element)[0]).getDescription();
		}
	}

	private final class SimpleContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			List<?> list = (List<?>) inputElement;
			if (list.isEmpty()) {
				return new Object[0];
			}
			return list.toArray();
		}

		public void dispose() {
			// TODO Auto-generated method stub
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
		}
	}

}
