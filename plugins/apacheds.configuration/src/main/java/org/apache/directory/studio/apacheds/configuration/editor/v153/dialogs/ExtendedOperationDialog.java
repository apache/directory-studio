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
import org.apache.directory.studio.apacheds.configuration.model.v153.ExtendedOperationEnum;
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
 * This class implements the Dialog for Extended Operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedOperationDialog extends Dialog
{
    /** The initial extended operations list */
    private List<ExtendedOperationEnum> initialExtendedOperations;

    /** The available extended operations list */
    private List<ExtendedOperationEnum> availableExtendedOperations;

    /** The selected extended operation */
    private ExtendedOperationEnum selectedExtendedOperation;

    // UI Fields
    private Table extendedOperationsTable;
    private TableViewer extendedOperationsTableViewer;
    private Button addButton;


    /**
     * Creates a new instance of ExtendedOperationDialog.
     */
    public ExtendedOperationDialog( List<ExtendedOperationEnum> extendedOperations )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.initialExtendedOperations = extendedOperations;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Add An Extended Operation" );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        // Choose Label
        Label chooseLabel = new Label( composite, SWT.NONE );
        chooseLabel.setText( "Choose an extended operation:" );
        chooseLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Interceptors Table Viewer
        extendedOperationsTable = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.heightHint = 148;
        gridData.minimumHeight = 148;
        gridData.widthHint = 350;
        gridData.minimumWidth = 350;
        extendedOperationsTable.setLayoutData( gridData );
        extendedOperationsTable.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( extendedOperationsTable.getSelectionIndex() != -1 )
                {
                    okPressed();
                }
            }
        } );

        extendedOperationsTableViewer = new TableViewer( extendedOperationsTable );
        extendedOperationsTableViewer.setContentProvider( new ArrayContentProvider() );
        extendedOperationsTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return ApacheDSConfigurationPlugin.getDefault().getImage(
                    ApacheDSConfigurationPluginConstants.IMG_EXTENDED_OPERATION );
            }


            public String getText( Object element )
            {
                if ( element instanceof ExtendedOperationEnum )
                {
                    return ( ( ExtendedOperationEnum ) element ).getName();

                }

                return super.getText( element );
            }
        } );
        extendedOperationsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) extendedOperationsTableViewer.getSelection();
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
        // Creating the available extended operations list
        availableExtendedOperations = new ArrayList<ExtendedOperationEnum>();
        if ( !initialExtendedOperations.contains( ExtendedOperationEnum.START_TLS ) )
        {
            availableExtendedOperations.add( ExtendedOperationEnum.START_TLS );
        }
        if ( !initialExtendedOperations.contains( ExtendedOperationEnum.GRACEFUL_SHUTDOWN ) )
        {
            availableExtendedOperations.add( ExtendedOperationEnum.GRACEFUL_SHUTDOWN );
        }
        if ( !initialExtendedOperations.contains( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI ) )
        {
            availableExtendedOperations.add( ExtendedOperationEnum.LAUNCH_DIAGNOSTIC_UI );
        }

        // Setting the input
        extendedOperationsTableViewer.setInput( availableExtendedOperations );
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        addButton = createButton( parent, IDialogConstants.OK_ID, "Add", true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        addButton.setEnabled( false );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        StructuredSelection selection = ( StructuredSelection ) extendedOperationsTableViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            selectedExtendedOperation = ( ExtendedOperationEnum ) selection.getFirstElement();
        }

        super.okPressed();
    }


    /**
     * Gets the extended operation.
     *
     * @return
     *      the extended operation
     */
    public ExtendedOperationEnum getExtendedOperation()
    {
        return selectedExtendedOperation;
    }
}
