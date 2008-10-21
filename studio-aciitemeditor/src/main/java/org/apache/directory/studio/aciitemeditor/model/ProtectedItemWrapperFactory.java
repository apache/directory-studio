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
package org.apache.directory.studio.aciitemeditor.model;


import org.apache.directory.shared.ldap.aci.ProtectedItem;
import org.apache.directory.studio.aciitemeditor.valueeditors.AttributeTypeAndValueValueEditor;
import org.apache.directory.studio.aciitemeditor.valueeditors.AttributeTypeValueEditor;
import org.apache.directory.studio.aciitemeditor.valueeditors.FilterValueEditor;
import org.apache.directory.studio.aciitemeditor.valueeditors.MaxValueCountValueEditor;
import org.apache.directory.studio.aciitemeditor.valueeditors.RestrictedByValueEditor;
import org.apache.directory.studio.valueeditors.TextValueEditor;
import org.apache.directory.studio.valueeditors.integer.IntegerValueEditor;


/**
 * The ProtectedItemWrapperFactory creates the ProtectedItemWrappers, ready to
 * be used in the protected item table.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProtectedItemWrapperFactory
{

    /**
     * Creates the protected item wrappers.
     * 
     * @return the protected item wrapper[]
     */
    public static final ProtectedItemWrapper[] createProtectedItemWrappers()
    {
        ProtectedItemWrapper[] protectedItemWrappers = new ProtectedItemWrapper[]
            {
                // entry
                new ProtectedItemWrapper( ProtectedItem.Entry.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // allUserAttributeTypes
                new ProtectedItemWrapper( ProtectedItem.AllUserAttributeTypes.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // attributeType { 1.2.3, cn }
                new ProtectedItemWrapper( ProtectedItem.AttributeType.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // allAttributeValues { 1.2.3, cn }
                new ProtectedItemWrapper( ProtectedItem.AllAttributeValues.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // attributeType
                new ProtectedItemWrapper( ProtectedItem.AllUserAttributeTypesAndValues.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // attributeValue { ou=people, cn=Ersin }
                new ProtectedItemWrapper( ProtectedItem.AttributeValue.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeAndValueValueEditor() ),

                // selfValue { 1.2.3, cn }
                new ProtectedItemWrapper( ProtectedItem.SelfValue.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // rangeOfValues (cn=E*)
                new ProtectedItemWrapper( ProtectedItem.RangeOfValues.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new FilterValueEditor() ),

                // maxValueCount { { type 10.11.12, maxCount 10 }, { maxCount 20, type 11.12.13  } }
                new ProtectedItemWrapper( ProtectedItem.MaxValueCount.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new MaxValueCountValueEditor() ),

                // maxImmSub 3
                new ProtectedItemWrapper( ProtectedItem.MaxImmSub.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new IntegerValueEditor() ),

                // restrictedBy { { type 10.11.12, valuesIn ou }, { valuesIn cn, type 11.12.13  } }
                new ProtectedItemWrapper( ProtectedItem.RestrictedBy.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new RestrictedByValueEditor() ),

                // classes and : { item: xyz , or:{item:X,item:Y}   }
                new ProtectedItemWrapper( ProtectedItem.Classes.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: RefinementValueEditor 
                ),

            };

        return protectedItemWrappers;
    }

}
