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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;


public final class RootDSE extends BaseDNEntry implements IRootDSE
{

    private static final long serialVersionUID = -8445018787232919754L;

    public static Properties oidMap = new Properties();
    static
    {

        try
        {
            URL url = RootDSE.class.getClassLoader().getResource(
                "org/apache/directory/ldapstudio/browser/core/model/ldap_oids.txt" ); //$NON-NLS-1$
            oidMap.load( url.openStream() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    protected RootDSE()
    {
    }


    public RootDSE( IConnection connection, ModelModifier source ) throws ModelModificationException
    {
        super();
        this.setDirectoryEntry( true );
        // this.connectionName = connection.getName();
        this.connection = connection;
        this.baseDn = new DN();
    }


    public boolean hasChildren()
    {
        return false;
    }


    public boolean isAttributesInitialized()
    {
        return true;
    }


    public String[] getSupportedExtensions()
    {
        if ( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION ) != null )
        {
            return get( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDEXTENSION ).getStringValues() );
        }
        else
        {
            return new String[0];
        }
    }


    public String[] getSupportedControls()
    {
        if ( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL ) != null )
        {
            return get( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL ).getStringValues() );
        }
        else
        {
            return new String[0];
        }
    }


    public String[] getSupportedFeatures()
    {
        if ( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES ) != null )
        {
            return get( getAttribute( ROOTDSE_ATTRIBUTE_SUPPORTEDFEATURES ).getStringValues() );
        }
        else
        {
            return new String[0];
        }
    }


    private String[] get( String[] a )
    {
        for ( int i = 0; i < a.length; i++ )
        {
            if ( oidMap.containsKey( a[i] ) )
            {
                String s = ( String ) oidMap.get( a[i] );
                a[i] = s;
                if ( s.matches( "^\".*\"" ) ) { //$NON-NLS-1$
                    a[i] = s.substring( 1, s.indexOf( "\"", 1 ) ); //$NON-NLS-1$
                }
            }
        }
        Arrays.sort( a );
        return a;
    }

}
