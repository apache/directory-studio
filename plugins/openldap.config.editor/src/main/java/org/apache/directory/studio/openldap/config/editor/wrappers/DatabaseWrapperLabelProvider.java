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
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginUtils;
import org.apache.directory.studio.openldap.config.editor.databases.DatabaseTypeEnum;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

/**
 * This class defines a label provider for a database wrapper viewer. We use a StyledCellLabelProvider
 * parent, to be able to grey the disabled databases.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabaseWrapperLabelProvider extends StyledCellLabelProvider
{
    /** The Style to use when a database is disabled */
    private static final Styler grayedStyle = new Styler() 
    {
        @Override
        public void applyStyles( TextStyle textStyle ) 
        {
            textStyle.foreground = CommonUIConstants.L_GREY_COLOR;
        }
    };
    
    
    /**
     * Get the Database image, if it's a Database. We can show two different icons, depending
     * on the Database status : enabled or disabled.
     */
    public Image getImage( Object element )
    {
        if ( element instanceof DatabaseWrapper )
        {
            // the olcDisabled AT is only present in 2.5
            // TODO : check with the schemaManager
            /*
            DatabaseWrapper database = (DatabaseWrapper) element;
            Boolean disabled = database.getDatabase().getOlcDisabled();
            
            if ( ( disabled == null ) || !disabled )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_DATABASE );
            }
            else
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_DISABLED_DATABASE );
            }
             */

            return OpenLdapConfigurationPlugin.getDefault().getImage(
                OpenLdapConfigurationPluginConstants.IMG_DATABASE );
        }

        return null;
    };
    
    
    /**
     * Shows the Database name, and grey it if it's disabled.
     * 
     * {@inheritDoc}
     */
    @Override
    public void update( ViewerCell cell ) 
    {
        Object element = cell.getElement();

        if ( element instanceof DatabaseWrapper ) 
        {
            DatabaseWrapper database = (DatabaseWrapper) element;

            String databaseName = getDatabaseType( database.getDatabase() ) + " (" + getSuffix( database.getDatabase() ) + ")";
            
            // the olcDisabled AT is only present in 2.5
            // TODO : check with the schemaManager
            /*
            Boolean disabled = database.getDatabase().getOlcDisabled();
            StyledString styledString = null;
            
            // Grey the database if it's disabled.
            if ( ( disabled == null ) || !disabled )
            {  
                styledString = new StyledString( databaseName, grayedStyle );
            }
            else
            {
                styledString = new StyledString( databaseName, null );
            }
            */
            
            StyledString styledString = new StyledString( databaseName, null );
            cell.setText( styledString.toString() );
            cell.setStyleRanges( styledString.getStyleRanges() );
            cell.setImage( getImage( database ) );
        }

        super.update(cell);
    }


    /**
     * Return the database type.
     */
    private String getDatabaseType( OlcDatabaseConfig database )
    {
        if ( database != null )
        {
            String databaseType = OpenLdapConfigurationPluginUtils.stripOrderingPrefix( database.getOlcDatabase() );

            DatabaseTypeEnum databasetype = DatabaseTypeEnum.valueOf( Strings.toUpperCase( databaseType ) );
            
            if ( databaseType != null )
            {
                return databasetype.getName();
            }
            else
            {
                return DatabaseTypeEnum.NONE.getName();
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

        return DatabaseTypeEnum.NONE.getName();
    }
}
