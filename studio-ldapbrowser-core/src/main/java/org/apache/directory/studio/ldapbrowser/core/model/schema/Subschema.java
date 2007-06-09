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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


public class Subschema implements Serializable
{

    private static final long serialVersionUID = 7821844589084867562L;

    private String[] objectClassNames;

    private Schema schema;

    private Set allAttributeNameSet;


    protected Subschema()
    {
    }


    public Subschema( IEntry entry )
    {
        if ( entry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE ) != null )
        {
            this.objectClassNames = entry.getAttribute( IAttribute.OBJECTCLASS_ATTRIBUTE ).getStringValues();
        }
        else
        {
            this.objectClassNames = new String[0];
        }
        this.schema = entry.getConnection().getSchema();
    }


    public Subschema( String[] objectClassNames, IConnection connection )
    {
        this.objectClassNames = objectClassNames;
        this.schema = connection.getSchema();
    }


    /**
     * Returns the names of this and all super object classes.
     * 
     * @return
     */
    public String[] getObjectClassNames()
    {
        return this.objectClassNames;
    }


    /**
     * Returns the must attribute names of this and all super object
     * classes.
     * 
     * @return
     */
    public String[] getMustAttributeNames()
    {
        Set mustAttrSet = new TreeSet();
        for ( int i = 0; i < this.objectClassNames.length; i++ )
        {
            this.fetchMust( this.objectClassNames[i], mustAttrSet );
        }
        return ( String[] ) mustAttrSet.toArray( new String[0] );
    }


    /**
     * Returns the must attribute types of this and all super object
     * classes.
     * 
     * @return
     */
    public AttributeTypeDescription[] getMustAttributeTypeDescriptions()
    {
        String[] musts = getMustAttributeNames();
        AttributeTypeDescription[] atds = new AttributeTypeDescription[musts.length];
        for ( int i = 0; i < musts.length; i++ )
        {
            AttributeTypeDescription atd = getSchema().getAttributeTypeDescription( musts[i] );
            atds[i] = atd;
        }
        return atds;
    }


    private void fetchMust( String ocName, Set attributeSet )
    {
        // add own must attributes
        ObjectClassDescription ocd = this.getSchema().getObjectClassDescription( ocName );
        attributeSet.addAll( Arrays.asList( ocd.getMustAttributeTypeDescriptionNames() ) );

        // add must attributes of super object classes
        if ( ocd.getSuperiorObjectClassDescriptionNames() != null )
        {
            for ( int k = 0; k < ocd.getSuperiorObjectClassDescriptionNames().length; k++ )
            {
                fetchMust( ocd.getSuperiorObjectClassDescriptionNames()[k], attributeSet );
            }
        }
    }


    /**
     * Returns the may attribute names of this and all super object classes.
     * 
     * @return
     */
    public String[] getMayAttributeNames()
    {
        Set mayAttrSet = new TreeSet();
        for ( int i = 0; i < this.objectClassNames.length; i++ )
        {
            this.fetchMay( this.objectClassNames[i], mayAttrSet );
        }
        return ( String[] ) mayAttrSet.toArray( new String[0] );
    }


    /**
     * Returns the may attribute types of this and all super object classes.
     * 
     * @return
     */
    public AttributeTypeDescription[] getMayAttributeTypeDescriptions()
    {
        String[] mays = getMayAttributeNames();
        AttributeTypeDescription[] atds = new AttributeTypeDescription[mays.length];
        for ( int i = 0; i < mays.length; i++ )
        {
            AttributeTypeDescription atd = getSchema().getAttributeTypeDescription( mays[i] );
            atds[i] = atd;
        }
        return atds;
    }


    private void fetchMay( String ocName, Set attributeSet )
    {
        // add own may attributes
        ObjectClassDescription ocd = this.getSchema().getObjectClassDescription( ocName );
        attributeSet.addAll( Arrays.asList( ocd.getMayAttributeTypeDescriptionNames() ) );

        // add may attributes of super object classes
        if ( ocd.getSuperiorObjectClassDescriptionNames() != null )
        {
            for ( int k = 0; k < ocd.getSuperiorObjectClassDescriptionNames().length; k++ )
            {
                fetchMay( ocd.getSuperiorObjectClassDescriptionNames()[k], attributeSet );
            }
        }
    }


    /**
     * Returns the must and may attribute names of this and all super object
     * classes.
     * 
     * @return
     */
    public String[] getAllAttributeNames()
    {
        return ( String[] ) getAllAttributeNameSet().toArray( new String[0] );
    }


    public Set getAllAttributeNameSet()
    {
        if ( this.allAttributeNameSet == null )
        {
            this.allAttributeNameSet = new TreeSet();
            this.allAttributeNameSet.addAll( Arrays.asList( this.getMustAttributeNames() ) );
            this.allAttributeNameSet.addAll( Arrays.asList( this.getMayAttributeNames() ) );
        }

        return this.allAttributeNameSet;
    }


    private Schema getSchema()
    {
        return schema;
        // return
        // BrowserCorePlugin.getDefault().getConnectionManager().getConnection(this.connectionName).getSchema();
    }

}
