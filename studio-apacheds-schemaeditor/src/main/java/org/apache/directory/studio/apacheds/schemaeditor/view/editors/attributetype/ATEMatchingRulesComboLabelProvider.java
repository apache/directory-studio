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


import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.NonExistingMatchingRule;
import org.eclipse.jface.viewers.LabelProvider;


/**
 * This class implements the Label Provider of the Matching Rules Combo of the Attribute Type Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ATEMatchingRulesComboLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        if ( obj instanceof MatchingRuleImpl )
        {
            MatchingRuleImpl mr = ( MatchingRuleImpl ) obj;

            String name = mr.getName();
            if ( name != null )
            {
                return name + "  -  (" + mr.getOid() + ")";
            }
            else
            {
                return "(None)  -  (" + mr.getOid() + ")";
            }
        }
        else if ( obj instanceof NonExistingMatchingRule )
        {
            return ( ( NonExistingMatchingRule ) obj ).getDisplayName();
        }

        // Default
        return null;
    }
}
