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

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.apache.directory.studio.openldap.syncrepl.SaslMechanism;
import org.apache.directory.studio.openldap.syncrepl.SyncRepl;


/**
 * The ReplicationSaslDialog is used to edit the SASL configuration of a SyncRepl consumer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReplicationSaslDialog extends Dialog
{
    /** The SyncRepl value */
    private SyncRepl syncRepl;

    /** The connection */
    private IBrowserConnection browserConnection;

    // UI widgets
    private ScrolledComposite scrolledComposite;
    private Composite composite;
    private ComboViewer saslMechanismComboViewer;
    private Text realmText;
    private Text authenticationIdText;
    private Text authorizationIdText;
    private Text credentialsText;
    private Button showCredentialsCheckbox;
    private Text secPropsText;

    // Listeners
    private SelectionListener showCredentialsCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            if ( showCredentialsCheckbox.getSelection() )
            {
                credentialsText.setEchoChar( '\0' );
            }
            else
            {
                credentialsText.setEchoChar( '\u2022' );
            }
        }
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param index the index
     * @param browserConnection the connection
     */
    public ReplicationSaslDialog( Shell parentShell, SyncRepl syncRepl, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.browserConnection = browserConnection;

        if ( syncRepl != null )
        {
            this.syncRepl = syncRepl.copy();
        }
        else
        {
            this.syncRepl = createDefaultSyncRepl();
        }
    }


    /**
     * Creates a default SyncRepl configuration.
     *
     * @return a default SyncRepl configuration
     */
    private SyncRepl createDefaultSyncRepl()
    {
        return new SyncRepl();
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Replication Options" );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed()
    {
        saveToSyncRepl();

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        // Creating the scrolled composite
        scrolledComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the composite and attaching it to the scrolled composite
        composite = new Composite( scrolledComposite, SWT.NONE );
        composite.setLayout( new GridLayout() );
        scrolledComposite.setContent( composite );

        createSaslConfigurationGroup( composite );

        initFromSyncRepl();

        applyDialogFont( scrolledComposite );
        composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

        return scrolledComposite;
    }


    /**
     * Creates the SASL Configuration group.
     *
     * @param parent the parent composite
     */
    private void createSaslConfigurationGroup( Composite parent )
    {
        // SASL Configuration Group
        Group group = BaseWidgetUtils.createGroup( parent, "SASL Configuration", 1 );
        group.setLayout( new GridLayout( 2, false ) );
        group.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SASL Mechanism
        BaseWidgetUtils.createLabel( group, "SASL Mechanism:", 1 );
        saslMechanismComboViewer = new ComboViewer( group );
        saslMechanismComboViewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        saslMechanismComboViewer.setContentProvider( new ArrayContentProvider() );
        saslMechanismComboViewer.setLabelProvider( new LabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                if ( element instanceof SaslMechanism )
                {
                    return ( ( SaslMechanism ) element ).getTitle();
                }

                return super.getText( element );
            }
        } );
        saslMechanismComboViewer.setInput( new SaslMechanism[]
            { SaslMechanism.DIGEST_MD5, SaslMechanism.GSSAPI } );

        // Authentication ID
        BaseWidgetUtils.createLabel( group, "Authentication ID:", 1 );
        authenticationIdText = BaseWidgetUtils.createText( group, "", 1 );

        // Authorization ID
        BaseWidgetUtils.createLabel( group, "Authorization ID:", 1 );
        authorizationIdText = BaseWidgetUtils.createText( group, "", 1 );

        // Credentials
        BaseWidgetUtils.createLabel( group, "Credentials:", 1 );
        credentialsText = BaseWidgetUtils.createText( group, "", 1 );
        credentialsText.setEchoChar( '\u2022' );

        // Show Credentials Checkbox
        BaseWidgetUtils.createLabel( group, "", 1 );
        showCredentialsCheckbox = BaseWidgetUtils.createCheckbox( group, "Show Credentials", 1 );

        // Realm
        BaseWidgetUtils.createLabel( group, "Realm:", 1 );
        realmText = BaseWidgetUtils.createText( group, "", 1 );

        // Sec Props
        BaseWidgetUtils.createLabel( group, "Sec Props:", 1 );
        secPropsText = BaseWidgetUtils.createText( group, "", 1 );
    }


    /**
     * Initializes the dialog using the SyncRepl object.
     */
    private void initFromSyncRepl()
    {
        if ( syncRepl != null )
        {
            // SASL Mechanism
            String saslMechanismString = syncRepl.getSaslMech();

            if ( saslMechanismString != null )
            {
                try
                {
                    saslMechanismComboViewer.setSelection( new StructuredSelection( SaslMechanism
                        .parse( saslMechanismString ) ) );
                }
                catch ( ParseException e )
                {
                    // Silent
                }
            }

            // Authentication ID
            String authenticationId = syncRepl.getAuthcid();

            if ( authenticationId != null )
            {
                authenticationIdText.setText( authenticationId );
            }

            // Authorization ID
            String authorizationId = syncRepl.getAuthzid();

            if ( authorizationId != null )
            {
                authorizationIdText.setText( authorizationId );
            }

            // Credentials
            String credentials = syncRepl.getCredentials();

            if ( credentials != null )
            {
                credentialsText.setText( credentials );
            }

            // Realm
            String realm = syncRepl.getRealm();

            if ( realm != null )
            {
                realmText.setText( realm );
            }

            // Sec Props
            String secProps = syncRepl.getSecProps();

            if ( secProps != null )
            {
                secPropsText.setText( secProps );
            }

            addListeners();
        }
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        showCredentialsCheckbox.addSelectionListener( showCredentialsCheckboxListener );
    }


    /**
     * Saves the content of the dialog to the SyncRepl object.
     */
    private void saveToSyncRepl()
    {
        if ( syncRepl != null )
        {
            // SASL Mechanism
            syncRepl.setSaslMech( getSaslMechanism() );

            // Authentication ID
            String authenticationId = authenticationIdText.getText();

            if ( ( authenticationId != null ) && ( !"".equals( authenticationId ) ) )
            {
                syncRepl.setAuthcid( authenticationId );
            }
            else
            {
                syncRepl.setAuthcid( null );
            }

            // Authorization ID
            String authorizationId = authorizationIdText.getText();

            if ( ( authorizationId != null ) && ( !"".equals( authorizationId ) ) )
            {
                syncRepl.setAuthzid( authorizationId );
            }
            else
            {
                syncRepl.setAuthzid( null );
            }

            // Credentials
            String credentials = credentialsText.getText();

            if ( ( credentials != null ) && ( !"".equals( credentials ) ) )
            {
                syncRepl.setCredentials( credentials );
            }
            else
            {
                syncRepl.setCredentials( null );
            }

            // Realm
            String realm = realmText.getText();

            if ( ( realm != null ) && ( !"".equals( realm ) ) )
            {
                syncRepl.setRealm( realm );
            }
            else
            {
                syncRepl.setRealm( null );
            }

            // Sec Props
            String secProps = secPropsText.getText();

            if ( ( secProps != null ) && ( !"".equals( secProps ) ) )
            {
                syncRepl.setSecProps( secProps );
            }
            else
            {
                syncRepl.setSecProps( null );
            }
        }
    }


    /**
     * Gets the selected SASL mechanism.
     *
     * @return the selected SASL mechanism
     */
    private String getSaslMechanism()
    {
        StructuredSelection selection = ( StructuredSelection ) saslMechanismComboViewer.getSelection();

        if ( ( selection != null ) && ( !selection.isEmpty() ) )
        {
            return ( ( SaslMechanism ) selection.getFirstElement() ).getValue();
        }

        return null;
    }


    /**
     * Gets the SyncRepl value.
     *
     * @return the SyncRepl value
     */
    public SyncRepl getSyncRepl()
    {
        return syncRepl;
    }
}
