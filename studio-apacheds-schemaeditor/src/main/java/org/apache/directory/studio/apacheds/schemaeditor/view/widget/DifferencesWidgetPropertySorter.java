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

import org.apache.directory.studio.apacheds.schemaeditor.model.difference.Difference;


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
        return DifferencesWidgetPropertySorterEnum.valueOf( diff1.getClass().getSimpleName() ).getWeight()
            - DifferencesWidgetPropertySorterEnum.valueOf( diff2.getClass().getSimpleName() ).getWeight();
    }

    
    /**
     * This enum is used to get the weight of each Difference.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private enum DifferencesWidgetPropertySorterEnum
    {
        AddAliasDifference(1), RemoveAliasDifference(2), AddDescriptionDifference(3), ModifyDescriptionDifference(4), RemoveDescriptionDifference(
            5), AddSuperiorATDifference(6), ModifySuperiorATDifference(7), RemoveSuperiorATDifference(8), AddSuperiorOCDifference(
            9), RemoveSuperiorOCDifference(10), ModifyUsageDifference(11), AddSyntaxDifference(12), ModifySyntaxDifference(
            13), RemoveSyntaxDifference(14), AddSyntaxLengthDifference(15), ModifySyntaxLengthDifference(16), RemoveSyntaxLengthDifference(
            17), ModifyClassTypeDifference(18), ModifyObsoleteDifference(19), ModifySingleValueDifference(20), ModifyCollectiveDifference(
            21), ModifyNoUserModificationDifference(22), AddEqualityDifference(23), ModifyEqualityDifference(24), RemoveEqualityDifference(
            25), AddOrderingDifference(26), ModifyOrderingDifference(27), RemoveOrderingDifference(28), AddSubstringDifference(
            29), ModifySubstringDifference(30), RemoveSubstringDifference(31), AddMandatoryATDifference(32), RemoveMandatoryATDifference(
            33), AddOptionalATDifference(34), RemoveOptionalATDifference(35);

        /** The weight */
        private int weight;


        /**
         * Creates a new instance of DifferencesWidgetTypeSorterEnum.
         *
         * @param weight
         *      the weight
         */
        private DifferencesWidgetPropertySorterEnum( int weight )
        {
            this.weight = weight;
        }


        /**
         * Gets the weight.
         *
         * @return
         *      the weight
         */
        public int getWeight()
        {
            return weight;
        }
    }
}
