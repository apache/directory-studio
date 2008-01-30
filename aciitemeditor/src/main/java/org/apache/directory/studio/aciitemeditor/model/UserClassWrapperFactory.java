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


import org.apache.directory.shared.ldap.aci.UserClass;
import org.apache.directory.studio.aciitemeditor.valueeditors.SubtreeValueEditor;
import org.apache.directory.studio.valueeditors.dn.DnValueEditor;


/**
 * The UserClassWrapperFactory creates the UserClassWrapper, ready to
 * be used in the user classes table.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UserClassWrapperFactory
{

    /**
     * Creates the user class wrappers.
     *
     * @return the user class wrapper[]
     */
    public static final UserClassWrapper[] createUserClassWrappers()
    {
        UserClassWrapper[] userClassWrappers = new UserClassWrapper[]
            {
                // allUsers
                new UserClassWrapper( UserClass.AllUsers.class, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // thisEntry
                new UserClassWrapper( UserClass.ThisEntry.class, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    null ),

                // name
                new UserClassWrapper( UserClass.Name.class, "\"", //$NON-NLS-1$
                    "\"", //$NON-NLS-1$
                    new DnValueEditor() ),

                // userGroup
                new UserClassWrapper( UserClass.UserGroup.class, "\"", //$NON-NLS-1$
                    "\"", //$NON-NLS-1$
                    new DnValueEditor() ),

                // subtree
                new UserClassWrapper( UserClass.Subtree.class, "", //$NON-NLS-1$
                    "", //$NON-NLS-1$
                    new SubtreeValueEditor( false )
                ) };

        return userClassWrappers;
    }

}
