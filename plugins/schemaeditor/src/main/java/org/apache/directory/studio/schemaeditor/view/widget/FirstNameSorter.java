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
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.model.difference.AttributeTypeDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ObjectClassDifference;


/**
 * This class is used to compare and sort ascending two TreeNode.
 */
public class FirstNameSorter implements Comparator<Object>
{
    /**
     * {@inheritDoc}
     */
    public int compare( Object o1, Object o2 )
    {
        List<String> o1Names = null;
        List<String> o2Names = null;

        if ( ( o1 instanceof AttributeTypeDifference ) && ( o2 instanceof AttributeTypeDifference ) )
        {
            AttributeTypeDifference atd1 = ( AttributeTypeDifference ) o1;
            AttributeTypeDifference atd2 = ( AttributeTypeDifference ) o2;

            switch ( atd1.getType() )
            {
                case ADDED:
                    o1Names = ( ( SchemaObject ) atd1.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o1Names = ( ( SchemaObject ) atd1.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o1Names = ( ( SchemaObject ) atd1.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o1Names = ( ( SchemaObject ) atd1.getDestination() ).getNames();
                    break;
            }

            switch ( atd2.getType() )
            {
                case ADDED:
                    o2Names = ( ( SchemaObject ) atd2.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o2Names = ( ( SchemaObject ) atd2.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o2Names = ( ( SchemaObject ) atd2.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o2Names = ( ( SchemaObject ) atd2.getDestination() ).getNames();
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
                    o1Names = ( ( SchemaObject ) ocd1.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o1Names = ( ( SchemaObject ) ocd1.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o1Names = ( ( SchemaObject ) ocd1.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o1Names = ( ( SchemaObject ) ocd1.getDestination() ).getNames();
                    break;
            }

            switch ( ocd2.getType() )
            {
                case ADDED:
                    o2Names = ( ( SchemaObject ) ocd2.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o2Names = ( ( SchemaObject ) ocd2.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o2Names = ( ( SchemaObject ) ocd2.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o2Names = ( ( SchemaObject ) ocd2.getDestination() ).getNames();
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
                    o1Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o1Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o1Names = ( ( SchemaObject ) atd.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o1Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
            }

            switch ( ocd.getType() )
            {
                case ADDED:
                    o2Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o2Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o2Names = ( ( SchemaObject ) ocd.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o2Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
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
                    o1Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o1Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o1Names = ( ( SchemaObject ) ocd.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o1Names = ( ( SchemaObject ) ocd.getDestination() ).getNames();
                    break;
            }

            switch ( atd.getType() )
            {
                case ADDED:
                    o2Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
                case MODIFIED:
                    o2Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
                case REMOVED:
                    o2Names = ( ( SchemaObject ) atd.getSource() ).getNames();
                    break;
                case IDENTICAL:
                    o2Names = ( ( SchemaObject ) atd.getDestination() ).getNames();
                    break;
            }
        }

        // Comparing the First Name
        if ( ( o1Names != null ) && ( o2Names != null ) )
        {
            if ( ( o1Names.size() > 0 ) && ( o2Names.size() > 0 ) )
            {
                return o1Names.get( 0 ).compareToIgnoreCase( o2Names.get( 0 ) );
            }
            else if ( ( o1Names.size() == 0 ) && ( o2Names.size() > 0 ) )
            {
                return "".compareToIgnoreCase( o2Names.get( 0 ) ); //$NON-NLS-1$
            }
            else if ( ( o1Names.size() > 0 ) && ( o2Names.size() == 0 ) )
            {
                return o1Names.get( 0 ).compareToIgnoreCase( "" ); //$NON-NLS-1$
            }
        }

        // Default
        return o1.toString().compareToIgnoreCase( o2.toString() );
    }
}
