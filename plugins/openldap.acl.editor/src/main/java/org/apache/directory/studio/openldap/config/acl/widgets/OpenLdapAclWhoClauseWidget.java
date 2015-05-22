/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.text.MessageFormat;

import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPluginConstants;
import org.apache.directory.studio.openldap.config.acl.dialogs.OpenLdapAccessLevelDialog;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevel;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevelLevelEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclControlEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseAnonymous;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseDn;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseDnAttr;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseGroup;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseSaslSsf;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseSelf;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseSsf;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseStar;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseTlsSsf;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseTransportSsf;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseUsers;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseDnAttributeComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseDnComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseGroupComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseSaslSsfComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseSsfComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseTlsSsfComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhoClauseTransportSsfComposite;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclWhoClauseWidget extends AbstractWidget implements SelectionListener
{
    /** The array of clauses */
    private Object[] clauses = new Object[]
        {
            new ClauseComboViewerName(),
            AclWhoClauseEnum.STAR,
            AclWhoClauseEnum.ANONYMOUS,
            AclWhoClauseEnum.USERS,
            AclWhoClauseEnum.SELF,
            AclWhoClauseEnum.DN,
            AclWhoClauseEnum.DNATTR,
            AclWhoClauseEnum.GROUP,
            AclWhoClauseEnum.SASL_SSF,
            AclWhoClauseEnum.SSF,
            AclWhoClauseEnum.TLS_SSF,
            AclWhoClauseEnum.TRANSPORT_SSF
    };

    /** The array of access levels */
    private Object[] accessLevels = new Object[]
        {
            new AccessLevelComboViewerName(),
            AclAccessLevelLevelEnum.MANAGE,
            AclAccessLevelLevelEnum.WRITE,
            AclAccessLevelLevelEnum.READ,
            AclAccessLevelLevelEnum.SEARCH,
            AclAccessLevelLevelEnum.COMPARE,
            AclAccessLevelLevelEnum.AUTH,
            AclAccessLevelLevelEnum.DISCLOSE,
            AclAccessLevelLevelEnum.NONE,
            new AccessLevelComboViewerCustom()
    };

    /** The array of controls */
    private Object[] controls = new Object[]
        {
            new ControlComboViewerName(),
            AclControlEnum.STOP,
            AclControlEnum.CONTINUE,
            AclControlEnum.BREAK
    };

    /** The parent builder widget */
    private OpenLdapAclWhoClausesBuilderWidget builderWidget;

    /** The row index */
    private int index;

    /** The clause */
    private AclWhoClause clause;

    /** The current clause selection */
    private Object currentClauseSelection = clauses[0];

    /** The current access level selection */
    private Object currentAccessLevelSelection = accessLevels[0];

    /** The current control selection */
    private Object currentControlSelection = controls[0];

    /** The current custom access level */
    private AclAccessLevel currentCustomAccessLevel;

    // UI Widgets
    private Composite composite;
    private Composite configurationComposite;
    private ComboViewer clauseComboViewer;
    private ComboViewer accessLevelComboViewer;
    private ComboViewer controlComboViewer;
    private ToolBar toolbar;
    private ToolItem addButton;
    private ToolItem deleteButton;
    private ToolItem moveUpButton;
    private ToolItem moveDownButton;

    // Listeners
    /** The listener for the clause combo viewer */
    private ISelectionChangedListener clauseComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            // Getting the selected clause
            Object selection = ( ( StructuredSelection ) clauseComboViewer
                .getSelection() ).getFirstElement();

            // Only changing the UI when the selection is different
            if ( currentClauseSelection != selection )
            {
                // Storing the current selection
                currentClauseSelection = selection;

                // Disposing the current composite
                if ( ( configurationComposite != null ) && ( !configurationComposite.isDisposed() ) )
                {
                    configurationComposite.dispose();
                    configurationComposite = null;
                }

                // Setting the clause from the current selection
                setClause();

                // Creating the configuration UI
                createConfigurationUI();

                // Notifying listeners
                notifyListeners();
            }
        }
    };

    /** The listener for the access level combo viewer */
    private ISelectionChangedListener accessLevelComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            // Getting the selected access level
            Object selection = ( ( StructuredSelection ) accessLevelComboViewer
                .getSelection() ).getFirstElement();

            // Special case for the 'custom' item
            if ( accessLevels[accessLevels.length - 1].equals( selection ) )
            {
                // Getting the current access level
                AclAccessLevel accessLevel = null;
                if ( clause != null )
                {
                    accessLevel = clause.getAccessLevel();
                }

                // Opening a dialog to edit the access level
                OpenLdapAccessLevelDialog accessLevelDialog = new OpenLdapAccessLevelDialog( accessLevel );
                if ( accessLevelDialog.open() == OpenLdapAccessLevelDialog.OK )
                {
                    // Getting the access level from the dialog
                    currentCustomAccessLevel = accessLevelDialog.getAccessLevel();
                }
                else
                {
                    // The dialog has been canceled

                    // Only changing the UI when the selection is different
                    if ( currentAccessLevelSelection != selection )
                    {
                        accessLevelComboViewer.removeSelectionChangedListener( accessLevelComboViewerListener );
                        accessLevelComboViewer
                            .setSelection( new StructuredSelection( currentAccessLevelSelection ) );
                        accessLevelComboViewer.addSelectionChangedListener( accessLevelComboViewerListener );
                    }

                    // We exit here
                    return;
                }
            }

            // Only changing the UI when the selection is different or we have a custom access level value to store
            if ( ( currentAccessLevelSelection != selection ) || ( currentCustomAccessLevel != null ) )
            {
                // Storing the current selection
                currentAccessLevelSelection = selection;

                // Setting the access level from the current selection
                setAccessLevel();

                // Is it a simple access level?
                AclAccessLevel accessLevel = clause.getAccessLevel();
                if ( isSimple( accessLevel ) )
                {
                    currentAccessLevelSelection = accessLevel.getLevel();
                }
                // Is it a custom access level?
                else if ( isCustom( accessLevel ) )
                {
                    currentAccessLevelSelection = accessLevels[accessLevels.length - 1];
                }
                // Bogus case
                else
                {
                    currentAccessLevelSelection = accessLevels[0];
                }

                // Setting the correct selection and refreshing the combo viewer to update the labels
                accessLevelComboViewer.removeSelectionChangedListener( accessLevelComboViewerListener );
                accessLevelComboViewer
                    .setSelection( new StructuredSelection( currentAccessLevelSelection ) );
                accessLevelComboViewer.addSelectionChangedListener( accessLevelComboViewerListener );
                accessLevelComboViewer.refresh();

                // Notifying listeners
                notifyListeners();
            }
        }
    };

    /** The listener for the control combo viewer */
    private ISelectionChangedListener controlComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            // Getting the selected control
            Object selection = ( ( StructuredSelection ) controlComboViewer
                .getSelection() ).getFirstElement();

            // Only changing the UI when the selection is different
            if ( currentControlSelection != selection )
            {
                // Storing the current selection
                currentControlSelection = selection;

                // Setting the control from the current selection
                setControl();

                // Notifying listeners
                notifyListeners();
            }
        }
    };


    /**
     * Creates a new instance of OpenLdapAclWhoClauseWidget.
     *
     * @param builderWidget the parent builder widget
     * @param index the row index
     */
    public OpenLdapAclWhoClauseWidget( OpenLdapAclWhoClausesBuilderWidget builderWidget, AclWhoClause clause, int index )
    {
        this.builderWidget = builderWidget;
        this.clause = clause;
        this.index = index;
    }


    public void create( Composite parent )
    {
        // Creating the widget base composite
        composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Creating the top composites
        Composite topComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        Composite topSubComposite = BaseWidgetUtils.createColumnContainer( topComposite, 3, true, 1 );

        // Creating the clause, access level and control combo viewers
        createClauseComboViewer( topSubComposite );
        createAccessLevelComboViewer( topSubComposite );
        createControlComboViewer( topSubComposite );

        // Creating the toolbar and buttons
        createToolbarAndButtons( topComposite );

        // Initializing the UI with the clause
        initWithClause();

        // Adding the listeners to the UI widgets
        addListeners();
    }


    /**
     * Initializes the UI with the clause
     */
    private void initWithClause()
    {
        if ( clause != null )
        {
            // Clause
            currentClauseSelection = AclWhoClauseEnum.get( clause );

            // Access Level
            AclAccessLevel accessLevel = clause.getAccessLevel();
            if ( accessLevel == null )
            {
                currentAccessLevelSelection = accessLevels[0];
            }
            else
            {
                // Is it a simple access level?
                if ( isSimple( accessLevel ) )
                {
                    currentAccessLevelSelection = accessLevel.getLevel();
                }
                // Is it a custom access level?
                else if ( isCustom( accessLevel ) )
                {
                    currentAccessLevelSelection = accessLevels[accessLevels.length - 1];
                }
                // Bogus case
                else
                {
                    currentAccessLevelSelection = accessLevels[0];
                }
            }

            // Control
            AclControlEnum control = clause.getControl();
            if ( control == null )
            {
                currentControlSelection = controls[0];
            }
            else
            {
                currentControlSelection = control;
            }
        }
        else
        {
            // Defaulting to the first row of the arrays
            currentClauseSelection = clauses[0];
            currentAccessLevelSelection = accessLevels[0];
            currentControlSelection = controls[0];
        }

        // Setting the selection for the combo viewers
        clauseComboViewer.setSelection( new StructuredSelection( currentClauseSelection ) );
        accessLevelComboViewer.setSelection( new StructuredSelection( currentAccessLevelSelection ) );
        controlComboViewer.setSelection( new StructuredSelection( currentControlSelection ) );
    }


    /**
     * Indicates if the given access level is simple or not.
     *
     * @param accessLevel the access level
     * @return <code>true</code> if the access level is simple,
     *         <code>false</code> if not
     */
    private boolean isSimple( AclAccessLevel accessLevel )
    {
        if ( accessLevel != null )
        {
            return ( !accessLevel.isSelf() ) && ( accessLevel.getLevel() != null );
        }

        return false;
    }


    /**
     * Indicates if the given access level is complex or not.
     *
     * @param accessLevel the access level
     * @return <code>true</code> if the access complex is simple,
     *         <code>false</code> if not
     */
    private boolean isCustom( AclAccessLevel accessLevel )
    {
        if ( accessLevel != null )
        {
            return ( accessLevel.isSelf() )
                || ( ( accessLevel.getPrivilegeModifier() != null ) && ( accessLevel.getPrivileges().size() > 0 ) );
        }

        return false;
    }


    /**
     * Creates the clause combo viewer.
     *
     * @param parent the parent composite
     */
    private void createClauseComboViewer( Composite parent )
    {
        clauseComboViewer = new ComboViewer( BaseWidgetUtils.createReadonlyCombo( parent, new String[0], -1, 1 ) );
        clauseComboViewer.setContentProvider( new ArrayContentProvider() );
        clauseComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof ClauseComboViewerName )
                {
                    return "< Clause >";
                }
                else if ( element instanceof AclWhoClauseEnum )
                {
                    AclWhoClauseEnum value = ( AclWhoClauseEnum ) element;
                    switch ( value )
                    {
                        case STAR:
                            return "Anyone (*)";
                        case ANONYMOUS:
                            return "Anonymous";
                        case USERS:
                            return "Users";
                        case SELF:
                            return "Self";
                        case DN:
                            return "DN";
                        case DNATTR:
                            return "DN in attribute";
                        case GROUP:
                            return "Group";
                        case SASL_SSF:
                            return "SASL SSF";
                        case SSF:
                            return "SSF";
                        case TLS_SSF:
                            return "TLS SSF";
                        case TRANSPORT_SSF:
                            return "Transport SSF";
                    }
                }

                return super.getText( element );
            }
        } );
        clauseComboViewer.setInput( clauses );
        clauseComboViewer.setSelection( new StructuredSelection( currentClauseSelection ) );
    }


    /**
     * Creating the access level combo viewer.
     *
     * @param parent the parent composite
     */
    private void createAccessLevelComboViewer( Composite parent )
    {
        accessLevelComboViewer = new ComboViewer(
            BaseWidgetUtils.createReadonlyCombo( parent, new String[0], -1, 1 ) );
        accessLevelComboViewer.setContentProvider( new ArrayContentProvider() );
        accessLevelComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof AccessLevelComboViewerName )
                {
                    return "< Access Level >";
                }
                else if ( element instanceof AccessLevelComboViewerCustom )
                {
                    if ( ( clause != null ) && ( isCustom( clause.getAccessLevel() ) ) )
                    {
                        return MessageFormat.format( "Custom... [{0}]", clause.getAccessLevel() );
                    }

                    return "Custom...";
                }
                else if ( element instanceof AclAccessLevelLevelEnum )
                {
                    AclAccessLevelLevelEnum value = ( AclAccessLevelLevelEnum ) element;
                    switch ( value )
                    {
                        case MANAGE:
                            return "Manage";
                        case WRITE:
                            return "Write";
                        case READ:
                            return "Read";
                        case SEARCH:
                            return "Search";
                        case COMPARE:
                            return "Compare";
                        case AUTH:
                            return "Auth";
                        case DISCLOSE:
                            return "Disclose";
                        case NONE:
                            return "None";
                    }
                }

                return super.getText( element );
            }
        } );
        accessLevelComboViewer.setInput( accessLevels );
        accessLevelComboViewer.setSelection( new StructuredSelection( currentAccessLevelSelection ) );
    }


    /**
     * Creates the control combo viewer.
     *
     * @param parent the parent composite
     */
    private void createControlComboViewer( Composite parent )
    {
        controlComboViewer = new ComboViewer(
            BaseWidgetUtils.createReadonlyCombo( parent, new String[0], -1, 1 ) );
        controlComboViewer.setContentProvider( new ArrayContentProvider() );
        controlComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof ControlComboViewerName )
                {
                    return "< Control >";
                }
                else if ( element instanceof AclControlEnum )
                {
                    AclControlEnum value = ( AclControlEnum ) element;
                    switch ( value )
                    {
                        case STOP:
                            return "Stop";
                        case CONTINUE:
                            return "Continue";
                        case BREAK:
                            return "Break";
                    }
                }

                return super.getText( element );
            }
        } );
        controlComboViewer.setInput( controls );
        controlComboViewer.setSelection( new StructuredSelection( currentControlSelection ) );
    }


    /**
     * Creates the toolbar and buttons.
     *
     * @param parent the parent composite
     */
    private void createToolbarAndButtons( Composite parent )
    {
        // Creating the toolbar
        toolbar = new ToolBar( parent, SWT.HORIZONTAL );

        // Creating the 'Add' button
        addButton = new ToolItem( toolbar, SWT.PUSH );
        addButton.setToolTipText( "Add" );
        addButton.setImage( OpenLdapAclEditorPlugin.getDefault().getImage(
            OpenLdapAclEditorPluginConstants.IMG_ADD ) );

        // Creating the 'Delete' button
        deleteButton = new ToolItem( toolbar, SWT.PUSH );
        deleteButton.setToolTipText( "Delete" );
        deleteButton.setImage( OpenLdapAclEditorPlugin.getDefault().getImage(
            OpenLdapAclEditorPluginConstants.IMG_DELETE ) );

        // Creating the 'Move Up' button
        moveUpButton = new ToolItem( toolbar, SWT.PUSH );
        moveUpButton.setToolTipText( "Move Up" );
        moveUpButton.setImage( OpenLdapAclEditorPlugin.getDefault().getImage(
            OpenLdapAclEditorPluginConstants.IMG_UP ) );
        // Creating the 'Move Down' button
        moveDownButton = new ToolItem( toolbar, SWT.PUSH );
        moveDownButton.setToolTipText( "Move Down" );
        moveDownButton.setImage( OpenLdapAclEditorPlugin.getDefault().getImage(
            OpenLdapAclEditorPluginConstants.IMG_DOWN ) );
    }


    /**
     * Adds the listeners to the UI widgets.
     */
    private void addListeners()
    {
        // Adding the selection listener for the clause combo viewer
        clauseComboViewer.addSelectionChangedListener( clauseComboViewerListener );

        // Adding the selection listener for the access level combo viewer
        accessLevelComboViewer.addSelectionChangedListener( accessLevelComboViewerListener );

        // Adding the selection listener for the control combo viewer
        controlComboViewer.addSelectionChangedListener( controlComboViewerListener );

        // Adding toolbar buttons listeners
        addButton.addSelectionListener( this );
        deleteButton.addSelectionListener( this );
        moveUpButton.addSelectionListener( this );
        moveDownButton.addSelectionListener( this );
    }


    /**
     * Create the configuration UI.
     */
    private void createConfigurationUI()
    {
        if ( currentClauseSelection instanceof AclWhoClauseEnum )
        {
            AclWhoClauseEnum currentClauseSelectionValue = ( AclWhoClauseEnum ) currentClauseSelection;
            switch ( currentClauseSelectionValue )
            {
                case STAR:
                    break; // Nothing to configure
                case ANONYMOUS:
                    break; // Nothing to configure
                case USERS:
                    break; // Nothing to configure
                case SELF:
                    break; // Nothing to configure
                case DN:
                    createUIWhoClauseDn();
                    break;
                case DNATTR:
                    createUIWhoClauseDnAttr();
                    break;
                case GROUP:
                    createUIWhoClauseGroup();
                    break;
                case SASL_SSF:
                    createCompositeWhoClauseSaslSsf();
                    break;
                case SSF:
                    createCompositeWhoClauseSsf();
                    break;
                case TLS_SSF:
                    createCompositeWhoClauseTlsSsf();
                    break;
                case TRANSPORT_SSF:
                    createCompositeWhoClauseTransportSsf();
                    break;
            }
        }
    }


    /**
     * Creates the UI for the DN who clause.
     */
    private void createUIWhoClauseDn()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseDnComposite composite = new WhoClauseDnComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the DN Attribute who clause.
     */
    private void createUIWhoClauseDnAttr()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseDnAttributeComposite composite = new WhoClauseDnAttributeComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the Group who clause.
     */
    private void createUIWhoClauseGroup()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseGroupComposite composite = new WhoClauseGroupComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the SASL SSF who clause.
     */
    private void createCompositeWhoClauseSaslSsf()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseSaslSsfComposite composite = new WhoClauseSaslSsfComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the SSF who clause.
     */
    private void createCompositeWhoClauseSsf()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseSsfComposite composite = new WhoClauseSsfComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the TLS SSF who clause.
     */
    private void createCompositeWhoClauseTlsSsf()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseTlsSsfComposite composite = new WhoClauseTlsSsfComposite( builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates the UI for the Transport SSF who clause.
     */
    private void createCompositeWhoClauseTransportSsf()
    {
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );

        WhoClauseTransportSsfComposite composite = new WhoClauseTransportSsfComposite(
            builderWidget.visualEditorComposite );
        composite.createComposite( configurationComposite );
    }


    /**
     * Creates a basic access level.
     *
     * @param level the level
     * @return a basic access level
     */
    private AclAccessLevel createBasicAccessLevel( AclAccessLevelLevelEnum level )
    {
        AclAccessLevel accessLevel = new AclAccessLevel();
        accessLevel.setLevel( level );
        return accessLevel;
    }


    /**
     * Sets the clause from the current selection.
     */
    private void setClause()
    {
        if ( currentClauseSelection instanceof AclWhoClauseEnum )
        {
            AclWhoClauseEnum clauseSelection = ( AclWhoClauseEnum ) currentClauseSelection;

            // Creating the clause associated with the selection
            switch ( clauseSelection )
            {
                case STAR:
                    clause = new AclWhoClauseStar();
                    break;
                case ANONYMOUS:
                    clause = new AclWhoClauseAnonymous();
                    break;
                case USERS:
                    clause = new AclWhoClauseUsers();
                    break;
                case SELF:
                    clause = new AclWhoClauseSelf();
                    break;
                case DN:
                    clause = new AclWhoClauseDn();
                    break;
                case DNATTR:
                    clause = new AclWhoClauseDnAttr();
                    break;
                case GROUP:
                    clause = new AclWhoClauseGroup();
                    break;
                case SASL_SSF:
                    clause = new AclWhoClauseSaslSsf();
                    break;
                case SSF:
                    clause = new AclWhoClauseSsf();
                    break;
                case TLS_SSF:
                    clause = new AclWhoClauseTlsSsf();
                    break;
                case TRANSPORT_SSF:
                    clause = new AclWhoClauseTransportSsf();
                    break;
            }

            // Also setting access level and control
            setAccessLevel();
            setControl();
        }
        else
        {
            clause = null;
        }
    }


    /**
     * Sets the access level from the current selection.
     */
    private void setAccessLevel()
    {
        if ( currentAccessLevelSelection instanceof AclAccessLevelLevelEnum )
        {
            AclAccessLevelLevelEnum accessLevelSelection = ( AclAccessLevelLevelEnum ) currentAccessLevelSelection;

            // Creating the access level associated with the selection
            switch ( accessLevelSelection )
            {
                case MANAGE:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.MANAGE ) );
                    break;
                case WRITE:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.WRITE ) );
                    break;
                case READ:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.READ ) );
                    break;
                case SEARCH:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.SEARCH ) );
                    break;
                case COMPARE:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.COMPARE ) );
                    break;
                case AUTH:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.AUTH ) );
                    break;
                case DISCLOSE:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.DISCLOSE ) );
                    break;
                case NONE:
                    setAccessLevel( createBasicAccessLevel( AclAccessLevelLevelEnum.NONE ) );
                    break;
            }
        }
        else if ( currentAccessLevelSelection instanceof AccessLevelComboViewerCustom )
        {
            setAccessLevel( currentCustomAccessLevel );
            currentCustomAccessLevel = null;
        }
        else
        {
            // Resetting access level
            setAccessLevel( null );
        }
    }


    /**
     * Sets the access level to the clause (if any).
     *
     * @param accessLevel the access level
     */
    private void setAccessLevel( AclAccessLevel accessLevel )
    {
        if ( clause != null )
        {
            clause.setAccessLevel( accessLevel );
        }
    }


    /**
     * Sets the control from the current selection
     */
    private void setControl()
    {
        if ( currentControlSelection instanceof AclControlEnum )
        {
            AclControlEnum controlSelection = ( AclControlEnum ) currentControlSelection;

            // Creating the clause associated with the selection
            switch ( controlSelection )
            {
                case STOP:
                    setControl( AclControlEnum.STOP );
                    break;
                case CONTINUE:
                    setControl( AclControlEnum.CONTINUE );
                    break;
                case BREAK:
                    setControl( AclControlEnum.BREAK );
                    break;
            }
        }
        else
        {
            // Resetting control
            setControl( null );
        }
    }


    /**
     * Sets the control to the clause (if any).
     *
     * @param control the control
     */
    private void setControl( AclControlEnum control )
    {
        if ( clause != null )
        {
            clause.setControl( control );
        }
    }


    /**
     * Gets the who clause.
     *
     * @return the who clause
     */
    public AclWhoClause getClause()
    {
        return clause;
    }


    /**
     * Gets the row index.
     *
     * @return the row index
     */
    public int getIndex()
    {
        return index;
    }


    /**
     * {@inheritDoc}
     */
    public void widgetSelected( SelectionEvent e )
    {
        Object source = e.getSource();

        if ( source == addButton )
        {
            builderWidget.addNewClause( this );
        }
        else if ( source == deleteButton )
        {
            builderWidget.deleteClause( this );
        }
        else if ( source == moveUpButton )
        {
            builderWidget.moveUpClause( this );
        }
        else if ( source == moveDownButton )
        {
            builderWidget.moveDownClause( this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected( SelectionEvent e )
    {
        // Nothing to do
    }


    /**
     * Gets the 'Add' button.
     *
     * @return the 'Add' button
     */
    public ToolItem getAddButton()
    {
        return addButton;
    }


    /**
     * Gets the 'Delete' button.
     *
     * @return the 'Delete' button
     */
    public ToolItem getDeleteButton()
    {
        return deleteButton;
    }


    /**
     * Gets the 'Move Up' button.
     *
     * @return the 'Move Up' button
     */
    public ToolItem getMoveUpButton()
    {
        return moveUpButton;
    }


    /**
     * Gets the 'Move Down' button.
     *
     * @return the 'Move Down button
     */
    public ToolItem getMoveDownButton()
    {
        return moveDownButton;
    }


    /**
     * Disposes all created SWT widgets.
     */
    public void dispose()
    {
        // Composite
        if ( ( composite != null ) && ( !composite.isDisposed() ) )
        {
            composite.dispose();
        }

        // Combo Viewer
        if ( ( clauseComboViewer != null ) && ( clauseComboViewer.getCombo() != null )
            && ( !clauseComboViewer.getCombo().isDisposed() ) )
        {
            clauseComboViewer.getCombo().dispose();
        }

        // Toolbar
        if ( ( toolbar != null ) && ( !toolbar.isDisposed() ) )
        {
            toolbar.dispose();
        }

        // Configuration composite
        if ( ( configurationComposite != null ) && ( !configurationComposite.isDisposed() ) )
        {
            configurationComposite.dispose();
        }
    }

    /**
     * A private object for the first row of the clause combo viewer.
     */
    class ClauseComboViewerName
    {
    }

    /**
     * A private object for the first row of the access level combo viewer.
     */
    private class AccessLevelComboViewerName
    {
    }

    /**
     * A private object for the last row of the access level combo viewer.
     */
    private class AccessLevelComboViewerCustom
    {
    }

    /**
     * A private object for the first row of the control combo viewer.
     */
    private class ControlComboViewerName
    {
    }
}
