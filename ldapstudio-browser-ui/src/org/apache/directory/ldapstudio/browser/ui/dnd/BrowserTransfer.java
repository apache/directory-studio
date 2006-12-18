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

package org.apache.directory.ldapstudio.browser.ui.dnd;


import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


// TODO: Browser Drag 'n' Drop
public class BrowserTransfer extends ByteArrayTransfer
{

    public static final String TYPENAME = "org.apache.directory.ldapstudio.browser.browser";

    private static final int TYPEID = registerType( TYPENAME );

    private static BrowserTransfer instance = new BrowserTransfer();


    private BrowserTransfer()
    {
    }


    public static BrowserTransfer getInstance()
    {
        return instance;
    }


    public void javaToNative( Object object, TransferData transferData )
    {

        if ( object == null || !( object instanceof String ) )
            return;

        if ( isSupportedType( transferData ) )
        {
            super.javaToNative( ( ( String ) object ).getBytes(), transferData );

            /*
             * BrowserTransferObject bto = (BrowserTransferObject) object;
             * try { ByteArrayOutputStream out = new
             * ByteArrayOutputStream(); DataOutputStream writeOut = new
             * DataOutputStream(out);
             * 
             * writeOut.writeInt(bto.getTransferType());
             * 
             * IEntry[] entries = bto.getEntriesToTransfer();
             * writeOut.writeInt(entries.length); for(int i=0; i<entries.length;
             * i++) { byte[] connectionName =
             * entries[i].getConnection().getName().getBytes();
             * writeOut.writeInt(connectionName.length);
             * writeOut.write(connectionName); byte[] dn =
             * entries[i].getDn().getBytes(); writeOut.writeInt(dn.length);
             * writeOut.write(dn); }
             * 
             * ISearch[] searches = bto.getSearchesToTransfer();
             * writeOut.writeInt(searches.length); for(int i=0; i<searches.length;
             * i++) { byte[] connectionName =
             * searches[i].getConnection().getName().getBytes();
             * writeOut.writeInt(connectionName.length);
             * writeOut.write(connectionName); byte[] searchName =
             * searches[i].getName().getBytes();
             * writeOut.writeInt(searchName.length);
             * writeOut.write(searchName); }
             * 
             * byte[] buffer = out.toByteArray(); writeOut.close();
             * 
             * super.javaToNative(buffer, transferData);
             *  } catch (IOException e) { }
             */
        }
    }


    public Object nativeToJava( TransferData transferData )
    {

        try
        {
            if ( isSupportedType( transferData ) )
            {

                byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
                if ( buffer != null )
                {
                    return new String( buffer );
                }
                else
                {
                    return null;
                }

                /*
                 * byte[] buffer = (byte[]) super.nativeToJava(transferData); if
                 * (buffer == null) return null;
                 * 
                 * try { IConnection connection = null; ByteArrayInputStream in =
                 * new ByteArrayInputStream(buffer); DataInputStream readIn =
                 * new DataInputStream(in);
                 * 
                 * int transferType = readIn.readInt();
                 * 
                 * int numberOfEntries = readIn.readInt(); IEntry[]
                 * entriesToTransfer = new IEntry[numberOfEntries]; for(int i=0;
                 * i<numberOfEntries; i++) { if(readIn.available() > 1) { int
                 * size = readIn.readInt(); byte[] connectionName = new
                 * byte[size]; readIn.read(connectionName); connection =
                 * BrowserPlugin.getDefault().getConnectionManager().getConnection(new
                 * String(connectionName)); } if(readIn.available() > 1 &&
                 * connection != null) { int size = readIn.readInt(); byte[] dn =
                 * new byte[size]; readIn.read(dn); InitializerProgressMonitor
                 * ipm = new InitializerProgressMonitor(){ public void
                 * reportProgress(String message) { } public void
                 * reportError(String message, Throwable exception) { } public
                 * boolean isCanceled() { return false; } public Throwable
                 * getReportedErrorThrowable() { return null; } public String
                 * getReportedErrorMessage() { return null; } };
                 * entriesToTransfer[i] = connection.getEntry(new String(dn),
                 * ipm); } }
                 * 
                 * int numberOfSearches = readIn.readInt(); ISearch[]
                 * searchesToTransfer = new ISearch[numberOfSearches]; for(int
                 * i=0; i<numberOfSearches; i++) { if(readIn.available() > 1) {
                 * int size = readIn.readInt(); byte[] connectionName = new
                 * byte[size]; readIn.read(connectionName); connection =
                 * BrowserPlugin.getDefault().getConnectionManager().getConnection(new
                 * String(connectionName)); } if(readIn.available() > 1 &&
                 * connection != null) { int size = readIn.readInt(); byte[]
                 * searchName = new byte[size]; readIn.read(searchName);
                 * searchesToTransfer[i] =
                 * connection.getSearchManager().getSearch(new
                 * String(searchName)); } }
                 * 
                 * readIn.close();
                 * 
                 * return new BrowserTransferObject(entriesToTransfer,
                 * searchesToTransfer); } catch (IOException ex) { return null; }
                 */
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }


    protected String[] getTypeNames()
    {
        return new String[]
            { TYPENAME };
    }


    protected int[] getTypeIds()
    {
        return new int[]
            { TYPEID };
    }
}