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

package org.apache.directory.studio.schemaeditor.view.widget;


import java.util.Comparator;

import org.apache.directory.api.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.model.difference.AttributeTypeDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ObjectClassDifference;


/**
 * This class is used to compare and sort ascending two DisplayableTreeElement
 */
public class OidSorter implements Comparator<Object>
{
    /**
     * {@inheritDoc}
     */
    public int compare( Object o1, Object o2 )
    {
        String oid1 = ""; //$NON-NLS-1$
        String oid2 = ""; //$NON-NLS-1$

        if ( ( o1 instanceof AttributeTypeDifference ) && ( o2 instanceof AttributeTypeDifference ) )
        {
            AttributeTypeDifference atd1 = ( AttributeTypeDifference ) o1;
            AttributeTypeDifference atd2 = ( AttributeTypeDifference ) o2;

            switch ( atd1.getType() )
            {
                case ADDED:
                    oid1 = ( ( SchemaObject ) atd1.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid1 = ( ( SchemaObject ) atd1.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid1 = ( ( SchemaObject ) atd1.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid1 = ( ( SchemaObject ) atd1.getDestination() ).getOid();
                    break;
            }

            switch ( atd2.getType() )
            {
                case ADDED:
                    oid2 = ( ( SchemaObject ) atd2.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid2 = ( ( SchemaObject ) atd2.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid2 = ( ( SchemaObject ) atd2.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid2 = ( ( SchemaObject ) atd2.getDestination() ).getOid();
                    break;
            }
        }
        else if ( ( o1 instanceof ObjectClassDifference ) && ( o2 instanceof ObjectClassDifference ) )
        {
            ObjectClassDifference ocd1 = ( ObjectClassDifference ) o1;
            ObjectClassDifference ocd2 = ( ObjectClassDifference ) o2;

            switch ( ocd1.getType() )
            {
                case ADDED:
                    oid1 = ( ( SchemaObject ) ocd1.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid1 = ( ( SchemaObject ) ocd1.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid1 = ( ( SchemaObject ) ocd1.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid1 = ( ( SchemaObject ) ocd1.getDestination() ).getOid();
                    break;
            }

            switch ( ocd2.getType() )
            {
                case ADDED:
                    oid2 = ( ( SchemaObject ) ocd2.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid2 = ( ( SchemaObject ) ocd2.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid2 = ( ( SchemaObject ) ocd2.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid2 = ( ( SchemaObject ) ocd2.getDestination() ).getOid();
                    break;
            }
        }
        else if ( ( o1 instanceof AttributeTypeDifference ) && ( o2 instanceof ObjectClassDifference ) )
        {
            AttributeTypeDifference atd = ( AttributeTypeDifference ) o1;
            ObjectClassDifference ocd = ( ObjectClassDifference ) o2;

            switch ( atd.getType() )
            {
                case ADDED:
                    oid1 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid1 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid1 = ( ( SchemaObject ) atd.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid1 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
            }

            switch ( ocd.getType() )
            {
                case ADDED:
                    oid2 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid2 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid2 = ( ( SchemaObject ) ocd.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid2 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
            }
        }
        else if ( ( o1 instanceof ObjectClassDifference ) && ( o2 instanceof AttributeTypeDifference ) )
        {
            ObjectClassDifference ocd = ( ObjectClassDifference ) o1;
            AttributeTypeDifference atd = ( AttributeTypeDifference ) o2;

            switch ( ocd.getType() )
            {
                case ADDED:
                    oid1 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid1 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid1 = ( ( SchemaObject ) ocd.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid1 = ( ( SchemaObject ) ocd.getDestination() ).getOid();
                    break;
            }

            switch ( atd.getType() )
            {
                case ADDED:
                    oid2 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
                case MODIFIED:
                    oid2 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
                case REMOVED:
                    oid2 = ( ( SchemaObject ) atd.getSource() ).getOid();
                    break;
                case IDENTICAL:
                    oid2 = ( ( SchemaObject ) atd.getDestination() ).getOid();
                    break;
            }
        }

        return oid1.compareToIgnoreCase( oid2 );
    }
}
