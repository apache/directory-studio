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


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescriptionSchemaParser;
import org.apache.directory.shared.ldap.schema.parsers.LdapSyntaxDescription;
import org.apache.directory.shared.ldap.schema.parsers.LdapSyntaxDescriptionSchemaParser;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleDescriptionSchemaParser;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescriptionSchemaParser;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SchemaImpl;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;


/**
 * A Generic Schema Connector, suitable for all LDAP servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GenericSchemaConnector extends AbstractSchemaConnector implements SchemaConnector
{

    private static final AliasDereferencingMethod DEREF_ALIAS_METHOD = AliasDereferencingMethod.ALWAYS;
    private static final ReferralHandlingMethod HANDLE_REFERALS_METHOD = ReferralHandlingMethod.FOLLOW;


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

        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.OBJECT_SCOPE );
        constraintSearch.setReturningAttributes( new String[]
            { "attributeTypes", "objectClasses", "ldapSyntaxes", "matchingRules" } );
        String schemaDn = getSubschemaSubentry( connection, monitor );
        NamingEnumeration<SearchResult> answer = wrapper.search( schemaDn, "(objectclass=subschema)", constraintSearch,
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, ( StudioProgressMonitor ) monitor, null );
        if ( answer != null )
        {
            try
            {
                while ( answer.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) answer.next();
                    try
                    {
                        schemas.add( getSchema( wrapper, searchResult, monitor ) );
                    }
                    catch ( Exception e )
                    {
                        monitor.reportError( e );
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
        return getSubschemaSubentry( connection, monitor ) != null;
    }


    private static String getSubschemaSubentry( Connection connection, StudioProgressMonitor monitor )
    {
        JNDIConnectionWrapper wrapper = connection.getJNDIConnectionWrapper();

        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.OBJECT_SCOPE );
        constraintSearch.setReturningAttributes( new String[]
            { "subschemaSubentry" } );

        NamingEnumeration<SearchResult> answer = wrapper.search( "", "(objectclass=*)", constraintSearch, //$NON-NLS-1$ //$NON-NLS-2$
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, monitor, null );

        if ( answer != null )
        {
            try
            {
                if ( answer.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) answer.next();

                    Attribute subschemaSubentryAttribute = searchResult.getAttributes().get( "subschemaSubentry" );
                    if ( subschemaSubentryAttribute == null )
                    {
                        return null;
                    }

                    if ( subschemaSubentryAttribute.size() != 1 )
                    {
                        return null;
                    }

                    String subschemaSubentry = null;
                    try
                    {
                        subschemaSubentry = ( String ) subschemaSubentryAttribute.get();
                    }
                    catch ( NamingException e )
                    {
                        return null;
                    }

                    return subschemaSubentry;
                }
            }
            catch ( NamingException e )
            {
                monitor.reportError( e );
            }
        }

        return null;
    }


    private static Schema getSchema( JNDIConnectionWrapper wrapper, SearchResult searchResult,
        StudioProgressMonitor monitor ) throws NamingException, ParseException
    {
        // Creating the schema
        Schema schema = new SchemaImpl( "schema" ); //$NON-NLS-1$

        Attribute attributeTypesAttribute = searchResult.getAttributes().get( "attributeTypes" );
        if ( attributeTypesAttribute != null )
        {
            NamingEnumeration<?> ne = attributeTypesAttribute.getAll();
            if ( ne != null )
            {
                while ( ne.hasMoreElements() )
                {
                    String value = ( String ) ne.nextElement();
                    AttributeTypeDescriptionSchemaParser parser = new AttributeTypeDescriptionSchemaParser();
                    parser.setQuirksMode( true );
                    AttributeTypeDescription atd = parser.parseAttributeTypeDescription( value );

                    AttributeTypeImpl impl = new AttributeTypeImpl( atd.getNumericOid() );
                    impl.setOid( atd.getNumericOid() );
                    impl.setNames( atd.getNames().toArray( new String[0] ) );
                    impl.setDescription( atd.getDescription() );
                    impl.setSuperiorName( atd.getSuperType() );
                    impl.setUsage( atd.getUsage() );
                    impl.setSyntaxOid( atd.getSyntax() );
                    impl.setLength( atd.getSyntaxLength() );
                    impl.setObsolete( atd.isObsolete() );
                    impl.setCollective( atd.isCollective() );
                    impl.setSingleValue( atd.isSingleValued() );
                    impl.setCanUserModify( atd.isUserModifiable() );
                    impl.setEqualityName( atd.getEqualityMatchingRule() );
                    impl.setOrderingName( atd.getOrderingMatchingRule() );
                    impl.setSubstrName( atd.getSubstringsMatchingRule() );
                    impl.setSchema( schema.getName() );
                    impl.setSchemaObject( schema );

                    // Active Directory hack
                    if ( impl.getSyntaxOid() != null && "OctetString".equalsIgnoreCase( impl.getSyntaxOid() ) ) //$NON-NLS-1$
                    {
                        impl.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.40" );
                    }

                    schema.addAttributeType( impl );
                }
            }
        }

        Attribute objectClassesAttribute = searchResult.getAttributes().get( "objectClasses" );
        if ( objectClassesAttribute != null )
        {
            NamingEnumeration<?> ne = objectClassesAttribute.getAll();
            if ( ne != null )
            {
                while ( ne.hasMoreElements() )
                {
                    String value = ( String ) ne.nextElement();
                    ObjectClassDescriptionSchemaParser parser = new ObjectClassDescriptionSchemaParser();
                    parser.setQuirksMode( true );
                    ObjectClassDescription ocd = parser.parseObjectClassDescription( value );

                    ObjectClassImpl impl = new ObjectClassImpl( ocd.getNumericOid() );
                    impl.setOid( ocd.getNumericOid() );
                    impl.setNames( ocd.getNames().toArray( new String[0] ) );
                    impl.setDescription( ocd.getDescription() );
                    impl.setSuperClassesNames( ocd.getSuperiorObjectClasses().toArray( new String[0] ) );
                    impl.setType( ocd.getKind() );
                    impl.setObsolete( ocd.isObsolete() );
                    impl.setMustNamesList( ocd.getMustAttributeTypes().toArray( new String[0] ) );
                    impl.setMayNamesList( ocd.getMayAttributeTypes().toArray( new String[0] ) );
                    impl.setSchema( schema.getName() );
                    impl.setSchemaObject( schema );

                    schema.addObjectClass( impl );
                }
            }
        }

        Attribute ldapSyntaxesAttribute = searchResult.getAttributes().get( "ldapSyntaxes" );
        if ( ldapSyntaxesAttribute != null )
        {
            NamingEnumeration<?> ne = ldapSyntaxesAttribute.getAll();
            if ( ne != null )
            {
                while ( ne.hasMoreElements() )
                {
                    String value = ( String ) ne.nextElement();
                    LdapSyntaxDescriptionSchemaParser parser = new LdapSyntaxDescriptionSchemaParser();
                    parser.setQuirksMode( true );
                    LdapSyntaxDescription lsd = parser.parseLdapSyntaxDescription( value );

                    SyntaxImpl impl = new SyntaxImpl( lsd.getNumericOid() );
                    impl.setDescription( lsd.getDescription() );
                    impl.setNames( new String[]
                        { lsd.getDescription() } );
                    //impl.setObsolete( lsd.isObsolete() );
                    impl.setHumanReadable( true );
                    impl.setSchema( schema.getName() );
                    impl.setSchemaObject( schema );

                    schema.addSyntax( impl );
                }
            }
        }
        // if online: assume all received syntaxes in attributes are valid -> create dummy syntaxes if missing
        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            String syntaxOid = at.getSyntaxOid();
            if ( syntaxOid != null && schema.getSyntax( syntaxOid ) == null )
            {
                SyntaxImpl impl = new SyntaxImpl( syntaxOid );
                impl.setSchema( schema.getName() );
                impl.setSchemaObject( schema );
                String oidDescription = Utils.getOidDescription( syntaxOid );
                impl.setDescription( oidDescription != null ? oidDescription : "Dummy" ); //$NON-NLS-1$
                impl.setNames( new String[]
                    { impl.getDescription() } );
                schema.addSyntax( impl );
            }
        }

        Attribute matchingRulesAttribute = searchResult.getAttributes().get( "matchingRules" );
        if ( matchingRulesAttribute != null )
        {
            NamingEnumeration<?> ne = matchingRulesAttribute.getAll();
            if ( ne != null )
            {
                while ( ne.hasMoreElements() )
                {
                    String value = ( String ) ne.nextElement();
                    MatchingRuleDescriptionSchemaParser parser = new MatchingRuleDescriptionSchemaParser();
                    parser.setQuirksMode( true );
                    MatchingRuleDescription mrd = parser.parseMatchingRuleDescription( value );

                    MatchingRuleImpl impl = new MatchingRuleImpl( mrd.getNumericOid() );
                    impl.setDescription( mrd.getDescription() );
                    impl.setNames( mrd.getNames().toArray( new String[0] ) );
                    impl.setObsolete( mrd.isObsolete() );
                    impl.setSyntaxOid( mrd.getSyntax() );
                    impl.setSchema( schema.getName() );
                    impl.setSchemaObject( schema );

                    schema.addMatchingRule( impl );
                }
            }
        }
        // if online: assume all received matching rules in attributes are valid -> create dummy matching rules if missing
        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            String equalityName = at.getEqualityName();
            String orderingName = at.getOrderingName();
            String substrName = at.getSubstrName();
            checkMatchingRules( schema, equalityName, orderingName, substrName );
        }

        return schema;
    }


    private static void checkMatchingRules( Schema schema, String... matchingRuleNames )
    {
        for ( String matchingRuleName : matchingRuleNames )
        {
            if ( matchingRuleName != null && schema.getMatchingRule( matchingRuleName ) == null )
            {
                MatchingRuleImpl impl = new MatchingRuleImpl( matchingRuleName );
                impl.setSchema( schema.getName() );
                impl.setSchemaObject( schema );
                impl.setDescription( "Dummy" ); //$NON-NLS-1$
                impl.setNames( new String[]
                    { matchingRuleName } );
                schema.addMatchingRule( impl );
            }
        }
    }

}
