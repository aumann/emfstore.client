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
package org.eclipse.emf.ecp.common.model;

import org.eclipse.emf.ecp.common.model.workSpaceModel.ECPWorkspace;

/**
 * Listener for ECP workspace init.
 * @author Jonas Helming
 *
 */
public interface PostECPWorkspaceInitiator {

	/**
	 * Called to notify about workspace init completion. 
	 * @param currentWorkspace the current workspace
	 */
	void workspaceInitComplete(ECPWorkspace currentWorkspace);

}
