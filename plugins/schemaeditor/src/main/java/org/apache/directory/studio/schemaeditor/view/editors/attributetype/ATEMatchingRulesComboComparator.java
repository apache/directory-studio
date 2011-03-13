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
package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.MutableMatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingMatchingRule;


/**
 * This class implements the Comparator used to compare elements in the Matching Rules Content Providers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATEMatchingRulesComboComparator implements Comparator<Object>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 )
    {
        if ( o1 instanceof MatchingRule && o2 instanceof MatchingRule )
        {
            List<String> mr1Names = ( ( MutableMatchingRuleImpl ) o1 ).getNames();
            List<String> mr2Names = ( ( MutableMatchingRuleImpl ) o2 ).getNames();

            if ( ( mr1Names != null ) && ( mr2Names != null ) && ( mr1Names.size() > 0 ) && ( mr2Names.size() > 0 ) )
            {
                return mr1Names.get( 0 ).compareToIgnoreCase( mr2Names.get( 0 ) );
            }
        }
        else if ( o1 instanceof MatchingRule && o2 instanceof NonExistingMatchingRule )
        {
            List<String> mr1Names = ( ( MutableMatchingRuleImpl ) o1 ).getNames();
            String mr2Name = ( ( NonExistingMatchingRule ) o2 ).getName();

            if ( ( mr1Names != null ) && ( mr2Name != null ) && ( mr1Names.size() > 0 ) )
            {
                return mr1Names.get( 0 ).compareToIgnoreCase( mr2Name );
            }
        }
        else if ( o1 instanceof NonExistingMatchingRule && o2 instanceof MatchingRule )
        {
            String mr1Name = ( ( NonExistingMatchingRule ) o1 ).getName();
            List<String> mr2Names = ( ( MutableMatchingRuleImpl ) o2 ).getNames();

            if ( ( mr1Name != null ) && ( mr2Names != null ) && ( mr2Names.size() > 0 ) )
            {
                return mr1Name.compareToIgnoreCase( mr2Names.get( 0 ) );
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
