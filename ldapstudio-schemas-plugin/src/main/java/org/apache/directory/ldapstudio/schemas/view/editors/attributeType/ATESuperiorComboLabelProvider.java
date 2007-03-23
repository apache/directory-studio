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
package org.apache.directory.ldapstudio.schemas.view.editors.attributeType;


import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.eclipse.jface.viewers.LabelProvider;


/**
 * This class implements the Label Provider of the Superior Combo of the Attribute Type Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ATESuperiorComboLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        if ( obj instanceof AttributeType )
        {
            return ViewUtils.concateAliases( ( ( AttributeType ) obj ).getNames() );
        }
        else if ( obj instanceof NonExistingAttributeType )
        {
            return ( ( NonExistingAttributeType ) obj ).getDisplayName();
        }

        // Default
        return null;
    }
}
