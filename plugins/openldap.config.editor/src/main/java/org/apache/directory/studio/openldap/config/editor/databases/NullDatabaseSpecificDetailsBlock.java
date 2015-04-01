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


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.model.OlcNullConfig;


/**
 * This interface represents a block for Null Specific Details.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NullDatabaseSpecificDetailsBlock extends AbstractDatabaseSpecificDetailsBlock<OlcNullConfig>
{
    // UI Widgets
    private Button allowBindCheckbox;


    /**
     * Creates a new instance of NullDatabaseSpecificDetailsBlock.
     * 
     * @param detailsPage the details page
     * @param database the database
     */
    public NullDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, OlcNullConfig database )
    {
        super( detailsPage, database );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit )
    {
        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Allow Bind
        allowBindCheckbox = toolkit.createButton( composite, "Allow bind to the database", SWT.CHECK );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( database != null )
        {
            // Allow Bind
            Boolean allowBind = database.getOlcDbBindAllowed();

            if ( allowBind != null )
            {
                allowBindCheckbox.setSelection( allowBind );
            }
            else
            {
                allowBindCheckbox.setSelection( false );
            }
        }

        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        allowBindCheckbox.addSelectionListener( dirtySelectionListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        allowBindCheckbox.removeSelectionListener( dirtySelectionListener );
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( database != null )
        {
            // Allow Bind
            database.setOlcDbBindAllowed( allowBindCheckbox.getSelection() );
        }
    }
}
