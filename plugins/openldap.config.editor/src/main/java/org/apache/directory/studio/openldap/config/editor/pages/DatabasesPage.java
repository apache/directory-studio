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
package org.apache.directory.studio.openldap.config.editor.pages;


import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.databases.DatabasesMasterDetailsBlock;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This class represents the Databases Page of the Server Configuration Editor. It just
 * create a main page that contains two other pages, a master block and a detail block.
 * 
 * NOTE : I'm not sure we need this page...
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabasesPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = DatabasesPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPDatabasesPage.Title" ); //$NON-NLS-1$

    /** The master details block */
    private DatabasesMasterDetailsBlock masterDetailsBlock;


    /**
     * Creates a new instance of DatabasePage.
     *
     * @param editor the associated editor
     */
    public DatabasesPage( OpenLdapServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        masterDetailsBlock = new DatabasesMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        if ( isInitialized() )
        {
            masterDetailsBlock.refreshUI();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave( IProgressMonitor monitor )
    {
        if ( masterDetailsBlock != null )
        {
            masterDetailsBlock.doSave( monitor );
        }
    }
}
