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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.MutableAttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.AttributeTypeSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;


/**
 * This class represents the Mandatory Attribute Types WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user specify the mandatory attribute types for the object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewObjectClassMandatoryAttributesPage extends WizardPage
{
    /** The mandatory attribute types list */
    private List<MutableAttributeTypeImpl> mandatoryAttributeTypesList;

    // UI Fields
    private TableViewer mandatoryAttributeTypesTableViewer;
    private Button mandatoryAttributeTypesAddButton;
    private Button mandatoryAttributeTypesRemoveButton;


    /**
     * Creates a new instance of NewObjectClassMandatoryAttributesPage.
     */
    protected NewObjectClassMandatoryAttributesPage()
    {
        super( "NewObjectClassMandatoryAttributesPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewObjectClassMandatoryAttributesPage.MandatoryAttributeTypes" ) ); //$NON-NLS-1$
        setDescription( Messages
            .getString( "NewObjectClassMandatoryAttributesPage.SpecifiyMandatoryAttributeTypeForObjectClass" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );
        mandatoryAttributeTypesList = new ArrayList<MutableAttributeTypeImpl>();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Mandatory Attribute Types Group
        Group mandatoryAttributeTypesGroup = new Group( composite, SWT.NONE );
        mandatoryAttributeTypesGroup.setText( Messages
            .getString( "NewObjectClassMandatoryAttributesPage.MandatoryAttributeTypes" ) ); //$NON-NLS-1$
        mandatoryAttributeTypesGroup.setLayout( new GridLayout( 2, false ) );
        mandatoryAttributeTypesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Mandatory Attribute Types
        Table mandatoryAttributeTypesTable = new Table( mandatoryAttributeTypesGroup, SWT.BORDER );
        GridData mandatoryAttributeTypesTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        mandatoryAttributeTypesTableGridData.heightHint = 100;
        mandatoryAttributeTypesTable.setLayoutData( mandatoryAttributeTypesTableGridData );
        mandatoryAttributeTypesTableViewer = new TableViewer( mandatoryAttributeTypesTable );
        mandatoryAttributeTypesTableViewer.setContentProvider( new ArrayContentProvider() );
        mandatoryAttributeTypesTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                if ( element instanceof MutableAttributeTypeImpl )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
                }

                // Default
                return super.getImage( element );
            }


            public String getText( Object element )
            {
                if ( element instanceof MutableAttributeTypeImpl )
                {
                    MutableAttributeTypeImpl at = ( MutableAttributeTypeImpl ) element;

                    List<String> names = at.getNames();
                    if ( ( names != null ) && ( names.size() > 0 ) )
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewObjectClassMandatoryAttributesPage.AliasOID" ), new String[] { ViewUtils.concateAliases( names ), at.getOid() } ); //$NON-NLS-1$
                    }
                    else
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewObjectClassMandatoryAttributesPage.NoneOID" ), new String[] { at.getOid() } ); //$NON-NLS-1$
                    }
                }
                // Default
                return super.getText( element );
            }
        } );
        mandatoryAttributeTypesTableViewer.setInput( mandatoryAttributeTypesList );
        mandatoryAttributeTypesTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                mandatoryAttributeTypesRemoveButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        mandatoryAttributeTypesAddButton = new Button( mandatoryAttributeTypesGroup, SWT.PUSH );
        mandatoryAttributeTypesAddButton.setText( Messages.getString( "NewObjectClassMandatoryAttributesPage.Add" ) ); //$NON-NLS-1$
        mandatoryAttributeTypesAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        mandatoryAttributeTypesAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                addMandatoryAttributeType();
            }
        } );
        mandatoryAttributeTypesRemoveButton = new Button( mandatoryAttributeTypesGroup, SWT.PUSH );
        mandatoryAttributeTypesRemoveButton.setText( Messages
            .getString( "NewObjectClassMandatoryAttributesPage.Remove" ) ); //$NON-NLS-1$
        mandatoryAttributeTypesRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        mandatoryAttributeTypesRemoveButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                removeMandatoryAttributeType();
            }
        } );
        mandatoryAttributeTypesRemoveButton.setEnabled( false );

        setControl( composite );
    }


    /**
     * This method is called when the "Add" button of the mandatory 
     * attribute types table is selected.
     */
    private void addMandatoryAttributeType()
    {
        AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
        List<MutableAttributeTypeImpl> hiddenAttributes = new ArrayList<MutableAttributeTypeImpl>();
        hiddenAttributes.addAll( mandatoryAttributeTypesList );
        dialog.setHiddenAttributeTypes( hiddenAttributes );
        if ( dialog.open() == Dialog.OK )
        {
            mandatoryAttributeTypesList.add( dialog.getSelectedAttributeType() );
            updateMandatoryAttributeTypesTableTable();
        }
    }


    /**
     * This method is called when the "Remove" button of the mandatory 
     * attribute types table is selected.
     */
    private void removeMandatoryAttributeType()
    {
        StructuredSelection selection = ( StructuredSelection ) mandatoryAttributeTypesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            mandatoryAttributeTypesList.remove( selection.getFirstElement() );
            updateMandatoryAttributeTypesTableTable();
        }
    }


    /**
     * Updates the mandatory attribute types table.
     */
    private void updateMandatoryAttributeTypesTableTable()
    {
        Collections.sort( mandatoryAttributeTypesList, new Comparator<MutableAttributeTypeImpl>()
        {
            public int compare( MutableAttributeTypeImpl o1, MutableAttributeTypeImpl o2 )
            {
                List<String> at1Names = o1.getNames();
                List<String> at2Names = o2.getNames();

                if ( ( at1Names != null ) && ( at2Names != null ) && ( at1Names.size() > 0 ) && ( at2Names.size() > 0 ) )
                {
                    return at1Names.get( 0 ).compareToIgnoreCase( at2Names.get( 0 ) );
                }

                // Default
                return 0;
            }
        } );

        mandatoryAttributeTypesTableViewer.refresh();
    }


    /**
     * Gets the mandatory attribute types.
     *
     * @return
     *      the mandatory attributes types
     */
    public List<MutableAttributeTypeImpl> getMandatoryAttributeTypes()
    {
        return mandatoryAttributeTypesList;
    }


    /**
     * Gets the names of the mandatory attribute types.
     *
     * @return
     *      the names of the mandatory attributes types
     */
    public List<String> getMandatoryAttributeTypesNames()
    {
        List<String> names = new ArrayList<String>();
        for ( MutableAttributeTypeImpl at : mandatoryAttributeTypesList )
        {
            names.add( at.getName() );
        }
        return names;
    }
}
