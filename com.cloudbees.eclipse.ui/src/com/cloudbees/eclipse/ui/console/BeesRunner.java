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
package com.cloudbees.eclipse.ui.console;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import org.eclipse.ui.console.IOConsoleOutputStream;

import com.cloudbees.eclipse.core.CloudBeesCorePlugin;
import com.cloudbees.eclipse.core.CloudBeesException;
import com.cloudbees.eclipse.core.GrandCentralService;
import com.cloudbees.eclipse.core.GrandCentralService.AuthInfo;
import com.cloudbees.eclipse.run.sdk.CBSdkActivator;

/**
 * Wrapper to run Bees command line client commands and access input/output streams
 * 
 * @author ahtik
 */
public class BeesRunner {

  private final String linesep = System.getProperty("line.separator");
  
  //private boolean streamProcessing = false;
  private OutputStream currentOutputStream = null;

  public void run(final BeesConsoleSession sess) {

    Thread inputThread = new Thread(new Runnable() {
      public void run() {
        try {
          Scanner scan = new Scanner(sess.getInputStream());
          while (scan.hasNextLine()) {

            final String input = scan.nextLine();
            if (input.length() == 0) {

              IOConsoleOutputStream out = sess.newOutputStream();
              try {
                out.write("bees ");
                out.flush();
                BeesConsole.moveCaret();
              } finally {
                if (out != null) {
                  out.close();
                }
              }

              continue;
            }

            final IOConsoleOutputStream out = sess.newOutputStream();
            // we need to wrap this into a separate thread in order to catch follow-up readlines even when the runbees hangs (waits for more input)
            Thread t = new Thread(new Runnable() {
              public void run() {
                try {
                  runBees(input, out);
                } catch (CloudBeesException e) {
                  e.printStackTrace();
                } catch (IOException e) {
                  e.printStackTrace();
                } finally {
                  if (out != null) {
                    try {
                      out.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
            },"CloudBees Console runner");
            t.start();

          }

        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    });

    inputThread.start();

  }

  private void runBees(String cmd, IOConsoleOutputStream out) throws CloudBeesException, IOException {

    if (currentOutputStream != null) { // current stream in progress, potentially waiting for the user input. let's send it to the current stream.
      currentOutputStream.write((cmd+linesep).getBytes("UTF-8"));
      currentOutputStream.flush();
      return;
    }

    GrandCentralService grandCentralService;
    grandCentralService = CloudBeesCorePlugin.getDefault().getGrandCentralService();
    AuthInfo cachedAuthInfo = grandCentralService.getCachedAuthInfo(false);
    String secretKey = "-Dbees.apiSecret=" + cachedAuthInfo.getAuth().secret_key;//$NON-NLS-1$
    String authKey = "-Dbees.apiKey=" + cachedAuthInfo.getAuth().api_key;//$NON-NLS-1$
    String beesHome = "-Dbees.home=" + CBSdkActivator.getDefault().getBeesHome();
    String beesHomeDir = CBSdkActivator.getDefault().getBeesHome();

    //java.home=C:\Java\jdk1.6.0_29\jre
    //String java = System.getProperty("eclipse.vm");

    String java = System.getProperty("java.home");
    if (!java.endsWith(File.separator)) {
      java = java + File.separator + "bin" + File.separator + "java";
    }

    OutputStreamWriter osw = new OutputStreamWriter(out);
    BufferedWriter writer = new BufferedWriter(osw);

    final ProcessBuilder pb = new ProcessBuilder(java, "-Xmx256m", beesHome, secretKey, authKey, "-cp", beesHomeDir
        + "lib/cloudbees-boot.jar", "com.cloudbees.sdk.boot.Launcher", cmd);
    pb.environment().put("BEES_HOME", beesHomeDir);
    pb.directory(new File(beesHomeDir));
    pb.redirectErrorStream(true);

    Process p = null;
    try {
      p = pb.start();
    } catch (Exception e) {
      writer.write("Error while running CloudBees SDK: " + e.getMessage() + "\n");
      e.printStackTrace(new PrintWriter(writer));
      return;
    }

    //    String line;

    InputStream stdin = p.getInputStream();

    byte[] b = new byte[4096 * 10];

    currentOutputStream = p.getOutputStream();
    try {
      for (int n; (n = stdin.read(b)) != -1;) {
        writer.write(new String(b, 0, n, "UTF-8"));
        writer.flush();
        BeesConsole.moveCaret();
      }
    } finally {
      currentOutputStream = null;
    }

    writer.write("bees ");
    writer.flush();
    BeesConsole.moveCaret();

  }

}
