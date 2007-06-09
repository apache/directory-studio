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

package org.apache.directory.studio.ldapbrowser.core.model.ldif.container;


import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifDnLine;


public class LdifChangeDeleteRecord extends LdifChangeRecord
{

    private static final long serialVersionUID = -1597258565782701577L;


    protected LdifChangeDeleteRecord()
    {
    }


    public LdifChangeDeleteRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public static LdifChangeDeleteRecord create( String dn )
    {
        LdifChangeDeleteRecord record = new LdifChangeDeleteRecord( LdifDnLine.create( dn ) );
        record.setChangeType( LdifChangeTypeLine.createDelete() );
        return record;
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        return this.getChangeTypeLine() != null && this.getSepLine() != null;
    }

}
