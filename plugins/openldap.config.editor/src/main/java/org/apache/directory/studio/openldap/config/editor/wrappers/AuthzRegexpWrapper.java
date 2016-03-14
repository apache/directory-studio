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
package org.apache.directory.studio.openldap.config.editor.wrappers;

/**
 * A wrapper class for the values stored in the olcAuthzRegexp attribute. It contains
 * two argument :
 * <pre>
 * &lt;match&gt; &lt;replace&gt; 
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AuthzRegexpWrapper implements Cloneable, Comparable<AuthzRegexpWrapper>
{
    /** The match part */
    private String match;
    
    /** The replace part */
    private String replace;
    
    /**
     * Creates a new instance of AuthzRegexpWrapper using a String value
     * 
     * @param regexp The value
     */
    public AuthzRegexpWrapper( String authzRegexp )
    {
        this.match = authzRegexp;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( AuthzRegexpWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }
        
        // 
        return 0;
    }

    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return match + ' ' + replace;
    }
}
