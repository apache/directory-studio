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
package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.apache.directory.ldapstudio.apacheds.configuration.model.Partition;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
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
    /** The Managed Form */
    private IManagedForm mform;

    /** The input Partition */
    private Partition input;

    // UI fields
    private Text nameText;
    private Text cacheSizeText;
    private Text suffixText;
    private Button enableOptimizerCheckbox;
    private Button synchOnWriteCheckbox;
    private Table indexedAttributesTable;
    private Button indexedAttributeAddButton;
    private Button indexedAttributeEditButton;
    private Button indexedAttributeDeleteButton;
    private Table contextEntryTable;
    private Button contextEntryAddButton;
    private Button contextEntryEditButton;
    private Button contextEntryDeleteButton;


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

        // Name
        toolkit.createLabel( client, "Name:" );
        nameText = toolkit.createText( client, "" );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Cache Size
        toolkit.createLabel( client, "Cache Size:" );
        cacheSizeText = toolkit.createText( client, "" );
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Suffix
        toolkit.createLabel( client, "Suffix:" );
        suffixText = toolkit.createText( client, "" );
        suffixText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Enable Optimizer
        enableOptimizerCheckbox = toolkit.createButton( client, "Enable optimizer", SWT.CHECK );
        enableOptimizerCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Synchronisation On Write
        synchOnWriteCheckbox = toolkit.createButton( client, " Synchronization on write", SWT.CHECK );
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
        gd.heightHint = 80;
        contextEntryTable.setLayoutData( gd );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        contextEntryAddButton = toolkit.createButton( client, "Add...", SWT.PUSH );
        contextEntryAddButton.setLayoutData( buttonsGD );

        contextEntryEditButton = toolkit.createButton( client, "Edit...", SWT.PUSH );
        contextEntryEditButton.setLayoutData( buttonsGD );

        contextEntryDeleteButton = toolkit.createButton( client, "Delete", SWT.PUSH );
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

        indexedAttributesTable = toolkit.createTable( indexedAttributesClient, SWT.NONE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 3 );
        gd.heightHint = 80;
        indexedAttributesTable.setLayoutData( gd );

        GridData buttonsGD = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        buttonsGD.widthHint = IDialogConstants.BUTTON_WIDTH;

        indexedAttributeAddButton = toolkit.createButton( indexedAttributesClient, "Add...", SWT.PUSH );
        indexedAttributeAddButton.setLayoutData( buttonsGD );

        indexedAttributeEditButton = toolkit.createButton( indexedAttributesClient, "Edit...", SWT.PUSH );
        indexedAttributeEditButton.setLayoutData( buttonsGD );

        indexedAttributeDeleteButton = toolkit.createButton( indexedAttributesClient, "Delete", SWT.PUSH );
        indexedAttributeDeleteButton.setLayoutData( buttonsGD );
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
        input.setName( nameText.getText() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose()
    {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale()
    {
        // TODO Auto-generated method stub
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh()
    {
        nameText.setText( input.getName() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {
        nameText.setFocus();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
