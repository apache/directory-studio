/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.dialogs;

import org.apache.directory.api.ldap.model.schema.SchemaUtils;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.CommonUIPlugin;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.acl.model.AclAttribute;
import org.apache.directory.studio.openldap.config.acl.wrapper.AclAttributeWrapper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This Dialog is used to add a new AclAttribute. 
 * 
 * <pre>
 * +---------------------------------------------+
 * | ACL Attribute                               |
 * | .-----------------------------------------. |
 * | | (o) Attribute                           | |
 * | | (o) Entry                               | |
 * | | (o) Children                            | |
 * | | (o) ObjectClass                         | |
 * | | (o) ObjectClass exclusion               | |
 * | |                                         | |
 * | | Value : [/////////////////////////////] | |
 * | '-----------------------------------------' |
 * |                                             |
 * |  (Cancel)                             (OK)  |
 * +---------------------------------------------+
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclAttributeDialog extends AddEditDialog<AclAttributeWrapper>
{
    /** The connection to the LDAP server */
    private IBrowserConnection connection;
    
    // The UI widgets
    /** The Attribute checkbox */
    private Button attributeCheckbox; 
    
    /** The entry checkbox */
    private Button entryCheckbox;
    
    /** The children checkbox */
    private Button childrenCheckbox;
    
    /** The OjectClass checkbox */
    private Button objectClassCheckbox;
    
    /** The OjectClass Exclusioncheckbox */
    private Button objectClassExclusionCheckbox;

    /** The Attribute Value text */
    private Text attributevalueText;
    
    /** A flag set when we clear the AttributeValue text */
    private boolean clearText;
    

    /** A listener for the AttributeCheckBox */
    private SelectionListener attributeCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Button selection = (Button)e.getSource();
            
            if ( selection.getSelection() )
            {
                // Clear the AttributeValue Text and disable it
                clearText = true;
                attributevalueText.setText( "" );
                attributevalueText.setEnabled( true );
                getButton( IDialogConstants.OK_ID ).setEnabled( false );
            }
        }
    };

    /** A listener for the EntryCheckBox */
    private SelectionListener entryCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Button selection = (Button)e.getSource();
            
            if ( selection.getSelection() )
            {
                // Clear the AttributeValue Text and disable it
                clearText = true;
                attributevalueText.setText( "" );
                attributevalueText.setEnabled( false );
                getEditedElement().getAclAttribute().setName( "entry" );
                getButton( IDialogConstants.OK_ID ).setEnabled( true );
            }
        }
    };

    /** A listener for the ChildrenCheckBox */
    private SelectionListener childrenCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Button selection = (Button)e.getSource();
            
            if ( selection.getSelection() )
            {
                // Clear the AttributeValue Text and disable it
                clearText = true;
                attributevalueText.setText( "" );
                attributevalueText.setEnabled( false );
                getEditedElement().getAclAttribute().setName( "children" );
                getButton( IDialogConstants.OK_ID ).setEnabled( true );
            }
        }
    };

    /** A listener for the ObjectClassCheckBox */
    private SelectionListener objectClassCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Button selection = (Button)e.getSource();
            
            if ( selection.getSelection() )
            {
                // Clear the AttributeValue Text and enable it
                clearText = true;
                attributevalueText.setText( "" );
                attributevalueText.setEnabled( true );
                getButton( IDialogConstants.OK_ID ).setEnabled( false );
            }
        }
    };

    /** A listener for the ObjectClassExclusionCheckBox */
    private SelectionListener objectClassExclusionCheckboxListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Button selection = (Button)e.getSource();
            
            if ( selection.getSelection() )
            {
                // Clear the AttributeValue Text and enable it
                clearText = true;
                attributevalueText.setText( "" );
                attributevalueText.setEnabled( true );
                getButton( IDialogConstants.OK_ID ).setEnabled( false );
            }
        }
    };
    
    /** A listener for the AttributeValuetext */
    private ModifyListener attributeValueTextListener = new ModifyListener()
    {
        @Override
        public void modifyText( ModifyEvent e )
        {
            // if the text has been clear by a button, don't do anything but switch the flag
            if ( clearText )
            {
                clearText = false;
                return;
            }
            
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            String attributeValue = attributevalueText.getText();
            boolean isAttribute = attributeCheckbox.getSelection();
            boolean isObjectClass = objectClassCheckbox.getSelection();
            boolean isObjectExclusionClass = objectClassExclusionCheckbox.getSelection();
            boolean isEntry = entryCheckbox.getSelection();
            boolean isChildren = childrenCheckbox.getSelection();
            
            // Check that is a valid name, if needed
            if ( isAttribute || isObjectClass || isObjectExclusionClass )
            {
                if ( Strings.isEmpty( attributeValue ) )
                {
                    okButton.setEnabled( false );
                    return;
                }
                
                if ( !SchemaUtils.isAttributeNameValid( attributeValue) )
                {
                    okButton.setEnabled( false );
                    return;
                }
            }
            
            // Handle the various use cases
            String result;
            
            if ( isAttribute )
            {
                // This is an attribute
                result = attributeValue;
            }
            else if ( isEntry )
            {
                // This is the special attribute value Entry
                result = AclAttribute.ENTRY;
            }
            else if ( isChildren )
            {
                // This is the special attribute value Children
                result = AclAttribute.CHILDREN;
            }
            else if ( isObjectClass )
            {
                // This is an ObjectClass
                StringBuilder buffer = new StringBuilder();
                buffer.append( AclAttribute.OC ).append( attributeValue );
                
                result = buffer.toString();
            }
            else
            {
                // This is an ObjectClass exclusion
                StringBuilder buffer = new StringBuilder( AclAttribute.OC_EX );
                buffer.append( AclAttribute.OC_EX ).append( attributeValue );
                
                result = buffer.toString();
            }

            getEditedElement().setAclAttribute( result );
            
            // Check that the element does not already exist
            if ( getElements().contains( getEditedElement() ) )
            {
                attributevalueText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.ERROR_COLOR ) );
                okButton.setEnabled( false );
            }
            else
            {
                attributevalueText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.DEFAULT_COLOR ) );
                okButton.setEnabled( true );
            }
        }
    };
    
    
    /**
     * Creates a new instance of AclAttributeDialog.
     * 
     * @param shell the parent shell
     */
    public AclAttributeDialog( Shell shell, IBrowserConnection connection )
    {
        super( shell );
        //shell.setText( Messages.getString( "AclAttribute.Title" ) );
        this.connection = connection;
    }
    
    
    /**
     * Create the Dialog for AclAttribute :
     * <pre>
     * +---------------------------------------------+
     * | ACL Attribute                               |
     * | .-----------------------------------------. |
     * | | (o) Attribute                           | |
     * | | (o) entry                               | |
     * | | (o) children                            | |
     * | | (o) ObjectClass                         | |
     * | | (o) ObjectClass exclusion               | |
     * | |                                         | |
     * | | Value : [/////////////////////////////] | |
     * | '-----------------------------------------' |
     * |                                             |
     * |  (Cancel)                             (OK)  |
     * +---------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        createAclAttributeEditGroup( composite );
        initDialog();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the AclAttribute input group.
     * 
     * <pre>
     * ACL Attribute
     * .-----------------------------------------.
     * | (o) Attribute                           |
     * | (o) entry                               |
     * | (o) children                            |
     * | (o) ObjectClass                         |
     * | (o) ObjectClass exclusion               |
     * |                                         |
     * | Value : [/////////////////////////////] |
     * '-----------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createAclAttributeEditGroup( Composite parent )
    {
        // Disallow Feature Group
        Group aclAttributeGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        GridLayout aclAttributeGridLayout = new GridLayout( 2, false );
        aclAttributeGroup.setLayout( aclAttributeGridLayout );
        aclAttributeGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The Attribute checkbox
        attributeCheckbox = BaseWidgetUtils.createRadiobutton( aclAttributeGroup, "Attribute", 2 );
        attributeCheckbox.addSelectionListener( attributeCheckboxListener );
        
        // The entry checkbox
        entryCheckbox = BaseWidgetUtils.createRadiobutton( aclAttributeGroup, "Entry", 2 );
        entryCheckbox.addSelectionListener( entryCheckboxListener );
        
        // The children checkbox
        childrenCheckbox = BaseWidgetUtils.createRadiobutton( aclAttributeGroup, "Children", 2 );
        childrenCheckbox.addSelectionListener( childrenCheckboxListener );
        
        // The OjectClass checkbox
        objectClassCheckbox = BaseWidgetUtils.createRadiobutton( aclAttributeGroup, "ObjectClass", 2 );
        objectClassCheckbox.addSelectionListener( objectClassCheckboxListener );
        
        // The OjectClass Exclusioncheckbox
        objectClassExclusionCheckbox = BaseWidgetUtils.createRadiobutton( aclAttributeGroup, "ObjectClass Exclusion", 2 );
        objectClassExclusionCheckbox.addSelectionListener( objectClassExclusionCheckboxListener );
        
        // The Value Text
        BaseWidgetUtils.createLabel( aclAttributeGroup, "Value : ", 1 );
        attributevalueText = BaseWidgetUtils.createText( aclAttributeGroup, "", 1 );
        attributevalueText.addModifyListener( attributeValueTextListener );
    }

    
    @Override
    protected void initDialog()
    {
        AclAttributeWrapper editedElement = (AclAttributeWrapper)getEditedElement();
        
        if ( editedElement != null )
        {
            AclAttribute aclAttribute =  editedElement.getAclAttribute();
            
            if ( aclAttribute.isEntry() )
            {
                entryCheckbox.setEnabled( true );
            }
            else if ( aclAttribute.isChildren() )
            {
                childrenCheckbox.setEnabled( true );
            }
            else if ( aclAttribute.isAttributeType() )
            {
                attributeCheckbox.setEnabled( true );
                attributevalueText.setText( CommonUIUtils.getTextValue( aclAttribute.getName() ) );
            }
            else if ( aclAttribute.isObjectClass() )
            {
                objectClassCheckbox.setEnabled( true );
                attributevalueText.setText( CommonUIUtils.getTextValue( aclAttribute.getName() ) );
            }
            else
            {
                objectClassExclusionCheckbox.setEnabled( true );
                attributevalueText.setText( CommonUIUtils.getTextValue( aclAttribute.getName() ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewElement()
    {
        // Default to none
        setEditedElement( new AclAttributeWrapper( new AclAttribute( "", connection ) ) );
    }

    
    /**
     * Add an Element that will be edited
     * 
     * @param editedElement The element to edit
     */
    public void addNewElement( AclAttributeWrapper editedElement )
    {
        AclAttributeWrapper newElement = (AclAttributeWrapper)editedElement.clone();
        setEditedElement( newElement );
    }
}
