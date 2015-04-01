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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.editor.dialogs.RwmMappingDialog;
import org.apache.directory.studio.openldap.config.model.OlcRwmConfig;


/**
 * This class implements a block for the configuration of the Audit Log overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RewriteRemapOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcRwmConfig>
{
    /** The mappings list */
    private List<String> mappings = new ArrayList<String>();

    // UI widgets
    private TableViewer mappingsTableViewer;
    private Button addMappingButton;
    private Button editMappingButton;
    private Button deleteMappingButton;

    // Listeners
    private ISelectionChangedListener mappingsTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteMappingButton.setEnabled( !mappingsTableViewer.getSelection().isEmpty() );
        }
    };
    private IDoubleClickListener mappingsTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editMappingButtonAction();
        }
    };
    private SelectionListener addMappingButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            RwmMappingDialog dialog = new RwmMappingDialog( addMappingButton.getShell(), browserConnection, "" );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String value = dialog.getValue();

                mappings.add( value );
                mappingsTableViewer.refresh();
                mappingsTableViewer.setSelection( new StructuredSelection( value ) );
            }
        }
    };
    private SelectionListener editMappingButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editMappingButtonAction();
        }
    };
    private SelectionListener deleteMappingButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mappingsTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                mappings.remove( selectedAttribute );
                mappingsTableViewer.refresh();
            }
        }
    };


    public RewriteRemapOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection )
    {
        super( dialog, connection );
        setOverlay( new OlcRwmConfig() );
    }


    public RewriteRemapOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection,
        OlcRwmConfig overlay )
    {
        super( dialog, connection );
        if ( overlay == null )
        {
            overlay = new OlcRwmConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Mappings
        BaseWidgetUtils.createLabel( composite, "Mappings:", 1 );
        Composite mappingsComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // Mappings TableViewer
        mappingsTableViewer = new TableViewer( mappingsComposite );
        GridData tableViewerGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        tableViewerGridData.heightHint = 20;
        tableViewerGridData.widthHint = 100;
        mappingsTableViewer.getControl().setLayoutData( tableViewerGridData );
        mappingsTableViewer.setContentProvider( new ArrayContentProvider() );
        mappingsTableViewer.setInput( mappings );
        mappingsTableViewer.addSelectionChangedListener( mappingsTableViewerSelectionChangedListener );
        mappingsTableViewer.addDoubleClickListener( mappingsTableViewerDoubleClickListener );

        // Mapping Add Button
        addMappingButton = BaseWidgetUtils.createButton( mappingsComposite, "Add...", 1 );
        addMappingButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addMappingButton.addSelectionListener( addMappingButtonSelectionListener );

        // Mapping Add Button
        editMappingButton = BaseWidgetUtils.createButton( mappingsComposite, "Edit...", 1 );
        editMappingButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editMappingButton.addSelectionListener( editMappingButtonSelectionListener );

        // Mapping Delete Button
        deleteMappingButton = BaseWidgetUtils.createButton( mappingsComposite, "Delete", 1 );
        deleteMappingButton.setEnabled( false );
        deleteMappingButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteMappingButton.addSelectionListener( deleteMappingButtonSelectionListener );
    }


    /**
     * Action launched when the edit mapping button is clicked, or
     * when the value sorts table viewer is double-clicked.
     */
    private void editMappingButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) mappingsTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedMapping = ( String ) selection.getFirstElement();

            RwmMappingDialog dialog = new RwmMappingDialog( addMappingButton.getShell(),
                browserConnection, selectedMapping );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String value = dialog.getValue();

                int index = mappings.indexOf( selectedMapping );
                mappings.remove( selectedMapping );
                mappings.add( index, value );
                mappingsTableViewer.refresh();
                mappingsTableViewer.setSelection( new StructuredSelection( value ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            mappings.clear();

            List<String> olcRwmMap = overlay.getOlcRwmMap();

            if ( olcRwmMap != null )
            {
                for ( String value : olcRwmMap )
                {
                    mappings.add( value );
                }
            }

            mappingsTableViewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            overlay.clearOlcRwmMap();

            for ( String mapping : mappings )
            {
                overlay.addOlcRwmMap( mapping );
            }
        }
    }
}
