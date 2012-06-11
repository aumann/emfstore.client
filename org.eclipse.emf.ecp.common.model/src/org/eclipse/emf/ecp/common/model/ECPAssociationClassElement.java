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

import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents an association class.
 * @author Maximilian Koegel
 *
 */
public class ECPAssociationClassElement {
	private EReference targetFeature;
	private EReference sourceFeature;
	private List<EStructuralFeature> associationFeatures;

	/**
	 * Constructor.
	 * @param sourceFeature the feature pointing to the source element
	 * @param targetFeature the feature pointing to the target element
	 * @param associationFeatures the other features
	 */
	public ECPAssociationClassElement(EReference sourceFeature, EReference targetFeature,
		List<EStructuralFeature> associationFeatures) {
		this.sourceFeature = sourceFeature;
		this.targetFeature = targetFeature;
		this.associationFeatures = associationFeatures;
	}

	/**
	 * Return the feature in which the source element of this link is contained.
	 * 
	 * @return the source feature
	 */
	public EReference getSourceFeature() {
		return sourceFeature;
	}

	/**
	 * Return the feature in which the target element of this link is contained.
	 * 
	 * @return the source feature
	 */
	public EReference getTargetFeature() {
		return targetFeature;
	}

	/**
	 * Return the features which constitute the features of this link that contain information about the link, other
	 * than source and target. The list should be ordered by priority of the feature, important features first.
	 * 
	 * @return a list of features
	 */
	public List<EStructuralFeature> getAssociationFeatures() {
		return associationFeatures;
	}

}
