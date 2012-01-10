/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
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


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


/**
 * Provides a handler which can be used for mapping to keystrokes as well as a
 * menu item to go into and out of fullscreen mode.
 * 
 * @author Alex Blewitt <alex.blewitt@gmail.com>
 */
@SuppressWarnings("restriction")
public class ToggleFullScreenHandler extends AbstractHandler
{

    /**
     * Toggles the window into fullScreen mode, via
     * {@link BnLWindow#toggleFullScreen(NSWindow)}.
     */
    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        NSWindow[] windows = getWindows();
        //for (int i = 0; i < windows.length; i++)
        BnLWindow.toggleFullScreen( windows[0] );
        return null;
    }


    /**
     * Helper to get the active window. Returns the windows in the workbench's
     * array.
     * 
     * @return the NSWindow to go into fullScreen mode.
     */
    protected NSWindow[] getWindows()
    {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
            .getWorkbenchWindows();
        NSWindow[] nsWindows = new NSWindow[windows.length];
        for ( int i = 0; i < windows.length; i++ )
        {
            nsWindows[i] = windows[i].getShell().view.window();
        }
        return nsWindows;
    }


    /**
     * This is enabled if we are on OSX 10.7 and above, and there are windows
     * present.
     */
    @Override
    public boolean isEnabled()
    {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
            .getWorkbenchWindows();
        return windows.length >= 1 && OS.VERSION >= 0x1070;
    }
}
