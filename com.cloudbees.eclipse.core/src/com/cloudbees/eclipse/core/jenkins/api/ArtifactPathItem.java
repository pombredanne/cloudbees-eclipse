/*******************************************************************************
 * Copyright (c) 2013 Cloud Bees, Inc.
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Cloud Bees, Inc. - initial API and implementation 
 *******************************************************************************/
package com.cloudbees.eclipse.core.jenkins.api;

public class ArtifactPathItem {
  public JenkinsBuildDetailsResponse parent;
  public JenkinsBuildDetailsResponse.Artifact item;

  public ArtifactPathItem(final JenkinsBuildDetailsResponse parent, final JenkinsBuildDetailsResponse.Artifact item) {
    this.parent = parent;
    this.item = item;
  }
}
