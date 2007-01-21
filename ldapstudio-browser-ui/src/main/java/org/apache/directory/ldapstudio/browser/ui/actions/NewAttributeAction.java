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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.wizards.AttributeWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;


/**
 * This Action creates a new Attribute
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeAction extends BrowserAction
{
    /**
     * Creates a new instance of NewAttributeAction.
     */
    public NewAttributeAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {

        IEntry entry = null;
        if ( getInput() != null && getInput() instanceof IEntry )
        {
            entry = ( IEntry ) getInput();
        }
        else if ( getSelectedEntries().length > 0 )
        {
            entry = getSelectedEntries()[0];
        }
        else if ( getSelectedAttributes().length > 0 )
        {
            entry = getSelectedAttributes()[0].getEntry();
        }
        else if ( getSelectedValues().length > 0 )
        {
            entry = getSelectedValues()[0].getAttribute().getEntry();
        }

        if ( entry != null )
        {
            AttributeWizard wizard = new AttributeWizard( "New Attribute", true, true, null, entry );
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            if ( dialog.open() == WizardDialog.OK )
            {
                String newAttributeDescription = wizard.getAttributeDescription();
                if ( newAttributeDescription != null && !"".equals( newAttributeDescription ) )
                {
                    try
                    {
                        IAttribute att = entry.getAttribute( newAttributeDescription );
                        if ( att == null )
                        {
                            att = new Attribute( entry, newAttributeDescription );
                            entry.addAttribute( att ) ;
                        }

                        att.addEmptyValue();
                    }
                    catch ( ModelModificationException mme )
                    {
                        MessageDialog.openError( Display.getDefault().getActiveShell(), "Error While Adding Attribute",
                            mme.getMessage() );
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "New Attribute...";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ATTRIBUTE_ADD );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return "org.apache.directory.ldapstudio.browser.action.addAttribute";
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {

        if ( ( getSelectedSearchResults().length == 1 && getSelectedAttributes().length > 0 ) )
        {
            return false;
        }

        return ( ( getInput() != null && getInput() instanceof IEntry ) || getSelectedEntries().length == 1
            || getSelectedAttributes().length > 0 || getSelectedValues().length > 0 );
    }
}
