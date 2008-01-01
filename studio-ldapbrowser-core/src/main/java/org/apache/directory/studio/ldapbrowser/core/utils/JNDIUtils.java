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

package org.apache.directory.studio.ldapbrowser.core.utils;


import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.naming.NamingException;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;


/**
 * Utility class for JNDI specific stuff.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class JNDIUtils
{

    /**
     * Gets the LdapDN from a JNDI SearchResult object.
     *
     * @param sr the JNDI search result
     * @return the LdapDN 
     * @throws NamingException
     */
    public static LdapDN getDn( javax.naming.directory.SearchResult sr ) throws NamingException
    {
        String dn = sr.getNameInNamespace();
        LdapDN ldapDn = new LdapDN( unescapeJndiName( dn ) );
        return ldapDn;
    }


    /**
     * Correct some JNDI encodings...
     * 
     * @param name the DN
     * @return the modified DN
     */
    public static String unescapeJndiName( String name )
    {
        if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
            name = name.substring( 1, name.length() - 1 );
        }

        name = name.replaceAll( "\\\\\\\\\"", "\\\\\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\2C", "\\\\," ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3B", "\\\\;" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\22", "\\\\\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3C", "\\\\<" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\3E", "\\\\>" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\2B", "\\\\+" ); //$NON-NLS-1$ //$NON-NLS-2$
        name = name.replaceAll( "\\\\5C", "\\\\\\\\" ); //$NON-NLS-1$ //$NON-NLS-2$

        return name;
    }


    /**
     * Creates a connection exception.
     * 
     * @param searchParameter the search parameter
     * @param e the exception
     * 
     * @return the connection exception
     */
    public static ConnectionException createConnectionException( SearchParameter searchParameter, Throwable e )
    {
    	// TODO: remove when improving error handling
        e.printStackTrace();
        
        ConnectionException connectionException = null;
        ConnectionException lastException = null;

        do
        {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            int ldapStatusCode = -1;

            // get LDAP status code
            // [LDAP: error code 21 - telephoneNumber: value #0 invalid per syntax]
            if ( message != null && message.startsWith( "[LDAP: error code " ) ) { //$NON-NLS-1$
                int begin = "[LDAP: error code ".length(); //$NON-NLS-1$
                int end = begin + 2;
                try
                {
                    ldapStatusCode = Integer.parseInt( message.substring( begin, end ).trim() );
                }
                catch ( NumberFormatException nfe )
                {
                }
            }

            // special causes
            // java_io_IOException=I/O exception occurred: {0}
            // java_io_EOFException=End of file encountered: {0}
            // java_io_FileNotFoundException=File not found: {0}
            // java_io_InterruptedIOException=I/O has been interrupted.
            // java_net_UnknownHostException=Cannot locate host: {0}
            // java_net_ConnectException=Cannot connect to host: {0}
            // java_net_SocketException=Socket Exception: {0}
            // java_net_NoRouteToHostException={0}
            if ( e instanceof ConnectException )
            {
                message = e.getMessage() + " (" + e.getMessage() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( e instanceof NoRouteToHostException )
            {
                message += e.getMessage() + " (" + e.getMessage() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if ( e instanceof UnknownHostException )
            {
                message = BrowserCoreMessages.model__unknown_host + e.getMessage();
            }
            if ( e instanceof SocketException )
            {
                message = e.getMessage() + " (" + e.getMessage() + ")";; //$NON-NLS-1$ //$NON-NLS-2$
            }

            ConnectionException ce = new ConnectionException( ldapStatusCode, message, e );
            if ( lastException != null )
            {
                lastException.initCause( ce );
            }
            lastException = ce;
            if ( connectionException == null )
            {
                connectionException = lastException;
            }

            // next cause
            e = e.getCause();
        }
        while ( e != null );

        return connectionException;

    }

    public static Control[] getManageDsaItControl()
    {
        Control[] controls = new Control[]
            { new BasicControl(
                org.apache.directory.studio.ldapbrowser.core.model.Control.MANAGEDSAIT_CONTROL.getOid(),
                org.apache.directory.studio.ldapbrowser.core.model.Control.MANAGEDSAIT_CONTROL.isCritical(),
                org.apache.directory.studio.ldapbrowser.core.model.Control.MANAGEDSAIT_CONTROL.getControlValue() ) };
        return controls;
    }
    
}
