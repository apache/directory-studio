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
 * A basic common abstract class implementing {@link ClauseComposite}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractClauseComposite implements ClauseComposite
{
    /** The visual editor composite */
    protected Composite visualEditorComposite;

    /** The connection */
    protected IBrowserConnection connection;
    
    /** The ACL context in use */
    protected OpenLdapAclValueWithContext context;


    /**
     * Creates a new instance of AbstractClauseComposite.
     */
    public AbstractClauseComposite()
    {
    }


    /**
     * Creates a new instance of AbstractClauseComposite.
     *
     * @param clause the clause
     */
    public AbstractClauseComposite( OpenLdapAclValueWithContext context, Composite visualEditorComposite )
    {
        this.context = context;
        this.visualEditorComposite = visualEditorComposite;
        connection = context.getConnection();
    }


    /**
     * {@inheritDoc}
     */
    public Composite createComposite( Composite parent )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Composite getVisualEditorComposite()
    {
        return visualEditorComposite;
    }


    /**
     * {@inheritDoc}
     */
    public void setVisualEditorComposite( Composite visualEditorComposite )
    {
        this.visualEditorComposite = visualEditorComposite;
    }


    /**
     * {@inheritDoc}
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
     * {@inheritDoc}
     */
    public void setConnection( IBrowserConnection connection )
    {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     */
    public void saveWidgetSettings()
    {
    }
    
    
    /**
     * @return The ACL context in use
     */
    public OpenLdapAclValueWithContext getContext()
    {
        return context;
    }
    
    
    /**
     * @param context The ACL context in use
     */
    public void setContext( OpenLdapAclValueWithContext context )
    {
        this.context = context;
    }
}
