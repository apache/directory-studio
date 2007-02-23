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
package org.apache.directory.ldapstudio.proxy.controller;


import org.apache.directory.ldapstudio.proxy.controller.actions.ConnectAction;
import org.apache.directory.ldapstudio.proxy.controller.actions.DisconnectAction;
import org.apache.directory.ldapstudio.proxy.model.LdapProxy;
import org.apache.directory.ldapstudio.proxy.view.ProxyView;
import org.eclipse.jface.action.IToolBarManager;


/**
 * This class implements the controller for the Proxy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProxyViewController
{
    /** The associated view */
    private ProxyView view;

    /** The LDAP Proxy */
    private LdapProxy ldapProxy;

    // Actions
    private ConnectAction connect;
    private DisconnectAction disconnect;


    /**
     * Creates a new instance of ProxyViewController.
     *
     * @param view
     *      the associated view
     */
    public ProxyViewController( ProxyView view )
    {
        this.view = view;

        initActions();
        initToolbar();
    }


    /**
     * Initializes the actions.
     */
    private void initActions()
    {
        connect = new ConnectAction( view );
        disconnect = new DisconnectAction( view );
    }


    /**
     * Initializes the toolbar.
     */
    private void initToolbar()
    {
        IToolBarManager toolbar = view.getViewSite().getActionBars().getToolBarManager();
        toolbar.add( connect );
        toolbar.add( disconnect );
    }


    /**
     * Gets the LDAP Proxy.
     *
     * @return
     *      the LDAP Proxy
     */
    public LdapProxy getLdapProxy()
    {
        return ldapProxy;
    }


    /**
     * Sets the LDAP Proxy.
     *
     * @param ldapProxy
     *      the LDAP Proxy to set
     */
    public void setLdapProxy( LdapProxy ldapProxy )
    {
        this.ldapProxy = ldapProxy;
        updateActions();
    }


    /**
     * Enables/Disables Actions.
     */
    private void updateActions()
    {
        if ( ldapProxy == null )
        {
            connect.setEnabled( true );
            disconnect.setEnabled( false );
        }
        else
        {
            connect.setEnabled( false );
            disconnect.setEnabled( true );
        }
    }
}
