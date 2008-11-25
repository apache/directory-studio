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

package org.apache.directory.studio.connection.ui;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;


/**
 * The ConnectionParameterPageManager manages the {@link ConnectionParameterPage}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionParameterPageManager
{

    /**
     * Gets the connection parameter pages by searching for connection parameter page
     * extensions.
     * 
     * @return the connection parameter pages
     */
    public static ConnectionParameterPage[] getConnectionParameterPages()
    {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( ConnectionUIPlugin.getDefault()
            .getPluginProperties().getString( "ExtensionPoint_ConnectionParameterPages_id" ) ); //$NON-NLS-1$
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();
        final Map<String, ConnectionParameterPage> pageMap = new HashMap<String, ConnectionParameterPage>();

        // For each extension: instantiate the page
        for ( int m = 0; m < members.length; m++ )
        {
            IConfigurationElement member = members[m];
            try
            {
                ConnectionParameterPage page = ( ConnectionParameterPage ) member.createExecutableExtension( "class" ); //$NON-NLS-1$
                page.setPageId( member.getAttribute( "id" ) ); //$NON-NLS-1$
                page.setPageName( member.getAttribute( "name" ) ); //$NON-NLS-1$
                page.setPageDescription( member.getAttribute( "description" ) ); //$NON-NLS-1$
                page.setPageDependsOnId( member.getAttribute( "dependsOnId" ) ); //$NON-NLS-1$
                pageMap.put( page.getPageId(), page );
            }
            catch ( Exception e )
            {
                ConnectionUIPlugin.getDefault().getLog().log(
                    new Status( IStatus.ERROR, ConnectionUIConstants.PLUGIN_ID, 1,
                        Messages.getString("ConnectionParameterPageManager.UnableCreateConnectionParamPage") + member.getAttribute( "class" ), e ) ); //$NON-NLS-2$
            }
        }

        final ConnectionParameterPage[] pages = pageMap.values().toArray( new ConnectionParameterPage[0] );
        Comparator<? super ConnectionParameterPage> pageComparator = new Comparator<ConnectionParameterPage>()
        {
            public int compare( ConnectionParameterPage p1, ConnectionParameterPage p2 )
            {
                String dependsOnId1 = p1.getPageDependsOnId();
                String dependsOnId2 = p2.getPageDependsOnId();
                do
                {
                    if ( dependsOnId1 == null && dependsOnId2 != null )
                    {
                        return -1;
                    }
                    else if ( dependsOnId2 == null && dependsOnId1 != null )
                    {
                        return 1;
                    }
                    else if ( dependsOnId1 != null && dependsOnId1.equals( p2.getPageId() ) )
                    {
                        return 1;
                    }
                    else if ( dependsOnId2 != null && dependsOnId2.equals( p1.getPageId() ) )
                    {
                        return -1;
                    }

                    ConnectionParameterPage page = pageMap.get( dependsOnId1 );
                    if ( page != null )
                    {
                        dependsOnId1 = page.getPageDependsOnId();
                    }
                    else
                    {
                        dependsOnId1 = null;
                    }
                }
                while ( dependsOnId1 != null && !dependsOnId1.equals( p1.getPageId() ) );

                dependsOnId1 = p1.getPageDependsOnId();
                dependsOnId2 = p2.getPageDependsOnId();
                do
                {
                    if ( dependsOnId1 == null && dependsOnId2 != null )
                    {
                        return -1;
                    }
                    else if ( dependsOnId2 == null && dependsOnId1 != null )
                    {
                        return 1;
                    }
                    else if ( dependsOnId1 != null && dependsOnId1.equals( p2.getPageId() ) )
                    {
                        return 1;
                    }
                    else if ( dependsOnId2 != null && dependsOnId2.equals( p1.getPageId() ) )
                    {
                        return -1;
                    }

                    ConnectionParameterPage page = pageMap.get( dependsOnId2 );
                    if ( page != null )
                    {
                        dependsOnId2 = page.getPageDependsOnId();
                    }
                    else
                    {
                        dependsOnId2 = null;
                    }
                }
                while ( dependsOnId2 != null && !dependsOnId2.equals( p2.getPageId() ) );

                return 0;
            }
        };
        Arrays.sort( pages, pageComparator );

        return pages;
    }
}
