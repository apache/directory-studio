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


import java.util.List;

import org.apache.directory.server.config.beans.IndexBean;
import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.apache.directory.server.config.beans.JdbmPartitionBean;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
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
import org.eclipse.osgi.util.NLS;
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
 */
public class PartitionDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private PartitionsMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input Partition */
    private JdbmPartitionBean input;

    /** The Indexed Attributes List */
    private List<IndexBean> indexedAttributes;

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
            //            editSelectedIndexedAttribute();
        }
    };

    /** The Listener for the Add button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeAddButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            //            IndexedAttributeDialog dialog = new IndexedAttributeDialog( new IndexedAttribute( "", 0 ) ); //$NON-NLS-1$
            //            if ( Dialog.OK == dialog.open() )
            //            {
            //                indexedAttributes.add( dialog.getIndexedAttribute() );
            //                indexedAttributesTableViewer.refresh();
            //                masterDetailsBlock.setEditorDirty();
            //                dirty = true;
            //            }
        }
    };

    /** The Listener for the Edit button of the Indexed Attributes Section */
    private SelectionListener indexedAttributeEditButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            //            editSelectedIndexedAttribute();
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
                //                IndexedAttribute indexedAttribute = ( IndexedAttribute ) selection.getFirstElement();
                //
                //                indexedAttributes.remove( indexedAttribute );
                //                indexedAttributesTableViewer.refresh();
                //                masterDetailsBlock.setEditorDirty();
                //                dirty = true;
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
        indexedAttributesSection.setText( "Indexed Attributes" );
        indexedAttributesSection.setDescription( "Set the indexed attributes of the partition." );
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
        indexedAttributesTableViewer.setLabelProvider( new LabelProvider()
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
        } );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, "Add", SWT.PUSH );
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, "Edit", SWT.PUSH );
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
            input = ( JdbmPartitionBean ) ssel.getFirstElement();
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
            input.setPartitionId( idText.getText() );
            input.setPartitionCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
            try
            {
                input.setPartitionSuffix( new Dn( suffixText.getText() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            input.setJdbmPartitionOptimizerEnabled( enableOptimizerCheckbox.getSelection() );
            input.setPartitionSyncOnWrite( synchOnWriteCheckbox.getSelection() );
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
        String id = input.getPartitionId();
        idText.setText( ( id == null ) ? "" : id ); //$NON-NLS-1$

        // Cache Size
        cacheSizeText.setText( "" + input.getPartitionCacheSize() ); //$NON-NLS-1$

        // Suffix
        Dn suffix = input.getPartitionSuffix();
        suffixText.setText( ( suffix == null ) ? "" : suffix.toString() ); //$NON-NLS-1$

        // Enable Optimizer
        enableOptimizerCheckbox.setSelection( input.isJdbmPartitionOptimizerEnabled() );

        // Synchronization on write
        synchOnWriteCheckbox.setSelection( input.isPartitionSyncOnWrite() );

        // Indexed Attributes
        indexedAttributes = input.getIndexes();
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
}
