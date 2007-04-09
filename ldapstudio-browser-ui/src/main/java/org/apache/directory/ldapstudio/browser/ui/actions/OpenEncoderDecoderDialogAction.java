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


import org.apache.directory.ldapstudio.browser.common.actions.BrowserAction;
import org.apache.directory.ldapstudio.browser.ui.dialogs.EncoderDecoderDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;


/**
 * This Action opens the Encoder/Decoder Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenEncoderDecoderDialogAction extends BrowserAction
{
    /**
     * Creates a new instance of OpenEncoderDecoderDialogAction.
     */
    public OpenEncoderDecoderDialogAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        EncoderDecoderDialog dlg = new EncoderDecoderDialog( PlatformUI.getWorkbench().getDisplay().getActiveShell() );
        dlg.open();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Open Encoder/Decoder";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }
}
