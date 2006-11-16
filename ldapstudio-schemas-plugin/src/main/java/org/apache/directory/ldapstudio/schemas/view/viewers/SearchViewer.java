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

package org.apache.directory.ldapstudio.schemas.view.viewers;


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.PoolManagerController;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


/**
 * This is the main class for the LDAP Studio Search Tool.
 *
 */
public class SearchViewer extends ViewPart implements PoolListener
{
    public static final String ID = Application.PLUGIN_ID + ".view.SearchViewer"; //$NON-NLS-1$
    private SchemaPool pool;
    private Composite top;
    private Table table;
    private TableViewer tableViewer;
    private Text searchField;
    private Combo typeCombo;
    private SearchContentProvider searchContentProvider;

    //Set the table column property names
    private final String TYPE_COLUMN = Messages.getString( "SearchViewer.Type_Column" ); //$NON-NLS-1$
    private final String NAME_COLUMN = Messages.getString( "SearchViewer.Name_Column" ); //$NON-NLS-1$
    private final String SCHEMA_COLUMN = Messages.getString( "SearchViewer.Schema_Column" ); //$NON-NLS-1$

    // Set column names
    private String[] columnNames = new String[]
        { TYPE_COLUMN, NAME_COLUMN, SCHEMA_COLUMN, };

    //search types
    public static final String SEARCH_ALL = Messages.getString( "SearchViewer.Search_All_metadata" ); //$NON-NLS-1$
    public static final String SEARCH_NAME = Messages.getString( "SearchViewer.Search_Name" ); //$NON-NLS-1$
    public static final String SEARCH_OID = Messages.getString( "SearchViewer.Search_OID" ); //$NON-NLS-1$
    public static final String SEARCH_DESC = Messages.getString( "SearchViewer.Search_Description" ); //$NON-NLS-1$

    //current search type
    public static String searchType = SEARCH_ALL;


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
        this.top = new Composite( parent, SWT.NONE );

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.numColumns = 2;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        top.setLayout( layout );

        //search field
        searchField = new Text( top, SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL, 0, true, false );
        gridData.heightHint = searchField.getLineHeight();
        gridData.verticalAlignment = SWT.CENTER;
        searchField.setLayoutData( gridData );
        searchField.addModifyListener( new ModifyListener()
        {

            public void modifyText( ModifyEvent e )
            {
                tableViewer.setInput( searchField.getText() );
            }

        } );

        //search type combo
        typeCombo = new Combo( top, SWT.READ_ONLY | SWT.SINGLE );

        gridData = new GridData( SWT.FILL, 0, false, false );
        gridData.verticalAlignment = SWT.CENTER;
        typeCombo.setLayoutData( gridData );
        typeCombo.addSelectionListener( new SelectionListener()
        {
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }


            public void widgetSelected( SelectionEvent e )
            {
                searchType = typeCombo.getItem( typeCombo.getSelectionIndex() );
                tableViewer.refresh();
            }
        } );
        typeCombo.add( SEARCH_ALL, 0 );
        typeCombo.add( SEARCH_NAME, 1 );
        typeCombo.add( SEARCH_OID, 2 );
        typeCombo.add( SEARCH_DESC, 3 );
        typeCombo.select( 0 );

        // Create the table 
        createTable( top );
        createTableViewer();
        this.searchContentProvider = new SearchContentProvider();
        tableViewer.setContentProvider( searchContentProvider );
        tableViewer.setLabelProvider( new SearchLabelProvider() );
    }


    /**
     * Creates the Table
     */
    private void createTable( Composite parent )
    {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        table = new Table( parent, style );

        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 );
        //gridData.horizontalSpan = 3;
        table.setLayoutData( gridData );

        table.setLinesVisible( false );
        table.setHeaderVisible( true );

        // 1st column with image - NOTE: The SWT.CENTER has no effect!!
        TableColumn column = new TableColumn( table, SWT.CENTER, 0 );
        column.setText( columnNames[0] );
        column.setWidth( 40 );

        // 2nd column with name
        column = new TableColumn( table, SWT.LEFT, 1 );
        column.setText( columnNames[1] );
        column.setWidth( 400 );
        // Add listener to column so tasks are sorted by name
        column.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                //tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
            }
        } );

        //3rd column with element defining schema
        column = new TableColumn( table, SWT.LEFT, 2 );
        column.setText( columnNames[2] );
        column.setWidth( 100 );
        // Add listener to column so tasks are sorted by schema
        column.addSelectionListener( new SelectionAdapter()
        {

            public void widgetSelected( SelectionEvent e )
            {
                //tableViewer.setSorter(new ExampleTaskSorter(ExampleTaskSorter.OWNER));
            }
        } );
        table.addMouseListener( new MouseListener()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                // Getting the source Table of the double click
                Table sourceTable = ( Table ) e.getSource();

                IEditorInput input = null;
                String editorId = null;

                // Here is the double clicked item
                Object item = sourceTable.getSelection()[0].getData();
                if ( item instanceof AttributeType )
                {
                    input = new AttributeTypeFormEditorInput( ( AttributeType ) item );
                    editorId = AttributeTypeFormEditor.ID;
                }
                else if ( item instanceof ObjectClass )
                {
                    input = new ObjectClassFormEditorInput( ( ObjectClass ) item );
                    editorId = ObjectClassFormEditor.ID;
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
                        Logger.getLogger( PoolManagerController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
            }
        } );
    }


    /**
     * Creates the TableViewer 
     */
    private void createTableViewer()
    {

        tableViewer = new TableViewer( table );
        tableViewer.setUseHashlookup( true );

        tableViewer.setColumnProperties( columnNames );
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
     * @see org.safhaus.ldapstudio.model.PoolListener#poolChanged(org.safhaus.ldapstudio.model.SchemaPool, org.safhaus.ldapstudio.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        searchContentProvider.refresh();
        tableViewer.refresh();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
     */
    public void setPartName( String partName )
    {
        super.setPartName( partName );
    }

}
