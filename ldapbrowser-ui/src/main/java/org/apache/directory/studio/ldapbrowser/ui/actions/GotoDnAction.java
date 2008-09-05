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
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.common.dialogs.DnDialog;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;


/**
 * This action locates a DN that the user entered into a dialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class GotoDnAction extends LocateInDitAction
{
    /**
     * Creates a new instance of LocateDnInDitAction.
     */
    public GotoDnAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Go to DN...";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_DN_IN_DIT );
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
        return getInput() instanceof IBrowserConnection;
    }


    /**
     * This implementation returns a connection and DN if th user put
     * a valid DN into the dialog
     */
    protected ConnectionAndDn getConnectionAndDn()
    {
        if ( getInput() instanceof IBrowserConnection )
        {
            IBrowserConnection conn = ( IBrowserConnection ) getInput();

            LdapDN dn = Utils.getLdapDn( getStringFromClipboard() );

            DnDialog dialog = new DnDialog( getShell(), "Go to DN", "Please enter the DN you want to go to.", conn, dn );
            if ( dialog.open() == TextDialog.OK && dialog.getDn() != null )
            {
                dn = dialog.getDn();
                return new ConnectionAndDn( conn, dn );
            }
        }

        return null;
    }


    private static String getStringFromClipboard()
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            Object contents = clipboard.getContents( TextTransfer.getInstance() );
            if ( contents != null && contents instanceof String )
            {
                return ( String ) contents;
            }
        }
        finally
        {
            if ( clipboard != null )
            {
                clipboard.dispose();
            }
        }
        return null;
    }
}
