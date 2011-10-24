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

package org.apache.directory.studio.ldifeditor.editor;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserLabelProvider;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
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
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


/**
 * This class implements the Outline Page for LDIF. 
 * It used to display LDIF files and records.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifOutlinePage extends ContentOutlinePage
{
    /** The editor it is attached to */
    private LdifEditor ldifEditor;

    /** Whether or not the outline page is linked to an entry in the LDAP Browser view*/
    private boolean isLinkedToLdapBrowser = false;


    /**
     * Creates a new instance of LdifOutlinePage.
     *
     * @param ldifEditor
     *      the editor the Outline page is attached to
     */
    public LdifOutlinePage( LdifEditor ldifEditor )
    {
        this.ldifEditor = ldifEditor;
    }


    /**
     * Creates a new instance of LdifOutlinePage.
     *
     * @param ldifEditor
     *      the editor the Outline page is attached to
     */
    public LdifOutlinePage( LdifEditor ldifEditor, boolean isLinkedToLdapBrowser )
    {
        this.ldifEditor = ldifEditor;
        this.isLinkedToLdapBrowser = isLinkedToLdapBrowser;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        super.createControl( parent );

        final TreeViewer treeViewer = getTreeViewer();
        treeViewer.setLabelProvider( new LdifLabelProvider( ldifEditor, isLinkedToLdapBrowser ) );
        treeViewer.setContentProvider( new LdifContentProvider() );

        if ( isLinkedToLdapBrowser )
        {
            treeViewer.setAutoExpandLevel( 2 );
        }

        treeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
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
                            ldifEditor.selectAndReveal( line.getOffset(), line.getRawAttributeDescription().length() );
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

            if ( ldifEditor != null )
            {
                if ( treeViewer.getInput() != ldifEditor.getLdifModel() )
                {
                    treeViewer.setInput( ldifEditor.getLdifModel() );
                }
            }

            treeViewer.refresh();

            if ( isLinkedToLdapBrowser )
            {
                treeViewer.setAutoExpandLevel( 2 );
            }

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
        if ( ldifEditor != null )
        {
            ldifEditor.outlinePageClosed();
            ldifEditor = null;
        }
    }

    /**
     * This class implements the ContentProvider used for the LDIF Outline View
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    private static class LdifContentProvider implements ITreeContentProvider
    {
        /**
         * {@inheritDoc}
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
     */
    private static class LdifLabelProvider extends LabelProvider
    {
        /** The editor it is attached to */
        private LdifEditor ldifEditor;

        /** Whether or not the outline page is linked to an entry in the LDAP Browser view*/
        private boolean isLinkedToLdapBrowser = false;


        public LdifLabelProvider( LdifEditor ldifEditor, boolean isLinkedToLdapBrowser )
        {
            super();
            this.ldifEditor = ldifEditor;
            this.isLinkedToLdapBrowser = isLinkedToLdapBrowser;
        }


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
            else if ( element instanceof List && ( ( List ) element ).get( 0 ) instanceof LdifAttrValLine )
            {
                List list = ( List ) element;
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
                if ( isLinkedToLdapBrowser )
                {
                    LdifContentRecord record = ( LdifContentRecord ) element;

                    LdifDnLine dnLine = record.getDnLine();
                    if ( dnLine != null )
                    {
                        String dn = dnLine.getUnfoldedDn();
                        if ( dn != null && "".equals( dn ) ) //$NON-NLS-1$
                        {
                            // Root DSE
                            return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ENTRY_ROOT );
                        }
                        else
                        {
                            // Any other case
                            try
                            {
                                return BrowserLabelProvider.getImageByObjectClass( ldifEditor.getConnection()
                                    .getEntryFromCache( new Dn( dn ) ) );
                            }
                            catch ( LdapInvalidDnException e )
                            {
                                // Will never occur
                            }
                        }
                    }
                }

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
            else if ( element instanceof List && ( ( List ) element ).get( 0 ) instanceof LdifAttrValLine )
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
