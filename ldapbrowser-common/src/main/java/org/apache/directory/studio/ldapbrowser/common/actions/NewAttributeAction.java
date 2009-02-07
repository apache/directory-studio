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

package org.apache.directory.studio.ldapbrowser.common.actions;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.wizards.AttributeWizard;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;


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
            AttributeWizard wizard = new AttributeWizard( Messages.getString("NewAttributeAction.NewAttribute"), true, true, null, entry ); //$NON-NLS-1$
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            if ( dialog.open() == WizardDialog.OK )
            {
                String newAttributeDescription = wizard.getAttributeDescription();
                if ( newAttributeDescription != null && !"".equals( newAttributeDescription ) ) //$NON-NLS-1$
                {
                    IAttribute att = entry.getAttribute( newAttributeDescription );
                    if ( att == null )
                    {
                        att = new Attribute( entry, newAttributeDescription );
                        entry.addAttribute( att );
                    }

                    att.addEmptyValue();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString("NewAttributeAction.NewAttributeLabel"); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserCommonActivator.getDefault().getImageDescriptor( BrowserCommonConstants.IMG_ATTRIBUTE_ADD );
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return BrowserCommonConstants.CMD_ADD_ATTRIBUTE;
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
