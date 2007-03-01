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


import java.io.IOException;

import org.apache.directory.ldapstudio.proxy.Activator;
import org.apache.directory.ldapstudio.proxy.model.LdapProxy;
import org.apache.directory.ldapstudio.proxy.view.IImageKeys;
import org.apache.directory.ldapstudio.proxy.view.ProxyView;
import org.apache.directory.ldapstudio.proxy.view.wizards.ConnectWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Connect action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectAction extends Action
{
    /** The associated view */
    private ProxyView view;


    /**
     * Creates a new instance of ConnectAction.
     *
     * @param view
     *      the associated view
     */
    public ConnectAction( ProxyView view )
    {
        super( "Connect" );
        setToolTipText( getText() );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.CONNECT ) );
        setEnabled( true );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        ConnectWizard connectWizard = new ConnectWizard();

        WizardDialog dialog = new WizardDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            connectWizard );
        dialog.create();
        if ( dialog.open() == Window.OK )
        {
            LdapProxy ldapProxy = new LdapProxy( connectWizard.getLocalPort(), connectWizard.getRemoteHost(),
                connectWizard.getRemotePort() );
            view.setLdapProxy( ldapProxy );
            try
            {
                ldapProxy.connect();
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
