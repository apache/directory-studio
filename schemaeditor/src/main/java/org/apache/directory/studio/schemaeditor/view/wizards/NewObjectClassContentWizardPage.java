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

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.ObjectClassSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This class represents the Content WizardPage of the ObjectClassWizard.
 * <p>
 * It is used to let the user enter content information about the
 * attribute type he wants to create (superiors, class type, and properties).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassContentWizardPage extends WizardPage
{
    /** The superiors object classes */
    private List<ObjectClassImpl> superiorsList;

    /** The type of the object class */
    private ObjectClassTypeEnum type = ObjectClassTypeEnum.STRUCTURAL;

    // UI Fields
    private TableViewer superiorsTableViewer;
    private Button superiorsAddButton;
    private Button superiorsRemoveButton;
    private Button structuralRadio;
    private Button abstractRadio;
    private Button auxiliaryRadio;
    private Button obsoleteCheckbox;


    /**
     * Creates a new instance of NewAttributeTypeContentWizardPage.
     */
    protected NewObjectClassContentWizardPage()
    {
        super( "NewObjectClassContentWizardPage" );
        setTitle( "Object Class Content" );
        setDescription( "Please enter the superiors, class type  and properties for the object class." );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );
        superiorsList = new ArrayList<ObjectClassImpl>();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Superiors
        Group superiorsGroup = new Group( composite, SWT.NONE );
        superiorsGroup.setText( "Superiors" );
        superiorsGroup.setLayout( new GridLayout( 2, false ) );
        superiorsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Superiors
        Table superiorsTable = new Table( superiorsGroup, SWT.BORDER );
        GridData superiorsTableGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        superiorsTableGridData.heightHint = 100;
        superiorsTable.setLayoutData( superiorsTableGridData );
        superiorsTableViewer = new TableViewer( superiorsTable );
        superiorsTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                if ( element instanceof ObjectClassImpl )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS );
                }

                // Default
                return super.getImage( element );
            }


            public String getText( Object element )
            {
                if ( element instanceof ObjectClassImpl )
                {
                    ObjectClassImpl oc = ( ObjectClassImpl ) element;

                    String[] names = oc.getNames();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        return ViewUtils.concateAliases( names ) + "  -  (" + oc.getOid() + ")";
                    }
                    else
                    {
                        return "(None)  -  (" + oc.getOid() + ")";
                    }
                }
                // Default
                return super.getText( element );
            }
        } );
        superiorsTableViewer.setContentProvider( new ArrayContentProvider() );
        superiorsTableViewer.setInput( superiorsList );
        superiorsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                superiorsRemoveButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        superiorsAddButton = new Button( superiorsGroup, SWT.PUSH );
        superiorsAddButton.setText( "Add..." );
        superiorsAddButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        superiorsAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                addSuperiorObjectClass();
            }
        } );
        superiorsRemoveButton = new Button( superiorsGroup, SWT.PUSH );
        superiorsRemoveButton.setText( "Remove" );
        superiorsRemoveButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        superiorsRemoveButton.setEnabled( false );
        superiorsRemoveButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                removeSuperiorObjectClass();
            }
        } );

        // Class Type Group
        Group classTypeGroup = new Group( composite, SWT.NONE );
        classTypeGroup.setText( "Class Type" );
        classTypeGroup.setLayout( new GridLayout( 5, false ) );
        classTypeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Class Type
        Label classTypeLable = new Label( classTypeGroup, SWT.NONE );
        classTypeLable.setText( "Class Type:" );
        new Label( classTypeGroup, SWT.NONE ).setText( "          " );
        structuralRadio = new Button( classTypeGroup, SWT.RADIO );
        structuralRadio.setText( "Structural" );
        GridData structuralRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        structuralRadioGridData.widthHint = 115;
        structuralRadio.setLayoutData( structuralRadioGridData );
        structuralRadio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                type = ObjectClassTypeEnum.STRUCTURAL;
            }
        } );
        structuralRadio.setSelection( true );
        abstractRadio = new Button( classTypeGroup, SWT.RADIO );
        abstractRadio.setText( "Abstract" );
        GridData abstractRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        abstractRadioGridData.widthHint = 115;
        abstractRadio.setLayoutData( structuralRadioGridData );
        abstractRadio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                type = ObjectClassTypeEnum.ABSTRACT;
            }
        } );
        auxiliaryRadio = new Button( classTypeGroup, SWT.RADIO );
        auxiliaryRadio.setText( "Auxiliary" );
        GridData auxiliaryRadioGridData = new GridData( SWT.LEFT, SWT.NONE, false, false );
        auxiliaryRadioGridData.widthHint = 115;
        auxiliaryRadio.setLayoutData( structuralRadioGridData );
        auxiliaryRadio.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                type = ObjectClassTypeEnum.AUXILIARY;
            }
        } );

        // Properties Group
        Group propertiesGroup = new Group( composite, SWT.NONE );
        propertiesGroup.setText( "Properties" );
        propertiesGroup.setLayout( new GridLayout() );
        propertiesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Obsolete
        obsoleteCheckbox = new Button( propertiesGroup, SWT.CHECK );
        obsoleteCheckbox.setText( "Obsolete" );

        setControl( composite );
    }


    /**
     * This method is called when the "Add" button of the superiors 
     * table is selected.
     */
    private void addSuperiorObjectClass()
    {
        ObjectClassSelectionDialog dialog = new ObjectClassSelectionDialog();
        dialog.setHiddenObjectClasses( superiorsList );
        if ( dialog.open() == Dialog.OK )
        {
            superiorsList.add( dialog.getSelectedObjectClass() );
            updateSuperiorsTable();
        }
    }


    /**
     * This method is called when the "Remove" button of the superiors 
     * table is selected.
     */
    private void removeSuperiorObjectClass()
    {
        StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            superiorsList.remove( selection.getFirstElement() );
            updateSuperiorsTable();
        }
    }


    /**
     * Updates the superiors table
     */
    private void updateSuperiorsTable()
    {
        Collections.sort( superiorsList, new Comparator<ObjectClassImpl>()
        {
            public int compare( ObjectClassImpl o1, ObjectClassImpl o2 )
            {
                String[] at1Names = o1.getNames();
                String[] at2Names = o2.getNames();

                if ( ( at1Names != null ) && ( at2Names != null ) && ( at1Names.length > 0 ) && ( at2Names.length > 0 ) )
                {
                    return at1Names[0].compareToIgnoreCase( at2Names[0] );
                }

                // Default
                return 0;
            }
        } );

        superiorsTableViewer.refresh();
    }


    /**
     * Gets the value of the superiors.
     *
     * @return
     *      the value of the superiors
     */
    public String[] getSuperiorsNameValue()
    {
        List<String> names = new ArrayList<String>();
        for ( ObjectClassImpl oc : superiorsList )
        {
            String[] aliases = oc.getNames();

            if ( ( aliases != null ) && ( aliases.length > 0 ) )
            {
                names.add( aliases[0] );
            }
            else
            {
                names.add( oc.getOid() );
            }
        }

        return names.toArray( new String[0] );
    }


    /**
     * Gets the class type value.
     *
     * @return
     *      the class type value
     */
    public ObjectClassTypeEnum getClassTypeValue()
    {
        return type;
    }


    /**
     * Gets the 'Obsolete' value.
     *
     * @return
     *      the 'Obsolete' value
     */
    public boolean getObsoleteValue()
    {
        return obsoleteCheckbox.getSelection();
    }
}
