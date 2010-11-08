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


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
        section.setDescription( " Partitions Details Description" );
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
        synchOnWriteCheckbox = toolkit.createButton( client, "Synchronization On Write:", SWT.CHECK );
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
        indexedAttributesSection.setDescription( "Indexed Attributes Description" );
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

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, "Add", SWT.PUSH );
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, "Edit", SWT.PUSH );
        indexedAttributeEditButton.setEnabled( false );
        indexedAttributeEditButton.setLayoutData( buttonsGD );

        indexedAttributeDeleteButton = toolkit.createButton( indexedAttributesClient, "Delete", SWT.PUSH );
        indexedAttributeDeleteButton.setEnabled( false );
        indexedAttributeDeleteButton.setLayoutData( buttonsGD );
    }




    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit( boolean onSave )
    {
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


    public void refresh()
    {
        // TODO Auto-generated method stub
        
    }
}
