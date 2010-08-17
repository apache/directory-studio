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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Name
        BaseWidgetUtils.createLabel( composite, Messages.getString( "ServerPropertyPage.Name" ), 1 ); //$NON-NLS-1$
        Text nameText = BaseWidgetUtils.createLabeledText( composite, "", 1 ); //$NON-NLS-1$

        // Type
        BaseWidgetUtils.createLabel( composite, "Type:", 1 );
        Text typeText = BaseWidgetUtils.createLabeledText( composite, "", 1 ); //$NON-NLS-1$

        // Vendor
        BaseWidgetUtils.createLabel( composite, "Vendor:", 1 );
        Text vendorText = BaseWidgetUtils.createLabeledText( composite, "", 1 ); //$NON-NLS-1$

        // Location
        Label locationLabel = BaseWidgetUtils.createLabel( composite,
            Messages.getString( "ServerPropertyPage.Location" ), 1 ); //$NON-NLS-1$
        locationLabel.setLayoutData( new GridData( SWT.NONE, SWT.TOP, false, false ) );
        Text locationText = BaseWidgetUtils.createWrappedLabeledText( composite, "", 1 ); //$NON-NLS-1$
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
