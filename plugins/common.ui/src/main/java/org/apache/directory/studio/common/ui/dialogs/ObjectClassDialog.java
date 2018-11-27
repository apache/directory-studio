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

package org.apache.directory.studio.common.ui.dialogs;


import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.Messages;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.wrappers.StringValueWrapper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The ObjectClassDialog is used to enter/select an ObjectClass. Here is what it looks like :
 * <pre>
 * .---------------------------------------------------------.
 * |X| Select ObjectClass or OID                             |
 * +---------------------------------------------------------|
 * | ObjectClass or OID : [                              |v] |
 * |                                                         |
 * |                                      (CANCEL)  (  OK  ) |
 * '---------------------------------------------------------'
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassDialog extends AddEditDialog<StringValueWrapper>
{
    /** The possible objectClasses and OIDs. */
    private String[] objectClassesAndOids;

    /** The combo containing the list of objectClasses. */
    private Combo objectClassOrOidCombo;


    /**
     * Creates a new instance of ObjectClassDialog.
     * 
     * @param parentShell the parent shell
     */
    public ObjectClassDialog( Shell parentShell )
    {
        super( parentShell );
    }
    
    
    /**
     * Set the list of possible objectClasses and OIDs
     * 
     * @param objectClassesAndOids The list of possible objectClasses and OID
     */
    public void setAttributeNamesAndOids( String[] objectClassesAndOids )
    {
        this.objectClassesAndOids = objectClassesAndOids;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "ObjectClassDialog.SelectObjectClassOrOID" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        setEditedElement( new StringValueWrapper( objectClassOrOidCombo.getText(), false ) );
        super.okPressed();
    }

    
    /**
     * Overriding the createButton method. The OK button is not enabled until we have a selected ObjectClass
     * 
     * {@inheritDoc}
     */
    protected Button createButton( Composite parent, int id, String label, boolean defaultButton ) 
    {
        Button button = super.createButton(parent, id, label, defaultButton);

        if ( id == IDialogConstants.OK_ID ) 
        {
            String objectClass = ((StringValueWrapper)getEditedElement() ).getValue();

            if ( ( objectClass == null ) || ( objectClass.length() == 0 ) )
            {
                button.setEnabled( false );
            }
        }
        
        return button;
    }


    /**
     * Create the ObjectClass dialog :
     * 
     * <pre>
     * .---------------------------------------------------------.
     * |X| Select ObjectClass or OID                             |
     * +---------------------------------------------------------|
     * | ObjectClass or OID : [                           |v] |
     * |                                                         |
     * |                                      (CANCEL)  (  OK  ) |
     * '---------------------------------------------------------'
     * </pre>
     * 
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, Messages.getString( "ObjectClassDialog.ObjectClassOrOID" ), 1 ); //$NON-NLS-1$
        objectClassOrOidCombo = BaseWidgetUtils.createCombo( c, objectClassesAndOids, -1, 1 );
        
        if ( getEditedElement() != null )
        {
            objectClassOrOidCombo.setText( getEditedElement().getValue() );
        }
        
        objectClassOrOidCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        initDialog();

        return composite;
    }

    
    /**
     * {@inheritDoc}
     */
    protected void initDialog()
    {
        // Nothing to do
    }


    /**
     * Check that we have selected an ObjectClass
     */
    private void validate()
    {
        Button okButton = getButton( IDialogConstants.OK_ID );
        
        // This button might be null when the dialog is called.
        if ( okButton == null )
        {
            return;
        }

        okButton.setEnabled( !"".equals( objectClassOrOidCombo.getText() ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewElement()
    {
        // Default to none
        setEditedElement( new StringValueWrapper( "", false  ) );
    }
}
