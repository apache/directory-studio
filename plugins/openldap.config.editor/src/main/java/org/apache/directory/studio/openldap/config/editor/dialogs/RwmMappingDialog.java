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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.config.model.overlay.OlcRwmMapValue;
import org.apache.directory.studio.openldap.config.model.overlay.OlcRwmMapValueTypeEnum;


/**
 * T
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RwmMappingDialog extends Dialog
{
    /** The connection's attribute types */
    private List<String> connectionAttributeTypes;

    /** The connection's object classes */
    private List<String> connectionObjectClasses;

    /** The connection */
    private IBrowserConnection browserConnection;

    /** The value */
    private OlcRwmMapValue value;

    // UI widgets
    private Button okButton;
    private ComboViewer typeComboViewer;
    private ComboViewer localNameComboViewer;
    private ComboViewer foreignNameComboViewer;

    // Listeners

    private ModifyListener namesComboViewerListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            checkAndUpdateOkButtonEnableState();
        }
    };
    private ISelectionChangedListener typeComboViewerSelectionListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            OlcRwmMapValueTypeEnum selectedType = getSelectedType();

            // Backing up the combos text
            String localNameText = localNameComboViewer.getCombo().getText();
            String foreignNameText = foreignNameComboViewer.getCombo().getText();

            // Adding the correct suggestions to the viewers
            if ( OlcRwmMapValueTypeEnum.ATTRIBUTE.equals( selectedType ) )
            {
                localNameComboViewer.setInput( connectionAttributeTypes );
                foreignNameComboViewer.setInput( connectionAttributeTypes );
            }
            else if ( OlcRwmMapValueTypeEnum.OBJECTCLASS.equals( selectedType ) )
            {
                localNameComboViewer.setInput( connectionObjectClasses );
                foreignNameComboViewer.setInput( connectionObjectClasses );
            }

            // Restoring the combos text
            localNameComboViewer.getCombo().setText( localNameText );
            foreignNameComboViewer.getCombo().setText( foreignNameText );
        }
    };


    /**
     * Creates a new instance of ValueSortingValueDialog.
     * 
     * @param parentShell the parent shell
     * @param browserConnection the connection
     * @param value the value
     */
    public RwmMappingDialog( Shell parentShell, IBrowserConnection browserConnection, String value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        // Parsing the value
        try
        {
            this.value = OlcRwmMapValue.parse( value );

            if ( this.value == null )
            {
                this.value = new OlcRwmMapValue();
            }
        }
        catch ( ParseException e )
        {
            this.value = new OlcRwmMapValue();
        }

        initAttributeTypesAndObjectClassesLists();
    }


    /**
     * Creates a new instance of ValueSortingValueDialog.
     * 
     * @param parentShell the parent shell
     * @param browserConnection the connection
     */
    public RwmMappingDialog( Shell parentShell, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        this.value = new OlcRwmMapValue();

        initAttributeTypesAndObjectClassesLists();
    }


    /**
     * Initializes the lists of attribute types and object classes.
     */
    private void initAttributeTypesAndObjectClassesLists()
    {
        connectionAttributeTypes = new ArrayList<String>();
        connectionObjectClasses = new ArrayList<String>();

        if ( browserConnection != null )
        {
            // Attribute Types
            Collection<AttributeType> atds = browserConnection.getSchema().getAttributeTypeDescriptions();

            for ( AttributeType atd : atds )
            {
                for ( String name : atd.getNames() )
                {
                    connectionAttributeTypes.add( name );
                }
            }

            // Object Classes
            Collection<ObjectClass> ocds = browserConnection.getSchema().getObjectClassDescriptions();

            for ( ObjectClass ocd : ocds )
            {
                for ( String name : ocd.getNames() )
                {
                    connectionObjectClasses.add( name );
                }
            }

            // Creating a case insensitive comparator
            Comparator<String> ignoreCaseComparator = new Comparator<String>()
            {
                public int compare( String o1, String o2 )
                {
                    return o1.compareToIgnoreCase( o2 );
                }
            };

            // Sorting the lists
            Collections.sort( connectionAttributeTypes, ignoreCaseComparator );
            Collections.sort( connectionObjectClasses, ignoreCaseComparator );

            // Adding the '*' special name
            connectionAttributeTypes.add( 0, "*" );
            connectionObjectClasses.add( 0, "*" );
        }
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Mapping" );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        checkAndUpdateOkButtonEnableState();
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Type
        value.setType( getSelectedType() );

        // Local Name
        String localName = localNameComboViewer.getCombo().getText();

        if ( ( localName != null ) && ( !localName.isEmpty() ) )
        {
            value.setLocalName( localName );
        }
        else
        {
            value.setLocalName( null );
        }

        // Foreign Name
        String foreignName = foreignNameComboViewer.getCombo().getText();

        if ( ( foreignName != null ) && ( !foreignName.isEmpty() ) )
        {
            value.setForeignName( foreignName );
        }
        else
        {
            value.setForeignName( null );
        }

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        // Creating the dialog composites
        Composite dialogComposite = ( Composite ) super.createDialogArea( parent );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        //        gridData.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        dialogComposite.setLayoutData( gridData );
        Composite composite = BaseWidgetUtils.createColumnContainer( dialogComposite, 2, 1 );

        // Type
        BaseWidgetUtils.createLabel( composite, "Type:", 1 );
        typeComboViewer = new ComboViewer( composite );
        typeComboViewer.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
        typeComboViewer.setContentProvider( new ArrayContentProvider() );
        typeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof OlcRwmMapValueTypeEnum )
                {
                    OlcRwmMapValueTypeEnum type = ( OlcRwmMapValueTypeEnum ) element;

                    switch ( type )
                    {
                        case ATTRIBUTE:
                            return "Attribute Type";
                        case OBJECTCLASS:
                            return "Object Class";
                    }
                }

                return super.getText( element );
            }
        } );
        typeComboViewer.setInput( new OlcRwmMapValueTypeEnum[]
            {
                OlcRwmMapValueTypeEnum.ATTRIBUTE,
                OlcRwmMapValueTypeEnum.OBJECTCLASS
        } );

        // Local Name
        BaseWidgetUtils.createLabel( composite, "Local Name:", 1 );
        localNameComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        localNameComboViewer.getControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        localNameComboViewer.setContentProvider( new ArrayContentProvider() );

        // Foreign Name
        BaseWidgetUtils.createLabel( composite, "Foreign Name:", 1 );
        foreignNameComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        foreignNameComboViewer.getControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        foreignNameComboViewer.setContentProvider( new ArrayContentProvider() );

        initFromValue();

        addListeners();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Adds listeners to UI widgets.
     */
    private void addListeners()
    {
        typeComboViewer.addSelectionChangedListener( typeComboViewerSelectionListener );
        localNameComboViewer.getCombo().addModifyListener( namesComboViewerListener );
        foreignNameComboViewer.getCombo().addModifyListener( namesComboViewerListener );
    }


    /**
     * Inits the UI from the value.
     */
    private void initFromValue()
    {
        // Type
        OlcRwmMapValueTypeEnum type = value.getType();

        if ( type != null )
        {
            typeComboViewer.setSelection( new StructuredSelection( type ) );

            // Adding the correct suggestions to the viewers
            if ( OlcRwmMapValueTypeEnum.ATTRIBUTE.equals( type ) )
            {
                localNameComboViewer.setInput( connectionAttributeTypes );
                foreignNameComboViewer.setInput( connectionAttributeTypes );
            }
            else if ( OlcRwmMapValueTypeEnum.OBJECTCLASS.equals( type ) )
            {
                localNameComboViewer.setInput( connectionObjectClasses );
                foreignNameComboViewer.setInput( connectionObjectClasses );
            }
        }
        else
        {
            typeComboViewer.setSelection( new StructuredSelection( OlcRwmMapValueTypeEnum.ATTRIBUTE ) );

            // Adding the suggestions to the viewers
            localNameComboViewer.setInput( connectionAttributeTypes );
            foreignNameComboViewer.setInput( connectionAttributeTypes );
        }

        // Local Name
        String localName = value.getLocalName();

        if ( localName != null )
        {
            localNameComboViewer.getCombo().setText( localName );
        }
        else
        {
            localNameComboViewer.getCombo().setText( "" );
        }

        // Local Name
        String foreignName = value.getForeignName();

        if ( foreignName != null )
        {
            foreignNameComboViewer.getCombo().setText( foreignName );
        }
        else
        {
            foreignNameComboViewer.getCombo().setText( "" );
        }
    }


    /**
     * Gets the selected sort method.
     *
     * @return the selected sort method
     */
    private OlcRwmMapValueTypeEnum getSelectedType()
    {
        StructuredSelection selection = ( StructuredSelection ) typeComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            return ( OlcRwmMapValueTypeEnum ) selection.getFirstElement();
        }

        return null;
    }


    /**
     * Checks and updates the OK button 'enable' state.
     */
    private void checkAndUpdateOkButtonEnableState()
    {
        boolean enableOkButton = true;

        do
        {
            // Type
            if ( getSelectedType() == null )
            {
                enableOkButton = false;
                break;
            }

            // Local Name can be omitted, so we don't check it

            // Foreign Name can't be omitted
            String foreignName = foreignNameComboViewer.getCombo().getText();

            if ( ( foreignName == null ) || ( foreignName.isEmpty() ) )
            {
                enableOkButton = false;
                break;
            }
        }
        while ( false );

        okButton.setEnabled( enableOkButton );
    }


    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value.toString();
    }
}
