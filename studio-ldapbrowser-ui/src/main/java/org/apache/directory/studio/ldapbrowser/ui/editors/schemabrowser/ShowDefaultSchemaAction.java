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


import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;


/**
 * This action toggles between connection specific schemas and the default schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ShowDefaultSchemaAction extends Action
{
    /** The schema browser */
    private SchemaBrowser schemaBrowser;


    /**
     * Creates a new instance of ShowDefaultSchemaAction.
     *
     * @param schemaBrowser the schema browser
     */
    public ShowDefaultSchemaAction( SchemaBrowser schemaBrowser )
    {
        super( "Show Default Schema", Action.AS_CHECK_BOX );
        super.setToolTipText( "Show Default Schema" );
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_DEFAULT_SCHEMA ) );
        super.setEnabled( true );

        this.schemaBrowser = schemaBrowser;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        this.schemaBrowser.setShowDefaultSchema( isChecked() );
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        this.schemaBrowser = null;
    }

}
