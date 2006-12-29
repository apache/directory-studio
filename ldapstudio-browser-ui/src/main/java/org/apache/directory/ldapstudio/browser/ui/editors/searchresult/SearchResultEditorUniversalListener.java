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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.events.EmptyValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryUpdateListener;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateEvent;
import org.apache.directory.ldapstudio.browser.core.events.SearchUpdateListener;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.actions.OpenSearchResultAction;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;


public class SearchResultEditorUniversalListener implements IPartListener, SearchUpdateListener, EntryUpdateListener
{

    private SearchResultEditor editor;

    private TableViewer viewer;

    private SearchResultEditorCursor cursor;

    private OpenBestEditorAction startEditAction;

    private ISearch selectedSearch;

    private Hyperlink dnLink;

    private TableEditor tableEditor;


    public SearchResultEditorUniversalListener( SearchResultEditor editor )
    {
        this.editor = editor;

        this.startEditAction = this.editor.getActionGroup().getOpenBestEditorAction();
        this.viewer = this.editor.getMainWidget().getViewer();
        this.cursor = this.editor.getConfiguration().getCursor( this.viewer );

        dnLink = new Hyperlink( viewer.getTable(), SWT.NONE );
        dnLink.setLayoutData( new GridData( SWT.BOTTOM, SWT.LEFT, true, true ) );
        dnLink.setText( "" );
        dnLink.setMenu( viewer.getTable().getMenu() );
        dnLink.addHyperlinkListener( new IHyperlinkListener()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                ISearchResult sr = ( ISearchResult ) e.widget.getData();
                OpenSearchResultAction action = new OpenSearchResultAction();
                action.setSelectedSearchResults( new ISearchResult[]
                    { sr } );
                action.run();
            }


            public void linkEntered( HyperlinkEvent e )
            {
            }


            public void linkExited( HyperlinkEvent e )
            {
            }
        } );
        tableEditor = new TableEditor( viewer.getTable() );
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.verticalAlignment = SWT.BOTTOM;
        tableEditor.grabHorizontal = true;
        tableEditor.grabVertical = true;

        this.initListeners();
    }


    private void initListeners()
    {

        cursor.addMouseMoveListener( new MouseMoveListener()
        {
            public void mouseMove( MouseEvent e )
            {
                if ( !cursor.isDisposed() )
                {
                    TableItem item = cursor.getRow();
                    if ( cursor.getColumn() == 0
                        && "DN".equalsIgnoreCase( cursor.getRow().getParent().getColumns()[0].getText() ) )
                    {
                        checkDnLink( item );
                    }
                }
            }
        } );

        viewer.getTable().addMouseMoveListener( new MouseMoveListener()
        {
            public void mouseMove( MouseEvent e )
            {
                if ( !viewer.getTable().isDisposed() )
                {
                    TableItem item = viewer.getTable().getItem( new Point( e.x, e.y ) );
                    viewer.getTable().getColumns()[0].getWidth();
                    if ( e.x > 0 && e.x < viewer.getTable().getColumns()[0].getWidth()
                        && "DN".equalsIgnoreCase( viewer.getTable().getColumns()[0].getText() ) )
                    {
                        checkDnLink( item );
                    }
                }
            }
        } );

        dnLink.addMouseTrackListener( new MouseTrackListener()
        {
            public void mouseEnter( MouseEvent e )
            {
            }


            public void mouseExit( MouseEvent e )
            {
                if ( !dnLink.isDisposed() )
                {
                    dnLink.setVisible( false );
                    tableEditor.setEditor( null );
                }
            }


            public void mouseHover( MouseEvent e )
            {
            }
        } );

        cursor.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                // viewer.setSelection(new StructuredSelection(getRow()), true);
                // viewer.getTable().setSelection(new TableItem[]{getRow()});
                viewer.setSelection( null, true );
                viewer.getTable().setSelection( new TableItem[0] );

                ISearchResult result = cursor.getSelectedSearchResult();
                String property = cursor.getSelectedProperty();
                if ( property != null && result != null && viewer.getCellModifier().canModify( result, property ) )
                {
                    cursor.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_LIST_SELECTION ) );
                }
                else
                {
                    cursor.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_TITLE_INACTIVE_FOREGROUND ) );
                }

                // cursor.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
                viewer.setSelection( null, true );
                viewer.getTable().setSelection( new TableItem[0] );
                if ( startEditAction.isEnabled() )
                    startEditAction.run();
            }
        } );

        cursor.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                viewer.setSelection( null, true );
                viewer.getTable().setSelection( new TableItem[0] );
                if ( startEditAction.isEnabled() )
                    startEditAction.run();
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
            }
        } );

        cursor.addKeyListener( new KeyListener()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.character != '\0' && e.character != SWT.CR && e.character != SWT.LF && e.character != SWT.BS
                    && e.character != SWT.DEL && e.character != SWT.TAB && e.character != SWT.ESC
                    && ( e.stateMask == 0 || e.stateMask == SWT.SHIFT ) )
                {

                    if ( startEditAction.isEnabled()
                        && startEditAction.getBestValueEditor().getCellEditor() instanceof TextCellEditor )
                    {
                        startEditAction.run();
                        CellEditor editor = viewer.getCellEditors()[cursor.getColumn()];
                        if ( editor instanceof TextCellEditor )
                        {
                            editor.setValue( String.valueOf( e.character ) );
                            ( ( Text ) editor.getControl() ).setSelection( 1 );
                        }
                    }

                }
            }


            public void keyReleased( KeyEvent e )
            {
            }
        } );

        editor.getSite().getPage().addPartListener( this );
        EventRegistry.addSearchUpdateListener( this );
        EventRegistry.addEntryUpdateListener( this );
    }


    public void dispose()
    {
        EventRegistry.removeSearchUpdateListener( this );
        EventRegistry.removeEntryUpdateListener( this );
        editor.getSite().getPage().removePartListener( this );

        this.selectedSearch = null;
        this.startEditAction = null;
        this.cursor = null;
        this.viewer = null;
        this.editor = null;
    }


    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( this.selectedSearch == searchUpdateEvent.getSearch() )
        {
            this.refreshInput();
        }
    }


    public void entryUpdated( EntryModificationEvent event )
    {

        if ( event instanceof EmptyValueAddedEvent && !this.editor.getActionGroup().isEditorActive() )
        {
            EmptyValueAddedEvent evae = ( EmptyValueAddedEvent ) event;
            IAttribute att = evae.getAddedValue().getAttribute();
            AttributeHierarchy ah = cursor.getSelectedAttributeHierarchie();
            if ( ah != null && ah.contains( att ) )
            {
                viewer.setSelection( null, true );
                viewer.getTable().setSelection( new TableItem[0] );
                if ( startEditAction.isEnabled() )
                {
                    startEditAction.run();
                }
            }
        }
        else
        {
            this.viewer.refresh( true );
            cursor.notifyListeners( SWT.Selection, new Event() );
        }
    }


    void setInput( ISearch search )
    {
        if ( search != this.viewer.getInput() )
        {
            this.selectedSearch = search;
            this.refreshInput();
            this.editor.getActionGroup().setInput( search );
        }
    }


    void refreshInput()
    {

        // create at least on column
        this.ensureColumnCount( 1 );

        // get all columns
        TableColumn[] columns = this.viewer.getTable().getColumns();

        // number of uses columns
        int usedColumns;

        if ( this.selectedSearch != null )
        {

            // get displayed attributes
            boolean showDn = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN )
                || this.selectedSearch.getReturningAttributes().length == 0;
            String[] attributes;
            if ( showDn )
            {
                attributes = new String[this.selectedSearch.getReturningAttributes().length + 1];
                attributes[0] = "DN";
                System
                    .arraycopy( this.selectedSearch.getReturningAttributes(), 0, attributes, 1, attributes.length - 1 );
            }
            else
            {
                attributes = this.selectedSearch.getReturningAttributes();
            }

            // create missing columns
            if ( attributes.length > columns.length )
            {
                this.ensureColumnCount( attributes.length );
                columns = this.viewer.getTable().getColumns();
            }

            // set column headers
            for ( int i = 0; i < attributes.length; i++ )
            {
                columns[i].setText( attributes[i] );
            }
            this.viewer.setColumnProperties( attributes );

            // set input
            ( ( SearchResultEditorLabelProvider ) this.viewer.getLabelProvider() ).inputChanged( this.selectedSearch,
                showDn );

            this.viewer.setInput( this.selectedSearch );
            // this.viewer.refresh();

            // update cell editors
            CellEditor[] editors = new CellEditor[attributes.length];
            this.viewer.setCellEditors( editors );

            if ( attributes.length > 0 )
            {
                int width = this.viewer.getTable().getClientArea().width / attributes.length;
                for ( int i = 0; i < attributes.length; i++ )
                {
                    columns[i].setWidth( width );
                }
            }

            // layout columns
            // for(int i=0; i<attributes.length; i++) {
            // columns[i].pack();
            // }
            usedColumns = attributes.length;
        }
        else
        {
            this.viewer.setInput( null );
            columns[0].setText( "DN" );
            columns[0].pack();
            usedColumns = 1;
        }

        // make unused columns invisible
        for ( int i = usedColumns; i < columns.length; i++ )
        {
            columns[i].setWidth( 0 );
            columns[i].setText( " " );
        }

        // refresh content provider (sorter and filter)
        this.editor.getConfiguration().getContentProvider( this.editor.getMainWidget() ).refresh();

        // this.cursor.setFocus();
    }


    private void ensureColumnCount( int count )
    {
        TableColumn[] columns = this.viewer.getTable().getColumns();
        if ( columns.length < count )
        {
            for ( int i = columns.length; i < count; i++ )
            {
                TableColumn column = new TableColumn( this.viewer.getTable(), SWT.LEFT );
                column.setText( "" );
                column.setWidth( 0 );
                column.setResizable( true );
                column.setMoveable( true );
            }
        }
    }


    private void checkDnLink( TableItem item )
    {

        if ( dnLink == null || dnLink.isDisposed() || tableEditor == null || this.viewer.getTable().isDisposed()
            || cursor.isDisposed() )
        {
            return;
        }

        boolean showLinks = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS );
        if ( showLinks )
        {

            boolean linkVisible = false;

            if ( item != null )
            {
                Object data = item.getData();

                if ( data instanceof ISearchResult )
                {
                    ISearchResult sr = ( ISearchResult ) data;

                    item.getFont();
                    viewer.getTable().getColumn( 0 ).getWidth();
                    viewer.getTable().getItemHeight();

                    // dnLink.setText("<a>"+sr.getDn().toString()+"</a>");
                    dnLink.setData( sr );
                    dnLink.setText( sr.getDn().toString() );
                    dnLink.setUnderlined( true );
                    dnLink.setFont( item.getFont() );
                    dnLink.setForeground( item.getForeground() );
                    dnLink.setBackground( item.getBackground() );
                    dnLink.setBounds( item.getBounds( 0 ) );
                    tableEditor.setEditor( dnLink, item, 0 );

                    linkVisible = true;
                }

            }

            if ( !linkVisible )
            {
                dnLink.setVisible( false );
                tableEditor.setEditor( null );
            }
        }
    }

    IContextActivation contextActivation;


    public void partActivated( IWorkbenchPart part )
    {
        if ( part == this.editor )
        {

            this.editor.getActionGroup().activateGlobalActionHandlers();

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextActivation = contextService
                .activateContext( "org.apache.directory.ldapstudio.browser.action.context" );

        }
    }


    public void partDeactivated( IWorkbenchPart part )
    {
        if ( part == this.editor && contextActivation != null )
        {

            IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                IContextService.class );
            contextService.deactivateContext( contextActivation );
            contextActivation = null;

            this.editor.getActionGroup().deactivateGlobalActionHandlers();

        }
    }


    public void partOpened( IWorkbenchPart part )
    {
    }


    public void partClosed( IWorkbenchPart part )
    {
    }


    public void partBroughtToTop( IWorkbenchPart part )
    {
    }

}
