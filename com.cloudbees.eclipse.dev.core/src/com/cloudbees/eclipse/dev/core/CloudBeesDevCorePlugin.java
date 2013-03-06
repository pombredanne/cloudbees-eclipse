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
package com.cloudbees.eclipse.dev.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.cloudbees.eclipse.core.CloudBeesCorePlugin;
import com.cloudbees.eclipse.core.CloudBeesException;
import com.cloudbees.eclipse.core.Logger;
import com.cloudbees.eclipse.core.forge.api.ForgeSync;
import com.cloudbees.eclipse.core.forge.api.ForgeSyncEnabler;

public class CloudBeesDevCorePlugin extends Plugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "com.cloudbees.eclipse.dev.core"; //$NON-NLS-1$

  private static CloudBeesDevCorePlugin plugin;

  private Logger logger;

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception {
    super.start(bundleContext);
    CloudBeesDevCorePlugin.plugin = this;
    this.logger = new Logger(getLog());
    registerForgeSyncProviders();
  }

  /*
   * (non-Javadoc)
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception {
    CloudBeesDevCorePlugin.plugin = null;
    this.logger = null;
    super.stop(bundleContext);
  }

  private void registerForgeSyncProviders() {
    IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, "forgeSyncProvider")
        .getExtensions();

    for (IExtension extension : extensions) {
      for (IConfigurationElement element : extension.getConfigurationElements()) {
        try {
          Object enabler = element.createExecutableExtension("enabler");
          if (enabler == null || !(enabler instanceof ForgeSyncEnabler) || !((ForgeSyncEnabler) enabler).isEnabled()) {
            getLogger().info("skipping sync provider: " + enabler + " because of dependencies");
            continue;
          }

          Object provider = element.createExecutableExtension("class");
          if (provider instanceof ForgeSync) {
            ForgeSync provider1 = ((ForgeSync) provider);
            CloudBeesCorePlugin.getDefault().getGrandCentralService().getForgeSyncService().addProvider(provider1);
            //System.out.println("adding: " + provider1);
          }
        } catch (CloudBeesException e) {
          logger.error(e.getMessage(), e);
        } catch (CoreException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }
  }

  /**
   * @return the shared instance
   */
  public static CloudBeesDevCorePlugin getDefault() {
    return plugin;
  }

  public Logger getLogger() {
    return this.logger;
  }

}
