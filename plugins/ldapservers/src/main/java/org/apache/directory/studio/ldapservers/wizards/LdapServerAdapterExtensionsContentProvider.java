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


import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.studio.ldapservers.LdapServerAdapterExtensionsManager;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements a {@link ITreeContentProvider} for LDAP Server Adapter Extensions {@link TreeViewer}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapServerAdapterExtensionsContentProvider implements ITreeContentProvider
{
    /** The {@link MultiValueMap} used to store LDAP Server Adapter Extensions and order them by vendor (used as key) */
    private MultiValueMap ldapServerAdapterExtensionsMap = new MultiValueMap();


    /**
     * Creates a new instance of LdapServerAdaptersContentProvider.
     */
    public LdapServerAdapterExtensionsContentProvider()
    {
        for ( LdapServerAdapterExtension extension : LdapServerAdapterExtensionsManager.getDefault()
            .getLdapServerAdapterExtensions() )
        {
            ldapServerAdapterExtensionsMap.put( extension.getVendor(), extension );
        }
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object inputElement )
    {
        return ldapServerAdapterExtensionsMap.keySet().toArray();
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getChildren( Object parentElement )
    {
        Object children = ldapServerAdapterExtensionsMap.get( parentElement );
        if ( children != null )
        {
            if ( children instanceof List )
            {
                return ( ( List<?> ) children ).toArray();
            }
            else
            {
                return new Object[]
                    { children };
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof String )
        {
            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public Object getParent( Object element )
    {
        // Hierarchy is only descending.
        // Should not be used.
        return null;
    }
}
