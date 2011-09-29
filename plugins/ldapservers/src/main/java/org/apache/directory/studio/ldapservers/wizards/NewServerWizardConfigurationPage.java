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
package org.apache.directory.studio.ldapservers.wizards;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterConfigurationPage;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterConfigurationPageModifyListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * This class implements the wizard page for the new server wizard configuration page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewServerWizardConfigurationPage extends WizardPage implements
    LdapServerAdapterConfigurationPageModifyListener
{
    /** The configuration page */
    private LdapServerAdapterConfigurationPage configurationPage;


    /**
     * Creates a new instance of NewServerWizardConfigurationPage.
     */
    public NewServerWizardConfigurationPage( LdapServerAdapterConfigurationPage configurationPage )
    {
        super( configurationPage.getId() );
        setTitle( configurationPage.getTitle() );
        setDescription( configurationPage.getDescription() );
        setImageDescriptor( configurationPage.getImageDescriptor() );
        setPageComplete( configurationPage.isPageComplete() );

        this.configurationPage = configurationPage;
        configurationPage.setModifyListener( this );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Creating the control for the configuration page
        Control control = configurationPage.createControl( parent );

        // Setting the control and the focus
        setControl( control );
        control.setFocus();
    }


    /**
     * Saves the configuration information to the given LDAP server.
     *
     * @param ldapServer the LDAP server
     */
    public void saveConfiguration( LdapServer ldapServer )
    {
        configurationPage.saveConfiguration( ldapServer );
    }


    /**
     * {@inheritDoc}
     */
    public void configurationPageModified()
    {
        setErrorMessage( configurationPage.getErrorMessage() );
        setPageComplete( configurationPage.isPageComplete() );
    }
}
