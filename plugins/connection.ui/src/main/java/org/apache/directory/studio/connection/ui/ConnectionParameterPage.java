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
package org.apache.directory.studio.connection.ui;


import org.apache.directory.shared.ldap.filter.LdapURL;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;


/**
 * An IConnectionParameterPage is used to add connection parameter pages
 * to the connection wizard and the connection property page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ConnectionParameterPage
{

    /**
     * Save the fields to the connection parameters.
     * 
     * @param parameter the connection parameter
     */
    public void saveParameters( ConnectionParameter parameter );


    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public boolean isValid();


    /**
     * Gets an error message that should be displayed
     * to the user. Null means no error message so an 
     * existing error message should be cleared.
     * 
     * @return the error message
     */
    public String getErrorMessage();


    /**
     * Gets a non-error message that should be displayed
     * to the user. Null means no message so an existing
     * message should be cleared.
     * 
     * @return the message
     */
    public String getMessage();


    /**
     * Gets an info message that should be displayed
     * to the user. Null means no info message so an 
     * existing info message should be cleared.
     * 
     * @return the info message
     */
    public String getInfoMessage();


    /**
     * Initializes the connection parameter page.
     * 
     * @param parent the parent
     * @param listener the connection parameter page modify listener
     * @param parameter the initial connection parameter
     */
    public void init( Composite parent, ConnectionParameterPageModifyListener listener, ConnectionParameter parameter );


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings();


    /**
     * Sets the runnable context.
     * 
     * @param runnableContext the runnable context
     */
    public void setRunnableContext( IRunnableContext runnableContext );


    /**
     * Sets the page id.
     * 
     * @param pageId the page id
     */
    public void setPageId( String pageId );


    /**
     * Gets the page id.
     * 
     * @return the page id
     */
    public String getPageId();


    /**
     * Sets the page name.
     * 
     * @param pageName the page name
     */
    public void setPageName( String pageName );


    /**
     * Gets the page name.
     * 
     * @return the page name
     */
    public String getPageName();


    /**
     * Sets the page description.
     * 
     * @param pageDescription the page description
     */
    public void setPageDescription( String pageDescription );


    /**
     * Gets the page description.
     * 
     * @return the page description
     */
    public String getPageDescription();


    /**
     * Sets the page id this page depends on.
     * 
     * @param pageDependsOnId the page id this page depends on
     */
    public void setPageDependsOnId( String pageDependsOnId );


    /**
     * Gets the page id this page depends on.
     * 
     * @return the page id this page depends on
     */
    public String getPageDependsOnId();


    /**
     * Sets the focus.
     */
    public void setFocus();


    /**
     * The implementing class must return true if important
     * connection parameters were modified that require a
     * reconnection to take effect.
     *
     * @return true if a reconnection if required
     */
    public boolean isReconnectionRequired();


    /**
     * The implementing class must return true if any
     * parameter was modified.
     * 
     * @return true, if parameters were modified
     */
    public boolean areParametersModifed();


    /**
     * Merges the connection parameters into the LDAP URL.
     *
     * @param parameter the source connection parameter
     * @param ldapUrl the target LDAP URL
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapURL ldapUrl );


    /**
     * Merges the LDAP URL into the connection parameters.
     *
     * @param ldapUrl the source LDAP URL
     * @param parameter the target connection parameter
     */
    public void mergeLdapUrlToParameters( LdapURL ldapUrl, ConnectionParameter parameter );
}
