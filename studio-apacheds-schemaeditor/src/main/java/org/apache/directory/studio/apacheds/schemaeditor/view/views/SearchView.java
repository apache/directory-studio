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

package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SearchViewController;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Search View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchView extends ViewPart
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.SearchView"; //$NON-NLS-1$

    // UI fields
    private Table resultsTable;
    private TableViewer resultsTableViewer;
    private Text searchField;
    private Combo scopeCombo;
    //    private SearchViewContentProvider searchContentProvider;

    /** The Type column */
    private final String TYPE_COLUMN = "Type";

    /** The Name column*/
    private final String NAME_COLUMN = "Name";

    /** The Schema column */
    private final String SCHEMA_COLUMN = "Schema";

    /** The Columns names Array */
    private String[] columnNames = new String[]
        { TYPE_COLUMN, NAME_COLUMN, SCHEMA_COLUMN, };

    private Composite searchFieldInnerComposite;

    private Label separatorLabel;

    private Composite parent;

    private Composite searchFieldComposite;

    /** The scope */
    public static final String SCOPE = "Scope";

    /** The Search All scope */
    public static final String SEARCH_ALL = "All Metadata";

    /** The Search Name scope */
    public static final String SEARCH_NAME = "Name";

    /** The Search OID scope */
    public static final String SEARCH_OID = "OID";

    /** The Search Description scope */
    public static final String SEARCH_DESC = "Description";

    /** The current Search type */
    public static String currentSearchScope = SEARCH_ALL;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent )
    {
        this.parent = parent;
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        parent.setLayout( gridLayout );

        // Search Field
        searchFieldComposite = new Composite( parent, SWT.NONE );
        gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        searchFieldComposite.setLayout( gridLayout );
        searchFieldComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        // This searchFieldCompositeSeparator is used to display correctly the searchFieldComposite,
        // since an empty composite does not display well.
        Label searchFieldCompositeSeparator = new Label( searchFieldComposite, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 1;
        searchFieldCompositeSeparator.setLayoutData( gridData );
        searchFieldCompositeSeparator.setVisible( false );

        // Search Results Label
        Label searchResultsLabel = new Label( parent, SWT.NONE );
        searchResultsLabel.setText( "'searchString' - X matches in workspace" );
        searchResultsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Separator Label
        Label separatorLabel2 = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel2.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Create the table 
        createTable();

        //        this.searchContentProvider = new SearchViewContentProvider();
        //        resultsTableViewer.setContentProvider( searchContentProvider );
        //        resultsTableViewer.setLabelProvider( new TableDecoratingLabelProvider( new SearchViewLabelProvider(), Activator
        //            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );

        //        initSearchHistory();
        //        initListeners();
        //        initToolbar();

        new SearchViewController( this );
    }


    /**
     * Create the Search Field Sections.
     */
    private void createSearchField()
    {
        // Search Inner Composite
        searchFieldInnerComposite = new Composite( searchFieldComposite, SWT.NONE );
        GridLayout searchFieldInnerCompositeGridLayout = new GridLayout( 4, false );
        searchFieldInnerCompositeGridLayout.horizontalSpacing = 1;
        searchFieldInnerCompositeGridLayout.verticalSpacing = 1;
        searchFieldInnerCompositeGridLayout.marginHeight = 1;
        searchFieldInnerCompositeGridLayout.marginWidth = 2;
        searchFieldInnerComposite.setLayout( searchFieldInnerCompositeGridLayout );
        searchFieldInnerComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Label
        Label searchFieldLabel = new Label( searchFieldInnerComposite, SWT.NONE );
        searchFieldLabel.setText( "Search:" );

        // Search Text Field
        searchField = new Text( searchFieldInnerComposite, SWT.BORDER );
        searchField.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Search Scope Toolbar
        final ToolBar scopeToolBar = new ToolBar( searchFieldInnerComposite, SWT.HORIZONTAL | SWT.FLAT );
        // Creating the Search Scope ToolItem
        final ToolItem scopeToolItem = new ToolItem( scopeToolBar, SWT.DROP_DOWN );
        scopeToolItem.setText( "Scope" );
        // Creating the associated Menu
        final Menu scopeMenu = new Menu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.POP_UP );
        // Adding the action to display the Menu when the item is clicked
        scopeToolItem.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                Rectangle rect = scopeToolItem.getBounds();
                Point pt = new Point( rect.x, rect.y + rect.height );
                pt = scopeToolBar.toDisplay( pt );
                scopeMenu.setLocation( pt.x, pt.y );
                scopeMenu.setVisible( true );
            }
        } );
        MenuItem item = new MenuItem( scopeMenu, SWT.CHECK );
        item.setSelection( true );
        item.setText( "Aliases" );

        // Search Button
        Button searchButton = new Button( searchFieldInnerComposite, SWT.PUSH | SWT.DOWN );
        searchButton.setImage( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SEARCH ).createImage() );
        searchButton.setToolTipText( "Search" );

        // Separator Label
        separatorLabel = new Label( searchFieldComposite, SWT.SEPARATOR | SWT.HORIZONTAL );
        separatorLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Table.
     */
    private void createTable()
    {
        resultsTable = new Table( parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );

        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        resultsTable.setLayoutData( gridData );

        resultsTable.setLinesVisible( false );
        resultsTable.setHeaderVisible( true );

        // 1st column with image
        TableColumn column = new TableColumn( resultsTable, SWT.CENTER, 0 );
        column.setText( columnNames[0] );
        column.setWidth( 40 );

        // 2nd column with name
        column = new TableColumn( resultsTable, SWT.LEFT, 1 );
        column.setText( columnNames[1] );
        column.setWidth( 400 );

        // 3rd column with element defining schema
        column = new TableColumn( resultsTable, SWT.LEFT, 2 );
        column.setText( columnNames[2] );
        column.setWidth( 100 );

        // Creating the TableViewer
        resultsTableViewer = new TableViewer( resultsTable );
        resultsTableViewer.setUseHashlookup( true );
        resultsTableViewer.setColumnProperties( columnNames );
    }


    //    /**
    //     * Initializes the Listeners
    //     */
    //    private void initListeners()
    //    {
    //        searchField.addModifyListener( new ModifyListener()
    //        {
    //            public void modifyText( ModifyEvent e )
    //            {
    //                resultsTableViewer.setInput( searchField.getText() );
    //            }
    //        } );
    //
    //        searchField.addKeyListener( new KeyAdapter()
    //        {
    //            public void keyReleased( KeyEvent e )
    //            {
    //                if ( e.keyCode == 13 )
    //                {
    //                    resultsTable.setFocus();
    //                }
    //            }
    //        } );
    //
    //        searchField.addFocusListener( new FocusAdapter()
    //        {
    //            public void focusLost( FocusEvent e )
    //            {
    //                if ( !"".equals( searchField.getText() ) ) //$NON-NLS-1$
    //                {
    //                    String searchString = searchField.getText();
    //                    saveHistory( PluginConstants.PREFS_SEARCH_VIEW_SEARCH_HISTORY, searchString );
    //                    initSearchHistory();
    //                    searchField.setText( searchString );
    //                }
    //            }
    //        } );
    //
    //        scopeCombo.addFocusListener( new FocusAdapter()
    //        {
    //            public void focusGained( FocusEvent arg0 )
    //            {
    //                resultsTable.setFocus();
    //            }
    //        } );
    //
    //        resultsTable.addMouseListener( new MouseAdapter()
    //        {
    //            public void mouseDoubleClick( MouseEvent e )
    //            {
    //                openEditor( ( Table ) e.getSource() );
    //            }
    //        } );
    //
    //        resultsTable.addKeyListener( new KeyAdapter()
    //        {
    //            public void keyPressed( KeyEvent e )
    //            {
    //                if ( e.keyCode == SWT.ARROW_UP )
    //                {
    //                    searchField.setFocus();
    //                }
    //
    //                if ( e.keyCode == 13 ) // return key
    //                {
    //                    openEditor( ( Table ) e.getSource() );
    //                }
    //            }
    //        } );
    //
    //        resultsTable.addFocusListener( new FocusAdapter()
    //        {
    //            public void focusGained( FocusEvent e )
    //            {
    //                if ( ( resultsTable.getSelectionCount() == 0 ) && ( resultsTable.getItemCount() != 0 ) )
    //                {
    //                    resultsTable.select( 0 );
    //                }
    //            }
    //        } );
    //    }

    //    /**
    //     * Open the editor associated with the current selection in the table
    //     *
    //     * @param table
    //     *      the associated table
    //     */
    //    private void openEditor( Table table )
    //    {
    //        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    //
    //        IEditorInput input = null;
    //        String editorId = null;
    //
    //        // Here is the double clicked item
    //        Object item = table.getSelection()[0].getData();
    //        if ( item instanceof AttributeType )
    //        {
    //            input = new AttributeTypeEditorInput( ( AttributeType ) item );
    //            editorId = AttributeTypeEditor.ID;
    //        }
    //        else if ( item instanceof ObjectClass )
    //        {
    //            input = new ObjectClassEditorInput( ( ObjectClass ) item );
    //            editorId = ObjectClassEditor.ID;
    //        }
    //
    //        // Let's open the editor
    //        if ( input != null )
    //        {
    //            try
    //            {
    //                page.openEditor( input, editorId );
    //            }
    //            catch ( PartInitException exception )
    //            {
    //                Logger.getLogger( SchemasViewController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
    //            }
    //        }
    //    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus()
    {
        if ( searchField != null && !searchField.isDisposed() )
        {
            searchField.setFocus();
        }
    }


    //    /* (non-Javadoc)
    //     * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
    //     */
    //    public void setPartName( String partName )
    //    {
    //        super.setPartName( partName );
    //    }

    //    /**
    //     * Initializes the Search History.
    //     */
    //    private void initSearchHistory()
    //    {
    //        searchField.setItems( loadHistory( PluginConstants.PREFS_SEARCH_VIEW_SEARCH_HISTORY ) );
    //    }
    //
    //
    //    /**
    //     * Saves to the History.
    //     *
    //     * @param key
    //     *      the key to save to
    //     * @param value
    //     *      the value to save
    //     */
    //    public static void saveHistory( String key, String value )
    //    {
    //        // get current history
    //        String[] history = loadHistory( key );
    //        List<String> list = new ArrayList<String>( Arrays.asList( history ) );
    //
    //        // add new value or move to first position
    //        if ( list.contains( value ) )
    //        {
    //            list.remove( value );
    //        }
    //        list.add( 0, value );
    //
    //        // check history size
    //        while ( list.size() > 15 )
    //        {
    //            list.remove( list.size() - 1 );
    //        }
    //
    //        // save
    //        history = ( String[] ) list.toArray( new String[list.size()] );
    //        Activator.getDefault().getDialogSettings().put( key, history );
    //
    //    }
    //
    //
    //    /**
    //     * Loads History
    //     *
    //     * @param key
    //     *      the preference key
    //     * @return
    //     */
    //    public static String[] loadHistory( String key )
    //    {
    //        String[] history = Activator.getDefault().getDialogSettings().getArray( key );
    //        if ( history == null )
    //        {
    //            history = new String[0];
    //        }
    //        return history;
    //    }
    //
    //
    //    public void setSearch( String searchString, String scope )
    //    {
    //        scopeCombo.setText( scope );
    //        currentSearchScope = scopeCombo.getText();
    //        searchField.setText( searchString );
    //        resultsTableViewer.setInput( searchString );
    //        resultsTable.setFocus();
    //    }

    /**
     * Shows the Search Field Section.
     */
    public void showSearchFieldSection()
    {
        createSearchField();
        parent.layout( true, true );
        searchField.setFocus();
    }


    /**
     * Hides the Search Field Section.
     */
    public void hideSearchFieldSection()
    {
        if ( searchFieldInnerComposite != null )
        {
            searchFieldInnerComposite.dispose();
            searchFieldInnerComposite = null;
        }
        if ( separatorLabel != null )
        {
            separatorLabel.dispose();
            separatorLabel = null;
        }
        parent.layout( true, true );
        resultsTable.setFocus();
    }
}
