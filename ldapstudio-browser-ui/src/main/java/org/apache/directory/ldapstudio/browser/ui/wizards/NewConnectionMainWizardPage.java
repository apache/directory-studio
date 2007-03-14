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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionPageModifyListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * The NewConnectionMainWizardPage is used to specify the connection name
 * and the network parameters of the new connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionMainWizardPage extends WizardPage implements ConnectionPageModifyListener
{

    /** The wizard. */
    private NewConnectionWizard wizard;


    /**
     * Creates a new instance of NewConnectionMainWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewConnectionMainWizardPage( String pageName, NewConnectionWizard wizard )
    {
        super( pageName );
        setTitle( "New LDAP Connection" );
        setDescription( "Please enter connection name and network parameters." );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_CONNECTION_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
        wizard.getCpw().addConnectionPageModifyListener( this );
    }


    /**
     * {@inheritDoc}
     */
    public void connectionPageModified()
    {
        if ( isCurrentPage() )
        {
            validate();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setMessage( String message )
    {
        if ( isCurrentPage() )
        {
            super.setMessage( message );
            validate();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setErrorMessage( String errorMessage )
    {
        if ( isCurrentPage() )
        {
            super.setErrorMessage( errorMessage );
            validate();
        }
    }


    /**
     * {@inheritDoc}
     */
    public IConnection getRealConnection()
    {
        return null;
    }


    /**
     * Validates this page.
     */
    private void validate()
    {
        setPageComplete( getMessage() == null );
        getContainer().updateButtons();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );

        wizard.getCpw().addMainInput( "", "", 389, IConnection.ENCYRPTION_NONE, composite );
        wizard.getCpw().addOpenConnectionInput( true, composite );

        setControl( composite );
    }

}