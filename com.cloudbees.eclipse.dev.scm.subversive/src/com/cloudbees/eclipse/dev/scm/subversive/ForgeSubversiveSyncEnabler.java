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
package com.cloudbees.eclipse.dev.scm.subversive;

import com.cloudbees.eclipse.core.ForgeSyncService;
import com.cloudbees.eclipse.core.forge.api.ForgeSyncEnabler;

public class ForgeSubversiveSyncEnabler implements ForgeSyncEnabler {

  @Override
  public boolean isEnabled() {
    //boolean connectorEnabled = ForgeSyncService.bundleActive("org.polarion.eclipse.team.svn.connector");
    //boolean svnEnabled = ForgeSyncService.bundleActive("org.eclipse.team.svn.core");
    
    // We are not doing the automatic update site addition because adding just the update site won't help as the connector discovery algorithm expects that the connectors feature is already installed.
    // Installing automatically connector at this stage would be too heavy.
    // Instead, we'll dependency for the org.eclipse.team.svn.connector so integration can be installed only when it's installed
    return ForgeSyncService.bundleActive("org.eclipse.team.svn.core");

/*    if (svnEnabled && connectorEnabled) {
      return true;
    }
    if (svnEnabled && !connectorEnabled) {
      // connector not found, add polarion update site
      // "broken" helios build that doesn't include the subversive connectors
      String url="http://community.polarion.com/projects/subversive/download/eclipse/2.0/helios-site/";
      URI updateURI = URI.create(url);
      ProvisioningUI ui = ProvisioningUI.getDefaultUI();
      RepositoryTracker tracker = ui.getRepositoryTracker();
      
      //This validate also checks for duplicates
      if (tracker.validateRepositoryLocation(ui.getSession(), updateURI, false, new NullProgressMonitor()).isOK()) {
        tracker.addRepository(updateURI, "Polarion SVN Connectors", ui.getSession());
        tracker.refreshRepositories(new URI[]{updateURI}, ui.getSession(), new NullProgressMonitor());
      }

      return true; // enabled for now, hopefully connector selection dialog succeeds
      
    }
      
    return false;
*/  }

  @Override
  public String getName() {
    return "Subversive";
  }

}
