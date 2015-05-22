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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This class defines a sorter for a database wrapper viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabaseWrapperViewerSorter extends ViewerSorter
{
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        if ( ( e1 != null ) && ( e2 != null ) && ( e1 instanceof DatabaseWrapper )
            && ( e2 instanceof DatabaseWrapper ) )
        {
            OlcDatabaseConfig database1 = ( ( DatabaseWrapper ) e1 ).getDatabase();
            OlcDatabaseConfig database2 = ( ( DatabaseWrapper ) e2 ).getDatabase();
            boolean db1HasOrderingPrefix = OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database1
                .getOlcDatabase() );
            boolean db2HasOrderingPrefix = OpenLdapConfigurationPluginUtils.hasOrderingPrefix( database2
                .getOlcDatabase() );

            if ( db1HasOrderingPrefix && db2HasOrderingPrefix )
            {
                int orderingPrefix1 = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database1
                    .getOlcDatabase() );
                int orderingPrefix2 = OpenLdapConfigurationPluginUtils.getOrderingPrefix( database2
                    .getOlcDatabase() );

                if ( orderingPrefix1 > orderingPrefix2 )
                {
                    return Integer.MAX_VALUE;
                }
                else if ( orderingPrefix1 < orderingPrefix2 )
                {
                    return Integer.MIN_VALUE;
                }
                else
                {
                    return 0;
                }
            }
            else if ( db1HasOrderingPrefix )
            {
                return Integer.MIN_VALUE;
            }
            else if ( db2HasOrderingPrefix )
            {
                return Integer.MAX_VALUE;
            }
            else
            {
                return 1;
            }
        }

        return super.compare( viewer, e1, e2 );
    }
}
