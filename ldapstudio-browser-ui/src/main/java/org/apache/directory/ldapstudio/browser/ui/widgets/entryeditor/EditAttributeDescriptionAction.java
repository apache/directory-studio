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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.core.jobs.RenameValuesJob;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.actions.DeleteAction;
import org.apache.directory.ldapstudio.browser.ui.wizards.AttributeWizard;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;


public class EditAttributeDescriptionAction extends AbstractEntryEditorListenerAction
{

    private DeleteAction deleteAction;


    public EditAttributeDescriptionAction( ISelectionProvider selectionProvider )
    {
        super( selectionProvider, "Edit Attribute Description", null,
            BrowserUIConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION );
        this.deleteAction = new DeleteAction();
    }


    public void dispose()
    {
        super.dispose();
        this.deleteAction.dispose();
        this.deleteAction = null;
    }


    public void run()
    {
        if ( this.selectedAttributes.length == 1 )
        {
            this.renameValues( this.selectedAttributes[0].getValues() );
        }
        else if ( this.selectedValues.length > 0 )
        {
            this.renameValues( this.selectedValues );
        }
    }


    private void renameValues( final IValue[] values )
    {
        AttributeWizard wizard = new AttributeWizard( "Edit Attribute Description", true, false, values[0]
            .getAttribute().getDescription(), values[0].getAttribute().getEntry() );
        WizardDialog dialog = new WizardDialog( Display.getDefault().getActiveShell(), wizard );
        dialog.setBlockOnOpen( true );
        dialog.create();
        if ( dialog.open() == Dialog.OK )
        {
            String newAttributeName = wizard.getAttributeDescription();
            if ( newAttributeName != null && !"".equals( newAttributeName )
                && !newAttributeName.equals( values[0].getAttribute().getDescription() ) )
            {
                new RenameValuesJob( this.selectedEntry, values, newAttributeName ).execute();
            }
        }
    }


    protected void updateEnabledState()
    {

        if ( this.deleteAction != null )
        {
            deleteAction.setSelectedAttributes( this.selectedAttributes );
            deleteAction.setSelectedValues( this.selectedValues );
            super.setEnabled( deleteAction.isEnabled() );
        }
        else
        {
            super.setEnabled( false );
        }
    }

}
