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
import org.apache.directory.studio.common.ui.wrappers.StringValueWrapper;
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
public class StringValueDialog extends AddEditDialog<StringValueWrapper>
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
     */
    public StringValueDialog( Shell parentShell, String atttributeName )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.attributeName = atttributeName;
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
        StringValueWrapper editedElement = (StringValueWrapper)getEditedElement();
        
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
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new StringValueWrapper( "", true ) );
    }


    public void addNewElement( StringValueWrapper editedElement )
    {
        StringValueWrapper newElement = (StringValueWrapper)editedElement.clone();
        setEditedElement( newElement );
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        stringValue.addModifyListener( stringValueTextListener );
    }
}
