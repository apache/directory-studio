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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.server.config.beans.IndexBean;
import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.server.config.beans.PartitionBean;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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


/**
 * This class represents the Partitions Master/Details Block used in the Partitions Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsMasterDetailsBlock extends MasterDetailsBlock
{
    private static final String NEW_ID = "partition";

    /** The associated page */
    private PartitionsPage page;

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
    public PartitionsMasterDetailsBlock( PartitionsPage page )
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
        viewer.setLabelProvider( PartitionsPage.PARTITIONS_LABEL_PROVIDER );

        // Creating the button(s)
        addButton = toolkit.createButton( client, "Add", SWT.PUSH );
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
        viewer.setInput( page.getConfigBean().getDirectoryServiceBean().getPartitions() );
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
                    JdbmPartitionBean partition = ( JdbmPartitionBean ) selection.getFirstElement();
                    if ( PartitionsPage.isSystemPartition( partition ) )
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
                String newId = getNewId();

                JdbmPartitionBean newPartitionBean = new JdbmPartitionBean();
                newPartitionBean.setPartitionId( newId );
                try
                {
                    newPartitionBean.setPartitionSuffix( new Dn( "dc=" + newId + ",dc=com" ) );
                }
                catch ( LdapInvalidDnException e1 )
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // Default values
                newPartitionBean.setPartitionCacheSize( 100 );
                newPartitionBean.setJdbmPartitionOptimizerEnabled( true );
                newPartitionBean.setPartitionSyncOnWrite( true );
                List<IndexBean> indexes = new ArrayList<IndexBean>();
                indexes.add( createJdbmIndex( "apacheAlias", 100 ) );
                indexes.add( createJdbmIndex( "apacheOneAlias", 100 ) );
                indexes.add( createJdbmIndex( "apacheOneLevel", 100 ) );
                indexes.add( createJdbmIndex( "apachePresence", 100 ) );
                indexes.add( createJdbmIndex( "apacheRdn", 100 ) );
                indexes.add( createJdbmIndex( "apacheSubAlias", 100 ) );
                indexes.add( createJdbmIndex( "apacheSubLevel", 100 ) );
                indexes.add( createJdbmIndex( "dc", 100 ) );
                indexes.add( createJdbmIndex( "entryCSN", 100 ) );
                indexes.add( createJdbmIndex( "entryUUID", 100 ) );
                indexes.add( createJdbmIndex( "krbPrincipalName", 100 ) );
                indexes.add( createJdbmIndex( "objectClass", 100 ) );
                indexes.add( createJdbmIndex( "ou", 100 ) );
                indexes.add( createJdbmIndex( "uid", 100 ) );
                newPartitionBean.setIndexes( indexes );

                page.getConfigBean().getDirectoryServiceBean().addPartitions( newPartitionBean );
                viewer.refresh();
                viewer.setSelection( new StructuredSelection( newPartitionBean ) );
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
                    JdbmPartitionBean partition = ( JdbmPartitionBean ) selection.getFirstElement();
                    if ( !PartitionsPage.isSystemPartition( partition ) )
                    {
                        page.getConfigBean().getDirectoryServiceBean().getPartitions().remove( partition );
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

            for ( PartitionBean partition : page.getConfigBean().getDirectoryServiceBean().getPartitions() )
            {
                if ( partition.getPartitionId().equalsIgnoreCase( name ) )
                {
                    ok = false;
                }
            }
            counter++;
        }

        return name;
    }


    /**
     * Create a JDBM Index with the given index attribute id and cache size.
     *
     * @param indexAttributeId the attribute id
     * @param indexCacheSize the cache size
     */
    private JdbmIndexBean<String, Entry> createJdbmIndex( String indexAttributeId, int indexCacheSize )
    {
        JdbmIndexBean<String, Entry> index = new JdbmIndexBean<String, Entry>();

        index.setIndexAttributeId( indexAttributeId );
        index.setIndexCacheSize( indexCacheSize );

        return index;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new PartitionDetailsPage( this );
        detailsPart.registerPage( JdbmPartitionBean.class, detailsPage );
    }


    @Override
    protected void createToolBarActions( IManagedForm managedForm )
    {
        // TODO Auto-generated method stub

    }


    /**
     * Sets the Editor as dirty.
     */
    public void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) page.getEditor() ).setDirty( true );
        viewer.refresh();
    }


    /**
     * Saves the necessary elements to the input model.
     */
    public void save()
    {
        detailsPage.commit( true );
    }
}
