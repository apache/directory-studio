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


import org.apache.directory.ldapstudio.browser.core.jobs.OpenConnectionsJob;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;


public class OpenConnectionAction extends BrowserAction
{

    public OpenConnectionAction()
    {
        super();
    }


    public void run()
    {
        OpenConnectionsJob ocj = new OpenConnectionsJob( getSelectedConnections() );
        ocj.execute();
    }


    public String getText()
    {
        return getSelectedConnections().length > 1 ? "Open Connections" : "Open Connection";
    }


    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_CONNECTION_CONNECT );
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        boolean canOpen = false;
        for ( int i = 0; i < getSelectedConnections().length; i++ )
        {
            if ( getSelectedConnections()[i].canOpen() )
            {
                canOpen = true;
                break;
            }
        }
        return getSelectedConnections().length > 0 && canOpen;
    }

}
