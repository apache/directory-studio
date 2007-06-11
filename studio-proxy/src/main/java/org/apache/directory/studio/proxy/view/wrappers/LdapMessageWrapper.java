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


import java.util.List;

import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.studio.proxy.Activator;
import org.apache.directory.studio.proxy.model.LdapMessageWithPDU;
import org.apache.directory.studio.proxy.view.IImageKeys;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents a LDAP Message Wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapMessageWrapper extends AbstractWrapper
{
    private LdapMessageWithPDU ldapMessage;


    /**
     * Creates a new instance of LdapMessageWrapper.
     *
     * @param parent
     *      the parent element
     */
    public LdapMessageWrapper( IWrapper parent, LdapMessageWithPDU ldapMessage )
    {
        super( parent );
        this.ldapMessage = ldapMessage;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.proxy.view.wrappers.Wrapper#createChildren(java.util.List)
     */
    protected void createChildren( List<IWrapper> children )
    {
        // TODO Auto-generated method stub
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.proxy.view.wrappers.Wrapper#getImage()
     */
    public Image getImage()
    {
        // TODO Auto-generated method stub
        return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.OUT ).createImage();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.proxy.view.wrappers.Wrapper#getText()
     */
    public String getText()
    {
        return ldapMessage.getLdapMessage().getMessageTypeName() + " - " + ldapMessage.getMessageId();
    }
}
