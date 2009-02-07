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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.actions.CopyEntryAsLdifAction;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


/**
 * This class implements the Outline Page for the Entry Editor. 
 * It used to display LDAP entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorOutlinePage extends ContentOutlinePage
{
    /** The editor it is attached to */
    private EntryEditor entryEditor;


    /**
     * Creates a new instance of EntryEditorOutlinePage.
     *
     * @param entryEditor
     *      the editor the Outline page is attached to
     */
    public EntryEditorOutlinePage( EntryEditor entryEditor )
    {
        this.entryEditor = entryEditor;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        super.createControl( parent );

        final TreeViewer treeViewer = getTreeViewer();
        treeViewer.setLabelProvider( new LdifLabelProvider() );
        treeViewer.setContentProvider( new LdifContentProvider() );
        // treeViewer.setAutoExpandLevel(1);

        treeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                if ( !event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection )
                {

                    Object o = entryEditor.getMainWidget().getViewer().getInput();
                    if ( o != null && o instanceof IEntry )
                    {
                        IEntry entry = ( IEntry ) o;
                        IAttribute[] attributes = entry.getAttributes();

                        List<Object> selectionList = new ArrayList<Object>();

                        Iterator<?> it = ( ( IStructuredSelection ) event.getSelection() ).iterator();
                        while ( it.hasNext() )
                        {
                            Object element = it.next();

                            if ( element instanceof LdifAttrValLine )
                            {
                                // select the value
                                LdifAttrValLine line = ( LdifAttrValLine ) element;
                                for ( IAttribute attribute : attributes )
                                {
                                    if ( attribute.getDescription().equals( line.getUnfoldedAttributeDescription() ) )
                                    {
                                        for ( IValue value : attribute.getValues() )
                                        {
                                            if ( value.getStringValue().equals( line.getValueAsString() ) )
                                            {
                                                selectionList.add( value );
                                            }
                                        }
                                    }
                                }
                            }
                            else if ( element instanceof List )
                            {
                                // select attribute and all values
                                List<?> list = ( List<?> ) element;
                                if ( !list.isEmpty() && list.get( 0 ) instanceof LdifAttrValLine )
                                {
                                    LdifAttrValLine line = ( LdifAttrValLine ) list.get( 0 );
                                    for ( IAttribute attribute : attributes )
                                    {
                                        if ( attribute.getDescription().equals( line.getUnfoldedAttributeDescription() ) )
                                        {
                                            selectionList.add( attribute );
                                            selectionList.addAll( Arrays.asList( attribute.getValues() ) );
                                        }
                                    }
                                }
                            }
                            else if ( element instanceof LdifRecord )
                            {
                                for ( IAttribute attribute : attributes )
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
                        treeViewer.collapseToLevel( obj, 1 );
                    else if ( ( ( ITreeContentProvider ) treeViewer.getContentProvider() ).hasChildren( obj ) )
                        treeViewer.expandToLevel( obj, 1 );
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
            treeViewer.refresh( element );
        }
    }


    /**
     * Refreshes this viewer completely with information freshly obtained from this viewer's model.
     */
    public void refresh()
    {
        final TreeViewer treeViewer = getTreeViewer();

        if ( treeViewer != null && treeViewer.getTree() != null && !treeViewer.getTree().isDisposed() )
        {
            // ISelection selection = treeViewer.getSelection();
            // Object[] expandedElements = treeViewer.getExpandedElements();

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
                else
                {
                    if ( o instanceof IEntry )
                    {
                        StringBuffer sb = new StringBuffer();
                        new CopyEntryAsLdifAction( CopyEntryAsLdifAction.MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
                            .serialializeEntries( new IEntry[]
                                { ( IEntry ) o }, sb );
                        LdifFile model = new LdifParser().parse( sb.toString() );
                        treeViewer.setInput( model );
                        treeViewer.expandToLevel( 2 );
                    }
                }
            }

            treeViewer.refresh();

            // treeViewer.setSelection(selection);
            // treeViewer.setExpandedElements(expandedElements);
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
            entryEditor = null;
        }
    }

    /**
     * This class implements the ContentProvider used for the LDIF Outline View
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private static class LdifContentProvider implements ITreeContentProvider
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren( Object element )
        {
            // file --> records
            if ( element instanceof LdifFile )
            {
                LdifFile ldifFile = ( LdifFile ) element;
                return ldifFile.getRecords();
            }

            // record --> Array of List of AttrValLine
            else if ( element instanceof LdifContentRecord )
            {
                LdifContentRecord record = ( LdifContentRecord ) element;
                return getUniqueAttrValLineArray( record.getAttrVals() );
            }
            else if ( element instanceof LdifChangeAddRecord )
            {
                LdifChangeAddRecord record = ( LdifChangeAddRecord ) element;
                return getUniqueAttrValLineArray( record.getAttrVals() );
            }
            else if ( element instanceof LdifChangeModifyRecord )
            {
                LdifChangeModifyRecord record = ( LdifChangeModifyRecord ) element;
                return record.getModSpecs();
            }
            else if ( element instanceof LdifChangeModDnRecord )
            {
                return new Object[0];
            }
            else if ( element instanceof LdifChangeDeleteRecord )
            {
                return new Object[0];
            }

            // List of AttrValLine --> Array of AttrValLine
            else if ( element instanceof List && ( ( List<?> ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                List<?> list = ( List<?> ) element;
                return list.toArray();
            }
            else if ( element instanceof LdifModSpec )
            {
                LdifModSpec modSpec = ( LdifModSpec ) element;
                return modSpec.getAttrVals();
            }

            else
            {
                return new Object[0];
            }
        }


        /**
         * Returns a unique line of attribute values from an array of attribute value lines
         *
         * @param lines
         *      the attribute value lines
         * @return 
         *      a unique line of attribute values from an array of attribute values lines
         */
        private Object[] getUniqueAttrValLineArray( LdifAttrValLine[] lines )
        {
            Map<String, List<LdifAttrValLine>> uniqueAttrMap = new LinkedHashMap<String, List<LdifAttrValLine>>();
            for ( int i = 0; i < lines.length; i++ )
            {
                if ( !uniqueAttrMap.containsKey( lines[i].getUnfoldedAttributeDescription() ) )
                {
                    uniqueAttrMap.put( lines[i].getUnfoldedAttributeDescription(), new ArrayList<LdifAttrValLine>() );
                }
                ( uniqueAttrMap.get( lines[i].getUnfoldedAttributeDescription() ) ).add( lines[i] );
            }
            return uniqueAttrMap.values().toArray();
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
     * This class implements the LabelProvider used for the LDIF Outline View
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private static class LdifLabelProvider extends LabelProvider
    {
        /**
         * {@inheritDoc}
         */
        public String getText( Object element )
        {
            // Record
            if ( element instanceof LdifRecord )
            {
                LdifRecord ldifRecord = ( LdifRecord ) element;
                return ldifRecord.getDnLine().getValueAsString();
            }

            // List of AttrValLine
            else if ( element instanceof List && ( ( List<?> ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                List<?> list = ( List<?> ) element;
                return ( ( LdifAttrValLine ) list.get( 0 ) ).getUnfoldedAttributeDescription() + " (" + list.size() //$NON-NLS-1$
                    + ")"; //$NON-NLS-1$
            }
            else if ( element instanceof LdifModSpec )
            {
                LdifModSpec modSpec = ( LdifModSpec ) element;
                return modSpec.getModSpecType().getUnfoldedAttributeDescription() + " (" + modSpec.getAttrVals().length //$NON-NLS-1$
                    + ")"; //$NON-NLS-1$
            }

            // AttrValLine
            else if ( element instanceof LdifAttrValLine )
            {
                LdifAttrValLine line = ( LdifAttrValLine ) element;
                return Utils.getShortenedString( line.getValueAsString(), 20 );
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
            // Record
            if ( element instanceof LdifContentRecord )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_ENTRY );
            }
            else if ( element instanceof LdifChangeAddRecord )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_ADD );
            }
            else if ( element instanceof LdifChangeModifyRecord )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MODIFY );
            }
            else if ( element instanceof LdifChangeDeleteRecord )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_DELETE );
            }
            else if ( element instanceof LdifChangeModDnRecord )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_RENAME );
            }

            // List of AttrValLine
            else if ( element instanceof List && ( ( List<?> ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_ATTRIBUTE );
            }
            else if ( element instanceof LdifModSpec )
            {
                LdifModSpec modSpec = ( LdifModSpec ) element;
                if ( modSpec.isAdd() )
                    return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_ADD );
                else if ( modSpec.isReplace() )
                    return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_REPLACE );
                else if ( modSpec.isDelete() )
                    return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_DELETE );
                else
                    return null;
            }

            // AttrValLine
            else if ( element instanceof LdifAttrValLine )
            {
                return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_VALUE );
            }

            else
            {
                return null;
            }
        }
    }
}
