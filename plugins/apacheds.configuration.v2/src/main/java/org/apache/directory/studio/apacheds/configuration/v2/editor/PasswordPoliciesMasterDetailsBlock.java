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


import org.apache.directory.server.config.beans.AuthenticationInterceptorBean;
import org.apache.directory.server.config.beans.InterceptorBean;
import org.apache.directory.server.config.beans.PasswordPolicyBean;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class represents the Password Policies Master/Details Block used in the Password Policies Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPoliciesMasterDetailsBlock extends MasterDetailsBlock
{
    private static final String NEW_ID = Messages.getString( "PasswordPoliciesMasterDetailsBlock.PasswordPolicyNewId" ); //$NON-NLS-1$

    private static final String AUTHENTICATION_INTERCEPTOR_ID = "authenticationInterceptor";

    /** The associated page */
    private PasswordPoliciesPage page;

    /** The Details Page */
    private PasswordPolicyDetailsPage detailsPage;

    // UI Fields
    private TableViewer viewer;
    private Button addButton;
    private Button deleteButton;


    /**
     * Creates a new instance of PasswordPoliciesMasterDetailsBlock.
     *
     * @param page
     *      the associated page
     */
    public PasswordPoliciesMasterDetailsBlock( PasswordPoliciesPage page )
    {
        this.page = page;
    }


    /**
     * {@inheritDoc}
     */
    public void createContent( IManagedForm managedForm )
    {
        super.createContent( managedForm );

        this.sashForm.setWeights( new int[]
            { 40, 60 } );
    }


    /**
     * {@inheritDoc}
     */
    protected void createMasterPart( final IManagedForm managedForm, Composite parent )
    {
        FormToolkit toolkit = managedForm.getToolkit();

        // Creating the Section
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.setText( Messages.getString( "PasswordPoliciesMasterDetailsBlock.AllPasswordPolicies" ) ); //$NON-NLS-1$
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite( section, SWT.WRAP );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout( layout );
        toolkit.paintBordersFor( client );
        section.setClient( client );

        // Creating the Table and Table Viewer
        Table table = toolkit.createTable( client, SWT.NULL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 2 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        final SectionPart spart = new SectionPart( section );
        managedForm.addPart( spart );
        viewer = new TableViewer( table );
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                managedForm.fireSelectionChanged( spart, event.getSelection() );
            }
        } );
        viewer.setContentProvider( new ArrayContentProvider() );
        viewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof PasswordPolicyBean )
                {
                    PasswordPolicyBean passwordPolicy = ( PasswordPolicyBean ) element;

                    if ( passwordPolicy.isEnabled() )
                    {
                        return NLS.bind( "{0} (enabled)", passwordPolicy.getPwdId() );
                    }
                    else
                    {
                        return NLS.bind( "{0} (disabled)", passwordPolicy.getPwdId() );
                    }
                }

                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof PasswordPolicyBean )
                {
                    PasswordPolicyBean passwordPolicy = ( PasswordPolicyBean ) element;

                    if ( PasswordPoliciesPage.isDefaultPasswordPolicy( passwordPolicy ) )
                    {
                        return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                            ApacheDS2ConfigurationPluginConstants.IMG_PASSWORD_POLICY_DEFAULT );
                    }
                    else
                    {
                        return ApacheDS2ConfigurationPlugin.getDefault().getImage(
                            ApacheDS2ConfigurationPluginConstants.IMG_PASSWORD_POLICY );
                    }
                }

                return super.getImage( element );
            }
        } );
        viewer.setComparator( new ViewerComparator()
        {
            public int compare( Viewer viewer, Object e1, Object e2 )
            {
                if ( ( e1 instanceof PasswordPolicyBean ) && ( e2 instanceof PasswordPolicyBean ) )
                {
                    PasswordPolicyBean passwordPolicy1 = ( PasswordPolicyBean ) e1;
                    PasswordPolicyBean passwordPolicy2 = ( PasswordPolicyBean ) e2;

                    String passwordPolicy1Id = passwordPolicy1.getPwdId();
                    String passwordPolicy2Id = passwordPolicy2.getPwdId();

                    if ( ( passwordPolicy1Id != null ) && ( passwordPolicy2Id != null ) )
                    {
                        return passwordPolicy1Id.compareTo( passwordPolicy2Id );
                    }
                }

                return super.compare( viewer, e1, e2 );
            }
        } );

        // Creating the button(s)
        addButton = toolkit.createButton( client,
            Messages.getString( "PasswordPoliciesMasterDetailsBlock.Add" ), SWT.PUSH ); //$NON-NLS-1$
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        deleteButton = toolkit.createButton( client,
            Messages.getString( "PasswordPoliciesMasterDetailsBlock.Delete" ), SWT.PUSH ); //$NON-NLS-1$
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        initFromInput();
        addListeners();
    }


    /**
     * Initializes the page with the Editor input.
     */
    private void initFromInput()
    {
        AuthenticationInterceptorBean authenticationInterceptor = getAuthenticationInterceptor();

        if ( authenticationInterceptor != null )
        {
            viewer.setInput( authenticationInterceptor.getPasswordPolicies() );
        }
        else
        {
            viewer.setInput( null );
        }
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        initFromInput();
        viewer.refresh();
    }


    /**
     * Gets the authentication interceptor.
     *
     * @return the authentication interceptor
     */
    private AuthenticationInterceptorBean getAuthenticationInterceptor()
    {
        // Looking for the authentication interceptor
        for ( InterceptorBean interceptor : page.getConfigBean().getDirectoryServiceBean().getInterceptors() )
        {
            if ( AUTHENTICATION_INTERCEPTOR_ID.equalsIgnoreCase( interceptor.getInterceptorId() )
                && ( interceptor instanceof AuthenticationInterceptorBean ) )
            {
                return ( AuthenticationInterceptorBean ) interceptor;
            }
        }

        return null;
    }


    /**
     * Add listeners to UI fields.
     */
    private void addListeners()
    {
        viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                viewer.refresh();

                // Getting the selection of the table viewer
                StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();

                // Delete button is enabled when something is selected
                deleteButton.setEnabled( !selection.isEmpty() );

                // Delete button is not enabled in the case of the system partition
                if ( !selection.isEmpty() )
                {
                    PasswordPolicyBean passwordPolicy = ( PasswordPolicyBean ) selection.getFirstElement();
                    if ( PasswordPoliciesPage.isDefaultPasswordPolicy( passwordPolicy ) )
                    {
                        deleteButton.setEnabled( false );
                    }
                }
            }
        } );

        addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addNewPasswordPolicy();
            }
        } );

        deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                deleteSelectedPasswordPolicy();
            }
        } );
    }


    /**
     * This method is called when the 'Add' button is clicked.
     */
    private void addNewPasswordPolicy()
    {
        // Getting a new ID for the password policy
        String newId = getNewId();

        // Creating and configuring the new password policy
        PasswordPolicyBean newPasswordPolicy = new PasswordPolicyBean();
        newPasswordPolicy.setPwdId( newId );
        newPasswordPolicy.setPwdMaxAge( 0 );
        newPasswordPolicy.setPwdFailureCountInterval( 30 );
        newPasswordPolicy.setPwdAttribute( "userPassword" );
        newPasswordPolicy.setPwdMaxFailure( 5 );
        newPasswordPolicy.setPwdLockout( true );
        newPasswordPolicy.setPwdMustChange( false );
        newPasswordPolicy.setPwdLockoutDuration( 0 );
        newPasswordPolicy.setPwdMinLength( 5 );
        newPasswordPolicy.setPwdInHistory( 5 );
        newPasswordPolicy.setPwdExpireWarning( 600 );
        newPasswordPolicy.setPwdMinAge( 0 );
        newPasswordPolicy.setPwdAllowUserChange( true );
        newPasswordPolicy.setPwdGraceAuthNLimit( 5 );
        newPasswordPolicy.setPwdCheckQuality( 1 );
        newPasswordPolicy.setPwdMaxLength( 0 );
        newPasswordPolicy.setPwdGraceExpire( 0 );
        newPasswordPolicy.setPwdMinDelay( 0 );
        newPasswordPolicy.setPwdMaxDelay( 0 );
        newPasswordPolicy.setPwdMaxIdle( 0 );

        // Adding the new password policy to the authentication interceptor
        getAuthenticationInterceptor().addPasswordPolicies( newPasswordPolicy );

        // Updating the UI and editor
        viewer.refresh();
        viewer.setSelection( new StructuredSelection( newPasswordPolicy ) );
        setEditorDirty();
    }


    /**
     * Gets a new ID for a new password policy.
     *
     * @return 
     *      a new ID for a new password policy
     */
    private String getNewId()
    {
        int counter = 1;
        String name = NEW_ID;
        boolean ok = false;

        while ( !ok )
        {
            ok = true;
            name = NEW_ID + counter;

            for ( PasswordPolicyBean passwordPolicy : getAuthenticationInterceptor().getPasswordPolicies() )
            {
                if ( passwordPolicy.getPwdId().equalsIgnoreCase( name ) )
                {
                    ok = false;
                }
            }
            counter++;
        }

        return name;
    }


    /**
     * This method is called when the 'Delete' button is clicked.
     */
    private void deleteSelectedPasswordPolicy()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        if ( !selection.isEmpty() )
        {
            PasswordPolicyBean passwordPolicy = ( PasswordPolicyBean ) selection.getFirstElement();
            if ( !PasswordPoliciesPage.isDefaultPasswordPolicy( passwordPolicy ) )
            {
                if ( MessageDialog
                    .openConfirm(
                        page.getManagedForm().getForm().getShell(),
                        Messages.getString( "PasswordPoliciesMasterDetailsBlock.ConfirmDelete" ), //$NON-NLS-1$
                        NLS.bind(
                            Messages.getString( "PasswordPoliciesMasterDetailsBlock.AreYouSureDeletePasswordPolicy" ), passwordPolicy.getPwdId() ) ) ) //$NON-NLS-1$
                {
                    getAuthenticationInterceptor().removePasswordPolicies( passwordPolicy );
                    setEditorDirty();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void registerPages( DetailsPart detailsPart )
    {
        detailsPage = new PasswordPolicyDetailsPage( this );
        detailsPart.registerPage( PasswordPolicyBean.class, detailsPage );
    }


    /**
     * {@inheritDoc}
     */
    protected void createToolBarActions( IManagedForm managedForm )
    {
        // No toolbar needed.
    }


    /**
     * Sets the Editor as dirty.
     */
    public void setEditorDirty()
    {
        ( ( ServerConfigurationEditor ) page.getEditor() ).setDirty( true );
        viewer.refresh();
    }


    /**
     * Saves the necessary elements to the input model.
     */
    public void save()
    {
        detailsPage.commit( true );
    }
}
