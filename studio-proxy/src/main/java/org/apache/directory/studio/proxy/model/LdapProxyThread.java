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
package org.apache.directory.studio.proxy.model;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.util.Asn1StringUtils;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapDecoder;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;


/**
 * This class implements the thread for LDAP Proxy.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapProxyThread extends Thread
{
    /** The LDAP proxy */
    private LdapProxy ldapProxy;
    
    /** The proxy port */
    private int localPort;

    /** The LDAP Server hostname */
    private String remoteHost;

    /** The LDAP Server port */
    private int remotePort;

    /** The timeout */
    private long timeout;

    /** The client socket */
    private Socket cSocket;

    /** The last message ID */
    private int lastMessageId = -1;


    /**
     * Creates a new instance of LdapProxyThread.
     *
     * @param localPort
     * @param remoteHost
     * @param remotePort
     * @param timeout
     * @param cSocket
     */
    public LdapProxyThread( LdapProxy ldapProxy, int localPort, String remoteHost, int remotePort, long timeout )
    {
        this.ldapProxy = ldapProxy;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.timeout = timeout;
    }


    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        try
        {
            ServerSocket sSocket = new ServerSocket( localPort );
            cSocket = sSocket.accept();

            if ( cSocket != null )
            {
                InputStream clientIn = null;
                OutputStream clientOut = null;
                InputStream serverIn = null;
                OutputStream serverOut = null;
                Socket toServer = null;
                int r0 = -1;
                int r1 = -1;
                int ch = -1;
                int i = -1;
                long time0 = System.currentTimeMillis();
                long time1 = time0 + timeout;

                try
                {
                    toServer = new Socket( remoteHost, remotePort );
                    System.out.println( "open connection to:" + toServer + "(timeout=" + timeout + " ms)" );
                    clientIn = cSocket.getInputStream();
                    clientOut = new BufferedOutputStream( cSocket.getOutputStream() );
                    serverIn = toServer.getInputStream();
                    serverOut = new BufferedOutputStream( toServer.getOutputStream() );

                    while ( r0 != 0 || r1 != 0 || ( time1 - time0 ) <= timeout )
                    {
                        r0 = clientIn.available();

                        if ( r0 != 0 )
                        {
                            System.out.println( "" );
                            // LdapProxy.setTextIn("<<<" + r0 + " bytes from client
                            // \n","red");
                            System.out.println( "" );
                            System.out.println( "<<<" + r0 + " bytes from client" );
                            System.out.println( "<<<" + r1 + " bytes from server" );
                        }

                        ByteBuffer bb = null;

                        while ( ( r0 = clientIn.available() ) > 0 )
                        {
                            byte[] in = new byte[r0];
                            int k = 0;

                            for ( i = 0; i < r0; i++ )
                            {
                                ch = clientIn.read();

                                if ( ch != -1 )
                                {
                                    in[k++] = ( byte ) ch;
                                    serverOut.write( ch );
                                }
                                else
                                {
                                    System.out.println( "client stream closed" );
                                    // LdapProxy.setTextIn("client stream closed
                                    // \n","redbold");
                                }
                            }

                            bb = ByteBuffer.allocate( r0 );
                            bb.put( in );
                            bb.flip();

                            while ( bb.hasRemaining() )
                            {
                                decode( bb );
                            }

                            /*
                             * LdapProxy.setTextIn("--->>>\n", "regular");
                             * LdapProxy.setTextIn(result, "regular");
                             * LdapProxy.setTextIn("----------------------------------\n",
                             * "regular");
                             */

                            time0 = System.currentTimeMillis();
                            serverOut.flush();
                        }

                        bb = null;

                        while ( ( r1 = serverIn.available() ) > 0 )
                        {
                            System.out.println( "" );
                            System.out.println( ">>>" + r1 + " bytes from server" );
                            // LdapProxy.setTextOut(">>>" + r1 + " bytes from server
                            // \n","red");
                            System.out.println( "" );
                            System.out.println( ">>>" + r1 + " bytes from server" );

                            byte[] out = new byte[r1];
                            int k = 0;

                            for ( i = 0; i < r1; i++ )
                            {
                                ch = serverIn.read();

                                if ( ch != -1 )
                                {
                                    out[k++] = ( byte ) ch;
                                }
                                else
                                {
                                    System.out.println( "server stream closed" );
                                    // LdapProxy.setTextOut("server stream closed
                                    // \n","redbold");
                                    break;
                                }

                                clientOut.write( ch );
                            }

                            bb = ByteBuffer.allocate( r1 );
                            bb.put( out );
                            bb.flip();

                            //                    while ( true )
                            //                    {
                            //                        try
                            //                        {
                            //                            String result = decode( bb );
                            //                            System.out.println( result );
                            //                            /*
                            //                             * LdapProxy.setTextOut("<<<---\n", "blue");
                            //                             * LdapProxy.setTextOut(result, "blue");
                            //                             * LdapProxy.setTextOut("----------------------------------\n",
                            //                             * "regular");
                            //                             */
                            //                            if ( bb.hasRemaining() == false )
                            //                            {
                            //                                break;
                            //                            }
                            //                        }
                            //                        catch ( DecoderException de )
                            //                        {
                            //                            StringBuffer result = new StringBuffer();
                            //                            result.append( de.getMessage() ).append( '\n' );
                            //
                            //                            byte[] tmp = new byte[out.length - bb.position()];
                            //                            System.arraycopy( out, bb.position(), tmp, 0, out.length - bb.position() );
                            //                            result.append( StringUtils.dumpBytes( tmp ) );
                            //
                            //                            System.out.println( result );
                            //                            /*
                            //                             * LdapProxy.setTextOut("<<<---\n", "regular");
                            //                             * LdapProxy.setTextOut(new String(tmp), "regular");
                            //                             * LdapProxy.setTextOut("----------------------------------\n",
                            //                             * "regular");
                            //                             */
                            //                            break;
                            //                        }
                            //                    }

                            time0 = new Date().getTime();
                            clientOut.flush();
                        }
                        if ( r0 == 0 && r1 == 0 )
                        {
                            time1 = new Date().getTime();
                            Thread.sleep( 100 );
                            // Proxy.display("waiting:"+(time1-time0)+" ms");
                        }
                    }
                }
                catch ( Throwable t )
                {
                    System.out.println( "i=" + i + " ch=" + ch );
                    t.printStackTrace( System.err );
                }
                finally
                {
                    try
                    {
                        clientIn.close();
                        clientOut.close();
                        serverIn.close();
                        serverOut.close();
                        cSocket.close();
                        toServer.close();
                        //                LdapProxy.quit( time1 - time0 );
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace( System.err );
                    }
                }
            }
        }
        catch ( IOException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    private String decode( ByteBuffer buffer ) throws DecoderException, NamingException
    {
        int position = buffer.position();

        DefaultMutableTreeNode mess;

        // DefaultMutableTreeNode messTrue;
        DefaultMutableTreeNode messTrue;

        Asn1Decoder ldapDecoder = new LdapDecoder();

        // Allocate a LdapMessageContainer Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the PDU
        ldapDecoder.decode( buffer, ldapMessageContainer );
        // Check that everything is OK
        LdapMessage ldapmessage = ( (LdapMessageContainer) ldapMessageContainer ).getLdapMessage();
        LdapMessageWithPDU message = new LdapMessageWithPDU();
        

        message.setLdapMessage( ldapmessage );
        message.setMessageId( ldapmessage.getMessageId() );

        // check the Id to verfy if it's a new Entry or not
        if ( message.getMessageId() != lastMessageId )
        {
//            mess = new DefaultMutableTreeNode( transformToStringType( ldapmessage.getMessageType() ) + " [Id = "
//                    + ( (LdapMessage) ldapmessage ).getMessageId() + " ]" );
//
//            messTrue = new DefaultMutableTreeNode( message );
//            lastNode = mess;
//            lastMessNode = messTrue;
//            lastMessageId = message.getMessageId();
//            // mainFrame.getTop().add(mess);
//            mainFrame.getTreeModel().insertNodeInto( mess, mainFrame.getTop(), mainFrame.getTop().getChildCount() );
//            mainFrame.getLdapMessageTree().add( messTrue );
//            currentCount = 1;

        }
        else
        {
//            mess = lastNode;
//            messTrue = lastMessNode;
//            currentCount++;
        }

          String type = transformToStringType( ldapmessage.getMessageType() );

        int pduLength = buffer.position() - position;
        byte[] bytes = buffer.array();
        byte[] newBytes = new byte[pduLength];
        System.arraycopy(bytes, position, newBytes, 0, pduLength );

        //TODO only one methode to Dump including buffer and position.
        message.setDumpBytes( Asn1StringUtils.dumpBytes( newBytes ) );

        ldapProxy.addReceivedLdapMessage( message );
        
        return message.getLdapMessage().toString();
    }
    
    public String transformToStringType( int type )
    {
        String stringType;

        switch ( type )
        {
            case LdapConstants.ABANDON_REQUEST :
                stringType = "ABANDON REQUEST";
                break;
            case LdapConstants.ADD_REQUEST :
                stringType = "ADD REQUEST";
                break;
            case LdapConstants.ADD_RESPONSE :
                stringType = "ADD RESPONSE";
                break;
            case LdapConstants.BIND_REQUEST :
                stringType = "BIND REQUEST";
                break;
            case LdapConstants.BIND_RESPONSE :
                stringType = "BIND RESPONSE";
                break;
            case LdapConstants.COMPARE_REQUEST :
                stringType = "COMPARE REQUEST";
                break;
            case LdapConstants.COMPARE_RESPONSE :
                stringType = "COMPARE RESPONSE";
                break;
            case LdapConstants.DEL_REQUEST :
                stringType = "DEL REQUEST";
                break;
            case LdapConstants.DEL_RESPONSE :
                stringType = "DEL RESPONSE";
                break;
            case LdapConstants.EXTENDED_REQUEST :
                stringType = "EXTENDED REQUEST";
                break;
            case LdapConstants.EXTENDED_RESPONSE :
                stringType = "EXTENDED RESPONSE";
                break;
            case LdapConstants.MODIFYDN_REQUEST :
                stringType = "MODIFYDN REQUEST";
                break;
            case LdapConstants.MODIFYDN_RESPONSE :
                stringType = "MODIFYDN RESPONSE";
                break;
            case LdapConstants.MODIFY_REQUEST :
                stringType = "MODIFY REQUEST";
                break;
            case LdapConstants.MODIFY_RESPONSE :
                stringType = "MODIFY RESPONSE";
                break;
            case LdapConstants.SEARCH_REQUEST :
                stringType = "SEARCH REQUEST";
                break;
            case LdapConstants.SEARCH_RESULT_DONE :
                stringType = "SEARCH RESULT DONE";
                break;
            case LdapConstants.SEARCH_RESULT_ENTRY :
                stringType = "SEARCH RESULT ENTRY";
                break;
            case LdapConstants.SEARCH_RESULT_REFERENCE :
                stringType = "SEARCH RESULT REFERENCE";
                break;
            case LdapConstants.UNBIND_REQUEST :
                stringType = "UNBIND REQUEST";
                break;
            case LdapConstants.UNKNOWN:
                stringType = "UNKNOWN";
                break;

            default :
                stringType = "UNKNOWN";
                break;
        }
        
        return stringType;
    }
}
