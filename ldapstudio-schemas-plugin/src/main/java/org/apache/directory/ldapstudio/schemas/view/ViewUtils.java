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


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;


/**
 * This Helper Class contains useful methods used to create the UI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ViewUtils
{
    /** The Black Color */
    public static final Color COLOR_BLACK = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
        .getDisplay().getSystemColor( SWT.COLOR_BLACK );

    /** The Red Color */
    public static final Color COLOR_RED = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
        .getSystemColor( SWT.COLOR_RED );


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
        if ( aliases.length > 0 )
        {
            sb.append( aliases[0] );
            for ( int i = 1; i < aliases.length; i++ )
            {
                sb.append( ", " );
                sb.append( aliases[i] );
            }
        }

        return sb.toString();
    }
    
    
    /**
     * Verifies that the given name is syntaxely correct according to the RFC 2252 
     * (Lightweight Directory Access Protocol (v3): Attribute Syntax Definitions).
     *
     * @param name
     *      the name to test
     * @return
     *      true if the name is correct, false if the name is not correct.
     */
    public static boolean verifyName( String name )
    {
        return name.matches( "[a-zA-Z]+[a-zA-Z0-9;-]*" );
    }
}
