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

package org.apache.directory.ldapstudio.browser.ui;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;


public class ExceptionHandler
{

    public ExceptionHandler()
    {
        super();
    }


    public void handleException( IStatus status )
    {
        display( null, status );
    }


    private void display( final String message, final IStatus status )
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                ErrorDialog.openError( Display.getDefault().getActiveShell(), "Error", message, status );
            }
        };
        Display.getDefault().asyncExec( runnable );
        BrowserCorePlugin.getDefault().getLog().log( status );
    }

}
