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

package org.apache.directory.studio.connection.ui;


import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;


/**
 * This class is used to handle exceptions thrown at runtime.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExceptionHandler
{
    /**
     * Opens an error dialog to display the given error.
     *
     * @param status
     *      the error to show to the user
     */
    public void handleException( IStatus status )
    {
        display( null, status );
    }


    /**
     * Opens an error dialog to display the given error.
     *
     * @param message
     *      the message to show in this dialog, or null to indicate that the error's message should be shown as the primary message
     * @param status
     *      the error to show to the user
     */
    private void display( final String message, final IStatus status )
    {
        if ( Thread.currentThread() == Display.getDefault().getThread() )
        {
            ErrorDialog.openError( Display.getDefault().getActiveShell(),
                Messages.getString( "ExceptionHandler.Error" ), message, status ); //$NON-NLS-1$
        }
        else
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    ErrorDialog.openError( Display.getDefault().getActiveShell(), Messages
                        .getString( "ExceptionHandler.Error" ), message, status ); //$NON-NLS-1$
                }
            };
            Display.getDefault().asyncExec( runnable );
        }

        ConnectionCorePlugin.getDefault().getLog().log( status );
    }
}
