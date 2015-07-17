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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.wrappers.OrderedStringValueWrapper;


/**
 * The StringValueDialog is used to edit a String, which is a value for any simple multiple
 * value attribute.
 * 
 * <pre>
 * +---------------------------------------+
 * | .-----------------------------------. |
 * | | Value  : [                      ] | |
 * | '-----------------------------------' |
 * |                                       |
 * |  (cancel)                       (OK)  |
 * +---------------------------------------+
 * 
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OrderedStringValueDialog extends AddEditDialog<OrderedStringValueWrapper>
{
    // UI widgets
    /** The attribute name */
    private String attributeName;
    
    /** The String value */
    private Text stringValue;
    
    /**
     * Create a new instance of the String
     * 
     * @param parentShell The parent Shell
     * @param attributeName The name of the attribute being editaed
     */
    public OrderedStringValueDialog( Shell parentShell, String attributeName )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.attributeName = attributeName;
    }
    
    
    /**
     * The listener for the String Text
     */
    private ModifyListener stringValueTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            getEditedElement().setValue( stringValue.getText() );
            okButton.setEnabled( true );
        }
    };
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( attributeName );
    }


    /**
     * Create the Dialog for StringValue :
     * <pre>
     * +---------------------------------------+
     * | .-----------------------------------. |
     * | | Value  : [                      ] | |
     * | '-----------------------------------' |
     * |                                       |
     * |  (cancel)                       (OK)  |
     * +---------------------------------------+
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // StringValue Group
        Group stringValueGroup = BaseWidgetUtils.createGroup( parent, null, 1 );
        GridLayout stringValueGroupGridLayout = new GridLayout( 2, false );
        stringValueGroup.setLayout( stringValueGroupGridLayout );
        stringValueGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // String Text
        BaseWidgetUtils.createLabel( stringValueGroup, attributeName + ":", 1 );
        stringValue = BaseWidgetUtils.createText( stringValueGroup, "", 1 );
        stringValue.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initDialog();
        addListeners();

        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Initializes the UI from the String
     */
    protected void initDialog()
    {
        OrderedStringValueWrapper editedElement = (OrderedStringValueWrapper)getEditedElement();
        
        if ( editedElement != null )
        {
            String value = editedElement.getValue();
            
            if ( value == null )
            {
                stringValue.setText( "" );
            }
            else
            {
                stringValue.setText( editedElement.getValue() );
            }
        }
        
        // Create the prefix if it's a Add : set it to the selected position atm
        if ( isAdd() )
        { 
            editedElement.setPrefix( getSelectedPosition() );
        }
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new OrderedStringValueWrapper( Integer.MAX_VALUE, "", true ) );
    }


    public void addNewElement( OrderedStringValueWrapper editedElement )
    {
        OrderedStringValueWrapper newElement = (OrderedStringValueWrapper)editedElement.clone();
        setEditedElement( newElement );
    }
    
    
    @Override
    public void okPressed()
    {
        // We have to check the prefix. If it was an Add, we might have a duplicated prefix. If so, we have to 
        // increment the second one and all the following ones
        if ( isAdd() )
        { 
            int position = getSelectedPosition();

            for ( int i = getElements().size() - 1; i >= position ; i-- )
            {
                OrderedStringValueWrapper value = getElements().get( i );
                
                value.setPrefix( value.getPrefix() + 1 );
            }
            
            getElements().add( position, getEditedElement() );
        }
        
        super.okPressed();
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        stringValue.addModifyListener( stringValueTextListener );
    }
}
