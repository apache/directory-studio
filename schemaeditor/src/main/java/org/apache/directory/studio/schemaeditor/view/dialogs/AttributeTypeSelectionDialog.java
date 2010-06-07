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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class is Attribute Type Selection Dialog, that allows user to select an attribute type.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeSelectionDialog extends Dialog
{
    /** The selected attribute type */
    private AttributeTypeImpl selectedAttributeType;

    /** The hidden attribute types */
    private List<AttributeTypeImpl> hiddenAttributeTypes;

    // UI Fields
    private Text searchText;
    private Table attributeTypesTable;
    private TableViewer attributeTypesTableViewer;
    private Label schemaIconLabel;
    private Label schemaNameLabel;
    private Button chooseButton;


    /**
     * Creates a new instance of AttributeTypeSelectionDialog.
     */
    public AttributeTypeSelectionDialog()
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        hiddenAttributeTypes = new ArrayList<AttributeTypeImpl>();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "AttributeTypeSelectionDialog.TypeSelection" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        composite.setLayout( layout );

        Label chooseLabel = new Label( composite, SWT.NONE );
        chooseLabel.setText( Messages.getString( "AttributeTypeSelectionDialog.ChooseAType" ) ); //$NON-NLS-1$
        chooseLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        searchText = new Text( composite, SWT.BORDER | SWT.SEARCH );
        searchText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        searchText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                setSearchInput( searchText.getText() );
            }
        } );
        searchText.addKeyListener( new KeyAdapter()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    attributeTypesTable.setFocus();
                }
            }
        } );

        Label matchingLabel = new Label( composite, SWT.NONE );
        matchingLabel.setText( Messages.getString( "AttributeTypeSelectionDialog.MatchingTypes" ) ); //$NON-NLS-1$
        matchingLabel.setLayoutData( new GridData( SWT.FILL, SWT.None, true, false ) );

        attributeTypesTable = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
        gridData.heightHint = 148;
        gridData.minimumHeight = 148;
        gridData.widthHint = 350;
        gridData.minimumWidth = 350;
        attributeTypesTable.setLayoutData( gridData );
        attributeTypesTable.addMouseListener( new MouseAdapter()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( attributeTypesTable.getSelectionIndex() != -1 )
                {
                    okPressed();
                }
            }
        } );

        attributeTypesTableViewer = new TableViewer( attributeTypesTable );
        attributeTypesTableViewer.setContentProvider( new AttributeTypeSelectionDialogContentProvider(
            hiddenAttributeTypes ) );
        attributeTypesTableViewer.setLabelProvider( new DecoratingLabelProvider(
            new AttributeTypeSelectionDialogLabelProvider(), Activator.getDefault().getWorkbench()
                .getDecoratorManager().getLabelDecorator() ) );
        attributeTypesTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) attributeTypesTableViewer.getSelection();
                if ( selection.isEmpty() )
                {
                    if ( ( chooseButton != null ) && ( !chooseButton.isDisposed() ) )
                    {
                        chooseButton.setEnabled( false );
                    }
                    schemaIconLabel.setImage( Activator.getDefault().getImage( PluginConstants.IMG_TRANSPARENT_16X16 ) );
                    schemaNameLabel.setText( "" ); //$NON-NLS-1$
                }
                else
                {
                    if ( ( chooseButton != null ) && ( !chooseButton.isDisposed() ) )
                    {
                        chooseButton.setEnabled( true );
                    }
                    schemaIconLabel.setImage( Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA ) );
                    schemaNameLabel.setText( ( ( AttributeTypeImpl ) selection.getFirstElement() ).getSchema() );
                }
            }
        } );

        // Schema Composite
        Composite schemaComposite = new Composite( composite, SWT.BORDER );
        schemaComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        GridLayout schemaCompositeGridLayout = new GridLayout( 2, false );
        schemaCompositeGridLayout.horizontalSpacing = 0;
        schemaCompositeGridLayout.verticalSpacing = 0;
        schemaCompositeGridLayout.marginWidth = 2;
        schemaCompositeGridLayout.marginHeight = 2;
        schemaComposite.setLayout( schemaCompositeGridLayout );

        // Schema Icon Label
        schemaIconLabel = new Label( schemaComposite, SWT.NONE );
        GridData schemaIconLabelGridData = new GridData( SWT.NONE, SWT.BOTTOM, false, false );
        schemaIconLabelGridData.widthHint = 18;
        schemaIconLabelGridData.heightHint = 16;
        schemaIconLabel.setLayoutData( schemaIconLabelGridData );
        schemaIconLabel.setImage( Activator.getDefault().getImage( PluginConstants.IMG_TRANSPARENT_16X16 ) );

        // Schema Name Label
        schemaNameLabel = new Label( schemaComposite, SWT.NONE );
        schemaNameLabel.setLayoutData( new GridData( SWT.FILL, SWT.BOTTOM, true, false ) );
        schemaNameLabel.setText( "" ); //$NON-NLS-1$

        // We need to force the input to load the complete list of attribute types
        setSearchInput( "" ); //$NON-NLS-1$

        return composite;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        chooseButton = createButton( parent, IDialogConstants.OK_ID, Messages
            .getString( "AttributeTypeSelectionDialog.Choose" ), true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        StructuredSelection selection = ( StructuredSelection ) attributeTypesTableViewer.getSelection();
        if ( selection.isEmpty() )
        {
            if ( ( chooseButton != null ) && ( !chooseButton.isDisposed() ) )
            {
                chooseButton.setEnabled( false );
            }
        }
        else
        {
            if ( ( chooseButton != null ) && ( !chooseButton.isDisposed() ) )
            {
                chooseButton.setEnabled( true );
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        StructuredSelection selection = ( StructuredSelection ) attributeTypesTableViewer.getSelection();
        if ( selection.isEmpty() )
        {
            MessageDialog.openError( getShell(), Messages.getString( "AttributeTypeSelectionDialog.InvalidSelection" ), //$NON-NLS-1$
                Messages.getString( "AttributeTypeSelectionDialog.MustChooseType" ) ); //$NON-NLS-1$
            return;
        }
        else
        {
            selectedAttributeType = ( AttributeTypeImpl ) selection.getFirstElement();
        }

        super.okPressed();
    }


    /**
     * Returns the selected Attribute Type.
     * 
     * @return
     *      the selected Attribute Type
     */
    public AttributeTypeImpl getSelectedAttributeType()
    {
        return selectedAttributeType;
    }


    /**
     * Set the hidden Attribute Types.
     *
     * @param list
     *      a list of Attribute Types to hide
     */
    public void setHiddenAttributeTypes( List<AttributeTypeImpl> list )
    {
        hiddenAttributeTypes = list;
    }


    /**
     * Sets the hidden Attribute Types.
     *
     * @param attributeTypes
     *      an array of Attribute Types to hide
     */
    public void setHiddenAttributeTypes( AttributeTypeImpl[] attributeTypes )
    {
        for ( AttributeTypeImpl objectClass : attributeTypes )
        {
            hiddenAttributeTypes.add( objectClass );
        }
    }


    /**
     * Set the Search Input.
     *
     * @param searchString
     *      the Search String
     */
    private void setSearchInput( String searchString )
    {
        attributeTypesTableViewer.setInput( searchString );

        Object firstElement = attributeTypesTableViewer.getElementAt( 0 );
        if ( firstElement != null )
        {
            attributeTypesTableViewer.setSelection( new StructuredSelection( firstElement ), true );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        hiddenAttributeTypes.clear();
        hiddenAttributeTypes = null;

        attributeTypesTableViewer = null;

        attributeTypesTable.dispose();
        attributeTypesTable = null;

        searchText.dispose();
        searchText = null;

        schemaIconLabel.dispose();
        schemaIconLabel = null;

        schemaNameLabel.dispose();
        schemaNameLabel = null;

        return super.close();
    }
}
