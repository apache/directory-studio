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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemasJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;


/**
 * This action reloads the schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReloadSchemaAction extends Action
{

    /** The schema page */
    private SchemaPage schemaPage;


    /**
     * Creates a new instance of ReloadSchemaAction.
     *
     * @param schemaPage the schema page
     */
    public ReloadSchemaAction( SchemaPage schemaPage )
    {
        super( "Reload Schema" );
        super.setToolTipText( "Reload Schema" );
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_REFRESH ) );
        super.setEnabled( true );

        this.schemaPage = schemaPage;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        final IBrowserConnection connection = schemaPage.getConnection();
        if ( connection != null )
        {
            new ReloadSchemasJob( new IBrowserConnection[]
                { connection } ).execute();
            schemaPage.getSchemaBrowser().refresh();
        }
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        schemaPage = null;
    }


    /**
     * Updates the enabled state.
     */
    public void updateEnabledState()
    {
        setEnabled( schemaPage.getConnection() != null && !schemaPage.isShowDefaultSchema() );
    }

}
