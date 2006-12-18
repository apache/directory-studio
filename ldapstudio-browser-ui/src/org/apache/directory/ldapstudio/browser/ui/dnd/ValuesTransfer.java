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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class ValuesTransfer extends ByteArrayTransfer
{

    private static final String TYPENAME = "org.apache.directory.ldapstudio.browser.value";

    private static final int TYPEID = registerType( TYPENAME );

    private static ValuesTransfer instance = new ValuesTransfer();


    public static ValuesTransfer getInstance()
    {
        return instance;
    }


    private ValuesTransfer()
    {
    }


    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof IValue[] ) )
            return;

        if ( isSupportedType( transferData ) )
        {
            IValue[] values = ( IValue[] ) object;
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream( out );

                for ( int i = 0; i < values.length; i++ )
                {
                    byte[] connectionName = values[i].getAttribute().getEntry().getConnection().getName().getBytes();
                    writeOut.writeInt( connectionName.length );
                    writeOut.write( connectionName );
                    byte[] dn = values[i].getAttribute().getEntry().getDn().toString().getBytes();
                    writeOut.writeInt( dn.length );
                    writeOut.write( dn );
                    byte[] attributeName = values[i].getAttribute().getDescription().getBytes();
                    writeOut.writeInt( attributeName.length );
                    writeOut.write( attributeName );
                    if ( values[i].isString() )
                    {
                        byte[] value = values[i].getStringValue().getBytes();
                        writeOut.writeBoolean( true );
                        writeOut.writeInt( value.length );
                        writeOut.write( value );
                    }
                    else if ( values[i].isBinary() )
                    {
                        byte[] value = values[i].getBinaryValue();
                        writeOut.writeBoolean( false );
                        writeOut.writeInt( value.length );
                        writeOut.write( value );
                    }
                }

                byte[] buffer = out.toByteArray();
                writeOut.close();

                super.javaToNative( buffer, transferData );

            }
            catch ( IOException e )
            {
            }
        }
    }


    public Object nativeToJava( TransferData transferData )
    {

        try
        {

            if ( isSupportedType( transferData ) )
            {

                byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
                if ( buffer == null )
                    return null;

                List valueList = new ArrayList();
                try
                {

                    ByteArrayInputStream in = new ByteArrayInputStream( buffer );
                    DataInputStream readIn = new DataInputStream( in );

                    do
                    {
                        IConnection connection = null;
                        if ( readIn.available() > 1 )
                        {
                            int size = readIn.readInt();
                            byte[] connectionName = new byte[size];
                            readIn.read( connectionName );
                            connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
                                new String( connectionName ) );
                        }

                        IEntry entry = null;
                        if ( readIn.available() > 1 && connection != null )
                        {
                            int size = readIn.readInt();
                            byte[] dn = new byte[size];
                            readIn.read( dn );
                            entry = connection.getEntryFromCache( new DN( new String( dn ) ) );
                        }
                        else
                        {
                            return null;
                        }

                        IAttribute attribute = null;
                        if ( readIn.available() > 1 && entry != null )
                        {
                            int size = readIn.readInt();
                            byte[] attributeName = new byte[size];
                            readIn.read( attributeName );
                            attribute = entry.getAttribute( new String( attributeName ) );
                        }
                        else
                        {
                            return null;
                        }

                        IValue value = null;
                        if ( readIn.available() > 1 && attribute != null )
                        {
                            boolean isString = readIn.readBoolean();
                            int size = readIn.readInt();
                            byte[] val = new byte[size];
                            readIn.read( val );
                            String test = new String( val );

                            IValue[] values = attribute.getValues();
                            for ( int i = 0; i < values.length; i++ )
                            {
                                if ( isString && values[i].isString() && test.equals( values[i].getStringValue() ) )
                                {
                                    value = values[i];
                                    break;
                                }
                                else if ( !isString && values[i].isBinary()
                                    && test.equals( new String( values[i].getBinaryValue() ) ) )
                                {
                                    value = values[i];
                                    break;
                                }
                            }
                        }
                        else
                        {
                            return null;
                        }

                        if ( value != null )
                        {
                            valueList.add( value );
                        }
                    }
                    while ( readIn.available() > 1 );

                    readIn.close();
                }
                catch ( IOException ex )
                {
                    return null;
                }

                return valueList.isEmpty() ? null : valueList.toArray( new IValue[valueList.size()] );
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