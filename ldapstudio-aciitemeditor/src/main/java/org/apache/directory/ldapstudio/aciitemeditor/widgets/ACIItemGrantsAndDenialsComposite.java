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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.shared.ldap.aci.GrantAndDenial;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * This composite contains GUI elements to edit ACI item grants and denials.

 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemGrantsAndDenialsComposite extends Composite
{

    /** The description label */
    private Label label = null;

    /** The tree control for the tree viewer */
    private Tree tree = null;

    /** The tree viewer containing all grants and denials */
    private TreeViewer treeViewer = null;

    /** The composite containing the buttons */
    private Composite buttonComposite = null;

    /** The grant all button */
    private Button grantAllButton = null;

    /** The deny all button */
    private Button denyAllButton = null;

    /** The deselect all button */
    private Button deselectAllButton = null;

    /** The undo button */
    private Button undoButton = null;

    /** The redo button */
    private Button redoButton = null;

    /** Colum 1 */
    private static String PERMISSION = Messages.getString( "ACIItemGrantsAndDenialsComposite.column1.header" ); //$NON-NLS-1$

    /** Colum2 */
    private static String STATE = Messages.getString( "ACIItemGrantsAndDenialsComposite.column2.header" ); //$NON-NLS-1$

    /** The colums */
    private static String[] COLUMNS = new String[]
        { PERMISSION, STATE };

    /** The undo/redo stack size */
    private static final int MAX_STACK_SIZE = 25;

    /** Used as input for the tree viewer */
    private GrantAndDenialCategory[] grantAndDenialCategories = new GrantAndDenialCategory[]
        {
            new GrantAndDenialCategory(
                Messages.getString( "ACIItemGrantsAndDenialsComposite.category.read" ), true, new GrantAndDenialWrapper[] //$NON-NLS-1$
                    {
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_BROWSE, GrantAndDenial.DENY_BROWSE ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_READ, GrantAndDenial.DENY_READ ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_COMPARE, GrantAndDenial.DENY_COMPARE ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_FILTER_MATCH, GrantAndDenial.DENY_FILTER_MATCH ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_RETURN_DN, GrantAndDenial.DENY_RETURN_DN ) } ),
            new GrantAndDenialCategory(
                Messages.getString( "ACIItemGrantsAndDenialsComposite.category.modify" ), true, new GrantAndDenialWrapper[] //$NON-NLS-1$
                    { new GrantAndDenialWrapper( GrantAndDenial.GRANT_ADD, GrantAndDenial.DENY_ADD ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_MODIFY, GrantAndDenial.DENY_MODIFY ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_REMOVE, GrantAndDenial.DENY_REMOVE ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_RENAME, GrantAndDenial.DENY_RENAME ) } ),
            new GrantAndDenialCategory(
                Messages.getString( "ACIItemGrantsAndDenialsComposite.category.advanced" ), false, new GrantAndDenialWrapper[] //$NON-NLS-1$
                    {
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_EXPORT, GrantAndDenial.DENY_EXPORT ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_IMPORT, GrantAndDenial.DENY_IMPORT ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_INVOKE, GrantAndDenial.DENY_INVOKE ),
                        new GrantAndDenialWrapper( GrantAndDenial.GRANT_DISCLOSE_ON_ERROR,
                            GrantAndDenial.DENY_DISCLOSE_ON_ERROR ) } ) };

    /**
     * A GrantAndDenialCategory is used to categorize grants and denials in a tree.
     */
    private class GrantAndDenialCategory
    {
        /** The category name, displayed in tree */
        private String name;

        /** The initial expanded state */
        private boolean expanded;

        /** The grants and denials wrappers display under this category */
        private GrantAndDenialWrapper[] grantAndDenialWrappers;


        /** 
         * Creates a new instance of GrantAndDenialCategory.
         *
         * @param name the category name, displayed in tree
         * @param expanded true if category should be initially expanded
         * @param grantAndDenialWrappers the grants and denials wrappers display under this category
         */
        private GrantAndDenialCategory( String name, boolean expanded, GrantAndDenialWrapper[] grantAndDenialWrappers )
        {
            this.name = name;
            this.expanded = expanded;
            this.grantAndDenialWrappers = grantAndDenialWrappers;
        }
    }

    /**
     * A GrantAndDenialWrapper is used to display grants and denials in tree and to 
     * track the current state (not specified, grant or deny). Additional it provides
     * undo/redo functionality.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class GrantAndDenialWrapper
    {
        /** The grant */
        private GrantAndDenial grant;

        /** The denial */
        private GrantAndDenial denial;

        /** The current state: null=not specified, grant or denial */
        private GrantAndDenial activeGrantAndDenial;

        /** List containing previous states of activeGrandAndDenial */
        private List<GrantAndDenial> undoStack;

        /** List containing "future" states of activeGrandAndDenial */
        private List<GrantAndDenial> redoStack;


        /**
         * Creates a new instance of GrantAndDenialWrapper.
         *
         * @param grant
         * @param denial
         */
        private GrantAndDenialWrapper( GrantAndDenial grant, GrantAndDenial denial )
        {
            this.grant = grant;
            this.denial = denial;
            this.activeGrantAndDenial = null;
            undoStack = new LinkedList<GrantAndDenial>();
            redoStack = new LinkedList<GrantAndDenial>();
        }
    }


    /**
     * Creates a new instance of ACIItemGrantsAndDenialsComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemGrantsAndDenialsComposite( Composite parent, int style )
    {
        super( parent, style );

        GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = 2;
        setLayout( layout );

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.verticalAlignment = GridData.FILL;
        setLayoutData( layoutData );

        GridData labelGridData = new GridData();
        labelGridData.horizontalSpan = 2;
        labelGridData.verticalAlignment = GridData.CENTER;
        labelGridData.grabExcessHorizontalSpace = true;
        labelGridData.horizontalAlignment = GridData.FILL;

        label = new Label( this, SWT.NONE );
        label.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.description" ) ); //$NON-NLS-1$
        label.setLayoutData( labelGridData );

        createTree();

        createButtonComposite();
    }


    /**
     * This method initializes tree
     *
     */
    private void createTree()
    {
        GridData tableGridData = new GridData( GridData.FILL_BOTH );
        tableGridData.grabExcessHorizontalSpace = true;
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalAlignment = GridData.FILL;
        //tableGridData.heightHint = 100;

        tree = new Tree( this, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        tree.setHeaderVisible( true );
        tree.setLayoutData( tableGridData );
        tree.setLinesVisible( true );

        TreeColumn c1 = new TreeColumn( tree, SWT.LEFT, 0 );
        c1.setText( COLUMNS[0] );
        c1.setWidth( 160 );
        TreeColumn c2 = new TreeColumn( tree, SWT.LEFT, 1 );
        c2.setText( COLUMNS[1] );
        c2.setWidth( 80 );
        //        TreeColumn c3 = new TreeColumn( tree, SWT.LEFT, 2 );
        //        c3.setText( " " ); //$NON-NLS-1$
        //        c3.setWidth( 0 );

        treeViewer = new TreeViewer( tree );
        treeViewer.setUseHashlookup( true );

        treeViewer.setColumnProperties( COLUMNS );

        ICellModifier cellModifier = new GrantsAndDenialsCellModifier();
        treeViewer.setCellModifier( cellModifier );
        CellEditor[] cellEditors = new CellEditor[]
            { null, new CheckboxCellEditor( tree ), null };
        treeViewer.setCellEditors( cellEditors );

        treeViewer.setContentProvider( new GrantsAndDenialsContentProvider() );
        treeViewer.setLabelProvider( new GrantsAndDenialsLabelProvider() );
        treeViewer.setInput( grantAndDenialCategories );

        // set expanded state
        List<GrantAndDenialCategory> expandedList = new ArrayList<GrantAndDenialCategory>();
        for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
        {
            if ( grantAndDenialCategory.expanded )
            {
                expandedList.add( grantAndDenialCategory );
            }
        }
        treeViewer.setExpandedElements( expandedList.toArray() );
    }


    /**
     * This method initializes buttonComposite  
     *
     */
    private void createButtonComposite()
    {
        GridData deselectAllButtonGridData = new GridData();
        deselectAllButtonGridData.horizontalAlignment = GridData.FILL;
        deselectAllButtonGridData.grabExcessHorizontalSpace = false;
        deselectAllButtonGridData.verticalAlignment = GridData.BEGINNING;
        deselectAllButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData denyAllButtonGridData = new GridData();
        denyAllButtonGridData.horizontalAlignment = GridData.FILL;
        denyAllButtonGridData.grabExcessHorizontalSpace = false;
        denyAllButtonGridData.verticalAlignment = GridData.BEGINNING;
        denyAllButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData grantAllButtonGridData = new GridData();
        grantAllButtonGridData.horizontalAlignment = GridData.FILL;
        grantAllButtonGridData.grabExcessHorizontalSpace = false;
        grantAllButtonGridData.verticalAlignment = GridData.BEGINNING;
        grantAllButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData undoButtonGridData = new GridData();
        undoButtonGridData.horizontalAlignment = GridData.FILL;
        undoButtonGridData.grabExcessHorizontalSpace = false;
        undoButtonGridData.verticalAlignment = GridData.BEGINNING;
        undoButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridData redoButtonGridData = new GridData();
        redoButtonGridData.horizontalAlignment = GridData.FILL;
        redoButtonGridData.grabExcessHorizontalSpace = false;
        redoButtonGridData.verticalAlignment = GridData.BEGINNING;
        redoButtonGridData.widthHint = Activator.getButtonWidth( this );

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalAlignment = GridData.FILL;

        buttonComposite = new Composite( this, SWT.NONE );
        buttonComposite.setLayoutData( gridData );
        buttonComposite.setLayout( gridLayout );

        grantAllButton = new Button( buttonComposite, SWT.NONE );
        grantAllButton.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.grantAll.button" ) ); //$NON-NLS-1$
        grantAllButton.setLayoutData( grantAllButtonGridData );
        grantAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                backup();
                for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
                {
                    for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.grant;
                    }
                }
                treeViewer.refresh();
            }
        } );

        denyAllButton = new Button( buttonComposite, SWT.NONE );
        denyAllButton.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.denyAll.button" ) ); //$NON-NLS-1$
        denyAllButton.setLayoutData( denyAllButtonGridData );
        denyAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                backup();
                for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
                {
                    for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.denial;
                    }
                }
                treeViewer.refresh();
            }
        } );

        deselectAllButton = new Button( buttonComposite, SWT.NONE );
        deselectAllButton.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.deselectAll.button" ) ); //$NON-NLS-1$
        deselectAllButton.setLayoutData( deselectAllButtonGridData );
        deselectAllButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                backup();
                for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
                {
                    for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = null;
                    }
                }
                treeViewer.refresh();
            }
        } );

        undoButton = new Button( buttonComposite, SWT.NONE );
        undoButton.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.undo.button" ) ); //$NON-NLS-1$
        undoButton.setLayoutData( undoButtonGridData );
        undoButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                undo();
                treeViewer.refresh();
            }
        } );
        undoButton.setEnabled( false );

        redoButton = new Button( buttonComposite, SWT.NONE );
        redoButton.setText( Messages.getString( "ACIItemGrantsAndDenialsComposite.redo.button" ) ); //$NON-NLS-1$
        redoButton.setLayoutData( redoButtonGridData );
        redoButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                redo();
                treeViewer.refresh();
            }
        } );
        redoButton.setEnabled( false );

    }

    /**
     * The ICellModifier user for this tree viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class GrantsAndDenialsCellModifier implements ICellModifier
    {

        /**
         * Only GrantAndDenialWrappers and the STATE colum is modifyable.
         * 
         * @param element the element
         * @param property the property
         * 
         * @return true, if can modify
         */
        public boolean canModify( Object element, String property )
        {
            if ( element instanceof GrantAndDenialWrapper )
            {
                return property.equals( STATE );
            }

            return false;
        }


        /**
         * The used CheckboxCellEditor accepts only Booleans.
         * 
         * @param element the element
         * @param property the property
         * 
         * @return the value
         */
        public Object getValue( Object element, String property )
        {
            if ( element instanceof GrantAndDenialWrapper )
            {
                if ( property.equals( STATE ) )
                {
                    return new Boolean( true );
                }
            }

            return null;
        }


        /**
         * Performs the tree-state transtion.
         * 
         * @param element the element
         * @param value the value
         * @param property the property
         */
        public void modify( Object element, String property, Object value )
        {
            if ( element != null && element instanceof Item )
            {
                element = ( ( Item ) element ).getData();
            }

            if ( element instanceof GrantAndDenialWrapper )
            {
                GrantAndDenialWrapper grantAndDenialWrapper = ( GrantAndDenialWrapper ) element;

                if ( property.equals( STATE ) )
                {
                    backup();
                    if ( grantAndDenialWrapper.activeGrantAndDenial == null )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.grant;
                    }
                    else if ( grantAndDenialWrapper.activeGrantAndDenial == grantAndDenialWrapper.grant )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.denial;
                    }
                    else if ( grantAndDenialWrapper.activeGrantAndDenial == grantAndDenialWrapper.denial )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = null;
                    }
                }
            }

            treeViewer.refresh();
        }

    }

    /**
     * The content provider used for this tree viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class GrantsAndDenialsContentProvider extends ArrayContentProvider implements ITreeContentProvider
    {

        /**
         * Only GrantAndDenialCategories have children.
         * 
         * @param parentElement the parent element
         * 
         * @return the children
         */
        public Object[] getChildren( Object parentElement )
        {
            if ( parentElement instanceof GrantAndDenialCategory )
            {
                GrantAndDenialCategory cat = ( GrantAndDenialCategory ) parentElement;
                return cat.grantAndDenialWrappers;
            }

            return null;
        }


        /**
         * Not used.
         * 
         * @param element the element
         * 
         * @return the parent
         */
        public Object getParent( Object element )
        {
            return null;
        }


        /**
         * Only GrantAndDenialCategories have children.
         * 
         * @param element the element
         * 
         * @return true, if has children
         */
        public boolean hasChildren( Object element )
        {
            return ( element instanceof GrantAndDenialCategory );
        }

    }

    /**
     * The label provider used for this tree viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class GrantsAndDenialsLabelProvider extends LabelProvider implements ITableLabelProvider
    {

        /**
         * The STATE is displayed as image.
         * 
         * @param element the element
         * @param columnIndex the column index
         * 
         * @return the column image
         */
        public Image getColumnImage( Object element, int columnIndex )
        {
            if ( element instanceof GrantAndDenialWrapper )
            {
                GrantAndDenialWrapper grantAndDenialWrapper = ( GrantAndDenialWrapper ) element;
                switch ( columnIndex )
                {
                    case 0:
                        return null;
                    case 1:
                        if ( grantAndDenialWrapper.activeGrantAndDenial == null )
                        {
                            return Activator.getDefault().getImage(
                                Messages.getString( "ACIItemGrantsAndDenialsComposite.unspecified.icon" ) ); //$NON-NLS-1$
                        }
                        else if ( grantAndDenialWrapper.activeGrantAndDenial == grantAndDenialWrapper.grant )
                        {
                            return Activator.getDefault().getImage(
                                Messages.getString( "ACIItemGrantsAndDenialsComposite.grant.icon" ) ); //$NON-NLS-1$
                        }
                        else if ( grantAndDenialWrapper.activeGrantAndDenial == grantAndDenialWrapper.denial )
                        {
                            return Activator.getDefault().getImage(
                                Messages.getString( "ACIItemGrantsAndDenialsComposite.deny.icon" ) ); //$NON-NLS-1$
                        }
                    case 2:
                        return null;
                }
            }
            return null;
        }


        /**
         * Returns GrantAndDenialCategory name or the MicroOperation name.
         * 
         * @param element the element
         * @param columnIndex the column index
         * 
         * @return the column text
         */
        public String getColumnText( Object element, int columnIndex )
        {
            if ( element instanceof GrantAndDenialCategory )
            {
                if ( columnIndex == 0 )
                {
                    GrantAndDenialCategory cat = ( GrantAndDenialCategory ) element;
                    return cat.name;
                }
            }
            else if ( element instanceof GrantAndDenialWrapper )
            {
                if ( columnIndex == 0 )
                {
                    GrantAndDenialWrapper wrapper = ( GrantAndDenialWrapper ) element;
                    return wrapper.grant.getMicroOperation().getName();
                }
            }

            return ""; //$NON-NLS-1$
        }

    }


    /**
     * Sets the grants and denials. 
     *
     * @param grantsAndDenials
     */
    public void setGrantsAndDenials( Collection<GrantAndDenial> grantsAndDenials )
    {
        for ( GrantAndDenial grantAndDenial : grantsAndDenials )
        {
            for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
            {
                for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
                {
                    if ( grantAndDenialWrapper.grant == grantAndDenial )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.grant;
                    }
                    else if ( grantAndDenialWrapper.denial == grantAndDenial )
                    {
                        grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.denial;
                    }
                }
            }
        }

        treeViewer.refresh();
    }


    /**
     * Returns the grants and denials as selected by the user.
     *
     * @return the grants and denials
     * @throws ParseException 
     */
    public Collection<GrantAndDenial> getGrantsAndDenials() throws ParseException
    {
        Collection<GrantAndDenial> grantsAndDenials = new ArrayList<GrantAndDenial>();

        for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
        {
            for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
            {
                if ( grantAndDenialWrapper.activeGrantAndDenial != null )
                {
                    grantsAndDenials.add( grantAndDenialWrapper.activeGrantAndDenial );
                }
            }
        }

        return grantsAndDenials;
    }


    /** 
     * Undos the last modification.
     */
    private void undo()
    {
        for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
        {
            for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
            {
                if ( grantAndDenialWrapper.undoStack.size() > 0 )
                {
                    grantAndDenialWrapper.redoStack.add( 0, grantAndDenialWrapper.activeGrantAndDenial );
                    grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.undoStack.remove( 0 );
                }

                undoButton.setEnabled( !grantAndDenialWrapper.undoStack.isEmpty() );
                redoButton.setEnabled( !grantAndDenialWrapper.redoStack.isEmpty() );
            }
        }
    }


    /**
     * Redos the last modification
     */
    private void redo()
    {
        for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
        {
            for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
            {
                if ( grantAndDenialWrapper.redoStack.size() > 0 )
                {
                    grantAndDenialWrapper.undoStack.add( 0, grantAndDenialWrapper.activeGrantAndDenial );
                    grantAndDenialWrapper.activeGrantAndDenial = grantAndDenialWrapper.redoStack.remove( 0 );
                }

                undoButton.setEnabled( !grantAndDenialWrapper.undoStack.isEmpty() );
                redoButton.setEnabled( !grantAndDenialWrapper.redoStack.isEmpty() );
            }
        }
    }


    /**
     * Saves the current state to the undo stack.
     */
    private void backup()
    {
        for ( GrantAndDenialCategory grantAndDenialCategory : grantAndDenialCategories )
        {
            for ( GrantAndDenialWrapper grantAndDenialWrapper : grantAndDenialCategory.grantAndDenialWrappers )
            {
                if ( grantAndDenialWrapper.undoStack.size() == MAX_STACK_SIZE )
                {
                    grantAndDenialWrapper.undoStack.remove( grantAndDenialWrapper.undoStack.size() - 1 );
                }
                grantAndDenialWrapper.undoStack.add( 0, grantAndDenialWrapper.activeGrantAndDenial );
                grantAndDenialWrapper.redoStack.clear();

                undoButton.setEnabled( !grantAndDenialWrapper.undoStack.isEmpty() );
                redoButton.setEnabled( !grantAndDenialWrapper.redoStack.isEmpty() );
            }
        }
    }

}
