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

package org.apache.directory.ldapstudio.dsmlv2.reponse;


import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.abandon.AbandonRequest;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.apache.directory.shared.ldap.codec.add.AddResponse;
import org.apache.directory.shared.ldap.codec.bind.BindRequest;
import org.apache.directory.shared.ldap.codec.bind.BindResponse;
import org.apache.directory.shared.ldap.codec.compare.CompareRequest;
import org.apache.directory.shared.ldap.codec.compare.CompareResponse;
import org.apache.directory.shared.ldap.codec.del.DelRequest;
import org.apache.directory.shared.ldap.codec.del.DelResponse;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequest;
import org.apache.directory.shared.ldap.codec.extended.ExtendedResponse;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponse;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequest;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNResponse;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.shared.ldap.codec.search.SearchResultDone;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.apache.directory.shared.ldap.codec.search.SearchResultReference;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequest;


/**
 * Decorator class for LDAP Message. This is the top level class, the one 
 * that holds the instance.
 */
public abstract class LdapMessageDecorator extends LdapMessage
{
    /** The decorated instance */
    protected LdapMessage instance;


    /**
     * Default constructor
     * @param ldapMessage the message to decorate
     */
    public LdapMessageDecorator( LdapMessage ldapMessage )
    {
        instance = ldapMessage;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#addControl(org.apache.directory.shared.ldap.codec.Control)
     */
    @Override
    public void addControl( Control control )
    {
        instance.addControl( control );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#computeLength()
     */
    @Override
    public int computeLength()
    {
        return instance.computeLength();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#encode(java.nio.ByteBuffer)
     */
    @Override
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return instance.encode( buffer );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getAbandonRequest()
     */
    @Override
    public AbandonRequest getAbandonRequest()
    {
        return instance.getAbandonRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getAddRequest()
     */
    @Override
    public AddRequest getAddRequest()
    {
        return instance.getAddRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getAddResponse()
     */
    @Override
    public AddResponse getAddResponse()
    {
        return instance.getAddResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getBindRequest()
     */
    @Override
    public BindRequest getBindRequest()
    {
        return instance.getBindRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getBindResponse()
     */
    @Override
    public BindResponse getBindResponse()
    {
        return instance.getBindResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getCompareRequest()
     */
    @Override
    public CompareRequest getCompareRequest()
    {
        return instance.getCompareRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getCompareResponse()
     */
    @Override
    public CompareResponse getCompareResponse()
    {
        return instance.getCompareResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getControls()
     */
    @Override
    public ArrayList getControls()
    {
        return instance.getControls();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getControls(int)
     */
    @Override
    public Control getControls( int i )
    {
        return instance.getControls( i );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getCurrentControl()
     */
    @Override
    public Control getCurrentControl()
    {
        return instance.getCurrentControl();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getDelRequest()
     */
    @Override
    public DelRequest getDelRequest()
    {
        return instance.getDelRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getDelResponse()
     */
    @Override
    public DelResponse getDelResponse()
    {
        return instance.getDelResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getExtendedRequest()
     */
    @Override
    public ExtendedRequest getExtendedRequest()
    {
        return instance.getExtendedRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getExtendedResponse()
     */
    @Override
    public ExtendedResponse getExtendedResponse()
    {
        return instance.getExtendedResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getLdapResponse()
     */
    @Override
    public LdapResponse getLdapResponse()
    {
        return instance.getLdapResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getMessageId()
     */
    @Override
    public int getMessageId()
    {
        return instance.getMessageId();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getMessageType()
     */
    @Override
    public int getMessageType()
    {
        return instance.getMessageType();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getMessageTypeName()
     */
    @Override
    public String getMessageTypeName()
    {
        return instance.getMessageTypeName();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getModifyDNRequest()
     */
    @Override
    public ModifyDNRequest getModifyDNRequest()
    {
        return instance.getModifyDNRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getModifyDNResponse()
     */
    @Override
    public ModifyDNResponse getModifyDNResponse()
    {
        return instance.getModifyDNResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getModifyRequest()
     */
    @Override
    public ModifyRequest getModifyRequest()
    {
        return instance.getModifyRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getModifyResponse()
     */
    @Override
    public ModifyResponse getModifyResponse()
    {
        return instance.getModifyResponse();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getSearchRequest()
     */
    @Override
    public SearchRequest getSearchRequest()
    {
        return instance.getSearchRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getSearchResultDone()
     */
    @Override
    public SearchResultDone getSearchResultDone()
    {
        return instance.getSearchResultDone();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getSearchResultEntry()
     */
    @Override
    public SearchResultEntry getSearchResultEntry()
    {
        return instance.getSearchResultEntry();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getSearchResultReference()
     */
    @Override
    public SearchResultReference getSearchResultReference()
    {
        return instance.getSearchResultReference();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#getUnBindRequest()
     */
    @Override
    public UnBindRequest getUnBindRequest()
    {
        return instance.getUnBindRequest();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#setMessageId(int)
     */
    @Override
    public void setMessageId( int messageId )
    {
        instance.setMessageId( messageId );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#setProtocolOP(org.apache.directory.shared.asn1.Asn1Object)
     */
    @Override
    public void setProtocolOP( Asn1Object protocolOp )
    {
        instance.setProtocolOP( protocolOp );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessage#toString()
     */
    @Override
    public String toString()
    {
        return instance.toString();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#addLength(int)
     */
    @Override
    public void addLength( int length ) throws DecoderException
    {
        instance.addLength( length );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getCurrentLength()
     */
    @Override
    public int getCurrentLength()
    {
        return instance.getCurrentLength();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getExpectedLength()
     */
    @Override
    public int getExpectedLength()
    {
        return instance.getExpectedLength();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getParent()
     */
    @Override
    public Asn1Object getParent()
    {
        return instance.getParent();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setCurrentLength(int)
     */
    @Override
    public void setCurrentLength( int currentLength )
    {
        instance.setCurrentLength( currentLength );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setExpectedLength(int)
     */
    @Override
    public void setExpectedLength( int expectedLength )
    {
        instance.setExpectedLength( expectedLength );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setParent(org.apache.directory.shared.asn1.Asn1Object)
     */
    @Override
    public void setParent( Asn1Object parent )
    {
        instance.setParent( parent );
    }

}
