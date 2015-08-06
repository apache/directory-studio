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
package org.apache.directory.studio.ldapservers.model;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * This interface defines a configuration page for an {@link LdapServerAdapter}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LdapServerAdapterConfigurationPage
{
    /**
     * Creates the control for the configuration page.
     *
     * @param parent the parent control
     * @return the created control
     */
    Control createControl( Composite parent );


    /**
     * Gets the description.
     *
     * @return the description
     */
    String getDescription();


    /**
     * Gets the error message.
     *
     * @return the error message
     */
    String getErrorMessage();


    /**
     * Gets the ID.
     *
     * @return the ID
     */
    String getId();


    /**
     * Gets the {@link ImageDescriptor}.
     *
     * @return the {@link ImageDescriptor}
     */
    ImageDescriptor getImageDescriptor();


    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();


    /**
     * Indicates if the page is complete.
     *
     * @return <code>true</code> if the page is complete,
     *         <code>false</code> if not.
     */
    boolean isPageComplete();


    /**
     * Loads configuration information from the given LDAP server.
     *
     * @param ldapServer the LDAP server
     */
    void loadConfiguration( LdapServer ldapServer );


    /**
     * Saves the configuration information to the given LDAP server.
     *
     * @param ldapServer the LDAP server
     */
    void saveConfiguration( LdapServer ldapServer );


    /**
     * Sets a modify listener.
     *
     * @param modifyListener the modify listener
     */
    void setModifyListener( LdapServerAdapterConfigurationPageModifyListener modifyListener );


    /**
     * Validates the configuration page
     */
    void validate();
}
