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

package org.apache.directory.studio.ldapservers.model;


import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;


/**
 * The {@link UnknownLdapServerAdapterExtension} class represents an extension to the 
 * LDAP Server Adapters extension point that can not be found while parsing the server instances file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnknownLdapServerAdapterExtension extends LdapServerAdapterExtension
{
    /**
     * Creates a new instance of UnknownLdapServerAdapterExtension.
     */
    public UnknownLdapServerAdapterExtension()
    {
        // Setting behavior for this particular LDAP Server Adapter Extension
        setInstance( new LdapServerAdapter()
        {
            /**
             * {@inheritDoc}
             */
            public void add( LdapServer server, StudioProgressMonitor monitor ) throws Exception
            {
                showWarningDialog();
            }


            /**
             * {@inheritDoc}
             */
            public void delete( LdapServer server, StudioProgressMonitor monitor ) throws Exception
            {
                // Nothing to do
            }


            /**
             * {@inheritDoc}
             */
            public void openConfiguration( LdapServer server, StudioProgressMonitor monitor ) throws Exception
            {
                showWarningDialog();
            }


            /**
             * {@inheritDoc}
             */
            public void start( LdapServer server, StudioProgressMonitor monitor ) throws Exception
            {
                showWarningDialog();

                server.setStatus( LdapServerStatus.STOPPED );
            }


            /**
             * {@inheritDoc}
             */
            public void stop( LdapServer server, StudioProgressMonitor monitor ) throws Exception
            {
                showWarningDialog();

                server.setStatus( LdapServerStatus.STOPPED );
            }


            /**
             * Shows the warning dialog.
             */
            private void showWarningDialog()
            {
                Display.getDefault().asyncExec( new Runnable()
                {
                    public void run()
                    {
                        CommonUIUtils.openWarningDialog(
                            "Server Adapter Not Available",
                            NLS.bind(
                                "This server was created with a server adapter which is no longer available. You need install it (again) using the update site of the vendor. \n\nServer adapter information: ID=''{0}'', Name=''{1}'', Vendor=''{2}'', Version=''{3}''",
                                new String[]
                                    { getId(), getName(), getVendor(), getVersion() } ) );
                    }
                } );
            }


            /**
             * {@inheritDoc}
             */
            public String[] checkPortsBeforeServerStart( LdapServer server )
            {
                return new String[0];
            }
        } );

    }
}
