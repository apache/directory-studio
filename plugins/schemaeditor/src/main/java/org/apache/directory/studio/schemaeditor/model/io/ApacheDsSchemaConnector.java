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

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SchemaImpl;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;


/**
 * A Schema Connector for Apache DS.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDsSchemaConnector extends AbstractSchemaConnector implements SchemaConnector
{

    /** The Schema DN */
    public static final String SCHEMA_DN = "ou=schema";

    /** The name of the metaAttributeType object class */
    private static final String META_ATTRIBUTE_TYPE = "metaAttributeType";
    /** The name of the metaObjectClass object class */
    private static final String META_OBJECT_CLASS = "metaObjectClass";
    /** The name of the metaMatchingRule object class */
    private static final String META_MATCHING_RULE = "metaMatchingRule";
    /** The name of the metaSyntax object class */
    private static final String META_SYNTAX = "metaSyntax";

    private static final AliasDereferencingMethod DEREF_ALIAS_METHOD = AliasDereferencingMethod.ALWAYS;
    private static final ReferralHandlingMethod HANDLE_REFERALS_METHOD = ReferralHandlingMethod.FOLLOW;

    /**
     * This enum represents the different types of nodes that can be found while
     * reading the schema from the DIT.
     */
    private enum SchemaNodeTypes
    {
        ATTRIBUTE_TYPE, OBJECT_CLASS, MATCHING_RULE, SYNTAX, UNKNOWN
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#exportSchema(org.apache.directory.studio.connection.core.Connection, org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public void exportSchema( Connection connection, StudioProgressMonitor monitor )
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.SchemaConnector#importSchema(org.apache.directory.studio.connection.core.Connection, org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public List<Schema> importSchema( Connection connection, StudioProgressMonitor monitor )
    {
        List<Schema> schemas = new ArrayList<Schema>();

        JNDIConnectionWrapper wrapper = connection.getJNDIConnectionWrapper();

        monitor.beginTask( Messages.getString( "GenericSchemaConnector.FetchingSchema" ), 1 ); //$NON-NLS-1$

        // Looking for all the defined schemas
        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.ONELEVEL_SCOPE );

        NamingEnumeration<SearchResult> answer = wrapper
            .search( "ou=schema", "(objectclass=metaSchema)", constraintSearch, DEREF_ALIAS_METHOD,
                HANDLE_REFERALS_METHOD, null, ( StudioProgressMonitor ) monitor, null );
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) answer.next();

                    // Getting the 'cn' Attribute
                    Attribute cnAttribute = searchResult.getAttributes().get( "cn" );

                    // Looping on the values
                    NamingEnumeration<?> ne = null;
                    ne = cnAttribute.getAll();
                    if ( ne != null )
                    {
                        while ( ne.hasMore() )
                        {
                            String value = ( String ) ne.next();
                            schemas.add( getSchema( wrapper, value, monitor ) );
                        }
                    }
                }
            }
            catch ( NamingException e )
            {
                monitor.reportError( e );
            }
        }

        monitor.worked( 1 );

        return schemas;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.io.AbstractSchemaConnector#isSuitableConnector(org.apache.directory.studio.connection.core.Connection, org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    public boolean isSuitableConnector( Connection connection, StudioProgressMonitor monitor )
    {
        JNDIConnectionWrapper wrapper = connection.getJNDIConnectionWrapper();

        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.OBJECT_SCOPE );
        constraintSearch.setReturningAttributes( new String[]
            { "+" } );

        NamingEnumeration<SearchResult> answer = wrapper.search( "", "(objectclass=*)", constraintSearch,
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );

        if ( answer != null )
        {
            try
            {
                if ( answer.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) answer.next();

                    Attribute vendorNameAttribute = searchResult.getAttributes().get( "vendorName" );
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

                    return ( ( vendorName != null ) && vendorName.equalsIgnoreCase( "Apache Software Foundation" ) );
                }
            }
            catch ( NamingException e )
            {
                monitor.reportError( e );
            }
        }

        return false;
    }


    private static Schema getSchema( JNDIConnectionWrapper wrapper, String name, StudioProgressMonitor monitor )
        throws NamingException
    {
        monitor.subTask( name ); //$NON-NLS-1$

        // Creating the schema
        Schema schema = new SchemaImpl( name );

        // Looking for the nodes of the schema
        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.SUBTREE_SCOPE );

        NamingEnumeration<SearchResult> answer = wrapper.search( "cn=" + name + ", ou=schema", "(objectclass=*)",
            constraintSearch, DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) answer.next();
                    switch ( getNodeType( searchResult ) )
                    {
                        case ATTRIBUTE_TYPE:
                            AttributeTypeImpl at = createAttributeType( searchResult );
                            at.setSchemaName( name );
                            at.setSchemaObject( schema );
                            schema.addAttributeType( at );
                            break;
                        case OBJECT_CLASS:
                            ObjectClassImpl oc = createObjectClass( searchResult );
                            oc.setSchemaName( name );
                            oc.setSchemaObject( schema );
                            schema.addObjectClass( oc );
                            break;
                        case MATCHING_RULE:
                            MatchingRuleImpl mr = createMatchingRule( searchResult );
                            mr.setSchemaName( name );
                            mr.setSchemaObject( schema );
                            schema.addMatchingRule( mr );
                            break;
                        case SYNTAX:
                            SyntaxImpl syntax = createSyntax( searchResult );
                            syntax.setSchemaName( name );
                            syntax.setSchemaObject( schema );
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
     * @param sr
     *      the SearchResult to be identified
     * @return
     *      the Type of node
     * @throws NamingException
     *      if an error occurrs when reading the SearchResult
     */
    private static SchemaNodeTypes getNodeType( SearchResult sr ) throws NamingException
    {
        // Getting the 'ObjectClass' Attribute
        Attribute objectClassAttribute = sr.getAttributes().get( "objectClass" );

        // Looping on the values
        NamingEnumeration<?> ne = objectClassAttribute.getAll();
        while ( ne.hasMore() )
        {
            String value = ( String ) ne.next();
            if ( META_ATTRIBUTE_TYPE.equals( value ) )
            {
                return SchemaNodeTypes.ATTRIBUTE_TYPE;
            }
            else if ( META_OBJECT_CLASS.equals( value ) )
            {
                return SchemaNodeTypes.OBJECT_CLASS;
            }
            else if ( META_MATCHING_RULE.equals( value ) )
            {
                return SchemaNodeTypes.MATCHING_RULE;
            }
            else if ( META_SYNTAX.equals( value ) )
            {
                return SchemaNodeTypes.SYNTAX;
            }
        }

        return SchemaNodeTypes.UNKNOWN;
    }


    /**
     * Create the AttributeTypeImpl associated with the given SearchResult.
     * 
     * @param sr
     *      the SearchResult
     * @return
     *      the AttributeTypeImpl associated with the SearchResult, or null if no 
     * AttributeTypeImpl could be created
     * @throws NamingException 
     */
    private static AttributeTypeImpl createAttributeType( SearchResult sr ) throws NamingException
    {
        AttributeTypeImpl at = new AttributeTypeImpl( getOid( sr ) );
        at.setNames( getNames( sr ) );
        at.setDescription( getDescription( sr ) );
        at.setObsolete( isObsolete( sr ) );
        at.setSuperiorOid( getSuperior( sr ) );
        at.setUsage( getUsage( sr ) );
        at.setSyntaxOid( getSyntax( sr ) );
        at.setSyntaxLength( getSyntaxLength( sr ) );
        at.setCollective( isCollective( sr ) );
        at.setSingleValued( isSingleValued( sr ) );
        at.setUserModifiable( isUserModifiable( sr ) );
        at.setEqualityOid( getEquality( sr ) );
        at.setOrderingOid( getOrdering( sr ) );
        at.setSubstringOid( getSubstring( sr ) );
        return at;
    }


    /**
     * Create the ObjectClassImpl associated with the given SearchResult.
     * 
     * @param sr
     *      the SearchResult
     * @return
     *      the ObjectClassImpl associated with the SearchResult, or null if no 
     * ObjectClassImpl could be created
     * @throws NamingException 
     */
    private static ObjectClassImpl createObjectClass( SearchResult sr ) throws NamingException
    {
        ObjectClassImpl oc = new ObjectClassImpl( getOid( sr ) );
        oc.setNames( getNames( sr ) );
        oc.setDescription( getDescription( sr ) );
        oc.setObsolete( isObsolete( sr ) );
        oc.setSuperiorOids( getSuperiors( sr ) );
        oc.setType( getType( sr ) );
        oc.setMayAttributeTypeOids( getMay( sr ) );
        oc.setMustAttributeTypeOids( getMust( sr ) );
        return oc;
    }


    /**
     * Create the MatchingRule associated with the given SearchResult.
     * 
     * @param sr
     *      the SearchResult
     * @return
     *      the MatchingRule associated with the SearchResult, or null if no 
     * ObjectClass could be created
     * @throws NamingException 
     */
    private static MatchingRuleImpl createMatchingRule( SearchResult sr ) throws NamingException
    {
        MatchingRuleImpl mr = new MatchingRuleImpl( getOid( sr ) );
        mr.setNames( getNames( sr ) );
        mr.setDescription( getDescription( sr ) );
        mr.setObsolete( isObsolete( sr ) );
        mr.setSyntaxOid( getSyntax( sr ) );
        return mr;
    }


    /**
     * Create the MatchingRule associated with the given SearchResult.
     * 
     * @param sr
     *      the SearchResult
     * @return
     *      the MatchingRule associated with the SearchResult, or null if no 
     * ObjectClass could be created
     * @throws NamingException 
     */
    private static SyntaxImpl createSyntax( SearchResult sr ) throws NamingException
    {
        SyntaxImpl syntax = new SyntaxImpl( getOid( sr ) );
        syntax.setNames( getNames( sr ) );
        syntax.setDescription( getDescription( sr ) );
        syntax.setObsolete( isObsolete( sr ) );
        syntax.setHumanReadable( isHumanReadable( sr ) );
        return syntax;
    }


    /**
     * Gets the oid of the schema object contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the oid of the schema object, or nullif no oid was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getOid( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-oid" );
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
     * Gets the names of the schema object contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the names of the schema object, or an empty array if no name was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String[] getNames( SearchResult sr ) throws NamingException
    {
        List<String> names = new ArrayList<String>();

        Attribute at = sr.getAttributes().get( "m-name" );
        if ( at != null )
        {
            NamingEnumeration<?> ne = at.getAll();
            while ( ne.hasMore() )
            {
                names.add( ( String ) ne.next() );
            }
        }

        return names.toArray( new String[0] );
    }


    /**
     * Gets the description of the schema object contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the description of the schema object, or null if no description was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getDescription( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-description" );

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
     * Gets the superior of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the superior of the attribute type, or null if no superior was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getSuperior( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-supAttributeType" );

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
     * Gets the usage of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the usage of the attribute type
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static UsageEnum getUsage( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-usage" );

        if ( at == null )
        {
            return UsageEnum.USER_APPLICATIONS;
        }
        else
        {
            try
            {
                return Enum.valueOf( UsageEnum.class, ( String ) at.get() );
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
     * Gets the syntax of the schema object contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the syntax of the schema object, or null if no syntax was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getSyntax( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-syntax" );

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
     * Gets the syntax length of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the syntax length of the attribute type, or -1 if no syntax length was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static int getSyntaxLength( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-length" );

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
     * Gets whether or not the schema object contained a SearchResult is obsolete.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      true if the schema object is obsolete, false if not
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static boolean isObsolete( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-obsolete" );

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
     * Gets whether or not the attribute type contained a SearchResult is collective.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      true if the attribute type is collective, false if not
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static boolean isCollective( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-collective" );

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
     * Gets whether or not the attribute type contained a SearchResult is single value.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      true if the attribute type is single value, false if not
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static boolean isSingleValued( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-singleValue" );

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
     * Gets whether or not the attribute type contained a SearchResult is single value.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      true if the attribute type is single value, false if not
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static boolean isUserModifiable( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-noUserModification" );

        if ( at == null )
        {
            return true;
        }
        else
        {
            return !Boolean.parseBoolean( ( String ) at.get() );
        }
    }


    /**
     * Gets the name of the equality matching rule of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the name of the equality matching rule of the attribute type, or null if no equality matching rule was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getEquality( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-equality" );

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
     * Gets the name of the ordering matching rule of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the name of the ordering matching rule of the attribute type, or null if no ordering matching rule was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getOrdering( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-ordering" );

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
     * Gets the name of the substr matching rule of the attribute type contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the name of the substr matching rule of the attribute type, or null if no substr matching rule was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static String getSubstring( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-substr" );

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
     * Gets the superiors of the object class contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the superiors of the attribute type, or an empty array if no superior was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static List<String> getSuperiors( SearchResult sr ) throws NamingException
    {
        List<String> names = new ArrayList<String>();

        Attribute at = sr.getAttributes().get( "m-supObjectClass" );
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
     * @param sr
     *      the SearchResult
     * @return
     *      the type of the object class
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static ObjectClassTypeEnum getType( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "m-typeObjectClass" );

        if ( at == null )
        {
            return ObjectClassTypeEnum.STRUCTURAL;
        }
        else
        {
            try
            {
                return Enum.valueOf( ObjectClassTypeEnum.class, ( String ) at.get() );
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
     * Gets the optional attribute types of the object class contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the optional attribute types of the attribute type, or an empty array if no optional attribute type was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static List<String> getMay( SearchResult sr ) throws NamingException
    {
        List<String> names = new ArrayList<String>();

        Attribute at = sr.getAttributes().get( "m-may" );
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
     * Gets the mandatory attribute types of the object class contained a SearchResult.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      the mandatory attribute types of the attribute type, or an empty array if no mandatory attribute type was found
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static List<String> getMust( SearchResult sr ) throws NamingException
    {
        List<String> names = new ArrayList<String>();

        Attribute at = sr.getAttributes().get( "m-must" );
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
     * Gets whether or not the schema object contained a SearchResult is obsolete.
     *
     * @param sr
     *      the SearchResult
     * @return
     *      true if the schema object is obsolete, false if not
     * @throws NamingException
     *      if an error occurrs when searching in the SearchResult
     */
    private static boolean isHumanReadable( SearchResult sr ) throws NamingException
    {
        Attribute at = sr.getAttributes().get( "x-humanReadable" );

        if ( at == null )
        {
            return false;
        }
        else
        {
            return Boolean.parseBoolean( ( String ) at.get() );
        }
    }
}
