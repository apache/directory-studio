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
        String syntax1Description = null;
        String syntax2Description = null;

        if ( o1 instanceof LdapSyntax && o2 instanceof LdapSyntax )
        {
            syntax1Description = ( ( LdapSyntax ) o1 ).getDescription();
            syntax2Description = ( ( LdapSyntax ) o2 ).getDescription();

        }
        else if ( o1 instanceof LdapSyntax && o2 instanceof NonExistingSyntax )
        {
            syntax1Description = ( ( LdapSyntax ) o1 ).getDescription();
            syntax2Description = ( ( NonExistingSyntax ) o2 ).getDescription();
        }
        else if ( o1 instanceof NonExistingSyntax && o2 instanceof LdapSyntax )
        {
            syntax1Description = ( ( NonExistingSyntax ) o1 ).getDescription();
            syntax2Description = ( ( LdapSyntax ) o2 ).getDescription();
        }
        else if ( o1 instanceof NonExistingSyntax && o2 instanceof NonExistingSyntax )
        {
            syntax1Description = ( ( NonExistingSyntax ) o1 ).getDescription();
            syntax2Description = ( ( NonExistingSyntax ) o2 ).getDescription();
        }

        if ( ( syntax1Description != null ) && ( syntax2Description != null ) )
        {
            return syntax1Description.compareToIgnoreCase( syntax2Description );
        }

        return 0;
    }
}
