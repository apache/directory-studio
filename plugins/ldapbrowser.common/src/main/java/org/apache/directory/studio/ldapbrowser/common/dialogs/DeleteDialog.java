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
package org.apache.directory.studio.ldapbrowser.common.dialogs;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog that prompts a user to delete items in the browser tree.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteDialog extends MessageDialog
{

    /** The "Use Tree Delete Control" dialog setting . */
    private static final String USE_TREE_DELETE_CONTROL_DIALOGSETTING_KEY = DeleteDialog.class.getName()
        + ".useTreeDeleteControl"; //$NON-NLS-1$

    private Button useTreeDeleteControlCheckbox;

    private boolean askForTreeDeleteControl;

    private boolean useTreeDeleteControl;


    /**
     * Instantiates a new delete dialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param message the message
     * @param askForTreeDeleteControl true if the user should be asked if the tree delete control should be used
     */
    public DeleteDialog( Shell parentShell, String title, String message, boolean askForTreeDeleteControl )
    {
        super( parentShell, title, null, message, QUESTION, new String[]
            { "OK", "Cancel" /*IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL*/ }, OK );

        this.askForTreeDeleteControl = askForTreeDeleteControl;
        this.useTreeDeleteControl = false;

        if ( BrowserCommonActivator.getDefault().getDialogSettings().get( USE_TREE_DELETE_CONTROL_DIALOGSETTING_KEY ) == null )
        {
            BrowserCommonActivator.getDefault().getDialogSettings().put( USE_TREE_DELETE_CONTROL_DIALOGSETTING_KEY,
                false );
        }
    }


    @Override
    protected Control createCustomArea( Composite parent )
    {
        if ( askForTreeDeleteControl )
        {
            useTreeDeleteControlCheckbox = new Button( parent, SWT.CHECK );
            useTreeDeleteControlCheckbox.setText( Messages.getString( "DeleteDialog.UseTreeDeleteControl" ) ); //$NON-NLS-1$
            useTreeDeleteControlCheckbox.setSelection( BrowserCommonActivator.getDefault().getDialogSettings()
                .getBoolean( USE_TREE_DELETE_CONTROL_DIALOGSETTING_KEY ) );
            return useTreeDeleteControlCheckbox;
        }
        else
        {
            return null;
        }
    }


    @Override
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == OK )
        {
            useTreeDeleteControl = useTreeDeleteControlCheckbox != null && useTreeDeleteControlCheckbox.getSelection();

            if ( useTreeDeleteControlCheckbox != null )
            {
                BrowserCommonActivator.getDefault().getDialogSettings().put( USE_TREE_DELETE_CONTROL_DIALOGSETTING_KEY,
                    useTreeDeleteControlCheckbox.getSelection() );
            }
        }
        super.buttonPressed( buttonId );
    }


    /**
     * Checks if tree delete control should be used.
     * 
     * @return true, if tree delete control should be used
     */
    public boolean isUseTreeDeleteControl()
    {
        return useTreeDeleteControl;
    }

}
