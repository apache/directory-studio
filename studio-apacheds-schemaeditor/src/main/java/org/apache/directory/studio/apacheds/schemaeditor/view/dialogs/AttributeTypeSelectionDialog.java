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

package org.apache.directory.studio.apacheds.schemaeditor.view.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
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
 * @version $Rev$, $Date$
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
        newShell.setText( "Attribute Type Selection" );
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
        chooseLabel.setText( "Choose an attribute type" );
        chooseLabel.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        searchText = new Text( composite, SWT.BORDER );
        searchText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        searchText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                attributeTypesTableViewer.setInput( searchText.getText() );
                attributeTypesTable.select( 0 );
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
        matchingLabel.setText( "Matching attribute type(s)" );
        matchingLabel.setLayoutData( new GridData( GridData.FILL, SWT.None, true, false ) );

        attributeTypesTable = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
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
        attributeTypesTableViewer.setUseHashlookup( true );

        attributeTypesTableViewer.setContentProvider( new AttributeTypeSelectionDialogContentProvider(
            hiddenAttributeTypes ) );
        attributeTypesTableViewer.setLabelProvider( new LabelProvider() );

        // We need to force the input to load the complete list of attribute types
        attributeTypesTableViewer.setInput( "" ); //$NON-NLS-1$
        // We also need to force the selection of the first row
        attributeTypesTable.select( 0 );

        return composite;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, "Add", true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        StructuredSelection selection = ( StructuredSelection ) attributeTypesTableViewer.getSelection();
        if ( selection.isEmpty() )
        {
            MessageDialog.openError( getShell(), "Invalid Selection", "You have to choose an attribute type" );
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
}
