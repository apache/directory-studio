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
package org.apache.directory.studio.schemas.view.editors.attributeType;


import java.util.Comparator;

import org.apache.directory.studio.schemas.model.MatchingRule;


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
        if ( o1 instanceof MatchingRule && o2 instanceof MatchingRule )
        {
            return ( ( MatchingRule ) o1 ).getName().compareToIgnoreCase( ( ( MatchingRule ) o2 ).getName() );
        }
        else if ( o1 instanceof MatchingRule && o2 instanceof NonExistingMatchingRule )
        {
            return ( ( MatchingRule ) o1 ).getName().compareToIgnoreCase( ( ( NonExistingMatchingRule ) o2 ).getName() );
        }
        else if ( o1 instanceof NonExistingMatchingRule && o2 instanceof MatchingRule )
        {
            return ( ( NonExistingMatchingRule ) o1 ).getName().compareToIgnoreCase( ( ( MatchingRule ) o2 ).getName() );
        }
        else if ( o1 instanceof NonExistingMatchingRule && o2 instanceof NonExistingMatchingRule )
        {
            return ( ( NonExistingMatchingRule ) o1 ).getName().compareToIgnoreCase(
                ( ( NonExistingMatchingRule ) o2 ).getName() );
        }

        return 0;
    }
}
