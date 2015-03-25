package org.apache.directory.studio.openldap.config.editor.databases;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.model.OlcBdbConfig;
import org.apache.directory.studio.openldap.config.model.OlcConfig;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;
import org.apache.directory.studio.openldap.config.model.OlcHdbConfig;
import org.apache.directory.studio.openldap.config.model.OlcLdifConfig;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;


/**
 * This class represents the Databases Master/Details Block used in the Databases Page.
 */
public class DatabasesMasterDetailsBlock extends MasterDetailsBlock
{
    /** The associated page */
    private DatabasesPage page;

    // UI Fields
    private TableViewer viewer;
    private Button addButton;
    private Button deleteButton;


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

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "All Databases" );
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout( layout );
        toolkit.paintBordersFor( client );
        section.setClient( client );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( client, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
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
                managedForm.fireSelectionChanged( spart, event.getSelection() );
            }
        } );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof OlcDatabaseConfig )
                {
                    OlcDatabaseConfig database = ( OlcDatabaseConfig ) element;

                    return database.getOlcDatabase();
                }

                return super.getText( element );
            };


            public Image getImage( Object element )
            {
                if ( element instanceof OlcDatabaseConfig )
                {
                    return OpenLdapConfigurationPlugin.getDefault().getImage(
                        OpenLdapConfigurationPluginConstants.IMG_DATABASE );
                }

                return super.getImage( element );
            };
        } );

        // Creating the button(s)
        addButton = toolkit.createButton( client, "Add", SWT.PUSH );
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        initFromInput();
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        OpenLdapConfiguration configuration = page.getConfiguration();

        List<OlcConfig> configurationElements = configuration.getConfigurationElements();
        List<OlcDatabaseConfig> databaseConfigurationElements = new ArrayList<OlcDatabaseConfig>();
        for ( OlcConfig configurationElement : configurationElements )
        {
            if ( configurationElement instanceof OlcDatabaseConfig )
            {
                databaseConfigurationElements.add( ( OlcDatabaseConfig ) configurationElement );
            }
        }

        viewer.setInput( databaseConfigurationElements.toArray( new OlcDatabaseConfig[0] ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        DatabasesDetailsPage olcDatabaseConfigDetailsPage = new DatabasesDetailsPage( this );
        detailsPart.registerPage( OlcDatabaseConfig.class, olcDatabaseConfigDetailsPage );

        DatabasesDetailsPage olcBdbConfigDetailsPage = new DatabasesDetailsPage( this );
        detailsPart.registerPage( OlcBdbConfig.class, olcBdbConfigDetailsPage );

        DatabasesDetailsPage olcHdbConfigDetailsPage = new DatabasesDetailsPage( this );
        detailsPart.registerPage( OlcHdbConfig.class, olcHdbConfigDetailsPage );

        DatabasesDetailsPage olcLdifConfigDetailsPage = new DatabasesDetailsPage( this );
        detailsPart.registerPage( OlcLdifConfig.class, olcLdifConfigDetailsPage );
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
}
