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
package org.apache.directory.ldapstudio.schemas.view;


/**
 * This Helper Class contains useful methods used to create the UI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ViewUtils
{
    /**
     * Concatenates all aliases in a String format. Aliases are separated with a comma (',')
     *
     * @param aliases
     *      the aliases to concatenate
     * @return
     *      a String representing all aliases
     */
    public static String concateAliases( String[] aliases )
    {
        StringBuffer sb = new StringBuffer();

        sb.append( aliases[0] );

        for ( int i = 1; i < aliases.length; i++ )
        {
            sb.append( ", " );
            sb.append( aliases[i] );
        }

        return sb.toString();
    }
}
