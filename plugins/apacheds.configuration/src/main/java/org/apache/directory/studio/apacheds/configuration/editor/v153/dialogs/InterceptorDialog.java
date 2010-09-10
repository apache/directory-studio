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
package org.apache.directory.studio.apacheds.configuration.editor.v153.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.model.v153.InterceptorEnum;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Dialog for Interceptor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InterceptorDialog extends Dialog
{
    /** The initial interceptors list */
    private List<InterceptorEnum> initialInterceptors;

    /** The available interceptors list */
    private List<InterceptorEnum> availableInterceptors;

    /** The selected interceptor */
    private InterceptorEnum selectedInterceptor;

    // UI Fields
    private Table interceptorsTable;
    private TableViewer interceptorsTableViewer;
    private Button addButton;


    /**
     * Creates a new instance of InterceptorDialog.
     */
    public InterceptorDialog( List<InterceptorEnum> interceptors )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.initialInterceptors = interceptors;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Add An Interceptor" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        // Choose Label
        Label chooseLabel = new Label( composite, SWT.NONE );
        chooseLabel.setText( "Choose an interceptor:" );
        chooseLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Interceptors Table Viewer
        interceptorsTable = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.heightHint = 148;
        gridData.minimumHeight = 148;
        gridData.widthHint = 350;
        gridData.minimumWidth = 350;
        interceptorsTable.setLayoutData( gridData );
        interceptorsTable.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( interceptorsTable.getSelectionIndex() != -1 )
                {
                    okPressed();
                }
            }
        } );

        interceptorsTableViewer = new TableViewer( interceptorsTable );
        interceptorsTableViewer.setContentProvider( new ArrayContentProvider() );
        interceptorsTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return ApacheDSConfigurationPlugin.getDefault().getImage(
                    ApacheDSConfigurationPluginConstants.IMG_INTERCEPTOR );
            }


            public String getText( Object element )
            {
                if ( element instanceof InterceptorEnum )
                {
                    return ( ( InterceptorEnum ) element ).getName();

                }

                return super.getText( element );
            }
        } );
        interceptorsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) interceptorsTableViewer.getSelection();
                if ( selection.isEmpty() )
                {
                    if ( ( addButton != null ) && ( !addButton.isDisposed() ) )
                    {
                        addButton.setEnabled( false );
                    }
                }
                else
                {
                    if ( ( addButton != null ) && ( !addButton.isDisposed() ) )
                    {
                        addButton.setEnabled( true );
                    }
                }
            }
        } );

        initFromInput();

        return composite;
    }


    /**
     * Initializes the UI from the input.
     */
    private void initFromInput()
    {
        // Creating the available interceptors list
        availableInterceptors = new ArrayList<InterceptorEnum>();
        if ( !initialInterceptors.contains( InterceptorEnum.NORMALIZATION ) )
        {
            availableInterceptors.add( InterceptorEnum.NORMALIZATION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.AUTHENTICATION ) )
        {
            availableInterceptors.add( InterceptorEnum.AUTHENTICATION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.REFERRAL ) )
        {
            availableInterceptors.add( InterceptorEnum.REFERRAL );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.ACI_AUTHORIZATION ) )
        {
            availableInterceptors.add( InterceptorEnum.ACI_AUTHORIZATION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.DEFAULT_AUTHORIZATION ) )
        {
            availableInterceptors.add( InterceptorEnum.DEFAULT_AUTHORIZATION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.EXCEPTION ) )
        {
            availableInterceptors.add( InterceptorEnum.EXCEPTION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.OPERATIONAL_ATTRIBUTE ) )
        {
            availableInterceptors.add( InterceptorEnum.OPERATIONAL_ATTRIBUTE );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.PASSWORD_POLICY ) )
        {
            availableInterceptors.add( InterceptorEnum.PASSWORD_POLICY );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.KEY_DERIVATION ) )
        {
            availableInterceptors.add( InterceptorEnum.KEY_DERIVATION );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.SCHEMA ) )
        {
            availableInterceptors.add( InterceptorEnum.SCHEMA );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.SUBENTRY ) )
        {
            availableInterceptors.add( InterceptorEnum.SUBENTRY );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.COLLECTIVE_ATTRIBUTE ) )
        {
            availableInterceptors.add( InterceptorEnum.COLLECTIVE_ATTRIBUTE );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.EVENT ) )
        {
            availableInterceptors.add( InterceptorEnum.EVENT );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.TRIGGER ) )
        {
            availableInterceptors.add( InterceptorEnum.TRIGGER );
        }
        if ( !initialInterceptors.contains( InterceptorEnum.REPLICATION ) )
        {
            availableInterceptors.add( InterceptorEnum.REPLICATION );
        }

        // Setting the input
        interceptorsTableViewer.setInput( availableInterceptors );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        addButton = createButton( parent, IDialogConstants.OK_ID, "Add", true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        addButton.setEnabled( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        StructuredSelection selection = ( StructuredSelection ) interceptorsTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            selectedInterceptor = ( InterceptorEnum ) selection.getFirstElement();
        }

        super.okPressed();
    }


    /**
     * Gets the interceptor.
     *
     * @return
     *      the interceptor
     */
    public InterceptorEnum getInterceptor()
    {
        return selectedInterceptor;
    }
}
