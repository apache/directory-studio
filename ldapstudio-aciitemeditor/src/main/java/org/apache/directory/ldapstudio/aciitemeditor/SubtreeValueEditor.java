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
package org.apache.directory.ldapstudio.aciitemeditor;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.subtree.SubtreeSpecification;
import org.apache.directory.shared.ldap.subtree.SubtreeSpecificationParser;
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
 * ACI item editor specific value editor to edit the SubtreeSpecification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SubtreeValueEditor extends AbstractDialogStringValueEditor
{
    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogValueEditor#openDialog(org.eclipse.swt.widgets.Shell)
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof SubtreeSpecificationValueWrapper )
        {
            SubtreeSpecificationValueWrapper wrapper = ( SubtreeSpecificationValueWrapper ) value;
            SubtreeSpecificationDialog dialog = new SubtreeSpecificationDialog( shell, wrapper.connection,
                wrapper.subtreeSpecification );
            if ( dialog.open() == TextDialog.OK )
            {
                String base = dialog.getBase();
                int minimum = dialog.getMinimum();
                int maximum = dialog.getMaximum();
                List<String> exclusions = dialog.getExclusions();

                StringBuffer sb = new StringBuffer();
                sb.append( "{" );

                // Adding base
                if ( base != null )
                {
                    sb.append( " base \"" + base + "\"," );
                }

                // Adding Minimum
                if ( minimum != 0 )
                {
                    sb.append( " minimum " + minimum + "," );
                }

                // Adding Maximum
                if ( maximum != 0 )
                {
                    sb.append( " maximum " + maximum + "," );
                }

                // Adding Exclusions
                if ( !exclusions.isEmpty() )
                {
                    sb.append( " specificExclusions {" );

                    for ( Iterator<String> it = exclusions.iterator(); it.hasNext(); )
                    {
                        sb.append( " " + it.next() );

                        if ( it.hasNext() )
                        {
                            sb.append( "," );
                        }
                    }

                    sb.append( " }," );
                }

                // Removing the last ','
                if ( sb.charAt( sb.length() - 1 ) == ',' )
                {
                    sb.deleteCharAt( sb.length() - 1 );
                }

                sb.append( " }" );

                setValue( sb.toString() );

                return true;
            }
        }
        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractDialogStringValueEditor#getRawValue(org.apache.directory.ldapstudio.browser.core.model.IConnection, java.lang.Object)
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        Object o = super.getRawValue( connection, value );
        if ( o != null && o instanceof String )
        {
            SubtreeSpecificationParser parser = new SubtreeSpecificationParser( null );
            try
            {
                SubtreeSpecification subtreeSpecification = parser.parse( ( String ) value );
                if ( subtreeSpecification != null )
                {
                    return new SubtreeSpecificationValueWrapper( connection, subtreeSpecification );
                }
            }
            catch ( ParseException e1 )
            {
                return new SubtreeSpecificationValueWrapper( connection, null );
            }
        }

        return null;
    }

    /**
     * This class provides a dialog to enter the Subtree Specification value.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class SubtreeSpecificationDialog extends Dialog
    {
        /** The dialog title */
        private static final String DIALOG_TITLE = "Subtree Editor";

        /** The connection */
        private IConnection connection;

        /** The SubtreeSpecification */
        private SubtreeSpecification subtreeSpecification;

        private int initialMaximum = 0;
        private int initialMinimum = 0;

        /** The return Base */
        private String returnBase;

        /** The return Minimum */
        private int returnMinimum;

        /** The return Maximum */
        private int returnMaximum;

        /** The Exclusions List */
        private List<String> exclusions;

        // UI Fields
        private EntryWidget entryWidget;
        private Spinner minimumSpinner;
        private Spinner maximumSpinner;
        private TableViewer exclusionsTableViewer;
        private Button exclusionsTableAddButton;
        private Button exclusionsTableEditButton;
        private Button exclusionsTableDeleteButton;


        /**
         * Creates a new instance of SubtreeSpecificationDialog.
         *
         * @param shell
         *      the shell to use
         * @param connection
         *      the connection to use
         * @param subtreeSpecification
         *      the SubtreeSpecification
         */
        private SubtreeSpecificationDialog( Shell shell, IConnection connection,
            SubtreeSpecification subtreeSpecification )
        {
            super( shell );
            this.connection = connection;
            this.subtreeSpecification = subtreeSpecification;
            exclusions = new ArrayList<String>();
            if ( subtreeSpecification != null )
            {
                Set chopBeforeExclusions = subtreeSpecification.getChopBeforeExclusions();
                for ( Object chopBeforeExclusion : chopBeforeExclusions )
                {
                    LdapDN dn = ( LdapDN ) chopBeforeExclusion;
                    exclusions.add( "chopBefore: \"" + dn.toNormName() + "\"" );
                }

                Set chopAfterExclusions = subtreeSpecification.getChopAfterExclusions();
                for ( Object chopAfterExclusion : chopAfterExclusions )
                {
                    LdapDN dn = ( LdapDN ) chopAfterExclusion;
                    exclusions.add( "chopAfter: \"" + dn.toNormName() + "\"" );
                }
            }
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
         * @see org.eclipse.jface.dialogs.Dialog#okPressed()
         */
        protected void okPressed()
        {
            returnBase = entryWidget.getDn().toString();
            returnMinimum = minimumSpinner.getSelection();
            returnMaximum = maximumSpinner.getSelection();
            super.okPressed();
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

            BaseWidgetUtils.createLabel( composite, "Base:", 1 );
            entryWidget = new EntryWidget( connection, null );
            entryWidget.createWidget( composite );

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

            applyDialogFont( composite );

            initFromInput();

            return composite;
        }


        /**
         * Initializes the Value Editor from the input.
         */
        private void initFromInput()
        {
            DN dn = null;
            try
            {
                dn = new DN( subtreeSpecification.getBase().toNormName() );
            }
            catch ( NameException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            entryWidget.setInput( connection, dn );
            minimumSpinner.setSelection( subtreeSpecification.getMinBaseDistance() );
            maximumSpinner.setSelection( subtreeSpecification.getMaxBaseDistance() );
            exclusionsTableViewer.setInput( exclusions );
        }


        /**
         * Creates the Exclusions Table.
         *
         * @param composite
         *      the composite
         */
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
         * Called when value is selected in Exclusions table viewer.
         * Updates the enabled/disabled state of the buttons.
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
         * Retuns the current selection in the Exclusions table viewer.
         * 
         * @return
         *      the value that is selected in the Exclusions table viewer, or null.
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
         * Opens the editor and adds the new Exclusion value to the list.
         */
        private void addValueExclusionsTable()
        {
            ExclusionValueEditor valueEditor = new ExclusionValueEditor();

            Object oldRawValue = valueEditor.getRawValue( connection, "" );

            CellEditor cellEditor = valueEditor.getCellEditor();
            cellEditor.setValue( oldRawValue );
            cellEditor.activate();
            Object newRawValue = cellEditor.getValue();

            if ( newRawValue != null )
            {
                String newValue = ( String ) valueEditor.getStringOrBinaryValue( newRawValue );

                exclusions.add( newValue );
                exclusionsTableViewer.refresh();
            }
        }


        /**
         * Opens the editor with the currently selected Exclusion
         * value and puts the modified value into the list.
         */
        private void editValueExclusionsTable()
        {
            ExclusionValueEditor valueEditor = new ExclusionValueEditor();

            String oldValue = getSelectedValueExclusionsTable();
            if ( oldValue != null )
            {
                Object oldRawValue = valueEditor.getRawValue( connection, oldValue );

                CellEditor cellEditor = valueEditor.getCellEditor();
                cellEditor.setValue( oldRawValue );
                cellEditor.activate();
                Object newRawValue = cellEditor.getValue();

                if ( newRawValue != null )
                {
                    String newValue = ( String ) valueEditor.getStringOrBinaryValue( newRawValue );

                    exclusions.remove( oldValue );
                    exclusions.add( newValue );
                    exclusionsTableViewer.refresh();
                }
            }
        }


        /**
         * Deletes the currently selected Exclusion value from list.
         */
        private void deleteValueExclusionsTable()
        {
            String value = getSelectedValueExclusionsTable();
            if ( value != null )
            {
                exclusions.remove( value );
                exclusionsTableViewer.refresh();
            }
        }


        /**
         * Gets the Base value.
         *
         * @return
         *      the base
         */
        public String getBase()
        {
            return returnBase;
        }


        /**
         * Gets the Minimum Value.
         *
         * @return
         *      the miminum
         */
        public int getMinimum()
        {
            return returnMinimum;
        }


        /**
         * Gets the Maximum value.
         *
         * @return
         *      the maximum
         */
        public int getMaximum()
        {
            return returnMaximum;
        }


        /**
         * Gets the List of Eclusions.
         *
         * @return
         *      the list of exclusions
         */
        public List<String> getExclusions()
        {
            return exclusions;
        }
    }

    /**
     * The SubtreeSpecificationValueWrapper is used to pass contextual 
     * information to the opened SubtreeSpecificationDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class SubtreeSpecificationValueWrapper
    {
        /** The connection, used in DnDialog to browse for an entry */
        private IConnection connection;

        /** The subtreeSpecification */
        private SubtreeSpecification subtreeSpecification;


        /**
         * Creates a new instance of SubtreeSpecificationValueWrapper.
         *
         * @param connection
         *      the connection
         * @param dn
         *      the DN
         */
        private SubtreeSpecificationValueWrapper( IConnection connection, SubtreeSpecification subtreeSpecification )
        {
            this.connection = connection;
            this.subtreeSpecification = subtreeSpecification;
        }
    }
}
