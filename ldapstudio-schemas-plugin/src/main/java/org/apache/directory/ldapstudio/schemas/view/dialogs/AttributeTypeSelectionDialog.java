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

package org.apache.directory.ldapstudio.schemas.view.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
    private Table attributeTypes_table;
    private String selectedAttributeType = null;
    private Text search_text;
    private TableViewer tableViewer;


    /**
     * Creates a new instance of AttributeTypeSelectionDialog.
     */
    public AttributeTypeSelectionDialog()
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "AttributeTypeSelectionDialog.Attribute_Type_Selection" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        composite.setLayout( layout );

        Label choose_label = new Label( composite, SWT.NONE );
        choose_label.setText( Messages.getString( "AttributeTypeSelectionDialog.Choose_an_attribute_type" ) ); //$NON-NLS-1$
        choose_label.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        search_text = new Text( composite, SWT.BORDER );
        search_text.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );
        search_text.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                tableViewer.setInput( search_text.getText() );
                attributeTypes_table.select( 0 );
            }
        } );
        search_text.addKeyListener( new KeyListener()
        {
            public void keyPressed( KeyEvent e )
            {
                if ( e.keyCode == SWT.ARROW_DOWN )
                {
                    attributeTypes_table.setFocus();
                }
            }


            public void keyReleased( KeyEvent e )
            {
            }
        } );

        Label matching_label = new Label( composite, SWT.NONE );
        matching_label.setText( Messages.getString( "AttributeTypeSelectionDialog.Matching_attribute_types" ) ); //$NON-NLS-1$
        matching_label.setLayoutData( new GridData( GridData.FILL, SWT.None, true, false ) );

        attributeTypes_table = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        GridData gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = 150;
        gridData.minimumHeight = 150;
        gridData.widthHint = 350;
        gridData.minimumWidth = 350;
        attributeTypes_table.setLayoutData( gridData );
        attributeTypes_table.setHeaderVisible( true );
        attributeTypes_table.addMouseListener( new MouseListener()
        {
            public void mouseDoubleClick( MouseEvent e )
            {
                if ( attributeTypes_table.getSelectionIndex() != -1 )
                {
                    okPressed();
                }
            }


            public void mouseDown( MouseEvent e )
            {
            }


            public void mouseUp( MouseEvent e )
            {
            }
        } );

        TableColumn column = new TableColumn( attributeTypes_table, SWT.LEFT, 0 );
        column.setText( "" ); //$NON-NLS-1$
        column.setWidth( 20 );

        column = new TableColumn( attributeTypes_table, SWT.LEFT, 1 );
        column.setText( Messages.getString( "AttributeTypeSelectionDialog.Name" ) ); //$NON-NLS-1$
        column.setWidth( 230 );

        column = new TableColumn( attributeTypes_table, SWT.LEFT, 2 );
        column.setText( Messages.getString( "AttributeTypeSelectionDialog.Schema" ) ); //$NON-NLS-1$
        column.setWidth( 100 );

        tableViewer = new TableViewer( attributeTypes_table );
        tableViewer.setUseHashlookup( true );

        tableViewer.setContentProvider( new AttributeTypeSelectionDialogContentProvider() );
        tableViewer.setLabelProvider( new AttributeTypeSelectionDialogLabelProvider() );

        // We need to force the input to load the complete list of attribute types
        tableViewer.setInput( "" ); //$NON-NLS-1$
        // We also need to force the selection of the first row
        attributeTypes_table.select( 0 );

        return composite;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, Messages.getString( "AttributeTypeSelectionDialog.Add" ), true ); //$NON-NLS-1$
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        if ( attributeTypes_table.getSelectionIndex() == -1 )
        {
            MessageDialog
                .openError(
                    getShell(),
                    Messages.getString( "AttributeTypeSelectionDialog.Invalid_Selection" ), Messages.getString( "AttributeTypeSelectionDialog.You_have_to_choose_an_attribute_type" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        selectedAttributeType = attributeTypes_table.getItem( attributeTypes_table.getSelectionIndex() ).getText( 1 );
        super.okPressed();
    }


    /**
     * Returns the selected attribute type.
     * 
     * @return
     *      the selected attribute type
     */
    public String getSelectedAttributeType()
    {
        return selectedAttributeType;
    }
}
