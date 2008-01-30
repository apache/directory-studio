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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.util.HashSet;
import java.util.Set;


public abstract class SchemaPart2 extends SchemaPart
{

    protected String[] names;

    protected boolean isObsolete;


    protected SchemaPart2()
    {
        super();
        this.names = new String[0];
        this.isObsolete = false;
    }


    public Set getLowerCaseIdentifierSet()
    {
        Set idSet = new HashSet();
        if ( this.numericOID != null )
        {
            idSet.add( this.numericOID.toLowerCase() );
        }
        idSet.addAll( toLowerCaseSet( this.names ) );
        return idSet;
    }


    /**
     * 
     * @return the string representation of this schema part, a
     *         comma-separated list of names.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < names.length; i++ )
        {
            sb.append( names[i] ).append( ", " );
        }
        if ( sb.length() > 2 )
        {
            sb.delete( sb.length() - 2, sb.length() );
        }
        return sb.toString();
    }


    /**
     * 
     * @return the names, may be an empty array
     */
    public String[] getNames()
    {
        return names;
    }


    public void setNames( String[] names )
    {
        this.names = names;
    }


    /**
     * 
     * @return the obsolete flag
     */
    public boolean isObsolete()
    {
        return isObsolete;
    }


    public void setObsolete( boolean isObsolete )
    {
        this.isObsolete = isObsolete;
    }


    protected Set toLowerCaseSet( String[] names )
    {
        Set set = new HashSet();
        if ( names != null )
        {
            for ( int i = 0; i < names.length; i++ )
            {
                set.add( names[i].toLowerCase() );
            }
        }
        return set;
    }

}
