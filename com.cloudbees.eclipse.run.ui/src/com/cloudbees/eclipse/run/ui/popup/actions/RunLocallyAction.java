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
package com.cloudbees.eclipse.run.ui.popup.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;

import com.cloudbees.eclipse.run.ui.CBRunUiActivator;
import com.cloudbees.eclipse.run.ui.launchconfiguration.CBLocalLaunchShortcut;

@SuppressWarnings("restriction")
public class RunLocallyAction implements IObjectActionDelegate {

  @Override
  public void run(final IAction action) {
    Job job = new Job("Running locally...") {
      @Override
      protected IStatus run(final IProgressMonitor monitor) {
        if (action instanceof ObjectPluginAction) {
          CBLocalLaunchShortcut shortcut = new CBLocalLaunchShortcut();
          ISelection selection = ((ObjectPluginAction) action).getSelection();
          shortcut.launch(selection, "run");
        }

        return new Status(IStatus.OK, CBRunUiActivator.PLUGIN_ID, "Launch complete");
      }
    };

    job.setUser(true);
    job.schedule();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

}
