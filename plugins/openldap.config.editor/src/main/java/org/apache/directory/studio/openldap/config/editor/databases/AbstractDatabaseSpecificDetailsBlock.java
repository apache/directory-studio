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
package org.apache.directory.studio.openldap.config.editor.databases;


import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;


/**
 * This interface represents a block for overlay configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractDatabaseSpecificDetailsBlock<D extends OlcDatabaseConfig> implements
    DatabaseSpecificDetailsBlock
{
    /** The details page*/
    protected DatabasesDetailsPage detailsPage;

    /** The database */
    protected D database;

    /** The connection */
    protected IBrowserConnection browserConnection;

    // Listeners
    protected ModifyListener dirtyModifyListener = event -> detailsPage.setEditorDirty();
    
    protected WidgetModifyListener dirtyWidgetModifyListener = event -> detailsPage.setEditorDirty();
    
    protected SelectionListener dirtySelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            detailsPage.setEditorDirty();
        }
    };


    /**
     * Creates a new instance of AbstractDatabaseSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param database the database
     */
    public AbstractDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, D database,
        IBrowserConnection browserConnection )
    {
        this.detailsPage = detailsPage;
        this.database = database;
        this.browserConnection = browserConnection;
    }


    /**
     * Creates a new instance of AbstractDatabaseSpecificDetailsBlock.
     *
     * @param detailsPage the details page
     * @param database the database
     */
    public AbstractDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, D database )
    {
        this( detailsPage, database, null );
    }


    /**
     * {@inheritDoc}
     */
    public DatabasesDetailsPage getDetailsPage()
    {
        return detailsPage;
    }
}
