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


import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.eclipse.swt.widgets.Composite;


/**
 * A basic common abstract class implementing {@link ClauseComposite}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractWhoClauseComposite<C> extends AbstractClauseComposite
{
    /** The Who clause */
    protected C whoClause;

    /**
     * Creates a new instance of AbstractClauseComposite.
     */
    public AbstractWhoClauseComposite()
    {
    }


    /**
     * Creates a new instance of AbstractClauseComposite.
     *
     * @param whoClause the clause
     */
    public AbstractWhoClauseComposite( OpenLdapAclValueWithContext context, C whoClause, Composite visualEditorComposite )
    {
        super( context, visualEditorComposite );
        this.whoClause = whoClause;
    }


    /**
     * {@inheritDoc}
     */
    public C getClause()
    {
        return whoClause;
    }


    /**
     * {@inheritDoc}
     */
    public void setClause( C clause )
    {
        this.whoClause = clause;
    }
}
