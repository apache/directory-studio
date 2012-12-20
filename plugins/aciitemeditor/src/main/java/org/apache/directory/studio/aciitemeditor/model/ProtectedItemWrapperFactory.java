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


import org.apache.directory.api.ldap.aci.protectedItem.AllAttributeValuesItem;
import org.apache.directory.api.ldap.aci.protectedItem.AllUserAttributeTypesAndValuesItem;
import org.apache.directory.api.ldap.aci.protectedItem.AllUserAttributeTypesItem;
import org.apache.directory.api.ldap.aci.protectedItem.AttributeTypeItem;
import org.apache.directory.api.ldap.aci.protectedItem.AttributeValueItem;
import org.apache.directory.api.ldap.aci.protectedItem.ClassesItem;
import org.apache.directory.api.ldap.aci.protectedItem.EntryItem;
import org.apache.directory.api.ldap.aci.protectedItem.MaxImmSubItem;
import org.apache.directory.api.ldap.aci.protectedItem.MaxValueCountItem;
import org.apache.directory.api.ldap.aci.protectedItem.RangeOfValuesItem;
import org.apache.directory.api.ldap.aci.protectedItem.RestrictedByItem;
import org.apache.directory.api.ldap.aci.protectedItem.SelfValueItem;
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
                new ProtectedItemWrapper( EntryItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // allUserAttributeTypes
                new ProtectedItemWrapper( AllUserAttributeTypesItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // attributeType { 1.2.3, cn }
                new ProtectedItemWrapper( AttributeTypeItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // allAttributeValues { 1.2.3, cn }
                new ProtectedItemWrapper( AllAttributeValuesItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // attributeType
                new ProtectedItemWrapper( AllUserAttributeTypesAndValuesItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // attributeValue { ou=people, cn=Ersin }
                new ProtectedItemWrapper( AttributeValueItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeAndValueValueEditor() ),

                // selfValue { 1.2.3, cn }
                new ProtectedItemWrapper( SelfValueItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor() ),

                // rangeOfValues (cn=E*)
                new ProtectedItemWrapper( RangeOfValuesItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new FilterValueEditor() ),

                // maxValueCount { { type 10.11.12, maxCount 10 }, { maxCount 20, type 11.12.13  } }
                new ProtectedItemWrapper( MaxValueCountItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new MaxValueCountValueEditor() ),

                // maxImmSub 3
                new ProtectedItemWrapper( MaxImmSubItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new IntegerValueEditor() ),

                // restrictedBy { { type 10.11.12, valuesIn ou }, { valuesIn cn, type 11.12.13  } }
                new ProtectedItemWrapper( RestrictedByItem.class, true, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new RestrictedByValueEditor() ),

                // classes and : { item: xyz , or:{item:X,item:Y}   }
                new ProtectedItemWrapper( ClassesItem.class, false, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: RefinementValueEditor 
                ),

            };

        return protectedItemWrappers;
    }

}
