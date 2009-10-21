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
package org.apache.directory.studio.apacheds.configuration.editor.v155;


import java.util.List;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.v155.Partition;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Partitions Master/Details Block used in the Partitions Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PartitionsMasterDetailsBlock extends MasterDetailsBlock
{
    /** The associated page */
    private FormPage page;

    /** The input Server Configuration */
    private ServerConfigurationV155 serverConfiguration;

    /** The Interceptors List */
    private List<Partition> partitions;

    /** The Details Page */
    private PartitionDetailsPage detailsPage;

    private static final String NEW_ID = Messages.getString( "PartitionsMasterDetailsBlock.NewPartition" ); //$NON-NLS-1$

    // UI Fields
    private TableViewer viewer;
    private Button addButton;
    private Button deleteButton;


    /**
     * Creates a new instance of PartitionsMasterDetailsBlock.
     *
     * @param page
     *      the associated page
     */
    public PartitionsMasterDetailsBlock( FormPage page )
    {
        this.page = page;
        serverConfiguration = ( ServerConfigurationV155 ) ( ( ServerConfigurationEditor ) page.getEditor() )
            .getServerConfiguration();
        partitions = serverConfiguration.getPartitions();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "PartitionsMasterDetailsBlock.AllPartitions" ) ); //$NON-NLS-1$
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

        // Creatig the Table and Table Viewer
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
            public Image getImage( Object element )
            {

                return ApacheDSConfigurationPlugin
                    .getDefault()
                    .getImage(
                        ( ( Partition ) element ).isSystemPartition() ? ApacheDSConfigurationPluginConstants.IMG_PARTITION_SYSTEM
                            : ApacheDSConfigurationPluginConstants.IMG_PARTITION );
            }
        } );

        // Creating the button(s)
        addButton = toolkit.createButton( client, Messages.getString( "PartitionsMasterDetailsBlock.Add" ), SWT.PUSH ); //$NON-NLS-1$
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client,
            Messages.getString( "PartitionsMasterDetailsBlock.Delete" ), SWT.PUSH ); //$NON-NLS-1$
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        initFromInput();
        addListeners();
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        viewer.setInput( partitions );
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                viewer.refresh();

                deleteButton.setEnabled( !event.getSelection().isEmpty() );
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Partition partition = ( Partition ) selection.getFirstElement();
                    if ( partition.isSystemPartition() )
                    {
                        deleteButton.setEnabled( false );
                    }
                }
            }
        } );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                Partition newPartition = new Partition( getNewId() );
                partitions.add( newPartition );
                viewer.refresh();
                viewer.setSelection( new StructuredSelection( newPartition ) );
                setEditorDirty();
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    Partition partition = ( Partition ) selection.getFirstElement();
                    if ( !partition.isSystemPartition() )
                    {
                        partitions.remove( partition );
                        viewer.refresh();
                        setEditorDirty();
                    }
                }
            }
        } );
    }


    /**
     * Gets a new ID for a new Partition.
     *
     * @return 
     *      a new ID for a new Partition
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

            for ( Partition partition : partitions )
            {
                if ( partition.getId().equalsIgnoreCase( name ) )
                {
                    ok = false;
                }
            }
            counter++;
        }

        return name;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions( IManagedForm managedForm )
    {
        final ScrolledForm form = managedForm.getForm();

        // Horizontal layout Action
        Action horizontalAction = new Action( "Horizontal layout", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.HORIZONTAL );
                form.reflow( true );
            }
        };
        horizontalAction.setChecked( true );
        horizontalAction.setToolTipText( "Horizontal Orientation" ); //$NON-NLS-1$
        horizontalAction.setImageDescriptor( ApacheDSConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDSConfigurationPluginConstants.IMG_HORIZONTAL_ORIENTATION ) );

        // Vertical layout Action
        Action verticalAction = new Action( "Vertical Orientation", Action.AS_RADIO_BUTTON ) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation( SWT.VERTICAL );
                form.reflow( true );
            }
        };
        verticalAction.setChecked( false );
        verticalAction.setToolTipText( "Vertical Orientation" ); //$NON-NLS-1$
        verticalAction.setImageDescriptor( ApacheDSConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDSConfigurationPluginConstants.IMG_VERTICAL_ORIENTATION ) );

        form.getToolBarManager().add( horizontalAction );
        form.getToolBarManager().add( verticalAction );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new PartitionDetailsPage( this );
        detailsPart.registerPage( Partition.class, detailsPage );
    }


    /**
     * Sets the Editor as dirty.
     */
    public void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) page.getEditor() ).setDirty( true );
    }


    /**
     * Saves the necessary elements to the input model.
     */
    public void save()
    {
        detailsPage.commit( true );
        viewer.setInput( partitions );
    }
}
