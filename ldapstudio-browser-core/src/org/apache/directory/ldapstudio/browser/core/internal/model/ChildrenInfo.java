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


import java.io.Serializable;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class ChildrenInfo implements Serializable
{

    private static final long serialVersionUID = -4642987611142312896L;

    public static int COUNTER = 0;

    protected volatile boolean childrenInitialzed = false;

    protected volatile Set childrenSet = null;

    protected volatile boolean hasMoreChildren = false;


    public ChildrenInfo()
    {
        COUNTER++;
    }

    class AliasOrReferral implements Serializable
    {

        private static final long serialVersionUID = -8339682035388780022L;

        protected IConnection connection;

        protected DN dn;


        protected AliasOrReferral()
        {
        }


        public AliasOrReferral( IConnection connection, DN dn )
        {
            this.connection = connection;
            this.dn = dn;
        }


        public boolean equals( Object o ) throws ClassCastException
        {
            if ( o instanceof AliasOrReferral )
            {
                return this.toString().equals( ( ( AliasOrReferral ) o ).toString() );
            }
            return false;
        }


        public int hashCode()
        {
            return this.toString().hashCode();
        }


        public String toString()
        {
            return connection.hashCode() + "_" + dn.toString(); //$NON-NLS-1$
        }

    }

}
