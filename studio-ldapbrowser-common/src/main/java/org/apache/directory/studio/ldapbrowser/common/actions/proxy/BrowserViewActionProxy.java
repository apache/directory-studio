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

package org.apache.directory.studio.ldapbrowser.common.actions.proxy;


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.eclipse.jface.viewers.Viewer;


/**
 * The BrowserViewActionProxy is a proxy for a real action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserViewActionProxy extends BrowserActionProxy
{

    /**
     * Creates a new instance of BrowserViewActionProxy.
     * 
     * @param viewer the viewer
     * @param actionHandlerManager the action handler manager, 
     *        used to deactivate and activate the action handlers and key bindings
     * @param action the real action
     */
    public BrowserViewActionProxy( Viewer viewer, ActionHandlerManager actionHandlerManager, BrowserAction action )
    {
        super( viewer, actionHandlerManager, action );
    }

}
