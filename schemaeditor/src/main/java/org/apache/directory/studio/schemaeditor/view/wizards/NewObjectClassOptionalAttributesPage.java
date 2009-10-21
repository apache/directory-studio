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

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
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
 * This class represents the Optional Attribute Types WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user specify the optional attribute types for the object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassOptionalAttributesPage extends WizardPage
{
    /** The optional attribute types list */
    private List<AttributeTypeImpl> optionalAttributeTypesList;

    // UI Fields
    private TableViewer optionalAttributeTypesTableViewer;
    private Button optionalAttributeTypesAddButton;
    private Button optionalAttributeTypesRemoveButton;


    /**
     * Creates a new instance of NewObjectClassOptionalAttributesPage.
     */
    protected NewObjectClassOptionalAttributesPage()
    {
        super( "NewObjectClassOptionalAttributesPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewObjectClassOptionalAttributesPage.OptionalAttributeTypes" ) ); //$NON-NLS-1$
        setDescription( Messages
            .getString( "NewObjectClassOptionalAttributesPage.SpecifiyOptionalAttributeTypesForObjectClass" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );
        optionalAttributeTypesList = new ArrayList<AttributeTypeImpl>();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Optional Attribute Types Group
        Group optionalAttributeTypesGroup = new Group( composite, SWT.NONE );
        optionalAttributeTypesGroup.setText( Messages
            .getString( "NewObjectClassOptionalAttributesPage.OptionalAttributeTypes" ) ); //$NON-NLS-1$
        optionalAttributeTypesGroup.setLayout( new GridLayout( 2, false ) );
        optionalAttributeTypesGroup.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Optional Attribute Types
        Table optionalAttributeTypesTable = new Table( optionalAttributeTypesGroup, SWT.BORDER );
        GridData optionalAttributeTypesTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        optionalAttributeTypesTableGridData.heightHint = 100;
        optionalAttributeTypesTable.setLayoutData( optionalAttributeTypesTableGridData );
        optionalAttributeTypesTableViewer = new TableViewer( optionalAttributeTypesTable );
        optionalAttributeTypesTableViewer.setContentProvider( new ArrayContentProvider() );
        optionalAttributeTypesTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                if ( element instanceof AttributeTypeImpl )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
                }

                // Default
                return super.getImage( element );
            }


            public String getText( Object element )
            {
                if ( element instanceof AttributeTypeImpl )
                {
                    AttributeTypeImpl at = ( AttributeTypeImpl ) element;

                    String[] names = at.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewObjectClassOptionalAttributesPage.AliasOID" ), new String[] { ViewUtils.concateAliases( names ), at.getOid() } ); //$NON-NLS-1$
                    }
                    else
                    {
                        return NLS
                            .bind(
                                Messages.getString( "NewObjectClassOptionalAttributesPage.NoneOID" ), new String[] { at.getOid() } ); //$NON-NLS-1$
                    }
                }
                // Default
                return super.getText( element );
            }
        } );
        optionalAttributeTypesTableViewer.setInput( optionalAttributeTypesList );
        optionalAttributeTypesTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                optionalAttributeTypesRemoveButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        optionalAttributeTypesAddButton = new Button( optionalAttributeTypesGroup, SWT.PUSH );
        optionalAttributeTypesAddButton.setText( Messages.getString( "NewObjectClassOptionalAttributesPage.Add" ) ); //$NON-NLS-1$
        optionalAttributeTypesAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        optionalAttributeTypesAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                addOptionalAttributeType();
            }
        } );
        optionalAttributeTypesRemoveButton = new Button( optionalAttributeTypesGroup, SWT.PUSH );
        optionalAttributeTypesRemoveButton
            .setText( Messages.getString( "NewObjectClassOptionalAttributesPage.Remove" ) ); //$NON-NLS-1$
        optionalAttributeTypesRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        optionalAttributeTypesRemoveButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                removeOptionalAttributeType();
            }
        } );
        optionalAttributeTypesRemoveButton.setEnabled( false );

        setControl( composite );
    }


    /**
     * This method is called when the "Add" button of the optional 
     * attribute types table is selected.
     */
    private void addOptionalAttributeType()
    {
        AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
        List<AttributeTypeImpl> hiddenAttributes = new ArrayList<AttributeTypeImpl>();
        hiddenAttributes.addAll( optionalAttributeTypesList );
        dialog.setHiddenAttributeTypes( hiddenAttributes );
        if ( dialog.open() == Dialog.OK )
        {
            optionalAttributeTypesList.add( dialog.getSelectedAttributeType() );
            updateOptionalAttributeTypesTableTable();
        }
    }


    /**
     * This method is called when the "Remove" button of the optional 
     * attribute types table is selected.
     */
    private void removeOptionalAttributeType()
    {
        StructuredSelection selection = ( StructuredSelection ) optionalAttributeTypesTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            optionalAttributeTypesList.remove( selection.getFirstElement() );
            updateOptionalAttributeTypesTableTable();
        }
    }


    /**
     * Updates the optional attribute types table.
     */
    private void updateOptionalAttributeTypesTableTable()
    {
        Collections.sort( optionalAttributeTypesList, new Comparator<AttributeTypeImpl>()
        {
            public int compare( AttributeTypeImpl o1, AttributeTypeImpl o2 )
            {
                String[] at1Names = o1.getNamesRef();
                String[] at2Names = o2.getNamesRef();

                if ( ( at1Names != null ) && ( at2Names != null ) && ( at1Names.length > 0 ) && ( at2Names.length > 0 ) )
                {
                    return at1Names[0].compareToIgnoreCase( at2Names[0] );
                }

                // Default
                return 0;
            }
        } );

        optionalAttributeTypesTableViewer.refresh();
    }


    /**
     * Gets the optional attribute types.
     *
     * @return
     *      the optional attributes types
     */
    public List<AttributeTypeImpl> getOptionalAttributeTypes()
    {
        return optionalAttributeTypesList;
    }


    /**
     * Gets the names of the optional attribute types.
     *
     * @return
     *      the names of the optional attributes types
     */
    public String[] getOptionalAttributeTypesNames()
    {
        List<String> names = new ArrayList<String>();
        for ( AttributeTypeImpl at : optionalAttributeTypesList )
        {
            names.add( at.getName() );
        }
        return names.toArray( new String[0] );
    }
}
