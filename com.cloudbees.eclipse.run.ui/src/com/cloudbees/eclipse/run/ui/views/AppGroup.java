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
package com.cloudbees.eclipse.run.ui.views;

import com.cloudbees.eclipse.ui.views.ICBGroup;

public class AppGroup implements ICBGroup {

  String name;

  public AppGroup(final String name) {
    this.name = name;
  }

  @Override
  public int getOrder() {
    return 4;
  }

}
