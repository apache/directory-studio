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
package org.apache.directory.studio.openldap.config.editor.databases;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.pages.DatabasesPage;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapperLabelProvider;
import org.apache.directory.studio.openldap.config.editor.wrappers.DatabaseWrapperViewerSorter;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Databases Master/Details Block used in the Databases Page. This is the
 * left part of the Database tab :
 * <pre>
 * .------------------------------------------------------------------.
 * | .-------------------------------.                                |
 * | | All Databases                 |                                |
 * | | +------------------+          |                                |
 * | | |DB1               | ( Add  ) |                                |
 * | | |DB2               | (Delete) |                                |
 * | | |DB3               | -------- |                                |
 * | | |                  | (  Up  ) |                                |
 * | | |                  | ( Down ) |                                |
 * | | |                  |          |                                |
 * | | |                  |          |                                |
 * | | |                  |          |                                |
 * | | +------------------+          |                                |
 * | +-------------------------------+                                |
 *'-------------------------------------------------------------------'
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabasesMasterDetailsBlock extends MasterDetailsBlock
{
    private static final String NEW_ID = "database";

    /** The associated page */
    private DatabasesPage page;
    
    /** The Managed form */
    private IManagedForm managedForm;

    /** The details page */
    private DatabasesDetailsPage detailsPage;

    /** The database wrappers */
    private List<DatabaseWrapper> databaseWrappers = new ArrayList<DatabaseWrapper>();

    /** The currently selected object */
    private Object currentSelection;

    // UI Fields
    /** The table listing all the existing databases */
    private TableViewer databaseTableViewer;

    /** The button used to add a new Database */
    private Button addButton;

    /** The button used to delete an existing Database */
    private Button deleteButton;

    /** The button used to move up Database in the list */
    private Button upButton;

    /** The button used to move down Database in the list */
    private Button downButton;

    // Listeners
    /**
     * A listener called when the Database table content has changed. It will enable
     * or disabled button accordingly to the changes.
     */
    private ISelectionChangedListener viewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            if ( !event.getSelection().isEmpty() )
            {
                Object newSelection = ( ( StructuredSelection ) event.getSelection() ).getFirstElement();
    
                if ( newSelection != currentSelection )
                {
                    currentSelection = newSelection;
                    
                    // Only show the details if the database is enabled
                    // 2.5 feature...
                    // TODO : check with the SchemaManager is the olcDisabled AT is present.
                    /*
                    OlcDatabaseConfig database = ((DatabaseWrapper)currentSelection).getDatabase();
                    Boolean disabled = database.getOlcDisabled();

                    if ( ( disabled == null ) || ( disabled == false ) )
                    {
                        detailsPart.commit( false );
                        managedForm.fireSelectionChanged( managedForm.getParts()[0], event.getSelection() );
                        databseViewer.refresh();
                        refreshButtonStates();
                    }
                    */
                    detailsPart.commit( false );
                    managedForm.fireSelectionChanged( managedForm.getParts()[0], event.getSelection() );
                    databaseTableViewer.refresh();
                    refreshButtonStates();
                }
            }
        }
    };

    
    /**
     * A listener called when the Add button is clicked
     */
    private SelectionAdapter addButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addNewDatabase();
        }
    };

    /**
     * A listener called when the Delete button is clicked
     */
    private SelectionAdapter deleteButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteSelectedDatabase();
        }
    };

    /**
     * A listener called when the Up button is clicked
     */
    private SelectionAdapter upButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            moveSelectedDatabaseUp();
        }
    };

    /**
     * A listener called when the Down button is clicked
     */
    private SelectionAdapter downButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            moveSelectedDatabaseDown();
        }
    };


    /**
     * Creates a new instance of DatabasesMasterDetailsBlock.
     *
     * @param page the associated page
     */
    public DatabasesMasterDetailsBlock( DatabasesPage page )
    {
        super();
        this.page = page;
    }


    /**
     * {@inheritDoc}
     */
    public void createContent( IManagedForm managedForm )
    {
        super.createContent( managedForm );

        // Giving the weights of both parts of the SashForm.
        sashForm.setWeights( new int[]
            { 1, 2 } );
    }


    /**
     * Create the form with the list of existing Database, and the button to update it :
     * <pre>
     * .------------------------------.
     * | All Databases                |
     * |+------------------+          |
     * ||DB1               | ( Add  ) |
     * ||DB2               | (Delete) |
     * ||DB3               | -------- |
     * ||                  | (  Up  ) |
     * ||                  | ( Down ) |
     * ||                  |          |
     * ||                  |          |
     * ||                  |          |
     * |+------------------+          |
     * +------------------------------+
     * </pre>
     */
    protected void createMasterPart( IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout() );

        // Creating the Section
        Section section = toolkit.createSection( composite, Section.TITLE_BAR );
        section.setText( "All Databases" );
        Composite client = toolkit.createComposite( section );
        client.setLayout( new GridLayout( 2, false ) );
        toolkit.paintBordersFor( client );
        section.setClient( client );
        section.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 5 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        SectionPart sectionPart = new SectionPart( section );
        this.managedForm = managedForm;
        managedForm.addPart( sectionPart );
        databaseTableViewer = new TableViewer( table );

        databaseTableViewer.setContentProvider( new ArrayContentProvider() );
        databaseTableViewer.setLabelProvider( new DatabaseWrapperLabelProvider() );
        databaseTableViewer.setSorter( new DatabaseWrapperViewerSorter() );
        
        // Add a contextual menu to enable/disable a Database. This is a 2.5 feature.
        // TODO : check with the schemaManager
        /*
        databseViewer.getTable().addMouseListener ( new MouseListener()
            {
                public void mouseUp( MouseEvent e ) 
                {
                    // Nothing to do
                }
                
                public void mouseDown( MouseEvent event ) 
                {
                    if ( event.button == 3 )
                    {
                        Table table = (Table)event.getSource();
                        int selectedItem = table.getSelectionIndex();
                        DatabaseWrapper database = databaseWrappers.get( selectedItem );
                        
                        Menu menu = new Menu( databseViewer.getTable().getShell(), SWT.POP_UP );
                        MenuItem enabled = new MenuItem ( menu, SWT.PUSH );
                        
                        Boolean disabled = database.getDatabase().getOlcDisabled();
                        
                        if ( ( disabled != null ) && ( disabled == true ) )
                        {
                            enabled.setText ( "Enable" );
                        }
                        else
                        {
                            enabled.setText ( "Disable" );
                        }
                        
                        // Add a listener on the menu
                        enabled.addListener( SWT.Selection, new Listener()
                        {
                            @Override
                            public void handleEvent( Event event ) 
                            {
                                // Switch the flag from disabled to enabled, and from enabled to disabled
                                database.getDatabase().setOlcDisabled( ( disabled == null ) || !disabled );
                                databseViewer.refresh();
                            }
                        });
            
                        // draws pop up menu:
                        Point pt = new Point( event.x, event.y );
                        pt = table.toDisplay( pt );
                        menu.setLocation( pt.x, pt.y );
                        menu.setVisible ( true );
                    }
                }
                
                public void mouseDoubleClick( MouseEvent e ) 
                {
                    // Nothing to do
                }
            }
        ); 
        */

        // Creating the button(s)
        addButton = toolkit.createButton( client, "Add", SWT.PUSH );
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        Label separator = BaseWidgetUtils.createSeparator( client, 1 );
        separator.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false ) );

        upButton = toolkit.createButton( client, "Up", SWT.PUSH );
        upButton.setEnabled( false );
        upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        downButton = toolkit.createButton( client, "Down", SWT.PUSH );
        downButton.setEnabled( false );
        downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        initFromInput();
        addListeners();
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        databaseWrappers.clear();

        for ( OlcDatabaseConfig database : page.getConfiguration().getDatabases() )
        {
            databaseWrappers.add( new DatabaseWrapper( database ) );
        }

        databaseTableViewer.setInput( databaseWrappers );
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        initFromInput();
        databaseTableViewer.refresh();
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        databaseTableViewer.addSelectionChangedListener( viewerSelectionChangedListener );
        addButton.addSelectionListener( addButtonSelectionListener );
        deleteButton.addSelectionListener( deleteButtonSelectionListener );
        upButton.addSelectionListener( upButtonSelectionListener );
        downButton.addSelectionListener( downButtonSelectionListener );
    }


    /**
     * This method is called when the 'Add' button is clicked.
     */
    private void addNewDatabase()
    {
        String newId = getNewId();

        OlcDatabaseConfig database = new OlcDatabaseConfig();
        database.setOlcDatabase( "{" + getNewOrderingValue() + "}" + newId );

        try
        {
            database.addOlcSuffix( new Dn( "dc=" + newId + ",dc=com" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch ( LdapInvalidDnException e1 )
        {
            // Will never happen
        }

        DatabaseWrapper databaseWrapper = new DatabaseWrapper( database );
        databaseWrappers.add( databaseWrapper );
        databaseTableViewer.refresh();
        databaseTableViewer.setSelection( new StructuredSelection( databaseWrapper ) );
        setEditorDirty();
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteSelectedDatabase()
    {
        StructuredSelection selection = ( StructuredSelection ) databaseTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            DatabaseWrapper databaseWrapper = ( DatabaseWrapper ) selection.getFirstElement();
            OlcDatabaseConfig database = databaseWrapper.getDatabase();

            if ( MessageDialog.openConfirm( page.getManagedForm().getForm().getShell(), "Confirm Delete",
                NLS.bind( "Are you sure you want to delete database ''{0} ({1})''?",
                    OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() ),
                    getSuffixValue( database ) ) ) )
            {
                databaseWrappers.remove( databaseWrapper );
                setEditorDirty();
            }
        }
    }


    /**
     * Gets the suffix value.
     *
     * @param database the database
     * @return the suffix value
     */
    private String getSuffixValue( OlcDatabaseConfig database )
    {
        String suffix = OpenLdapConfigurationPluginUtils.getFirstValueDn( database.getOlcSuffix() );

        if ( suffix != null )
        {
            return suffix;
        }
        else
        {
            return "none";
        }
    }


    /**
     * Gets a new ID for a new database. They are incremental
     *
     * @return  a new ID for a new database
     */
    private String getNewId()
    {
        int counter = 1;
        String name = NEW_ID;
        boolean ok = false;

        while ( !ok )
        {
            ok = true;
            name = NEW_ID + counter;

            for ( DatabaseWrapper databaseWrapper : databaseWrappers )
            {
                if ( name.equalsIgnoreCase( OpenLdapConfigurationPluginUtils.stripOrderingPrefix( databaseWrapper
                    .getDatabase().getOlcDatabase() ) ) )
                {
                    ok = false;
                }
            }

            counter++;
        }

        return name;
    }


    /**
     * Gets the new ordering value.
     *
     * @return the new ordering value
     */
    private int getNewOrderingValue()
    {
        return getMaxOrderingValue() + 1;
    }


    /**
     * Gets the minimum ordering value.
     *
     * @return the minimum ordering value
     */
    private int getMinOrderingValue()
    {
        int minOrderingValue = Integer.MAX_VALUE;

        for ( DatabaseWrapper databaseWrapper : databaseWrappers )
        {
            if ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( databaseWrapper.getDatabase().getOlcDatabase() ) )
            {
                int databaseOrderingValue = OpenLdapConfigurationPluginUtils.getOrderingPrefix( databaseWrapper
                    .getDatabase().getOlcDatabase() );

                if ( databaseOrderingValue < minOrderingValue )
                {
                    minOrderingValue = databaseOrderingValue;
                }
            }
        }

        return minOrderingValue;
    }


    /**
     * Gets the maximum ordering value.
     *
     * @return the maximum ordering value
     */
    private int getMaxOrderingValue()
    {
        int maxOrderingValue = -1;

        for ( DatabaseWrapper databaseWrapper : databaseWrappers )
        {
            String database = databaseWrapper.getDatabase().getOlcDatabase();
            
            int databaseOrderingValue = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database );

            if ( databaseOrderingValue > maxOrderingValue )
            {
                maxOrderingValue = databaseOrderingValue;
            }
        }

        return maxOrderingValue;
    }


    /**
     * This method is called when the 'Up' button is clicked.
     */
    private void moveSelectedDatabaseUp()
    {
        StructuredSelection selection = ( StructuredSelection ) databaseTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            OlcDatabaseConfig selectedDatabase = ( ( DatabaseWrapper ) selection.getFirstElement() ).getDatabase();
            int selectedDatabaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( selectedDatabase
                .getOlcDatabase() );
            String selectedDatabaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( selectedDatabase
                .getOlcDatabase() );

            OlcDatabaseConfig swapDatabase = findPreviousDatabase( selectedDatabaseOrderingPrefix );

            if ( swapDatabase != null )
            {
                int swapDatabaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( swapDatabase
                    .getOlcDatabase() );
                String swapDatabaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( swapDatabase
                    .getOlcDatabase() );

                selectedDatabase.setOlcDatabase( "{" + swapDatabaseOrderingPrefix + "}" + selectedDatabaseName );
                swapDatabase.setOlcDatabase( "{" + selectedDatabaseOrderingPrefix + "}" + swapDatabaseName );

                databaseTableViewer.refresh();
                refreshButtonStates();
                setEditorDirty();
            }
        }
    }


    /**
     * Finds the previous database.
     *
     * @param orderingPrefix the ordering prefix
     * @return the previous database
     */
    private OlcDatabaseConfig findPreviousDatabase( int orderingPrefix )
    {
        OlcDatabaseConfig selectedDatabase = null;
        int selectedDatabaseOrderingPrefix = Integer.MIN_VALUE;

        for ( DatabaseWrapper databaseWrapper : databaseWrappers )
        {
            int databaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( databaseWrapper
                .getDatabase().getOlcDatabase() );

            if ( ( databaseOrderingPrefix < orderingPrefix )
                && ( databaseOrderingPrefix > selectedDatabaseOrderingPrefix ) )
            {
                selectedDatabase = databaseWrapper.getDatabase();
                selectedDatabaseOrderingPrefix = databaseOrderingPrefix;
            }
        }

        return selectedDatabase;
    }


    /**
     * This method is called when the 'Down' button is clicked.
     */
    private void moveSelectedDatabaseDown()
    {
        StructuredSelection selection = ( StructuredSelection ) databaseTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            OlcDatabaseConfig selectedDatabase = ( ( DatabaseWrapper ) selection.getFirstElement() ).getDatabase();
            int selectedDatabaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( selectedDatabase
                .getOlcDatabase() );
            String selectedDatabaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( selectedDatabase
                .getOlcDatabase() );

            OlcDatabaseConfig swapDatabase = findNextDatabase( selectedDatabaseOrderingPrefix );

            if ( swapDatabase != null )
            {
                int swapDatabaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( swapDatabase
                    .getOlcDatabase() );
                String swapDatabaseName = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( swapDatabase
                    .getOlcDatabase() );

                selectedDatabase.setOlcDatabase( "{" + swapDatabaseOrderingPrefix + "}" + selectedDatabaseName );
                swapDatabase.setOlcDatabase( "{" + selectedDatabaseOrderingPrefix + "}" + swapDatabaseName );

                databaseTableViewer.refresh();
                refreshButtonStates();
                setEditorDirty();
            }
        }
    }


    /**
     * Finds the next database.
     *
     * @param orderingPrefix the ordering prefix
     * @return the next database
     */
    private OlcDatabaseConfig findNextDatabase( int orderingPrefix )
    {
        OlcDatabaseConfig selectedDatabase = null;
        int selectedDatabaseOrderingPrefix = Integer.MAX_VALUE;

        for ( DatabaseWrapper databaseWrapper : databaseWrappers )
        {
            int databaseOrderingPrefix = OpenLdapConfigurationPluginUtils.getOrderingPrefix( databaseWrapper
                .getDatabase().getOlcDatabase() );

            if ( ( databaseOrderingPrefix > orderingPrefix )
                && ( databaseOrderingPrefix < selectedDatabaseOrderingPrefix ) )
            {
                selectedDatabase = databaseWrapper.getDatabase();
                selectedDatabaseOrderingPrefix = databaseOrderingPrefix;
            }
        }

        return selectedDatabase;
    }


    /**
     * Update the button according to the content of the table. If we have
     * no Database, we just enable the Add button. If we only have one Database,
     * we don't enable the Add and Delete buttons. We also enable the Up and
     * Down button accordingly to the selected database : if it's the first one,
     * the Up butto is disabled, if it's the last one, the Down button is
     * disabled.
     */
    private void refreshButtonStates()
    {
        // Getting the selection of the table viewer
        StructuredSelection selection = ( StructuredSelection ) databaseTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            OlcDatabaseConfig database = ( ( DatabaseWrapper ) selection.getFirstElement() ).getDatabase();

            deleteButton.setEnabled( true );
            upButton.setEnabled( getMinOrderingValue() != OpenLdapConfigurationPluginUtils
                .getOrderingPrefix( database.getOlcDatabase() ) );
            downButton.setEnabled( getMaxOrderingValue() != OpenLdapConfigurationPluginUtils
                .getOrderingPrefix( database.getOlcDatabase() ) );
        }
        else
        {
            deleteButton.setEnabled( false );
            upButton.setEnabled( false );
            downButton.setEnabled( false );
        }
    }


    /**
     * Sets the Editor as dirty.
     */
    public void setEditorDirty()
    {
        ( ( OpenLDAPServerConfigurationEditor ) page.getEditor() ).setDirty( true );
        detailsPage.commit( false );
        databaseTableViewer.refresh();
    }


    /**
     * {@inheritDoc}
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new DatabasesDetailsPage( this );
        detailsPart.registerPage( DatabaseWrapper.class, detailsPage );
    }


    /**
     * {@inheritDoc}
     */
    protected void createToolBarActions( IManagedForm managedForm )
    {
        // No toolbar actions
    }


    /**
     * Gets the associated editor page.
     * 
     * @return the associated editor page
     */
    public DatabasesPage getPage()
    {
        return page;
    }


    /**
     * Saves the necessary elements to the input model.
     */
    public void doSave( IProgressMonitor monitor )
    {
        // Committing information on the details page
        detailsPage.commit( true );

        // Saving the databases
        getPage().getConfiguration().clearDatabases();

        for ( DatabaseWrapper databaseWrapper : databaseWrappers )
        {
            getPage().getConfiguration().add( databaseWrapper.getDatabase() );
        }
    }
}
