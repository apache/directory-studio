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


import org.apache.directory.ldapstudio.browser.core.events.BookmarkUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.ConnectionUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IBookmark;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserCategory;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserEntryPage;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserSearchResultPage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public abstract class BrowserAction implements IWorkbenchWindowActionDelegate
{

    private IConnection[] selectedConnections;

    private BrowserCategory[] selectedBrowserViewCategories;

    private IEntry[] selectedEntries;

    private BrowserEntryPage[] selectedBrowserEntryPages;

    private ISearch[] selectedSearches;

    private ISearchResult[] selectedSearchResults;

    private BrowserSearchResultPage[] selectedBrowserSearchResultPages;

    private IBookmark[] selectedBookmarks;

    private IAttribute[] selectedAttributes;

    private AttributeHierarchy[] selectedAttributeHierarchies;

    private IValue[] selectedValues;

    private LdifFile selectedLdifModel;

    private LdifContainer[] selectedLdifContainers;

    private LdifPart[] selectedLdifParts;

    private Object input;


    protected BrowserAction()
    {
        this.init();
    }


    public void init( IWorkbenchWindow window )
    {
        this.init();
    }


    public void run( IAction action )
    {
        this.run();
    }


    public void selectionChanged( IAction action, ISelection selection )
    {
        setSelectedConnections( SelectionUtils.getConnections( selection ) );

        setSelectedBrowserViewCategories( SelectionUtils.getBrowserViewCategories( selection ) );
        setSelectedEntries( SelectionUtils.getEntries( selection ) );
        setSelectedBrowserEntryPages( SelectionUtils.getBrowserEntryPages( selection ) );
        setSelectedSearchResults( SelectionUtils.getSearchResults( selection ) );
        setSelectedBrowserSearchResultPages( SelectionUtils.getBrowserSearchResultPages( selection ) );
        setSelectedBookmarks( SelectionUtils.getBookmarks( selection ) );

        setSelectedSearches( SelectionUtils.getSearches( selection ) );

        setSelectedAttributes( SelectionUtils.getAttributes( selection ) );
        setSelectedAttributeHierarchies( SelectionUtils.getAttributeHierarchie( selection ) );
        setSelectedValues( SelectionUtils.getValues( selection ) );

        action.setEnabled( this.isEnabled() );
        action.setText( this.getText() );
        action.setToolTipText( this.getText() );
    }


    public abstract String getText();


    public abstract ImageDescriptor getImageDescriptor();


    public abstract String getCommandId();


    public abstract boolean isEnabled();


    public abstract void run();


    private void init()
    {
        this.selectedConnections = new IConnection[0];
        this.selectedBrowserViewCategories = new BrowserCategory[0];
        this.selectedEntries = new IEntry[0];
        this.selectedBrowserEntryPages = new BrowserEntryPage[0];
        this.selectedSearches = new ISearch[0];
        this.selectedSearchResults = new ISearchResult[0];
        this.selectedBrowserSearchResultPages = new BrowserSearchResultPage[0];
        this.selectedBookmarks = new IBookmark[0];
        this.selectedAttributes = new IAttribute[0];
        this.selectedAttributeHierarchies = new AttributeHierarchy[0];
        this.selectedValues = new IValue[0];

        this.selectedLdifModel = null;
        this.selectedLdifContainers = new LdifContainer[0];
        this.selectedLdifParts = new LdifPart[0];

        this.input = null;
    }


    public void dispose()
    {
        this.selectedConnections = new IConnection[0];
        this.selectedBrowserViewCategories = new BrowserCategory[0];
        this.selectedEntries = new IEntry[0];
        this.selectedBrowserEntryPages = new BrowserEntryPage[0];
        this.selectedSearches = new ISearch[0];
        this.selectedSearchResults = new ISearchResult[0];
        this.selectedBrowserSearchResultPages = new BrowserSearchResultPage[0];
        this.selectedBookmarks = new IBookmark[0];
        this.selectedAttributes = new IAttribute[0];
        this.selectedAttributeHierarchies = new AttributeHierarchy[0];
        this.selectedValues = new IValue[0];

        this.selectedLdifModel = null;
        this.selectedLdifContainers = new LdifContainer[0];
        this.selectedLdifParts = new LdifPart[0];

        this.input = null;
    }


    protected Shell getShell()
    {
        return PlatformUI.getWorkbench().getDisplay().getActiveShell();
    }


    public final void entryUpdated( EntryModificationEvent event )
    {
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
    }


    public void bookmarkUpdated( BookmarkUpdateEvent bookmarkUpdateEvent )
    {
    }


    public final void connectionUpdated( ConnectionUpdateEvent connectionUpdateEvent )
    {
    }


    public IAttribute[] getSelectedAttributes()
    {
        return selectedAttributes;
    }


    public void setSelectedAttributes( IAttribute[] selectedAttributes )
    {
        this.selectedAttributes = selectedAttributes;
    }


    public IBookmark[] getSelectedBookmarks()
    {
        return selectedBookmarks;
    }


    public void setSelectedBookmarks( IBookmark[] selectedBookmarks )
    {
        this.selectedBookmarks = selectedBookmarks;
    }


    public BrowserCategory[] getSelectedBrowserViewCategories()
    {
        return selectedBrowserViewCategories;
    }


    public void setSelectedBrowserViewCategories( BrowserCategory[] selectedBrowserViewCategories )
    {
        this.selectedBrowserViewCategories = selectedBrowserViewCategories;
    }


    public IConnection[] getSelectedConnections()
    {
        return selectedConnections;
    }


    public void setSelectedConnections( IConnection[] selectedConnections )
    {
        this.selectedConnections = selectedConnections;
    }


    public IEntry[] getSelectedEntries()
    {
        return selectedEntries;
    }


    public void setSelectedEntries( IEntry[] selectedEntries )
    {
        this.selectedEntries = selectedEntries;
    }


    public ISearch[] getSelectedSearches()
    {
        return selectedSearches;
    }


    public void setSelectedSearches( ISearch[] selectedSearches )
    {
        this.selectedSearches = selectedSearches;
    }


    public ISearchResult[] getSelectedSearchResults()
    {
        return selectedSearchResults;
    }


    public void setSelectedSearchResults( ISearchResult[] selectedSearchResults )
    {
        this.selectedSearchResults = selectedSearchResults;
    }


    public IValue[] getSelectedValues()
    {
        return selectedValues;
    }


    public void setSelectedValues( IValue[] selectedValues )
    {
        this.selectedValues = selectedValues;
    }


    public Object getInput()
    {
        return input;
    }


    public void setInput( Object input )
    {
        this.input = input;
    }


    public LdifContainer[] getSelectedLdifContainers()
    {
        return selectedLdifContainers;
    }


    public void setSelectedLdifContainers( LdifContainer[] selectedLdifContainers )
    {
        this.selectedLdifContainers = selectedLdifContainers;
    }


    public LdifFile getSelectedLdifModel()
    {
        return selectedLdifModel;
    }


    public void setSelectedLdifModel( LdifFile selectedLdifModel )
    {
        this.selectedLdifModel = selectedLdifModel;
    }


    public LdifPart[] getSelectedLdifParts()
    {
        return selectedLdifParts;
    }


    public void setSelectedLdifParts( LdifPart[] selectedLdifParts )
    {
        this.selectedLdifParts = selectedLdifParts;
    }


    public BrowserEntryPage[] getSelectedBrowserEntryPages()
    {
        return selectedBrowserEntryPages;
    }


    public void setSelectedBrowserEntryPages( BrowserEntryPage[] selectedBrowserEntryPages )
    {
        this.selectedBrowserEntryPages = selectedBrowserEntryPages;
    }


    public BrowserSearchResultPage[] getSelectedBrowserSearchResultPages()
    {
        return selectedBrowserSearchResultPages;
    }


    public void setSelectedBrowserSearchResultPages( BrowserSearchResultPage[] selectedBrowserSearchResultPages )
    {
        this.selectedBrowserSearchResultPages = selectedBrowserSearchResultPages;
    }


    public AttributeHierarchy[] getSelectedAttributeHierarchies()
    {
        return selectedAttributeHierarchies;
    }


    public void setSelectedAttributeHierarchies( AttributeHierarchy[] ahs )
    {
        this.selectedAttributeHierarchies = ahs;
    }

}
