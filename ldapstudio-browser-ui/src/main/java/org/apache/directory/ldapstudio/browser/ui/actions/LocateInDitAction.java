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


import org.apache.directory.ldapstudio.browser.core.jobs.ReadEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public abstract class LocateInDitAction extends BrowserAction
{

    public final void run()
    {
        Object[] connectionAndDn = getConnectionAndDn();
        if ( connectionAndDn != null )
        {
            IConnection connection = ( IConnection ) connectionAndDn[0];
            DN dn = ( DN ) connectionAndDn[1];

            IEntry entry = connection.getEntryFromCache( dn );
            if ( entry == null )
            {
                ReadEntryJob job = new ReadEntryJob( connection, dn );
                RunnableContextJobAdapter.execute( job );
                entry = job.getReadEntry();
            }

            if ( entry != null )
            {
                String targetId = BrowserView.getId();
                IViewPart targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                    targetId );
                if ( targetView == null )
                {
                    try
                    {
                        targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                            targetId, null, IWorkbenchPage.VIEW_ACTIVATE );
                    }
                    catch ( PartInitException e )
                    {
                    }
                }
                if ( targetView != null && targetView instanceof BrowserView )
                {
                    ( ( BrowserView ) targetView ).select( entry );
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate( targetView );
                }
            }
        }
    }


    public String getCommandId()
    {
        return "org.apache.directory.ldapstudio.browser.action.locateInDit";
    }


    public final boolean isEnabled()
    {
        return getConnectionAndDn() != null;
    }


    protected abstract Object[] getConnectionAndDn();

}
