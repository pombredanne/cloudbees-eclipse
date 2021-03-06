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

import java.lang.reflect.Field;

import com.google.gson.annotations.Expose;

public class QTreeFactory {

  public static String create(final Class<?> cls) {
    return buildTreeQuery(cls.getPackage().getName(), cls, new StringBuffer()).toString();

  }

  private final static StringBuffer buildTreeQuery(final String pkg, final Class<?> baseClass, final StringBuffer sb) {


    Field[] fields = baseClass.getFields();

    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];

      Expose exposed = field.getAnnotation(Expose.class);
      if (exposed != null) {
        if (!(exposed.deserialize() && exposed.serialize())) {
          continue;
        }
      }

      sb.append(field.getName());

      Class<?> cl = field.getType();

      if (cl.getPackage() != null && pkg.equals(cl.getPackage().getName())) {
        sb.append("[");
        buildTreeQuery(pkg, cl, sb);
        sb.append("]");
      } else if (cl.isArray()) {
        sb.append("[");
        buildTreeQuery(pkg, cl.getComponentType(), sb);
        sb.append("]");
      }

      if (i < fields.length) {
        sb.append(",");
      }

    }
    if (sb.charAt(sb.length() - 1) == ',') {
      // trailing comma, remove. due to not-serialized fields it's not too trivial to detect the condition beforehand. so let's just remove it here.
      sb.replace(sb.length() - 1, sb.length(), "");
    }

    return sb;
  }

}
