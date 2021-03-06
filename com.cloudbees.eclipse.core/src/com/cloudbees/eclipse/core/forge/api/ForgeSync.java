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
package com.cloudbees.eclipse.core.forge.api;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cloudbees.eclipse.core.CloudBeesException;
import com.cloudbees.eclipse.core.jenkins.api.ChangeSetPathItem;
import com.cloudbees.eclipse.core.jenkins.api.JenkinsScmConfig;

public interface ForgeSync {

  void updateStatus(ForgeInstance instance, IProgressMonitor subProgressMonitor) throws CloudBeesException;

  void sync(ForgeInstance instance, IProgressMonitor monitor) throws CloudBeesException;

  boolean openRemoteFile(JenkinsScmConfig scmConfig, ChangeSetPathItem item, IProgressMonitor monitor);

  void addToRepository(ForgeInstance instance, IProject project, IProgressMonitor monitor) throws CloudBeesException;

  boolean isUnderSvnScm(IProject project);

  ForgeInstance getMainRepo(IProject project);

}
