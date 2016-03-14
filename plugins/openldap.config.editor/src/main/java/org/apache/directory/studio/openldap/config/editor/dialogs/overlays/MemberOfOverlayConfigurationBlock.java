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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.model.overlay.OlcMemberOf;
import org.apache.directory.studio.openldap.config.model.overlay.OlcMemberOfDanglingReferenceBehaviorEnum;


/**
 * This class implements a block for the configuration of the Member Of overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MemberOfOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcMemberOf>
{
    /** The connection's attribute types */
    private List<String> connectionAttributeTypes;

    /** The connection's objectClasses */
    private List<String> connectionObjectClasses;

    /** The list of result codes */
    private List<ResultCodeEnum> resultCodes;

    // UI widgets
    private ComboViewer groupObjectClassComboViewer;
    private ComboViewer groupAttributeTypeComboViewer;
    private ComboViewer entryAttributeTypeComboViewer;
    private EntryWidget modifierNameEntryWidget;
    private ComboViewer danglingReferenceBehaviorComboViewer;
    private ComboViewer danglingReferenceErrorCodeComboViewer;
    private Button maintianReferentialIntegrityCheckbox;


    /**
     * Creates a new instance of MemberOfOverlayConfigurationBlock.
     *
     * @param dialog the dialog
     * @param browserConnection the connection
     */
    public MemberOfOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection )
    {
        super( dialog, browserConnection );
        setOverlay( new OlcMemberOf() );

        init();
    }


    /**
     * Creates a new instance of MemberOfOverlayConfigurationBlock.
     *
     * @param dialog the dialog
     * @param browserConnection the connection
     * @param overlay the overlay
     */
    public MemberOfOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection,
        OlcMemberOf overlay )
    {
        super( dialog, browserConnection );

        if ( overlay == null )
        {
            overlay = new OlcMemberOf();
        }

        setOverlay( overlay );

        init();
    }


    /**
     * Initializes the list of attribute types and object class.
     */
    private void init()
    {
        initAttributeTypesAndObjectClassesLists();
        initResultCodesList();
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
        }
    }


    /**
     * Initializes the list of result codes.
     */
    private void initResultCodesList()
    {
        // Initializing the list
        resultCodes = new ArrayList<ResultCodeEnum>();

        // Adding all result codes to the list
        resultCodes.add( ResultCodeEnum.SUCCESS );
        resultCodes.add( ResultCodeEnum.PARTIAL_RESULTS );
        resultCodes.add( ResultCodeEnum.COMPARE_FALSE );
        resultCodes.add( ResultCodeEnum.COMPARE_TRUE );
        resultCodes.add( ResultCodeEnum.REFERRAL );
        resultCodes.add( ResultCodeEnum.SASL_BIND_IN_PROGRESS );
        resultCodes.add( ResultCodeEnum.AUTH_METHOD_NOT_SUPPORTED );
        resultCodes.add( ResultCodeEnum.STRONG_AUTH_REQUIRED );
        resultCodes.add( ResultCodeEnum.CONFIDENTIALITY_REQUIRED );
        resultCodes.add( ResultCodeEnum.ALIAS_DEREFERENCING_PROBLEM );
        resultCodes.add( ResultCodeEnum.INAPPROPRIATE_AUTHENTICATION );
        resultCodes.add( ResultCodeEnum.INVALID_CREDENTIALS );
        resultCodes.add( ResultCodeEnum.INSUFFICIENT_ACCESS_RIGHTS );
        resultCodes.add( ResultCodeEnum.OPERATIONS_ERROR );
        resultCodes.add( ResultCodeEnum.PROTOCOL_ERROR );
        resultCodes.add( ResultCodeEnum.TIME_LIMIT_EXCEEDED );
        resultCodes.add( ResultCodeEnum.SIZE_LIMIT_EXCEEDED );
        resultCodes.add( ResultCodeEnum.ADMIN_LIMIT_EXCEEDED );
        resultCodes.add( ResultCodeEnum.UNAVAILABLE_CRITICAL_EXTENSION );
        resultCodes.add( ResultCodeEnum.BUSY );
        resultCodes.add( ResultCodeEnum.UNAVAILABLE );
        resultCodes.add( ResultCodeEnum.UNWILLING_TO_PERFORM );
        resultCodes.add( ResultCodeEnum.LOOP_DETECT );
        resultCodes.add( ResultCodeEnum.NO_SUCH_ATTRIBUTE );
        resultCodes.add( ResultCodeEnum.UNDEFINED_ATTRIBUTE_TYPE );
        resultCodes.add( ResultCodeEnum.INAPPROPRIATE_MATCHING );
        resultCodes.add( ResultCodeEnum.CONSTRAINT_VIOLATION );
        resultCodes.add( ResultCodeEnum.ATTRIBUTE_OR_VALUE_EXISTS );
        resultCodes.add( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX );
        resultCodes.add( ResultCodeEnum.NO_SUCH_OBJECT );
        resultCodes.add( ResultCodeEnum.ALIAS_PROBLEM );
        resultCodes.add( ResultCodeEnum.INVALID_DN_SYNTAX );
        resultCodes.add( ResultCodeEnum.NAMING_VIOLATION );
        resultCodes.add( ResultCodeEnum.OBJECT_CLASS_VIOLATION );
        resultCodes.add( ResultCodeEnum.NOT_ALLOWED_ON_NON_LEAF );
        resultCodes.add( ResultCodeEnum.NOT_ALLOWED_ON_RDN );
        resultCodes.add( ResultCodeEnum.ENTRY_ALREADY_EXISTS );
        resultCodes.add( ResultCodeEnum.OBJECT_CLASS_MODS_PROHIBITED );
        resultCodes.add( ResultCodeEnum.AFFECTS_MULTIPLE_DSAS );
        resultCodes.add( ResultCodeEnum.OTHER );
        resultCodes.add( ResultCodeEnum.CANCELED );
        resultCodes.add( ResultCodeEnum.NO_SUCH_OPERATION );
        resultCodes.add( ResultCodeEnum.TOO_LATE );
        resultCodes.add( ResultCodeEnum.CANNOT_CANCEL );
        resultCodes.add( ResultCodeEnum.UNKNOWN );

        // Sorting the list
        Collections.sort( resultCodes, new Comparator<ResultCodeEnum>()
        {
            public int compare( ResultCodeEnum o1, ResultCodeEnum o2 )
            {
                return Integer.compare( o1.getResultCode(), o2.getResultCode() );
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Group Object Class
        BaseWidgetUtils.createLabel( composite, "Group Object Class:", 1 );
        groupObjectClassComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        groupObjectClassComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        groupObjectClassComboViewer.setContentProvider( new ArrayContentProvider() );
        groupObjectClassComboViewer.setInput( connectionObjectClasses );

        // Group Attribute Type
        BaseWidgetUtils.createLabel( composite, "Group Attribute Type:", 1 );
        groupAttributeTypeComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        groupAttributeTypeComboViewer.getControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        groupAttributeTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        groupAttributeTypeComboViewer.setInput( connectionAttributeTypes );

        // Entry Attribute Type
        BaseWidgetUtils.createLabel( composite, "Entry Attribute Type:", 1 );
        entryAttributeTypeComboViewer = new ComboViewer( new Combo( composite, SWT.DROP_DOWN ) );
        entryAttributeTypeComboViewer.getControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        entryAttributeTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        entryAttributeTypeComboViewer.setInput( connectionAttributeTypes );

        // Modifier Name
        BaseWidgetUtils.createLabel( composite, "Modifier's Name:", 1 );
        modifierNameEntryWidget = new EntryWidget( browserConnection );
        modifierNameEntryWidget.createWidget( composite );
        modifierNameEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Dangling Reference Behavior
        BaseWidgetUtils.createLabel( composite, "Dangling Ref. Behavior:", 1 );
        danglingReferenceBehaviorComboViewer = new ComboViewer( composite );
        danglingReferenceBehaviorComboViewer.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
        danglingReferenceBehaviorComboViewer.setContentProvider( new ArrayContentProvider() );
        danglingReferenceBehaviorComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof OlcMemberOfDanglingReferenceBehaviorEnum )
                {
                    OlcMemberOfDanglingReferenceBehaviorEnum behavior = ( OlcMemberOfDanglingReferenceBehaviorEnum ) element;

                    switch ( behavior )
                    {
                        case IGNORE:
                            return "Ignore";
                        case DROP:
                            return "Drop";
                        case ERROR:
                            return "Error";
                    }
                }

                return super.getText( element );
            };
        } );
        danglingReferenceBehaviorComboViewer.setInput( new OlcMemberOfDanglingReferenceBehaviorEnum[]
            {
                OlcMemberOfDanglingReferenceBehaviorEnum.IGNORE,
                OlcMemberOfDanglingReferenceBehaviorEnum.DROP,
                OlcMemberOfDanglingReferenceBehaviorEnum.ERROR
        } );

        // Dangling Reference Error Code
        BaseWidgetUtils.createLabel( composite, "Dangling Ref. Error Code:", 1 );
        danglingReferenceErrorCodeComboViewer = new ComboViewer( composite );
        danglingReferenceErrorCodeComboViewer.getControl().setLayoutData(
            new GridData( SWT.FILL, SWT.NONE, true, false ) );
        danglingReferenceErrorCodeComboViewer.setContentProvider( new ArrayContentProvider() );
        danglingReferenceErrorCodeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof ResultCodeEnum )
                {
                    ResultCodeEnum resultCode = ( ResultCodeEnum ) element;

                    return NLS.bind( "{0} ({1})", new Object[]
                        { resultCode.getResultCode(), resultCode.getMessage() } );
                }

                return super.getText( element );
            };
        } );
        danglingReferenceErrorCodeComboViewer.setInput( resultCodes );

        // Maintain Referential Integrity
        maintianReferentialIntegrityCheckbox = BaseWidgetUtils.createCheckbox( composite,
            "Maintain Referential Integrity", 2 );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            // Group Object Class
            setComboViewerText( groupObjectClassComboViewer, overlay.getOlcMemberOfGroupOC() );

            // Group Attribute Type
            setComboViewerText( groupAttributeTypeComboViewer, overlay.getOlcMemberOfMemberAD() );

            // Entry Attribute Type
            setComboViewerText( entryAttributeTypeComboViewer, overlay.getOlcMemberOfMemberOfAD() );

            // Modifier Name
            Dn modifierName = overlay.getOlcMemberOfDN();

            if ( modifierName != null )
            {
                modifierNameEntryWidget.setInput( modifierName );
            }
            else
            {
                modifierNameEntryWidget.setInput( Dn.EMPTY_DN );
            }

            // Dangling Reference Behavior
            String danglingReferenceBehaviorString = overlay.getOlcMemberOfDangling();

            if ( danglingReferenceBehaviorString != null )
            {
                OlcMemberOfDanglingReferenceBehaviorEnum danglingReferenceBehavior = OlcMemberOfDanglingReferenceBehaviorEnum
                    .fromString( danglingReferenceBehaviorString );

                if ( danglingReferenceBehavior != null )
                {
                    danglingReferenceBehaviorComboViewer.setSelection( new StructuredSelection(
                        danglingReferenceBehavior ) );
                }
                else
                {
                    danglingReferenceBehaviorComboViewer.setSelection( new StructuredSelection(
                        OlcMemberOfDanglingReferenceBehaviorEnum.IGNORE ) );
                }
            }
            else
            {
                danglingReferenceBehaviorComboViewer.setSelection( new StructuredSelection(
                    OlcMemberOfDanglingReferenceBehaviorEnum.IGNORE ) );
            }

            // Dangling Reference Error Code
            String danglingReferenceErrorCode = overlay.getOlcMemberOfDanglingError();

            if ( danglingReferenceErrorCode != null )
            {
                try
                {
                    // Getting the error code as a ResultCodeEnum value
                    ResultCodeEnum resultCode = ResultCodeEnum.getResultCode( Integer
                        .parseInt( danglingReferenceErrorCode ) );

                    danglingReferenceErrorCodeComboViewer.setSelection( new StructuredSelection( resultCode ) );

                }
                catch ( NumberFormatException e )
                {
                    // The error code is not an int value
                    danglingReferenceErrorCodeComboViewer.setSelection( new StructuredSelection(
                        ResultCodeEnum.CONSTRAINT_VIOLATION ) );
                }
            }
            else
            {
                danglingReferenceErrorCodeComboViewer.setSelection( new StructuredSelection(
                    ResultCodeEnum.CONSTRAINT_VIOLATION ) );
            }

            // Maintain Referential Integrity
            Boolean maintainReferentialIntegrity = overlay.getOlcMemberOfRefInt();

            if ( maintainReferentialIntegrity != null )
            {
                maintianReferentialIntegrityCheckbox.setSelection( maintainReferentialIntegrity );
            }
            else
            {
                maintianReferentialIntegrityCheckbox.setSelection( false );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            // Group Object Class
            String groupObjectClass = getComboViewerText( groupObjectClassComboViewer );

            if ( ( groupObjectClass != null ) && ( !groupObjectClass.isEmpty() ) )
            {
                overlay.setOlcMemberOfGroupOC( groupObjectClass );
            }
            else
            {
                overlay.setOlcMemberOfGroupOC( null );
            }

            // Group Attribute Type
            String groupAttributeType = getComboViewerText( groupAttributeTypeComboViewer );

            if ( ( groupAttributeType != null ) && ( !groupAttributeType.isEmpty() ) )
            {
                overlay.setOlcMemberOfMemberAD( groupAttributeType );
            }
            else
            {
                overlay.setOlcMemberOfMemberAD( null );
            }

            // Entry Attribute Type
            String entryAttributeType = getComboViewerText( entryAttributeTypeComboViewer );

            if ( ( entryAttributeType != null ) && ( !entryAttributeType.isEmpty() ) )
            {
                overlay.setOlcMemberOfMemberOfAD( entryAttributeType );
            }
            else
            {
                overlay.setOlcMemberOfMemberOfAD( null );
            }

            // Modifier Name
            Dn modifierName = modifierNameEntryWidget.getDn();

            if ( ( modifierName != null ) && ( !Dn.EMPTY_DN.equals( modifierName ) ) )
            {
                overlay.setOlcMemberOfDN( modifierName );
            }
            else
            {
                overlay.setOlcMemberOfDN( null );
            }

            // Dangling Reference Behavior
            OlcMemberOfDanglingReferenceBehaviorEnum danglingReferenceBehavior = getSelectedDanglingReferenceBehavior();

            if ( danglingReferenceBehavior != null )
            {
                overlay.setOlcMemberOfDangling( danglingReferenceBehavior.toString() );
            }
            else
            {

                overlay.setOlcMemberOfDangling( null );
            }

            // Dangling Reference Error Code
            ResultCodeEnum danglingReferenceErrorCode = getSelectedDanglingReferenceErrorCode();

            if ( ( danglingReferenceErrorCode != null )
                && ( !ResultCodeEnum.CONSTRAINT_VIOLATION.equals( danglingReferenceErrorCode ) ) )
            {
                overlay.setOlcMemberOfDanglingError( danglingReferenceErrorCode.getResultCode() + "" );
            }
            else
            {
                overlay.setOlcMemberOfDanglingError( null );
            }

            // Maintain Referential Integrity
            overlay.setOlcMemberOfRefInt( maintianReferentialIntegrityCheckbox.getSelection() );

        }
    }


    /**
     * Gets the text selection of the combo viewer.
     *
     * @param viewer the viewer
     * @return the text selection of the viewer
     */
    private String getComboViewerText( ComboViewer viewer )
    {
        return viewer.getCombo().getText();
    }


    /**
     * Sets the text selection of the combo viewer.
     *
     * @param viewer the viewer
     * @param text the text
     */
    private void setComboViewerText( ComboViewer viewer, String text )
    {
        if ( text != null )
        {
            viewer.getCombo().setText( text );
        }
        else
        {
            viewer.getCombo().setText( "" );
        }
    }


    /**
     * Gets the selected dangling reference behavior.
     *
     * @return the selected dangling reference behavior
     */
    private OlcMemberOfDanglingReferenceBehaviorEnum getSelectedDanglingReferenceBehavior()
    {
        StructuredSelection selection = ( StructuredSelection ) danglingReferenceBehaviorComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            Object firstElement = selection.getFirstElement();

            if ( firstElement instanceof OlcMemberOfDanglingReferenceBehaviorEnum )
            {
                return ( OlcMemberOfDanglingReferenceBehaviorEnum ) firstElement;
            }
            else
            {
                return null;
            }
        }

        return null;
    }


    /**
     * Gets the selected dangling reference error code.
     *
     * @return the selected dangling reference error code
     */
    private ResultCodeEnum getSelectedDanglingReferenceErrorCode()
    {
        StructuredSelection selection = ( StructuredSelection ) danglingReferenceErrorCodeComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            Object firstElement = selection.getFirstElement();

            if ( firstElement instanceof ResultCodeEnum )
            {
                return ( ResultCodeEnum ) firstElement;
            }
            else
            {
                return null;
            }
        }

        return null;
    }
}
