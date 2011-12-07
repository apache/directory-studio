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

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.parsers.AttributeTypeDescriptionSchemaParser;
import org.apache.directory.shared.ldap.model.schema.parsers.LdapSyntaxDescriptionSchemaParser;
import org.apache.directory.shared.ldap.model.schema.parsers.MatchingRuleDescriptionSchemaParser;
import org.apache.directory.shared.ldap.model.schema.parsers.ObjectClassDescriptionSchemaParser;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.osgi.util.NLS;


/**
 * A Generic Schema Connector, suitable for all LDAP servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GenericSchemaConnector extends AbstractSchemaConnector implements SchemaConnector
{
    private static final AliasDereferencingMethod DEREF_ALIAS_METHOD = AliasDereferencingMethod.ALWAYS;
    private static final ReferralHandlingMethod HANDLE_REFERALS_METHOD = ReferralHandlingMethod.FOLLOW;


    /**
     * {@inheritDoc}
     */
    public void importSchema( Project project, StudioProgressMonitor monitor )
        throws SchemaConnectorException
    {
        monitor.beginTask( Messages.getString( "GenericSchemaConnector.FetchingSchema" ), 1 ); //$NON-NLS-1$
        List<Schema> schemas = new ArrayList<Schema>();
        project.setInitialSchema( schemas );
        ConnectionWrapper wrapper = project.getConnection().getConnectionWrapper();

        SearchControls constraintSearch = new SearchControls();
        constraintSearch.setSearchScope( SearchControls.OBJECT_SCOPE );
        constraintSearch.setReturningAttributes( new String[]
            { "attributeTypes", "objectClasses", "ldapSyntaxes", "matchingRules" } );
        String schemaDn = getSubschemaSubentry( wrapper, monitor );
        NamingEnumeration<SearchResult> answer = wrapper.search( schemaDn, "(objectclass=subschema)", constraintSearch,
            DEREF_ALIAS_METHOD, HANDLE_REFERALS_METHOD, null, ( StudioProgressMonitor ) monitor, null );
        if ( answer != null )
        {
            try
            {
                // Looping the results
                while ( answer.hasMore() )
                {
                    // Creating the schema
                    Schema schema = new Schema( "schema" ); //$NON-NLS-1$
                    schema.setProject( project );
                    schemas.add( schema );

                    getSchema( schema, wrapper, ( SearchResult ) answer.next(), monitor );
                }

            }
            catch ( SchemaConnectorException e )
            {
                throw e;
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
        return getSubschemaSubentry( connection.getConnectionWrapper(), monitor ) != null;
    }


    private static String getSubschemaSubentry( ConnectionWrapper wrapper, StudioProgressMonitor monitor )
    {
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


    private static void getSchema( Schema schema, ConnectionWrapper wrapper, SearchResult searchResult,
        StudioProgressMonitor monitor ) throws NamingException, SchemaConnectorException
    {
        // The counter for parser exceptions
        int parseErrorCount = 0;

        Attribute attributeTypesAttribute = searchResult.getAttributes().get( "attributeTypes" );
        if ( attributeTypesAttribute != null )
        {
            NamingEnumeration<?> ne = attributeTypesAttribute.getAll();
            if ( ne != null )
            {
                while ( ne.hasMoreElements() )
                {
                    String value = ( String ) ne.nextElement();

                    try
                    {
                        AttributeTypeDescriptionSchemaParser parser = new AttributeTypeDescriptionSchemaParser();
                        parser.setQuirksMode( true );

                        AttributeType atd = parser.parseAttributeTypeDescription( value );

                        AttributeType impl = new AttributeType( atd.getOid() );
                        impl.setNames( atd.getNames().toArray( new String[0] ) );
                        impl.setDescription( atd.getDescription() );
                        impl.setSuperiorOid( atd.getSuperiorOid() );
                        impl.setUsage( atd.getUsage() );
                        impl.setSyntaxOid( atd.getSyntaxOid() );
                        impl.setSyntaxLength( atd.getSyntaxLength() );
                        impl.setObsolete( atd.isObsolete() );
                        impl.setCollective( atd.isCollective() );
                        impl.setSingleValued( atd.isSingleValued() );
                        impl.setUserModifiable( atd.isUserModifiable() );
                        impl.setEqualityOid( atd.getEqualityOid() );
                        impl.setOrderingOid( atd.getOrderingOid() );
                        impl.setSubstringOid( atd.getSubstringOid() );
                        impl.setSchemaName( schema.getSchemaName() );

                        // Active Directory hack
                        if ( impl.getSyntaxOid() != null && "OctetString".equalsIgnoreCase( impl.getSyntaxOid() ) ) //$NON-NLS-1$
                        {
                            impl.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.40" );
                        }

                        schema.addAttributeType( impl );
                    }
                    catch ( ParseException e )
                    {
                        // Logging the exception and incrementing the counter
                        PluginUtils.logError( "Unable to parse the attribute type.", e );
                        parseErrorCount++;
                    }
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

                    try
                    {
                        ObjectClassDescriptionSchemaParser parser = new ObjectClassDescriptionSchemaParser();
                        parser.setQuirksMode( true );
                        ObjectClass ocd = parser.parseObjectClassDescription( value );

                        ObjectClass impl = new ObjectClass( ocd.getOid() );
                        impl.setNames( ocd.getNames().toArray( new String[0] ) );
                        impl.setDescription( ocd.getDescription() );
                        impl.setSuperiorOids( ocd.getSuperiorOids() );
                        impl.setType( ocd.getType() );
                        impl.setObsolete( ocd.isObsolete() );
                        impl.setMustAttributeTypeOids( ocd.getMustAttributeTypeOids() );
                        impl.setMayAttributeTypeOids( ocd.getMayAttributeTypeOids() );
                        impl.setSchemaName( schema.getSchemaName() );

                        schema.addObjectClass( impl );
                    }
                    catch ( ParseException e )
                    {
                        // Logging the exception and incrementing the counter
                        PluginUtils.logError( "Unable to parse the object class.", e );
                        parseErrorCount++;
                    }
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

                    try
                    {
                        LdapSyntaxDescriptionSchemaParser parser = new LdapSyntaxDescriptionSchemaParser();
                        parser.setQuirksMode( true );
                        LdapSyntax lsd = parser.parseLdapSyntaxDescription( value );

                        LdapSyntax impl = new LdapSyntax( lsd.getOid() );
                        impl.setDescription( lsd.getDescription() );
                        impl.setNames( new String[]
                            { lsd.getDescription() } );
                        //impl.setObsolete( lsd.isObsolete() );
                        impl.setHumanReadable( true );
                        impl.setSchemaName( schema.getSchemaName() );

                        schema.addSyntax( impl );
                    }
                    catch ( ParseException e )
                    {
                        // Logging the exception and incrementing the counter
                        PluginUtils.logError( "Unable to parse the syntax.", e );
                        parseErrorCount++;
                    }
                }
            }
        }

        // if online: assume all received syntaxes in attributes are valid -> create dummy syntaxes if missing
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            String syntaxOid = at.getSyntaxOid();
            if ( syntaxOid != null && schema.getSyntax( syntaxOid ) == null )
            {
                LdapSyntax impl = new LdapSyntax( syntaxOid );
                impl.setSchemaName( schema.getSchemaName() );
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

                    try
                    {
                        MatchingRuleDescriptionSchemaParser parser = new MatchingRuleDescriptionSchemaParser();
                        parser.setQuirksMode( true );
                        MatchingRule mrd = parser.parseMatchingRuleDescription( value );

                        MatchingRule impl = new MatchingRule( mrd.getOid() );
                        impl.setDescription( mrd.getDescription() );
                        impl.setNames( mrd.getNames().toArray( new String[0] ) );
                        impl.setObsolete( mrd.isObsolete() );
                        impl.setSyntaxOid( mrd.getSyntaxOid() );
                        impl.setSchemaName( schema.getSchemaName() );

                        schema.addMatchingRule( impl );
                    }
                    catch ( ParseException e )
                    {
                        // Logging the exception and incrementing the counter
                        PluginUtils.logError( "Unable to parse the matching rule.", e );
                        parseErrorCount++;
                    }
                }
            }
        }

        // if online: assume all received matching rules in attributes are valid -> create dummy matching rules if missing
        for ( AttributeType at : schema.getAttributeTypes() )
        {
            String equalityName = at.getEqualityOid();
            String orderingName = at.getOrderingOid();
            String substrName = at.getSubstringOid();
            checkMatchingRules( schema, equalityName, orderingName, substrName );
        }

        // Showing an error
        if ( parseErrorCount > 0 )
        {
            if ( parseErrorCount == 1 )
            {
                throw new SchemaConnectorException(
                    Messages.getString( "GenericSchemaConnector.OneSchemaElementCouldNotBeParsedError" ) ); //$NON-NLS-1$

            }
            else
            {
                throw new SchemaConnectorException( NLS.bind(
                    Messages.getString( "GenericSchemaConnector.MultipleSchemaElementsCouldNotBeParsedError" ), //$NON-NLS-1$
                    parseErrorCount ) );
            }
        }
    }


    private static void checkMatchingRules( Schema schema, String... matchingRuleNames )
    {
        for ( String matchingRuleName : matchingRuleNames )
        {
            if ( matchingRuleName != null && schema.getMatchingRule( matchingRuleName ) == null )
            {
                MatchingRule impl = new MatchingRule( matchingRuleName );
                impl.setSchemaName( schema.getSchemaName() );
                impl.setDescription( "Dummy" ); //$NON-NLS-1$
                impl.setNames( new String[]
                    { matchingRuleName } );
                schema.addMatchingRule( impl );
            }
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
