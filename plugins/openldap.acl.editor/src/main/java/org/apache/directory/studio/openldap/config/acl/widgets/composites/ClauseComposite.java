/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets.composites;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.eclipse.swt.widgets.Composite;


/**
 * This interface defines a clause composite.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ClauseComposite<C>
{
    /**
     * Creates the composite.
     *
     * @param parent the parent composite
     */
    Composite createComposite( Composite parent );


    /**
     * Gets the visual editor composite.
     * 
     *  @return the visual editor composite
     */
    Composite getVisualEditorComposite();


    /**
     * Sets the visual editor composite.
     *
     * @param visualEditorComposite the visual editor composite
     */
    void setVisualEditorComposite( Composite visualEditorComposite );


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    IBrowserConnection getConnection();


    /**
     * Sets the connection.
     *
     * @param connection the connection
     */
    void setConnection( IBrowserConnection connection );


    /**
     * Gets the clause.
     *
     * @return the clause
     */
    C getClause();


    /**
     * Sets the clause.
     *
     * @param clause the clause
     */
    void setClause( C clause );


    /**
     * Saves widget settings.
     */
    void saveWidgetSettings();
    
    
    /**
     * @return The ACL context in use
     */
    OpenLdapAclValueWithContext getContext();
    
    
    /**
     * @param context The ACL context in use
     */
    void setContext( OpenLdapAclValueWithContext context );
}
