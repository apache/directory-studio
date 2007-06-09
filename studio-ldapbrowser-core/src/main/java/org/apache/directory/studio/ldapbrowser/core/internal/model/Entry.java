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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;


public class Entry extends AbstractEntry
{

    private static final long serialVersionUID = -4718107307581983276L;

    protected RDN rdn;

    protected IEntry parent;


    protected Entry()
    {
    }


    public Entry( IEntry parent, RDN rdn ) throws ModelModificationException
    {
        super();

        if ( parent == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_entry );
        }
        if ( rdn == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_rdn );
        }
        if ( "".equals( rdn.toString() ) ) { //$NON-NLS-1$
            throw new ModelModificationException( BrowserCoreMessages.model__empty_rdn );
        }

        this.parent = parent;
        this.rdn = rdn;
    }


    // performance opt.
    public RDN getRdn()
    {
        return this.rdn;
    }


    public DN getDn()
    {
        DN dn = new DN( new RDN( this.rdn ), this.parent.getDn() );
        return dn;
    }


    public IEntry getParententry()
    {
        return this.parent;
    }


    public IConnection getConnection()
    {
        return this.getParententry().getConnection();
    }


    protected void setRdn( RDN newRdn )
    {
        this.rdn = newRdn;
    }


    protected void setParent( IEntry newParent )
    {
        this.parent = newParent;
    }

}
