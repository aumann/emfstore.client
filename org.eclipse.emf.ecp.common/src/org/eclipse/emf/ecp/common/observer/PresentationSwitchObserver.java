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
package org.eclipse.emf.ecp.common.observer;

import org.eclipse.emf.emfstore.common.observer.IObserver;

/**
 * Observer for events when the presentation within a view is switched, e.g. to another tab.
 * 
 * @author Jonas
 */
public interface PresentationSwitchObserver extends IObserver {
	/**
	 * called if the presentation is switched.
	 * 
	 * @param viewID the ID of the focused view
	 * @param presentationID the idea of the activated presnetation
	 */
	void onPresentationSwitchEvent(String viewID, String presentationID);

}
