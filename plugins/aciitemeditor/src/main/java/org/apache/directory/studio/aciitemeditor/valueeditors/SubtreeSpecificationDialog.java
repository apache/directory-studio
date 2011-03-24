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
package org.apache.directory.studio.aciitemeditor.valueeditors;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.subtree.BaseSubtreeSpecification;
import org.apache.directory.shared.ldap.model.subtree.SubtreeSpecification;
import org.apache.directory.shared.ldap.model.subtree.SubtreeSpecificationParser;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a dialog to enter the Subtree Specification value.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
class SubtreeSpecificationDialog extends Dialog
{

    /** The parser. */
    private final SubtreeSpecificationParser parser = new SubtreeSpecificationParser( null );

    /** The connection */
    private IBrowserConnection connection;

    /** The subentry's Dn */
    private Dn subentryDn;

    /** Flag indicating if the refinement or filter widget should be visible */
    private boolean refinementOrFilterVisible;

    /** Flag indicating if a local name should be used for the base */
    private boolean useLocalName;

    /** The initial SubtreeSpecification */
    private SubtreeSpecification subtreeSpecification;

    /** The Exclusions List */
    private List<String> exclusions;

    /** The returned SubtreeSpecification */
    private String returnValue;

    // UI Fields
    private EntryWidget entryWidget;
    private Spinner minimumSpinner;
    private Spinner maximumSpinner;
    private TableViewer exclusionsTableViewer;
    private Button exclusionsTableAddButton;
    private Button exclusionsTableEditButton;
    private Button exclusionsTableDeleteButton;
    private Button refinementButton;
    private Text refinementText;
    private Button filterButton;
    private FilterWidget filterWidget;


    /**
     * Creates a new instance of SubtreeSpecificationDialog.
     *
     * @param shell
     *      the shell to use
     * @param connection
     *      the connection to use
     * @param subentryDn
     *      the subentry's Dn
     * @param initialSubtreeSpecification
     *      the initial SubtreeSpecification
     * @param refinementOrFilterVisible
     *      true if the refinement of filter widget should be visible
     * @param useLocalName 
     *      true to use local name for the base
     */
    SubtreeSpecificationDialog( Shell shell, IBrowserConnection connection, Dn subentryDn,
        String initialSubtreeSpecification, boolean refinementOrFilterVisible, boolean useLocalName )
    {
        super( shell );
        this.connection = connection;
        this.subentryDn = subentryDn;
        this.refinementOrFilterVisible = refinementOrFilterVisible;
        this.useLocalName = useLocalName;

        // parse
        try
        {
            subtreeSpecification = parser.parse( initialSubtreeSpecification );
            if ( subtreeSpecification == null )
            {
                subtreeSpecification = new BaseSubtreeSpecification();
            }
        }
        catch ( ParseException pe )
        {
            // TODO
            pe.printStackTrace();
            subtreeSpecification = new BaseSubtreeSpecification();
        }

        exclusions = new ArrayList<String>();
        Set<Dn> chopBeforeExclusions = subtreeSpecification.getChopBeforeExclusions();
        for ( Dn dn : chopBeforeExclusions )
        {
            exclusions.add( "chopBefore: \"" + dn.getNormName() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Set<Dn> chopAfterExclusions = subtreeSpecification.getChopAfterExclusions();
        for ( Dn dn : chopAfterExclusions )
        {
            exclusions.add( "chopAfter: \"" + dn.getNormName() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        returnValue = null;
    }


    /** (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "SubtreeValueEditor.title" ) ); //$NON-NLS-1$
        newShell.setImage( Activator.getDefault().getImage( Messages.getString( "SubtreeValueEditor.icon" ) ) ); //$NON-NLS-1$
    }


    /** (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        // set return value
        //returnValue = buildSubreeSpecification();
        StringBuilder sb = new StringBuilder();
        subtreeSpecification.toString( sb );
        returnValue = sb.toString();

        // save filter and dn history
        if ( refinementOrFilterVisible )
        {
            filterWidget.saveDialogSettings();
        }
        entryWidget.saveDialogSettings();

        super.okPressed();
    }


    /** (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite outer = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        outer.setLayoutData( gd );

        Composite composite = BaseWidgetUtils.createColumnContainer( outer, 3, 1 );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "SubtreeValueEditor.label.base" ), 1 ); //$NON-NLS-1$

        Dn base = subtreeSpecification.getBase();
        Dn suffix = subentryDn != null ? subentryDn.getParent() : null;
        entryWidget = new EntryWidget( connection, base, suffix, useLocalName );
        entryWidget.createWidget( composite );
        entryWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        GridData spinnersGridData = new GridData();
        spinnersGridData.grabExcessHorizontalSpace = true;
        spinnersGridData.verticalAlignment = GridData.CENTER;
        spinnersGridData.horizontalSpan = 2;
        spinnersGridData.horizontalAlignment = GridData.BEGINNING;
        spinnersGridData.widthHint = 3 * 12;

        BaseWidgetUtils.createLabel( composite, Messages.getString( "SubtreeValueEditor.label.minimum" ), 1 ); //$NON-NLS-1$
        minimumSpinner = new Spinner( composite, SWT.BORDER );
        minimumSpinner.setMinimum( 0 );
        minimumSpinner.setMaximum( Integer.MAX_VALUE );
        minimumSpinner.setDigits( 0 );
        minimumSpinner.setIncrement( 1 );
        minimumSpinner.setPageIncrement( 100 );
        minimumSpinner.setSelection( subtreeSpecification.getMinBaseDistance() );
        minimumSpinner.setLayoutData( spinnersGridData );
        minimumSpinner.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "SubtreeValueEditor.label.maximum" ), 1 ); //$NON-NLS-1$
        maximumSpinner = new Spinner( composite, SWT.BORDER );
        maximumSpinner.setMinimum( 0 );
        maximumSpinner.setMaximum( Integer.MAX_VALUE );
        maximumSpinner.setDigits( 0 );
        maximumSpinner.setIncrement( 1 );
        maximumSpinner.setPageIncrement( 100 );
        maximumSpinner.setSelection( subtreeSpecification.getMaxBaseDistance() );
        maximumSpinner.setLayoutData( spinnersGridData );
        maximumSpinner.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                validate();
            }
        } );

        createExclusionsTable( composite );

        if ( refinementOrFilterVisible )
        {
            BaseWidgetUtils.createSpacer( composite, 3 );
            createRefinementOrFilterWidgets( composite );
        }

        applyDialogFont( outer );

        initFromInput();

        validate();

        return outer;
    }


    /**
     * Initializes the Value Editor from the input.
     */
    private void initFromInput()
    {

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

        BaseWidgetUtils.createLabel( composite, Messages.getString( "SubtreeValueEditor.label.exclusions" ), 1 ); //$NON-NLS-1$
        Table exclusionsTable = new Table( composite, SWT.BORDER );
        exclusionsTable.setHeaderVisible( false );
        exclusionsTable.setLayoutData( tableGridData );
        exclusionsTable.setLinesVisible( false );
        exclusionsTableViewer = new TableViewer( exclusionsTable );
        exclusionsTableViewer.setContentProvider( new ArrayContentProvider() );
        exclusionsTableViewer.setLabelProvider( new LabelProvider() );
        exclusionsTableViewer.setInput( exclusions );
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
        buttonGridData.widthHint = Activator.getButtonWidth( buttonComposite );

        exclusionsTableAddButton = new Button( buttonComposite, SWT.PUSH );
        exclusionsTableAddButton.setText( Messages.getString( "SubtreeValueEditor.button.add" ) ); //$NON-NLS-1$
        exclusionsTableAddButton.setLayoutData( buttonGridData );
        exclusionsTableAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                addValueExclusionsTable();
            }
        } );

        exclusionsTableEditButton = new Button( buttonComposite, SWT.PUSH );
        exclusionsTableEditButton.setText( Messages.getString( "SubtreeValueEditor.button.edit" ) ); //$NON-NLS-1$
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
        exclusionsTableDeleteButton.setText( Messages.getString( "SubtreeValueEditor.button.delete" ) ); //$NON-NLS-1$
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
     * Creates the refinement or filter widgets
     *
     * @param composite
     *      the composite
     */
    private void createRefinementOrFilterWidgets( Composite parent )
    {
        // Messages.getString( "SubtreeValueEditor.label.exclusions" )
        BaseWidgetUtils.createLabel( parent, Messages
            .getString( "SubtreeValueEditor.SubtreeValueEditor.label.refinementOrFilter" ), 1 ); //$NON-NLS-1$

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 2 );

        // refinement redio button
        refinementButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "SubtreeValueEditor.SubtreeValueEditor.label.refinement" ), 2 ); //$NON-NLS-1$

        // refinement text
        refinementText = new Text( composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = 2;
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH / 6 );
        refinementText.setLayoutData( gd );
        try
        {
            StringBuilder refinementBuffer = new StringBuilder();
            if ( subtreeSpecification.getRefinement() != null )
            {
                subtreeSpecification.getRefinement().printRefinementToBuffer( refinementBuffer );
            }
            refinementText.setText( refinementBuffer.toString().trim() );
            refinementText.setEnabled( true );
            refinementButton.setSelection( true );
        }
        catch ( UnsupportedOperationException e )
        {
            // thrown if the ExprNode doesn't represent a valid refinement
            refinementText.setText( "" ); //$NON-NLS-1$
            refinementText.setEnabled( false );
            refinementButton.setSelection( false );
        }

        // filter radio button
        filterButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "SubtreeValueEditor.SubtreeValueEditor.label.filter" ), 2 ); //$NON-NLS-1$

        // filter widget
        String filter = "";
        if ( subtreeSpecification.getRefinement() != null )
        {
            filter = subtreeSpecification.getRefinement().toString();
        }
        filterWidget = new FilterWidget( filter );
        filterWidget.createWidget( composite );
        filterWidget.setBrowserConnection( connection );
        filterButton.setSelection( !refinementButton.getSelection() );
        filterWidget.setEnabled( !refinementButton.getSelection() );

        // add listeners
        refinementButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                refinementText.setEnabled( true );
                //filterButton.setSelection( false );
                filterWidget.setEnabled( false );
                validate();
            }
        } );
        refinementText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                validate();
            }
        } );
        filterButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent arg0 )
            {
                //refinementButton.setSelection( false );
                refinementText.setEnabled( false );
                filterWidget.setEnabled( true );
                validate();
            }
        } );
        filterWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
    }


    /**
     * Validates if the composed subtree specification is valid.
     */
    private void validate()
    {
        boolean valid = true;

        Dn base = entryWidget.getDn();
        valid &= base != null;

        String ss = buildSubreeSpecification();

        try
        {
            subtreeSpecification = parser.parse( ss );
            valid &= true;
        }
        catch ( ParseException pe )
        {
            subtreeSpecification = null;
            valid &= false;
        }

        if ( refinementOrFilterVisible && filterButton.getSelection() )
        {
            valid &= filterWidget.getFilter() != null;
        }

        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( valid );
        }
    }


    private String buildSubreeSpecification()
    {
        // build subtree specification tree
        StringBuffer sb = new StringBuffer();
        sb.append( "{" ); //$NON-NLS-1$

        // Adding base
        Dn base = entryWidget.getDn();
        if ( base != null && !SubtreeValueEditor.EMPTY.equals( base.toString() ) )
        {
            sb.append( " base \"" + base.toString() + "\"," ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Adding Minimum
        int minimum = minimumSpinner.getSelection();
        if ( minimum != 0 )
        {
            sb.append( " minimum " + minimum + "," ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Adding Maximum
        int maximum = maximumSpinner.getSelection();
        if ( maximum != 0 )
        {
            sb.append( " maximum " + maximum + "," ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Adding Exclusions
        if ( !exclusions.isEmpty() )
        {
            sb.append( " specificExclusions {" ); //$NON-NLS-1$

            for ( Iterator<String> it = exclusions.iterator(); it.hasNext(); )
            {
                sb.append( " " + it.next() ); //$NON-NLS-1$

                if ( it.hasNext() )
                {
                    sb.append( "," ); //$NON-NLS-1$
                }
            }

            sb.append( " }," ); //$NON-NLS-1$
        }

        // Add Refinement or Filter
        String refinementOrFilter = ""; //$NON-NLS-1$
        if ( refinementOrFilterVisible )
        {
            if ( refinementButton.getSelection() )
            {
                refinementOrFilter = refinementText.getText();
            }
            else
            {
                refinementOrFilter = filterWidget.getFilter();
            }
        }
        else
        {
            refinementOrFilter = ""; //$NON-NLS-1$
        }
        if ( refinementOrFilter != null && !SubtreeValueEditor.EMPTY.equals( refinementOrFilter ) )
        {
            sb.append( " specificationFilter " + refinementOrFilter + "," ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Removing the last ','
        if ( sb.charAt( sb.length() - 1 ) == ',' )
        {
            sb.deleteCharAt( sb.length() - 1 );
        }

        sb.append( " }" ); //$NON-NLS-1$

        return sb.toString();
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
        Dn chopBase = subtreeSpecification.getBase();
        
        if ( useLocalName && ( subentryDn != null ) )
        {
            Dn suffix = subentryDn.getParent();
            
            if ( !Dn.isNullOrEmpty( suffix ) )
            {
                try
                { 
                    chopBase = chopBase.addAll( suffix );
                }
                catch ( LdapInvalidDnException lide )
                {
                    // Do nothing 
                }
            }
        }

        ExclusionDialog dialog = new ExclusionDialog( getShell(), connection, chopBase, "" ); //$NON-NLS-1$
        
        if ( dialog.open() == TextDialog.OK && !SubtreeValueEditor.EMPTY.equals( dialog.getType() )
            && !SubtreeValueEditor.EMPTY.equals( dialog.getDN() ) )
        {
            String newValue = dialog.getType() + ": \"" + dialog.getDN() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
            exclusions.add( newValue );
            exclusionsTableViewer.refresh();
            validate();
        }
    }


    /**
     * Opens the editor with the currently selected Exclusion
     * value and puts the modified value into the list.
     */
    private void editValueExclusionsTable()
    {
        String oldValue = getSelectedValueExclusionsTable();
        if ( oldValue != null )
        {
            Dn chopBase = subtreeSpecification.getBase();
            
            if ( useLocalName && ( subentryDn != null ) )
            {
                Dn suffix = subentryDn.getParent();
                
                if ( !Dn.isNullOrEmpty( suffix ) )
                {
                    try
                    {
                        chopBase = chopBase.addAll( suffix );
                    }
                    catch ( LdapInvalidDnException lide )
                    {
                        // Do nothing 
                    }

                }
            }

            ExclusionDialog dialog = new ExclusionDialog( getShell(), connection, chopBase, oldValue );
            
            if ( dialog.open() == TextDialog.OK && !SubtreeValueEditor.EMPTY.equals( dialog.getType() )
                && !SubtreeValueEditor.EMPTY.equals( dialog.getDN() ) )
            {
                String newValue = dialog.getType() + ": \"" + dialog.getDN() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                exclusions.remove( oldValue );
                exclusions.add( newValue );
                exclusionsTableViewer.refresh();
                validate();
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
            validate();
        }
    }


    /**
     * Gets the subtree specification value or null if canceled.
     *
     * @return the subtree specification value or null if canceled
     */
    public String getSubtreeSpecificationValue()
    {
        return returnValue;
    }
}