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
package org.apache.directory.studio.ldapservers.wizards;


import java.util.regex.Pattern;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersPlugin;
import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;


/**
 * This class implements the wizard page for the new server wizard selection page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewServerWizardSelectionPage extends WizardPage
{
    /** The servers handler */
    private LdapServersManager ldapServersManager;

    /** The content provider of the TreeViewer */
    private LdapServerAdapterExtensionsContentProvider contentProvider;

    /** The label provider of the TreeViewer */
    private LdapServerAdapterExtensionsLabelProvider labelProvider;

    // UI fields
    private Label filterLabel;
    private Text filterText;
    private TreeViewer ldapServerAdaptersTreeViewer;
    private Text serverNameText;


    /**
     * Creates a new instance of NewServerWizardSelectionPage.
     */
    public NewServerWizardSelectionPage()
    {
        super( NewServerWizardSelectionPage.class.getCanonicalName() );
        setTitle( "Create an LDAP Server" );
        setDescription( "Please choose the type of server and specify a name to create a new server." );
        setImageDescriptor( LdapServersPlugin.getDefault().getImageDescriptor(
            LdapServersPluginConstants.IMG_SERVER_NEW_WIZARD ) );
        setPageComplete( false );
        ldapServersManager = LdapServersManager.getDefault();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Creating the composite to hold the UI
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );

        // Filter Label
        filterLabel = BaseWidgetUtils.createLabel( composite, "Select the server type:", 2 );
        filterLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // Filter Text
        filterText = new Text( composite, SWT.BORDER | SWT.SEARCH | SWT.CANCEL );
        filterText.setMessage( "Type filter here..." );
        filterText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // LDAP Server Adapters Tree Viewer
        ldapServerAdaptersTreeViewer = new TreeViewer( new Tree( composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.BORDER ) );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
        gd.heightHint = 90;
        ldapServerAdaptersTreeViewer.getTree().setLayoutData( gd );
        contentProvider = new LdapServerAdapterExtensionsContentProvider();
        ldapServerAdaptersTreeViewer.setContentProvider( contentProvider );
        labelProvider = new LdapServerAdapterExtensionsLabelProvider();
        ldapServerAdaptersTreeViewer.setLabelProvider( labelProvider );
        ldapServerAdaptersTreeViewer.setInput( "LDAP Server Adapters Tree Viewer Input" );
        ldapServerAdaptersTreeViewer.expandAll();
        ldapServerAdaptersTreeViewer.addFilter( new ViewerFilter()
        {
            public boolean select( Viewer viewer, Object parentElement, Object element )
            {
                // The current element is a Vendor
                if ( element instanceof String )
                {
                    Object[] children = contentProvider.getChildren( element );
                    for ( Object child : children )
                    {
                        String label = labelProvider.getText( child );

                        return getFilterPattern().matcher( label ).matches();
                    }
                }
                // The current element is an LdapServerAdapterExtension
                else if ( element instanceof LdapServerAdapterExtension )
                {
                    String label = labelProvider.getText( element );

                    return getFilterPattern().matcher( label ).matches();
                }

                return false;
            }


            /**
             * Gets the filter pattern.
             *
             * @return
             *      the filter pattern
             */
            private Pattern getFilterPattern()
            {
                String filter = filterText.getText();

                return Pattern.compile( ( ( filter == null ) ? ".*" : ".*" + filter + ".*" ), Pattern.CASE_INSENSITIVE );
            }
        } );

        // Filler
        Label filler = new Label( composite, SWT.NONE );
        filler.setLayoutData( new GridData( SWT.NONE, SWT.NONE, true, false, 2, 1 ) );

        // Server Name Label
        BaseWidgetUtils.createLabel( composite, "Server Name:", 1 );

        // Server Name Text
        serverNameText = BaseWidgetUtils.createText( composite, "", 1 );

        // Adding listeners
        addListeners();

        // Setting the control on the composite and setting focus
        setControl( composite );
        composite.setFocus();
    }


    /**
     * Adding listeners to UI elements.
     */
    private void addListeners()
    {
        // Filter Text
        filterText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                // Refreshing the LDAP Server Adapters Tree Viewer
                ldapServerAdaptersTreeViewer.refresh();
                ldapServerAdaptersTreeViewer.expandAll();
            }
        } );

        // LDAP Server Adapters Tree Viewer
        ldapServerAdaptersTreeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                // Assigning an automatic name to the LDAP Server based on the selected LDAP Server Adapter Extension
                serverNameText.setText( getServerName( ( StructuredSelection ) ldapServerAdaptersTreeViewer
                    .getSelection() ) );

                //                getContainer().updateButtons();

                validate();
            }


            /**
             * Get a name for the server based on the current selection.
             *
             * @param selection
             *      the current selection
             * @return
             *      a name for the server based on the current selection
             */
            private String getServerName( StructuredSelection selection )
            {
                if ( !selection.isEmpty() )
                {
                    Object selectedObject = selection.getFirstElement();
                    if ( selectedObject instanceof LdapServerAdapterExtension )
                    {
                        // Getting the name of the LDAP Server Adapter Extension
                        String serverName = labelProvider.getText( selection.getFirstElement() );

                        // Checking if the name if available
                        if ( ldapServersManager.isNameAvailable( serverName ) )
                        {
                            return serverName;
                        }
                        else
                        {
                            // The name is not available, looking for another name
                            String newServerName = serverName;

                            for ( int i = 2; !ldapServersManager.isNameAvailable( newServerName ); i++ )
                            {
                                newServerName = serverName + " (" + i + ")";
                            }

                            return newServerName;
                        }
                    }
                }

                // Returning an empty string if the selection is empty or if the current selection is a vendor
                return "";
            }
        } );

        // Server Name Text
        serverNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        displayErrorMessage( null );

        // LDAP Server Adapters Tree Viewer
        StructuredSelection selection = ( StructuredSelection ) ldapServerAdaptersTreeViewer.getSelection();
        if ( selection.isEmpty() )
        {
            displayErrorMessage( "Choose the type of server to create." );
            return;
        }
        else
        {
            Object selectedObject = selection.getFirstElement();
            if ( selectedObject instanceof String )
            {
                displayErrorMessage( "Choose the type of server to create." );
                return;
            }
        }

        // Server Name Text
        String name = serverNameText.getText();
        if ( ( name != null ) )
        {
            if ( "".equals( name ) ) //$NON-NLS-1$
            {
                displayErrorMessage( "Enter a name for the LDAP server." );
                return;
            }
            if ( !ldapServersManager.isNameAvailable( name ) )
            {
                displayErrorMessage( "An LDAP server with the same name already exists." );
                return;
            }
        }
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    protected void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Gets the name of the server.
     *
     * @return
     *      the name of the server
     */
    public String getServerName()
    {
        return serverNameText.getText();
    }


    /**
     * Gets the Ldap Server Adapter Extension.
     *
     * @return
     *      the Ldap Server Adapter Extension
     */
    public LdapServerAdapterExtension getLdapServerAdapterExtension()
    {
        StructuredSelection selection = ( StructuredSelection ) ldapServerAdaptersTreeViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Object selectedObject = selection.getFirstElement();
            if ( selectedObject instanceof LdapServerAdapterExtension )
            {
                return ( LdapServerAdapterExtension ) selectedObject;
            }
        }

        return null;
    }
}
