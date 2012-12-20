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


import org.apache.directory.api.ldap.model.schema.AbstractSchemaObject;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * The SchemaBrowserManager is used to set and get the the input
 * of the single schema browser instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaBrowserManager
{

    /** The dummy input, to find the single schema browser instance */
    private static SchemaBrowserInput DUMMY_INPUT = new SchemaBrowserInput( null, null );


    /**
     * Sets the input to the schema browser.
     *
     * @param connection the connection
     * @param schemaElement the schema element
     */
    public static void setInput( IBrowserConnection connection, AbstractSchemaObject schemaElement )
    {
        SchemaBrowserInput input = new SchemaBrowserInput( connection, schemaElement );
        setInput( input );
    }


    /**
     * Sets the input to the schema browser. 
     *
     * @param input the input
     */
    private static void setInput( SchemaBrowserInput input )
    {
        SchemaBrowser editor = ( SchemaBrowser ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findEditor( DUMMY_INPUT );
        if ( editor == null && input != null )
        {
            // open new schema browser
            try
            {
                editor = ( SchemaBrowser ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor( input, SchemaBrowser.getId(), false );
                editor.setInput( input );
            }
            catch ( PartInitException e )
            {
                e.printStackTrace();
            }
        }
        else if ( editor != null )
        {
            // set the input to already opened schema browser
            editor.setInput( input );

            // bring schema browser to top
            if ( !PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible( editor ) )
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( editor );
            }
        }
    }

}
