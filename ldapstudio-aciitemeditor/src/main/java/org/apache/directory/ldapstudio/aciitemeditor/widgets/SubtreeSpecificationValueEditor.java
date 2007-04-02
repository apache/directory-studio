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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.apache.directory.ldapstudio.aciitemeditor.ExclusionValueEditor;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;


/**
 * TODO SubtreeSpecificationValueEditor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SubtreeSpecificationValueEditor extends AbstractDialogStringValueEditor
{
    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogValueEditor#openDialog(org.eclipse.swt.widgets.Shell)
     */
    protected boolean openDialog( Shell shell )
    {
        SubtreeDialog dialog = new SubtreeDialog( shell );
        if ( dialog.open() == TextDialog.OK )
        {
            return true;
        }

        return false;
    }

    /**
     * TODO SubtreeDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class SubtreeDialog extends Dialog
    {
        /** The dialog title */
        public static final String DIALOG_TITLE = "Subtree Editor";

        private Spinner minimumSpinner;
        private int initialMinimum = 0;
        private Spinner maximumSpinner;
        private int initialMaximum = 0;

        private TableViewer exclusionsTableViewer;

        private Button exclusionsTableAddButton;

        private Button exclusionsTableEditButton;

        private Button exclusionsTableDeleteButton;

        private TableViewer refinementsTableViewer;

        private Button refinementsTableAddButton;

        private Button refinementsTableEditButton;

        private Button refinementsTableDeleteButton;


        /**
         * Creates a new instance of SubtreeDialog.
         *
         * @param parentShell
         */
        protected SubtreeDialog( Shell parentShell )
        {
            super( parentShell );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         */
        protected void configureShell( Shell newShell )
        {
            super.configureShell( newShell );
            newShell.setText( DIALOG_TITLE );
        }


        /* (non-Javadoc)
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        protected Control createDialogArea( Composite parent )
        {
            Composite composite = ( Composite ) super.createDialogArea( parent );
            GridData gd = new GridData( GridData.FILL_BOTH );
            gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
            composite.setLayoutData( gd );
            composite.setLayout( new GridLayout( 3, false ) );

            BaseWidgetUtils.createLabel( composite, "Base:", 3 );

            GridData spinnersGridData = new GridData();
            spinnersGridData.grabExcessHorizontalSpace = true;
            spinnersGridData.verticalAlignment = GridData.CENTER;
            spinnersGridData.horizontalSpan = 2;
            spinnersGridData.horizontalAlignment = GridData.BEGINNING;
            spinnersGridData.widthHint = 3 * 12;

            BaseWidgetUtils.createLabel( composite, "Minimum:", 1 );
            minimumSpinner = new Spinner( composite, SWT.BORDER );
            minimumSpinner.setMinimum( 0 );
            minimumSpinner.setMaximum( Integer.MAX_VALUE );
            minimumSpinner.setDigits( 0 );
            minimumSpinner.setIncrement( 1 );
            minimumSpinner.setPageIncrement( 100 );
            minimumSpinner.setSelection( initialMinimum );
            minimumSpinner.setLayoutData( spinnersGridData );

            BaseWidgetUtils.createLabel( composite, "Maximum:", 1 );
            maximumSpinner = new Spinner( composite, SWT.BORDER );
            maximumSpinner.setMinimum( 0 );
            maximumSpinner.setMaximum( Integer.MAX_VALUE );
            maximumSpinner.setDigits( 0 );
            maximumSpinner.setIncrement( 1 );
            maximumSpinner.setPageIncrement( 100 );
            maximumSpinner.setSelection( initialMaximum );
            maximumSpinner.setLayoutData( spinnersGridData );

            createExclusionsTable( composite );

            createRefinementsTable( composite );

            applyDialogFont( composite );
            return composite;
        }


        private void createExclusionsTable( Composite composite )
        {
            GridData tableGridData = new GridData();
            tableGridData.grabExcessHorizontalSpace = true;
            tableGridData.verticalAlignment = GridData.FILL;
            tableGridData.horizontalAlignment = GridData.FILL;
            tableGridData.heightHint = 100;

            BaseWidgetUtils.createLabel( composite, "Exclusions:", 1 );
            Table exclusionsTable = new Table( composite, SWT.BORDER );
            exclusionsTable.setHeaderVisible( false );
            exclusionsTable.setLayoutData( tableGridData );
            exclusionsTable.setLinesVisible( false );
            exclusionsTableViewer = new TableViewer( exclusionsTable );
            exclusionsTableViewer.setContentProvider( new ArrayContentProvider() );
            exclusionsTableViewer.setLabelProvider( new LabelProvider() );
            exclusionsTableViewer.setInput( new String[]
                { "chopBefore: \"ou=A\"", "chopAfter: \"ou=A\"" } );
            exclusionsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
            {
                public void selectionChanged( SelectionChangedEvent event )
                {
                    valueSelectedExclusionsTable();
                }
            } );
            exclusionsTableViewer.addDoubleClickListener( new IDoubleClickListener()
            {
                public void doubleClick( DoubleClickEvent event )
                {
                    editValueExclusionsTable();
                }
            } );

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.CENTER;
            gridData.grabExcessHorizontalSpace = false;
            gridData.grabExcessVerticalSpace = false;
            gridData.verticalAlignment = GridData.FILL;

            Composite buttonComposite = new Composite( composite, SWT.NONE );
            buttonComposite.setLayoutData( gridData );
            buttonComposite.setLayout( gridLayout );

            GridData buttonGridData = new GridData();
            buttonGridData.horizontalAlignment = GridData.FILL;
            buttonGridData.grabExcessHorizontalSpace = false;
            buttonGridData.verticalAlignment = GridData.BEGINNING;
            buttonGridData.widthHint = Activator.getButtonWidth( composite );

            exclusionsTableAddButton = new Button( buttonComposite, SWT.PUSH );
            exclusionsTableAddButton.setText( "Add..." );
            exclusionsTableAddButton.setLayoutData( buttonGridData );
            exclusionsTableAddButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    addValueExclusionsTable();
                }
            } );

            exclusionsTableEditButton = new Button( buttonComposite, SWT.PUSH );
            exclusionsTableEditButton.setText( "Edit..." );
            exclusionsTableEditButton.setLayoutData( buttonGridData );
            exclusionsTableEditButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    editValueExclusionsTable();
                }
            } );
            exclusionsTableEditButton.setEnabled( false );

            exclusionsTableDeleteButton = new Button( buttonComposite, SWT.PUSH );
            exclusionsTableDeleteButton.setText( "Delete" );
            exclusionsTableDeleteButton.setLayoutData( buttonGridData );
            exclusionsTableDeleteButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    deleteValueExclusionsTable();
                }
            } );
            exclusionsTableDeleteButton.setEnabled( false );
        }


        /**
         * TODO valueSelectedExclusionsTable.
         *
         */
        private void valueSelectedExclusionsTable()
        {
            String value = getSelectedValueExclusionsTable();

            if ( value == null )
            {
                exclusionsTableEditButton.setEnabled( false );
                exclusionsTableDeleteButton.setEnabled( false );
            }
            else
            {
                exclusionsTableEditButton.setEnabled( true );
                exclusionsTableDeleteButton.setEnabled( true );
            }
        }


        /**
         * TODO getSelectedValue.
         *
         * @return
         */
        private String getSelectedValueExclusionsTable()
        {
            String value = null;

            IStructuredSelection selection = ( IStructuredSelection ) exclusionsTableViewer.getSelection();
            if ( !selection.isEmpty() )
            {
                Object element = selection.getFirstElement();
                if ( element instanceof String )
                {
                    value = ( String ) element;
                }
            }

            return value;
        }


        /**
         * TODO addValueExclusionsTable.
         *
         */
        private void addValueExclusionsTable()
        {
            ExclusionValueEditor valueEditor = new ExclusionValueEditor();

            Object oldRawValue = valueEditor.getRawValue( null, "" );

            CellEditor cellEditor = valueEditor.getCellEditor();
            cellEditor.setValue( oldRawValue );
            cellEditor.activate();
        }


        /**
         * TODO editValueExclusionsTable.
         *
         */
        private void editValueExclusionsTable()
        {
            ExclusionValueEditor valueEditor = new ExclusionValueEditor();
            
            String oldValue = getSelectedValueExclusionsTable();
            if ( oldValue != null )
            {
                Object oldRawValue = valueEditor.getRawValue( null, oldValue );
                
                CellEditor cellEditor = valueEditor.getCellEditor();
                cellEditor.setValue( oldRawValue );
                cellEditor.activate();
                Object newRawValue = cellEditor.getValue();
                
                if(newRawValue != null) 
                {
                    String newValue = (String) valueEditor.getStringOrBinaryValue( newRawValue );
                    
//                    values.remove( oldValue );
//                    values.add( newValue );
//                    tableViewer.refresh();
                }
            }
        }


        /**
         * TODO deleteValueExclusionsTable.
         *
         */
        private void deleteValueExclusionsTable()
        {
            // TODO Auto-generated method stub

        }


        private void createRefinementsTable( Composite composite )
        {
            GridData tableGridData = new GridData();
            tableGridData.grabExcessHorizontalSpace = true;
            tableGridData.verticalAlignment = GridData.FILL;
            tableGridData.horizontalAlignment = GridData.FILL;
            tableGridData.heightHint = 100;

            BaseWidgetUtils.createLabel( composite, "Refinements:", 1 );
            Table refinementsTable = new Table( composite, SWT.BORDER );
            refinementsTable.setHeaderVisible( false );
            refinementsTable.setLayoutData( tableGridData );
            refinementsTable.setLinesVisible( false );
            refinementsTableViewer = new TableViewer( refinementsTable );
            refinementsTableViewer.setContentProvider( new ArrayContentProvider() );
            refinementsTableViewer.setLabelProvider( new LabelProvider() );
            refinementsTableViewer.setInput( new String[]
                { "and:{ item:35.5.2.1, item:inetOrgPerson }" } );
            refinementsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
            {
                public void selectionChanged( SelectionChangedEvent event )
                {
                    valueSelectedRefinementsTable();
                }
            } );

            refinementsTableViewer.addDoubleClickListener( new IDoubleClickListener()
            {
                public void doubleClick( DoubleClickEvent event )
                {
                    editValueRefinementsTable();
                }
            } );

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.CENTER;
            gridData.grabExcessHorizontalSpace = false;
            gridData.grabExcessVerticalSpace = false;
            gridData.verticalAlignment = GridData.FILL;

            Composite buttonComposite = new Composite( composite, SWT.NONE );
            buttonComposite.setLayoutData( gridData );
            buttonComposite.setLayout( gridLayout );

            GridData buttonGridData = new GridData();
            buttonGridData.horizontalAlignment = GridData.FILL;
            buttonGridData.grabExcessHorizontalSpace = false;
            buttonGridData.verticalAlignment = GridData.BEGINNING;
            buttonGridData.widthHint = Activator.getButtonWidth( composite );

            refinementsTableAddButton = new Button( buttonComposite, SWT.PUSH );
            refinementsTableAddButton.setText( "Add..." );
            refinementsTableAddButton.setLayoutData( buttonGridData );
            refinementsTableAddButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    addValueRefinementsTable();
                }
            } );

            refinementsTableEditButton = new Button( buttonComposite, SWT.PUSH );
            refinementsTableEditButton.setText( "Edit..." );
            refinementsTableEditButton.setLayoutData( buttonGridData );
            refinementsTableEditButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    editValueRefinementsTable();
                }
            } );
            refinementsTableEditButton.setEnabled( false );

            refinementsTableDeleteButton = new Button( buttonComposite, SWT.PUSH );
            refinementsTableDeleteButton.setText( "Delete" );
            refinementsTableDeleteButton.setLayoutData( buttonGridData );
            refinementsTableDeleteButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    deleteValueRefinementsTable();
                }
            } );
            refinementsTableDeleteButton.setEnabled( false );
        }


        /**
         * TODO valueSelectedExclusionsTable.
         *
         */
        private void valueSelectedRefinementsTable()
        {
            String value = getSelectedValueRefinementsTable();

            if ( value == null )
            {
                refinementsTableEditButton.setEnabled( false );
                refinementsTableDeleteButton.setEnabled( false );
            }
            else
            {
                refinementsTableEditButton.setEnabled( true );
                refinementsTableDeleteButton.setEnabled( true );
            }
        }


        /**
         * TODO getSelectedValue.
         *
         * @return
         */
        private String getSelectedValueRefinementsTable()
        {
            String value = null;

            IStructuredSelection selection = ( IStructuredSelection ) refinementsTableViewer.getSelection();
            if ( !selection.isEmpty() )
            {
                Object element = selection.getFirstElement();
                if ( element instanceof String )
                {
                    value = ( String ) element;
                }
            }

            return value;
        }


        private void addValueRefinementsTable()
        {
            // TODO Auto-generated method stub

        }


        private void editValueRefinementsTable()
        {
            // TODO Auto-generated method stub

        }


        private void deleteValueRefinementsTable()
        {
        }

    }
}
