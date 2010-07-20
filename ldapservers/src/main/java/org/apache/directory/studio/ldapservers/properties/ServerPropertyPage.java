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
package org.apache.directory.studio.ldapservers.properties;


import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This class implements the Info property page for an LDAP server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{
    /**
     * Creates a new instance of ServerPropertyPage.
     */
    public ServerPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Name
        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( Messages.getString( "ServerPropertyPage.Name" ) ); //$NON-NLS-1$
        Text nameText = new Text( composite, SWT.NONE );
        nameText.setEditable( false );
        nameText.setBackground( parent.getBackground() );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Type
        Label typeLabel = new Label( composite, SWT.NONE );
        typeLabel.setText( "Type:" );
        Text typeText = new Text( composite, SWT.NONE );
        typeText.setEditable( false );
        typeText.setBackground( parent.getBackground() );
        typeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Vendor
        Label vendorLabel = new Label( composite, SWT.NONE );
        vendorLabel.setText( "Vendor:" );
        Text vendorText = new Text( composite, SWT.NONE );
        vendorText.setEditable( false );
        vendorText.setBackground( parent.getBackground() );
        vendorText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Location
        Label locationLabel = new Label( composite, SWT.NONE );
        locationLabel.setText( Messages.getString( "ServerPropertyPage.Location" ) ); //$NON-NLS-1$
        locationLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );
        Text locationText = new Text( composite, SWT.WRAP );
        locationText.setEditable( false );
        locationText.setBackground( parent.getBackground() );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.widthHint = 300;
        locationText.setLayoutData( gd );

        // Getting the server
        LdapServer server = ( LdapServer ) getElement();
        if ( server != null )
        {
            LdapServerAdapterExtension ldapServerAdapterExtension = server.getLdapServerAdapterExtension();

            nameText.setText( server.getName() );
            typeText.setText( ldapServerAdapterExtension.getName() + " " + ldapServerAdapterExtension.getVersion() );
            vendorText.setText( ldapServerAdapterExtension.getVendor() );
            locationText.setText( LdapServersManager.getServersFolder().append( server.getId() ).toOSString() );
        }

        return parent;
    }
}
