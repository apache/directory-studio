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

package org.apache.directory.studio.connection.core;


/**
 * Some utils.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Utils
{

    /**
     * Shortens the given label to the given maximum length.
     * 
     * @param label the label
     * @param maxLength the max length
     * 
     * @return the shortened label
     */
    public static String shorten( String label, int maxLength )
    {
        if ( label == null )
        {
            return null;
        }
        if ( maxLength < 3 )
        {
            return "...";
        }
        if ( label.length() > maxLength )
        {
            label = label.substring( 0, maxLength / 2 ) + "..."
                + label.substring( label.length() - maxLength / 2, label.length() );

        }
        StringBuffer sb = new StringBuffer( maxLength + 3 );
        for ( int i = 0; i < label.length(); i++ )
        {
            char c = label.charAt( i );
            if ( c > 31 && c < 127 )
                sb.append( c );
            else
                sb.append( '.' );
        }
        return sb.toString();
    }

}
