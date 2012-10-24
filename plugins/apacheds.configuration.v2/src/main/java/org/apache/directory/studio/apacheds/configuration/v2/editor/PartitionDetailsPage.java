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


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.server.config.beans.IndexBean;
import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.shared.ldap.model.entry.Attribute;
import org.apache.directory.shared.ldap.model.entry.DefaultAttribute;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.ldif.LdifEntry;
import org.apache.directory.shared.ldap.model.ldif.LdifReader;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.dialogs.AttributeValueDialog;
import org.apache.directory.studio.apacheds.configuration.v2.dialogs.IndexDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Partition type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Partition */
    private JdbmPartitionBean input;

    /** The Context Entry */
    private Entry contextEntry;

    /** The Indexes List */
    private List<IndexBean> indexesList;

    // UI fields
    private Text idText;
    private Text cacheSizeText;
    private Text suffixText;
    private Button enableOptimizerCheckbox;
    private Button synchOnWriteCheckbox;
    private Button autoGenerateContextEntryCheckbox;
    private TableViewer contextEntryTableViewer;
    private Button contextEntryAddButton;
    private Button contextEntryEditButton;
    private Button contextEntryDeleteButton;
    private TableViewer indexesTableViewer;
    private Button indexesAddButton;
    private Button indexesEditButton;
    private Button indexesDeleteButton;

    // Listeners
    /** The Text Modify Listener */
    private ModifyListener textModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    private ModifyListener suffixTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            autoGenerateContextEntry();
        }
    };

    /** The Checkbox Selection Listener */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    private SelectionListener autoGenerateContextEntryCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            autoGenerateContextEntry();
            updateContextEntryEnableState();
        }
    };

    /** The Selection Changed Listener for the Context Entry Table Viewer */
    private ISelectionChangedListener contextEntryTableViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            updateContextEntryEnableState();
        }
    };

    /** The Double Click Listener for the Indexed Attributes Table Viewer */
    private IDoubleClickListener contextEntryTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editSelectedContextEntry();
        }
    };

    /** The Listener for the Add button of the Context Entry Section */
    private SelectionListener contextEntryAddButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeValueDialog dialog = new AttributeValueDialog( new AttributeValueObject( "", "" ) );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                AttributeValueObject newAttributeValueObject = dialog.getAttributeValueObject();
                Attribute attribute = contextEntry.get( newAttributeValueObject.getAttribute() );

                if ( attribute != null )
                {
                    try
                    {
                        attribute.add( newAttributeValueObject.getValue() );
                    }
                    catch ( LdapInvalidAttributeValueException liave )
                    {
                        // TODO : handle the exception
                    }
                }
                else
                {
                    try
                    {
                        contextEntry.put( new DefaultAttribute( newAttributeValueObject.getAttribute(),
                            newAttributeValueObject.getValue() ) );
                    }
                    catch ( LdapException e1 )
                    {
                        // Will never occur
                    }
                }

                contextEntryTableViewer.refresh();
                resizeContextEntryTableColumnsToFit();
                masterDetailsBlock.setEditorDirty();
                //                dirty = true; TODO
                commit( true );
            }
        }
    };

    /** The Listener for the Edit button of the Context Entry Section */
    private SelectionListener contextEntryEditButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editSelectedContextEntry();
        }
    };

    /** The Listener for the Delete button of the Context Entry Section */
    private SelectionListener contextEntryDeleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) contextEntryTableViewer.getSelection();
            if ( !selection.isEmpty() )
            {
                AttributeValueObject attributeValueObject = ( AttributeValueObject ) selection.getFirstElement();

                Attribute attribute = contextEntry.get( attributeValueObject.getAttribute() );
                if ( attribute != null )
                {
                    attribute.remove( attributeValueObject.getValue() );
                    contextEntryTableViewer.refresh();
                    resizeContextEntryTableColumnsToFit();
                    masterDetailsBlock.setEditorDirty();
                    //                    dirty = true; TODO
                    commit( true );
                }
            }
        }
    };

    /** The Selection Changed Listener for the Indexed Attributes Table Viewer */
    private ISelectionChangedListener indexedAttributesTableViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            indexesEditButton.setEnabled( !event.getSelection().isEmpty() );
            indexesDeleteButton.setEnabled( !event.getSelection().isEmpty() );
        }
    };

    /** The Double Click Listener for the Indexed Attributes Table Viewer */
    private IDoubleClickListener indexedAttributesTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editSelectedIndex();
        }
    };

    /** The Listener for the Add button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeAddButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            addNewIndex();
        }
    };

    /** The Listener for the Edit button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeEditButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editSelectedIndex();
        }
    };

    /** The Listener for the Delete button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeDeleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            deleteSelectedIndex();
        }
    };


    /**
     * Creates a new instance of PartitionDetailsPage.
     *
     * @param pmdb
     *      the associated Master Details Block
     */
    public PartitionDetailsPage( PartitionsMasterDetailsBlock pmdb )
    {
        masterDetailsBlock = pmdb;
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( Composite parent )
    {
        FormToolkit toolkit = mform.getToolkit();
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        createDetailsSection( parent, toolkit );
        createContextEntrySection( parent, toolkit );
        createIndexesSection( parent, toolkit );
    }


    /**
     * Creates the Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createDetailsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Partition Details" );
        section.setDescription( "Set the properties of the partition." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 3, false );
        client.setLayout( glayout );
        section.setClient( client );

        // ID
        toolkit.createLabel( client, "ID:" );
        idText = toolkit.createText( client, "" ); //$NON-NLS-1$
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Cache Size
        toolkit.createLabel( client, "Cache Size:" );
        cacheSizeText = toolkit.createText( client, "" ); //$NON-NLS-1$
        cacheSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Suffix
        toolkit.createLabel( client, "Suffix:" ); //$NON-NLS-1$
        suffixText = toolkit.createText( client, "" ); //$NON-NLS-1$
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Optimizer
        enableOptimizerCheckbox = toolkit.createButton( client, "Enable Optimizer", SWT.CHECK );
        enableOptimizerCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Synchronisation On Write
        synchOnWriteCheckbox = toolkit.createButton( client, "Synchronization On Write", SWT.CHECK );
        synchOnWriteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
    }


    /**
     * Creates the Context Entry Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createContextEntrySection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Context Entry" ); //$NON-NLS-1$
        section.setDescription( "Set the attribute/value pairs for the Context Entry of the partition." ); //$NON-NLS-1$
        section.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        client.setLayout( new GridLayout( 2, false ) );
        section.setClient( client );

        // Auto Generate Context Entry Checkbox
        autoGenerateContextEntryCheckbox = toolkit.createButton( client, "Auto-generate context entry from suffix DN",
            SWT.CHECK );
        autoGenerateContextEntryCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Context Entry Table Viewer
        Table contextEntryTable = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 62;
        gd.widthHint = 50;
        contextEntryTable.setLayoutData( gd );
        TableColumn idColumn = new TableColumn( contextEntryTable, SWT.LEFT, 0 );
        idColumn.setText( "Attribute" );
        idColumn.setWidth( 100 );
        TableColumn valueColumn = new TableColumn( contextEntryTable, SWT.LEFT, 1 );
        valueColumn.setText( "Value" );
        valueColumn.setWidth( 100 );
        contextEntryTable.setHeaderVisible( true );
        contextEntryTableViewer = new TableViewer( contextEntryTable );
        contextEntryTableViewer.setContentProvider( new IStructuredContentProvider()
        {
            public Object[] getElements( Object inputElement )
            {
                List<AttributeValueObject> elements = new ArrayList<AttributeValueObject>();
                Entry entry = ( Entry ) inputElement;

                Iterator<Attribute> attributes = entry.iterator();
                while ( attributes.hasNext() )
                {
                    Attribute attribute = attributes.next();

                    Iterator<Value<?>> values = attribute.iterator();
                    while ( values.hasNext() )
                    {
                        Value<?> value = values.next();
                        elements.add( new AttributeValueObject( attribute.getId(), value.getString() ) );
                    }
                }

                return elements.toArray();
            }


            public void dispose()
            {
            }


            public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
            {
            }
        } );
        contextEntryTableViewer.setLabelProvider( new ITableLabelProvider()
        {
            public String getColumnText( Object element, int columnIndex )
            {
                if ( element != null )
                {
                    switch ( columnIndex )
                    {
                        case 0:
                            return ( ( AttributeValueObject ) element ).getAttribute();
                        case 1:
                            return ( ( AttributeValueObject ) element ).getValue().toString();
                        default:
                            break;
                    }
                }

                return null;
            }


            public Image getColumnImage( Object element, int columnIndex )
            {
                return null;
            }


            public void addListener( ILabelProviderListener listener )
            {
            }


            public void dispose()
            {
            }


            public boolean isLabelProperty( Object element, String property )
            {
                return false;
            }


            public void removeListener( ILabelProviderListener listener )
            {
            }
        } );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        // Context Entry Add Button
        contextEntryAddButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        contextEntryAddButton.setLayoutData( buttonsGD );

        // Context Entry Edit Button
        contextEntryEditButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        contextEntryEditButton.setEnabled( false );
        contextEntryEditButton.setLayoutData( buttonsGD );

        // Context Entry Delete Button
        contextEntryDeleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        contextEntryDeleteButton.setEnabled( false );
        contextEntryDeleteButton.setLayoutData( buttonsGD );
    }


    /**
     * Updates the context entry widgets enable state.
     */
    private void updateContextEntryEnableState()
    {
        contextEntryTableViewer.getTable().setEnabled( !autoGenerateContextEntryCheckbox.getSelection() );
        contextEntryAddButton.setEnabled( !autoGenerateContextEntryCheckbox.getSelection() );
        contextEntryEditButton.setEnabled( ( !autoGenerateContextEntryCheckbox.getSelection() )
            && ( !contextEntryTableViewer.getSelection().isEmpty() ) );
        contextEntryDeleteButton.setEnabled( ( !autoGenerateContextEntryCheckbox.getSelection() )
            && ( !contextEntryTableViewer.getSelection().isEmpty() ) );
    }


    /**
     * Creates the Indexes Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createIndexesSection( Composite parent, FormToolkit toolkit )
    {
        // Section
        Section indexedAttributesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        indexedAttributesSection.marginWidth = 10;
        indexedAttributesSection.setText( "Indexed Attributes" );
        indexedAttributesSection.setDescription( "Set the indexed attributes of the partition." );
        indexedAttributesSection.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        Composite indexedAttributesClient = toolkit.createComposite( indexedAttributesSection );
        toolkit.paintBordersFor( indexedAttributesClient );
        indexedAttributesClient.setLayout( new GridLayout( 2, false ) );
        indexedAttributesSection.setClient( indexedAttributesClient );

        // TableViewer
        Table indexedAttributesTable = toolkit.createTable( indexedAttributesClient, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 80;
        indexedAttributesTable.setLayoutData( gd );
        indexesTableViewer = new TableViewer( indexedAttributesTable );
        indexesTableViewer.setContentProvider( new ArrayContentProvider() );
        indexesTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof JdbmIndexBean )
                {
                    JdbmIndexBean<String, Entry> jdbmIndexBean = ( JdbmIndexBean<String, Entry> ) element;

                    return NLS.bind( "{0} [{1}]", jdbmIndexBean.getIndexAttributeId(),
                        jdbmIndexBean.getIndexCacheSize() );
                }

                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof JdbmIndexBean )
                {
                    return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                        ApacheDS2ConfigurationPluginConstants.IMG_INDEX );
                }

                return super.getImage( element );
            };
        } );

        // Add button
        indexesAddButton = toolkit.createButton( indexedAttributesClient, "Add", SWT.PUSH );
        indexesAddButton.setLayoutData( createNewButtonGridData() );

        // Edit button
        indexesEditButton = toolkit.createButton( indexedAttributesClient, "Edit", SWT.PUSH );
        indexesEditButton.setEnabled( false );
        indexesEditButton.setLayoutData( createNewButtonGridData() );

        // Delete button
        indexesDeleteButton = toolkit.createButton( indexedAttributesClient, "Delete", SWT.PUSH );
        indexesDeleteButton.setEnabled( false );
        indexesDeleteButton.setLayoutData( createNewButtonGridData() );
    }


    /**
     * Create a new button grid data.
     *
     * @return the new button grid data
     */
    private GridData createNewButtonGridData()
    {
        GridData gd = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        gd.widthHint = IDialogConstants.BUTTON_WIDTH;
        return gd;
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        idText.addModifyListener( textModifyListener );
        cacheSizeText.addModifyListener( textModifyListener );
        suffixText.addModifyListener( textModifyListener );
        suffixText.addModifyListener( suffixTextModifyListener );
        enableOptimizerCheckbox.addSelectionListener( checkboxSelectionListener );
        synchOnWriteCheckbox.addSelectionListener( checkboxSelectionListener );

        autoGenerateContextEntryCheckbox.addSelectionListener( autoGenerateContextEntryCheckboxSelectionListener );
        contextEntryTableViewer.addDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.addSelectionChangedListener( contextEntryTableViewerListener );
        contextEntryAddButton.addSelectionListener( contextEntryAddButtonListener );
        contextEntryEditButton.addSelectionListener( contextEntryEditButtonListener );
        contextEntryDeleteButton.addSelectionListener( contextEntryDeleteButtonListener );

        indexesTableViewer.addSelectionChangedListener( indexedAttributesTableViewerListener );
        indexesTableViewer.addDoubleClickListener( indexedAttributesTableViewerDoubleClickListener );
        indexesAddButton.addSelectionListener( indexedAttributeAddButtonListener );
        indexesEditButton.addSelectionListener( indexedAttributeEditButtonListener );
        indexesDeleteButton.addSelectionListener( indexedAttributeDeleteButtonListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        idText.removeModifyListener( textModifyListener );
        cacheSizeText.removeModifyListener( textModifyListener );
        suffixText.removeModifyListener( textModifyListener );
        suffixText.removeModifyListener( suffixTextModifyListener );
        enableOptimizerCheckbox.removeSelectionListener( checkboxSelectionListener );
        synchOnWriteCheckbox.removeSelectionListener( checkboxSelectionListener );

        autoGenerateContextEntryCheckbox.removeSelectionListener( autoGenerateContextEntryCheckboxSelectionListener );
        contextEntryTableViewer.removeDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.removeSelectionChangedListener( contextEntryTableViewerListener );
        contextEntryAddButton.removeSelectionListener( contextEntryAddButtonListener );
        contextEntryEditButton.removeSelectionListener( contextEntryEditButtonListener );
        contextEntryDeleteButton.removeSelectionListener( contextEntryDeleteButtonListener );

        indexesTableViewer.removeSelectionChangedListener( indexedAttributesTableViewerListener );
        indexesTableViewer.removeDoubleClickListener( indexedAttributesTableViewerDoubleClickListener );
        indexesAddButton.removeSelectionListener( indexedAttributeAddButtonListener );
        indexesEditButton.removeSelectionListener( indexedAttributeEditButtonListener );
        indexesDeleteButton.removeSelectionListener( indexedAttributeDeleteButtonListener );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( JdbmPartitionBean ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( input != null )
        {
            input.setPartitionId( idText.getText() );
            input.setPartitionCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
            try
            {
                input.setPartitionSuffix( new Dn( suffixText.getText() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                // Stay silent
            }
            input.setJdbmPartitionOptimizerEnabled( enableOptimizerCheckbox.getSelection() );
            input.setPartitionSyncOnWrite( synchOnWriteCheckbox.getSelection() );

            if ( contextEntry.size() > 0 )
            {
                LdifEntry ldifEntry = new LdifEntry( contextEntry );
                ldifEntry.setDn( input.getPartitionSuffix() );
                input.setContextEntry( ldifEntry.toString() );
            }
            else
            {
                input.setContextEntry( null );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        // Checking if the selected partition is the system partition
        boolean isPartition = PartitionsPage.isSystemPartition( input );

        // ID
        String id = input.getPartitionId();
        idText.setText( ( id == null ) ? "" : id ); //$NON-NLS-1$
        idText.setEnabled( !isPartition );

        // Cache Size
        cacheSizeText.setText( "" + input.getPartitionCacheSize() ); //$NON-NLS-1$

        // Suffix
        Dn suffix = input.getPartitionSuffix();
        suffixText.setText( ( suffix == null ) ? "" : suffix.toString() ); //$NON-NLS-1$
        suffixText.setEnabled( !isPartition );

        // Enable Optimizer
        enableOptimizerCheckbox.setSelection( input.isJdbmPartitionOptimizerEnabled() );

        // Synchronization on write
        synchOnWriteCheckbox.setSelection( input.isPartitionSyncOnWrite() );

        // Auto Generate Context Entry
        autoGenerateContextEntryCheckbox.setSelection( true );

        // Context Entry
        refreshContextEntry();

        // Indexed Attributes
        indexesList = input.getIndexes();
        indexesTableViewer.setInput( indexesList );

        addListeners();
    }


    private void refreshContextEntry()
    {
        String contextEntryString = input.getContextEntry();

        if ( ( contextEntryString != null ) && ( !"".equals( contextEntryString ) ) )
        {
            try
            {
                // Replace '\n' to real LF
                contextEntryString = contextEntryString.replaceAll( "\\\\n", "\n" );

                LdifReader reader = new LdifReader( new StringReader( contextEntryString ) );
                contextEntry = reader.next().getEntry();
                reader.close();
            }
            catch ( Exception e )
            {
                contextEntry = new DefaultEntry();
            }
        }
        else
        {
            contextEntry = new DefaultEntry();
        }

        contextEntryTableViewer.setInput( contextEntry );
        resizeContextEntryTableColumnsToFit();

        boolean enabled = !autoGenerateContextEntryCheckbox.getSelection();
        contextEntryTableViewer.getTable().setEnabled( enabled );
        contextEntryAddButton.setEnabled( enabled );
        contextEntryEditButton.setEnabled( enabled );
        contextEntryDeleteButton.setEnabled( enabled );
    }



    /**
     * Auto generates the context entry.
     */
    private void autoGenerateContextEntry()
    {
        if ( autoGenerateContextEntryCheckbox.getSelection() )
        {
            try
            {
                Dn suffixDn = new Dn( suffixText.getText() );
                input.setContextEntry( PartitionsMasterDetailsBlock.getContextEntryLdif( suffixDn ) );
                refreshContextEntry();
            }
            catch ( LdapInvalidDnException e1 )
            {
                // Silent
            }
        }
    }

    /**
     * Resizes the columns to fit the size of the cells.
     */
    private void resizeContextEntryTableColumnsToFit()
    {
        // Resizing the first column
        contextEntryTableViewer.getTable().getColumn( 0 ).pack();
        // Adding a little space to the first column
        contextEntryTableViewer.getTable().getColumn( 0 )
            .setWidth( contextEntryTableViewer.getTable().getColumn( 0 ).getWidth() + 5 );
        // Resizing the second column
        contextEntryTableViewer.getTable().getColumn( 1 ).pack();
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        idText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }


    /**
     * Opens an indexed dialog with the selected index in the indexes table viewer.
     */
    private void editSelectedIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) indexesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            JdbmIndexBean<String, Entry> index = ( JdbmIndexBean<String, Entry> ) selection.getFirstElement();

            IndexDialog dialog = new IndexDialog( index );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                indexesTableViewer.refresh();
                masterDetailsBlock.setEditorDirty();
            }
        }
    }


    /**
     * Adds a new index and opens the index dialog.
     */
    private void addNewIndex()
    {
        JdbmIndexBean<String, Entry> newIndex = new JdbmIndexBean<String, Entry>();
        newIndex.setIndexAttributeId( "" ); //$NON-NLS-1$
        newIndex.setIndexCacheSize( 100 );

        IndexDialog dialog = new IndexDialog( newIndex );
        if ( Dialog.OK == dialog.open() )
        {
            indexesList.add( dialog.getIndex() );
            indexesTableViewer.refresh();
            indexesTableViewer.setSelection( new StructuredSelection( dialog.getIndex() ) );
            masterDetailsBlock.setEditorDirty();
        }
    }


    /**
     * Deletes the selected index in the indexes table viewer
     */
    private void deleteSelectedIndex()
    {
        StructuredSelection selection = ( StructuredSelection ) indexesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            JdbmIndexBean<String, Entry> selectedIndex = ( JdbmIndexBean<String, Entry> ) selection.getFirstElement();

            if ( MessageDialog.openConfirm( mform.getForm().getShell(), "Confirm Delete",
                NLS.bind( "Are you sure you want to delete index ''{0}''?", selectedIndex.getIndexAttributeId() ) ) )
            {
                indexesList.remove( selectedIndex );
                indexesTableViewer.refresh();
                masterDetailsBlock.setEditorDirty();
            }
        }
    }


    /**
     * Opens a Context Entry Dialog with the selected Attribute Value Object in the
     * Context Entry Table Viewer.
     */
    private void editSelectedContextEntry()
    {
        StructuredSelection selection = ( StructuredSelection ) contextEntryTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            AttributeValueObject attributeValueObject = ( AttributeValueObject ) selection.getFirstElement();

            String oldId = attributeValueObject.getAttribute();
            String oldValue = attributeValueObject.getValue();

            AttributeValueDialog dialog = new AttributeValueDialog( attributeValueObject );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                Attribute attribute = contextEntry.get( oldId );
                if ( attribute != null )
                {
                    attribute.remove( oldValue );
                }

                AttributeValueObject newAttributeValueObject = dialog.getAttributeValueObject();
                attribute = contextEntry.get( newAttributeValueObject.getAttribute() );

                if ( attribute != null )
                {
                    try
                    {
                        attribute.add( newAttributeValueObject.getValue() );
                    }
                    catch ( LdapInvalidAttributeValueException liave )
                    {
                        // TODO : handle the exception
                    }
                }
                else
                {
                    try
                    {
                        contextEntry.put( new DefaultAttribute( newAttributeValueObject.getAttribute(),
                            newAttributeValueObject.getValue() ) );
                    }
                    catch ( LdapException e )
                    {
                        // Will never occur
                    }
                }

                contextEntryTableViewer.refresh();
                resizeContextEntryTableColumnsToFit();
                masterDetailsBlock.setEditorDirty();
                //                dirty = true; TODO
                commit( true );
            }
        }
    }
}
