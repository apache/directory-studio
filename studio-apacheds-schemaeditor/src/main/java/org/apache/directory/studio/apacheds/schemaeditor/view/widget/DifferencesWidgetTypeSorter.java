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
 * This class is used to compare, group and sort Differences by 'Type'
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetTypeSorter implements Comparator<Difference>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Difference diff1, Difference diff2 )
    {
        return DifferencesWidgetTypeSorterEnum.valueOf( diff1.getClass().getSimpleName() ).getWeight()
            - DifferencesWidgetTypeSorterEnum.valueOf( diff2.getClass().getSimpleName() ).getWeight();
    }

    /**
     * This enum is used to get the weight of each Difference.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private enum DifferencesWidgetTypeSorterEnum
    {
        AddAliasDifference(1), AddDescriptionDifference(2), AddSuperiorATDifference(3), AddSuperiorOCDifference(4), AddSyntaxDifference(
            5), AddSyntaxLengthDifference(6), AddEqualityDifference(7), AddOrderingDifference(8), AddSubstringDifference(
            9), AddMandatoryATDifference(10), AddOptionalATDifference(11), ModifyDescriptionDifference(12), ModifySuperiorATDifference(
            13), ModifyUsageDifference(14), ModifySyntaxDifference(15), ModifySyntaxLengthDifference(16), ModifyClassTypeDifference(
            17), ModifyObsoleteDifference(18), ModifySingleValueDifference(19), ModifyCollectiveDifference(20), ModifyNoUserModificationDifference(
            21), ModifyEqualityDifference(22), ModifyOrderingDifference(23), ModifySubstringDifference(24), RemoveAliasDifference(
            25), RemoveDescriptionDifference(26), RemoveSuperiorATDifference(27), RemoveSuperiorOCDifference(28), RemoveSyntaxDifference(
            29), RemoveSyntaxLengthDifference(30), RemoveEqualityDifference(31), RemoveSubstringDifference(33), RemoveOrderingDifference(
            32), RemoveMandatoryATDifference(34), RemoveOptionalATDifference(35);

        /** The weight */
        private int weight;


        /**
         * Creates a new instance of DifferencesWidgetTypeSorterEnum.
         *
         * @param weight
         *      the weight
         */
        private DifferencesWidgetTypeSorterEnum( int weight )
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
