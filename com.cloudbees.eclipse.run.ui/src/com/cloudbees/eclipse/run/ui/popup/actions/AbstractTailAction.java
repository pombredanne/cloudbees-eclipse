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
package com.cloudbees.eclipse.run.ui.popup.actions;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.eclipse.ui.internal.console.ConsoleView;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.eclipse.run.core.BeesSDK;
import com.cloudbees.eclipse.run.ui.CBRunUiActivator;
import com.cloudbees.eclipse.run.ui.Images;
import com.cloudbees.eclipse.run.ui.console.ConsoleUtil;

@SuppressWarnings("restriction")
public abstract class AbstractTailAction implements IObjectActionDelegate {

  public static final String LOG_NAME_SERVER = "server";
  public static final String LOG_NAME_ACCESS = "access";
  public static final String LOG_NAME_ERROR = "error";
  protected static final String CONSOLE_TYPE = "com.cloudbees.eclipse.run.ui.tailLogs";

  private static Map<String, Thread> tmap = new ConcurrentHashMap<String, Thread>();

  private static IConsoleListener listener = new IConsoleListener() {
    public void consolesRemoved(IConsole[] arg0) {
      /*      if (silenceThreadKiller) {
              return;
            }
      */// make sure the thread that shows output is interrupted.
      for (int i = 0; i < arg0.length; i++) {
        IConsole c = arg0[0];
        if (c.getType() != null && c.getType().startsWith(CONSOLE_TYPE)) {

          Thread t = tmap.get(c.getName());
          if (t != null) {
            tmap.remove(t);
            try {
              t.interrupt();
            } catch (Exception e) {

            }
          }
        }
      }
    }

    public void consolesAdded(IConsole[] arg0) {

    }
  };

  //volatile private static boolean silenceThreadKiller = false;

  @Override
  public void run(IAction action) {
    if (action instanceof ObjectPluginAction) {
      ObjectPluginAction pluginAction = (ObjectPluginAction) action;
      ISelection selection = pluginAction.getSelection();

      if (selection instanceof IStructuredSelection) {
        IStructuredSelection structSelection = (IStructuredSelection) selection;
        Object element = structSelection.getFirstElement();

        if (element instanceof ApplicationInfo) {
          final ApplicationInfo appInfo = (ApplicationInfo) element;

          Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
              tail(appInfo, getLogName());
            }
          });
        }
      }
    }
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  private IConsole getTailConsole(String consoleName) {
    ImageDescriptor descriptor = CBRunUiActivator.imageDescriptorFromPlugin(CBRunUiActivator.PLUGIN_ID,
        Images.CLOUDBEES_ICON_16x16_PATH);

    IConsole console = null;

    IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

    for (IConsole c : manager.getConsoles()) {
      if (consoleName.equals(c.getName())) {
        console = c;
      }
    }

    if (console == null) {
      console = new IOConsole(consoleName, CONSOLE_TYPE, descriptor, true);
      manager.addConsoles(new IConsole[] { console });
      //manager.showConsoleView(console);      
    } else {
      // activate existing console. Have not found a way to force top position in console stack without removing it temporarily.

      //      try {
      //silenceThreadKiller = true;
      //manager.removeConsoles(new IConsole[] { foundConsole });
      //manager.addConsoles(new IConsole[] { foundConsole });
      //((IOConsole) foundConsole).activate();
      //manager.refresh(foundConsole);
      //manager.showConsoleView(foundConsole);
      //} finally {
      //silenceThreadKiller = false;
      //}
      //
      //((ConsoleManager)manager).
      //manager.showConsoleView(foundConsole);
      //console.activate();
    }

    return console;
  }

  private void tail(final ApplicationInfo appInfo, final String logName) {
    IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

    manager.addConsoleListener(listener);

    String consoleName = MessageFormat.format("Tail {0} log :: {1}", logName, appInfo.getTitle());
    final IConsole console = getTailConsole(consoleName);

    ConsoleUtil.activateConsole(console);

    manager.showConsoleView(console);

    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {
        IOConsoleOutputStream consoleOutputStream = ((IOConsole) console).newOutputStream();
        try {
          BeesSDK.tail(appInfo.getId(), logName, consoleOutputStream);
        } catch (Exception e) {
          //CBRunUiActivator.logErrorAndShowDialog(e);
          //e.printStackTrace();
          // Safe to not log as when console is closed this thread will stop as writing to outputstream fails
        }
      }
    });

    t.start();

    tmap.put(consoleName, t);
  }

 

  protected abstract String getLogName();

  public static class TailServerLogAction extends AbstractTailAction {

    @Override
    protected String getLogName() {
      return LOG_NAME_SERVER;
    }

  }

  public static class TailAccessLogAction extends AbstractTailAction {

    @Override
    protected String getLogName() {
      return LOG_NAME_ACCESS;
    }

  }

  public static class TailErrorLogAction extends AbstractTailAction {

    @Override
    protected String getLogName() {
      return LOG_NAME_ERROR;
    }

  }
}
