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
package org.apache.directory.studio.apacheds.configuration.editor.v153;


import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.apache.directory.studio.apacheds.configuration.editor.v153.dialogs.AttributeValueDialog;
import org.apache.directory.studio.apacheds.configuration.editor.v153.dialogs.AttributeValueObject;
import org.apache.directory.studio.apacheds.configuration.editor.v153.dialogs.IndexedAttributeDialog;
import org.apache.directory.studio.apacheds.configuration.model.v153.IndexedAttribute;
import org.apache.directory.studio.apacheds.configuration.model.v153.Partition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
    private Partition input;

    /** The Context Entry */
    private Attributes contextEntry;

    /** The Indexed Attributes List */
    private List<IndexedAttribute> indexedAttributes;

    /** The dirty flag */
    private boolean dirty = false;

    // UI fields
    private Text idText;
    private Text cacheSizeText;
    private Text suffixText;
    private Button enableOptimizerCheckbox;
    private Button synchOnWriteCheckbox;
    private Table contextEntryTable;
    private TableViewer contextEntryTableViewer;
    private Button contextEntryAddButton;
    private Button contextEntryEditButton;
    private Button contextEntryDeleteButton;
    private TableViewer indexedAttributesTableViewer;
    private Button indexedAttributeAddButton;
    private Button indexedAttributeEditButton;
    private Button indexedAttributeDeleteButton;

    // Listeners
    /** The Text Modify Listener */
    private ModifyListener textModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            masterDetailsBlock.setEditorDirty();
            dirty = true;
        }
    };

    /** The Checkbox Selection Listener */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            masterDetailsBlock.setEditorDirty();
            dirty = true;
        }
    };

    /** The Selection Changed Listener for the Context Entry Table Viewer */
    private ISelectionChangedListener contextEntryTableViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            contextEntryEditButton.setEnabled( !event.getSelection().isEmpty() );
            contextEntryDeleteButton.setEnabled( !event.getSelection().isEmpty() );
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
                    attribute.add( newAttributeValueObject.getValue() );
                }
                else
                {
                    contextEntry.put( new BasicAttribute( newAttributeValueObject.getAttribute(),
                        newAttributeValueObject.getValue() ) );
                }

                contextEntryTableViewer.refresh();
                resizeContextEntryTableColumnsToFit();
                masterDetailsBlock.setEditorDirty();
                dirty = true;
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
                    dirty = true;
                }
            }
        }
    };

    /** The Selection Changed Listener for the Indexed Attributes Table Viewer */
    private ISelectionChangedListener indexedAttributesTableViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            indexedAttributeEditButton.setEnabled( !event.getSelection().isEmpty() );
            indexedAttributeDeleteButton.setEnabled( !event.getSelection().isEmpty() );
        }
    };

    /** The Double Click Listener for the Indexed Attributes Table Viewer */
    private IDoubleClickListener indexedAttributesTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editSelectedIndexedAttribute();
        }
    };

    /** The Listener for the Add button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeAddButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            IndexedAttributeDialog dialog = new IndexedAttributeDialog( new IndexedAttribute( "", 0 ) );
            if ( Dialog.OK == dialog.open() )
            {
                indexedAttributes.add( dialog.getIndexedAttribute() );
                indexedAttributesTableViewer.refresh();
                masterDetailsBlock.setEditorDirty();
                dirty = true;
            }
        }
    };

    /** The Listener for the Edit button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeEditButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editSelectedIndexedAttribute();
        }
    };

    /** The Listener for the Delete button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeDeleteButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) indexedAttributesTableViewer.getSelection();
            if ( !selection.isEmpty() )
            {
                IndexedAttribute indexedAttribute = ( IndexedAttribute ) selection.getFirstElement();

                indexedAttributes.remove( indexedAttribute );
                indexedAttributesTableViewer.refresh();
                masterDetailsBlock.setEditorDirty();
                dirty = true;
            }
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
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
        createIndexedAttributesSection( parent, toolkit );
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
        section.setText( "Partition Details" ); //$NON-NLS-1$
        section.setDescription( "Set the properties of the partition." ); //$NON-NLS-1$
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
        idText = toolkit.createText( client, "" );
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Cache Size
        toolkit.createLabel( client, "Cache Size:" );
        cacheSizeText = toolkit.createText( client, "" );
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
        toolkit.createLabel( client, "Suffix:" );
        suffixText = toolkit.createText( client, "" );
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Optimizer
        enableOptimizerCheckbox = toolkit.createButton( client, "Enable optimizer", SWT.CHECK );
        enableOptimizerCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Synchronisation On Write
        synchOnWriteCheckbox = toolkit.createButton( client, "Synchronization on write", SWT.CHECK );
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

        contextEntryTable = toolkit.createTable( client, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 103;
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

                Attributes attributes = ( Attributes ) inputElement;

                NamingEnumeration<? extends Attribute> ne = attributes.getAll();
                while ( ne.hasMoreElements() )
                {
                    Attribute attribute = ( Attribute ) ne.nextElement();
                    try
                    {
                        NamingEnumeration<?> values = attribute.getAll();
                        while ( values.hasMoreElements() )
                        {
                            elements.add( new AttributeValueObject( attribute.getID(), values.nextElement() ) );
                        }
                    }
                    catch ( NamingException e )
                    {
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

        contextEntryAddButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        contextEntryAddButton.setLayoutData( buttonsGD );

        contextEntryEditButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        contextEntryEditButton.setEnabled( false );
        contextEntryEditButton.setLayoutData( buttonsGD );

        contextEntryDeleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
        contextEntryDeleteButton.setEnabled( false );
        contextEntryDeleteButton.setLayoutData( buttonsGD );
    }


    /**
     * Creates the Indexed Attributes Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createIndexedAttributesSection( Composite parent, FormToolkit toolkit )
    {
        Section indexedAttributesSection = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        indexedAttributesSection.marginWidth = 10;
        indexedAttributesSection.setText( "Indexed Attributes" ); //$NON-NLS-1$
        indexedAttributesSection.setDescription( "Set the indexed attributes of the partition." ); //$NON-NLS-1$
        indexedAttributesSection.setLayoutData( new TableWrapData( TableWrapData.FILL ) );
        Composite indexedAttributesClient = toolkit.createComposite( indexedAttributesSection );
        toolkit.paintBordersFor( indexedAttributesClient );
        indexedAttributesClient.setLayout( new GridLayout( 2, false ) );
        indexedAttributesSection.setClient( indexedAttributesClient );

        Table indexedAttributesTable = toolkit.createTable( indexedAttributesClient, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 80;
        indexedAttributesTable.setLayoutData( gd );
        indexedAttributesTableViewer = new TableViewer( indexedAttributesTable );
        indexedAttributesTableViewer.setContentProvider( new ArrayContentProvider() );
        indexedAttributesTableViewer.setLabelProvider( new LabelProvider() );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, "Add...", SWT.PUSH );
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, "Edit...", SWT.PUSH );
        indexedAttributeEditButton.setEnabled( false );
        indexedAttributeEditButton.setLayoutData( buttonsGD );

        indexedAttributeDeleteButton = toolkit.createButton( indexedAttributesClient, "Delete", SWT.PUSH );
        indexedAttributeDeleteButton.setEnabled( false );
        indexedAttributeDeleteButton.setLayoutData( buttonsGD );
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        idText.addModifyListener( textModifyListener );
        cacheSizeText.addModifyListener( textModifyListener );
        suffixText.addModifyListener( textModifyListener );
        enableOptimizerCheckbox.addSelectionListener( checkboxSelectionListener );
        synchOnWriteCheckbox.addSelectionListener( checkboxSelectionListener );

        contextEntryTableViewer.addDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.addSelectionChangedListener( contextEntryTableViewerListener );
        contextEntryAddButton.addSelectionListener( contextEntryAddButtonListener );
        contextEntryEditButton.addSelectionListener( contextEntryEditButtonListener );
        contextEntryDeleteButton.addSelectionListener( contextEntryDeleteButtonListener );

        indexedAttributesTableViewer.addSelectionChangedListener( indexedAttributesTableViewerListener );
        indexedAttributesTableViewer.addDoubleClickListener( indexedAttributesTableViewerDoubleClickListener );
        indexedAttributeAddButton.addSelectionListener( indexedAttributeAddButtonListener );
        indexedAttributeEditButton.addSelectionListener( indexedAttributeEditButtonListener );
        indexedAttributeDeleteButton.addSelectionListener( indexedAttributeDeleteButtonListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        idText.removeModifyListener( textModifyListener );
        cacheSizeText.removeModifyListener( textModifyListener );
        suffixText.removeModifyListener( textModifyListener );
        enableOptimizerCheckbox.removeSelectionListener( checkboxSelectionListener );
        synchOnWriteCheckbox.removeSelectionListener( checkboxSelectionListener );

        contextEntryTableViewer.removeDoubleClickListener( contextEntryTableViewerDoubleClickListener );
        contextEntryTableViewer.removeSelectionChangedListener( contextEntryTableViewerListener );
        contextEntryAddButton.removeSelectionListener( contextEntryAddButtonListener );
        contextEntryEditButton.removeSelectionListener( contextEntryEditButtonListener );
        contextEntryDeleteButton.removeSelectionListener( contextEntryDeleteButtonListener );

        indexedAttributesTableViewer.removeSelectionChangedListener( indexedAttributesTableViewerListener );
        indexedAttributesTableViewer.removeDoubleClickListener( indexedAttributesTableViewerDoubleClickListener );
        indexedAttributeAddButton.removeSelectionListener( indexedAttributeAddButtonListener );
        indexedAttributeEditButton.removeSelectionListener( indexedAttributeEditButtonListener );
        indexedAttributeDeleteButton.removeSelectionListener( indexedAttributeDeleteButtonListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( Partition ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit( boolean onSave )
    {
        if ( input != null )
        {
            input.setId( idText.getText() );
            input.setCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
            input.setSuffix( suffixText.getText() );
            input.setEnableOptimizer( enableOptimizerCheckbox.getSelection() );
            input.setSynchronizationOnWrite( synchOnWriteCheckbox.getSelection() );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh()
    {
        removeListeners();

        // ID
        String id = input.getId();
        idText.setText( ( id == null ) ? "" : id );

        // Cache Size
        cacheSizeText.setText( "" + input.getCacheSize() );

        // Suffix
        String suffix = input.getSuffix();
        suffixText.setText( ( suffix == null ) ? "" : suffix );

        // Enable Optimizer
        enableOptimizerCheckbox.setSelection( input.isEnableOptimizer() );

        // Synchronization on write
        synchOnWriteCheckbox.setSelection( input.isSynchronizationOnWrite() );

        // Context Entry
        contextEntry = input.getContextEntry();
        contextEntryTableViewer.setInput( contextEntry );
        resizeContextEntryTableColumnsToFit();

        // Indexed Attributes
        indexedAttributes = input.getIndexedAttributes();
        indexedAttributesTableViewer.setInput( indexedAttributes );

        addListeners();
    }


    /**
     * Resizes the columns to fit the size of the cells.
     */
    private void resizeContextEntryTableColumnsToFit()
    {
        // Resizing the first column
        contextEntryTable.getColumn( 0 ).pack();
        // Adding a little space to the first column
        contextEntryTable.getColumn( 0 ).setWidth( contextEntryTable.getColumn( 0 ).getWidth() + 5 );
        // Resizing the second column
        contextEntryTable.getColumn( 1 ).pack();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {
        idText.setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }


    /**
     * Opens an Indexed Attribute Dialog with the selected Indexed Attribute in the
     * Indexed Attributes Table Viewer.
     */
    private void editSelectedIndexedAttribute()
    {
        StructuredSelection selection = ( StructuredSelection ) indexedAttributesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            IndexedAttribute indexedAttribute = ( IndexedAttribute ) selection.getFirstElement();

            IndexedAttributeDialog dialog = new IndexedAttributeDialog( indexedAttribute );
            if ( Dialog.OK == dialog.open() && dialog.isDirty() )
            {
                indexedAttributesTableViewer.refresh();
                masterDetailsBlock.setEditorDirty();
                dirty = true;
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
            Object oldValue = attributeValueObject.getValue();

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
                    attribute.add( newAttributeValueObject.getValue() );
                }
                else
                {
                    contextEntry.put( new BasicAttribute( newAttributeValueObject.getAttribute(),
                        newAttributeValueObject.getValue() ) );
                }

                contextEntryTableViewer.refresh();
                resizeContextEntryTableColumnsToFit();
                masterDetailsBlock.setEditorDirty();
                dirty = true;
            }
        }
    }
}
