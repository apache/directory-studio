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
package org.apache.directory.ldapstudio.proxy.controller.actions;


import org.apache.directory.ldapstudio.proxy.Activator;
import org.apache.directory.ldapstudio.proxy.view.IImageKeys;
import org.apache.directory.ldapstudio.proxy.view.ProxyView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Disconnect action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DisconnectAction extends Action
{
    /** The associated view */
    private ProxyView view;


    /**
     * Creates a new instance of DisconnectAction.
     * 
     * @param view
     *      the associated view
     */
    public DisconnectAction( ProxyView view )
    {
        super( "Disconnect" );
        setToolTipText( getText() );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.DISCONNECT ) );
        setEnabled( false );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        view.getController().getLdapProxy().disconnect();
        view.getController().setLdapProxy( null );
    }
}
