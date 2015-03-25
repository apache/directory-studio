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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.openldap.config.model.OlcLdifConfig;


/**
 * This interface represents a block for None Specific Details.
 */
public class LdifDatabaseSpecificDetailsBlock implements DatabaseSpecificDetailsBlock
{
    /** The database */
    private OlcLdifConfig database;

    // UI Widgets
    private Text directoryText;


    /**
     * Creates a new instance of LdifDatabaseSpecificDetailsBlock.
     * 
     * @param database the database
     */
    public LdifDatabaseSpecificDetailsBlock( OlcLdifConfig database )
    {
        this.database = database;
    }


    /**
     * {@inheritDoc}
     */
    public void createFormContent( Composite parent, FormToolkit toolkit )
    {
        // Directory Text
        toolkit.createLabel( parent, "Directory:" );
        directoryText = toolkit.createText( parent, "" );
        directoryText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {

        if ( database == null )
        {
            // Blank out all fields
            // TODO
        }
        else
        {
            // Directory Text
            String directory = database.getOlcDbDirectory();
            directoryText.setText( ( directory == null ) ? "" : directory ); //$NON-NLS-1$
        }
    }
}
