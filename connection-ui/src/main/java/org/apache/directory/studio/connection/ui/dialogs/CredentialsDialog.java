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

package org.apache.directory.studio.connection.ui.dialogs;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The CredentialsDialog is used to ask the user for credentials (paasword).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CredentialsDialog extends InputDialog
{

    /**
     * Creates a new instance of CredentialsDialog.
     * 
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogMessage the dialog message
     * @param initialValue the initial value
     * @param validator the validator
     */
    public CredentialsDialog( Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
        IInputValidator validator )
    {
        super( parentShell, dialogTitle, dialogMessage, initialValue, validator );
    }


    /**
     * @see org.eclipse.jface.dialogs.InputDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        super.getText().setEchoChar( '*' );
        return composite;
    }

}
