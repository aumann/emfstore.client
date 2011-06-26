/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering, Technische Universitaet Muenchen. All rights
 * reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.ecp.validation;

import org.eclipse.emf.validation.model.IClientSelector;

/**
 * ClientSelector, necessary for validation.
 * 
 * @author wesendonk
 */
public class ValidationClientSelector implements IClientSelector {

	private static boolean running;

	/**
	 * {@inheritDoc}
	 */
	public boolean selects(Object object) {
		return running;
	}

	/**
	 * running ...
	 * 
	 * @param running ...
	 */
	public static void setRunning(boolean running) {
		ValidationClientSelector.running = running;
	}

}
