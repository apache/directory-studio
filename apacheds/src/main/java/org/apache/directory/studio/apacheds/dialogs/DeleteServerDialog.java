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
package org.apache.directory.studio.apacheds.dialogs;


import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog that prompts a user to delete server(s) and/or server configuration(s).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DeleteServerDialog extends MessageDialog
{
    protected Server server;

    protected Button checkDeleteRunning;
    protected Button checkDeleteRunningStop;


    /**
     * Creates a new DeleteServerDialog.
     * 
     * @param parentShell a shell
     * @param server
     *      the server
     */
    public DeleteServerDialog( Shell parentShell, Server server )
    {
        super( parentShell, "Delete Server", null, null, QUESTION, new String[]
            { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, OK );

        if ( server == null )
        {
            throw new IllegalArgumentException();
        }

        this.server = server;
        message = NLS.bind( "Are you sure you want to delete {0}?", server.getName() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createCustomArea( Composite parent )
    {
        // create a composite with standard margins and spacing
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        layout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        layout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        layout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        composite.setFont( parent.getFont() );

        // prompt for stopping running server
        if ( server.getState() != ServerStateEnum.STOPPED )
        {
            checkDeleteRunning = new Button( composite, SWT.CHECK );
            checkDeleteRunning.setText( "Delete running server" );
            checkDeleteRunning.setSelection( true );

            checkDeleteRunningStop = new Button( composite, SWT.CHECK );
            checkDeleteRunningStop.setText( "Stop server before deleting" );
            checkDeleteRunningStop.setSelection( true );
            GridData data = new GridData();
            data.horizontalIndent = 15;
            checkDeleteRunningStop.setLayoutData( data );

            checkDeleteRunning.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    checkDeleteRunningStop.setEnabled( checkDeleteRunning.getSelection() );
                }
            } );
        }

        Dialog.applyDialogFont( composite );

        return composite;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        super.buttonPressed( buttonId );
    }
}