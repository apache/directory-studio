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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserLabelProvider;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


/**
 * This class implements the Outline Page for the Entry Editor. 
 * It used to display LDAP entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorOutlinePage extends ContentOutlinePage
{
    /** The editor it is attached to */
    private EntryEditor entryEditor;

    /** This listener updates the viewer if an property (e.g. is operational attributes visible) has been changed */
    protected IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
    {
        public void propertyChange( PropertyChangeEvent event )
        {
            refresh();
        }
    };

    private Composite noOutlineComposite;

    private Composite composite;

    private Composite fakeComposite;


    /**
     * Creates a new instance of EntryEditorOutlinePage.
     *
     * @param entryEditor
     *      the editor the Outline page is attached to
     */
    public EntryEditorOutlinePage( EntryEditor entryEditor )
    {
        this.entryEditor = entryEditor;
        BrowserCommonActivator.getDefault().getPreferenceStore().addPropertyChangeListener( propertyChangeListener );
    }


    public Control getControl()
    {
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Creating the composite and fake composite
        this.composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new FillLayout() );
        this.fakeComposite = new Composite( parent, SWT.NONE );

        // Creating the No Outline composite
        noOutlineComposite = new Composite( composite, SWT.NONE );
        noOutlineComposite.setLayout( new FillLayout() );

        Label label = new Label( noOutlineComposite, SWT.WRAP );
        label.setText( Messages.getString( "EntryEditorOutlinePage.NoOutline" ) ); //$NON-NLS-1$

        // Creating the Outline tree viewer
        super.createControl( parent );

        final TreeViewer treeViewer = getTreeViewer();
        treeViewer.setLabelProvider( new EntryEditorOutlineLabelProvider() );
        treeViewer.setContentProvider( new EntryEditorOutlineContentProvider() );

        treeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection )
                {
                    Object o = entryEditor.getMainWidget().getViewer().getInput();
                    if ( o != null && o instanceof IEntry )
                    {
                        List<Object> selectionList = new ArrayList<Object>();

                        for ( Object element : ( ( IStructuredSelection ) event.getSelection() ).toArray() )
                        {
                            if ( element instanceof IValue )
                            {
                                // select the value
                                IValue value = ( IValue ) element;
                                selectionList.add( value );
                            }
                            else if ( element instanceof IAttribute )
                            {
                                // select attribute and all values
                                IAttribute attribute = ( IAttribute ) element;
                                selectionList.add( attribute );
                                selectionList.addAll( Arrays.asList( attribute.getValues() ) );
                            }
                            else if ( element instanceof EntryWrapper )
                            {
                                // select all attributes and values
                                IEntry entry = ( ( EntryWrapper ) element ).entry;
                                for ( IAttribute attribute : entry.getAttributes() )
                                {
                                    selectionList.add( attribute );
                                    selectionList.addAll( Arrays.asList( attribute.getValues() ) );
                                }
                            }
                        }

                        IStructuredSelection selection = new StructuredSelection( selectionList );
                        entryEditor.getMainWidget().getViewer().setSelection( selection );
                    }
                }
            }
        } );

        treeViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                if ( event.getSelection() instanceof IStructuredSelection )
                {
                    Object obj = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();
                    if ( treeViewer.getExpandedState( obj ) )
                    {
                        treeViewer.collapseToLevel( obj, 1 );
                    }
                    else if ( ( ( ITreeContentProvider ) treeViewer.getContentProvider() ).hasChildren( obj ) )
                    {
                        treeViewer.expandToLevel( obj, 1 );
                    }
                }
            }
        } );

        this.refresh();
    }


    /**
     * Refreshes this viewer starting with the given element.
     *
     * @param element
     *      the element
     */
    public void refresh( Object element )
    {
        final TreeViewer treeViewer = getTreeViewer();
        if ( treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed() )
        {
            treeViewer.setFilters( entryEditor.getMainWidget().getViewer().getFilters() );
            treeViewer.refresh( element );
        }
    }


    /**
     * Refreshes this viewer completely with information freshly obtained from this viewer's model.
     */
    public void refresh()
    {
        TreeViewer treeViewer = getTreeViewer();

        if ( hasAnOutline() )
        {
            if ( treeViewer != null )
            {
                Control treeViewerControl = treeViewer.getControl();

                if ( ( treeViewerControl != null ) && ( !treeViewerControl.isDisposed() ) )
                {
                    treeViewerControl.setParent( composite );
                }
            }

            noOutlineComposite.setParent( fakeComposite );
        }
        else
        {
            if ( treeViewer != null )
            {
                Control treeViewerControl = treeViewer.getControl();

                if ( ( treeViewerControl != null ) && ( !treeViewerControl.isDisposed() ) )
                {
                    treeViewerControl.setParent( fakeComposite );
                }
            }

            noOutlineComposite.setParent( composite );
        }

        composite.layout();

        if ( treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed() )
        {
            treeViewer.setFilters( entryEditor.getMainWidget().getViewer().getFilters() );
            if ( !treeViewer.getTree().isEnabled() )
            {
                treeViewer.getTree().setEnabled( true );
            }

            if ( entryEditor != null )
            {
                Object o = entryEditor.getMainWidget().getViewer().getInput();

                if ( o == null )
                {
                    treeViewer.setInput( null );
                    treeViewer.getTree().setEnabled( false );
                }
                else if ( o instanceof IEntry )
                {
                    treeViewer.setInput( o );
                    treeViewer.expandToLevel( 2 );
                }
            }

            treeViewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
        if ( entryEditor != null )
        {
            BrowserCommonActivator.getDefault().getPreferenceStore().removePropertyChangeListener(
                propertyChangeListener );
            entryEditor = null;
        }
    }

    /**
     * This class implements the ContentProvider used for the Entry Editor Outline View.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private static class EntryEditorOutlineContentProvider implements ITreeContentProvider
    {
        /**
         * {@inheritDoc}
         */
        public Object[] getChildren( Object element )
        {
            // entry -> entry wrapper
            // the entry is the input and is not visible, 
            // so we use an wrapper around to make it visible as root element 
            if ( element instanceof IEntry )
            {
                IEntry entry = ( IEntry ) element;
                return new EntryWrapper[]
                    { new EntryWrapper( entry ) };
            }

            // entry wrapper -> attribute
            if ( element instanceof EntryWrapper )
            {
                EntryWrapper entryWrapper = ( EntryWrapper ) element;
                return entryWrapper.entry.getAttributes();
            }

            // attribute -> values
            else if ( element instanceof IAttribute )
            {
                IAttribute attribute = ( IAttribute ) element;
                return attribute.getValues();
            }

            else
            {
                return new Object[0];
            }
        }


        /**
         * {@inheritDoc}
         */
        public Object getParent( Object element )
        {
            return null;
        }


        /**
         * {@inheritDoc}
         */
        public boolean hasChildren( Object element )
        {
            return getChildren( element ) != null && getChildren( element ).length > 0;
        }


        /**
         * {@inheritDoc}
         */
        public Object[] getElements( Object inputElement )
        {
            return getChildren( inputElement );
        }


        /**
         * {@inheritDoc}
         */
        public void dispose()
        {
        }


        /**
         * {@inheritDoc}
         */
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    /**
     * This class implements the LabelProvider used for the Entry Editor Outline View
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private static class EntryEditorOutlineLabelProvider extends LabelProvider
    {
        /**
         * {@inheritDoc}
         */
        public String getText( Object element )
        {
            // Entry
            if ( element instanceof EntryWrapper )
            {
                IEntry entry = ( ( EntryWrapper ) element ).entry;

                // Checking the Root DSE
                if ( entry.getDn() != null && "".equals( entry.getDn().toString() ) ) //$NON-NLS-1$
                {
                    // Root DSE
                    return "Root DSE"; //$NON-NLS-1$
                }
                else
                {
                    // Any other case
                    return entry.getDn().getName();
                }
            }

            // Attribute
            else if ( element instanceof IAttribute )
            {
                IAttribute attribute = ( IAttribute ) element;
                return attribute.getDescription() + " (" + attribute.getValueSize() + ")"; //$NON-NLS-1$  //$NON-NLS-2$
            }

            // Value
            else if ( element instanceof IValue )
            {
                IValue value = ( IValue ) element;
                return Utils.getShortenedString( value.getStringValue(), 20 );
            }

            else
            {
                return ""; //$NON-NLS-1$
            }
        }


        /**
         * {@inheritDoc}
         */
        public Image getImage( Object element )
        {
            // Entry
            if ( element instanceof EntryWrapper )
            {
                IEntry entry = ( ( EntryWrapper ) element ).entry;

                // Checking the Root DSE
                if ( entry.getDn() != null && "".equals( entry.getDn().toString() ) ) //$NON-NLS-1$
                {
                    // Root DSE
                    return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ROOT );
                }
                else
                {
                    // Any other case
                    return BrowserLabelProvider.getImageByObjectClass( entry );
                }
            }

            // Attribute
            else if ( element instanceof IAttribute )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_ATTRIBUTE );
            }

            // Value
            else if ( element instanceof IValue )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_VALUE );
            }

            else
            {
                return null;
            }
        }
    }

    /**
     * Wrapper around an entry.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private static class EntryWrapper
    {
        IEntry entry;


        public EntryWrapper( IEntry entry )
        {
            super();
            this.entry = entry;
        }


        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( entry == null ) ? 0 : entry.hashCode() );
            return result;
        }


        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            EntryWrapper other = ( EntryWrapper ) obj;
            if ( entry == null )
            {
                if ( other.entry != null )
                    return false;
            }
            else if ( !entry.equals( other.entry ) )
                return false;
            return true;
        }
    }


    /**
     * Indicates if the entry has an outline.
     *
     * @return <code>true</code> if the entry editor has an outline,
     *         <code>false</code> if not.
     */
    public boolean hasAnOutline()
    {
        Object o = entryEditor.getMainWidget().getViewer().getInput();

        return ( ( o != null ) && ( o instanceof IEntry ) );
    }
}
