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

import org.apache.directory.studio.schemaeditor.model.difference.AliasDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ClassTypeDifference;
import org.apache.directory.studio.schemaeditor.model.difference.CollectiveDifference;
import org.apache.directory.studio.schemaeditor.model.difference.DescriptionDifference;
import org.apache.directory.studio.schemaeditor.model.difference.EqualityDifference;
import org.apache.directory.studio.schemaeditor.model.difference.MandatoryATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.NoUserModificationDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ObsoleteDifference;
import org.apache.directory.studio.schemaeditor.model.difference.OptionalATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.OrderingDifference;
import org.apache.directory.studio.schemaeditor.model.difference.PropertyDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SingleValueDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SubstringDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SuperiorATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SuperiorOCDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SyntaxDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SyntaxLengthDifference;
import org.apache.directory.studio.schemaeditor.model.difference.UsageDifference;


/**
 * This class is used to compare, group and sort Differences by 'Type'
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TypeSorter implements Comparator<PropertyDifference>
{
    /**
     * {@inheritDoc}
     */
    public int compare( PropertyDifference diff1, PropertyDifference diff2 )
    {
        return getWeight( diff1 ) - getWeight( diff2 );
    }


    /**
     * Gets the weight of the given difference
     *
     * @param diff
     *      the difference
     * @return
     *      the weight of the difference
     */
    private int getWeight( PropertyDifference diff )
    {
        if ( diff instanceof AliasDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 1;
                case REMOVED:
                    return 25;
                default:
                    break;
            }
        }
        else if ( diff instanceof ClassTypeDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 17;
                default:
                    break;
            }
        }
        else if ( diff instanceof CollectiveDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 20;
                default:
                    break;
            }
        }
        else if ( diff instanceof DescriptionDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 2;
                case MODIFIED:
                    return 12;
                case REMOVED:
                    return 26;
                default:
                    break;
            }
        }
        else if ( diff instanceof EqualityDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 7;
                case MODIFIED:
                    return 22;
                case REMOVED:
                    return 31;
                default:
                    break;
            }
        }
        else if ( diff instanceof MandatoryATDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 10;
                case REMOVED:
                    return 34;
                default:
                    break;
            }
        }
        else if ( diff instanceof NoUserModificationDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 21;
                default:
                    break;
            }
        }
        else if ( diff instanceof ObsoleteDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 18;
                default:
                    break;
            }
        }
        else if ( diff instanceof OptionalATDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 11;
                case REMOVED:
                    return 35;
                default:
                    break;
            }
        }
        else if ( diff instanceof OrderingDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 8;
                case MODIFIED:
                    return 23;
                case REMOVED:
                    return 32;
                default:
                    break;
            }
        }
        else if ( diff instanceof SingleValueDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 19;
                default:
                    break;
            }
        }
        else if ( diff instanceof SubstringDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 9;
                case MODIFIED:
                    return 24;
                case REMOVED:
                    return 33;
                default:
                    break;
            }
        }
        else if ( diff instanceof SuperiorATDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 3;
                case MODIFIED:
                    return 13;
                case REMOVED:
                    return 27;
                default:
                    break;
            }
        }
        else if ( diff instanceof SuperiorOCDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 4;
                case REMOVED:
                    return 28;
                default:
                    break;
            }
        }
        else if ( diff instanceof SyntaxDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 5;
                case MODIFIED:
                    return 15;
                case REMOVED:
                    return 29;
                default:
                    break;
            }
        }
        else if ( diff instanceof SyntaxLengthDifference )
        {
            switch ( diff.getType() )
            {
                case ADDED:
                    return 6;
                case MODIFIED:
                    return 16;
                case REMOVED:
                    return 30;
                default:
                    break;
            }
        }
        else if ( diff instanceof UsageDifference )
        {
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return 14;
                default:
                    break;
            }
        }

        return 0;
    }
}
