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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.model.URL;


public class JNDIUtils
{

    public static DN getDn( SearchResult sr, String base, JNDIConnectionContext context ) throws NamingException,
        NameException, NoSuchFieldException
    {
        DN dn = null;
        if ( sr.isRelative() )
        {
            Name name = ( Name ) context.getNameParser().parse( base ).clone();
            Name rdnName = context.getNameParser().parse( unescapeJndiName( sr.getName() ) );
            name.addAll( rdnName );
            dn = new DN( name.toString() );
        }
        else
        {
            URL url = new URL( sr.getName() );
            dn = url.getDn();
            // dn = new DN(sr.getName());
        }
        return dn;
    }


    /**
     * Correct some JNDI encodings...
     * 
     * @param name
     * @return
     */
    public static String unescapeJndiName( String name )
    {

        if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
            name = name.substring( 1, name.length() - 1 );
        }

        name = name.replaceAll( "\\\\\\\\\"", "\\\\\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\2C", "\\\\," ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3B", "\\\\;" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\22", "\\\\\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3C", "\\\\<" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3E", "\\\\>" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\2B", "\\\\+" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\5C", "\\\\\\\\" ); //$NON-NLS-1$ //$NON-NLS-2$

        return name;
    }

}
