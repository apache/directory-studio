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


import java.util.Collections;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingMatchingRule;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Equality Combo of the Attribute Type Editor.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATEMatchingRulesComboContentProvider implements IStructuredContentProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof ATEMatchingRulesComboInput )
        {
            ATEMatchingRulesComboInput input = ( ATEMatchingRulesComboInput ) inputElement;

            if ( input.getChildren().isEmpty() )
            {
                // Creating the '(None)' item
                input.addChild( new NonExistingMatchingRule( NonExistingMatchingRule.NONE ) );

                // Creating Children
                List<MatchingRule> equalityMatchingRules = Activator.getDefault().getSchemaHandler()
                    .getMatchingRules();
                for ( MatchingRule matchingRule : equalityMatchingRules )
                {
                    input.addChild( matchingRule );
                }
            }

            // Getting Children
            List<Object> children = input.getChildren();

            // Sorting Children
            Collections.sort( children, new ATEMatchingRulesComboComparator() );

            return children.toArray();
        }

        // Default
        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }
}
