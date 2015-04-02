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
import org.eclipse.swt.widgets.Composite;


/**
 * This interface defines a clause composite.
 */
public interface ClauseComposite<C>
{
    /**
     * Creates the composite.
     *
     * @param parent the parent composite
     */
    public Composite createComposite( Composite parent );


    /**
     * Gets the visual editor composite.
     * 
     *  @return the visual editor composite
     */
    public Composite getVisualEditorComposite();


    /**
     * Sets the visual editor composite.
     *
     * @param visualEditorComposite the visual editor composite
     */
    public void setVisualEditorComposite( Composite visualEditorComposite );


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public IBrowserConnection getConnection();


    /**
     * Sets the connection.
     *
     * @param connection the connection
     */
    public void setConnection( IBrowserConnection connection );


    /**
     * Gets the clause.
     *
     * @return the clause
     */
    public C getClause();


    /**
     * Sets the clause.
     *
     * @param clause the clause
     */
    public void setClause( C clause );


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings();
}
