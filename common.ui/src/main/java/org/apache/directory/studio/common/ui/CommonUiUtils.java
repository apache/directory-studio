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
package org.apache.directory.studio.common.ui;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;


/**
 * This class contains helpful methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CommonUiUtils
{
    /**
     * Reports an error.
     *
     * @param message
     *      the message
     */
    public static void reportError( String message )
    {
        MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Error!", null, message, MessageDialog.ERROR, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }
}
