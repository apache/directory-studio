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
package org.apache.directory.ldapstudio.proxy.model;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;


/**
 * This class implements the thread for LDAP Proxy.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapProxyThread extends Thread
{
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
    public LdapProxyThread( int localPort, String remoteHost, int remotePort, long timeout )
    {
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

                            //                    while ( bb.hasRemaining() )
                            //                    {
                            //                        decode( bb );
                            //                    }

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
}
