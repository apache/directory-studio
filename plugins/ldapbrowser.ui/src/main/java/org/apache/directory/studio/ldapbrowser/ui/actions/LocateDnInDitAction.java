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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This action is used within the entry editor and search result editor 
 * to locate and open the entry identified by the DN under the cursor. 
 * 
 * Example: Attribute "seeAlso" with value "ou=test" is selected in entry edtor. 
 * Then this action is enabled and opens entry "ou=test" in DIT. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LocateDnInDitAction extends LocateInDitAction
{
    /**
     * Creates a new instance of LocateDnInDitAction.
     */
    public LocateDnInDitAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return Messages.getString( "LocateDnInDitAction.LocateDN" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LOCATE_DN_IN_DIT );
    }


    /**
     * This implementation returns a connection and DN if the selected attribute or value
     * contains a valid DN.
     */
    protected ConnectionAndDn getConnectionAndDn()
    {

        if ( getSelectedAttributeHierarchies().length == 1
            && getSelectedAttributeHierarchies()[0].getAttribute().getValueSize() == 1
            && getSelectedSearchResults().length == 1 )
        {
            try
            {
                IValue value = getSelectedAttributeHierarchies()[0].getAttribute().getValues()[0];
                if ( value.isString() && DN.isValid( value.getStringValue() ) )
                {
                    return new ConnectionAndDn( value.getAttribute().getEntry().getBrowserConnection(), new DN(
                        value.getStringValue() ) );
                }
            }
            catch ( LdapInvalidDnException e )
            {
                // no valid DN
            }
        }

        if ( getSelectedValues().length == 1 && getSelectedAttributes().length == 0 )
        {
            try
            {
                IValue value = getSelectedValues()[0];
                if ( value.isString() && DN.isValid( value.getStringValue() ) )
                {
                    return new ConnectionAndDn( value.getAttribute().getEntry().getBrowserConnection(), new DN(
                        value.getStringValue() ) );
                }
            }
            catch ( LdapInvalidDnException e )
            {
                // no valid DN
            }
        }

        if ( getSelectedSearchResults().length == 1 && getSelectedAttributeHierarchies().length == 0 )
        {
            ISearchResult result = getSelectedSearchResults()[0];
            return new ConnectionAndDn( result.getEntry().getBrowserConnection(), result.getEntry().getDn() );
        }

        return null;
    }
}
