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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.core.jobs.ExecuteLdifJob;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifEditor;

import org.eclipse.jface.action.Action;


/**
 * This Action executes LDIF code.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExecuteLdifAction extends Action
{
    private LdifEditor editor;


    /**
     * Creates a new instance of ExecuteLdifAction.
     *
     * @param editor
     *      the attached editor
     */
    public ExecuteLdifAction( LdifEditor editor )
    {
        super( "Execute LDIF", BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXECUTE ) );
        super.setToolTipText( "Execute LDIF" );
        this.editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {

        IConnection connection = editor.getConnection();
        String ldif = editor.getLdifModel().toRawString();

        new ExecuteLdifJob( connection, ldif, true ).execute();

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return editor != null && editor.getConnection() != null;
    }
}
