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
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
 * This class represents the Databases Master/Details Block used in the Databases Page.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabasesMasterDetailsBlock extends MasterDetailsBlock
{
    private static final String NEW_ID = "database";

    /** The associated page */
    private DatabasesPage page;

    /** The details page */
    private DatabasesDetailsPage detailsPage;

    /** The database wrappers */
    private List<DatabaseWrapper> databaseWrappers = new ArrayList<DatabaseWrapper>();

    /** The currently selected object */
    private Object currentSelection;

    // UI Fields
    /** The table listing all the existing databases */
    private TableViewer viewer;

    /** The button used to add a new Database */
    private Button addButton;

    /** The button used to delete an existing Database */
    private Button deleteButton;

    /** The button used to move up Database in the list */
    private Button upButton;

    /** The button used to move down Database in the list */
    private Button downButton;

    // Listeners
    private ISelectionChangedListener viewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            viewer.refresh();
            refreshButtonStates();
        }
    };
    private SelectionAdapter addButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addNewDatabase();
        }
    };
    private SelectionAdapter deleteButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteSelectedDatabase();
        }
    };
    private SelectionAdapter upButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            moveSelectedDatabaseUp();
        }
    };
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
     * @param page
     *      the associated page
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
     * {@inheritDoc}
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
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
        final SectionPart spart = new SectionPart( section );
        managedForm.addPart( spart );
        viewer = new TableViewer( table );
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                Object newSelection = ( ( StructuredSelection ) event.getSelection() ).getFirstElement();

                if ( newSelection != currentSelection )
                {
                    currentSelection = newSelection;
                    detailsPart.commit( false );
                    managedForm.fireSelectionChanged( spart, event.getSelection() );
                }
            }
        } );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new DatabaseWrapperLabelProvider() );
        viewer.setSorter( new DatabaseWrapperViewerSorter() );

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

        viewer.setInput( databaseWrappers );
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        initFromInput();
        viewer.refresh();
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        viewer.addSelectionChangedListener( viewerSelectionChangedListener );
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
        viewer.refresh();
        viewer.setSelection( new StructuredSelection( databaseWrapper ) );
        setEditorDirty();
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteSelectedDatabase()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

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
     * Gets a new ID for a new database.
     *
     * @return 
     *      a new ID for a new database
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
            if ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( databaseWrapper.getDatabase().getOlcDatabase() ) )
            {
                int databaseOrderingValue = OpenLdapConfigurationPluginUtils.getOrderingPrefix( databaseWrapper
                    .getDatabase().getOlcDatabase() );

                if ( databaseOrderingValue > maxOrderingValue )
                {
                    maxOrderingValue = databaseOrderingValue;
                }
            }
        }

        return maxOrderingValue;
    }


    /**
     * This method is called when the 'Up' button is clicked.
     */
    private void moveSelectedDatabaseUp()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

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

                viewer.refresh();
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
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

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

                viewer.refresh();
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


    private void refreshButtonStates()
    {
        // Getting the selection of the table viewer
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

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
        ( ( ServerConfigurationEditor ) page.getEditor() ).setDirty( true );
        detailsPage.commit( false );
        viewer.refresh();
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
            getPage().getConfiguration().addDatabase( databaseWrapper.getDatabase() );
        }
    }

    /**
     * This class defines a label provider for a database wrapper viewer.
     */
    private class DatabaseWrapperLabelProvider extends LabelProvider
    {
        public String getText( Object element )
        {
            if ( element instanceof DatabaseWrapper )
            {
                OlcDatabaseConfig database = ( ( DatabaseWrapper ) element ).getDatabase();

                return getDatabaseType( database ) + " (" + getSuffix( database ) + ")";
            }

            return super.getText( element );
        };


        public Image getImage( Object element )
        {
            if ( element instanceof DatabaseWrapper )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_DATABASE );
            }

            return super.getImage( element );
        };


        private String getDatabaseType( OlcDatabaseConfig database )
        {
            if ( database != null )
            {
                String databaseType = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() );

                if ( "bdb".equalsIgnoreCase( databaseType ) )
                {
                    return "BDB";
                }
                else if ( "hdb".equalsIgnoreCase( databaseType ) )
                {
                    return "HDB";
                }
                else if ( "mdb".equalsIgnoreCase( databaseType ) )
                {
                    return "MDB";
                }
                else if ( "ldap".equalsIgnoreCase( databaseType ) )
                {
                    return "LDAP";
                }
                else if ( "ldif".equalsIgnoreCase( databaseType ) )
                {
                    return "LDIF";
                }
                else if ( "null".equalsIgnoreCase( databaseType ) )
                {
                    return "Null";
                }
                else if ( "relay".equalsIgnoreCase( databaseType ) )
                {
                    return "Relay";
                }
                else if ( "frontend".equalsIgnoreCase( databaseType ) )
                {
                    return "FrontEnd";
                }
                else if ( "config".equalsIgnoreCase( databaseType ) )
                {
                    return "Config";
                }
                else
                {
                    return "None";
                }
            }

            return null;
        }


        private String getSuffix( OlcDatabaseConfig database )
        {
            if ( database != null )
            {
                List<Dn> suffixes = database.getOlcSuffix();

                if ( ( suffixes != null ) && ( suffixes.size() > 0 ) )
                {
                    return suffixes.get( 0 ).toString();
                }
            }

            return "none";
        }
    }

    /**
     * This class defines a sorter for a database wrapper viewer.
     */
    private class DatabaseWrapperViewerSorter extends ViewerSorter
    {
        public int compare( Viewer viewer, Object e1, Object e2 )
        {
            if ( ( e1 != null ) && ( e2 != null ) && ( e1 instanceof DatabaseWrapper )
                && ( e2 instanceof DatabaseWrapper ) )
            {
                OlcDatabaseConfig database1 = ( ( DatabaseWrapper ) e1 ).getDatabase();
                OlcDatabaseConfig database2 = ( ( DatabaseWrapper ) e2 ).getDatabase();

                if ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database1.getOlcDatabase() )
                    && ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database2.getOlcDatabase() ) ) )
                {
                    int orderingPrefix1 = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database1
                        .getOlcDatabase() );
                    int orderingPrefix2 = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database2
                        .getOlcDatabase() );

                    if ( orderingPrefix1 > orderingPrefix2 )
                    {
                        return Integer.MAX_VALUE;
                    }
                    else if ( orderingPrefix1 < orderingPrefix2 )
                    {
                        return Integer.MIN_VALUE;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else if ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database1.getOlcDatabase() )
                    && ( !OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database2.getOlcDatabase() ) ) )
                {
                    return Integer.MIN_VALUE;
                }
                else if ( !OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database1.getOlcDatabase() )
                    && ( OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database2.getOlcDatabase() ) ) )
                {
                    return Integer.MAX_VALUE;
                }
                else
                {
                    return 1;
                }
            }

            return super.compare( viewer, e1, e2 );
        }
    }
}
