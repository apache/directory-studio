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


import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action opens the Property Dialog for a given object.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PropertiesAction extends BrowserAction
{
    /**
     * Creates a new instance of PropertiesAction.
     */
    public PropertiesAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Properties";
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
        return IWorkbenchActionDefinitionIds.PROPERTIES;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {

        return getSelectedConnections().length == 1
            || getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length
                + getSelectedSearches().length == 1 || getSelectedAttributes().length + getSelectedValues().length == 1
            || ( getSelectedAttributeHierarchies().length == 1 && getSelectedAttributeHierarchies()[0].size() == 1 );

    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {

        IAdaptable element = null;
        String pageId = null;
        String title = null;

        if ( getSelectedValues().length == 1 )
        {
            element = ( IAdaptable ) getSelectedValues()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.ValuePropertyPage";
            title = getSelectedValues()[0].toString();
        }
        else if ( getSelectedAttributes().length == 1 )
        {
            element = ( IAdaptable ) getSelectedAttributes()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.AttributePropertyPage";
            title = getSelectedAttributes()[0].toString();
        }
        else if ( getSelectedAttributeHierarchies().length == 1 )
        {
            IAttribute att = getSelectedAttributeHierarchies()[0].getAttribute();
            element = att;
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.AttributePropertyPage";
            title = att.toString();
        }
        else if ( getSelectedSearches().length == 1 )
        {
            element = ( IAdaptable ) getSelectedSearches()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.SearchPropertyPage";
            title = getSelectedSearches()[0].getName();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            element = ( IAdaptable ) getSelectedBookmarks()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.BookmarkPropertyPage";
            title = getSelectedBookmarks()[0].getName();
        }
        else if ( getSelectedEntries().length == 1 )
        {
            element = ( IAdaptable ) getSelectedEntries()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.EntryPropertyPage";
            title = getSelectedEntries()[0].getDn().toString();
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            element = ( IAdaptable ) getSelectedSearchResults()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.EntryPropertyPage";
            title = getSelectedSearchResults()[0].getDn().toString();
        }
        else if ( getSelectedConnections().length == 1 )
        {
            element = ( IAdaptable ) getSelectedConnections()[0];
            pageId = "org.apache.directory.ldapstudio.browser.ui.dialogs.properties.ConnectionPropertyPage";
            title = getSelectedConnections()[0].getName();
        }

        if ( element != null )
        {
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( getShell(), element, pageId, null, null );
            if ( dialog != null )
                title = Utils.shorten( title, 30 );
            dialog.getShell().setText( "Properties for '" + title + "'" );
            dialog.open();

        }
    }
}
