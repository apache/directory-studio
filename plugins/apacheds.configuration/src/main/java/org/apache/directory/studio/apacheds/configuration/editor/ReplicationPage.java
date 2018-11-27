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
package org.apache.directory.studio.apacheds.configuration.editor;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * This class represents the General Page of the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationPage extends ServerConfigurationEditorPage
{
    /** The Page ID */
    public static final String ID = ReplicationPage.class.getName();

    /** The Page Title */
    private static final String TITLE = Messages.getString( "ReplicationPage.Replication" ); //$NON-NLS-1$

    /** The Master Details Block */
    private ReplicationMasterDetailsBlock masterDetailsBlock;


    /**
     * Creates a new instance of ReplicationPage.
     *
     * @param editor
     *      the associated editor
     */
    public ReplicationPage( ServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        masterDetailsBlock = new ReplicationMasterDetailsBlock( this );
        masterDetailsBlock.createContent( getManagedForm() );
    }


    /**
     * {@inheritDoc}
     */
    protected void refreshUI()
    {
        if ( isInitialized() )
        {
            masterDetailsBlock.refreshUI();
        }
    }
}
