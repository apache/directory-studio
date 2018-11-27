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


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.ProjectsHandler;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class represents the Information Page of the NewProjectWizard.
 * <p>
 * It is used to let the user create a new Project
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewProjectWizardInformationPage extends AbstractWizardPage
{
    /** The ProjectsHandler */
    private ProjectsHandler projectsHandler;

    // UI Fields
    private Text nameText;
    private Button typeOnlineRadio;
    private Button typeOfflineRadio;


    /**
     * Creates a new instance of NewProjectWizardInformationPage.
     */
    protected NewProjectWizardInformationPage()
    {
        super( "NewProjectWizardInformationPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewProjectWizardInformationPage.CreateSchemaProject" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewProjectWizardInformationPage.SpecifiyNameAndType" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_PROJECT_NEW_WIZARD ) );
        projectsHandler = Activator.getDefault().getProjectsHandler();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );

        // Name
        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( Messages.getString( "NewProjectWizardInformationPage.ProjectName" ) ); //$NON-NLS-1$
        nameText = new Text( composite, SWT.BORDER );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        if ( PluginUtils.getSchemaConnectors().size() > 0 )
        {
            // Type Group
            Group typeGroup = new Group( composite, SWT.NONE );
            typeGroup.setText( Messages.getString( "NewProjectWizardInformationPage.Type" ) ); //$NON-NLS-1$
            typeGroup.setLayout( new GridLayout() );
            typeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

            typeOfflineRadio = new Button( typeGroup, SWT.RADIO );
            typeOfflineRadio.setText( Messages.getString( "NewProjectWizardInformationPage.OfflineSchema" ) ); //$NON-NLS-1$
            typeOfflineRadio.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

            typeOnlineRadio = new Button( typeGroup, SWT.RADIO );
            typeOnlineRadio.setText( Messages.getString( "NewProjectWizardInformationPage.OnlineSchema" ) ); //$NON-NLS-1$
            typeOnlineRadio.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        }

        initFields();
        addListeners();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        if ( typeOfflineRadio != null )
        {
            typeOfflineRadio.setSelection( true );
        }

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );

        SelectionListener dialogChangedSelectionListener = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                dialogChanged();
            }
        };

        typeOfflineRadio.addSelectionListener( dialogChangedSelectionListener );
        typeOnlineRadio.addSelectionListener( dialogChangedSelectionListener );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Name
        if ( nameText.getText().equals( "" ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "NewProjectWizardInformationPage.ErrorNoNameSpecified" ) ); //$NON-NLS-1$
            return;
        }
        else if ( projectsHandler.isProjectNameAlreadyTaken( nameText.getText() ) )
        {
            displayErrorMessage( Messages.getString( "NewProjectWizardInformationPage.ErrorProjectNameExists" ) ); //$NON-NLS-1$
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the name of the project.
     *
     * @return
     *      the name of the project
     */
    public String getProjectName()
    {
        return nameText.getText();
    }


    /**
     * Gets the type of the project.
     *
     * @return
     *      the type of the project
     */
    public ProjectType getProjectType()
    {
        if ( typeOnlineRadio != null )
        {
            if ( typeOnlineRadio.getSelection() )
            {
                return ProjectType.ONLINE;
            }
            else
            {
                return ProjectType.OFFLINE;
            }
        }

        // Default
        return ProjectType.OFFLINE;
    }
}
