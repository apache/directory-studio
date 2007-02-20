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


import org.apache.directory.ldapstudio.proxy.view.LdapMessageView;
import org.eclipse.jface.action.IToolBarManager;


/**
 * This class implements the controller for the LDAP Message View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapMessageViewController
{
    /** The associated view */
    private LdapMessageView view;


    /**
     * Creates a new instance of LdapMessageViewController.
     *
     * @param view
     *      the associated view
     */
    public LdapMessageViewController( LdapMessageView view )
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
    }


    /**
     * Initializes the toolbar.
     */
    private void initToolbar()
    {
    }
}
