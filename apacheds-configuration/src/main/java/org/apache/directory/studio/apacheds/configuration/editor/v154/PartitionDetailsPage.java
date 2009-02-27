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
package org.apache.directory.studio.apacheds.configuration.editor.v154;


import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.v154.dialogs.IndexedAttributeDialog;
import org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute;
import org.apache.directory.studio.apacheds.configuration.model.v154.Partition;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
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
 * @version $Rev$, $Date$
 */
public class PartitionDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Partition */
    private Partition input;

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
            IndexedAttributeDialog dialog = new IndexedAttributeDialog( new IndexedAttribute( "", 0 ) ); //$NON-NLS-1$
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
        section.setText( Messages.getString("PartitionDetailsPage.PartitionDetails") ); //$NON-NLS-1$
        section.setDescription( Messages.getString("PartitionDetailsPage.PartitionsDetailsDescription") ); //$NON-NLS-1$
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 3, false );
        client.setLayout( glayout );
        section.setClient( client );

        // ID
        toolkit.createLabel( client, Messages.getString("PartitionDetailsPage.ID") ); //$NON-NLS-1$
        idText = toolkit.createText( client, "" ); //$NON-NLS-1$
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Cache Size
        toolkit.createLabel( client, Messages.getString("PartitionDetailsPage.CacheSize") ); //$NON-NLS-1$
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
        toolkit.createLabel( client, Messages.getString("PartitionDetailsPage.Suffix") ); //$NON-NLS-1$
        suffixText = toolkit.createText( client, "" ); //$NON-NLS-1$
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Optimizer
        enableOptimizerCheckbox = toolkit.createButton( client, Messages.getString("PartitionDetailsPage.EnableOptimizer"), SWT.CHECK ); //$NON-NLS-1$
        enableOptimizerCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Synchronisation On Write
        synchOnWriteCheckbox = toolkit.createButton( client, Messages.getString("PartitionDetailsPage.SynchronizationOnWrite"), SWT.CHECK ); //$NON-NLS-1$
        synchOnWriteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
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
        indexedAttributesSection.setText( Messages.getString("PartitionDetailsPage.IndexedAttributes") ); //$NON-NLS-1$
        indexedAttributesSection.setDescription( Messages.getString("PartitionDetailsPage.IndexedAttributesDescription") ); //$NON-NLS-1$
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

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, Messages.getString("PartitionDetailsPage.Add"), SWT.PUSH ); //$NON-NLS-1$
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, Messages.getString("PartitionDetailsPage.Edit"), SWT.PUSH ); //$NON-NLS-1$
        indexedAttributeEditButton.setEnabled( false );
        indexedAttributeEditButton.setLayoutData( buttonsGD );

        indexedAttributeDeleteButton = toolkit.createButton( indexedAttributesClient, Messages.getString("PartitionDetailsPage.Delete"), SWT.PUSH ); //$NON-NLS-1$
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
        idText.setText( ( id == null ) ? "" : id ); //$NON-NLS-1$

        // Cache Size
        cacheSizeText.setText( "" + input.getCacheSize() ); //$NON-NLS-1$

        // Suffix
        String suffix = input.getSuffix();
        suffixText.setText( ( suffix == null ) ? "" : suffix ); //$NON-NLS-1$

        // Enable Optimizer
        enableOptimizerCheckbox.setSelection( input.isEnableOptimizer() );

        // Synchronization on write
        synchOnWriteCheckbox.setSelection( input.isSynchronizationOnWrite() );

        // Indexed Attributes
        indexedAttributes = input.getIndexedAttributes();
        indexedAttributesTableViewer.setInput( indexedAttributes );

        addListeners();
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
}
