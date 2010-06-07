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
package org.apache.directory.studio.apacheds.wizards;


import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class implements the wizard page for the new server wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewServerWizardPage extends WizardPage
{
    /** The servers handler */
    private ServersHandler serversHandler;

    // UI fields
    private Text nameText;


    /**
     * Creates a new instance of NewServerWizardPage.
     */
    public NewServerWizardPage()
    {
        super( NewServerWizardPage.class.getCanonicalName() );
        setTitle( Messages.getString( "NewServerWizardPage.CreateNewServer" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewServerWizardPage.PleaseSpecifyName" ) ); //$NON-NLS-1$
        setImageDescriptor( ApacheDsPlugin.getDefault().getImageDescriptor(
            ApacheDsPluginConstants.IMG_SERVER_NEW_WIZARD ) );
        setPageComplete( false );
        serversHandler = ServersHandler.getDefault();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );

        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( Messages.getString( "NewServerWizardPage.Name" ) ); //$NON-NLS-1$
        nameText = new Text( composite, SWT.BORDER );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        setControl( composite );
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        displayErrorMessage( null );

        String name = nameText.getText();
        if ( ( name != null ) )
        {
            if ( "".equals( name ) ) //$NON-NLS-1$
            {
                displayErrorMessage( Messages.getString( "NewServerWizardPage.ErrorEnterName" ) ); //$NON-NLS-1$
                return;
            }
            if ( !serversHandler.isNameAvailable( name ) )
            {
                displayErrorMessage( Messages.getString( "NewServerWizardPage.ErrorNameExists" ) ); //$NON-NLS-1$
                return;
            }
        }
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    protected void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Gets the name of the server.
     *
     * @return
     *      the name of the server
     */
    public String getServerName()
    {
        return nameText.getText();
    }
}
