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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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

    // Listeners
    VerifyListener integerVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
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
        Button enabledCheckbox = toolkit.createButton( client, "Enabled", SWT.CHECK );
        enabledCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // ID Text
        toolkit.createLabel( client, "ID:" );
        Text idText = toolkit.createText( client, "" );
        idText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Description Text
        toolkit.createLabel( client, "Description:" );
        Text descriptionText = toolkit.createText( client, "" );
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
        Text remoteHostText = toolkit.createText( composite, "" );
        remoteHostText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Remote Port Text
        toolkit.createLabel( composite, "Remote Port:" );
        Text remotePortText = toolkit.createText( composite, "" );
        remotePortText.addVerifyListener( integerVerifyListener );
        remotePortText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Bind DN Text
        toolkit.createLabel( composite, "Bind DN:" );
        Text bindDnText = toolkit.createText( composite, "" );
        bindDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Bind Password Text
        toolkit.createLabel( composite, "Bind Password:" );
        Text bindPasswordText = toolkit.createText( composite, "" );
        bindPasswordText.setEchoChar( '\u2022' );
        bindPasswordText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Show Password Checkbox
        toolkit.createLabel( composite, "" ); //$NON-NLS-1$
        Button showPasswordCheckbox = toolkit.createButton( composite, "Show password", SWT.CHECK );
        showPasswordCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        showPasswordCheckbox.setSelection( false );

        // Size Limit Text
        toolkit.createLabel( composite, "Size Limit:" );
        Text sizeLimitText = toolkit.createText( composite, "" );
        sizeLimitText.addVerifyListener( integerVerifyListener );
        sizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Time Limit Text
        toolkit.createLabel( composite, "Time Limit:" );
        Text timeLimitText = toolkit.createText( composite, "" );
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
        Composite client = toolkit.createComposite( section );
        toolkit.paintBordersFor( client );
        GridLayout glayout = new GridLayout( 2, false );
        client.setLayout( glayout );
        section.setClient( client );

        // Base DN Text
        toolkit.createLabel( client, "Base DN:" );
        Text baseDnText = toolkit.createText( client, "" );
        baseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Filter Text
        toolkit.createLabel( client, "Filter:" );
        Text filterText = toolkit.createText( client, "" );
        filterText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Scope
        Label scopeLabel = toolkit.createLabel( client, "Scope:" );
        scopeLabel.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false, 1, 3 ) );

        // Subtree Scope Button
        Button subtreeScopeButton = toolkit.createButton( client, "Subtree", SWT.RADIO );

        // One Level Scope Button
        Button oneLevelScopeButton = toolkit.createButton( client, "One Level", SWT.RADIO );

        // Object Scope Button
        Button objectScopeButton = toolkit.createButton( client, "Object", SWT.RADIO );

        // Attributes Text
        toolkit.createLabel( client, "Attributes:" );
        Text attributesText = toolkit.createText( client, "" );
        attributesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Aliases Dereferencing Text
        Label aliasesDereferencingLable = toolkit.createLabel( client, "Aliases\nDereferencing:" );
        aliasesDereferencingLable.setLayoutData( new GridData( SWT.BEGINNING, SWT.TOP, false, false, 1, 2 ) );

        // Finding Base DN Aliases Dereferencing Button
        Button findingBaseDnAliasesDereferencingButton = toolkit.createButton( client, "Finding Base DN", SWT.CHECK );

        // Search Aliases Dereferencing Button
        Button searchAliasesDereferencingButton = toolkit.createButton( client, "Search", SWT.CHECK );
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
    }


    /**
     * Removes listeners to UI fields.
     */
    private void removeListeners()
    {
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

        addListeners();
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }
}
