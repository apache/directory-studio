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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeAddRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeDeleteRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeModDnRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeModifyRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifModSpec;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.parser.LdifParser;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.CopyEntryAsLdifAction;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditor;

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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


public class LdifOutlinePage extends ContentOutlinePage
{

    private IEditorPart editorPart;


    public LdifOutlinePage( IEditorPart editorPart )
    {
        this.editorPart = editorPart;
    }


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
                if ( editorPart instanceof LdifEditor )
                {
                    LdifEditor ldifEditor = ( LdifEditor ) editorPart;
                    if ( !event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection )
                    {
                        Object element = ( ( IStructuredSelection ) event.getSelection() ).getFirstElement();

                        if ( element instanceof LdifRecord )
                        {
                            LdifRecord ldifRecord = ( LdifRecord ) element;
                            ldifEditor.selectAndReveal( ldifRecord.getDnLine().getOffset(), ldifRecord.getDnLine()
                                .getLength() );
                        }
                        else if ( element instanceof List )
                        {
                            List list = ( List ) element;
                            if ( !list.isEmpty() && list.get( 0 ) instanceof LdifAttrValLine )
                            {
                                LdifAttrValLine line = ( LdifAttrValLine ) list.get( 0 );
                                ldifEditor.selectAndReveal( line.getOffset(), line.getRawAttributeDescription()
                                    .length() );
                            }
                        }
                        else if ( element instanceof LdifAttrValLine )
                        {
                            LdifAttrValLine line = ( LdifAttrValLine ) element;
                            ldifEditor.selectAndReveal( line.getOffset() + line.getRawAttributeDescription().length()
                                + line.getRawValueType().length(), line.getRawValue().length() );
                        }
                        else if ( element instanceof LdifModSpec )
                        {
                            LdifModSpec modSpec = ( LdifModSpec ) element;
                            ldifEditor.selectAndReveal( modSpec.getOffset(), modSpec.getModSpecType().getLength() );
                        }
                    }
                }
                else if ( editorPart instanceof EntryEditor )
                {
                    EntryEditor entryEditor = ( EntryEditor ) editorPart;
                    if ( !event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection )
                    {

                        Object o = entryEditor.getMainWidget().getViewer().getInput();
                        if ( o != null && o instanceof IEntry )
                        {
                            IEntry entry = ( IEntry ) o;
                            IAttribute[] attributes = entry.getAttributes();

                            List selectionList = new ArrayList();

                            Iterator it = ( ( IStructuredSelection ) event.getSelection() ).iterator();
                            while ( it.hasNext() )
                            {
                                Object element = it.next();

                                if ( element instanceof LdifAttrValLine )
                                {
                                    // select the value
                                    LdifAttrValLine line = ( LdifAttrValLine ) element;
                                    for ( int a = 0; a < attributes.length; a++ )
                                    {
                                        IAttribute attribute = attributes[a];
                                        if ( attribute.getDescription().equals( line.getUnfoldedAttributeDescription() ) )
                                        {
                                            IValue[] values = attribute.getValues();
                                            for ( int v = 0; v < values.length; v++ )
                                            {
                                                IValue value = values[v];
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
                                    List list = ( List ) element;
                                    if ( !list.isEmpty() && list.get( 0 ) instanceof LdifAttrValLine )
                                    {
                                        LdifAttrValLine line = ( LdifAttrValLine ) list.get( 0 );
                                        for ( int a = 0; a < attributes.length; a++ )
                                        {
                                            IAttribute attribute = attributes[a];
                                            if ( attribute.getDescription().equals(
                                                line.getUnfoldedAttributeDescription() ) )
                                            {
                                                selectionList.add( attribute );
                                                selectionList.addAll( Arrays.asList( attribute.getValues() ) );
                                            }
                                        }
                                    }
                                }
                                else if ( element instanceof LdifRecord )
                                {
                                    for ( int a = 0; a < attributes.length; a++ )
                                    {
                                        IAttribute attribute = attributes[a];
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


    public void refresh( Object element )
    {
        final TreeViewer treeViewer = getTreeViewer();
        if ( treeViewer != null )
        {
            treeViewer.refresh( element );
        }
    }


    public void refresh()
    {

        final TreeViewer treeViewer = getTreeViewer();

        if ( treeViewer != null )
        {

            // ISelection selection = treeViewer.getSelection();
            // Object[] expandedElements = treeViewer.getExpandedElements();

            if ( this.editorPart != null && this.editorPart instanceof LdifEditor )
            {
                if ( treeViewer.getInput() != ( ( LdifEditor ) this.editorPart ).getLdifModel() )
                {
                    treeViewer.setInput( ( ( LdifEditor ) this.editorPart ).getLdifModel() );
                }
            }
            else if ( this.editorPart != null && this.editorPart instanceof EntryEditor )
            {
                Object o = ( ( EntryEditor ) this.editorPart ).getMainWidget().getViewer().getInput();
                if ( o != null && o instanceof IEntry )
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

            treeViewer.refresh();

            // treeViewer.setSelection(selection);
            // treeViewer.setExpandedElements(expandedElements);
        }
    }


    public void dispose()
    {
        super.dispose();
        if ( this.editorPart != null && this.editorPart instanceof LdifEditor )
        {
            ( ( LdifEditor ) this.editorPart ).outlinePageClosed();
            this.editorPart = null;
        }
    }

    private static class LdifContentProvider implements ITreeContentProvider
    {
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
            else if ( element instanceof List && ( ( List ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                List list = ( List ) element;
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


        private Object[] getUniqueAttrValLineArray( LdifAttrValLine[] lines )
        {
            Map uniqueAttrMap = new LinkedHashMap();
            for ( int i = 0; i < lines.length; i++ )
            {
                if ( !uniqueAttrMap.containsKey( lines[i].getUnfoldedAttributeDescription() ) )
                {
                    uniqueAttrMap.put( lines[i].getUnfoldedAttributeDescription(), new ArrayList() );
                }
                ( ( List ) uniqueAttrMap.get( lines[i].getUnfoldedAttributeDescription() ) ).add( lines[i] );
            }
            return uniqueAttrMap.values().toArray();
        }


        public Object getParent( Object element )
        {
            return null;
        }


        public boolean hasChildren( Object element )
        {
            return getChildren( element ) != null && getChildren( element ).length > 0;
        }


        public Object[] getElements( Object inputElement )
        {
            return getChildren( inputElement );
        }


        public void dispose()
        {
        }


        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    private static class LdifLabelProvider extends LabelProvider
    {
        public String getText( Object element )
        {

            // Record
            if ( element instanceof LdifRecord )
            {
                LdifRecord ldifRecord = ( LdifRecord ) element;
                return ldifRecord.getDnLine().getValueAsString();
            }

            // List of AttrValLine
            else if ( element instanceof List && ( ( List ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                List list = ( List ) element;
                return ( ( LdifAttrValLine ) list.get( 0 ) ).getUnfoldedAttributeDescription() + " (" + list.size()
                    + ")";
            }
            else if ( element instanceof LdifModSpec )
            {
                LdifModSpec modSpec = ( LdifModSpec ) element;
                return modSpec.getModSpecType().getUnfoldedAttributeDescription() + " (" + modSpec.getAttrVals().length
                    + ")";
            }

            // AttrValLine
            else if ( element instanceof LdifAttrValLine )
            {
                LdifAttrValLine line = ( LdifAttrValLine ) element;
                return Utils.getShortenedString( line.getValueAsString(), 20 );
            }

            else
            {
                return "";
            }
        }


        public Image getImage( Object element )
        {

            // Record
            if ( element instanceof LdifContentRecord )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ENTRY );
            }
            else if ( element instanceof LdifChangeAddRecord )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_ADD );
            }
            else if ( element instanceof LdifChangeModifyRecord )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_MODIFY );
            }
            else if ( element instanceof LdifChangeDeleteRecord )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_DELETE );
            }
            else if ( element instanceof LdifChangeModDnRecord )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_RENAME );
            }

            // List of AttrValLine
            else if ( element instanceof List && ( ( List ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_ATTRIBUTE );
            }
            else if ( element instanceof LdifModSpec )
            {
                LdifModSpec modSpec = ( LdifModSpec ) element;
                if ( modSpec.isAdd() )
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_MOD_ADD );
                else if ( modSpec.isReplace() )
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_MOD_REPLACE );
                else if ( modSpec.isDelete() )
                    return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_MOD_DELETE );
                else
                    return null;
            }

            // AttrValLine
            else if ( element instanceof LdifAttrValLine )
            {
                return BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LDIF_VALUE );
            }

            else
            {
                return null;
            }
        }
    }

}
