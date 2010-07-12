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
package org.apache.directory.studio.ldapservers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the LDAP Server Extensions Manager.
 * <p>
 * It is used to store all the LDAP Server Extensions.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServerAdapterExtensionsManager
{
    /** The default instance */
    private static LdapServerAdapterExtensionsManager instance;

    /** The list and map for LDAP Server Adapter Extensions */
    private List<LdapServerAdapterExtension> ldapServerAdapterExtensionsList;

    /** The map and map for LDAP Server Adapter Extensions */
    private Map<String, LdapServerAdapterExtension> ldapServerAdapterExtensionsByIdMap;


    /**
     * Creates a new instance of LdapServerAdapterExtensionsManager.
     */
    private LdapServerAdapterExtensionsManager()
    {
    }


    /**
     * Loads the LDAP Server Adapter Extensions.
     */
    public void loadLdapServerAdapterExtensions()
    {
        // Initializing the list and map for LDAP Server Adapter Extensions
        ldapServerAdapterExtensionsList = new ArrayList<LdapServerAdapterExtension>();
        ldapServerAdapterExtensionsByIdMap = new HashMap<String, LdapServerAdapterExtension>();

        // Getting members of LDAP Server Adapters Extension Point
        IConfigurationElement[] members = Platform.getExtensionRegistry()
            .getExtensionPoint( LdapServersPluginConstants.LDAP_SERVER_ADAPTERS_EXTENSION_POINT )
            .getConfigurationElements();

        // Creating an object associated with each member
        for ( IConfigurationElement member : members )
        {
            // Creating the LdapServerAdapterExtension object container
            LdapServerAdapterExtension ldapServerAdapterExtension = new LdapServerAdapterExtension();

            // Getting the ID of the extending plugin
            String extendingPluginId = member.getDeclaringExtension().getNamespaceIdentifier();

            // Setting 
            ldapServerAdapterExtension.setId( member.getAttribute( "id" ) );
            ldapServerAdapterExtension.setName( member.getAttribute( "name" ) );
            ldapServerAdapterExtension.setVersion( member.getAttribute( "version" ) );
            ldapServerAdapterExtension.setVendorName( member.getAttribute( "vendorName" ) );
            ldapServerAdapterExtension.setClassName( member.getAttribute( "class" ) );
            ldapServerAdapterExtension.setDescription( member.getAttribute( "description" ) );
            String iconPath = member.getAttribute( "icon" );
            if ( iconPath != null )
            {
                ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin( extendingPluginId, iconPath );
                if ( icon == null )
                {
                    icon = ImageDescriptor.getMissingImageDescriptor();
                }
                ldapServerAdapterExtension.setIcon( icon );
            }

            // TODO uncomment this...
            //            try
            //            {
            //                bean.setEditorInstance( ( IEntryEditor ) member.createExecutableExtension( CLASS_ATTR ) );
            //            }
            //            catch ( CoreException e )
            //            {
            //                // Will never happen
            //            }

            ldapServerAdapterExtensionsList.add( ldapServerAdapterExtension );
            ldapServerAdapterExtensionsByIdMap.put( ldapServerAdapterExtension.getId(), ldapServerAdapterExtension );
        }
    }


    /**
     * Gets the default {@link LdapServerAdapterExtensionsManager} (singleton pattern).
     *
     * @return
     *      the default {@link LdapServerAdapterExtensionsManager}
     */
    public static LdapServerAdapterExtensionsManager getDefault()
    {
        if ( instance == null )
        {
            instance = new LdapServerAdapterExtensionsManager();
        }

        return instance;
    }


    /**
     * Gets the LDAP Server Adapter Extensions list.
     *
     * @return
     *      the LDAP Server Adapter Extensions list.
     */
    public List<LdapServerAdapterExtension> getLdapServerAdapterExtensions()
    {
        return ldapServerAdapterExtensionsList;
    }


    /**
     * Gets the LDAP Server Adapter Extension associated with the given id.
     *
     * @return
     *      the LDAP Server Adapter Extension associated with the given id.
     */
    public LdapServerAdapterExtension getLdapServerAdapterExtensionById( String id )
    {
        return ldapServerAdapterExtensionsByIdMap.get( id );
    }
}
