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

package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers;


import java.util.Comparator;

import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;


/**
 * This class is used to compare and sort ascending two DisplayableTreeElement
 */
public class OidSorter implements Comparator<TreeNode>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( TreeNode o1, TreeNode o2 )
    {
        if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            AttributeTypeImpl at1 = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            AttributeTypeImpl at2 = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            return at1.getOid().compareToIgnoreCase( at2.getOid() );
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            ObjectClassImpl oc1 = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            ObjectClassImpl oc2 = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            return oc1.getOid().compareToIgnoreCase( oc2.getOid() );
        }
        else if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            return at.getOid().compareToIgnoreCase( oc.getOid() );
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            return oc.getOid().compareToIgnoreCase( at.getOid() );
        }

        // Default
        return o1.toString().compareToIgnoreCase( o2.toString() );
    }
}
