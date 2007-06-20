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
package org.apache.directory.studio.apacheds.schemaeditor.view.widget;


import java.util.Comparator;

import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractAddDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractModifyDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractRemoveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyClassTypeDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyCollectiveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyNoUserModificationDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyObsoleteDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySingleValueDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyUsageDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxLengthDifference;


/**
 * This class is used to compare, group and sort Differences by 'Property'
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetPropertySorter implements Comparator<Difference>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Difference diff1, Difference diff2 )
    {
        return getWeight( diff1 ) - getWeight( diff2 );
    }


    /**
     * Gets the weight of the Difference.
     *
     * @param o
     *      the difference
     * @return
     *      the wright of the Difference
     */
    private int getWeight( Difference o )
    {
        if ( o instanceof AbstractAddDifference )
        {
            if ( o instanceof AddAliasDifference )
            {
                return 1;
            }
            else if ( o instanceof AddDescriptionDifference )
            {
                return 3;
            }
            else if ( o instanceof AddEqualityDifference )
            {
                return 23;
            }
            else if ( o instanceof AddMandatoryATDifference )
            {
                return 32;
            }
            else if ( o instanceof AddOptionalATDifference )
            {
                return 34;
            }
            else if ( o instanceof AddOrderingDifference )
            {
                return 26;
            }
            else if ( o instanceof AddSubstringDifference )
            {
                return 29;
            }
            else if ( o instanceof AddSuperiorATDifference )
            {
                return 6;
            }
            else if ( o instanceof AddSuperiorOCDifference )
            {
                return 9;
            }
            else if ( o instanceof AddSyntaxDifference )
            {
                return 12;
            }
            else if ( o instanceof AddSyntaxLengthDifference )
            {
                return 15;
            }
        }
        else if ( o instanceof AbstractModifyDifference )
        {
            if ( o instanceof ModifyClassTypeDifference )
            {
                return 18;
            }
            else if ( o instanceof ModifyCollectiveDifference )
            {
                return 21;
            }
            else if ( o instanceof ModifyDescriptionDifference )
            {
                return 4;
            }
            else if ( o instanceof ModifyEqualityDifference )
            {
                return 24;
            }
            else if ( o instanceof ModifyNoUserModificationDifference )
            {
                return 22;
            }
            else if ( o instanceof ModifyObsoleteDifference )
            {
                return 19;
            }
            else if ( o instanceof ModifyOrderingDifference )
            {
                return 27;
            }
            else if ( o instanceof ModifySingleValueDifference )
            {
                return 20;
            }
            else if ( o instanceof ModifySubstringDifference )
            {
                return 30;
            }
            else if ( o instanceof ModifySuperiorATDifference )
            {
                return 7;
            }
            else if ( o instanceof ModifySyntaxDifference )
            {
                return 13;
            }
            else if ( o instanceof ModifySyntaxLengthDifference )
            {
                return 16;
            }
            else if ( o instanceof ModifyUsageDifference )
            {
                return 11;
            }
        }
        else if ( o instanceof AbstractRemoveDifference )
        {
            if ( o instanceof RemoveAliasDifference )
            {
                return 2;
            }
            else if ( o instanceof RemoveDescriptionDifference )
            {
                return 5;
            }
            else if ( o instanceof RemoveEqualityDifference )
            {
                return 25;
            }
            else if ( o instanceof RemoveMandatoryATDifference )
            {
                return 33;
            }
            else if ( o instanceof RemoveOptionalATDifference )
            {
                return 35;
            }
            else if ( o instanceof RemoveOrderingDifference )
            {
                return 28;
            }
            else if ( o instanceof RemoveSubstringDifference )
            {
                return 31;
            }
            else if ( o instanceof RemoveSuperiorATDifference )
            {
                return 8;
            }
            else if ( o instanceof RemoveSuperiorOCDifference )
            {
                return 10;
            }
            else if ( o instanceof RemoveSyntaxDifference )
            {
                return 14;
            }
            else if ( o instanceof RemoveSyntaxLengthDifference )
            {
                return 17;
            }
        }

        // Default
        return 0;
    }
}
