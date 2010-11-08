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
package org.apache.directory.studio.apacheds.configuration.v2.editor;


import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
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
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Partitions Master/Details Block used in the Partitions Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsMasterDetailsBlock extends MasterDetailsBlock
{
    /** The associated page */
    private FormPage page;

    /** The Details Page */
    private PartitionDetailsPage detailsPage;

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
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "All Partitions" );
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
                return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                    ApacheDS2ConfigurationPluginConstants.IMG_PARTITION );
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
        viewer.setInput( new String[]
            { "ou=system", "dc=example,dc=com" } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new PartitionDetailsPage( this );
        detailsPart.registerPage( String.class, detailsPage );
    }


    @Override
    protected void createToolBarActions( IManagedForm managedForm )
    {
        // TODO Auto-generated method stub

    }

}
