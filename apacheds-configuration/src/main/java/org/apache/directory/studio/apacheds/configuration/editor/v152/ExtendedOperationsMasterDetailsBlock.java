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
package org.apache.directory.studio.apacheds.configuration.editor.v152;


import java.util.List;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.editor.v152.dialogs.ExtendedOperationDialog;
import org.apache.directory.studio.apacheds.configuration.model.v152.ExtendedOperationEnum;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerConfigurationV152;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
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
 * This class represents the Extended Operations Master/Details Block used in the Extended Operations Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExtendedOperationsMasterDetailsBlock extends MasterDetailsBlock
{
    /** The associated page */
    private FormPage page;

    /** The input Server Configuration */
    private ServerConfigurationV152 serverConfiguration;

    /** The Extended Operations List */
    private List<ExtendedOperationEnum> extendedOperations;

    /** The Details Page */
    private ExtendedOperationDetailsPage detailsPage;

    // UI Fields
    private TableViewer viewer;
    private Button addButton;
    private Button deleteButton;


    /**
     * Creates a new instance of ExtendedOperationsMasterDetailsBlock.
     *
     * @param page
     */
    public ExtendedOperationsMasterDetailsBlock( FormPage page )
    {
        this.page = page;
        serverConfiguration = ( ServerConfigurationV152 ) ( ( ServerConfigurationEditor ) page.getEditor() )
            .getServerConfiguration();
        extendedOperations = serverConfiguration.getExtendedOperations();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "All Extended Operations" );
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
                return ApacheDSConfigurationPlugin.getDefault().getImage(
                    ApacheDSConfigurationPluginConstants.IMG_EXTENDED_OPERATION );
            }
        } );

        // Creating the button(s)
        addButton = toolkit.createButton( client, "Add...", SWT.PUSH ); //$NON-NLS-1$
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
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
        viewer.setInput( extendedOperations );
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
            }
        } );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ExtendedOperationDialog dialog = new ExtendedOperationDialog( extendedOperations );
                if ( Dialog.OK == dialog.open() )
                {
                    ExtendedOperationEnum newExtendedOperation = dialog.getExtendedOperation();
                    extendedOperations.add( newExtendedOperation );
                    viewer.refresh();
                    viewer.setSelection( new StructuredSelection( newExtendedOperation ) );
                    setEditorDirty();
                }
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
                if ( !selection.isEmpty() )
                {
                    ExtendedOperationEnum extendedOperation = ( ExtendedOperationEnum ) selection.getFirstElement();

                    extendedOperations.remove( extendedOperation );
                    viewer.refresh();
                    setEditorDirty();
                }
            }
        } );
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
        detailsPage = new ExtendedOperationDetailsPage( this );
        detailsPart.registerPage( ExtendedOperationEnum.class, detailsPage );
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
        viewer.setInput( extendedOperations );
    }
}
