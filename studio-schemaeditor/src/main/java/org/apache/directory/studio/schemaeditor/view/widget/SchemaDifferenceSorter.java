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

import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.difference.SchemaDifference;


/**
 * This class is used to compare and sort ascending two Schemas
 */
public class SchemaDifferenceSorter implements Comparator<Object>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 )
    {
        if ( ( o1 instanceof SchemaDifference ) && ( o2 instanceof SchemaDifference ) )
        {
            SchemaDifference sd1 = ( SchemaDifference ) o1;
            SchemaDifference sd2 = ( SchemaDifference ) o2;

            String name1 = "";
            String name2 = "";
            switch ( sd1.getType() )
            {
                case ADDED:
                    name1 = ( ( Schema ) sd1.getDestination() ).getName();
                    break;
                case MODIFIED:
                    name1 = ( ( Schema ) sd1.getDestination() ).getName();
                    break;
                case REMOVED:
                    name1 = ( ( Schema ) sd1.getSource() ).getName();
                    break;
                case IDENTICAL:
                    name1 = ( ( Schema ) sd1.getDestination() ).getName();
                    break;
            }

            switch ( sd2.getType() )
            {
                case ADDED:
                    name2 = ( ( Schema ) sd2.getDestination() ).getName();
                    break;
                case MODIFIED:
                    name2 = ( ( Schema ) sd2.getDestination() ).getName();
                    break;
                case REMOVED:
                    name2 = ( ( Schema ) sd2.getSource() ).getName();
                    break;
                case IDENTICAL:
                    name2 = ( ( Schema ) sd2.getDestination() ).getName();
                    break;
            }

            return name1.compareToIgnoreCase( name2 );
        }

        // Default
        return o1.toString().compareToIgnoreCase( o2.toString() );
    }
}
