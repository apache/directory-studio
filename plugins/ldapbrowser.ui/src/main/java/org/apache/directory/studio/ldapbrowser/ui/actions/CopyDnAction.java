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


import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This class implements the Copy Drag'n'Drop Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CopyDnAction extends BrowserAction
{

    /**
     * Creates a new instance of CopyDnAction.
     */
    public CopyDnAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        String dn = null;
        if ( getSelectedEntries().length > 0 )
        {
            dn = getSelectedEntries()[0].getDn().getUpName();
        }
        else if ( getSelectedAttributes().length > 0 )
        {
            dn = getSelectedAttributes()[0].getEntry().getDn().getUpName();
        }
        else if ( getSelectedAttributeHierarchies().length > 0 )
        {
            dn = getSelectedAttributeHierarchies()[0].getAttribute().getEntry().getDn().getUpName();
        }
        else if ( getSelectedValues().length > 0 )
        {
            dn = getSelectedValues()[0].getAttribute().getEntry().getDn().getUpName();
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            dn = getSelectedSearchResults()[0].getDn().getUpName();
        }
        else if ( getSelectedBookmarks().length > 0 )
        {
            dn = getSelectedBookmarks()[0].getDn().getUpName();
        }

        if ( dn != null )
        {
            CopyAction.copyToClipboard( new Object[]
                { dn }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "CopyDnAction.CopyDN" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_DN );
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
        return getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 1
            || getSelectedAttributes().length + getSelectedValues().length > 0;
    }
}
