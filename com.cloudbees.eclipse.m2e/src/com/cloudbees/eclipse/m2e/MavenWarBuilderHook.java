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
package com.cloudbees.eclipse.m2e;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ResolverConfiguration;

import com.cloudbees.eclipse.core.CloudBeesException;
import com.cloudbees.eclipse.core.CloudBeesNature;
import com.cloudbees.eclipse.core.NatureUtil;
import com.cloudbees.eclipse.run.core.WarBuilderHook;

public class MavenWarBuilderHook implements WarBuilderHook {

  public MavenWarBuilderHook() {
  }

  public IFile buildProject(IProject project, IProgressMonitor monitor) throws CloudBeesException {

    boolean pomExists = project.exists(new Path("/pom.xml"));
    if (!pomExists) {
      return null;
    }

    IMaven maven = MavenPlugin.getMaven();

    String version = MavenPlugin.getMavenRuntimeManager().getDefaultRuntime().getVersion();

    MavenExecutionRequest request;

    IMavenProjectFacade mpr = MavenPlugin.getMavenProjectRegistry().getProject(project);
    try {

      if (mpr == null) {
        // maven not configured for the project. But as pom.xml existed, let's configure.
        NatureUtil.addNatures(project, new String[] { CloudBeesNature.NATURE_ID, JavaCore.NATURE_ID,
            "org.eclipse.m2e.core.maven2Nature" }, monitor);
        project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
        mpr = MavenPlugin.getMavenProjectRegistry().getProject(project);
      }

      request = maven.createExecutionRequest(monitor);
    } catch (CoreException e) {
      throw new CloudBeesException("Failed to prepare maven war preparation request", e);
    }

    //IMavenProjectFacade facade = projectManager.create(pomFile, true, new SubProgressMonitor(monitor, 1));
    ResolverConfiguration config = mpr.getResolverConfiguration();

    request.setPom(project.getFile(new Path("/pom.xml")).getRawLocation().toFile());
    //request.setBaseDirectory(project.getRawLocation().toFile());

    request.getSystemProperties().put("maven.test.skip", "true");

    //MavenExecutionRequest request = projectManager.createExecutionRequest(pomFile, config, new SubProgressMonitor(monitor, 1));

    //request.getUserProperties().setProperty("m2e.version", MavenPlugin.getVersion());

    List<String> goals = new ArrayList<String>();
    goals.add("package");
    //goals.add("eclipse:eclipse");
    request.setGoals(goals);

    //MavenPlugin.getConsole().showConsole();

    MavenExecutionResult result = maven.execute(request, monitor);
    if (result.hasExceptions()) {
      // Throw CoreException
      //System.out.println("Got exceptions while running!");
      Iterator<Throwable> it = result.getExceptions().iterator();
      Throwable e = null;

      while (it.hasNext()) {
        Throwable throwable = (Throwable) it.next();
        throwable.printStackTrace();
        e = throwable;
      }
      throw new CloudBeesException("Maven build failed!", e);
    }

    BuildSummary summary = result.getBuildSummary(result.getProject());
    MavenProject proj = summary.getProject();

    Artifact artifact = proj.getArtifact();
    File f = artifact.getFile();
    System.out.println("Artifact: " + f);
    System.out.println("Artifact name: " + f.getName());
    if (f.getName().endsWith(".jar")) {
      String apath = f.getAbsolutePath().substring(project.getLocation().toFile().getAbsolutePath().length());
      System.out.println("APATH: " + apath);
      return project.getFile(apath);
    }

    return null;
  }

}
