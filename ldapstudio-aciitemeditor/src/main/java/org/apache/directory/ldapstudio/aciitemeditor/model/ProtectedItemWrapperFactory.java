
package org.apache.directory.ldapstudio.aciitemeditor.model;

import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.TextValueEditor;
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
                    new TextValueEditor() // TODO: AttributeTypeValueEditor
                ),
                
                // allAttributeValues { 1.2.3, cn }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.AllAttributeValues.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: AttributeTypeValueEditor
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
                    new TextValueEditor() // TODO: AttributeTypeAndValueValueEditor
                ),
                
                // selfValue { 1.2.3, cn }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.SelfValue.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: AttributeTypeValueEditor
                ),
                
                // rangeOfValues (cn=E*)
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.RangeOfValues.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() 
                ),
                
                // maxValueCount { { type 10.11.12, maxCount 10 }, { maxCount 20, type 11.12.13  } }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.MaxValueCount.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: MaxValueCountValueEditor
                ),
                
                // maxImmSub 3
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.MaxImmSub.class, 
                    false,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: IntegerValueEditor
                ),
                
                // restrictedBy { { type 10.11.12, valuesIn ou }, { valuesIn cn, type 11.12.13  } }
                new ProtectedItemWrapper
                ( 
                    ProtectedItem.RestrictedBy.class, 
                    true,
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: RestrictedByValueEditor
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
