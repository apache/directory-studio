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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.studio.ldapbrowser.common.wizards.AttributeWizard;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;


/**
 * This Action is used to edit an attribute description within the entry edtitor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditAttributeDescriptionAction extends BrowserAction
{

    /** To avoid duplicate implementations of the isEnabled() code we use a delete action */
    private EntryEditorActionProxy deleteActionProxy;


    /**
     * Creates a new instance of EditAttributeDescriptionAction.
     * 
     * @param viewer the viewer
     */
    public EditAttributeDescriptionAction( Viewer viewer )
    {
        deleteActionProxy = new EntryEditorActionProxy( viewer, new DeleteAction() );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandId()
    {
        return BrowserCommonConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getText()
    {
        return Messages.getString( "EditAttributeDescriptionAction.EditAttributeDescription" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled()
    {
        return deleteActionProxy.getAction().isEnabled();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        if ( getSelectedAttributes().length == 1 )
        {
            renameValues( getSelectedAttributes()[0].getValues() );
        }
        else if ( getSelectedValues().length > 0 )
        {
            renameValues( getSelectedValues() );
        }
    }


    /**
     * Rename the given values.
     * 
     * @param values the values
     */
    private void renameValues( final IValue[] values )
    {
        AttributeWizard wizard = new AttributeWizard( Messages
            .getString( "EditAttributeDescriptionAction.EditAttributeDescription" ), true, false, //$NON-NLS-1$
            values[0].getAttribute().getDescription(), values[0].getAttribute().getEntry() );
        WizardDialog dialog = new WizardDialog( Display.getDefault().getActiveShell(), wizard );
        dialog.setBlockOnOpen( true );
        dialog.create();
        if ( dialog.open() == Dialog.OK )
        {
            String newAttributeDescription = wizard.getAttributeDescription();
            new CompoundModification().renameValues( values, newAttributeDescription );
        }
    }

}
