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
import org.apache.directory.studio.openldap.config.editor.dialogs.ValueSortingValueDialog;
import org.apache.directory.studio.openldap.config.model.OlcValSortConfig;


/**
 * This class implements a block for the configuration of the Value Sorting overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueSortingOverlayConfigurationBlock extends
    AbstractOverlayDialogConfigurationBlock<OlcValSortConfig>
{
    /** The value sorts list */
    private List<String> valueSorts = new ArrayList<String>();

    // UI widgets
    private TableViewer valueSortsTableViewer;
    private Button addValueSortButton;
    private Button editValueSortButton;
    private Button deleteValueSortButton;

    // Listeners
    private ISelectionChangedListener valueSortsTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteValueSortButton.setEnabled( !valueSortsTableViewer.getSelection().isEmpty() );
        }
    };
    private IDoubleClickListener valueSortsTableViewerDoubleClickListener = new IDoubleClickListener()
    {
        public void doubleClick( DoubleClickEvent event )
        {
            editValueSortButtonAction();
        }
    };
    private SelectionListener addValueSortButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ValueSortingValueDialog dialog = new ValueSortingValueDialog( addValueSortButton.getShell(),
                browserConnection, "" );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String value = dialog.getValue();

                valueSorts.add( value );
                valueSortsTableViewer.refresh();
                valueSortsTableViewer.setSelection( new StructuredSelection( value ) );
            }
        }
    };
    private SelectionListener editValueSortButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            editValueSortButtonAction();
        }
    };
    private SelectionListener deleteValueSortButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) valueSortsTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                valueSorts.remove( selectedAttribute );
                valueSortsTableViewer.refresh();
            }
        }
    };


    public ValueSortingOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection )
    {
        super( dialog, connection );
        setOverlay( new OlcValSortConfig() );
    }


    public ValueSortingOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection,
        OlcValSortConfig overlay )
    {
        super( dialog, connection );
        if ( overlay == null )
        {
            overlay = new OlcValSortConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Value Sorts
        BaseWidgetUtils.createLabel( composite, "Value Sorts:", 1 );
        Composite valueSortsComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // Value Sorts TableViewer
        valueSortsTableViewer = new TableViewer( valueSortsComposite );
        GridData tableViewerGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        tableViewerGridData.heightHint = 20;
        tableViewerGridData.widthHint = 100;
        valueSortsTableViewer.getControl().setLayoutData( tableViewerGridData );
        valueSortsTableViewer.setContentProvider( new ArrayContentProvider() );
        valueSortsTableViewer.setInput( valueSorts );
        valueSortsTableViewer.addSelectionChangedListener( valueSortsTableViewerSelectionChangedListener );
        valueSortsTableViewer.addDoubleClickListener( valueSortsTableViewerDoubleClickListener );

        // Value Sort Add Button
        addValueSortButton = BaseWidgetUtils.createButton( valueSortsComposite, "Add...", 1 );
        addValueSortButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addValueSortButton.addSelectionListener( addValueSortButtonSelectionListener );

        // Value Sort Add Button
        editValueSortButton = BaseWidgetUtils.createButton( valueSortsComposite, "Edit...", 1 );
        editValueSortButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        editValueSortButton.addSelectionListener( editValueSortButtonSelectionListener );

        // Value Sort Delete Button
        deleteValueSortButton = BaseWidgetUtils.createButton( valueSortsComposite, "Delete", 1 );
        deleteValueSortButton.setEnabled( false );
        deleteValueSortButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteValueSortButton.addSelectionListener( deleteValueSortButtonSelectionListener );
    }


    /**
     * Action launched when the edit value sort button is clicked, or
     * when the value sorts table viewer is double-clicked.
     */
    private void editValueSortButtonAction()
    {
        StructuredSelection selection = ( StructuredSelection ) valueSortsTableViewer.getSelection();

        if ( !selection.isEmpty() )
        {
            String selectedValueSort = ( String ) selection.getFirstElement();

            ValueSortingValueDialog dialog = new ValueSortingValueDialog( addValueSortButton.getShell(),
                browserConnection, selectedValueSort );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String value = dialog.getValue();

                int index = valueSorts.indexOf( selectedValueSort );
                valueSorts.remove( selectedValueSort );
                valueSorts.add( index, value );
                valueSortsTableViewer.refresh();
                valueSortsTableViewer.setSelection( new StructuredSelection( value ) );
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
            valueSorts.clear();

            List<String> olcValSortAttr = overlay.getOlcValSortAttr();

            if ( olcValSortAttr != null )
            {
                for ( String value : olcValSortAttr )
                {
                    valueSorts.add( value );
                }
            }

            valueSortsTableViewer.refresh();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            overlay.clearOlcValSortAttr();

            for ( String valueSort : valueSorts )
            {
                overlay.addOlcValSortAttr( valueSort );
            }
        }
    }
}
