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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchResult;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;


/**
 * The cursor implementation for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorCursor extends TableCursor implements ISelectionProvider, EntryUpdateListener
{

    /** The viewer. */
    private TableViewer viewer;

    /** The selection changes listener list. */
    private List<ISelectionChangedListener> selectionChangesListenerList;

    /** The cloned reference copy of the search result under the cursor */
    private ISearchResult referenceCopy;

    /** The cloned working copy of the search result under the cursor */
    private ISearchResult workingCopy;


    /**
     * Creates a new instance of SearchResultEditorCursor.
     * 
     * @param viewer the viewer
     */
    public SearchResultEditorCursor( TableViewer viewer )
    {
        super( viewer.getTable(), SWT.NONE );
        this.viewer = viewer;
        this.selectionChangesListenerList = new ArrayList<ISelectionChangedListener>();

        setBackground( Display.getDefault().getSystemColor( SWT.COLOR_LIST_SELECTION ) );
        setForeground( Display.getDefault().getSystemColor( SWT.COLOR_LIST_SELECTION_TEXT ) );

        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );

        initSelectionChecker();
        initSelectionProvider();
    }


    /**
     * Initializes the selection checker.
     */
    private void initSelectionChecker()
    {
        addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                checkSelection();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
                checkSelection();
            }


            private void checkSelection()
            {
                if ( viewer != null && viewer.getColumnProperties() != null
                    && viewer.getColumnProperties().length - 1 < getColumn() )
                {
                    setSelection( getRow(), viewer.getColumnProperties().length - 1 );
                }
            }
        } );
    }


    /**
     * Initializes the selection provider.
     */
    private void initSelectionProvider()
    {
        addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                for ( Iterator<?> it = selectionChangesListenerList.iterator(); it.hasNext(); )
                {
                    ( ( ISelectionChangedListener ) it.next() ).selectionChanged( new SelectionChangedEvent(
                        SearchResultEditorCursor.this, getSelection() ) );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFocus()
    {
        return super.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        EventRegistry.removeEntryUpdateListener( this );
        viewer = null;
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        viewer.refresh();
        redraw();
    }


    /**
     * Gets the selected property.
     * 
     * @return the selected property
     */
    public String getSelectedProperty()
    {
        if ( !isDisposed() && getRow() != null && viewer != null && viewer.getColumnProperties() != null
            && viewer.getColumnProperties().length >= getColumn() + 1 )
        {
            String property = ( String ) viewer.getColumnProperties()[getColumn()];
            return property;
        }
        return null;
    }


    /**
     * Gets the selected attribute hierarchy.
     * 
     * @return the selected attribute hierarchy
     */
    public AttributeHierarchy getSelectedAttributeHierarchy()
    {
        if ( !isDisposed() && getRow() != null && viewer != null && viewer.getColumnProperties() != null
            && viewer.getColumnProperties().length >= getColumn() + 1 )
        {
            ISearchResult sr = getSelectedSearchResult();
            String property = ( String ) viewer.getColumnProperties()[getColumn()];
            if ( sr != null && !BrowserUIConstants.DN.equals( property ) )
            {
                AttributeHierarchy ah = sr.getAttributeWithSubtypes( property );

                if ( ah == null )
                {
                    ah = new AttributeHierarchy( sr.getEntry(), property, new IAttribute[]
                        { new Attribute( sr.getEntry(), property ) } );
                }

                return ah;
            }
        }
        return null;
    }


    /**
     * Gets the selected search result.
     * 
     * @return the selected search result
     */
    public ISearchResult getSelectedSearchResult()
    {
        if ( !isDisposed() && getRow() != null )
        {
            Object o = getRow().getData();
            if ( o instanceof ISearchResult )
            {
                ISearchResult sr = ( ISearchResult ) o;
                if ( !sr.equals( workingCopy ) )
                {
                    IEntry entry = sr.getEntry();
                    IEntry referenceEntry = new CompoundModification().cloneEntry( entry );
                    referenceCopy = new SearchResult( referenceEntry, sr.getSearch() );
                    IEntry workingEntry = new CompoundModification().cloneEntry( entry );
                    workingCopy = new SearchResult( workingEntry, sr.getSearch() );
                }

                return workingCopy;
            }
        }
        return null;
    }


    /**
     * Gets the selected reference copy.
     * 
     * @return the selected reference copy, may be null
     */
    public ISearchResult getSelectedReferenceCopy()
    {
        return referenceCopy;
    }


    /**
     * Resets reference and working copy copy.
     */
    public void resetCopies()
    {
        referenceCopy = null;
        workingCopy = null;

        // update all actions with the fresh selection
        for ( Iterator<?> it = selectionChangesListenerList.iterator(); it.hasNext(); )
        {
            ( ( ISelectionChangedListener ) it.next() ).selectionChanged( new SelectionChangedEvent(
                SearchResultEditorCursor.this, getSelection() ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener )
    {
        if ( !selectionChangesListenerList.contains( listener ) )
        {
            selectionChangesListenerList.add( listener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public ISelection getSelection()
    {
        ISearchResult searchResult = getSelectedSearchResult();
        AttributeHierarchy ah = getSelectedAttributeHierarchy();
        String property = getSelectedProperty();

        List<Object> list = new ArrayList<Object>();
        if ( searchResult != null )
        {
            list.add( searchResult );
        }
        if ( ah != null )
        {
            list.add( ah );
        }
        if ( property != null )
        {
            list.add( property );
        }

        return new StructuredSelection( list );
    }


    /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener )
    {
        if ( selectionChangesListenerList.contains( listener ) )
        {
            selectionChangesListenerList.remove( listener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setSelection( ISelection selection )
    {
    }

}
