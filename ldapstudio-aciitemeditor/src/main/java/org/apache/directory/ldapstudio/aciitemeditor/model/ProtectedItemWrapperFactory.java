
package org.apache.directory.ldapstudio.aciitemeditor.model;

import org.apache.directory.ldapstudio.aciitemeditor.AttributeTypeAndValueValueEditor;
import org.apache.directory.ldapstudio.aciitemeditor.AttributeTypeValueEditor;
import org.apache.directory.ldapstudio.aciitemeditor.FilterValueEditor;
import org.apache.directory.ldapstudio.aciitemeditor.MaxValueCountValueEditor;
import org.apache.directory.ldapstudio.aciitemeditor.RestrictedByValueEditor;
import org.apache.directory.ldapstudio.valueeditors.TextValueEditor;
import org.apache.directory.ldapstudio.valueeditors.integer.IntegerValueEditor;
import org.apache.directory.shared.ldap.aci.ProtectedItem;

public class ProtectedItemWrapperFactory
{
    
    public static final ProtectedItemWrapper[] createProtectedItemWrappers()
    {
        ProtectedItemWrapper[] protectedItemWrappers = new ProtectedItemWrapper[]
            { 
                // entry
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.Entry.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null 
                ),
                
                // allUserAttributeTypes
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AllUserAttributeTypes.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null 
                ),
                
                // attributeType { 1.2.3, cn }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AttributeType.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor()
                ),
                
                // allAttributeValues { 1.2.3, cn }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AllAttributeValues.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor()
                ),
                
                // attributeType
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AllUserAttributeTypesAndValues.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null 
                ),
                
                // attributeValue { ou=people, cn=Ersin }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AttributeValue.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeAndValueValueEditor()
                ),
                
                // selfValue { 1.2.3, cn }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.SelfValue.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new AttributeTypeValueEditor()
                ),
                
                // rangeOfValues (cn=E*)
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.RangeOfValues.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new FilterValueEditor() 
                ),
                
                // maxValueCount { { type 10.11.12, maxCount 10 }, { maxCount 20, type 11.12.13  } }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.MaxValueCount.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new MaxValueCountValueEditor()
                ),
                
                // maxImmSub 3
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.MaxImmSub.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new IntegerValueEditor()
                ),
                
                // restrictedBy { { type 10.11.12, valuesIn ou }, { valuesIn cn, type 11.12.13  } }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.RestrictedBy.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new RestrictedByValueEditor()
                ),
                
                // classes and : { item: xyz , or:{item:X,item:Y}   }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.Classes.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: RefinementValueEditor 
                ),
                
            };
        
        return protectedItemWrappers;
    }
    
}
