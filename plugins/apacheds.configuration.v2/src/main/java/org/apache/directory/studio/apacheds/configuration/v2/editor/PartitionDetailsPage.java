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

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.config.beans.IndexBean;
import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.server.config.beans.MavibotIndexBean;
import org.apache.directory.server.config.beans.MavibotPartitionBean;
import org.apache.directory.server.config.beans.PartitionBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.dialogs.AttributeValueDialog;
import org.apache.directory.studio.apacheds.configuration.v2.dialogs.JdbmIndexDialog;
import org.apache.directory.studio.apacheds.configuration.v2.dialogs.MavibotIndexDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Partition type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionDetailsPage implements IDetailsPage
{
    /** The class instance */
    private PartitionDetailsPage instance;

    /** The associated Master Details Block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;

    /** The partition wrapper */
    private PartitionWrapper partitionWrapper;

    /** The partition specific details block */
    private PartitionSpecificDetailsBlock partitionSpecificDetailsBlock;

    /** The Context Entry */
    private Entry contextEntry;

    /** The Indexes List */
    private List<IndexBean> indexesList;

    // UI fields
    private Composite parentComposite;
    private FormToolkit toolkit;
    private Composite partitionSpecificDetailsComposite;
    private Section specificSettingsSection;
    private Composite specificSettingsSectionComposite;
    private ComboViewer partitionTypeComboViewer;
    private Text idText;
    private Text suffixText;
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
    private ISelectionChangedListener contextEntryTableViewerSelectionListener = new ISelectionChangedListener()
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
            AttributeValueDialog dialog = new AttributeValueDialog( new AttributeValueObject( "", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( AttributeValueDialog.OK == dialog.open() && dialog.isDirty() )
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
                        // Will never occur
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

    private ISelectionChangedListener partitionTypeComboViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            PartitionType type = ( PartitionType ) ( ( StructuredSelection ) partitionTypeComboViewer.getSelection() )
                .getFirstElement();

            if ( ( partitionWrapper != null ) && ( partitionWrapper.getPartition() != null ) )
            {
                PartitionBean partition = partitionWrapper.getPartition();

                // Only change the type if it's a different one
                if ( type != PartitionType.fromPartition( partition ) )
                {
                    switch ( type )
                    {
                        case JDBM:
                            JdbmPartitionBean newJdbmPartition = new JdbmPartitionBean();
                            copyPartitionProperties( partition, newJdbmPartition );
                            partitionWrapper.setPartition( newJdbmPartition );
                            break;
                        case MAVIBOT:
                            MavibotPartitionBean newMavibotPartition = new MavibotPartitionBean();
                            copyPartitionProperties( partition, newMavibotPartition );
                            partitionWrapper.setPartition( newMavibotPartition );
                            break;
                        default:
                            break;
                    }

                    refresh();
                    setEditorDirty();
                }
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
        instance = this;
        masterDetailsBlock = pmdb;
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( Composite parent )
    {
        this.parentComposite = parent;
        parent.setLayout( new GridLayout() );

        createGeneralDetailsSection( parent, toolkit );
        createContextEntrySection( parent, toolkit );
        createPartitionSpecificSettingsSection( parent, toolkit );
        createIndexesSection( parent, toolkit );
    }


    /**
     * Creates the General Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createGeneralDetailsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( Messages.getString( "PartitionDetailsPage.PartitionsGeneralDetails" ) ); //$NON-NLS-1$
        section.setDescription( Messages.getString( "PartitionDetailsPage.SetPropertiesOfPartition" ) ); //$NON-NLS-1$
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Type
        toolkit.createLabel( client, "Partition Type:" );
        Combo partitionTypeCombo = new Combo( client, SWT.READ_ONLY | SWT.SINGLE );
        partitionTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        partitionTypeComboViewer = new ComboViewer( partitionTypeCombo );
        partitionTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        partitionTypeComboViewer.setInput( new Object[]
            { PartitionType.JDBM, PartitionType.MAVIBOT } );

        // ID
        toolkit.createLabel( client, Messages.getString( "PartitionDetailsPage.Id" ) ); //$NON-NLS-1$
        idText = toolkit.createText( client, "" ); //$NON-NLS-1$
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Suffix
        toolkit.createLabel( client, "Suffix:" ); //$NON-NLS-1$
        suffixText = toolkit.createText( client, "" ); //$NON-NLS-1$
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Synchronisation On Write
        synchOnWriteCheckbox = toolkit.createButton( client,
            Messages.getString( "PartitionDetailsPage.SynchronizationOnWrite" ), SWT.CHECK ); //$NON-NLS-1$
        synchOnWriteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
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
        section.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        client.setLayout( new GridLayout( 2, false ) );
        section.setClient( client );

        // Auto Generate Context Entry Checkbox
        autoGenerateContextEntryCheckbox = toolkit.createButton( client,
            Messages.getString( "PartitionDetailsPage.AutoGenerateContextEntryFromSuffixDn" ), //$NON-NLS-1$
            SWT.CHECK );
        autoGenerateContextEntryCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Context Entry Table Viewer
        Table contextEntryTable = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 62;
        gd.widthHint = 50;
        contextEntryTable.setLayoutData( gd );
        TableColumn idColumn = new TableColumn( contextEntryTable, SWT.LEFT, 0 );
        idColumn.setText( Messages.getString( "PartitionDetailsPage.Attribute" ) ); //$NON-NLS-1$
        idColumn.setWidth( 100 );
        TableColumn valueColumn = new TableColumn( contextEntryTable, SWT.LEFT, 1 );
        valueColumn.setText( Messages.getString( "PartitionDetailsPage.Value" ) ); //$NON-NLS-1$
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
        contextEntryAddButton = toolkit.createButton( client,
            Messages.getString( "PartitionDetailsPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        contextEntryAddButton.setLayoutData( buttonsGD );

        // Context Entry Edit Button
        contextEntryEditButton = toolkit.createButton( client,
            Messages.getString( "PartitionDetailsPage.Edit" ), SWT.PUSH ); //$NON-NLS-1$
        contextEntryEditButton.setEnabled( false );
        contextEntryEditButton.setLayoutData( buttonsGD );

        // Context Entry Delete Button
        contextEntryDeleteButton = toolkit.createButton( client,
            Messages.getString( "PartitionDetailsPage.Delete" ), SWT.PUSH ); //$NON-NLS-1$
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
     * Creates the Partition Specific Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createPartitionSpecificSettingsSection( Composite parent, FormToolkit toolkit )
    {
        // Creating the Section
        specificSettingsSection = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED
            | Section.TITLE_BAR );
        specificSettingsSection.marginWidth = 10;
        specificSettingsSection.setText( "Partition Specific Settings" );
        specificSettingsSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the Composite
        specificSettingsSectionComposite = toolkit.createComposite( specificSettingsSection );
        toolkit.paintBordersFor( specificSettingsSectionComposite );
        GridLayout gd = new GridLayout();
        gd.marginHeight = gd.marginWidth = 0;
        gd.verticalSpacing = gd.horizontalSpacing = 0;
        specificSettingsSectionComposite.setLayout( gd );
        specificSettingsSection.setClient( specificSettingsSectionComposite );
    }


    /**
     * Disposes the inner specific settings composite.
     */
    private void disposeSpecificSettingsComposite()
    {
        if ( ( partitionSpecificDetailsComposite != null ) && !( partitionSpecificDetailsComposite.isDisposed() ) )
        {
            partitionSpecificDetailsComposite.dispose();
        }

        partitionSpecificDetailsComposite = null;
    }


    /**
     * Updates the partition specific settings section.
     */
    private void updatePartitionSpecificSettingsSection()
    {
        // Disposing existing specific settings composite
        disposeSpecificSettingsComposite();

        // Create the specific settings block content
        if ( partitionSpecificDetailsBlock != null )
        {
            partitionSpecificDetailsComposite = partitionSpecificDetailsBlock.createBlockContent(
                specificSettingsSectionComposite,
                toolkit );
            partitionSpecificDetailsBlock.refresh();
        }

        parentComposite.layout( true, true );

        // Making the section visible or not
        specificSettingsSection.setVisible( partitionSpecificDetailsBlock != null );
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
        indexedAttributesSection.setText( Messages.getString( "PartitionDetailsPage.IndexedAttributes" ) ); //$NON-NLS-1$
        indexedAttributesSection.setDescription( Messages
            .getString( "PartitionDetailsPage.SetIndexedAttributesOfPartition" ) ); //$NON-NLS-1$
        indexedAttributesSection.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
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
                    JdbmIndexBean jdbmIndexBean = ( JdbmIndexBean ) element;

                    return NLS.bind( "{0} [{1}]", jdbmIndexBean.getIndexAttributeId(), //$NON-NLS-1$
                        jdbmIndexBean.getIndexCacheSize() );
                }
                else if ( element instanceof MavibotIndexBean )
                {
                    MavibotIndexBean mavibotIndexBean = ( MavibotIndexBean ) element;

                    return mavibotIndexBean.getIndexAttributeId();
                }

                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof IndexBean )
                {
                    return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                        ApacheDS2ConfigurationPluginConstants.IMG_INDEX );
                }

                return super.getImage( element );
            };
        } );

        // Add button
        indexesAddButton = toolkit.createButton( indexedAttributesClient,
            Messages.getString( "PartitionDetailsPage.Add" ), SWT.PUSH ); //$NON-NLS-1$
        indexesAddButton.setLayoutData( createNewButtonGridData() );

        // Edit button
        indexesEditButton = toolkit.createButton( indexedAttributesClient,
            Messages.getString( "PartitionDetailsPage.Edit" ), SWT.PUSH ); //$NON-NLS-1$
        indexesEditButton.setEnabled( false );
        indexesEditButton.setLayoutData( createNewButtonGridData() );

        // Delete button
        indexesDeleteButton = toolkit.createButton( indexedAttributesClient,
            Messages.getString( "PartitionDetailsPage.Delete" ), SWT.PUSH ); //$NON-NLS-1$
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
        partitionTypeComboViewer.addSelectionChangedListener( partitionTypeComboViewerSelectionChangedListener );

        idText.addModifyListener( textModifyListener );
        suffixText.addModifyListener( textModifyListener );
        suffixText.addModifyListener( suffixTextModifyListener );
        synchOnWriteCheckbox.addSelectionListener( checkboxSelectionListener );

        autoGenerateContextEntryCheckbox.addSelectionListener( autoGenerateContextEntryCheckboxSelectionListener );
        contextEntryTableViewer.addDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.addSelectionChangedListener( contextEntryTableViewerSelectionListener );
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
        partitionTypeComboViewer.removeSelectionChangedListener( partitionTypeComboViewerSelectionChangedListener );

        idText.removeModifyListener( textModifyListener );
        suffixText.removeModifyListener( textModifyListener );
        suffixText.removeModifyListener( suffixTextModifyListener );
        synchOnWriteCheckbox.removeSelectionListener( checkboxSelectionListener );

        autoGenerateContextEntryCheckbox.removeSelectionListener( autoGenerateContextEntryCheckboxSelectionListener );
        contextEntryTableViewer.removeDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.removeSelectionChangedListener( contextEntryTableViewerSelectionListener );
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
            partitionWrapper = ( PartitionWrapper ) ssel.getFirstElement();
        }
        else
        {
            partitionWrapper = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( ( partitionWrapper != null ) && ( partitionWrapper.getPartition() != null ) )
        {
            PartitionBean partition = partitionWrapper.getPartition();

            // ID
            partition.setPartitionId( idText.getText() );

            // Suffix
            try
            {
                partition.setPartitionSuffix( new Dn( suffixText.getText() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                // Stay silent
            }

            // Context Entry
            if ( contextEntry.size() > 0 )
            {
                LdifEntry ldifEntry = new LdifEntry( contextEntry );
                ldifEntry.setDn( partition.getPartitionSuffix() );
                partition.setContextEntry( ldifEntry.toString() );
            }
            else
            {
                partition.setContextEntry( null );
            }

            // Synchronization on write
            partition.setPartitionSyncOnWrite( synchOnWriteCheckbox.getSelection() );

            //
            // Specific Settings
            //
            if ( partitionSpecificDetailsBlock != null )
            {
                partitionSpecificDetailsBlock.commit( onSave );
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
        toolkit = form.getToolkit();
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

        if ( ( partitionWrapper != null ) && ( partitionWrapper.getPartition() != null ) )
        {
            PartitionBean partition = partitionWrapper.getPartition();

            // Checking if the selected partition is the system partition
            boolean isSystemPartition = PartitionsPage.isSystemPartition( partition );

            // Type
            PartitionType partitionType = PartitionType.fromPartition( partition );
            partitionTypeComboViewer.setSelection( new StructuredSelection( partitionType ) );
            partitionTypeComboViewer.getCombo().setEnabled( !isSystemPartition );

            // ID
            String id = partition.getPartitionId();
            idText.setText( ( id == null ) ? "" : id ); //$NON-NLS-1$
            idText.setEnabled( !isSystemPartition );

            // Suffix
            Dn suffix = partition.getPartitionSuffix();
            suffixText.setText( ( suffix == null ) ? "" : suffix.toString() ); //$NON-NLS-1$
            suffixText.setEnabled( !isSystemPartition );

            // Auto Generate Context Entry
            autoGenerateContextEntryCheckbox.setSelection( true ); // TODO review this

            // Context Entry
            refreshContextEntry();

            // Indexed Attributes
            indexesList = partition.getIndexes();
            indexesTableViewer.setInput( indexesList );

            // Synchronization on write
            synchOnWriteCheckbox.setSelection( partition.isPartitionSyncOnWrite() );

            //
            // Specific Settings
            //

            // JdbmPartitionBean Type
            if ( partition instanceof JdbmPartitionBean )
            {
                partitionTypeComboViewer.setSelection( new StructuredSelection( PartitionType.JDBM ) );
                partitionSpecificDetailsBlock = new JdbmPartitionSpecificDetailsBlock( instance,
                    ( JdbmPartitionBean ) partition );
            }
            // MavibotPartitionBean Type
            else if ( partition instanceof MavibotPartitionBean )
            {
                partitionTypeComboViewer.setSelection( new StructuredSelection( PartitionType.MAVIBOT ) );
                partitionSpecificDetailsBlock = new MavibotPartitionSpecificDetailsBlock( instance,
                    ( MavibotPartitionBean ) partition );
            }
            else
            {
                partitionTypeComboViewer.setSelection( null );
                partitionSpecificDetailsBlock = null;
            }

            updatePartitionSpecificSettingsSection();
        }

        addListeners();
    }


    private void refreshContextEntry()
    {
        if ( ( partitionWrapper != null ) && ( partitionWrapper.getPartition() != null ) )
        {
            PartitionBean partition = partitionWrapper.getPartition();

            String contextEntryString = partition.getContextEntry();

            if ( ( contextEntryString != null ) && ( !"".equals( contextEntryString ) ) ) //$NON-NLS-1$
            {
                try
                {
                    // Replace '\n' to real LF
                    contextEntryString = contextEntryString.replaceAll( "\\\\n", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$

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

            // TODO Verify this

            boolean enabled = !autoGenerateContextEntryCheckbox.getSelection();
            contextEntryTableViewer.getTable().setEnabled( enabled );
            contextEntryAddButton.setEnabled( enabled );
            contextEntryEditButton.setEnabled( enabled );
            contextEntryDeleteButton.setEnabled( enabled );
        }
    }


    /**
     * Auto generates the context entry.
     */
    private void autoGenerateContextEntry()
    {
        if ( ( partitionWrapper != null ) && ( partitionWrapper.getPartition() != null ) )
        {
            PartitionBean partition = partitionWrapper.getPartition();

            if ( autoGenerateContextEntryCheckbox.getSelection() )
            {
                try
                {
                    Dn suffixDn = new Dn( suffixText.getText() );
                    partition.setContextEntry( PartitionsMasterDetailsBlock.getContextEntryLdif( suffixDn ) );
                    refreshContextEntry();
                }
                catch ( LdapInvalidDnException e1 )
                {
                    // Silent
                }
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
            PartitionType partitionType = ( PartitionType ) ( ( StructuredSelection ) partitionTypeComboViewer
                .getSelection() ).getFirstElement();

            if ( partitionType != null )
            {
                IndexBean editedIndex = null;

                // JDBM partition
                if ( partitionType == PartitionType.JDBM )
                {
                    // Getting the selected JDBM index
                    JdbmIndexBean index = ( JdbmIndexBean ) selection.getFirstElement();

                    // Creating a JDBM dialog
                    JdbmIndexDialog dialog = new JdbmIndexDialog( index );

                    if ( JdbmIndexDialog.OK == dialog.open() && dialog.isDirty() )
                    {
                        editedIndex = index;
                    }
                }
                // Mavibot Partition
                else if ( partitionType == PartitionType.MAVIBOT )
                {
                    // Getting the selected Mavibot index
                    MavibotIndexBean index = ( MavibotIndexBean ) selection.getFirstElement();

                    // Creating a Mavibot dialog
                    MavibotIndexDialog dialog = new MavibotIndexDialog( index );

                    if ( MavibotIndexDialog.OK == dialog.open() && dialog.isDirty() )
                    {
                        editedIndex = index;
                    }
                }

                // Checking the new index
                if ( editedIndex != null )
                {
                    indexesTableViewer.refresh();
                    masterDetailsBlock.setEditorDirty();
                }
            }
        }
    }


    /**
     * Adds a new index and opens the index dialog.
     */
    private void addNewIndex()
    {
        PartitionType partitionType = ( PartitionType ) ( ( StructuredSelection ) partitionTypeComboViewer
            .getSelection() ).getFirstElement();

        if ( partitionType != null )
        {
            IndexBean newIndex = null;

            // JDBM partition
            if ( partitionType == PartitionType.JDBM )
            {
                JdbmIndexBean newJdbmIndex = new JdbmIndexBean();
                newJdbmIndex.setIndexAttributeId( "" ); //$NON-NLS-1$
                newJdbmIndex.setIndexCacheSize( 100 );

                JdbmIndexDialog dialog = new JdbmIndexDialog( newJdbmIndex );
                if ( JdbmIndexDialog.OK == dialog.open() )
                {
                    newIndex = dialog.getIndex();
                }
                else
                {
                    // Cancel
                    return;
                }
            }
            // Mavibot Partition
            else if ( partitionType == PartitionType.MAVIBOT )
            {
                MavibotIndexBean newMavibotIndex = new MavibotIndexBean();
                newMavibotIndex.setIndexAttributeId( "" ); //$NON-NLS-1$

                MavibotIndexDialog dialog = new MavibotIndexDialog( newMavibotIndex );
                if ( MavibotIndexDialog.OK == dialog.open() )
                {
                    newIndex = dialog.getIndex();
                }
                else
                {
                    // Cancel
                    return;
                }
            }

            // Checking the new index
            if ( newIndex != null )
            {
                indexesList.add( newIndex );
                indexesTableViewer.refresh();
                indexesTableViewer.setSelection( new StructuredSelection( newIndex ) );
                masterDetailsBlock.setEditorDirty();
            }
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
            IndexBean selectedIndex = ( IndexBean ) selection.getFirstElement();

            if ( MessageDialog
                .openConfirm( indexesDeleteButton.getShell(),
                    Messages.getString( "PartitionDetailsPage.ConfirmDelete" ), //$NON-NLS-1$
                    NLS.bind(
                        Messages.getString( "PartitionDetailsPage.AreYouSureDeleteIndex" ), selectedIndex.getIndexAttributeId() ) ) ) //$NON-NLS-1$
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
            if ( AttributeValueDialog.OK == dialog.open() && dialog.isDirty() )
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
                        // Will never occur
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


    /**
     * Sets the associated editor dirty.
     */
    public void setEditorDirty()
    {
        masterDetailsBlock.setEditorDirty();
    }


    /**
     * Copies partition properties from one instance to the other.
     *
     * @param original the original partition
     * @param destination the destination partition
     */
    private void copyPartitionProperties( PartitionBean original, PartitionBean destination )
    {
        if ( ( original != null ) && ( destination != null ) )
        {
            // Simple properties
            destination.setContextEntry( original.getContextEntry() );
            destination.setDescription( original.getDescription() );
            destination.setDn( original.getDn() );
            destination.setEnabled( original.isEnabled() );
            destination.setPartitionId( original.getPartitionId() );
            destination.setPartitionSuffix( original.getPartitionSuffix() );
            destination.setPartitionSyncOnWrite( original.isPartitionSyncOnWrite() );

            // Indexes
            List<IndexBean> originalIndexes = original.getIndexes();
            List<IndexBean> destinationIndexes = new ArrayList<IndexBean>();

            if ( originalIndexes != null )
            {
                for ( IndexBean originalIndexBean : originalIndexes )
                {
                    if ( destination instanceof JdbmPartitionBean )
                    {
                        JdbmIndexBean destinationIndexBean = new JdbmIndexBean();

                        destinationIndexBean.setIndexAttributeId( originalIndexBean.getIndexAttributeId() );
                        destinationIndexBean.setIndexHasReverse( originalIndexBean.getIndexHasReverse() );

                        destinationIndexes.add( destinationIndexBean );
                    }
                    else if ( destination instanceof MavibotPartitionBean )
                    {
                        MavibotIndexBean destinationIndexBean = new MavibotIndexBean();

                        destinationIndexBean.setIndexAttributeId( originalIndexBean.getIndexAttributeId() );
                        destinationIndexBean.setIndexHasReverse( originalIndexBean.getIndexHasReverse() );

                        destinationIndexes.add( destinationIndexBean );
                    }
                }
            }

            destination.setIndexes( destinationIndexes );
        }
    }
}
