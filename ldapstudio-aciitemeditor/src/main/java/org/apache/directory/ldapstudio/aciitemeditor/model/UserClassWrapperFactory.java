
package org.apache.directory.ldapstudio.aciitemeditor.model;

import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.DnValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.TextValueEditor;
import org.apache.directory.shared.ldap.aci.UserClass;

public class UserClassWrapperFactory
{
    
    
    public static final UserClassWrapper[] createUserClassWrappers()
    {
        UserClassWrapper[] userClassWrappers = new UserClassWrapper[]
            { 
                // allUsers
                new UserClassWrapper
                ( 
                    UserClass.AllUsers.class, 
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null 
                ),
                
                // thisEntry
                new UserClassWrapper
                ( 
                    UserClass.ThisEntry.class, 
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null 
                ),
                
                // name
                new UserClassWrapper
                ( 
                    UserClass.Name.class, 
                    "\"",  //$NON-NLS-1$
                    "\"", //$NON-NLS-1$
                    new DnValueEditor() 
                ),
                
                // userGroup
                new UserClassWrapper
                ( 
                    UserClass.UserGroup.class, 
                    "\"",  //$NON-NLS-1$
                    "\"", //$NON-NLS-1$
                    new DnValueEditor() 
                ),
                
                // subtree
                new UserClassWrapper
                ( 
                    UserClass.Subtree.class, 
                    "",  //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new TextValueEditor() // TODO: SubtreeSpecificationValueEditor
                )
            };
        
        return userClassWrappers;
    }
    
    
}
