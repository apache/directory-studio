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

import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.parser.LdifParser;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class LdifContentRecordTransfer extends ByteArrayTransfer
{

    private static final String TYPENAME = "org.apache.directory.ldapstudio.browser.ldifContentRecord";

    private static final int TYPEID = registerType( TYPENAME );

    private static LdifContentRecordTransfer instance = new LdifContentRecordTransfer();


    private LdifContentRecordTransfer()
    {
    }


    public static LdifContentRecordTransfer getInstance()
    {
        return instance;
    }


    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof LdifContentRecord[] ) )
            return;

        if ( isSupportedType( transferData ) )
        {
            LdifContentRecord[] records = ( LdifContentRecord[] ) object;

            StringBuffer sb = new StringBuffer();
            for ( int i = 0; i < records.length; i++ )
            {
                sb.append( records[i].toFormattedString() );
            }

            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream( out );

                byte[] bytes = sb.toString().getBytes();
                writeOut.writeInt( bytes.length );
                writeOut.write( bytes );

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

                LdifContentRecord[] records = null;
                try
                {

                    ByteArrayInputStream in = new ByteArrayInputStream( buffer );
                    DataInputStream readIn = new DataInputStream( in );

                    if ( readIn.available() > 1 )
                    {
                        int size = readIn.readInt();
                        byte[] bytes = new byte[size];
                        readIn.read( bytes );
                        String ldif = new String( bytes );
                        LdifFile model = new LdifParser().parse( ldif );
                        LdifRecord[] ldifRecords = model.getRecords();
                        records = new LdifContentRecord[ldifRecords.length];
                        for ( int i = 0; i < ldifRecords.length; i++ )
                        {
                            if ( ldifRecords[i] instanceof LdifContentRecord )
                            {
                                records[i] = ( LdifContentRecord ) ldifRecords[i];
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }

                    readIn.close();
                }
                catch ( IOException ex )
                {
                    return null;
                }

                return records;
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