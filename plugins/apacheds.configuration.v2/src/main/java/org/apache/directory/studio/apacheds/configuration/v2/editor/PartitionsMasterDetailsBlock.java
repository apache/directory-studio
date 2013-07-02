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

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.server.config.beans.IndexBean;
import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.server.config.beans.PartitionBean;
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
    private static final String NEW_ID = Messages.getString( "PartitionsMasterDetailsBlock.PartitionNewId" ); //$NON-NLS-1$

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


    /**
     * {@inheritDoc}
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
        viewer.setLabelProvider( PartitionsPage.PARTITIONS_LABEL_PROVIDER );
        viewer.setComparator( PartitionsPage.PARTITIONS_COMPARATOR );

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
        viewer.setInput( page.getConfigBean().getDirectoryServiceBean().getPartitions() );
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
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                viewer.refresh();

                // Getting the selection of the table viewer
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

                // Delete button is enabled when something is selected
                deleteButton.setEnabled( !selection.isEmpty() );

                // Delete button is not enabled in the case of the system partition
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
                addNewPartition();
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                deleteSelectedPartition();
            }
        } );
    }


    /**
     * This method is called when the 'Add' button is clicked.
     */
    private void addNewPartition()
    {
        String newId = getNewId();

        JdbmPartitionBean newPartitionBean = new JdbmPartitionBean();
        newPartitionBean.setPartitionId( newId );
        try
        {
            newPartitionBean.setPartitionSuffix( new Dn( "dc=" + newId + ",dc=com" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch ( LdapInvalidDnException e1 )
        {
            // Will never happen
        }

        // Default values
        newPartitionBean.setPartitionCacheSize( 100 );
        newPartitionBean.setJdbmPartitionOptimizerEnabled( true );
        newPartitionBean.setPartitionSyncOnWrite( true );
        newPartitionBean.setContextEntry( getContextEntryLdif( newPartitionBean.getPartitionSuffix() ) );
        List<IndexBean> indexes = new ArrayList<IndexBean>();
        indexes.add( createJdbmIndex( "apacheAlias", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apacheOneAlias", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apacheOneLevel", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apachePresence", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apacheRdn", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apacheSubAlias", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "apacheSubLevel", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "dc", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "entryCSN", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "entryUUID", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "krb5PrincipalName", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "objectClass", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "ou", 100 ) ); //$NON-NLS-1$
        indexes.add( createJdbmIndex( "uid", 100 ) ); //$NON-NLS-1$
        newPartitionBean.setIndexes( indexes );

        page.getConfigBean().getDirectoryServiceBean().addPartitions( newPartitionBean );
        viewer.refresh();
        viewer.setSelection( new StructuredSelection( newPartitionBean ) );
        setEditorDirty();
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
     * Creates the context entry for the partition.
     * 
     * @param dn the dn
     * @return the LDIF representation of the context entry
     */
    public static String getContextEntryLdif( Dn dn )
    {
        try
        {
            Entry entry = new DefaultEntry( dn );
            entry.add( SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.DOMAIN_OC );
            entry.add( SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.TOP_OC );

            // Getting the RDN from the DN
            Rdn rdn = dn.getRdn();

            if ( !SchemaConstants.DC_AT.equalsIgnoreCase( rdn.getType() ) )
            {
                entry.add( SchemaConstants.OBJECT_CLASS_AT, SchemaConstants.EXTENSIBLE_OBJECT_OC );
                entry.add( SchemaConstants.DC_AT, rdn.getValue() );
            }

            entry.add( rdn.getType(), rdn.getValue() );

            LdifEntry ldifEntry = new LdifEntry( entry );

            return ldifEntry.toString();
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteSelectedPartition()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        if ( !selection.isEmpty() )
        {
            JdbmPartitionBean partition = ( JdbmPartitionBean ) selection.getFirstElement();
            if ( !PartitionsPage.isSystemPartition( partition ) )
            {
                if ( MessageDialog
                    .openConfirm(
                        page.getManagedForm().getForm().getShell(),
                        Messages.getString( "PartitionsMasterDetailsBlock.ConfirmDelete" ), //$NON-NLS-1$
                        NLS.bind(
                            Messages.getString( "PartitionsMasterDetailsBlock.AreYouSureDeletePartition" ), partition.getPartitionId(), //$NON-NLS-1$
                            partition.getPartitionSuffix() ) ) )
                {
                    page.getConfigBean().getDirectoryServiceBean().getPartitions().remove( partition );
                    setEditorDirty();
                }
            }
        }
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


    /**
     * {@inheritDoc}
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new PartitionDetailsPage( this );
        detailsPart.registerPage( JdbmPartitionBean.class, detailsPage );
    }


    /**
     * {@inheritDoc}
     */
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
