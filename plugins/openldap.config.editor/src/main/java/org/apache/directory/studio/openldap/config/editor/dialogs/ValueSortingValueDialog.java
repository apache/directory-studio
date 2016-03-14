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

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
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
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.model.overlay.OlcValSortMethodEnum;
import org.apache.directory.studio.openldap.config.model.overlay.OlcValSortValue;


/**
 * The ValueSortingValueDialog is used to edit a value from the Value Sorting overlay configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueSortingValueDialog extends Dialog
{
    /** The 'weighted' combo viewer option */
    private static final String WEIGHTED_OPTION = "Weighted";

    /** The '<none>' combo viewer option */
    private static final String NONE_OPTION = "<none>";

    /** The connection's attribute types */
    private List<String> connectionAttributeTypes;

    /** The connection */
    private IBrowserConnection browserConnection;

    /** The value */
    private OlcValSortValue value;

    // UI widgets
    private Button okButton;
    private ComboViewer attributeComboViewer;
    private EntryWidget baseDnEntryWidget;
    private ComboViewer sortMethodComboViewer;
    private ComboViewer secondarySortMethodComboViewer;

    // Listeners

    private ModifyListener attributeComboViewerListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            checkAndUpdateOkButtonEnableState();
        }
    };
    private WidgetModifyListener baseDnEntryWidgetListener = new WidgetModifyListener()
    {

        public void widgetModified( WidgetModifyEvent event )
        {
            checkAndUpdateOkButtonEnableState();
        }
    };
    private ISelectionChangedListener sortMethodComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedSortMethod = getSelectedSortMethod();

            if ( WEIGHTED_OPTION.equals( selectedSortMethod ) )
            {
                secondarySortMethodComboViewer.getCombo().setEnabled( true );
            }
            else
            {
                secondarySortMethodComboViewer.getCombo().setEnabled( false );
                secondarySortMethodComboViewer.setSelection( new StructuredSelection( NONE_OPTION ) );
            }
        }
    };


    /**
     * Creates a new instance of ValueSortingValueDialog.
     * 
     * @param parentShell the parent shell
     * @param browserConnection the connection
     * @param value the value
     */
    public ValueSortingValueDialog( Shell parentShell, IBrowserConnection browserConnection, String value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        // Parsing the value
        try
        {
            this.value = OlcValSortValue.parse( value );

            if ( this.value == null )
            {
                this.value = new OlcValSortValue();
            }
        }
        catch ( ParseException e )
        {
            this.value = new OlcValSortValue();
        }

        initAttributeTypesList();
    }


    /**
     * Creates a new instance of ValueSortingValueDialog.
     * 
     * @param parentShell the parent shell
     * @param browserConnection the connection
     */
    public ValueSortingValueDialog( Shell parentShell, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        this.value = new OlcValSortValue();

        initAttributeTypesList();
    }


    /**
     * Initializes the list of attribute types.
     */
    private void initAttributeTypesList()
    {
        connectionAttributeTypes = new ArrayList<String>();

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

            // Sorting the list
            Collections.sort( connectionAttributeTypes, new Comparator<String>()
            {
                public int compare( String o1, String o2 )
                {
                    return o1.compareToIgnoreCase( o2 );
                }
            } );
        }
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Value Sort" );
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
        // Attribute
        value.setAttribute( attributeComboViewer.getCombo().getText() );

        // Base DN
        value.setBaseDn( baseDnEntryWidget.getDn() );

        // Sort Method
        Object selectedSortMethod = getSelectedSortMethod();

        if ( WEIGHTED_OPTION.equals( selectedSortMethod ) )
        {
            value.setWeighted( true );

            // Secondary Sort Method
            Object selectedSecondarySortMethod = getSelectedSecondarySortMethod();

            if ( NONE_OPTION.equals( selectedSecondarySortMethod ) )
            {
                value.setSortMethod( null );
            }
            else
            {
                value.setSortMethod( ( OlcValSortMethodEnum ) selectedSecondarySortMethod );
            }
        }
        else
        {
            value.setSortMethod( ( OlcValSortMethodEnum ) selectedSortMethod );
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

        // Attribute
        BaseWidgetUtils.createLabel( composite, "Attribute:", 1 );
        attributeComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        attributeComboViewer.getControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        attributeComboViewer.setContentProvider( new ArrayContentProvider() );
        attributeComboViewer.setInput( connectionAttributeTypes );

        // Base DN
        BaseWidgetUtils.createLabel( composite, "Base DN:", 1 );
        baseDnEntryWidget = new EntryWidget( browserConnection );
        baseDnEntryWidget.createWidget( composite );
        baseDnEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Sort Method
        BaseWidgetUtils.createLabel( composite, "Sort Method:", 1 );
        sortMethodComboViewer = new ComboViewer( composite );
        sortMethodComboViewer.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
        sortMethodComboViewer.setContentProvider( new ArrayContentProvider() );
        sortMethodComboViewer.setLabelProvider( new OlcValSortMethodEnumLabelProvider() );
        sortMethodComboViewer.setInput( new Object[]
            {
                OlcValSortMethodEnum.ALPHA_ASCEND,
                OlcValSortMethodEnum.ALPHA_DESCEND,
                OlcValSortMethodEnum.NUMERIC_ASCEND,
                OlcValSortMethodEnum.NUMERIC_DESCEND,
                WEIGHTED_OPTION
        } );

        // Secondary Sort Method
        BaseWidgetUtils.createLabel( composite, "Secondary Sort Method:", 1 );
        secondarySortMethodComboViewer = new ComboViewer( composite );
        secondarySortMethodComboViewer.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
        secondarySortMethodComboViewer.setContentProvider( new ArrayContentProvider() );
        secondarySortMethodComboViewer.setLabelProvider( new OlcValSortMethodEnumLabelProvider() );
        secondarySortMethodComboViewer.setInput( new Object[]
            {
                NONE_OPTION,
                OlcValSortMethodEnum.ALPHA_ASCEND,
                OlcValSortMethodEnum.ALPHA_DESCEND,
                OlcValSortMethodEnum.NUMERIC_ASCEND,
                OlcValSortMethodEnum.NUMERIC_DESCEND
        } );

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
        attributeComboViewer.getCombo().addModifyListener( attributeComboViewerListener );
        baseDnEntryWidget.addWidgetModifyListener( baseDnEntryWidgetListener );
        sortMethodComboViewer.addSelectionChangedListener( sortMethodComboViewerListener );
    }


    /**
     * Inits the UI from the value.
     */
    private void initFromValue()
    {
        // Attribute
        String attribute = value.getAttribute();

        if ( attribute != null )
        {
            attributeComboViewer.getCombo().setText( attribute );
        }
        else
        {
            attributeComboViewer.getCombo().setText( "" );
        }

        // Base DN
        Dn baseDn = value.getBaseDn();

        if ( baseDn != null )
        {
            baseDnEntryWidget.setInput( baseDn );
        }
        else
        {
            baseDnEntryWidget.setInput( Dn.EMPTY_DN );
        }

        // Sort Method
        if ( value.isWeighted() )
        {
            sortMethodComboViewer.setSelection( new StructuredSelection( WEIGHTED_OPTION ) );
        }
        else
        {
            OlcValSortMethodEnum secondarySortMethod = value.getSortMethod();

            if ( secondarySortMethod != null )
            {
                sortMethodComboViewer.setSelection( new StructuredSelection( secondarySortMethod ) );
            }
            else
            {
                sortMethodComboViewer
                    .setSelection( new StructuredSelection( OlcValSortMethodEnum.ALPHA_ASCEND ) );
            }
        }

        // Secondary Sort Method
        if ( value.isWeighted() )
        {
            OlcValSortMethodEnum secondarySortMethod = value.getSortMethod();

            if ( secondarySortMethod != null )
            {
                secondarySortMethodComboViewer.setSelection( new StructuredSelection( secondarySortMethod ) );
            }
            else
            {
                secondarySortMethodComboViewer.setSelection( new StructuredSelection( NONE_OPTION ) );
            }
        }
        else
        {
            secondarySortMethodComboViewer.setSelection( new StructuredSelection( NONE_OPTION ) );
            secondarySortMethodComboViewer.getControl().setEnabled( false );
        }
    }


    /**
     * Gets the selected sort method.
     *
     * @return the selected sort method
     */
    private Object getSelectedSortMethod()
    {
        StructuredSelection selection = ( StructuredSelection ) sortMethodComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            return selection.getFirstElement();
        }

        return null;
    }


    /**
     * Gets the selected secondary sort method.
     *
     * @return the selected secondary sort method
     */
    private Object getSelectedSecondarySortMethod()
    {
        StructuredSelection selection = ( StructuredSelection ) secondarySortMethodComboViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            return selection.getFirstElement();
        }

        return null;
    }


    /**
     * Checks and updates the OK button 'enable' state.
     */
    private void checkAndUpdateOkButtonEnableState()
    {
        boolean enableOkButton = true;

        // Attribute
        String attribute = attributeComboViewer.getCombo().getText();

        if ( ( attribute == null ) || ( attribute.isEmpty() ) )
        {
            enableOkButton = false;
        }

        // Base DN
        if ( enableOkButton )
        {
            Dn baseDn = baseDnEntryWidget.getDn();

            if ( ( baseDn == null ) || ( Dn.EMPTY_DN.equals( baseDn ) ) )
            {
                enableOkButton = false;
            }
        }

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

    /**
     * This class implement a {@link LabelProvider} for {@link OlcValSortMethodEnum} objects.
     */
    private class OlcValSortMethodEnumLabelProvider extends LabelProvider
    {
        public String getText( Object element )
        {
            if ( element instanceof OlcValSortMethodEnum )
            {
                OlcValSortMethodEnum sortMethod = ( OlcValSortMethodEnum ) element;

                switch ( sortMethod )
                {
                    case ALPHA_ASCEND:
                        return "Alpha Ascendant";
                    case ALPHA_DESCEND:
                        return "Alpha Descendant";
                    case NUMERIC_ASCEND:
                        return "Numeric Ascendant";
                    case NUMERIC_DESCEND:
                        return "Numeric Descendant";
                }
            }

            return super.getText( element );
        }
    }
}
