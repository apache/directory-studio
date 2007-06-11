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
package org.apache.directory.studio.proxy.view.wrappers;


import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.proxy.model.LdapMessageWithPDU;
import org.apache.directory.studio.proxy.model.LdapProxy;
import org.apache.directory.studio.proxy.view.ProxyView;


/**
 * This class represents a LDAP Proxy Wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapProxyWrapper extends AbstractWrapper
{
    /** The associated view */
    private ProxyView view;


    /**
     * Creates a new instance of LdapProxyWrapper.
     *
     * @param parent
     *      the parent element
     */
    public LdapProxyWrapper( ProxyView view )
    {
        super( null );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.proxy.view.wrappers.Wrapper#createChildren(java.util.List)
     */
    protected void createChildren( List<IWrapper> children )
    {
        LdapProxy ldapProxy = view.getLdapProxy();

        if ( ldapProxy != null )
        {
            List<LdapMessageWithPDU> receivedLdapMessages = ldapProxy.getReceivedLdapMessages();
            for ( Iterator iter = receivedLdapMessages.iterator(); iter.hasNext(); )
            {
                children.add( new LdapMessageWrapper( this, ( ( LdapMessageWithPDU ) iter.next() ) ) );
            }
        }
        System.out.println( "createChildren" );
    }
    
    public void addChild( IWrapper element )
    {
        fChildren.add( element );
    }
}
