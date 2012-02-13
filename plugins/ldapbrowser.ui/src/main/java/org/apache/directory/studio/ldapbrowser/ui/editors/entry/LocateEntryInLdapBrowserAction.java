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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This action is used to locate the input entry in the LDAP Browser DIT.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LocateEntryInLdapBrowserAction extends Action
{
    /** The entry editor */
    protected EntryEditor entryEditor;

    /** The menu manager */
    protected EntryEditorShowInMenuManager showInMenuManager;


    /**
     * Creates a new instance of LocateEntryInLdapBrowserAction.
     *
     * @param entryEditor the entry editor
     * @param showInMenuManager the menu manager
     */
    public LocateEntryInLdapBrowserAction( EntryEditor entryEditor, EntryEditorShowInMenuManager showInMenuManager )
    {
        super();
        this.entryEditor = entryEditor;
        this.showInMenuManager = showInMenuManager;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( entryEditor != null )
        {
            EntryEditorInput editorInput = entryEditor.getEntryEditorInput();

            if ( editorInput != null )
            {
                IEntry entry = editorInput.getResolvedEntry();

                if ( entry != null )
                {
                    select( entry );
                }
            }
        }
    }


    /**
     * Select the object in the LDAP Browser.
     *
     * @param o the object
     */
    protected void select( Object o )
    {
        String targetId = BrowserView.getId();
        IViewPart targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
            targetId );

        if ( targetView == null )
        {
            try
            {
                targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                    targetId, null, IWorkbenchPage.VIEW_ACTIVATE );
            }
            catch ( PartInitException e )
            {
            }
        }

        if ( targetView != null && targetView instanceof BrowserView )
        {
            ( ( BrowserView ) targetView ).select( o );
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate( targetView );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "LocateEntryInLdapBrowserAction.LDAPBrowser" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return getText();
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_ENTRY_IN_DIT );
    }
}
