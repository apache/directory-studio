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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.constants.MetaSchemaConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
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

        NamingEnumeration<SearchResult> answer = wrapper
            .search( SchemaConstants.OU_SCHEMA, "(objectclass=metaSchema)", constraintSearch, DEREF_ALIAS_METHOD, //$NON-NLS-1$ //$NON-NLS-2$
                HANDLE_REFERALS_METHOD, null, monitor, null );
        
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    SearchResult searchResult = answer.next();

                    // Getting the 'cn' Attribute
                    Attribute cnAttribute = searchResult.getAttributes().get( SchemaConstants.CN_AT );

                    // Looping on the values
                    NamingEnumeration<?> ne = cnAttribute.getAll();
                    
                    if ( ne != null )
                    {
                        while ( ne.hasMore() )
                        {
                            String cnValue = (String)ne.next();
                            Schema schema = getSchema( wrapper, cnValue, monitor );
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

        NamingEnumeration<SearchResult> answer = wrapper.search( "", LdapConstants.OBJECT_CLASS_STAR, constraintSearch, //$NON-NLS-1$ //$NON-NLS-2$
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );

        if ( answer != null )
        {
            try
            {
                if ( answer.hasMore() )
                {
                    SearchResult searchResult = answer.next();

                    Attribute vendorNameAttribute = searchResult.getAttributes().get( SchemaConstants.VENDOR_NAME_AT );
                    
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
                        vendorName = ( String ) vendorNameAttribute.get();
                    }
                    catch ( NamingException e )
                    {
                        return false;
                    }

                    return ( ( vendorName != null ) && vendorName.equalsIgnoreCase( "Apache Software Foundation" ) ); //$NON-NLS-1$
                }
            }
            catch ( NamingException e )
            {
                monitor.reportError( e );
            }
        }

        return false;
    }


    private static Schema getSchema( ConnectionWrapper wrapper, String name, StudioProgressMonitor monitor )
        throws NamingException
    {
        monitor.subTask( name );

        // Creating the schema
        Schema schema = new Schema( name );

        // Looking for the nodes of the schema
        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.SUBTREE_SCOPE );

        NamingEnumeration<SearchResult> answer = wrapper.search( "cn=" + name + ", ou=schema", LdapConstants.OBJECT_CLASS_STAR, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            constraintSearch, DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );
        
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    SearchResult searchResult = answer.next();
                    
                    switch ( getNodeType( searchResult ) )
                    {
                        case ATTRIBUTE_TYPE:
                            AttributeType at = createAttributeType( searchResult );
                            at.setSchemaName( name );
                            schema.addAttributeType( at );
                            break;
                            
                        case OBJECT_CLASS:
                            MutableObjectClass oc = createObjectClass( searchResult );
                            oc.setSchemaName( name );
                            schema.addObjectClass( oc );
                            break;
                            
                        case MATCHING_RULE:
                            MatchingRule mr = createMatchingRule( searchResult );
                            mr.setSchemaName( name );
                            schema.addMatchingRule( mr );
                            break;
                            
                        case SYNTAX:
                            LdapSyntax syntax = createSyntax( searchResult );
                            syntax.setSchemaName( name );
                            schema.addSyntax( syntax );
                            break;
                            
                        default:
                            break;
                    }
                }
            }
            catch ( NamingException e )
            {
                monitor.reportError( e );
            }
        }

        return schema;
    }


    /**
     * Gets the Type of node of the given SearchResult.
     * 
     * @param sr the SearchResult to be identified
     * @return the Type of node
     * @throws NamingException if an error occurrs when reading the SearchResult
     */
    private static SchemaNodeTypes getNodeType( SearchResult sr ) throws NamingException
    {
        // Getting the 'ObjectClass' Attribute
        Attribute objectClassAttribute = sr.getAttributes().get( SchemaConstants.OBJECT_CLASS_AT );

        // Looping on the values
        NamingEnumeration<?> ne = objectClassAttribute.getAll();
        
        while ( ne.hasMore() )
        {
            String value = ( String ) ne.next();
            
            if ( SchemaConstants.META_ATTRIBUTE_TYPE_OC.equalsIgnoreCase( value ) )
            {
                return SchemaNodeTypes.ATTRIBUTE_TYPE;
            }
            else if ( SchemaConstants.META_OBJECT_CLASS_OC.equalsIgnoreCase( value ) )
            {
                return SchemaNodeTypes.OBJECT_CLASS;
            }
            else if ( SchemaConstants.META_MATCHING_RULE_OC.equalsIgnoreCase( value ) )
            {
                return SchemaNodeTypes.MATCHING_RULE;
            }
            else if ( SchemaConstants.META_SYNTAX_OC.equalsIgnoreCase( value ) )
            {
                return SchemaNodeTypes.SYNTAX;
            }
        }

        return SchemaNodeTypes.UNKNOWN;
    }


    /**
     * Create the AttributeTypeImpl associated with the given SearchResult.
     * 
     * @param sr the SearchResult
     * @return the AttributeTypeImpl associated with the SearchResult, or null if no
     * AttributeTypeImpl could be created
     * @throws NamingException
     */
    private static AttributeType createAttributeType( SearchResult sr ) throws NamingException
    {
        MutableAttributeType at = new MutableAttributeType( getStringValue( sr, MetaSchemaConstants.M_OID_AT ) );
        at.setNames( getStringValues( sr, MetaSchemaConstants.M_NAME_AT ) );
        at.setDescription( getStringValue( sr, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        at.setObsolete( getBooleanValue( sr, MetaSchemaConstants.M_OBSOLETE_AT ) );
        at.setSuperiorOid( getStringValue( sr, MetaSchemaConstants.M_SUP_ATTRIBUTE_TYPE_AT ) );
        at.setUsage( getUsage( sr ) );
        at.setSyntaxOid( getStringValue( sr, MetaSchemaConstants.M_SYNTAX_AT ) );
        at.setSyntaxLength( getSyntaxLength( sr ) );
        at.setCollective( getBooleanValue( sr, MetaSchemaConstants.M_COLLECTIVE_AT ) );
        at.setSingleValued( getBooleanValue( sr, MetaSchemaConstants.M_SINGLE_VALUE_AT ) );
        at.setUserModifiable( getBooleanValue( sr, MetaSchemaConstants.M_NO_USER_MODIFICATION_AT ) );
        at.setEqualityOid( getStringValue( sr, MetaSchemaConstants.M_EQUALITY_AT ) );
        at.setOrderingOid( getStringValue( sr, MetaSchemaConstants.M_ORDERING_AT ) );
        at.setSubstringOid( getStringValue( sr, MetaSchemaConstants.M_SUBSTR_AT ) );
        
        return at;
    }


    /**
     * Create the ObjectClassImpl associated with the given SearchResult.
     * 
     * @param sr the SearchResult
     * @return the ObjectClassImpl associated with the SearchResult, or null if no
     * ObjectClassImpl could be created
     * @throws NamingException
     */
    private static MutableObjectClass createObjectClass( SearchResult sr ) throws NamingException
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
     * @param sr the SearchResult
     * @return the MatchingRule associated with the SearchResult, or null if no
     * ObjectClass could be created
     * @throws NamingException
     */
    private static MatchingRule createMatchingRule( SearchResult sr ) throws NamingException
    {
        MutableMatchingRule mr = new MutableMatchingRule( getStringValue( sr, MetaSchemaConstants.M_OID_AT ) );
        mr.setNames( getStringValues( sr, MetaSchemaConstants.M_NAME_AT ) );
        mr.setDescription( getStringValue( sr, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        mr.setObsolete( getBooleanValue( sr, MetaSchemaConstants.M_OBSOLETE_AT ) );
        mr.setSyntaxOid( getStringValue( sr, MetaSchemaConstants.M_SYNTAX_AT ) );
        
        return mr;
    }


    /**
     * Create the MatchingRule associated with the given SearchResult.
     * 
     * @param sr the SearchResult
     * @return the MatchingRule associated with the SearchResult, or null if no
     * ObjectClass could be created
     * @throws NamingException
     */
    private static LdapSyntax createSyntax( SearchResult sr ) throws NamingException
    {
        LdapSyntax syntax = new LdapSyntax( getStringValue( sr, MetaSchemaConstants.M_OID_AT ) );
        syntax.setNames( getStringValues( sr, MetaSchemaConstants.M_NAME_AT ) );
        syntax.setDescription( getStringValue( sr, MetaSchemaConstants.M_DESCRIPTION_AT ) );
        syntax.setObsolete( getBooleanValue( sr, MetaSchemaConstants.M_OBSOLETE_AT ) );
        syntax.setHumanReadable( isHumanReadable( sr ) );
        
        return syntax;
    }


    /**
     * Gets the usage of the attribute type contained a SearchResult.
     *
     * @param sr the SearchResult
     * @return the usage of the attribute type
     * @throws NamingException if an error occurs when searching in the SearchResult
     */
    private static UsageEnum getUsage( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( MetaSchemaConstants.M_USAGE_AT );

        if ( at == null )
        {
            return UsageEnum.USER_APPLICATIONS;
        }
        else
        {
            try
            {
                return UsageEnum.getUsage( ( String ) at.get() );
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
     * @param sr the SearchResult
     * @return the syntax length of the attribute type, or -1 if no syntax length was found
     * @throws NamingException if an error occurs when searching in the SearchResult
     */
    private static int getSyntaxLength( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( MetaSchemaConstants.M_LENGTH_AT );

        if ( at == null )
        {
            return -1;
        }
        else
        {
            try
            {
                return Integer.parseInt( ( String ) at.get() );
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
     * @throws NamingException if an error occurs when searching in the SearchResult
     */
    private static String getStringValue( SearchResult sr, String schemaElement ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( schemaElement );

        if ( at == null )
        {
            return null;
        }
        else
        {
            return ( String ) at.get();
        }
    }


    /**
     * Gets the Boolean value for a Schema element of an attribute type contained in a SearchResult.
     *
     * @param sr the SearchResult
     * @param schemaElement The Schema Element we are looking for
     * @return The boolean value if found
     * @throws NamingException if an error occurs when searching in the SearchResult
     */
    private static boolean getBooleanValue( SearchResult sr, String schemaElement ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( schemaElement );

        if ( at == null )
        {
            return false;
        }
        else
        {
            return Boolean.parseBoolean( ( String ) at.get() );
        }
    }


    /**
     * Gets the list of values for a schema element of an object class contained in a SearchResult.
     *
     * @param sr the SearchResult
     * @param schemaElement The Schema Element we are looking for
     * @return the optional attribute types of the attribute type, or an empty array if no optional attribute type was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static List<String> getStringValues( SearchResult sr, String schemaElement ) throws NamingException
    {
        List<String> names = new ArrayList<String>();

        Attribute at = sr.getAttributes().get( schemaElement );
        
        if ( at != null )
        {
            NamingEnumeration<?> ne = at.getAll();
            
            while ( ne.hasMore() )
            {
                names.add( ( String ) ne.next() );
            }
        }

        return names;
    }


    /**
     * Gets the type of the object class contained a SearchResult.
     *
     * @param sr the SearchResult
     * @return the type of the object class
     * @throws NamingException if an error occurrs when searching in the SearchResult
     */
    private static ObjectClassTypeEnum getType( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( MetaSchemaConstants.M_TYPE_OBJECT_CLASS_AT );

        if ( at == null )
        {
            return ObjectClassTypeEnum.STRUCTURAL;
        }
        else
        {
            try
            {
                return ObjectClassTypeEnum.getClassType( ( String ) at.get() );
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
     * @param sr the SearchResult
     * @return true if the schema object is obsolete, false if not
     * @throws NamingException if an error occurs when searching in the SearchResult
     */
    private static boolean isHumanReadable( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( MetaSchemaConstants.X_NOT_HUMAN_READABLE_AT );

        if ( at == null )
        {
            return false;
        }
        else
        {
            return !Boolean.parseBoolean( ( String ) at.get() );
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
