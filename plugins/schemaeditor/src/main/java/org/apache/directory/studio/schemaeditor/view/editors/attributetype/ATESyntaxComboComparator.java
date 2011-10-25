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
package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingSyntax;


/**
 * This class implements the Comparator used to compare elements in the Matching Rules Content Providers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATESyntaxComboComparator implements Comparator<Object>
{
    /**
     * {@inheritDoc}
     */
    public int compare( Object o1, Object o2 )
    {
        if ( o1 instanceof LdapSyntax && o2 instanceof LdapSyntax )
        {
            List<String> syntax1Names = ( ( LdapSyntax ) o1 ).getNames();
            List<String> syntax2Names = ( ( LdapSyntax ) o2 ).getNames();

            if ( ( syntax1Names != null ) && ( syntax2Names != null ) && ( syntax1Names.size() > 0 )
                && ( syntax2Names.size() > 0 ) )
            {
                return syntax1Names.get( 0 ).compareToIgnoreCase( syntax2Names.get( 0 ) );
            }
        }
        else if ( o1 instanceof LdapSyntax && o2 instanceof NonExistingSyntax )
        {
            List<String> syntax1Names = ( ( LdapSyntax ) o1 ).getNames();
            String syntax2Name = ( ( NonExistingSyntax ) o2 ).getName();

            if ( ( syntax1Names != null ) && ( syntax2Name != null ) && ( syntax1Names.size() > 0 ) )
            {
                return syntax1Names.get( 0 ).compareToIgnoreCase( syntax2Name );
            }
        }
        else if ( o1 instanceof NonExistingSyntax && o2 instanceof LdapSyntax )
        {
            String syntax1Name = ( ( NonExistingSyntax ) o1 ).getName();
            List<String> syntax2Names = ( ( LdapSyntax ) o2 ).getNames();

            if ( ( syntax1Name != null ) && ( syntax2Names != null ) && ( syntax2Names.size() > 0 ) )
            {
                return syntax1Name.compareToIgnoreCase( syntax2Names.get( 0 ) );
            }
        }
        else if ( o1 instanceof NonExistingSyntax && o2 instanceof NonExistingSyntax )
        {
            String syntax1Name = ( ( NonExistingSyntax ) o1 ).getName();
            String syntax2Name = ( ( NonExistingSyntax ) o2 ).getName();

            if ( ( syntax1Name != null ) && ( syntax2Name != null ) )
            {
                return syntax1Name.compareToIgnoreCase( syntax2Name );
            }
        }

        return 0;
    }
}
