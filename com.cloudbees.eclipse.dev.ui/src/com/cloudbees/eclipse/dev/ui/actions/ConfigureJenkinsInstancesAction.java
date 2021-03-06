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
package com.cloudbees.eclipse.dev.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.cloudbees.eclipse.ui.views.CBTreeAction;

public class ConfigureJenkinsInstancesAction extends CBTreeAction implements IObjectActionDelegate {

  public ConfigureJenkinsInstancesAction() {
    super(false);
    setText("Configure on-premise Jenkins instances...");
    setToolTipText("Configure more Jenkins instances to monitor");

    /*    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
            .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
     */
  }

  @Override
  public void run() {
    PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(null,
        "com.cloudbees.eclipse.ui.preferences.JenkinsInstancesPreferencePage", new String[] {
            "com.cloudbees.eclipse.ui.preferences.JenkinsInstancesPreferencePage",
            "com.cloudbees.eclipse.ui.internal.preferences.GeneralPreferencePage" }, null);
    if (pref != null) {
      pref.open();
    }
  }

  @Override
  public boolean isPopup() {
    return false;
  }

  @Override
  public boolean isPullDown() {
    return true;
  }

  @Override
  public boolean isToolbar() {
    return false;
  }

  @Override
  public void run(IAction action) {
    run();
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

}
