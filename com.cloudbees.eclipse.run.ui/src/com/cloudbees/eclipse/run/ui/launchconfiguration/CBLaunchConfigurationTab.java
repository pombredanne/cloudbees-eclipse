/*******************************************************************************
 * Copyright (c) 2013 Cloud Bees, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.cloudbees.eclipse.run.ui.launchconfiguration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.cloudbees.eclipse.run.core.launchconfiguration.CBLaunchConfigurationConstants;
import com.cloudbees.eclipse.run.core.util.CBRunUtil;
import com.cloudbees.eclipse.run.ui.CBRunUiActivator;
import com.cloudbees.eclipse.run.ui.Images;

public class CBLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

  private static final String TAB_NAME = "CloudBees Application";

  protected ProjectSelectionComposite projectSelector;
  protected Composite main;

  private PortSelectionComposite portSelectionComposite;

  /**
   * @wbp.parser.entryPoint
   */
  @Override
  public void createControl(Composite parent) {
    this.main = new Composite(parent, SWT.NONE);
    this.main.setLayout(new GridLayout(2, false));

    this.projectSelector = new ProjectSelectionComposite(this.main, SWT.None) {
      @Override
      public void handleUpdate() {
        validateConfigurationTab();
      }
    };
    this.projectSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    this.portSelectionComposite = new PortSelectionComposite(this.main, SWT.None) {

      @Override
      public void handleChange() {
        try {
          int port = Integer.parseInt(getPort());
          if (port < 1) {
            setErrorMessage("Port must be bigger than 0");
          } else if (port > 65535) {
            setErrorMessage("Port must be smaller than 35535");
          } else {
            setErrorMessage(null);
          }
        } catch (NumberFormatException e) {
          setErrorMessage("Port number must be an integer!");
        }
        updateLaunchConfigurationDialog();
      }
    };
    this.portSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    setControl(this.main);
  }

  @Override
  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
  }

  @Override
  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      String projectName = configuration
          .getAttribute(CBLaunchConfigurationConstants.ATTR_CB_PROJECT_NAME, new String());
      if (projectName == null || projectName.length() == 0) {
        projectName = this.projectSelector.getDefaultSelection();
      }
      this.projectSelector.setText(projectName);

      String port = configuration.getAttribute(CBLaunchConfigurationConstants.ATTR_CB_PORT, new String());
      this.portSelectionComposite.setPort(port);
    } catch (CoreException e) {
      CBRunUiActivator.logError(e);
    }
    scheduleUpdateJob();
  }

  @Override
  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    String projectName = this.projectSelector.getText();
    try {
      CBRunUtil.addDefaultAttributes(configuration, projectName, this.portSelectionComposite.getPort());
    } catch (CoreException e) {
      CBRunUiActivator.logError(e);
    }
  }

  protected boolean validateConfigurationTab() {
    IStatus projectStatus = this.projectSelector.validate();
    if (!projectStatus.isOK()) {
      setErrorMessage(projectStatus.getMessage());
      updateLaunchConfigurationDialog();
      return false;
    }

    setErrorMessage(null);
    setMessage("Run CloudBees application");
    updateLaunchConfigurationDialog();
    return true;
  }

  @Override
  public String getName() {
    return TAB_NAME;
  }

  @Override
  public Image getImage() {
    return CBRunUiActivator.getDefault().getImageRegistry().get(Images.CLOUDBEES_ICON_16x16);
  }

  @Override
  public boolean isValid(ILaunchConfiguration launchConfig) {
    return this.projectSelector.validate().getSeverity() == IStatus.OK && getErrorMessage() == null;
  }

}
