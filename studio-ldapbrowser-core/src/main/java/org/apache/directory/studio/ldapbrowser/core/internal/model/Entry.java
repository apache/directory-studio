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


import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;


/**
 * The Entry class represents an entry with a logical parent entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Entry extends AbstractEntry
{

    private static final long serialVersionUID = -4718107307581983276L;

    /** The RDN. */
    protected RDN rdn;

    /** The parent entry. */
    protected IEntry parent;


    protected Entry()
    {
    }


    /**
     * Creates a new instance of Entry.
     * 
     * @param parent the parent entry
     * @param rdn the RDN
     */
    public Entry( IEntry parent, RDN rdn )
    {
        assert parent != null;
        assert rdn != null;
        assert !"".equals( rdn.toString() );

        this.parent = parent;
        this.rdn = rdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.internal.model.AbstractEntry#getRdn()
     */
    public RDN getRdn()
    {
        // performance opt.
        return rdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getDn()
     */
    public DN getDn()
    {
        DN dn = new DN( new RDN( rdn ), parent.getDn() );
        return dn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getParententry()
     */
    public IEntry getParententry()
    {
        return parent;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getBrowserConnection()
     */
    public IBrowserConnection getBrowserConnection()
    {
        return getParententry().getBrowserConnection();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.internal.model.AbstractEntry#setRdn(org.apache.directory.studio.ldapbrowser.core.model.RDN)
     */
    protected void setRdn( RDN newRdn )
    {
        this.rdn = newRdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.internal.model.AbstractEntry#setParent(org.apache.directory.studio.ldapbrowser.core.model.IEntry)
     */
    protected void setParent( IEntry newParent )
    {
        this.parent = newParent;
    }

}
