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
package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.studio.openldap.common.ui.model.DbIndexTypeEnum;


/**
 * This class represents an index value.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcDbIndex
{
    /** The "default" special flag tag */
    private static final String DEFAULT_FLAG = "default";

    /** The space ' ' separator */
    private static final String SPACE_SEPARATOR = " ";

    /** The comma ',' separator */
    private static final String COMMA_SEPARATOR = ",";

    /** The default flag */
    private boolean isDefault = false;

    /** The list of attributes */
    private List<String> attributes = new ArrayList<String>();

    /** The list of index types */
    private Set<DbIndexTypeEnum> indexTypes = new HashSet<DbIndexTypeEnum>();


    /**
     * Creates a new instance of OlcDbIndex.
     */
    public OlcDbIndex()
    {
    }


    /**
     * Creates a new instance of OlcDbIndex.
     *
     * @param s the string
     */
    public OlcDbIndex( String s )
    {
        if ( s != null )
        {
            String[] components = s.split( SPACE_SEPARATOR );

            if ( components.length > 0 )
            {
                String[] attributes = components[0].split( COMMA_SEPARATOR );

                if ( attributes.length > 0 )
                {
                    for ( String attribute : attributes )
                    {
                        addAttribute( attribute );
                    }
                }

                if ( components.length == 2 )
                {
                    String[] indexTypes = components[1].split( COMMA_SEPARATOR );

                    if ( indexTypes.length > 0 )
                    {
                        for ( String indexType : indexTypes )
                        {
                            DbIndexTypeEnum type = DbIndexTypeEnum.valueOf( indexType );

                            if ( type != null )
                            {
                                addIndexType( type );
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Adds an attribute.
     *
     * @param attribute the attribute
     */
    public void addAttribute( String attribute )
    {
        if ( DEFAULT_FLAG.equalsIgnoreCase( attribute ) )
        {
            setDefault( true );
        }
        else
        {
            attributes.add( attribute );
        }
    }


    /**
     * Gets the default flag.
     *
     * @return the default flag
     */
    public boolean isDefault()
    {
        return isDefault;
    }


    /**
     * Sets the default flag.
     *
     * @param isDefault the default flag
     */
    public void setDefault( boolean isDefault )
    {
        this.isDefault = isDefault;
    }


    /**
     * Adds an index type.
     *
     * @param indexType the index type
     */
    public void addIndexType( DbIndexTypeEnum indexType )
    {
        indexTypes.add( indexType );
    }


    /**
     * Clears the attributes.
     */
    public void clearAttributes()
    {
        attributes.clear();
    }


    /**
     * Clears the index types.
     */
    public void clearIndexTypes()
    {
        indexTypes.clear();
    }


    /**
     * Removes an attribute.
     *
     * @param attribute the attribute
     */
    public void removeAttribute( String attribute )
    {
        attributes.remove( attribute );
    }


    /**
     * Removes an index type.
     *
     * @param indexType the index type
     */
    public void removeIndexType( DbIndexTypeEnum indexType )
    {
        indexTypes.remove( indexType );
    }


    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    public List<String> getAttributes()
    {
        return attributes;
    }


    /**
     * Gets the index types.
     *
     * @return the index types
     */
    public Set<DbIndexTypeEnum> getIndexTypes()
    {
        return indexTypes;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        if ( isDefault )
        {
            sb.append( DEFAULT_FLAG );
        }
        else
        {
            if ( attributes.size() > 0 )
            {
                for ( String attribute : attributes )
                {
                    if ( isFirst )
                    {
                        isFirst = false;
                    }
                    else
                    {
                        sb.append( COMMA_SEPARATOR );
                    }
                    
                    sb.append( attribute );
                }
            }
        }

        if ( indexTypes.size() > 0 )
        {
            sb.append( SPACE_SEPARATOR );
            isFirst = true;

            for ( DbIndexTypeEnum indexType : indexTypes )
            {
                if ( isFirst )
                {
                    isFirst = false;
                }
                else
                {
                    sb.append( COMMA_SEPARATOR );
                }
                
                sb.append( indexType.toString() );
            }
        }

        return sb.toString();
    }
}
