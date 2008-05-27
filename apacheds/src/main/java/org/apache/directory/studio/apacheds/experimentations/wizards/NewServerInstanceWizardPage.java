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
package org.apache.directory.studio.apacheds.experimentations.wizards;


import org.apache.directory.studio.apacheds.experimentations.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.experimentations.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.experimentations.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.experimentations.model.ServersHandler;
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
 * This class implements the wizard page for the new server instance wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewServerInstanceWizardPage extends WizardPage
{
    /** The servers handler */
    private ServersHandler serversHandler;

    // UI fields
    private Text nameText;


    /**
     * Creates a new instance of NewServerInstanceWizardPage.
     */
    public NewServerInstanceWizardPage()
    {
        super( NewServerInstanceWizardPage.class.getCanonicalName() );
        setTitle( "Create a Server" );
        setDescription( "Please specify a name to create a new server." );
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
        nameLabel.setText( "Name:" );
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
            if ( "".equals( name ) )
            {
                displayErrorMessage( "Enter a name for the server." );
                return;
            }
            if ( !serversHandler.isNameAvailable( name ) )
            {
                displayErrorMessage( "A server with the same name already exists." );
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
     * Gets the name of the server instance.
     *
     * @return
     *      the name of the server instance
     */
    public String getServerInstanceName()
    {
        return nameText.getText();
    }
}
