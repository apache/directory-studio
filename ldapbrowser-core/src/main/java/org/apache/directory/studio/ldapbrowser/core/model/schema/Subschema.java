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

import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * A subschema represents the schema information for an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Subschema implements Serializable
{

    private static final long serialVersionUID = 7821844589084867562L;

    private String[] objectClassNames;

    private Schema schema;

    private Set<String> allAttributeNameSet;


    protected Subschema()
    {
    }


    /**
     * Creates a new instance of Subschema.
     * 
     * @param entry the entry
     */
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
        this.schema = entry.getBrowserConnection().getSchema();
    }


    /**
     * Creates a new instance of Subschema.
     * 
     * @param objectClassNames the object class names
     * @param connection the connection
     */
    public Subschema( String[] objectClassNames, IBrowserConnection connection )
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
     * Gets the must attribute names of this subschema.
     * 
     * @return the must attribute names of this subschema
     */
    public String[] getMustAttributeNames()
    {
        Set<String> mustAttributeNames = new TreeSet<String>();
        for ( String objectClassName : objectClassNames )
        {
            fetchMust( objectClassName, mustAttributeNames );
        }
        return ( String[] ) mustAttributeNames.toArray( new String[0] );
    }


    /**
     * Gets the must attribute types descriptions of this subschema.
     * 
     * @return the must attribute types descriptions of this subschema
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


    private void fetchMust( String ocName, Set<String> mustAttributeNames )
    {
        // add own must attributes
        ObjectClassDescription ocd = getSchema().getObjectClassDescription( ocName );
        mustAttributeNames.addAll( ocd.getMustAttributeTypes() );

        // add must attributes of super object classes
        if ( ocd.getSuperiorObjectClasses() != null )
        {
            for ( String superior : ocd.getSuperiorObjectClasses() )
            {
                fetchMust( superior, mustAttributeNames );
            }
        }
    }


    /**
     * Gets the may attribute names of this subschema.
     * 
     * @return the may attribute names of this subschema
     */
    public String[] getMayAttributeNames()
    {
        Set<String> mayAttrSet = new TreeSet<String>();
        for ( String objectClassName : objectClassNames )
        {
            fetchMay( objectClassName, mayAttrSet );
        }
        return ( String[] ) mayAttrSet.toArray( new String[0] );
    }


    /**
     * Gets the may attribute types descriptions of this subschema.
     * 
     * @return the may attribute types descriptions of this subschema
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


    private void fetchMay( String ocName, Set<String> mustAttributeNames )
    {
        // add own may attributes
        ObjectClassDescription ocd = this.getSchema().getObjectClassDescription( ocName );
        mustAttributeNames.addAll( ocd.getMayAttributeTypes() );

        // add may attributes of super object classes
        if ( ocd.getSuperiorObjectClasses() != null )
        {
            for ( String superior : ocd.getSuperiorObjectClasses() )
            {
                fetchMust( superior, mustAttributeNames );
            }
        }
    }


    /**
     * Gets the must and may attribute names of this subschema.
     * 
     * @return the must and may attribute names of this subschema
     */
    public String[] getAllAttributeNames()
    {
        return ( String[] ) getAllAttributeNameSet().toArray( new String[0] );
    }


    public Set<String> getAllAttributeNameSet()
    {
        if ( allAttributeNameSet == null )
        {
            allAttributeNameSet = new TreeSet<String>();
            allAttributeNameSet.addAll( Arrays.asList( this.getMustAttributeNames() ) );
            allAttributeNameSet.addAll( Arrays.asList( this.getMayAttributeNames() ) );
        }

        return allAttributeNameSet;
    }


    /**
     * Gets the schema.
     * 
     * @return the schema
     */
    private Schema getSchema()
    {
        return schema;
    }

}
