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
package com.cloudbees.eclipse.dtp.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;

import com.cloudbees.api.DatabaseInfo;

public class DatabaseInfoAdapterFactory implements IAdapterFactory {

  @Override
  public Object getAdapter(Object adaptableObject, Class adapterType) {

    if (!(adaptableObject instanceof DatabaseInfo)) {
      return null;
    }

    if (adapterType == IPropertySource.class) {
      return new DatabaseInfoPropertySource((DatabaseInfo) adaptableObject);
    }

    if (adapterType == IActionFilter.class) {
      return new DatabaseStatusActionFilter();
    }

    return null;
  }

  @Override
  public Class[] getAdapterList() {
    return new Class[] { IPropertySource.class, IActionFilter.class };
  }

}
