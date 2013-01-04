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


import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.server.config.beans.ReplConsumerBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Replication Master/Details Block used in the Replication Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationMasterDetailsBlock extends MasterDetailsBlock
{
    private static final String NEW_ID = "consumer";

    /** The associated page */
    private ReplicationPage page;

    /** The Details Page */
    private ReplicationDetailsPage detailsPage;

    // UI Fields
    private TableViewer viewer;
    private Button addButton;
    private Button deleteButton;


    /**
     * Creates a new instance of ReplicationMasterDetailsBlock.
     *
     * @param page
     *      the associated page
     */
    public ReplicationMasterDetailsBlock( ReplicationPage page )
    {
        this.page = page;
    }


    /**
     * {@inheritDoc}
     */
    public void createContent( IManagedForm managedForm )
    {
        super.createContent( managedForm );

        this.sashForm.setWeights( new int[]
            { 30, 70 } );
    }


    /**
     * {@inheritDoc}
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        sashForm.setOrientation( SWT.HORIZONTAL );

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( "All Replication Consummers" );
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
                if ( element instanceof ReplConsumerBean )
                {
                    ReplConsumerBean consumer = ( ReplConsumerBean ) element;

                    return consumer.getReplConsumerId();
                }

                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof ReplConsumerBean )
                {
                    return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                        ApacheDS2ConfigurationPluginConstants.IMG_REPLICATION_CONSUMER );
                }

                return super.getImage( element );
            }
        } );
        viewer.setComparator( new ViewerComparator()
        {
            public int compare( Viewer viewer, Object e1, Object e2 )
            {
                if ( ( e1 instanceof ReplConsumerBean ) && ( e2 instanceof ReplConsumerBean ) )
                {
                    ReplConsumerBean o1 = ( ReplConsumerBean ) e1;
                    ReplConsumerBean o2 = ( ReplConsumerBean ) e2;

                    String id1 = o1.getReplConsumerId();
                    String id2 = o2.getReplConsumerId();

                    if ( ( id1 != null ) && ( id2 != null ) )
                    {
                        return id1.compareTo( id2 );
                    }
                }

                return super.compare( viewer, e1, e2 );
            }
        } );

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
        viewer.setInput( page.getConfigBean().getDirectoryServiceBean().getLdapServerBean().getReplConsumers() );
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
            }
        } );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addNewConsumer();
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                deleteSelectedConsumer();
            }
        } );
    }


    /**
     * This method is called when the 'Add' button is clicked.
     */
    private void addNewConsumer()
    {
        String newId = getNewId();

        ReplConsumerBean consumerBean = getNewReplConsumerBean();

        consumerBean.setReplConsumerId( newId );

        page.getConfigBean().getDirectoryServiceBean().getLdapServerBean().addReplConsumers( consumerBean );
        viewer.refresh();
        viewer.setSelection( new StructuredSelection( consumerBean ) );
        setEditorDirty();
    }


    /**
     * Gets a new ReplConsumerBean.
     *
     * @return a new ReplConsumerBean
     */
    private ReplConsumerBean getNewReplConsumerBean()
    {
        ReplConsumerBean consumerBean = new ReplConsumerBean();

        consumerBean.setEnabled( true );
        consumerBean.setReplAliasDerefMode( AliasDerefMode.NEVER_DEREF_ALIASES.getJndiValue() );
        consumerBean.setReplProvHostName( "localhost" );
        consumerBean.setReplProvPort( 10389 );
        consumerBean.setReplSearchFilter( "(objectClass=*)" );
        consumerBean.setReplSearchScope( SearchScope.SUBTREE.getLdapUrlValue() );
        consumerBean.setReplUserDn( "uid=admin,ou=system" );
        consumerBean.setReplUserPassword( "secret".getBytes() );
        consumerBean.setReplRefreshInterval( 60 * 1000 );
        consumerBean.setReplRefreshNPersist( true );
        consumerBean.addReplAttributes( SchemaConstants.ALL_USER_ATTRIBUTES );
        consumerBean.setSearchBaseDn( "dc=example,dc=com" );

        return consumerBean;
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

            for ( ReplConsumerBean consumer : page.getConfigBean().getDirectoryServiceBean().getLdapServerBean()
                .getReplConsumers() )
            {
                if ( consumer.getReplConsumerId().equalsIgnoreCase( name ) )
                {
                    ok = false;
                }
            }
            counter++;
        }

        return name;
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteSelectedConsumer()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        if ( !selection.isEmpty() )
        {
            ReplConsumerBean consumer = ( ReplConsumerBean ) selection.getFirstElement();

            if ( MessageDialog.openConfirm( page.getManagedForm().getForm().getShell(), "Confirm Delete",
                NLS.bind( "Are you sure you want to delete replication consumer ''{0}''?",
                    consumer.getReplConsumerId() ) ) )
            {
                page.getConfigBean().getDirectoryServiceBean().getLdapServerBean().getReplConsumers().remove( consumer );
                setEditorDirty();
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new ReplicationDetailsPage( this );
        detailsPart.registerPage( ReplConsumerBean.class, detailsPage );
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


    /**
     * Gets the replication page.
     *
     * @return the replication
     */
    public ReplicationPage getPage()
    {
        return this.page;
    }
}
