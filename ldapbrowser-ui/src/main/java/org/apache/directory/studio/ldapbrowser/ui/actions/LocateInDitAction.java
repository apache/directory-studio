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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This action is used to locate and open an entry by its DN in DIT.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class LocateInDitAction extends BrowserAction
{
    /**
     * {@inheritDoc}
     */
    public final void run()
    {
        ConnectionAndDn connectionAndDn = getConnectionAndDn();
        if ( connectionAndDn != null )
        {
            IBrowserConnection connection = connectionAndDn.connection;
            LdapDN dn = connectionAndDn.dn;

            IEntry entry = connection.getEntryFromCache( dn );
            if ( entry == null )
            {
                ReadEntryRunnable runnable = new ReadEntryRunnable( connection, dn );
                RunnableContextRunner.execute( runnable, null, true );
                entry = runnable.getReadEntry();
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


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.apache.directory.studio.ldapbrowser.action.locateInDit";
    }


    /**
     * {@inheritDoc}
     */
    public final boolean isEnabled()
    {
        return getConnectionAndDn() != null;
    }


    /**
     * Get the connection and DN to open.
     * 
     * @return a ConnectionAndDn bean, or null.
     */
    protected abstract ConnectionAndDn getConnectionAndDn();

    
    /**
     * Inner class to get connection and DN of the entry to locate.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    protected class ConnectionAndDn
    {
        /** The connection */
        private IBrowserConnection connection;

        /** The DN */
        private LdapDN dn;


        /**
         * Creates a new instance of ConnectionAndDn.
         *
         * @param connection the connection
         * @param dn the DN
         */
        protected ConnectionAndDn( IBrowserConnection connection, LdapDN dn )
        {
            this.connection = connection;
            this.dn = dn;
        }
    }
}
