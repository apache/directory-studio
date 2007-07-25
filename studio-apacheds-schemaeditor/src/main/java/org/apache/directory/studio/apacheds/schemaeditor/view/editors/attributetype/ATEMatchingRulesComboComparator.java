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
package org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype;


import java.util.Comparator;

import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.NonExistingMatchingRule;


/**
 * This class implements the Comparator used to compare elements in the Matching Rules Content Providers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ATEMatchingRulesComboComparator implements Comparator<Object>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 )
    {
        if ( o1 instanceof MatchingRuleImpl && o2 instanceof MatchingRuleImpl )
        {
            String[] mr1Names = ( ( MatchingRuleImpl ) o1 ).getNames();
            String[] mr2Names = ( ( MatchingRuleImpl ) o2 ).getNames();

            if ( ( mr1Names != null ) && ( mr2Names != null ) && ( mr1Names.length > 0 ) && ( mr2Names.length > 0 ) )
            {
                return mr1Names[0].compareToIgnoreCase( mr1Names[0] );
            }
        }
        else if ( o1 instanceof MatchingRuleImpl && o2 instanceof NonExistingMatchingRule )
        {
            String[] mr1Names = ( ( MatchingRuleImpl ) o1 ).getNames();
            String mr2Name = ( ( NonExistingMatchingRule ) o2 ).getName();

            if ( ( mr1Names != null ) && ( mr2Name != null ) && ( mr1Names.length > 0 ) )
            {
                return mr1Names[0].compareToIgnoreCase( mr2Name );
            }
        }
        else if ( o1 instanceof NonExistingMatchingRule && o2 instanceof MatchingRuleImpl )
        {
            String mr1Name = ( ( NonExistingMatchingRule ) o1 ).getName();
            String[] mr2Names = ( ( MatchingRuleImpl ) o2 ).getNames();

            if ( ( mr1Name != null ) && ( mr2Names != null ) && ( mr2Names.length > 0 ) )
            {
                return mr1Name.compareToIgnoreCase( mr2Names[0] );
            }
        }
        else if ( o1 instanceof NonExistingMatchingRule && o2 instanceof NonExistingMatchingRule )
        {
            String mr1Name = ( ( NonExistingMatchingRule ) o1 ).getName();
            String mr2Name = ( ( NonExistingMatchingRule ) o2 ).getName();

            if ( ( mr1Name != null ) && ( mr2Name != null ) )
            {
                return mr1Name.compareToIgnoreCase( mr2Name );
            }
        }

        return 0;
    }
}
