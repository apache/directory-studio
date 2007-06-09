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


import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionParameter;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IConnectionProvider;
import org.apache.directory.studio.ldapbrowser.core.model.ICredentials;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifModSpec;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifControlLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifSepLine;


public class JNDIConnectionProvider implements IConnectionProvider
{

    private JNDIConnectionContext context;


    public JNDIConnectionProvider()
    {
    }


    public void connect( ConnectionParameter parameter, ExtendedProgressMonitor monitor ) throws ConnectionException
    {
        try
        {
            this.context = new JNDIConnectionContext();
            this.context.connect( parameter.getHost(), parameter.getPort(),
                parameter.getEncryptionMethod() == IConnection.ENCYRPTION_LDAPS,
                parameter.getEncryptionMethod() == IConnection.ENCYRPTION_STARTTLS, null, monitor );
        }
        catch ( NamingException ne )
        {
            this.context = null;
            throw createConnectionException( null, ne );
        }
    }


    public void bind( ConnectionParameter parameter, ICredentials credentials, ExtendedProgressMonitor monitor )
        throws ConnectionException
    {
        try
        {
            if ( parameter.getAuthMethod() == IConnection.AUTH_SIMPLE )
            {
                String bindPrincipal = credentials.getBindPrincipal();
                String bindPassword = credentials.getBindPassword();
                this.context.bindSimple( bindPrincipal, bindPassword, monitor );
            }
            else if ( parameter.getAuthMethod() == IConnection.AUTH_ANONYMOUS )
            {
                this.context.bindAnonymous( monitor );
            }

        }
        catch ( NamingException ne )
        {
            this.context = null;
            throw createConnectionException( null, ne );
        }
    }


    public void close() throws ConnectionException
    {
        if ( this.context != null )
        {
            try
            {
                this.context.close();
                this.context = null;
            }
            catch ( NamingException e )
            {
            }
        }
    }


    public LdifEnumeration search( SearchParameter parameter, ExtendedProgressMonitor monitor )
        throws ConnectionException
    {
        SearchControls controls = new SearchControls();
        switch ( parameter.getScope() )
        {
            case ISearch.SCOPE_OBJECT:
                controls.setSearchScope( SearchControls.OBJECT_SCOPE );
                break;
            case ISearch.SCOPE_ONELEVEL:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                break;
            case ISearch.SCOPE_SUBTREE:
                controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
                break;
            default:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        }
        controls.setReturningAttributes( parameter.getReturningAttributes() );
        controls.setCountLimit( parameter.getCountLimit() );
        controls.setTimeLimit( parameter.getTimeLimit() );
        String filter = parameter.getFilter();
        String derefAliasMethod = getDerefAliasMethod( parameter );
        String handleReferralsMethod = getReferralsHandlingMethod( parameter.getReferralsHandlingMethod() );

        Control[] ldapControls = null;
        if ( parameter.getControls() != null )
        {
            org.apache.directory.studio.ldapbrowser.core.model.Control[] ctls = parameter.getControls();
            ldapControls = new Control[ctls.length];
            for ( int i = 0; i < ctls.length; i++ )
            {
                ldapControls[i] = new JNDIControl( ctls[i].getOid(), ctls[i].isCritical(), ctls[i].getControlValue() );
            }
            // Control subEntryControl = new
            // JNDIControl("1.3.6.1.4.1.4203.1.10.1", false, new
            // byte[]{0x01, 0x01, ( byte ) 0xFF});
            // ldapControls = new Control[]{subEntryControl};
        }

        try
        {
            NamingEnumeration list = this.context.search( parameter.getSearchBase().toString(), filter, controls,
                derefAliasMethod, handleReferralsMethod, ldapControls, monitor );
            return new LdifEnumerationImpl( list, parameter );
        }
        catch ( NamingException e )
        {
            throw createConnectionException( parameter, e );
        }

    }


    public void applyModification( LdifRecord record, int referralsHandlingMethod, ExtendedProgressMonitor monitor )
        throws ConnectionException
    {

        if ( !record.isValid() )
        {
            throw new ConnectionException( BrowserCoreMessages.model__invalid_record );
        }

        String dn = record.getDnLine().getValueAsString();

        if ( record instanceof LdifContentRecord )
        {
            LdifContentRecord attrValRecord = ( LdifContentRecord ) record;
            LdifAttrValLine[] attrVals = attrValRecord.getAttrVals();
            Attributes jndiAttributes = new BasicAttributes();
            for ( int ii = 0; ii < attrVals.length; ii++ )
            {
                String attributeName = attrVals[ii].getUnfoldedAttributeDescription();
                // String valueType = attrVals[ii].getValueType();
                // String value = attrVals[ii].getValue();
                Object realValue = attrVals[ii].getValueAsObject();

                if ( jndiAttributes.get( attributeName ) != null )
                {
                    jndiAttributes.get( attributeName ).add( realValue );
                }
                else
                {
                    jndiAttributes.put( attributeName, realValue );
                }
            }

            try
            {
                this.context.createSubcontext( dn, jndiAttributes, getControls( attrValRecord ), monitor );
            }
            catch ( NamingException ne )
            {
                throw createConnectionException( null, ne );
            }
        }
        else if ( record instanceof LdifChangeAddRecord )
        {
            LdifChangeAddRecord changeAddRecord = ( LdifChangeAddRecord ) record;
            LdifAttrValLine[] attrVals = changeAddRecord.getAttrVals();
            Attributes jndiAttributes = new BasicAttributes();
            for ( int ii = 0; ii < attrVals.length; ii++ )
            {
                String attributeName = attrVals[ii].getUnfoldedAttributeDescription();
                Object realValue = attrVals[ii].getValueAsObject();

                if ( jndiAttributes.get( attributeName ) != null )
                {
                    jndiAttributes.get( attributeName ).add( realValue );
                }
                else
                {
                    jndiAttributes.put( attributeName, realValue );
                }
            }

            try
            {
                this.context.createSubcontext( dn, jndiAttributes, getControls( changeAddRecord ), monitor );
            }
            catch ( NamingException ne )
            {
                throw createConnectionException( null, ne );
            }
        }
        else if ( record instanceof LdifChangeDeleteRecord )
        {
            LdifChangeDeleteRecord changeDeleteRecord = ( LdifChangeDeleteRecord ) record;
            try
            {
                this.context.destroySubcontext( dn, getControls( changeDeleteRecord ), monitor );
            }
            catch ( NamingException ne )
            {
                throw createConnectionException( null, ne );
            }
        }
        else if ( record instanceof LdifChangeModifyRecord )
        {
            LdifChangeModifyRecord modifyRecord = ( LdifChangeModifyRecord ) record;
            LdifModSpec[] modSpecs = modifyRecord.getModSpecs();
            ModificationItem[] mis = new ModificationItem[modSpecs.length];
            for ( int ii = 0; ii < modSpecs.length; ii++ )
            {
                LdifModSpecTypeLine modSpecType = modSpecs[ii].getModSpecType();
                LdifAttrValLine[] attrVals = modSpecs[ii].getAttrVals();

                Attribute attribute = new BasicAttribute( modSpecType.getUnfoldedAttributeDescription() );
                for ( int x = 0; x < attrVals.length; x++ )
                {
                    attribute.add( attrVals[x].getValueAsObject() );
                }

                if ( modSpecType.isAdd() )
                {
                    mis[ii] = new ModificationItem( DirContext.ADD_ATTRIBUTE, attribute );
                }
                else if ( modSpecType.isDelete() )
                {
                    mis[ii] = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, attribute );
                }
                else if ( modSpecType.isReplace() )
                {
                    mis[ii] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attribute );
                }
            }

            try
            {
                this.context.modifyAttributes( dn, mis, getControls( modifyRecord ), monitor );
            }
            catch ( NamingException ne )
            {
                throw createConnectionException( null, ne );
            }
        }
        else if ( record instanceof LdifChangeModDnRecord )
        {
            LdifChangeModDnRecord modDnRecord = ( LdifChangeModDnRecord ) record;
            if ( modDnRecord.getNewrdnLine() != null && modDnRecord.getDeloldrdnLine() != null )
            {
                String newRdn = modDnRecord.getNewrdnLine().getValueAsString();
                boolean deleteOldRdn = modDnRecord.getDeloldrdnLine().isDeleteOldRdn();

                try
                {
                    DN newDn;
                    if ( modDnRecord.getNewsuperiorLine() != null )
                        newDn = new DN( newRdn, modDnRecord.getNewsuperiorLine().getValueAsString() );
                    else
                    {
                        DN dnObject = new DN( dn );
                        newDn = new DN( newRdn.toString(), dnObject.getParentDn().toString() );
                    }

                    try
                    {
                        this.context.rename( dn.toString(), newDn.toString(), deleteOldRdn, getControls( modDnRecord ),
                            monitor );
                    }
                    catch ( NamingException ne )
                    {
                        throw createConnectionException( null, ne );
                    }
                }
                catch ( NameException ne )
                {
                    throw new ConnectionException( ne );
                }
            }
        }
    }


    private Control[] getControls( LdifRecord record )
    {
        Control[] controls = null;
        if ( record instanceof LdifChangeRecord )
        {
            LdifChangeRecord changeRecord = ( LdifChangeRecord ) record;
            LdifControlLine[] controlLines = changeRecord.getControls();
            controls = new Control[controlLines.length];
            for ( int i = 0; i < controlLines.length; i++ )
            {
                LdifControlLine line = controlLines[i];
                // TODO: encoded control value
                controls[i] = new JNDIControl( line.getUnfoldedOid(), line.isCritical(), null );
            }
        }
        return controls;
    }

    class LdifEnumerationImpl implements LdifEnumeration
    {

        private NamingEnumeration enumeration;

        private SearchParameter parameter;


        public LdifEnumerationImpl( NamingEnumeration enumeration, SearchParameter parameter )
        {
            this.enumeration = enumeration;
            this.parameter = parameter;
        }


        public boolean hasNext( ExtendedProgressMonitor monitor ) throws ConnectionException
        {
            try
            {
                return enumeration != null && enumeration.hasMore();
            }
            catch ( NamingException e )
            {
                throw createConnectionException( parameter, e );
            }
        }


        public LdifContainer next( ExtendedProgressMonitor monitor ) throws ConnectionException
        {

            try
            {
                SearchResult sr = ( SearchResult ) enumeration.next();

                DN dn = JNDIUtils.getDn( sr, parameter.getSearchBase().toString(), context );
                LdifContentRecord record = LdifContentRecord.create( dn.toString() );

                NamingEnumeration attributeEnumeration = sr.getAttributes().getAll();
                while ( attributeEnumeration.hasMore() )
                {
                    Attribute attribute = ( Attribute ) attributeEnumeration.next();
                    String attributeName = attribute.getID();
                    NamingEnumeration valueEnumeration = attribute.getAll();
                    while ( valueEnumeration.hasMore() )
                    {
                        Object o = valueEnumeration.next();
                        if ( o instanceof String )
                        {
                            record.addAttrVal( LdifAttrValLine.create( attributeName, ( String ) o ) );
                        }
                        if ( o instanceof byte[] )
                        {
                            record.addAttrVal( LdifAttrValLine.create( attributeName, ( byte[] ) o ) );
                        }
                    }
                }

                record.finish( LdifSepLine.create() );

                return record;

            }
            catch ( NamingException e )
            {
                throw createConnectionException( parameter, e );
            }
            catch ( NameException e )
            {
                throw new ConnectionException( e );
            }
            catch ( NoSuchFieldException e )
            {
                throw new ConnectionException( e );
            }
        }

    }


    private ConnectionException createConnectionException( SearchParameter searchParameter, Throwable e )
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
                ce = new org.apache.directory.studio.ldapbrowser.core.internal.model.ReferralException(
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


    private String getDerefAliasMethod( SearchParameter parameter )
    {
        String m = "always"; //$NON-NLS-1$

        switch ( parameter.getAliasesDereferencingMethod() )
        {
            case IConnection.DEREFERENCE_ALIASES_NEVER:
                m = "never"; //$NON-NLS-1$
                break;
            case IConnection.DEREFERENCE_ALIASES_ALWAYS:
                m = "always"; //$NON-NLS-1$
                break;
            case IConnection.DEREFERENCE_ALIASES_FINDING:
                m = "finding"; //$NON-NLS-1$
                break;
            case IConnection.DEREFERENCE_ALIASES_SEARCH:
                m = "searching"; //$NON-NLS-1$
                break;
        }

        return m;
    }


    private String getReferralsHandlingMethod( int referralHandlingMethod )
    {
        String m = "follow"; //$NON-NLS-1$

        switch ( referralHandlingMethod )
        {
            case IConnection.HANDLE_REFERRALS_IGNORE:
                m = "ignore"; //$NON-NLS-1$
                break;
            case IConnection.HANDLE_REFERRALS_FOLLOW:
                // m = "follow"; //$NON-NLS-1$
                m = "throw"; //$NON-NLS-1$
                break;
        }

        return m;
    }

}
