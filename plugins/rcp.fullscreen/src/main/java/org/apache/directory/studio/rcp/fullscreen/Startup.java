/*******************************************************************************
 * Copyright (c) 2011, Alex Blewitt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alex Blewitt - initial API and implementation
 *******************************************************************************/
package org.apache.directory.studio.rcp.fullscreen;


import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class Startup implements Runnable, IStartup
{

    private IWorkbenchWindow[] windows;


    @Override
    public void earlyStartup()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        windows = workbench.getWorkbenchWindows();
        workbench.getDisplay().asyncExec( this );
    }


    @Override
    public void run()
    {
        for ( int i = 0; i < windows.length; i++ )
        {
            IWorkbenchWindow iWorkbenchWindow = windows[i];
            Activator.setWindowFullscreen( iWorkbenchWindow.getShell() );
        }
    }
}