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

package org.apache.directory.ldapstudio.dsmlv2.engine;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.naming.NamingException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.ldapstudio.dsmlv2.BatchRequest;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.ldapstudio.dsmlv2.BatchRequest.OnError;
import org.apache.directory.ldapstudio.dsmlv2.BatchRequest.Processing;
import org.apache.directory.ldapstudio.dsmlv2.BatchRequest.ResponseOrder;
import org.apache.directory.ldapstudio.dsmlv2.reponse.AddResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.AuthResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.CompareResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.DelResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ExtendedResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ModDNResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ModifyResponseDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResultDoneDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResultEntryDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResultReferenceDsml;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse.ErrorResponseType;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapDecoder;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.bind.BindRequest;
import org.apache.directory.shared.ldap.codec.bind.BindResponse;
import org.apache.directory.shared.ldap.codec.bind.LdapAuthentication;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.codec.extended.ExtendedResponse;
import org.apache.directory.shared.ldap.codec.util.LdapResultEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.StringTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.xmlpull.v1.XmlPullParserException;


/**
 * This is the DSMLv2Engine. It can be use to execute operations on a LDAP Server and get the results of these operations.
 * The format used for request and responses is the DSMLv2 format.
 */
public class Dsmlv2Engine
{
    /** Socket used to connect to the server */
    private SocketChannel channel;
    private SocketAddress serverAddress;

    // server configuration
    private int port;
    private String host;
    private String user;
    private String password;

    private Asn1Decoder ldapDecoder = new LdapDecoder();

    private IAsn1Container ldapMessageContainer = new LdapMessageContainer();

    private Dsmlv2Parser parser;

    private boolean continueOnError;
    private boolean exit = false;

    private int bbLimit;

    private int bbposition;
    private Document xmlResponse;
    private BatchRequest batchRequest;


    /**
     * Default Constructor
     * 
     * @param host the server host
     * @param port the server port
     * @param user the server admin DN
     * @param password the server admin DN's password
     */
    public Dsmlv2Engine( String host, int port, String user, String password )
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    /**
     * Processes the file given and return the result of the operations
     * @param dsmlInput The DSMLv2 formatted request input
     * @return The XML response in DSMLv2 Format
     * @throws XmlPullParserException
     * @throws FileNotFoundException
     */
    public String processDSML( String dsmlInput ) throws XmlPullParserException, FileNotFoundException
    {
        parser = new Dsmlv2Parser();

        parser.setInput( dsmlInput );

        return processDSML();
    }


    /**
     * Processes the file given and return the result of the operations
     * @param fileName The path to the file
     * @return The XML response in DSMLv2 Format
     * @throws XmlPullParserException
     * @throws FileNotFoundException
     */
    public String processDSMLFile( String fileName ) throws XmlPullParserException, FileNotFoundException
    {
        parser = new Dsmlv2Parser();

        parser.setInputFile( fileName );

        return processDSML();
    }


    /**
     * Processes the file given and return the result of the operations
     * @param inputStream contains a raw byte input stream of possibly unknown encoding (when inputEncoding is null).
     * @param inputEncoding if not null it MUST be used as encoding for inputStream
     * @return The XML response in DSMLv2 Format
     * @throws XmlPullParserException
     * @throws FileNotFoundException
     */
    public String processDSML( InputStream inputStream, String inputEncoding ) throws XmlPullParserException,
        FileNotFoundException
    {
        parser = new Dsmlv2Parser();

        parser.setInput( inputStream, inputEncoding );

        return processDSML();
    }


    /**
     * Processes the Request document
     * @return The XML response in DSMLv2 Format
     */
    private String processDSML()
    {
        // Creating XML Document and root Element 'batchResponse'
        xmlResponse = DocumentHelper.createDocument();
        xmlResponse.addElement( "batchResponse" );

        // Binding to LDAP Server
        try
        {
            bind( 1 );
        }
        catch ( Exception e )
        {
            // Unable to connect to server
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.COULD_NOT_CONNECT, e.getMessage() );

            errorResponse.toDsml( xmlResponse.getRootElement() );
            return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
        }

        // Processing BatchRequest:
        //    - Parsing and Getting BatchRequest
        //    - Getting and registering options from BatchRequest
        try
        {
            processBatchRequest();
        }
        catch ( XmlPullParserException e )
        {
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, e.getMessage()
                + " - Line " + e.getLineNumber() + " - Column " + e.getColumnNumber() );

            errorResponse.toDsml( xmlResponse.getRootElement() );
            return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
        }

        // Processing each request:
        //    - Getting a new request
        //    - Checking if the request is well formed
        //    - Sending the request to the server
        //    - Getting and converting reponse(s) as XML
        //    - Looping until last request
        LdapMessage request = null;
        try
        {
            request = parser.getNextRequest();
        }
        catch ( XmlPullParserException e )
        {
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, e.getMessage()
                + " - Line " + e.getLineNumber() + " - Column " + e.getColumnNumber() );

            errorResponse.toDsml( xmlResponse.getRootElement() );
            return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
        }

        while ( request != null ) // (Request == null when there's no more request to process)
        {
            // Checking the request has a requestID attribute if Processing = Parallel and ResponseOrder = Unordered
            if ( ( batchRequest.getProcessing().equals( Processing.PARALLEL ) )
                && ( batchRequest.getResponseOrder().equals( ResponseOrder.UNORDERED ) )
                && ( request.getMessageId() == 0 ) )
            {
                // Then we have to send an errorResponse
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST,
                    "A requestID must be specified to each request when Processing is Parallel and ReponseOrder is Unordered." );

                errorResponse.toDsml( xmlResponse.getRootElement() );
                return xmlResponse.asXML();
            }

            try
            {
                processRequest( request );
            }
            catch ( Exception e )
            {
                // We create a new ErrorResponse and return the XML response.
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.GATEWAY_INTERNAL_ERROR,
                    "Internal Error: " + e.getMessage() );

                errorResponse.toDsml( xmlResponse.getRootElement() );
                return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
            }

            // Checking if we need to exit processing (if an error has ocurred if onError == Exit)
            if ( exit )
            {
                break;
            }

            // Getting next request
            try
            {
                request = parser.getNextRequest();
            }
            catch ( XmlPullParserException e )
            {
                // We create a new ErrorResponse and return the XML response.
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, e.getMessage()
                    + " - Line " + e.getLineNumber() + " - Column " + e.getColumnNumber() );

                errorResponse.toDsml( xmlResponse.getRootElement() );
                return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
            }
        }

        return styleDocument( xmlResponse, "DSMLv2.xslt" ).asXML();
    }


    /**
     * Processes a single request
     * @param request the request to process
     * @throws EncoderException 
     * @throws IOException 
     * @throws NamingException 
     * @throws DecoderException 
     */
    private void processRequest( LdapMessage request ) throws EncoderException, IOException, DecoderException,
        NamingException
    {
        LdapMessage message = new LdapMessage();

        message.setProtocolOP( request );

        message.setMessageId( request.getMessageId() );

        ByteBuffer bb = null;

        bb = message.encode( null );

        bb.flip();

        sendMessage( bb );

        bb.clear();
        bb.position( bb.capacity() );
        // Get the response
        LdapMessage response = null;

        response = readResponse( bb );

        if ( LdapConstants.ADD_RESPONSE == response.getMessageType() )
        {
            AddResponseDsml addResponseDsml = new AddResponseDsml( response );
            addResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.BIND_RESPONSE == response.getMessageType() )
        {
            AuthResponseDsml authResponseDsml = new AuthResponseDsml( response );
            authResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.COMPARE_RESPONSE == response.getMessageType() )
        {
            CompareResponseDsml authResponseDsml = new CompareResponseDsml( response );
            authResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.DEL_RESPONSE == response.getMessageType() )
        {
            DelResponseDsml delResponseDsml = new DelResponseDsml( response );
            delResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.MODIFY_RESPONSE == response.getMessageType() )
        {
            ModifyResponseDsml modifyResponseDsml = new ModifyResponseDsml( response );
            modifyResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.MODIFYDN_RESPONSE == response.getMessageType() )
        {
            ModDNResponseDsml modDNResponseDsml = new ModDNResponseDsml( response );
            modDNResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( LdapConstants.EXTENDED_RESPONSE == response.getMessageType() )
        {
            ExtendedResponseDsml extendedResponseDsml = new ExtendedResponseDsml( response );
            extendedResponseDsml.toDsml( xmlResponse.getRootElement() );
        }
        else if ( ( LdapConstants.SEARCH_RESULT_ENTRY == response.getMessageType() )
            || ( LdapConstants.SEARCH_RESULT_REFERENCE == response.getMessageType() )
            || ( LdapConstants.SEARCH_RESULT_DONE == response.getMessageType() ) )
        {
            // A SearchResponse can contains multiple responses of 3 types:
            //     - 0 to n SearchResultEntry
            //     - O to n SearchResultReference
            //     - 1 (only) SearchResultDone
            // So we have to include those individual reponses in a "General" SearchResponse
            Element searchResponse = xmlResponse.getRootElement().addElement( "searchResponse" );

            // RequestID
            int requestID = response.getMessageId();
            if ( requestID != 0 )
            {
                searchResponse.addAttribute( "requestID", "" + requestID );
            }

            while ( LdapConstants.SEARCH_RESULT_DONE != response.getMessageType() )
            {
                if ( LdapConstants.SEARCH_RESULT_ENTRY == response.getMessageType() )
                {
                    SearchResultEntryDsml searchResultEntryDsml = new SearchResultEntryDsml( response );
                    searchResultEntryDsml.toDsml( searchResponse );
                }
                else if ( LdapConstants.SEARCH_RESULT_REFERENCE == response.getMessageType() )
                {
                    SearchResultReferenceDsml searchResultReferenceDsml = new SearchResultReferenceDsml( response );
                    searchResultReferenceDsml.toDsml( searchResponse );
                }

                response = readResponse( bb );
            }

            SearchResultDoneDsml searchResultDoneDsml = new SearchResultDoneDsml( response );
            searchResultDoneDsml.toDsml( searchResponse );
        }

        LdapResponse realResponse = response.getLdapResponse();

        if ( !continueOnError )
        {
            if ( ( realResponse.getLdapResult().getResultCode() != LdapResultEnum.SUCCESS )
                && ( realResponse.getLdapResult().getResultCode() != LdapResultEnum.COMPARE_TRUE )
                && ( realResponse.getLdapResult().getResultCode() != LdapResultEnum.COMPARE_FALSE )
                && ( realResponse.getLdapResult().getResultCode() != LdapResultEnum.REFERRAL ) )
            {
                // Turning on Exit flag
                exit = true;
            }
        }

    }


    /**
     * Processes the BatchRequest
     *     - Parsing and Getting BatchRequest
     *     - Getting and registering options from BatchRequest
     * @throws XmlPullParserException
     */
    private void processBatchRequest() throws XmlPullParserException
    {
        // Parsing BatchRequest
        parser.parseBatchRequest();

        // Getting BatchRequest
        batchRequest = parser.getBatchRequest();

        if ( OnError.RESUME.equals( batchRequest.getOnError() ) )
        {
            continueOnError = true;
        }
        else if ( OnError.EXIT.equals( batchRequest.getOnError() ) )
        {
            continueOnError = false;
        }

        if ( batchRequest.getRequestID() != 0 )
        {
            xmlResponse.getRootElement().addAttribute( "requestID", "" + batchRequest.getRequestID() );
        }
    }


    /**
     * XML Pretty Printer XSLT Tranformation
     * @param document
     * @param stylesheet
     * @return
     */
    public Document styleDocument( Document document, String stylesheet )
    {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = factory
                .newTransformer( new StreamSource( Dsmlv2Engine.class.getResourceAsStream( stylesheet ) ) );
        }
        catch ( TransformerConfigurationException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();
        try
        {
            transformer.transform( source, result );
        }
        catch ( TransformerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }


    /**
     * Connect to the LDAP server through a socket and establish the Input and
     * Output Streams. All the required information for the connection should be
     * in the options from the command line, or the default values.
     * 
     * @throws UnknownHostException
     *             The hostname or the Address of server could not be found
     * @throws IOException
     *             There was a error opening or establishing the socket
     */
    private void connect() throws UnknownHostException, IOException
    {
        serverAddress = new InetSocketAddress( host, port );
        channel = SocketChannel.open( serverAddress );

        channel.configureBlocking( true );
    }


    /**
     * Sends a message
     * @param bb
     * @throws IOException
     */
    private void sendMessage( ByteBuffer bb ) throws IOException
    {
        channel.write( bb );
        bb.clear();
    }


    /**
     * Reads the response to a request
     * @param bb
     * @return the response
     * @throws IOException
     * @throws DecoderException
     * @throws NamingException
     */
    private LdapMessage readResponse( ByteBuffer bb ) throws IOException, DecoderException, NamingException
    {

        LdapMessage messageResp = null;

        if ( bb.hasRemaining() )
        {
            bb.position( bbposition );
            bb.limit( bbLimit );
            ldapDecoder.decode( bb, ldapMessageContainer );
            bbposition = bb.position();
            bbLimit = bb.limit();
        }
        bb.flip();
        while ( ldapMessageContainer.getState() != TLVStateEnum.PDU_DECODED )
        {

            int nbRead = channel.read( bb );

            if ( nbRead == -1 )
            {
                System.err.println( "fsdfsdfsdfsd" );
            }

            bb.flip();
            ldapDecoder.decode( bb, ldapMessageContainer );
            bbposition = bb.position();
            bbLimit = bb.limit();
            bb.flip();
        }

        messageResp = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();

        if ( messageResp instanceof BindResponse )
        {
            BindResponse resp = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage().getBindResponse();

            if ( resp.getLdapResult().getResultCode() != 0 )
            {
                System.err.println( "Error : " + resp.getLdapResult().getErrorMessage() );
            }
        }
        else if ( messageResp instanceof ExtendedResponse )
        {
            ExtendedResponse resp = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage()
                .getExtendedResponse();

            if ( resp.getLdapResult().getResultCode() != 0 )
            {
                System.err.println( "Error : " + resp.getLdapResult().getErrorMessage() );
            }
        }

        ( ( LdapMessageContainer ) ldapMessageContainer ).clean();

        return messageResp;
    }


    /**
     * Bind to the ldap server
     * 
     * @param messageId The message Id
     * @throws NamingException 
     */
    private void bind( int messageId ) throws EncoderException, DecoderException, IOException, NamingException
    {
        BindRequest bindRequest = new BindRequest();
        LdapMessage message = new LdapMessage();
        LdapAuthentication authentication = new SimpleAuthentication();
        ( ( SimpleAuthentication ) authentication ).setSimple( StringTools.getBytesUtf8( password ) );

        bindRequest.setAuthentication( authentication );
        bindRequest.setName( new LdapDN( user ) );
        bindRequest.setVersion( 3 );

        message.setProtocolOP( bindRequest );
        message.setMessageId( messageId );

        // Encode and send the bind request
        ByteBuffer bb = message.encode( null );
        bb.flip();

        connect();
        sendMessage( bb );

        bb.clear();

        bb.position( bb.limit() );

        readResponse( bb );
    }
}
