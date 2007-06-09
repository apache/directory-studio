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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.actions.SelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;


public abstract class AbstractSearchResultListenerAction extends Action implements ISelectionChangedListener,
    EntryUpdateListener
{

    protected ISelectionProvider selectionProvider;

    protected ISearch selectedSearch;

    protected ISearchResult selectedSearchResult;

    protected AttributeHierarchy selectedAttributeHierarchie;

    protected String selectedProperty;


    AbstractSearchResultListenerAction( ISelectionProvider selectionProvider, String title, ImageDescriptor image,
        String command, int style )
    {
        super( title, style );
        super.setText( title );
        super.setToolTipText( title );
        super.setImageDescriptor( image );
        super.setActionDefinitionId( command );
        super.setEnabled( false );

        this.selectionProvider = selectionProvider;

        this.init();
    }


    AbstractSearchResultListenerAction( ISelectionProvider selectionProvider, String title, ImageDescriptor image,
        String command )
    {
        this( selectionProvider, title, image, command, Action.AS_PUSH_BUTTON );
    }


    private void init()
    {
        this.selectionProvider.addSelectionChangedListener( this );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        this.selectedSearch = null;
        this.selectedSearchResult = null;
        this.selectedAttributeHierarchie = null;
        this.selectedProperty = null;
    }


    public void selectionChanged( SelectionChangedEvent event )
    {

        ISelection selection = event.getSelection();

        ISearchResult[] searchResults = SelectionUtils.getSearchResults( selection );
        AttributeHierarchy[] ah = SelectionUtils.getAttributeHierarchie( selection );
        String[] selectedProperties = SelectionUtils.getProperties( selection );

        if ( searchResults.length == 1 )
        {
            this.selectedSearchResult = searchResults[0];
            this.selectedSearch = this.selectedSearchResult.getSearch();
        }
        else
        {
            this.selectedSearchResult = null;
            this.selectedSearch = null;
        }

        if ( ah.length == 1 )
        {
            this.selectedAttributeHierarchie = ah[0];
        }
        else
        {
            this.selectedAttributeHierarchie = null;
        }

        if ( selectedProperties.length == 1 )
        {
            this.selectedProperty = selectedProperties[0];
        }
        else
        {
            this.selectedProperty = null;
        }

        this.updateEnabledState();
    }


    public final void entryUpdated( EntryModificationEvent event )
    {
        this.updateEnabledState();
    }


    protected abstract void updateEnabledState();


    public void dispose()
    {
        EventRegistry.removeEntryUpdateListener( this );
        this.selectionProvider.removeSelectionChangedListener( this );

        this.selectedSearchResult = null;
        this.selectedAttributeHierarchie = null;
        this.selectedSearch = null;
        this.selectedProperty = null;
    }

}
