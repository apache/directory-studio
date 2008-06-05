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

package org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs;


import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.eclipse.jface.action.Action;


/**
 * This action is used to toggle the "enable modification logs" preference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EnableModificationLogsAction extends Action
{

    /**
     * Creates a new instance of EnableModificationLogsAction.
     */
    public EnableModificationLogsAction()
    {
        super( "Enable Modification Logs", AS_CHECK_BOX );
        setToolTipText( getText() );
        setEnabled( true );
        setChecked( ConnectionCorePlugin.getDefault().getPluginPreferences().getBoolean(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE, super.isChecked() );
    }

}
