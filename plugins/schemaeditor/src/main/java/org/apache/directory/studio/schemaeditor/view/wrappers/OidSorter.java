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

package org.apache.directory.studio.schemaeditor.view.wrappers;


import java.util.Comparator;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;


/**
 * This class is used to compare and sort ascending two DisplayableTreeElement
 */
public class OidSorter implements Comparator<TreeNode>
{
    /**
     * {@inheritDoc}
     */
    public int compare( TreeNode o1, TreeNode o2 )
    {
        if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            AttributeType at1 = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            AttributeType at2 = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            return at1.getOid().compareToIgnoreCase( at2.getOid() );
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            ObjectClass oc1 = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            ObjectClass oc2 = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            return oc1.getOid().compareToIgnoreCase( oc2.getOid() );
        }
        else if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            AttributeType at = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            ObjectClass oc = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            return at.getOid().compareToIgnoreCase( oc.getOid() );
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            ObjectClass oc = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            AttributeType at = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            return oc.getOid().compareToIgnoreCase( at.getOid() );
        }

        // Default
        return o1.toString().compareToIgnoreCase( o2.toString() );
    }
}
