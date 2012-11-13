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


import org.apache.directory.server.config.beans.ReplConsumerBean;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Label;
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
 * This class represents the Details Page of the Server Configuration Editor for the Replication type
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private ReplicationMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The input consumer */
    private ReplConsumerBean input;

    /** The browser connection */
    private IBrowserConnection browserConnection;

    // UI Widgets
    private Button enabledCheckbox;
    private Text idText;
    private Text descriptionText;
    private Text remoteHostText;
    private Text remotePortText;
    private Text bindDnText;
    private Text bindPasswordText;
    private Button showPasswordCheckbox;
    private Text sizeLimitText;
    private Text timeLimitText;
    private EntryWidget entryWidget;
    private FilterWidget filterWidget;
    private Button subtreeScopeButton;
    private Button oneLevelScopeButton;
    private Button objectScopeButton;
    private TableViewer attributesTableViewer;
    private Button addAttributeButton;
    private Button editAttributeButton;
    private Button deleteAttributeButton;
    private Button findingBaseDnAliasesDereferencingButton;
    private Button searchAliasesDereferencingButton;

    // Listeners
    /** The Text Modify Listener */
    private ModifyListener textModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    /** The button Selection Listener */
    private SelectionListener buttonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    /** The widget Modify Listener */
    private WidgetModifyListener widgetModifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            commit( true );
            masterDetailsBlock.setEditorDirty();
        }
    };

    private VerifyListener integerVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };

    private SelectionListener showPasswordCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            if ( showPasswordCheckbox.getSelection() )
            {
                bindPasswordText.setEchoChar( '\0' );
            }
            else
            {
                bindPasswordText.setEchoChar( '\u2022' );
            }
        }
    };


    /**
     * Creates a new instance of ReplicationDetailsPage.
     *
     * @param pmdb
     *      the associated Master Details Block
     */
    public ReplicationDetailsPage( ReplicationMasterDetailsBlock pmdb )
    {
        masterDetailsBlock = pmdb;

        // Getting the browser connection associated with the connection in the configuration
        browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( masterDetailsBlock.getPage().getConnection() );
    }


    /**
     * {@inheritDoc}
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
        createConnectionSection( parent, toolkit );
        createConfigurationSection( parent, toolkit );
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
        section.setText( "Replication Consumer Details" );
        section.setDescription( "Set the properties of the replication consumer." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Enabled Checkbox
        enabledCheckbox = toolkit.createButton( client, "Enabled", SWT.CHECK );
        enabledCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // ID Text
        toolkit.createLabel( client, "ID:" );
        idText = toolkit.createText( client, "" );
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Description Text
        toolkit.createLabel( client, "Description:" );
        descriptionText = toolkit.createText( client, "" );
        descriptionText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createConnectionSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Connection" );
        section.setDescription( "Set the properties of the connection." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Remote Host Text
        toolkit.createLabel( composite, "Remote Host:" );
        remoteHostText = toolkit.createText( composite, "" );
        remoteHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Remote Port Text
        toolkit.createLabel( composite, "Remote Port:" );
        remotePortText = toolkit.createText( composite, "" );
        remotePortText.addVerifyListener( integerVerifyListener );
        remotePortText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Bind DN Text
        toolkit.createLabel( composite, "Bind DN:" );
        bindDnText = toolkit.createText( composite, "" );
        bindDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Bind Password Text
        toolkit.createLabel( composite, "Bind Password:" );
        bindPasswordText = toolkit.createText( composite, "" );
        bindPasswordText.setEchoChar( '\u2022' );
        bindPasswordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Show Password Checkbox
        toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        showPasswordCheckbox = toolkit.createButton( composite, "Show password", SWT.CHECK );
        showPasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        showPasswordCheckbox.setSelection( false );

        // Size Limit Text
        toolkit.createLabel( composite, "Size Limit:" );
        sizeLimitText = toolkit.createText( composite, "" );
        sizeLimitText.addVerifyListener( integerVerifyListener );
        sizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Time Limit Text
        toolkit.createLabel( composite, "Time Limit:" );
        timeLimitText = toolkit.createText( composite, "" );
        timeLimitText.addVerifyListener( integerVerifyListener );
        timeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Details Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createConfigurationSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Replication Consumer Details" );
        section.setDescription( "Set the properties of the configuration." );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 3, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Base DN Text
        toolkit.createLabel( composite, "Base DN:" );
        entryWidget = new EntryWidget( browserConnection, Dn.EMPTY_DN );
        entryWidget.createWidget( composite );

        // Filter Text
        toolkit.createLabel( composite, "Filter:" );
        filterWidget = new FilterWidget();
        filterWidget.setBrowserConnection( browserConnection );
        filterWidget.createWidget( composite );

        // Scope
        Label scopeLabel = toolkit.createLabel( composite, "Scope:" );
        scopeLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false, 1, 3 ) );

        // Subtree Scope Button
        subtreeScopeButton = toolkit.createButton( composite, "Subtree", SWT.RADIO );
        subtreeScopeButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );

        // One Level Scope Button
        oneLevelScopeButton = toolkit.createButton( composite, "One Level", SWT.RADIO );
        oneLevelScopeButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );

        // Object Scope Button
        objectScopeButton = toolkit.createButton( composite, "Object", SWT.RADIO );
        objectScopeButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );

        // Attributes Label
        Label attributesLabel = toolkit.createLabel( composite, "Attributes:" );
        attributesLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        // Attributes Table Viewer
        Composite attributesTableComposite = toolkit.createComposite( composite );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginWidth = gl.marginHeight = 0;
        attributesTableComposite.setLayout( gl );
        attributesTableComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
        Table attributesTable = toolkit.createTable( attributesTableComposite, SWT.BORDER );
        attributesTable.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 3 ) );
        attributesTableViewer = new TableViewer( attributesTable );

        addAttributeButton = toolkit.createButton( attributesTableComposite, "Add...", SWT.PUSH );
        addAttributeButton.setLayoutData( createNewButtonGridData() );

        editAttributeButton = toolkit.createButton( attributesTableComposite, "Edit...", SWT.PUSH );
        editAttributeButton.setLayoutData( createNewButtonGridData() );

        deleteAttributeButton = toolkit.createButton( attributesTableComposite, "Delete", SWT.PUSH );
        deleteAttributeButton.setLayoutData( createNewButtonGridData() );

        // Aliases Dereferencing Text
        Label aliasesDereferencingLable = toolkit.createLabel( composite, "Aliases\nDereferencing:" );
        aliasesDereferencingLable.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false, 1, 2 ) );

        // Finding Base DN Aliases Dereferencing Button
        findingBaseDnAliasesDereferencingButton = toolkit.createButton( composite, "Finding Base DN", SWT.CHECK );
        findingBaseDnAliasesDereferencingButton
            .setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );

        // Search Aliases Dereferencing Button
        searchAliasesDereferencingButton = toolkit.createButton( composite, "Search", SWT.CHECK );
        searchAliasesDereferencingButton.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );

    }


    /**
     * Create a new button grid data.
     *
     * @return the new button grid data
     */
    private GridData createNewButtonGridData()
    {
        GridData gd = new GridData( SWT.FILL, SWT.BEGINNING, false, false );
        gd.widthHint = IDialogConstants.BUTTON_WIDTH;
        return gd;
    }


    /**
     * Adds listeners to UI fields.
     */
    private void addListeners()
    {
        enabledCheckbox.addSelectionListener( buttonSelectionListener );
        idText.addModifyListener( textModifyListener );
        descriptionText.addModifyListener( textModifyListener );
        remoteHostText.addModifyListener( textModifyListener );
        remotePortText.addModifyListener( textModifyListener );
        bindDnText.addModifyListener( textModifyListener );
        bindPasswordText.addModifyListener( textModifyListener );
        showPasswordCheckbox.addSelectionListener( showPasswordCheckboxSelectionListener );
        sizeLimitText.addModifyListener( textModifyListener );
        timeLimitText.addModifyListener( textModifyListener );
        entryWidget.addWidgetModifyListener( widgetModifyListener );
        filterWidget.addWidgetModifyListener( widgetModifyListener );
        subtreeScopeButton.addSelectionListener( buttonSelectionListener );
        oneLevelScopeButton.addSelectionListener( buttonSelectionListener );
        objectScopeButton.addSelectionListener( buttonSelectionListener );
        //attributesTableViewer;
        //addAttributeButton;
        //editAttributeButton;
        //deleteAttributeButton;
        findingBaseDnAliasesDereferencingButton.addSelectionListener( buttonSelectionListener );
        searchAliasesDereferencingButton.addSelectionListener( buttonSelectionListener );
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
        enabledCheckbox.removeSelectionListener( buttonSelectionListener );
        idText.removeModifyListener( textModifyListener );
        descriptionText.removeModifyListener( textModifyListener );
        remoteHostText.removeModifyListener( textModifyListener );
        remotePortText.removeModifyListener( textModifyListener );
        bindDnText.removeModifyListener( textModifyListener );
        bindPasswordText.removeModifyListener( textModifyListener );
        showPasswordCheckbox.removeSelectionListener( showPasswordCheckboxSelectionListener );
        sizeLimitText.removeModifyListener( textModifyListener );
        timeLimitText.removeModifyListener( textModifyListener );
        entryWidget.removeWidgetModifyListener( widgetModifyListener );
        filterWidget.removeWidgetModifyListener( widgetModifyListener );
        subtreeScopeButton.removeSelectionListener( buttonSelectionListener );
        oneLevelScopeButton.removeSelectionListener( buttonSelectionListener );
        objectScopeButton.removeSelectionListener( buttonSelectionListener );
        //attributesTableViewer;
        //addAttributeButton;
        //editAttributeButton;
        //deleteAttributeButton;
        findingBaseDnAliasesDereferencingButton.removeSelectionListener( buttonSelectionListener );
        searchAliasesDereferencingButton.removeSelectionListener( buttonSelectionListener );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            input = ( ReplConsumerBean ) ssel.getFirstElement();
        }
        else
        {
            input = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( input != null )
        {
            // Enabled
            input.setEnabled( enabledCheckbox.getSelection() );

            // ID
            input.setReplConsumerId( checkEmptyString( idText.getText() ) );

            // Description
            input.setDescription( checkEmptyString( descriptionText.getText() ) );

            // Remote Host
            input.setReplProvHostName( checkEmptyString( remoteHostText.getText() ) );

            // Remote Port
            try
            {
                input.setReplProvPort( Integer.parseInt( remotePortText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                input.setReplProvPort( 0 );
            }

            // Bind DN
            input.setReplUserDn( checkEmptyString( bindDnText.getText() ) );

            // Bind Password
            String password = checkEmptyString( bindPasswordText.getText() );

            if ( password != null )
            {
                input.setReplUserPassword( password.getBytes() );
            }
            else
            {
                input.setReplUserPassword( null );
            }

            // Size Limit
            try
            {
                input.setReplSearchSizeLimit( Integer.parseInt( sizeLimitText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                input.setReplSearchSizeLimit( 0 );
            }

            // Time Limit
            try
            {
                input.setReplSearchTimeout( Integer.parseInt( timeLimitText.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                input.setReplSearchTimeout( 0 );
            }

            // Search Base DN
            input.setSearchBaseDn( checkEmptyString( entryWidget.getDn().toString() ) );;

            // Search Filter
            input.setReplSearchFilter( checkEmptyString( filterWidget.getFilter() ) );

            // Search Scope
            SearchScope scope = getSearchScope();

            if ( scope != null )
            {
                input.setReplSearchScope( scope.getLdapUrlValue() );
            }
            else
            {
                input.setReplSearchScope( null );
            }

            // Aliases Dereferencing
            input.setReplAliasDerefMode( getAliasDerefMode().getJndiValue() );
        }
    }


    /**
     * Gets the search scope.
     *
     * @return the search scope
     */
    private SearchScope getSearchScope()
    {
        if ( subtreeScopeButton.getSelection() )
        {
            return SearchScope.SUBTREE;
        }
        else if ( oneLevelScopeButton.getSelection() )
        {
            return SearchScope.ONELEVEL;
        }
        else if ( objectScopeButton.getSelection() )
        {
            return SearchScope.OBJECT;
        }

        return null;
    }


    /**
     * Gets the aliases dereferencing mode.
     *
     * @return the aliases dereferencing mode
     */
    private AliasDerefMode getAliasDerefMode()
    {
        if ( findingBaseDnAliasesDereferencingButton.getSelection() && searchAliasesDereferencingButton.getSelection() )
        {
            return AliasDerefMode.DEREF_ALWAYS;
        }
        else if ( !findingBaseDnAliasesDereferencingButton.getSelection()
            && searchAliasesDereferencingButton.getSelection() )
        {
            return AliasDerefMode.DEREF_IN_SEARCHING;
        }
        else if ( findingBaseDnAliasesDereferencingButton.getSelection()
            && !searchAliasesDereferencingButton.getSelection() )
        {
            return AliasDerefMode.DEREF_FINDING_BASE_OBJ;
        }
        else if ( !findingBaseDnAliasesDereferencingButton.getSelection()
            && !searchAliasesDereferencingButton.getSelection() )
        {
            return AliasDerefMode.NEVER_DEREF_ALIASES;
        }

        return AliasDerefMode.NEVER_DEREF_ALIASES;
    }


    /**
     * Checks if the string is <code>null</code>
     * and returns an empty string in that case.
     *
     * @param s the string
     * @return a non-<code>null</code> string
     */
    private String checkEmptyString( String s )
    {
        if ( "".equals( s ) )
        {
            return null;
        }

        return s;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( input != null )
        {
            // Enabled
            enabledCheckbox.setSelection( input.isEnabled() );

            // ID
            idText.setText( checkNull( input.getReplConsumerId() ) );

            // Description
            descriptionText.setText( checkNull( input.getDescription() ) );

            // Remote Host
            remoteHostText.setText( checkNull( input.getReplProvHostName() ) );

            // Remote Port
            remotePortText.setText( checkNull( String.valueOf( input.getReplProvPort() ) ) );

            // Bind DN
            bindDnText.setText( checkNull( input.getReplUserDn() ) );

            // Bind Password
            bindPasswordText.setText( checkNull( String.valueOf( input.getReplUserPassword() ) ) );

            // Size Limit
            sizeLimitText.setText( checkNull( String.valueOf( input.getReplSearchSizeLimit() ) ) );

            // Time Limit
            timeLimitText.setText( checkNull( String.valueOf( input.getReplSearchTimeout() ) ) );

            // Search Base DN
            try
            {
                entryWidget.setInput( browserConnection, new Dn( input.getSearchBaseDn() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                entryWidget.setInput( browserConnection, Dn.EMPTY_DN );
            }

            // Search Filter
            filterWidget.setFilter( checkNull( input.getReplSearchFilter() ) );

            // Search Scope
            SearchScope scope = null;
            try
            {
                scope = SearchScope.getSearchScope( SearchScope.getSearchScope( input.getReplSearchScope() ) );
            }
            catch ( IllegalArgumentException e )
            {
                scope = null;
            }

            if ( scope != null )
            {
                switch ( scope )
                {
                    case SUBTREE:
                        subtreeScopeButton.setSelection( true );
                        break;
                    case ONELEVEL:
                        oneLevelScopeButton.setSelection( true );
                        break;
                    case OBJECT:
                        objectScopeButton.setSelection( true );
                        break;
                }
            }
            else
            {
                subtreeScopeButton.setSelection( true );
            }

            // Aliases Dereferencing
            AliasDerefMode aliasDerefMode = null;
            try
            {
                aliasDerefMode = AliasDerefMode.getDerefMode( input.getReplAliasDerefMode() );
            }
            catch ( IllegalArgumentException e )
            {
                aliasDerefMode = null;
            }

            if ( aliasDerefMode != null )
            {
                switch ( aliasDerefMode )
                {
                    case DEREF_ALWAYS:
                        findingBaseDnAliasesDereferencingButton.setSelection( true );
                        searchAliasesDereferencingButton.setSelection( true );
                        break;
                    case DEREF_FINDING_BASE_OBJ:
                        findingBaseDnAliasesDereferencingButton.setSelection( true );
                        searchAliasesDereferencingButton.setSelection( false );
                        break;
                    case DEREF_IN_SEARCHING:
                        findingBaseDnAliasesDereferencingButton.setSelection( false );
                        searchAliasesDereferencingButton.setSelection( true );
                        break;
                    case NEVER_DEREF_ALIASES:
                        findingBaseDnAliasesDereferencingButton.setSelection( false );
                        searchAliasesDereferencingButton.setSelection( false );
                        break;
                }
            }
            else
            {
                findingBaseDnAliasesDereferencingButton.setSelection( true );
                searchAliasesDereferencingButton.setSelection( true );
            }
        }

        addListeners();
    }


    /**
     * Checks if the string is <code>null</code>
     * and returns an empty string in that case.
     *
     * @param s the string
     * @return a non-<code>null</code> string
     */
    private String checkNull( String s )
    {
        if ( s == null )
        {
            return "";
        }

        return s;
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        idText.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
