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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.widgets.ConnectionActionGroup;
import org.apache.directory.studio.connection.ui.widgets.ConnectionConfiguration;
import org.apache.directory.studio.connection.ui.widgets.ConnectionUniversalListener;
import org.apache.directory.studio.connection.ui.widgets.ConnectionWidget;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Information Page of the NewProjectWizard.
 * <p>
 * It is used to let the user create a new Project
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewProjectWizardConnectionSelectionPage extends WizardPage
{
    private ConnectionConfiguration configuration;
    private ConnectionUniversalListener universalListener;

    // UI Fields
    private ConnectionWidget connectionWidget;
    private ConnectionActionGroup actionGroup;


    /**
     * Creates a new instance of NewProjectWizardConnectionSelectionPage.
     */
    protected NewProjectWizardConnectionSelectionPage()
    {
        super( "NewProjectWizardConnectionSelectionPage" );
        setTitle( "Create a Schema project." );
        setDescription( "Please select a connection." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_PROJECT_NEW_WIZARD ) );
        setPageComplete( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );

        // Choose A Connection Label
        Label label = new Label( composite, SWT.NONE );
        label.setText( "Choose a connection:" );
        label.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating configuration
        configuration = new ConnectionConfiguration();

        // Creating Connection Widget
        connectionWidget = new ConnectionWidget( configuration, null );
        connectionWidget.createWidget( composite );
        connectionWidget.setInput( ConnectionCorePlugin.getDefault().getConnectionFolderManager() );

        connectionWidget.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                validatePage();
            }
        } );

        // creating the listener
        universalListener = new ConnectionUniversalListener( connectionWidget.getViewer() );

        // create actions and context menu (and register global actions)
        actionGroup = new ConnectionActionGroup( connectionWidget, configuration );
        actionGroup.fillToolBar( connectionWidget.getToolBarManager() );
        actionGroup.fillMenu( connectionWidget.getMenuManager() );
        actionGroup.fillContextMenu( connectionWidget.getContextMenuManager() );
        actionGroup.activateGlobalActionHandlers();

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * Validates the page.
     */
    private void validatePage()
    {
        ISelection selection = connectionWidget.getViewer().getSelection();
        if ( selection.isEmpty() )
        {
            displayErrorMessage( "A connection must be selected." );
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    private void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Gets the selected connection.
     *
     * @return
     *      the selection connection
     */
    public Connection getSelectedConnection()
    {
        return ( Connection ) ( ( StructuredSelection ) connectionWidget.getViewer().getSelection() ).getFirstElement();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            actionGroup.dispose();
            actionGroup = null;
            configuration.dispose();
            configuration = null;
            universalListener.dispose();
            universalListener = null;
            connectionWidget.dispose();
            connectionWidget = null;
        }

        super.dispose();
    }
}
