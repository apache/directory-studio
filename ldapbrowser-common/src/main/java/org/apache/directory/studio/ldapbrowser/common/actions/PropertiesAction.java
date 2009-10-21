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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.connection.core.Utils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
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
        return Messages.getString( "PropertiesAction.Properties" ); //$NON-NLS-1$
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

        return getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length
            + getSelectedSearches().length == 1
            || getSelectedAttributes().length + getSelectedValues().length == 1
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
            pageId = BrowserCommonConstants.PROP_VALUE;
            title = getSelectedValues()[0].toString();
        }
        else if ( getSelectedAttributes().length == 1 )
        {
            element = ( IAdaptable ) getSelectedAttributes()[0];
            pageId = BrowserCommonConstants.PROP_ATTRIBUTE;
            title = getSelectedAttributes()[0].toString();
        }
        else if ( getSelectedAttributeHierarchies().length == 1 )
        {
            IAttribute att = getSelectedAttributeHierarchies()[0].getAttribute();
            element = att;
            pageId = BrowserCommonConstants.PROP_ATTRIBUTE;
            title = att.toString();
        }
        else if ( getSelectedSearches().length == 1 )
        {
            element = ( IAdaptable ) getSelectedSearches()[0];
            pageId = BrowserCommonConstants.PROP_SEARCH;
            title = getSelectedSearches()[0].getName();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            element = ( IAdaptable ) getSelectedBookmarks()[0];
            pageId = BrowserCommonConstants.PROP_BOOKMARK;
            title = getSelectedBookmarks()[0].getName();
        }
        else if ( getSelectedEntries().length == 1 )
        {
            element = ( IAdaptable ) getSelectedEntries()[0];
            pageId = BrowserCommonConstants.PROP_ENTRY;
            title = getSelectedEntries()[0].getDn().getUpName();
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            element = ( IAdaptable ) getSelectedSearchResults()[0];
            pageId = BrowserCommonConstants.PROP_ENTRY;
            title = getSelectedSearchResults()[0].getDn().getUpName();
        }

        if ( element != null )
        {
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( getShell(), element, pageId, null, null );
            if ( dialog != null )
            {
                title = Utils.shorten( title, 30 );
            }

            dialog.getShell().setText( NLS.bind( Messages.getString( "PropertiesAction.PropertiesForX" ), title ) ); //$NON-NLS-1$
            dialog.open();

        }
    }
}
