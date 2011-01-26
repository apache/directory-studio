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

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;


/**
 * This class implements the Comparator used to compare elements in the Matching Rules Content Providers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATESuperiorComboComparator implements Comparator<Object>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 )
    {
        if ( o1 instanceof AttributeType && o2 instanceof AttributeType )
        {
            List<String> at1Names = ( ( AttributeType ) o1 ).getNames();
            List<String> at2Names = ( ( AttributeType ) o2 ).getNames();

            if ( ( at1Names != null ) && ( at2Names != null ) && ( at1Names.size() > 0 ) && ( at2Names.size() > 0 ) )
            {
                return at1Names.get( 0 ).compareToIgnoreCase( at2Names.get( 0 ) );
            }
        }
        else if ( o1 instanceof AttributeType && o2 instanceof NonExistingAttributeType )
        {
            List<String> at1Names = ( ( AttributeType ) o1 ).getNames();
            String at2Name = ( ( NonExistingAttributeType ) o2 ).getName();

            if ( ( at1Names != null ) && ( at2Name != null ) && ( at1Names.size() > 0 ) )
            {
                return at1Names.get( 0 ).compareToIgnoreCase( at2Name );
            }
        }
        else if ( o1 instanceof NonExistingAttributeType && o2 instanceof AttributeType )
        {
            String at1Name = ( ( NonExistingAttributeType ) o1 ).getName();
            List<String> at2Names = ( ( AttributeType ) o2 ).getNames();

            if ( ( at1Name != null ) && ( at2Names != null ) && ( at2Names.size() > 0 ) )
            {
                return at1Name.compareToIgnoreCase( at2Names.get( 0 ) );
            }
        }
        else if ( o1 instanceof NonExistingAttributeType && o2 instanceof NonExistingAttributeType )
        {
            String at1Name = ( ( NonExistingAttributeType ) o1 ).getName();
            String at2Name = ( ( NonExistingAttributeType ) o2 ).getName();

            if ( ( at1Name != null ) && ( at2Name != null ) )
            {
                return at1Name.compareToIgnoreCase( at2Name );
            }
        }

        return 0;
    }
}
