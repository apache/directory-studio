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
package org.apache.directory.ldapstudio.newversion;


/**
 * This class is used to display the 'New Version' message.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewVersion
{
    /**
     * Shows (if needed) the New Version Dialog.
     */
    public static void showNewVersionDialog()
    {
        if ( !Activator.getDefault().getDialogSettings().getBoolean( NewVersionDialog.DIALOG_SETTINGS_ID ) )
        {
            Activator.getDefault().getWorkbench().getDisplay().asyncExec( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 2500 );
                    }
                    catch ( InterruptedException e )
                    {
                    }
                    NewVersionDialog dialog = new NewVersionDialog();
                    dialog.open();
                }
            } );
        }
    }
}
