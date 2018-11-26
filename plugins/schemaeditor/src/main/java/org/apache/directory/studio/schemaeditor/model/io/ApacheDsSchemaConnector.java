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
package org.apache.directory.studio.schemaeditor.model.io;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.naming.directory.SearchControls;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.constants.MetaSchemaConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableMatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResult;
import org.apache.directory.studio.connection.core.io.api.StudioSearchResultEnumeration;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * A Schema Connector for ApacheDS.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDsSchemaConnector extends AbstractSchemaConnector implements SchemaConnector
{
    /**
     * This enum represents the different types of nodes that can be found while
     * reading the schema from the DIT.
     */
    private enum SchemaNodeTypes
    {
        ATTRIBUTE_TYPE, OBJECT_CLASS, MATCHING_RULE, SYNTAX, UNKNOWN
    }


    /**
     * {@inheritDoc}
     */
    public void importSchema( Project project, StudioProgressMonitor monitor )
        throws SchemaConnectorException
    {
        monitor.beginTask( Messages.getString( "ApacheDsSchemaConnector.FetchingSchema" ), 1 ); //$NON-NLS-1$
        List<Schema> schemas = new ArrayList<Schema>();
        project.setInitialSchema( schemas );
        ConnectionWrapper wrapper = project.getConnection().getConnectionWrapper();

        // Looking for all the defined schemas
        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.ONELEVEL_SCOPE );

        StudioSearchResultEnumeration answer = wrapper
            .search( SchemaConstants.OU_SCHEMA, "(objectclass=metaSchema)", constraintSearch, DEREF_ALIAS_METHOD, //$NON-NLS-1$ //$NON-NLS-2$
                HANDLE_REFERALS_METHOD, null, monitor, null );
        
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    StudioSearchResult searchResult = answer.next();

                    // Getting the 'cn' Attribute
                    Attribute cnAttribute = searchResult.getEntry()
                        .get( SchemaConstants.CN_AT );

                    // Looping on the values
                    if ( cnAttribute != null )
                    {
                        for ( Value cnValue : cnAttribute )
                        {
                            Schema schema = getSchema( wrapper, cnValue.getValue(), monitor );
                            schema.setProject( project );
                            schemas.add( schema );
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                throw new SchemaConnectorException( e );
            }
        }

        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSuitableConnector( Connection connection, StudioProgressMonitor monitor )
    {
        ConnectionWrapper wrapper = connection.getConnectionWrapper();

        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.OBJECT_SCOPE );
        constraintSearch.setReturningAttributes( new String[]
            { SchemaConstants.ALL_OPERATIONAL_ATTRIBUTES } );

        StudioSearchResultEnumeration answer = wrapper.search( "", LdapConstants.OBJECT_CLASS_STAR, constraintSearch, //$NON-NLS-1$ //$NON-NLS-2$
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );

        if ( answer != null )
        {
            try
            {
                if ( answer.hasMore() )
                {
                    Entry entry = answer.next().getEntry();

                    Attribute vendorNameAttribute = entry.get( SchemaConstants.VENDOR_NAME_AT );
                    
                    if ( vendorNameAttribute == null )
                    {
                        return false;
                    }

                    if ( vendorNameAttribute.size() != 1 )
                    {
                        return false;
                    }

                    String vendorName = null;
                    try
                    {
                        vendorName = vendorNameAttribute.getString();
                    }
                    catch ( LdapInvalidAttributeValueException e )
                    {
                        return false;
                    }

                    return ( ( vendorName != null ) && vendorName.equalsIgnoreCase( "Apache Software Foundation" ) ); //$NON-NLS-1$
                }
            }
            catch ( LdapException e )
            {
                monitor.reportError( e );
            }
        }

        return false;
    }


    private static Schema getSchema( ConnectionWrapper wrapper, String name, StudioProgressMonitor monitor )
        throws LdapException
    {
        monitor.subTask( name );

        // Creating the schema
        Schema schema = new Schema( name );

        // Looking for the nodes of the schema
        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.SUBTREE_SCOPE );

        StudioSearchResultEnumeration answer = wrapper.search( "cn=" + name + ", ou=schema", LdapConstants.OBJECT_CLASS_STAR, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            constraintSearch, DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );
        
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    Entry entry = answer.next().getEntry();
                    
                    switch ( getNodeType( entry ) )
                    {
                        case ATTRIBUTE_TYPE:
                            AttributeType at = createAttributeType( entry );
                            at.setSchemaName( name );
                            schema.addAttributeType( at );
                            break;
                            
                        case OBJECT_CLASS:
                            MutableObjectClass oc = createObjectClass( entry );
                            oc.setSchemaName( name );
                            schema.addObjectClass( oc );
                            break;
                            
                        case MATCHING_RULE:
                            MatchingRule mr = createMatchingRule( entry );
                            mr.setSchemaName( name );
                            schema.addMatchingRule( mr );
                            break;
                            
                        case SYNTAX:
                            LdapSyntax syntax = createSyntax( entry );
                            syntax.setSchemaName( name );
                            schema.addSyntax( syntax );
                            break;
                            
                        default:
                            break;
                    }
                }
            }
            catch ( LdapInvalidAttributeValueException e )
            {
                monitor.reportError( e );
            }
        }

        return schema;
    }


    /**
     * Gets the Type of node of the given SearchResult.
     * 
     * @param entry the SearchResult to be identified
     * @return the Type of node
     */
    private static SchemaNodeTypes getNodeType( Entry entry )
    {
        if ( entry.hasObjectClass( SchemaConstants.META_ATTRIBUTE_TYPE_OC ) )
        {
            return SchemaNodeTypes.ATTRIBUTE_TYPE;
        }
        else if ( entry.hasObjectClass( SchemaConstants.META_OBJECT_CLASS_OC ) )
        {
            return SchemaNodeTypes.OBJECT_CLASS;
        }
        else if ( entry.hasObjectClass( SchemaConstants.META_MATCHING_RULE_OC ) )
        {
            return SchemaNodeTypes.MATCHING_RULE;
        }
        else if ( entry.hasObjectClass( SchemaConstants.META_SYNTAX_OC ) )
        {
            return SchemaNodeTypes.SYNTAX;
        }
        else
        {
            return SchemaNodeTypes.UNKNOWN;
        }
    }


    /**
     * Create the AttributeTypeImpl associated with the given SearchResult.
     * 
     * @param entry the search result entry
     * @return the AttributeTypeImpl associated with the SearchResult, or null if no
     * AttributeTypeImpl could be created
     * @throws LdapInvalidAttributeValueException
     */
    private static AttributeType createAttributeType( Entry entry ) throws LdapInvalidAttributeValueException
    {
        MutableAttributeType at = new MutableAttributeType( getStringValue( entry, MetaSchemaConstants.M_OID_AT ) );
        at.setNames( getStringValues( entry, MetaSchemaConstants.M_NAME_AT ) );
        at.setDescription( getStringValue( entry, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        at.setObsolete( getBooleanValue( entry, MetaSchemaConstants.M_OBSOLETE_AT ) );
        at.setSuperiorOid( getStringValue( entry, MetaSchemaConstants.M_SUP_ATTRIBUTE_TYPE_AT ) );
        at.setUsage( getUsage( entry ) );
        at.setSyntaxOid( getStringValue( entry, MetaSchemaConstants.M_SYNTAX_AT ) );
        at.setSyntaxLength( getSyntaxLength( entry ) );
        at.setCollective( getBooleanValue( entry, MetaSchemaConstants.M_COLLECTIVE_AT ) );
        at.setSingleValued( getBooleanValue( entry, MetaSchemaConstants.M_SINGLE_VALUE_AT ) );
        at.setUserModifiable( getBooleanValue( entry, MetaSchemaConstants.M_NO_USER_MODIFICATION_AT ) );
        at.setEqualityOid( getStringValue( entry, MetaSchemaConstants.M_EQUALITY_AT ) );
        at.setOrderingOid( getStringValue( entry, MetaSchemaConstants.M_ORDERING_AT ) );
        at.setSubstringOid( getStringValue( entry, MetaSchemaConstants.M_SUBSTR_AT ) );
        
        return at;
    }


    /**
     * Create the ObjectClassImpl associated with the given SearchResult.
     * 
     * @param sr the SearchResult
     * @return the ObjectClassImpl associated with the SearchResult, or null if no
     * ObjectClassImpl could be created
     * @throws LdapInvalidAttributeValueException
     */
    private static MutableObjectClass createObjectClass( Entry sr ) throws LdapInvalidAttributeValueException
    {
        MutableObjectClass oc = new MutableObjectClass( getStringValue( sr, MetaSchemaConstants.M_OID_AT ) );
        oc.setNames( getStringValues( sr, MetaSchemaConstants.M_NAME_AT ) );
        oc.setDescription( getStringValue( sr, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        oc.setObsolete( getBooleanValue( sr, MetaSchemaConstants.M_OBSOLETE_AT ) );
        oc.setSuperiorOids( getStringValues( sr, MetaSchemaConstants.M_SUP_OBJECT_CLASS_AT ) );
        oc.setType( getType( sr ) );
        oc.setMayAttributeTypeOids( getStringValues( sr, MetaSchemaConstants.M_MAY_AT ) );
        oc.setMustAttributeTypeOids( getStringValues( sr, MetaSchemaConstants.M_MUST_AT ) );
        
        return oc;
    }


    /**
     * Create the MatchingRule associated with the given SearchResult.
     * 
     * @param entry the SearchResult
     * @return the MatchingRule associated with the SearchResult, or null if no
     * ObjectClass could be created
     * @throws LdapInvalidAttributeValueException 
     */
    private static MatchingRule createMatchingRule( Entry entry ) throws LdapInvalidAttributeValueException
    {
        MutableMatchingRule mr = new MutableMatchingRule( getStringValue( entry, MetaSchemaConstants.M_OID_AT ) );
        mr.setNames( getStringValues( entry, MetaSchemaConstants.M_NAME_AT ) );
        mr.setDescription( getStringValue( entry, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        mr.setObsolete( getBooleanValue( entry, MetaSchemaConstants.M_OBSOLETE_AT ) );
        mr.setSyntaxOid( getStringValue( entry, MetaSchemaConstants.M_SYNTAX_AT ) );
        
        return mr;
    }


    /**
     * Create the MatchingRule associated with the given SearchResult.
     * 
     * @param entry the SearchResult
     * @return the MatchingRule associated with the SearchResult, or null if no
     * ObjectClass could be created
     * @throws LdapInvalidAttributeValueException
     */
    private static LdapSyntax createSyntax( Entry entry ) throws LdapInvalidAttributeValueException
    {
        LdapSyntax syntax = new LdapSyntax( getStringValue( entry, MetaSchemaConstants.M_OID_AT ) );
        syntax.setNames( getStringValues( entry, MetaSchemaConstants.M_NAME_AT ) );
        syntax.setDescription( getStringValue( entry, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        syntax.setObsolete( getBooleanValue( entry, MetaSchemaConstants.M_OBSOLETE_AT ) );
        syntax.setHumanReadable( isHumanReadable( entry ) );
        
        return syntax;
    }


    /**
     * Gets the usage of the attribute type contained a SearchResult.
     *
     * @param sr the SearchResult
     * @return the usage of the attribute type
     */
    private static UsageEnum getUsage( Entry entry ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( MetaSchemaConstants.M_USAGE_AT );

        if ( at == null )
        {
            return UsageEnum.USER_APPLICATIONS;
        }
        else
        {
            try
            {
                return UsageEnum.getUsage( at.getString() );
            }
            catch ( IllegalArgumentException e )
            {
                return UsageEnum.USER_APPLICATIONS;
            }
            catch ( NullPointerException e )
            {
                return UsageEnum.USER_APPLICATIONS;
            }
        }
    }


    /**
     * Gets the syntax length of the attribute type contained a SearchResult.
     *
     * @param entry the SearchResult
     * @return the syntax length of the attribute type, or -1 if no syntax length was found
     * @throws LdapInvalidAttributeValueException if an error occurs when searching in the SearchResult
     */
    private static int getSyntaxLength( Entry entry ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( MetaSchemaConstants.M_LENGTH_AT );

        if ( at == null )
        {
            return -1;
        }
        else
        {
            try
            {
                return Integer.parseInt( at.getString() );
            }
            catch ( NumberFormatException e )
            {
                return -1;
            }
        }
    }


    /**
     * Gets the String value of a Schema element of an attribute type contained in a SearchResult.
     *
     * @param sr the SearchResult
     * @param schemaElement The Schema Element we are looking for
     * @return The String value if found
     * @throws LdapInvalidAttributeValueException 
     */
    private static String getStringValue( Entry entry, String schemaElement ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( schemaElement );

        if ( at == null )
        {
            return null;
        }
        else
        {
            return at.getString();
        }
    }


    /**
     * Gets the Boolean value for a Schema element of an attribute type contained in a SearchResult.
     *
     * @param entry the SearchResult
     * @param schemaElement The Schema Element we are looking for
     * @return The boolean value if found
     * @throws LdapInvalidAttributeValueException 
     */
    private static boolean getBooleanValue( Entry entry, String schemaElement ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( schemaElement );

        if ( at == null )
        {
            return false;
        }
        else
        {
            return Boolean.parseBoolean( at.getString() );
        }
    }


    /**
     * Gets the list of values for a schema element of an object class contained in a SearchResult.
     *
     * @param entry the SearchResult
     * @param schemaElement The Schema Element we are looking for
     * @return the optional attribute types of the attribute type, or an empty array if no optional attribute type was found
     */
    private static List<String> getStringValues( Entry entry, String schemaElement )
    {
        Attribute at = entry.get( schemaElement );
        return StreamSupport.stream( at.spliterator(), false ).map( Value::getValue ).collect( Collectors.toList() );
    }


    /**
     * Gets the type of the object class contained a SearchResult.
     *
     * @param entry the SearchResult
     * @return the type of the object class
     * @throws LdapInvalidAttributeValueException 
     */
    private static ObjectClassTypeEnum getType( Entry entry ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( MetaSchemaConstants.M_TYPE_OBJECT_CLASS_AT );

        if ( at == null )
        {
            return ObjectClassTypeEnum.STRUCTURAL;
        }
        else
        {
            try
            {
                return ObjectClassTypeEnum.getClassType( at.getString() );
            }
            catch ( IllegalArgumentException e )
            {
                return ObjectClassTypeEnum.STRUCTURAL;
            }
            catch ( NullPointerException e )
            {
                return ObjectClassTypeEnum.STRUCTURAL;
            }
        }
    }


    /**
     * Gets whether or not the schema object contained a SearchResult is obsolete.
     *
     * @param entry the SearchResult
     * @return true if the schema object is obsolete, false if not
     * @throws LdapInvalidAttributeValueException 
     */
    private static boolean isHumanReadable( Entry entry ) throws LdapInvalidAttributeValueException
    {
        Attribute at = entry.get( MetaSchemaConstants.X_NOT_HUMAN_READABLE_AT );

        if ( at == null )
        {
            return false;
        }
        else
        {
            return !Boolean.parseBoolean( at.getString() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void exportSchema( Project project, StudioProgressMonitor monitor )
        throws SchemaConnectorException
    {
        // TODO Auto-generated method stub
    }
}
