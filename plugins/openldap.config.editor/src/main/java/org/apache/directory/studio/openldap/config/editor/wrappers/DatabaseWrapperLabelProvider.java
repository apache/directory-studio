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

import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * This class defines a label provider for a database wrapper viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabaseWrapperLabelProvider extends LabelProvider
{
    /**
     * Construct the label for a database. It's the type, followed by the suffixDN.
     */
    public String getText( Object element )
    {
        if ( element instanceof DatabaseWrapper )
        {
            OlcDatabaseConfig database = ( ( DatabaseWrapper ) element ).getDatabase();

            return getDatabaseType( database ) + " (" + getSuffix( database ) + ")";
        }

        return super.getText( element );
    };


    /**
     * Get the Database image, if it's a Database
     */
    public Image getImage( Object element )
    {
        if ( element instanceof DatabaseWrapper )
        {
            return OpenLdapConfigurationPlugin.getDefault().getImage(
                OpenLdapConfigurationPluginConstants.IMG_DATABASE );
        }

        return super.getImage( element );
    };


    private String getDatabaseType( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            String databaseType = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() );

            if ( "bdb".equalsIgnoreCase( databaseType ) )
            {
                return "BDB";
            }
            else if ( "hdb".equalsIgnoreCase( databaseType ) )
            {
                return "HDB";
            }
            else if ( "mdb".equalsIgnoreCase( databaseType ) )
            {
                return "MDB";
            }
            else if ( "ldap".equalsIgnoreCase( databaseType ) )
            {
                return "LDAP";
            }
            else if ( "ldif".equalsIgnoreCase( databaseType ) )
            {
                return "LDIF";
            }
            else if ( "null".equalsIgnoreCase( databaseType ) )
            {
                return "Null";
            }
            else if ( "relay".equalsIgnoreCase( databaseType ) )
            {
                return "Relay";
            }
            else if ( "frontend".equalsIgnoreCase( databaseType ) )
            {
                return "FrontEnd";
            }
            else if ( "config".equalsIgnoreCase( databaseType ) )
            {
                return "Config";
            }
            else
            {
                return "None";
            }
        }

        return null;
    }


    /**
     * Return the Database suffix DN
     */
    private String getSuffix( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            List<Dn> suffixes = database.getOlcSuffix();

            if ( ( suffixes != null ) && ( suffixes.size() > 0 ) )
            {
                return suffixes.get( 0 ).toString();
            }
        }

        return "none";
    }

}
