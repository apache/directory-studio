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
package org.apache.directory.studio.openldap.common.ui.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;


public class LogOperationsWidget extends AbstractWidget
{
    // UI widgets
    private Composite composite;
    private Composite writeOperationsComposite;
    private Composite readOperationsComposite;
    private Composite sessionOperationsComposite;
    private Button allOperationsCheckbox;
    private Button writeOperationsCheckbox;
    private Button addOperationCheckbox;
    private Button deleteOperationCheckbox;
    private Button modifyOperationCheckbox;
    private Button modifyRdnOperationCheckbox;
    private Button readOperationsCheckbox;
    private Button compareOperationCheckbox;
    private Button searchOperationCheckbox;
    private Button sessionOperationsCheckbox;
    private Button abandonOperationCheckbox;
    private Button bindOperationCheckbox;
    private Button unbindOperationCheckbox;

    // Listeners
    private SelectionAdapter allOperationsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            allOperationsCheckboxesSetSelection( allOperationsCheckbox.getSelection() );
            notifyListeners();
        }
    };
    private SelectionAdapter writeOperationsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            writeOperationsCheckboxesSetSelection( writeOperationsCheckbox.getSelection() );
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };
    private SelectionAdapter writeOperationCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkWriteOperationsCheckboxSelectionState();
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };
    private SelectionAdapter readOperationsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            readOperationsCheckboxesSetSelection( readOperationsCheckbox.getSelection() );
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };
    private SelectionAdapter readOperationCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkReadOperationsCheckboxSelectionState();
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };
    private SelectionAdapter sessionOperationsCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            sessionOperationsCheckboxesSetSelection( sessionOperationsCheckbox.getSelection() );
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };
    private SelectionAdapter sessionOperationCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkSessionOperationsCheckboxSelectionState();
            checkAllOperationsCheckboxSelectionState();
            notifyListeners();
        }
    };


    /**
     * Creates the widget.
     *
     * @param parent the parent composite
     */
    public void create( Composite parent )
    {
        // Creating the widget base composite
        composite = new Composite( parent, SWT.NONE );
        GridLayout compositeGridLayout = new GridLayout( 3, true );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        compositeGridLayout.verticalSpacing = compositeGridLayout.horizontalSpacing = 0;
        composite.setLayout( compositeGridLayout );

        // All Operations Checkbox
        allOperationsCheckbox = BaseWidgetUtils.createCheckbox( composite, "All operations", 3 );

        // Write Operations Checkbox
        writeOperationsCheckbox = BaseWidgetUtils.createCheckbox( composite, "Read operations", 1 );

        // Read Operations Checkbox
        readOperationsCheckbox = BaseWidgetUtils.createCheckbox( composite, "Write operations", 1 );

        // Session Operations Checkbox
        sessionOperationsCheckbox = BaseWidgetUtils.createCheckbox( composite, "Session operations", 1 );

        // Write Operations Composite
        writeOperationsComposite = new Composite( composite, SWT.NONE );
        GridLayout writeOperationsCompositeGridLayout = new GridLayout( 2, false );
        writeOperationsCompositeGridLayout.marginHeight = writeOperationsCompositeGridLayout.marginWidth = 0;
        writeOperationsCompositeGridLayout.verticalSpacing = writeOperationsCompositeGridLayout.horizontalSpacing = 0;
        writeOperationsComposite.setLayout( writeOperationsCompositeGridLayout );
        writeOperationsComposite.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        // Read Operations Composite
        readOperationsComposite = new Composite( composite, SWT.NONE );
        GridLayout readOperationsCompositeGridLayout = new GridLayout( 2, false );
        readOperationsCompositeGridLayout.marginHeight = readOperationsCompositeGridLayout.marginWidth = 0;
        readOperationsCompositeGridLayout.verticalSpacing = readOperationsCompositeGridLayout.horizontalSpacing = 0;
        readOperationsComposite.setLayout( readOperationsCompositeGridLayout );
        readOperationsComposite.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        // Session Operations Composite
        sessionOperationsComposite = new Composite( composite, SWT.NONE );
        GridLayout sessionOperationsCompositeGridLayout = new GridLayout( 2, false );
        sessionOperationsCompositeGridLayout.marginHeight = sessionOperationsCompositeGridLayout.marginWidth = 0;
        sessionOperationsCompositeGridLayout.verticalSpacing = sessionOperationsCompositeGridLayout.horizontalSpacing = 0;
        sessionOperationsComposite.setLayout( sessionOperationsCompositeGridLayout );
        sessionOperationsComposite.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );

        // Add Operation Checkbox
        BaseWidgetUtils.createRadioIndent( writeOperationsComposite, 1 );
        addOperationCheckbox = BaseWidgetUtils.createCheckbox( writeOperationsComposite, "Add", 1 );

        // Delete Operation Checkbox
        BaseWidgetUtils.createRadioIndent( writeOperationsComposite, 1 );
        deleteOperationCheckbox = BaseWidgetUtils.createCheckbox( writeOperationsComposite, "Delete", 1 );

        // Modify Operation Checkbox
        BaseWidgetUtils.createRadioIndent( writeOperationsComposite, 1 );
        modifyOperationCheckbox = BaseWidgetUtils.createCheckbox( writeOperationsComposite, "Modify", 1 );

        // Modify RDN Operation Checkbox
        BaseWidgetUtils.createRadioIndent( writeOperationsComposite, 1 );
        modifyRdnOperationCheckbox = BaseWidgetUtils.createCheckbox( writeOperationsComposite, "Modify RDN", 1 );

        // Compare Operation Checkbox
        BaseWidgetUtils.createRadioIndent( readOperationsComposite, 1 );
        compareOperationCheckbox = BaseWidgetUtils.createCheckbox( readOperationsComposite, "Compare", 1 );

        // Search Operation Checkbox
        BaseWidgetUtils.createRadioIndent( readOperationsComposite, 1 );
        searchOperationCheckbox = BaseWidgetUtils.createCheckbox( readOperationsComposite, "Search", 1 );

        // Abandon Operation Checkbox
        BaseWidgetUtils.createRadioIndent( sessionOperationsComposite, 1 );
        abandonOperationCheckbox = BaseWidgetUtils.createCheckbox( sessionOperationsComposite, "Abandon", 1 );

        // Bind Operation Checkbox
        BaseWidgetUtils.createRadioIndent( sessionOperationsComposite, 1 );
        bindOperationCheckbox = BaseWidgetUtils.createCheckbox( sessionOperationsComposite, "Bind", 1 );

        // Unbind Operation Checkbox
        BaseWidgetUtils.createRadioIndent( sessionOperationsComposite, 1 );
        unbindOperationCheckbox = BaseWidgetUtils.createCheckbox( sessionOperationsComposite, "Unbind", 1 );

        // Adding the listeners to the UI widgets
        addListeners();
    }


    /**
     * Returns the associated composite.
     *
     * @return the composite
     */
    public void adapt( FormToolkit toolkit )
    {
        if ( toolkit != null )
        {
            toolkit.adapt( composite );
            toolkit.adapt( writeOperationsComposite );
            toolkit.adapt( readOperationsComposite );
            toolkit.adapt( sessionOperationsComposite );
        }
    }


    /**
     * Returns the primary control associated with this widget.
     *
     * @return the primary control associated with this widget.
     */
    public Control getControl()
    {
        return composite;
    }


    /**
     * Adds the listeners to the UI widgets.
     */
    private void addListeners()
    {
        allOperationsCheckbox.addSelectionListener( allOperationsCheckboxListener );
        writeOperationsCheckbox.addSelectionListener( writeOperationsCheckboxListener );
        addOperationCheckbox.addSelectionListener( writeOperationCheckboxListener );
        deleteOperationCheckbox.addSelectionListener( writeOperationCheckboxListener );
        modifyOperationCheckbox.addSelectionListener( writeOperationCheckboxListener );
        modifyRdnOperationCheckbox.addSelectionListener( writeOperationCheckboxListener );
        readOperationsCheckbox.addSelectionListener( readOperationsCheckboxListener );
        compareOperationCheckbox.addSelectionListener( readOperationCheckboxListener );
        searchOperationCheckbox.addSelectionListener( readOperationCheckboxListener );
        sessionOperationsCheckbox.addSelectionListener( sessionOperationsCheckboxListener );
        abandonOperationCheckbox.addSelectionListener( sessionOperationCheckboxListener );
        bindOperationCheckbox.addSelectionListener( sessionOperationCheckboxListener );
        unbindOperationCheckbox.addSelectionListener( sessionOperationCheckboxListener );
    }


    /**
     * Sets the selection for all operations checkboxes.
     *
     * @param selection the selection
     */
    private void allOperationsCheckboxesSetSelection( boolean selection )
    {
        allOperationsCheckbox.setGrayed( false );
        allOperationsCheckbox.setSelection( selection );
        writeOperationsCheckboxesSetSelection( selection );
        readOperationsCheckboxesSetSelection( selection );
        sessionOperationsCheckboxesSetSelection( selection );
    }


    /**
     * Sets the selection for the 'Write' operations checkboxes.
     *
     * @param selection the selection
     */
    private void writeOperationsCheckboxesSetSelection( boolean selection )
    {
        writeOperationsCheckbox.setGrayed( false );
        writeOperationsCheckbox.setSelection( selection );
        addOperationCheckbox.setSelection( selection );
        deleteOperationCheckbox.setSelection( selection );
        modifyOperationCheckbox.setSelection( selection );
        modifyRdnOperationCheckbox.setSelection( selection );
    }


    /**
     * Sets the selection for the 'Read' operations checkboxes.
     *
     * @param selection the selection
     */
    private void readOperationsCheckboxesSetSelection( boolean selection )
    {
        readOperationsCheckbox.setGrayed( false );
        readOperationsCheckbox.setSelection( selection );
        compareOperationCheckbox.setSelection( selection );
        searchOperationCheckbox.setSelection( selection );
    }


    /**
     * Sets the selection for the 'Session' operations checkboxes.
     *
     * @param selection the selection
     */
    private void sessionOperationsCheckboxesSetSelection( boolean selection )
    {
        sessionOperationsCheckbox.setGrayed( false );
        sessionOperationsCheckbox.setSelection( selection );
        abandonOperationCheckbox.setSelection( selection );
        bindOperationCheckbox.setSelection( selection );
        unbindOperationCheckbox.setSelection( selection );
    }


    /**
     * Verifies the selection state for the 'All Operations' checkbox.
     */
    private void checkAllOperationsCheckboxSelectionState()
    {
        boolean atLeastOneSelected = addOperationCheckbox.getSelection()
            || deleteOperationCheckbox.getSelection() || modifyOperationCheckbox.getSelection()
            || modifyRdnOperationCheckbox.getSelection() || compareOperationCheckbox.getSelection()
            || searchOperationCheckbox.getSelection() || abandonOperationCheckbox.getSelection()
            || bindOperationCheckbox.getSelection() || unbindOperationCheckbox.getSelection();
        boolean allSelected = addOperationCheckbox.getSelection()
            && deleteOperationCheckbox.getSelection() && modifyOperationCheckbox.getSelection()
            && modifyRdnOperationCheckbox.getSelection() && compareOperationCheckbox.getSelection()
            && searchOperationCheckbox.getSelection() && abandonOperationCheckbox.getSelection()
            && bindOperationCheckbox.getSelection() && unbindOperationCheckbox.getSelection();
        allOperationsCheckbox.setGrayed( atLeastOneSelected && !allSelected );
        allOperationsCheckbox.setSelection( atLeastOneSelected );
    }


    /**
     * Verifies the selection state for the 'Write Operations' checkbox.
     */
    private void checkWriteOperationsCheckboxSelectionState()
    {
        boolean atLeastOneSelected = isChecked( addOperationCheckbox )
            || isChecked( deleteOperationCheckbox ) || isChecked( modifyOperationCheckbox )
            || isChecked( modifyRdnOperationCheckbox );
        boolean allSelected = isChecked( addOperationCheckbox )
            && isChecked( deleteOperationCheckbox ) && isChecked( modifyOperationCheckbox )
            && isChecked( modifyRdnOperationCheckbox );
        writeOperationsCheckbox.setGrayed( atLeastOneSelected && !allSelected );
        writeOperationsCheckbox.setSelection( atLeastOneSelected );
    }


    /**
     * Verifies the selection state for the 'Read Operations' checkbox.
     */
    private void checkReadOperationsCheckboxSelectionState()
    {
        boolean atLeastOneSelected = isChecked( compareOperationCheckbox )
            || isChecked( searchOperationCheckbox );
        boolean allSelected = isChecked( compareOperationCheckbox )
            && isChecked( searchOperationCheckbox );
        readOperationsCheckbox.setGrayed( atLeastOneSelected && !allSelected );
        readOperationsCheckbox.setSelection( atLeastOneSelected );
    }


    /**
     * Verifies the selection state for the 'Session Operations' checkbox.
     */
    private void checkSessionOperationsCheckboxSelectionState()
    {
        boolean atLeastOneSelected = isChecked( abandonOperationCheckbox )
            || isChecked( bindOperationCheckbox ) || isChecked( unbindOperationCheckbox );
        boolean allSelected = isChecked( abandonOperationCheckbox )
            && isChecked( bindOperationCheckbox ) && isChecked( unbindOperationCheckbox );
        sessionOperationsCheckbox.setGrayed( atLeastOneSelected && !allSelected );
        sessionOperationsCheckbox.setSelection( atLeastOneSelected );
    }


    /**
     * Sets the input.
     *
     * @param operationsList the operations list
     */
    public void setInput( List<LogOperation> operationsList )
    {
        // Reset all checkboxes
        resetAllCheckboxes();

        // Select checkboxes according to the log operations list
        if ( operationsList != null )
        {
            for ( LogOperation logOperation : operationsList )
            {
                switch ( logOperation )
                {
                    case ALL:
                        allOperationsCheckbox.setSelection( true );
                        allOperationsCheckboxesSetSelection( true );
                        break;
                    case WRITES:
                        writeOperationsCheckbox.setSelection( true );
                        writeOperationsCheckboxesSetSelection( true );
                        break;
                    case ADD:
                        addOperationCheckbox.setSelection( true );
                        break;
                    case DELETE:
                        deleteOperationCheckbox.setSelection( true );
                        break;
                    case MODIFY:
                        modifyOperationCheckbox.setSelection( true );
                        break;
                    case MODIFY_RDN:
                        modifyRdnOperationCheckbox.setSelection( true );
                        break;
                    case READS:
                        readOperationsCheckbox.setSelection( true );
                        readOperationsCheckboxesSetSelection( true );
                        break;
                    case COMPARE:
                        compareOperationCheckbox.setSelection( true );
                        break;
                    case SEARCH:
                        searchOperationCheckbox.setSelection( true );
                        break;
                    case SESSION:
                        sessionOperationsCheckbox.setSelection( true );
                        sessionOperationsCheckboxesSetSelection( true );
                        break;
                    case ABANDON:
                        abandonOperationCheckbox.setSelection( true );
                        break;
                    case BIND:
                        bindOperationCheckbox.setSelection( true );
                        break;
                    case UNBIND:
                        unbindOperationCheckbox.setSelection( true );
                        break;
                }
            }
        }

        // Check hierarchical checkboxes
        checkWriteOperationsCheckboxSelectionState();
        checkReadOperationsCheckboxSelectionState();
        checkSessionOperationsCheckboxSelectionState();
        checkAllOperationsCheckboxSelectionState();
    }


    /**
     * Resets all checkboxes.
     */
    private void resetAllCheckboxes()
    {
        allOperationsCheckbox.setSelection( false );
        allOperationsCheckbox.setGrayed( false );
        writeOperationsCheckbox.setSelection( false );
        writeOperationsCheckbox.setGrayed( false );
        addOperationCheckbox.setSelection( false );
        addOperationCheckbox.setGrayed( false );
        deleteOperationCheckbox.setSelection( false );
        deleteOperationCheckbox.setGrayed( false );
        modifyOperationCheckbox.setSelection( false );
        modifyOperationCheckbox.setGrayed( false );
        readOperationsCheckbox.setSelection( false );
        readOperationsCheckbox.setGrayed( false );
        compareOperationCheckbox.setSelection( false );
        compareOperationCheckbox.setGrayed( false );
        searchOperationCheckbox.setSelection( false );
        searchOperationCheckbox.setGrayed( false );
        sessionOperationsCheckbox.setSelection( false );
        sessionOperationsCheckbox.setGrayed( false );
        abandonOperationCheckbox.setSelection( false );
        abandonOperationCheckbox.setGrayed( false );
        bindOperationCheckbox.setSelection( false );
        bindOperationCheckbox.setGrayed( false );
        unbindOperationCheckbox.setSelection( false );
        unbindOperationCheckbox.setGrayed( false );
    }


    /**
     * Returns the list of selected operations.
     *
     * @return the list of selected operations
     */
    public List<LogOperation> getSelectedOperationsList()
    {
        List<LogOperation> logOperations = new ArrayList<LogOperation>();

        // All operations
        if ( isChecked( allOperationsCheckbox ) )
        {
            logOperations.add( LogOperation.ALL );
        }
        else
        {
            // Write operations
            if ( isChecked( writeOperationsCheckbox ) )
            {
                logOperations.add( LogOperation.WRITES );
            }
            else
            {
                // Add operation
                if ( isChecked( addOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.ADD );
                }

                // Delete operation
                if ( isChecked( deleteOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.DELETE );
                }

                // Modify operation
                if ( isChecked( modifyOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.MODIFY );
                }

                // Modify RDN operation
                if ( isChecked( modifyRdnOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.MODIFY_RDN );
                }
            }

            // Read operations
            if ( isChecked( readOperationsCheckbox ) )
            {
                logOperations.add( LogOperation.READS );
            }
            else
            {
                // Compare operation
                if ( isChecked( compareOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.COMPARE );
                }

                // Search operation
                if ( isChecked( searchOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.SEARCH );
                }
            }

            // Session operations
            if ( isChecked( sessionOperationsCheckbox ) )
            {
                logOperations.add( LogOperation.SESSION );
            }
            else
            {
                // Abandon operation
                if ( isChecked( abandonOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.ABANDON );
                }

                // Bind operation
                if ( isChecked( bindOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.BIND );
                }

                // Unbind operation
                if ( isChecked( unbindOperationCheckbox ) )
                {
                    logOperations.add( LogOperation.UNBIND );
                }
            }
        }

        return logOperations;
    }


    /**
     * Indicates if a checkbox is checked ('selected' and not 'grayed').
     *
     * @param checkbox the checkbox
     * @return <code>true</code> if the checkbox is checked
     *         <code>false</code> if not.
     */
    private boolean isChecked( Button checkbox )
    {
        return ( ( checkbox != null ) && ( !checkbox.isDisposed() ) && ( checkbox.getSelection() ) && ( !checkbox
            .getGrayed() ) );
    }


    /**
     * Disposes all created SWT widgets.
     */
    public void dispose()
    {
        // Composite
        if ( ( composite != null ) && ( !composite.isDisposed() ) )
        {
            composite.dispose();
        }
    }
}
