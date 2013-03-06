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
package com.cloudbees.eclipse.dtp;


public interface Images {

  String ICONS = "icons";
  String ICONS16 = ICONS + "/16x16";
  
  String JDBC_16_PATH = ICONS + "/jdbc_16.gif";
  String JDBC_16_ICON = Images.class.getSimpleName() + ".jdbc_16.gif";

  String CLOUDBEES_ICON_16x16_PATH = ICONS16 + "/cb_plain.png";
  String CLOUDBEES_ICON_16x16 = Images.class.getSimpleName() + ".cb_plain.png";

  String CLOUDBEES_FOLDER = Images.class.getSimpleName() + ".cb_folder_cb.png";
  String CLOUDBEES_FOLDER_PATH = ICONS16 + "/cb_folder_cb.png";

  String CLOUDBEES_REFRESH = Images.class.getSimpleName() + ".refresh.png";
  String CLOUDBEES_REFRESH_PATH = ICONS16 + "/refresh.png";
}