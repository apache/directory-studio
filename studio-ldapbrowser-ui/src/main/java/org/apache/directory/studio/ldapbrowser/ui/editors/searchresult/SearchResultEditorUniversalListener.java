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


/**
 * The SearchResultEditorUniversalListener manages all events for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.actions.OpenSearchResultAction;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;


public class SearchResultEditorUniversalListener implements SearchUpdateListener, EntryUpdateListener
{

    /** The search result editor */
    private SearchResultEditor editor;

    /** The table viewer */
    private TableViewer viewer;

    /** The cursor */
    private SearchResultEditorCursor cursor;

    /** The action used to start the default value editor */
    private OpenBestEditorAction startEditAction;

    /** The selected search that is displayed in the search result editor */
    private ISearch selectedSearch;

    /** The hyperlink used for DNs */
    private Hyperlink dnLink;

    /** The table editor, used to display the hyperlink */
    private TableEditor tableEditor;

    /** Token used to activate and deactivate shortcuts in the editor */
    private IContextActivation contextActivation;

    /** Listener that listens for selections of ISearch objects. */
    private INullSelectionListener searchSelectionListener = new INullSelectionListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation sets the editor's input when a search is selected.
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            if ( editor != null && part != null )
            {
                if ( editor.getSite().getWorkbenchWindow() == part.getSite().getWorkbenchWindow() )
                {
                    ISearch[] searches = BrowserSelectionUtils.getSearches( selection );
                    Object[] objects = BrowserSelectionUtils.getObjects( selection );
                    if ( searches.length == 1 && objects.length == 1 )
                    {
                        editor.setInput( new SearchResultEditorInput( searches[0] ) );
                    }
                    else
                    {
                        editor.setInput( new SearchResultEditorInput( null ) );
                    }
                }
            }
        }
    };

    /** The part listener used to activate and deactivate the shortcuts */
    private IPartListener2 partListener = new IPartListener2()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation deactivates the shortcuts when the part is deactivated.
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
            if ( partRef.getPart( false ) == editor && contextActivation != null )
            {
                editor.getActionGroup().deactivateGlobalActionHandlers();

                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextService.deactivateContext( contextActivation );
                contextActivation = null;
            }
        }


        /**
         * {@inheritDoc}
         *
         * This implementation activates the shortcuts when the part is activated.
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
            if ( partRef.getPart( false ) == editor )
            {
                IContextService contextService = ( IContextService ) PlatformUI.getWorkbench().getAdapter(
                    IContextService.class );
                contextActivation = contextService
                    .activateContext( BrowserCommonConstants.CONTEXT_WINDOWS );

                editor.getActionGroup().activateGlobalActionHandlers();
            }
        }


        /**
         * {@inheritDoc}
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }
    };

    /** The listener used to handle clicks to the DN hyper link */
    private IHyperlinkListener dnHyperlinkListener = new IHyperlinkListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation opens the search result when clicking thd DN link.
         */
        public void linkActivated( HyperlinkEvent e )
        {
            ISearchResult sr = ( ISearchResult ) e.widget.getData();
            OpenSearchResultAction action = new OpenSearchResultAction();
            action.setSelectedSearchResults( new ISearchResult[]
                { sr } );
            action.run();
        }


        /**
         * {@inheritDoc}
         */
        public void linkEntered( HyperlinkEvent e )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void linkExited( HyperlinkEvent e )
        {
        }
    };

    /** This listener removes the DN link when then mouse exits the hyperlink control */
    private MouseTrackListener dnMouseTrackListener = new MouseTrackListener()
    {
        /**
         * {@inheritDoc}
         */
        public void mouseEnter( MouseEvent e )
        {
        }


        /**
         * {@inheritDoc}
         *
         * This implementation removed the DN link.
         */
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
    };

    /** This listener renders the DN hyperlink when the mouse cursor moves over the DN */
    private MouseMoveListener cursorMouseMoveListener = new MouseMoveListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation renders the DN link.
         */
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
    };

    /** This listener renders the DN link when the mouse cursor moves over the DN */
    private MouseMoveListener viewerMouseMoveListener = new MouseMoveListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation renders the DN link.
         */
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
    };

    /** This listener starts the value editor and toggles the cursor's background color */
    private SelectionListener cursorSelectionListener = new SelectionAdapter()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation sets the cursor's background color.
         */
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


        /**
         * {@inheritDoc}
         *
         * This implementation starts the value editor when pressing enter.
         */
        public void widgetDefaultSelected( SelectionEvent e )
        {
            viewer.setSelection( null, true );
            viewer.getTable().setSelection( new TableItem[0] );
            if ( startEditAction.isEnabled() )
                startEditAction.run();
        }
    };

    /** This listener starts the value editor when double-clicking a cell */
    private MouseListener cursorMouseListener = new MouseAdapter()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation starts the value editor when double-clicking a cell.
         */
        public void mouseDoubleClick( MouseEvent e )
        {
            viewer.setSelection( null, true );
            viewer.getTable().setSelection( new TableItem[0] );
            if ( startEditAction.isEnabled() )
                startEditAction.run();
        }


        /**
         * {@inheritDoc}
         */
        public void mouseDown( MouseEvent e )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void mouseUp( MouseEvent e )
        {
        }
    };

    /** This listener starts the value editor when typing */
    private KeyListener cursorKeyListener = new KeyListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation starts the value editor when a non-control key is pressed.
         */
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


        /**
         * {@inheritDoc}
         */
        public void keyReleased( KeyEvent e )
        {
        }
    };


    /**
     * Creates a new instance of SearchResultEditorUniversalListener.
     *
     * @param editor the search result editor
     */
    public SearchResultEditorUniversalListener( SearchResultEditor editor )
    {
        this.editor = editor;

        startEditAction = editor.getActionGroup().getOpenBestEditorAction();
        viewer = editor.getMainWidget().getViewer();
        cursor = editor.getConfiguration().getCursor( viewer );

        // create dn link control
        dnLink = new Hyperlink( viewer.getTable(), SWT.NONE );
        dnLink.setLayoutData( new GridData( SWT.BOTTOM, SWT.LEFT, true, true ) );
        dnLink.setText( "" );
        dnLink.setMenu( viewer.getTable().getMenu() );
        tableEditor = new TableEditor( viewer.getTable() );
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.verticalAlignment = SWT.BOTTOM;
        tableEditor.grabHorizontal = true;
        tableEditor.grabVertical = true;

        // init listeners
        dnLink.addHyperlinkListener( dnHyperlinkListener );
        dnLink.addMouseTrackListener( dnMouseTrackListener );

        cursor.addMouseMoveListener( cursorMouseMoveListener );
        cursor.addSelectionListener( cursorSelectionListener );
        cursor.addMouseListener( cursorMouseListener );
        cursor.addKeyListener( cursorKeyListener );

        viewer.getTable().addMouseMoveListener( viewerMouseMoveListener );

        editor.getSite().getPage().addPartListener( partListener );
        editor.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener( BrowserView.getId(),
            searchSelectionListener );

        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
    }


    /**
     * Disposes the listener.
     */
    public void dispose()
    {
        if ( editor != null )
        {
            editor.getSite().getPage().removePartListener( partListener );
            editor.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(
                BrowserView.getId(), searchSelectionListener );

            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeEntryUpdateListener( this );

            selectedSearch = null;
            startEditAction = null;
            cursor = null;
            viewer = null;
            editor = null;
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the search result editor.
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( selectedSearch == searchUpdateEvent.getSearch() )
        {
            refreshInput();
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the search result editor
     * or starts the value editor if an empty value was added.
     */
    public void entryUpdated( EntryModificationEvent event )
    {

        if ( event instanceof EmptyValueAddedEvent && !editor.getActionGroup().isEditorActive() )
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
            viewer.refresh( true );
            cursor.notifyListeners( SWT.Selection, new Event() );
        }
    }


    /**
     * Sets the input.
     *
     * @param search the search
     */
    void setInput( ISearch search )
    {
        selectedSearch = search;
        refreshInput();
        editor.getActionGroup().setInput( search );
    }


    /**
     * Refreshes the input, makes columns visible or hides columns depending on the number
     * of returning attributes.
     */
    void refreshInput()
    {

        // create at least on column
        ensureColumnCount( 1 );

        // get all columns
        TableColumn[] columns = viewer.getTable().getColumns();

        // number of used columns
        int usedColumns;

        if ( selectedSearch != null )
        {

            // get displayed attributes
            boolean showDn = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN )
                || selectedSearch.getReturningAttributes().length == 0;
            String[] attributes;
            if ( showDn )
            {
                attributes = new String[selectedSearch.getReturningAttributes().length + 1];
                attributes[0] = "DN";
                System.arraycopy( selectedSearch.getReturningAttributes(), 0, attributes, 1, attributes.length - 1 );
            }
            else
            {
                attributes = selectedSearch.getReturningAttributes();
            }

            // create missing columns
            if ( attributes.length > columns.length )
            {
                ensureColumnCount( attributes.length );
                columns = viewer.getTable().getColumns();
            }

            // set column headers
            for ( int i = 0; i < attributes.length; i++ )
            {
                columns[i].setText( attributes[i] );
            }
            viewer.setColumnProperties( attributes );

            // set input
            ( ( SearchResultEditorLabelProvider ) viewer.getLabelProvider() ).inputChanged( selectedSearch, showDn );

            viewer.setInput( selectedSearch );
            // this.viewer.refresh();

            // update cell editors
            CellEditor[] editors = new CellEditor[attributes.length];
            viewer.setCellEditors( editors );

            if ( attributes.length > 0 )
            {
                int width = viewer.getTable().getClientArea().width / attributes.length;
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
            viewer.setInput( null );
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
        editor.getConfiguration().getContentProvider( editor.getMainWidget() ).refresh();

        // this.cursor.setFocus();
    }


    /**
     * Ensures that the table contains at least the number of
     * the requested columns.
     *
     * @param count the requested number of columns
     */
    private void ensureColumnCount( int count )
    {
        TableColumn[] columns = viewer.getTable().getColumns();
        if ( columns.length < count )
        {
            for ( int i = columns.length; i < count; i++ )
            {
                TableColumn column = new TableColumn( viewer.getTable(), SWT.LEFT );
                column.setText( "" );
                column.setWidth( 0 );
                column.setResizable( true );
                column.setMoveable( true );
            }
        }
    }


    /**
     * Renders the DN link.
     *
     * @param item the table item
     */
    private void checkDnLink( TableItem item )
    {

        if ( dnLink == null || dnLink.isDisposed() || tableEditor == null || viewer.getTable().isDisposed()
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
                    dnLink.setText( sr.getDn().getUpName() );
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

}
