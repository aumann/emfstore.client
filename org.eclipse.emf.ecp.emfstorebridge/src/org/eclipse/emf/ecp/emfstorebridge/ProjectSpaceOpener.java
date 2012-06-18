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
package org.eclipse.emf.ecp.emfstorebridge;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.common.util.ModelElementOpener;
import org.eclipse.emf.emfstore.client.model.ProjectSpace;

/**
 * Opener for Project Space.
 * @author Maximilian Koegel
 *
 */
public class ProjectSpaceOpener implements ModelElementOpener {

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.emf.ecp.common.util.ModelElementOpener#canOpen(org.eclipse.emf.ecore.EObject)
	 */
	public int canOpen(EObject modelElement) {
		if (modelElement instanceof ProjectSpace) {
			return 1;
		}
		return DONOTOPEN;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.emf.ecp.common.util.ModelElementOpener#openModelElement(org.eclipse.emf.ecore.EObject)
	 */
	public void openModelElement(EObject modelElement) {
		// do nothing
	}

}
