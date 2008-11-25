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
package org.apache.directory.studio.schemaeditor.view.dialogs;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.search.SearchPage;
import org.apache.directory.studio.schemaeditor.view.search.SearchPage.SearchInEnum;
import org.apache.directory.studio.schemaeditor.view.views.SearchView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


/**
 * This dialog is used to display the previous searches.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PreviousSearchesDialog extends Dialog
{
    /** The associated view */
    private SearchView view;

    // UI Fields
    private TableViewer tableViewer;
    private Button openButton;
    private Button removeButton;


    /**
     * Creates a new instance of PreviousSearchesDialog.
     */
    public PreviousSearchesDialog( SearchView view )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString("PreviousSearchesDialog.Previous") );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        Label label = new Label( composite, SWT.NONE );
        label.setText( Messages.getString("PreviousSearchesDialog.ShowResultsInView") );
        label.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        tableViewer = new TableViewer( composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.widthHint = 300;
        gd.heightHint = 200;
        tableViewer.getTable().setLayoutData( gd );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_SEARCH_HISTORY_ITEM );
            }
        } );
        tableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                openButton.setEnabled( !event.getSelection().isEmpty() );
                removeButton.setEnabled( !event.getSelection().isEmpty() );
            }
        } );
        tableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                buttonPressed( IDialogConstants.OK_ID );
            }
        } );

        removeButton = new Button( composite, SWT.NONE );
        removeButton.setText( Messages.getString("PreviousSearchesDialog.Remove") );
        removeButton.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false ) );
        removeButton.setEnabled( false );
        removeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();
                String selectedSearch = ( String ) selection.getFirstElement();
                SearchPage.removeSearchStringHistory( selectedSearch );
                initTableViewer();
            }
        } );

        initTableViewer();

        return composite;
    }


    /**
     * Initializes the TableViewer.
     */
    private void initTableViewer()
    {
        tableViewer.setInput( SearchPage.loadSearchStringHistory() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
        openButton = createButton( parent, IDialogConstants.OK_ID, Messages.getString("PreviousSearchesDialog.Open"), true );
        openButton.setEnabled( false );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            if ( !tableViewer.getSelection().isEmpty() )
            {
                StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();
                String selectedSearch = ( String ) selection.getFirstElement();

                view.setSearchInput( selectedSearch, SearchPage.loadSearchIn().toArray( new SearchInEnum[0] ),
                    SearchPage.loadScope() );
            }
        }

        super.buttonPressed( buttonId );
    }
}
