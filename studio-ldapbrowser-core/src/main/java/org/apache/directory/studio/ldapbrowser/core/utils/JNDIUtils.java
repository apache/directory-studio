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
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ReferralException;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;




public class JNDIUtils
{

    public static DN getDn( javax.naming.directory.SearchResult sr ) throws NamingException,
        NameException, NoSuchFieldException
    {
        String dn = sr.getNameInNamespace();
        dn = unescapeJndiName( dn );
        return new DN( dn );
    }


    /**
     * Correct some JNDI encodings...
     * 
     * @param name
     * @return
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


    public static ConnectionException createConnectionException( SearchParameter searchParameter, Throwable e )
    {
        ConnectionException connectionException = null;
        ConnectionException lastException = null;
    
        do
        {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            int ldapStatusCode = -1;
            String[] referrals = null;
    
            // get LDAP status code
            // [LDAP: error code 21 - telephoneNumber: value #0 invalid per
            // syntax]
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
            if ( e instanceof ReferralException )
            {
    
                message = "Referrals: "; //$NON-NLS-1$
                ReferralException re;
                ArrayList referralsList = new ArrayList();
    
                re = ( ReferralException ) e;
                message += BrowserCoreConstants.LINE_SEPARATOR + re.getReferralInfo();
                referralsList.add( re.getReferralInfo() );
    
                while ( re.skipReferral() )
                {
                    try
                    {
                        Context ctx = re.getReferralContext();
                        ctx.list( "" ); //$NON-NLS-1$
                    }
                    catch ( NamingException e1 )
                    {
                        if ( e1 instanceof ReferralException )
                        {
                            re = ( ReferralException ) e1;
                            message += BrowserCoreConstants.LINE_SEPARATOR + re.getReferralInfo();
                            referralsList.add( re.getReferralInfo() );
                        }
                        else
                        {
                            break;
                        }
                    }
                }
    
                referrals = ( String[] ) referralsList.toArray( new String[referralsList.size()] );
            }
    
            ConnectionException ce;
            if ( referrals != null )
            {
                ce = new org.apache.directory.studio.ldapbrowser.core.model.ReferralException(
                    searchParameter, referrals, ldapStatusCode, message, e );
            }
            else
            {
                ce = new ConnectionException( ldapStatusCode, message, e );
            }
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

}
