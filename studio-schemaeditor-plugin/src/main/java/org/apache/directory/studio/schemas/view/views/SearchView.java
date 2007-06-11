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

package org.apache.directory.studio.schemas.view.views;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.controller.SchemasViewController;
import org.apache.directory.studio.schemas.controller.actions.EraseSearchAction;
import org.apache.directory.studio.schemas.model.AttributeType;
import org.apache.directory.studio.schemas.model.LDAPModelEvent;
import org.apache.directory.studio.schemas.model.ObjectClass;
import org.apache.directory.studio.schemas.model.PoolListener;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditorInput;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditor;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This class represents the Search View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchView extends ViewPart implements PoolListener
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.SearchView"; //$NON-NLS-1$

    /** The Schema Pool */
    private SchemaPool pool;

    // UI fields
    private Table resultsTable;
    private TableViewer resultsTableViewer;
    private Combo searchField;
    private Combo scopeCombo;
    private SearchViewContentProvider searchContentProvider;

    /** The Type column */
    private final String TYPE_COLUMN = Messages.getString( "SearchView.Type_Column" ); //$NON-NLS-1$

    /** The Name column*/
    private final String NAME_COLUMN = Messages.getString( "SearchView.Name_Column" ); //$NON-NLS-1$

    /** The Schema column */
    private final String SCHEMA_COLUMN = Messages.getString( "SearchView.Schema_Column" ); //$NON-NLS-1$

    /** The Columns names Array */
    private String[] columnNames = new String[]
        { TYPE_COLUMN, NAME_COLUMN, SCHEMA_COLUMN, };

    /** The Search All scope */
    public static final String SEARCH_ALL = Messages.getString( "SearchView.Search_All_metadata" ); //$NON-NLS-1$

    /** The Search Name scope */
    public static final String SEARCH_NAME = Messages.getString( "SearchView.Search_Name" ); //$NON-NLS-1$

    /** The Search OID scope */
    public static final String SEARCH_OID = Messages.getString( "SearchView.Search_OID" ); //$NON-NLS-1$

    /** The Search Description scope */
    public static final String SEARCH_DESC = Messages.getString( "SearchView.Search_Description" ); //$NON-NLS-1$

    /** The current Search type */
    public static String currentSearchScope = SEARCH_ALL;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent )
    {
        this.pool = SchemaPool.getInstance();
        //we want to be notified if the pool has been modified
        pool.addListener( this );

        //top container
        Composite top = new Composite( parent, SWT.NONE );

        GridLayout layout = new GridLayout( 4, false );
        top.setLayout( layout );

        Label searchLabel = new Label( top, SWT.NONE );
        searchLabel.setText( Messages.getString( "SearchView.Search" ) ); //$NON-NLS-1$

        //search field
        searchField = new Combo( top, SWT.DROP_DOWN | SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL, 0, true, false );
        gridData.verticalAlignment = SWT.CENTER;
        searchField.setLayoutData( gridData );

        Label inLabel = new Label( top, SWT.NONE );
        inLabel.setText( Messages.getString( "SearchView.in" ) ); //$NON-NLS-1$

        //search scope combo
        scopeCombo = new Combo( top, SWT.READ_ONLY | SWT.SINGLE );

        gridData = new GridData( SWT.FILL, 0, false, false );
        gridData.verticalAlignment = SWT.CENTER;
        scopeCombo.setLayoutData( gridData );
        scopeCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                currentSearchScope = scopeCombo.getText();
                resultsTableViewer.refresh();
            }
        } );
        scopeCombo.add( SEARCH_ALL, 0 );
        scopeCombo.add( SEARCH_NAME, 1 );
        scopeCombo.add( SEARCH_OID, 2 );
        scopeCombo.add( SEARCH_DESC, 3 );
        scopeCombo.select( 0 );

        // Create the table 
        createTable( top );
        createTableViewer();
        this.searchContentProvider = new SearchViewContentProvider();
        resultsTableViewer.setContentProvider( searchContentProvider );
        resultsTableViewer.setLabelProvider( new TableDecoratingLabelProvider( new SearchViewLabelProvider(), Activator
            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );

        initSearchHistory();
        initListeners();
        initToolbar();
    }


    private void initToolbar()
    {
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( new EraseSearchAction( this ) );
    }


    /**
     * Creates the Table
     */
    private void createTable( Composite parent )
    {
        resultsTable = new Table( parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );

        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true, 4, 1 );
        resultsTable.setLayoutData( gridData );

        resultsTable.setLinesVisible( false );
        resultsTable.setHeaderVisible( true );

        // 1st column with image - NOTE: The SWT.CENTER has no effect!!
        TableColumn column = new TableColumn( resultsTable, SWT.CENTER, 0 );
        column.setText( columnNames[0] );
        column.setWidth( 40 );

        // 2nd column with name
        column = new TableColumn( resultsTable, SWT.LEFT, 1 );
        column.setText( columnNames[1] );
        column.setWidth( 400 );

        //3rd column with element defining schema
        column = new TableColumn( resultsTable, SWT.LEFT, 2 );
        column.setText( columnNames[2] );
        column.setWidth( 100 );
    }


    /**
     * Initializes the Listeners
     */
    private void initListeners()
    {
        searchField.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                resultsTableViewer.setInput( searchField.getText() );
            }
        } );

        searchField.addKeyListener( new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                if ( e.keyCode == 13 )
                {
                    resultsTable.setFocus();
                }
            }
        } );

        searchField.addFocusListener( new FocusAdapter()
        {
            public void focusLost( FocusEvent e )
            {
                if ( !"".equals( searchField.getText() ) ) //$NON-NLS-1$
                {
                    String searchString = searchField.getText();
                    saveHistory( PluginConstants.PREFS_SEARCH_VIEW_SEARCH_HISTORY, searchString );
                    initSearchHistory();
                    searchField.setText( searchString );
                }
            }
        } );

        scopeCombo.addFocusListener( new FocusAdapter()
        {
            public void focusGained( FocusEvent arg0 )
            {
                resultsTable.setFocus();
            }
        } );

        resultsTable.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                openEditor( ( Table ) e.getSource() );
            }
        } );

        resultsTable.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_UP )
                {
                    searchField.setFocus();
                }

                if ( e.keyCode == 13 ) // return key
                {
                    openEditor( ( Table ) e.getSource() );
                }
            }
        } );

        resultsTable.addFocusListener( new FocusAdapter()
        {
            public void focusGained( FocusEvent e )
            {
                if ( ( resultsTable.getSelectionCount() == 0 ) && ( resultsTable.getItemCount() != 0 ) )
                {
                    resultsTable.select( 0 );
                }
            }
        } );
    }


    /**
     * Open the editor associated with the current selection in the table
     *
     * @param table
     *      the associated table
     */
    private void openEditor( Table table )
    {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IEditorInput input = null;
        String editorId = null;

        // Here is the double clicked item
        Object item = table.getSelection()[0].getData();
        if ( item instanceof AttributeType )
        {
            input = new AttributeTypeEditorInput( ( AttributeType ) item );
            editorId = AttributeTypeEditor.ID;
        }
        else if ( item instanceof ObjectClass )
        {
            input = new ObjectClassEditorInput( ( ObjectClass ) item );
            editorId = ObjectClassEditor.ID;
        }

        // Let's open the editor
        if ( input != null )
        {
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemasViewController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    }


    /**
     * Creates the TableViewer 
     */
    private void createTableViewer()
    {
        resultsTableViewer = new TableViewer( resultsTable );
        resultsTableViewer.setUseHashlookup( true );
        resultsTableViewer.setColumnProperties( columnNames );
    }


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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.model.PoolListener#poolChanged(org.apache.directory.studio.schemas.model.SchemaPool, org.apache.directory.studio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        resultsTableViewer.refresh();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
     */
    public void setPartName( String partName )
    {
        super.setPartName( partName );
    }


    /**
     * Initializes the Search History.
     */
    private void initSearchHistory()
    {
        searchField.setItems( loadHistory( PluginConstants.PREFS_SEARCH_VIEW_SEARCH_HISTORY ) );
    }


    /**
     * Saves to the History.
     *
     * @param key
     *      the key to save to
     * @param value
     *      the value to save
     */
    public static void saveHistory( String key, String value )
    {
        // get current history
        String[] history = loadHistory( key );
        List<String> list = new ArrayList<String>( Arrays.asList( history ) );

        // add new value or move to first position
        if ( list.contains( value ) )
        {
            list.remove( value );
        }
        list.add( 0, value );

        // check history size
        while ( list.size() > 15 )
        {
            list.remove( list.size() - 1 );
        }

        // save
        history = ( String[] ) list.toArray( new String[list.size()] );
        Activator.getDefault().getDialogSettings().put( key, history );

    }


    /**
     * Loads History
     *
     * @param key
     *      the preference key
     * @return
     */
    public static String[] loadHistory( String key )
    {
        String[] history = Activator.getDefault().getDialogSettings().getArray( key );
        if ( history == null )
        {
            history = new String[0];
        }
        return history;
    }


    public void setSearch( String searchString, String scope )
    {
        scopeCombo.setText( scope );
        currentSearchScope = scopeCombo.getText();
        searchField.setText( searchString );
        resultsTableViewer.setInput( searchString );
        resultsTable.setFocus();
    }
}
