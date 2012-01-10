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


import org.eclipse.swt.internal.cocoa.NSWindow;

import org.apache.directory.studio.rcp.fullscreen.SO.Reflect;


/**
 * Wrapper methods to assist with extensions to NSWindow
 * 
 * @author Alex Blewitt <alex.blewitt@gmail.com>
 */
@SuppressWarnings("restriction")
public class BnLWindow
{

    /**
     * Toggle the window in and out of fullScreen mode.
     * 
     * @param window
     *            the window, which must not be <code>null</code>.
     */
    public static void toggleFullScreen( NSWindow window )
    {
        long toggleFullScreen = SO.selector( "toggleFullScreen:" ); //$NON-NLS-1$
        long target = SO.getID( window );
        SO.objc_msgSend( target, toggleFullScreen, 0 );
    }


    /**
     * Returns true if the window is in fullScreen mode already.
     * 
     * @param window
     *            the window, which must not be null.
     * @return true if the window is in fullScreen mode, false otherwise.
     */
    public static boolean isFullScreen( NSWindow window )
    {
        long styleMask = Reflect.executeLong( window, "styleMask" ); //$NON-NLS-1$
        return ( ( ( styleMask >> 14 ) & 1 ) == 1 );
    }


    /**
     * Sets the window's fullScreen mode, regardless of current setting
     * 
     * @param window
     *            the window, which must not be null.
     * @param fullScreen
     *            true if the window is to go into fullScreen mode, false
     *            otherwise.
     */
    public static void setFullScreen( NSWindow window, boolean fullScreen )
    {
        if ( isFullScreen( window ) != fullScreen )
            toggleFullScreen( window );
    }
}
