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

package org.apache.directory.ldapstudio.schemas.model;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.directory.ldapstudio.schemas.Activator;
import org.eclipse.core.runtime.Platform;


/**
 * This class allows to get the list of all syntaxes
 * (which is initialized once parsing a XML file)
 * 
 */
public class Syntaxes
{
    private static final ArrayList<Syntax> syntaxes;

    static
    {
        try
        {
            syntaxes = new ArrayList<Syntax>();
            URL url = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/utils/syntaxes.xml" ); //$NON-NLS-1$
            XMLConfiguration config = new XMLConfiguration( url );

            // We get the number of syntaxes to parse
            Object syntaxesList = config.getProperty( "syntax.name" ); //$NON-NLS-1$
            if ( syntaxesList instanceof Collection )
            {
                for ( int i = 0; i < ( ( Collection ) syntaxesList ).size(); i++ )
                {
                    // We parse each syntax and get its properties
                    String name = config.getString( "syntax(" + i + ").name" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String oid = config.getString( "syntax(" + i + ").oid" ); //$NON-NLS-1$ //$NON-NLS-2$
                    String hr = config.getString( "syntax(" + i + ").hr" ); //$NON-NLS-1$ //$NON-NLS-2$

                    // We create the corresponding syntax object
                    Syntax syntax = null;
                    if ( hr.equals( "Y" ) ) { //$NON-NLS-1$
                        syntax = new Syntax( name, oid, true );
                    }
                    else if ( hr.equals( "N" ) ) { //$NON-NLS-1$
                        syntax = new Syntax( name, oid, true );
                    }

                    if ( syntax != null )
                        syntaxes.add( syntax );
                }
            }

        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    /**
     * Return the unique initialized ArrayList containing all syntaxes
     * @return the syntaxes ArrayList
     */
    public static ArrayList<Syntax> getSyntaxes()
    {
        return syntaxes;
    }


    /**
     * Return the syntax object corresponding to the name given in parameter
     * If no syntax is corresponding, it returns null
     * @param name the name of the syntax
     * @return the coreesponding Syntax object
     */
    public static Syntax getSyntax( String name )
    {
        for ( Iterator iter = syntaxes.iterator(); iter.hasNext(); )
        {
            Syntax syntax = ( Syntax ) iter.next();
            if ( syntax.getName().equals( name ) )
            {
                return syntax;
            }
        }
        return null;
    }
}
