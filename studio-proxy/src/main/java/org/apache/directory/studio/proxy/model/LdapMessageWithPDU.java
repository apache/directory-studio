/*
 *   Copyright 2005 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.apache.directory.studio.proxy.model;

import org.apache.directory.shared.ldap.codec.LdapMessage;


/**
 * A LdapMessage container
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapMessageWithPDU
{
    /** The PDU */
    private String      dumpBytes;

    /** The ldapMessage */
    private LdapMessage ldapMessage;

    /** The ldap MessaheId */
    private int         messageId;

    public String getDumpBytes()
    {
        return dumpBytes;
    }

    public void setDumpBytes( String dumpBytes )
    {
        this.dumpBytes = dumpBytes;
    }

    public LdapMessage getLdapMessage()
    {
        return ldapMessage;
    }

    public void setLdapMessage( LdapMessage ldapMessage )
    {
        this.ldapMessage = ldapMessage;
    }

    public int getMessageId()
    {
        return messageId;
    }

    public void setMessageId( int messageId )
    {
        this.messageId = messageId;
    }
}
