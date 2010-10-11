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
package org.apache.directory.studio.ldapservers.wizards;


import org.apache.directory.studio.ldapservers.LdapServersPlugin;
import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements a {@link ILabelProvider} for LDAP Server Adapter Extensions {@link TreeViewer}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServerAdapterExtensionsLabelProvider extends LabelProvider
{
    /**
    * {@inheritDoc}
    */
    public Image getImage( Object element )
    {
        if ( element instanceof String )
        {
            return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_FOLDER );
        }
        else if ( element instanceof LdapServerAdapterExtension )
        {
            return LdapServersPlugin.getDefault().getImage( LdapServersPluginConstants.IMG_SERVER );
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getText( Object element )
    {
        if ( element instanceof String )
        {
            return ( String ) element;

        }
        else if ( element instanceof LdapServerAdapterExtension )
        {
            LdapServerAdapterExtension extension = ( LdapServerAdapterExtension ) element;

            return extension.getName() + " " + extension.getVersion(); //$NON-NLS-1$
        }

        return super.getText( element );
    }
}
