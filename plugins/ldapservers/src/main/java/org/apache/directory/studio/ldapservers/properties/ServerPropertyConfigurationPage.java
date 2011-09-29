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
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterConfigurationPage;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterConfigurationPageModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This class implements the Configuration property page for an LDAP server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerPropertyConfigurationPage extends PropertyPage implements IWorkbenchPropertyPage,
    LdapServerAdapterConfigurationPageModifyListener
{
    /** The LDAP server*/
    private LdapServer ldapServer;

    /** The configuration page */
    private LdapServerAdapterConfigurationPage configurationPage;


    /**
     * Creates a new instance of ServerPropertyPage.
     */
    public ServerPropertyConfigurationPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        // Getting the server
        ldapServer = ( LdapServer ) getElement();

        if ( ldapServer != null )
        {
            configurationPage = ldapServer.getLdapServerAdapterExtension()
                .getNewConfigurationPageInstance();
            configurationPage.setModifyListener( this );

            Control control = configurationPage.createControl( parent );
            configurationPage.loadConfiguration( ldapServer );

            return control;
        }

        return parent;
    }


    /**
     * {@inheritDoc}
     */
    public void configurationPageModified()
    {
        if ( ldapServer != null )
        {
            setErrorMessage( configurationPage.getErrorMessage() );
            setValid( configurationPage.isPageComplete() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        if ( ldapServer != null )
        {
            // Saving the configuration
            configurationPage.saveConfiguration( ldapServer );

            // Saving the server to the file store.
            LdapServersManager.getDefault().saveServersToStore();

        }

        return super.performOk();
    }
}
