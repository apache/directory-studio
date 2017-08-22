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
package org.apache.directory.studio.openldap.config.model.io;


import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.SearchResult;

import org.apache.directory.api.ldap.model.constants.LdapConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.AttributeUtils;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.exception.LdapNoSuchObjectException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.util.tree.DnNode;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.openldap.config.ExpandedLdifUtils;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.DirectoryServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditorUtils;
import org.apache.directory.studio.openldap.config.jobs.EntryBasedConfigurationPartition;
import org.apache.directory.studio.openldap.config.model.AuxiliaryObjectClass;
import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcConfig;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.apache.directory.studio.openldap.config.model.OlcModuleList;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements a configuration reader for OpenLDAP.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConfigurationReader
{
    private ConfigurationReader()
    {
        // Nothing to do
    }
    
    
    /** The package name where the model classes are stored */
    private static final String MODEL_PACKAGE_NAME = "org.apache.directory.studio.openldap.config.model";

    /** The package name where the database model classes are stored */
    private static final String DATABASE_PACKAGE_NAME = "org.apache.directory.studio.openldap.config.model.database";

    /** The package name where the overlay model classes are stored */
    private static final String OVERLAY_PACKAGE_NAME = "org.apache.directory.studio.openldap.config.model.overlay";


    /**
     * Reads the configuration.
     *
     * @param input the input
     * @return the OpenLDAP configuration
     * @throws Exception
     */
    public static OpenLdapConfiguration readConfiguration( ConnectionServerConfigurationInput input ) throws Exception
    {
        // Creating a new OpenLDAP configuration
        OpenLdapConfiguration configuration = new OpenLdapConfiguration();

        // Saving the connection to the configuration
        configuration.setConnection( input.getConnection() );

        // Getting the browser connection associated with the connection in the input
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( input.getConnection() );

        // Find the location of the configuration
        Dn configurationDn = ConfigurationUtils.getConfigurationDn( browserConnection );

        // Reading the configuration entries on the server
        List<Entry> configurationEntries = readEntries( configurationDn, input, browserConnection );

        // Creating a map to store object created based on their DN
        Map<Dn, OlcConfig> dnToConfigObjectMap = new HashMap<>();

        // For each configuration entries we create an associated configuration
        // object and store it in the OpenLDAP configuration
        for ( Entry entry : configurationEntries )
        {
            // Converting the entry into a configuration object
            OlcConfig configurationObject = createConfigurationObject( entry );
            if ( configurationObject != null )
            {
                // Storing the object in the configuration objects map
                dnToConfigObjectMap.put( entry.getDn(), configurationObject );

                if ( configurationObject instanceof OlcOverlayConfig )
                {
                    OlcOverlayConfig overlayConfig = ( OlcOverlayConfig ) configurationObject;

                    OlcDatabaseConfig databaseConfig = ( OlcDatabaseConfig ) dnToConfigObjectMap.get( entry.getDn()
                        .getParent() );

                    if ( databaseConfig != null )
                    {
                        databaseConfig.addOverlay( overlayConfig );
                    }
                    else
                    {
                        configuration.add( overlayConfig );
                    }
                }
                else if ( configurationObject instanceof OlcGlobal )
                {
                    configuration.setGlobal( ( OlcGlobal ) configurationObject );
                }
                else if ( configurationObject instanceof OlcModuleList )
                {
                    configuration.add( (OlcModuleList)configurationObject );
                }
                else if ( configurationObject instanceof OlcDatabaseConfig )
                {
                    configuration.add( ( OlcDatabaseConfig ) configurationObject );
                }
                else
                {
                    configuration.add( configurationObject );
                }
            }
        }

        return configuration;
    }


    /**
     * Reads the configuration.
     *
     * @param input the input
     * @return the OpenLDAP configuration
     * @throws Exception
     */
    public static OpenLdapConfiguration readConfiguration( DirectoryServerConfigurationInput input ) throws Exception
    {
        return readConfiguration( input.getDirectory() );
    }


    /**
     * Reads the configuration.
     *
     * @param directory the directory
     * @return the OpenLDAP configuration
     * @throws Exception
     */
    public static OpenLdapConfiguration readConfiguration( File directory ) throws Exception
    {
        // Creating a new OpenLDAP configuration
        OpenLdapConfiguration configuration = new OpenLdapConfiguration();

        // Reading the configuration entries disk
        DnNode<Entry> tree = readEntries( directory );

        // Creating configuration objects
        createConfigurationObjects( tree, configuration );

        return configuration;
    }


    /**
     * Creates the configuration objects.
     *
     * @param configuration the configuration
     * @param dnToConfigObjectMap the maps to store
     */
    /**
     * Creates the configuration objects.
     *
     * @param tree the tree
     * @param configuration the configuration
     * @throws ConfigurationException
     */
    private static void createConfigurationObjects( DnNode<Entry> tree, OpenLdapConfiguration configuration )
        throws ConfigurationException
    {
        // Creating a map to store object created based on their DN
        Map<Dn, OlcConfig> dnToConfigObjectMap = new HashMap<>();

        createConfigurationObjects( tree, configuration, dnToConfigObjectMap );
    }


    /**
     * Creates the configuration objects.
     *
     * @param node the node
     * @param configuration the configuration
     * @param dnToConfigObjectMap the maps to associate DNs to configuration objects
     * @throws ConfigurationException
     */
    private static void createConfigurationObjects( DnNode<Entry> node, OpenLdapConfiguration configuration,
        Map<Dn, OlcConfig> dnToConfigObjectMap ) throws ConfigurationException
    {
        if ( node != null )
        {
            // Checking if the node as an element
            if ( node.hasElement() )
            {
                // Getting the entry for the node
                Entry entry = node.getElement();

                // Converting the entry into a configuration object
                OlcConfig configurationObject = createConfigurationObject( entry );

                if ( configurationObject != null )
                {
                    // Storing the object in the configuration objects map
                    dnToConfigObjectMap.put( entry.getDn(), configurationObject );

                    // Checking if it's an overlay
                    if ( configurationObject instanceof OlcOverlayConfig )
                    {
                        OlcOverlayConfig overlayConfig = ( OlcOverlayConfig ) configurationObject;

                        // Getting the associated database configuration object
                        OlcDatabaseConfig databaseConfig = ( OlcDatabaseConfig ) dnToConfigObjectMap.get( entry.getDn()
                            .getParent() );

                        if ( databaseConfig != null )
                        {
                            databaseConfig.addOverlay( overlayConfig );
                        }
                        else
                        {
                            configuration.add( overlayConfig );
                        }
                    }
                    // Checking if it's the "global' configuration object
                    else if ( configurationObject instanceof OlcGlobal )
                    {
                        configuration.setGlobal( ( OlcGlobal ) configurationObject );
                    }
                    // Checking if it's a database
                    else if ( configurationObject instanceof OlcDatabaseConfig )
                    {
                        configuration.add( (OlcDatabaseConfig)configurationObject );
                    }
                    // Any other object type
                    else
                    {
                        configuration.add( configurationObject );
                    }
                }
            }

            // Checking the node has some children
            if ( node.hasChildren() )
            {
                Collection<DnNode<Entry>> children = node.getChildren().values();

                for ( DnNode<Entry> child : children )
                {
                    createConfigurationObjects( child, configuration, dnToConfigObjectMap );
                }
            }
        }
    }


    /**
     * Reads the configuration entries from the input.
     *
     * @param directory the directory
     * @return the tree of configuration entries found
     * @throws Exception if an error occurred
     */
    private static DnNode<Entry> readEntries( File directory )
        throws Exception
    {
        // Reading the entries tree
        DnNode<Entry> tree = ExpandedLdifUtils.read( directory );

        // Checking the read tree
        if ( ( tree != null ) && ( tree.size() != 0 ) )
        {
            return tree;
        }
        else
        {
            throw new Exception( "No entries found" );
        }
    }


    /**
     * Gets the highest structural object class found in the attribute.
     *
     * @param objectClassAttribute the 'objectClass' attribute
     * @return the highest structural object class found in the attribute.
     */
    public static ObjectClass getHighestStructuralObjectClass( Attribute objectClassAttribute )
        throws ConfigurationException
    {
        Set<ObjectClass> candidates = new HashSet<>();

        try
        {
            SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();

            if ( ( objectClassAttribute != null ) && ( schemaManager != null ) )
            {
                // Create the set of candidates
                for ( Value objectClassValue : objectClassAttribute )
                {
                    ObjectClass oc = OpenLDAPServerConfigurationEditorUtils.getObjectClass( schemaManager,
                        objectClassValue.getValue() );

                    if ( ( oc != null ) && ( oc.isStructural() ) )
                    {
                        candidates.add( oc );
                    }
                }

                // Now find the parent OC
                for ( Value objectClassValue : objectClassAttribute )
                {
                    ObjectClass oc = OpenLDAPServerConfigurationEditorUtils.getObjectClass( schemaManager,
                        objectClassValue.getValue() );

                    if ( oc != null )
                    {
                        for ( String superiorName : oc.getSuperiorOids() )
                        {
                            ObjectClass superior = OpenLDAPServerConfigurationEditorUtils.getObjectClass( schemaManager,
                                superiorName );

                            if ( ( superior != null ) && ( superior.isStructural() )
                                && ( candidates.contains( superior ) ) )
                            {
                                candidates.remove( superior );
                            }
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationException( e );
        }

        // The remaining OC in the candidates set is the one we are looking for
        return candidates.toArray( new ObjectClass[]
            {} )[0];
    }


    /**
     * Gets the auxiliary object classes found in the attribute.
     *
     * @param objectClassAttribute the 'objectClass' attribute
     * @return the auxiliary object classes found in the attribute.
     */
    public static ObjectClass[] getAuxiliaryObjectClasses( Attribute objectClassAttribute )
        throws ConfigurationException
    {
        List<ObjectClass> auxiliaryObjectClasses = new ArrayList<>();

        try
        {
            SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();

            if ( ( objectClassAttribute != null ) && ( schemaManager != null ) )
            {
                for ( Value objectClassValue : objectClassAttribute )
                {
                    ObjectClass oc = OpenLDAPServerConfigurationEditorUtils.getObjectClass( schemaManager,
                        objectClassValue.getValue() );

                    if ( ( oc != null ) && ( oc.isAuxiliary() ) )
                    {
                        auxiliaryObjectClasses.add( oc );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationException( e );
        }

        return auxiliaryObjectClasses.toArray( new ObjectClass[0] );
    }


    /**
     * Reads the configuration entries from the input.
     *
     * @param configurationDn the configuration DN
     * @param input the editor input
     * @param browserConnection the connection
     * @return the list of configuration entries found
     * @throws Exception if an error occurred
     */
    public static List<Entry> readEntries( Dn configurationDn, ConnectionServerConfigurationInput input,
        IBrowserConnection browserConnection ) throws Exception
    {
        List<Entry> foundEntries = new ArrayList<>();

        IProgressMonitor progressMonitor = new NullProgressMonitor();
        StudioProgressMonitor monitor = new StudioProgressMonitor( progressMonitor );
        Connection connection = input.getConnection();

        // Creating the schema manager
        SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();

        // The DN corresponding to the configuration base

        // Creating the configuration partition
        EntryBasedConfigurationPartition configurationPartition = OpenLDAPServerConfigurationEditorUtils
            .createConfigurationPartition( schemaManager, configurationDn );

        // Opening the connection (if needed)
        ConfigurationUtils.openConnection( connection, monitor );

        // Creating the search parameter
        SearchParameter configSearchParameter = new SearchParameter();
        configSearchParameter.setSearchBase( configurationDn );
        configSearchParameter.setFilter( LdapConstants.OBJECT_CLASS_STAR );
        configSearchParameter.setScope( SearchScope.OBJECT );
        configSearchParameter.setReturningAttributes( SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );

        // Looking for the 'ou=config' base entry
        Entry configEntry = null;
        StudioNamingEnumeration enumeration = SearchRunnable.search( browserConnection, configSearchParameter,
            monitor );

        // Checking if an error occurred
        if ( monitor.errorsReported() )
        {
            throw monitor.getException();
        }

        // Getting the entry
        if ( enumeration.hasMore() )
        {
            // Creating the base entry
            SearchResult searchResult =  enumeration.next();
            configEntry = new DefaultEntry( schemaManager, AttributeUtils.toEntry( searchResult.getAttributes(),
                new Dn( searchResult.getNameInNamespace() ) ) );
        }
        enumeration.close();

        // Verifying we found the base entry
        if ( configEntry == null )
        {
            throw new LdapNoSuchObjectException( NLS.bind( "Unable to find the ''{0}'' base entry.", configurationDn ) );
        }

        // Creating a list to hold the entries that needs to be checked
        // for children and added to the partition
        List<Entry> entries = new ArrayList<>();
        entries.add( configEntry );

        // Looping on the entries list until it's empty
        while ( !entries.isEmpty() )
        {
            // Removing the first entry from the list
            Entry entry = entries.remove( 0 );

            // Adding the entry to the partition and the entries list
            configurationPartition.addEntry( entry );
            foundEntries.add( entry );

            SearchParameter searchParameter = new SearchParameter();
            searchParameter.setSearchBase( entry.getDn() );
            searchParameter.setFilter( LdapConstants.OBJECT_CLASS_STAR );
            searchParameter.setScope( SearchScope.ONELEVEL );
            searchParameter.setReturningAttributes( SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );

            // Looking for the children of the entry
            StudioNamingEnumeration childrenEnumeration = SearchRunnable.search( browserConnection,
                searchParameter, monitor );

            // Checking if an error occurred
            if ( monitor.errorsReported() )
            {
                throw monitor.getException();
            }

            while ( childrenEnumeration.hasMore() )
            {
                // Creating the child entry
                SearchResult searchResult =  childrenEnumeration.next();
                Entry childEntry = new DefaultEntry( schemaManager, AttributeUtils.toEntry(
                    searchResult.getAttributes(),
                    new Dn( searchResult.getNameInNamespace() ) ) );

                // Adding the children to the list of entries
                entries.add( childEntry );
            }
            childrenEnumeration.close();
        }

        // Setting the created partition to the input
        input.setOriginalPartition( configurationPartition );

        return foundEntries;
    }


    private static OlcConfig createConfigurationObject( Entry entry )
        throws ConfigurationException
    {
        // Getting the 'objectClass' attribute
        Attribute objectClassAttribute = entry.get( SchemaConstants.OBJECT_CLASS_AT );

        if ( objectClassAttribute != null )
        {
            // Getting the highest structural object class based on schema
            ObjectClass highestStructuralObjectClass = getHighestStructuralObjectClass( objectClassAttribute );

            // Computing the class name for the bean corresponding to the structural object class
            String highestObjectClassName = highestStructuralObjectClass.getName();
            StringBuilder className = new StringBuilder();

            if ( objectClassAttribute.contains( "olcDatabaseConfig" ) )
            {
                className.append( DATABASE_PACKAGE_NAME );
            }
            else if ( objectClassAttribute.contains( "olcOverlayConfig" ) )
            {
                className.append( OVERLAY_PACKAGE_NAME );
            }
            else
            {
                className.append( MODEL_PACKAGE_NAME );
            }

            className.append( "." );
            className.append( Character.toUpperCase( highestObjectClassName.charAt( 0 ) ) );
            className.append( highestObjectClassName.substring( 1 ) );

            // Instantiating the object
            OlcConfig bean = null;

            try
            {
                Class<?> clazz = Class.forName( className.toString() );
                Constructor<?> constructor = clazz.getConstructor();
                bean = ( OlcConfig ) constructor.newInstance();
            }
            catch ( Exception e )
            {
                throw new ConfigurationException( e );
            }

            // Checking if the bean as been created
            if ( bean == null )
            {
                throw new ConfigurationException( "The instantiated bean for '" + highestObjectClassName + "' is null" );
            }

            // Checking auxiliary object classes
            ObjectClass[] auxiliaryObjectClasses = getAuxiliaryObjectClasses( objectClassAttribute );

            if ( ( auxiliaryObjectClasses != null ) && ( auxiliaryObjectClasses.length > 0 ) )
            {
                for ( ObjectClass auxiliaryObjectClass : auxiliaryObjectClasses )
                {
                    // Computing the class name for the bean corresponding to the auxiliary object class
                    String auxiliaryObjectClassName = auxiliaryObjectClass.getName();
                    className = new StringBuilder();
                    className.append( MODEL_PACKAGE_NAME );
                    className.append( "." );
                    className.append( Character.toUpperCase( auxiliaryObjectClassName.charAt( 0 ) ) );
                    className.append( auxiliaryObjectClassName.substring( 1 ) );

                    // Instantiating the object
                    AuxiliaryObjectClass auxiliaryObjectClassBean = null;

                    try
                    {
                        Class<?> clazz = Class.forName( className.toString() );
                        Constructor<?> constructor = clazz.getConstructor();
                        auxiliaryObjectClassBean = ( AuxiliaryObjectClass ) constructor.newInstance();
                    }
                    catch ( Exception e )
                    {
                        throw new ConfigurationException( e );
                    }

                    // Checking if the bean as been created
                    if ( auxiliaryObjectClassBean == null )
                    {
                        throw new ConfigurationException( "The instantiated auxiliary object class bean for '"
                            + auxiliaryObjectClassName + "' is null" );
                    }

                    // Reading all values
                    readValues( entry, auxiliaryObjectClassBean );

                    // Adding the auxiliary object class bean to the bean
                    bean.addAuxiliaryObjectClasses( auxiliaryObjectClassBean );
                }
            }

            // Reading all values
            readValues( entry, bean );

            // Storing the parent DN
            bean.setParentDn( entry.getDn().getParent() );

            return bean;
        }
        return null;
    }


    /**
     * Reads the values of the entry and saves them to the bean.
     *
     * @param entry the entry
     * @param bean then bean
     * @throws ConfigurationException
     */
    private static void readValues( Entry entry, Object bean ) throws ConfigurationException
    {
        // Checking all fields of the bean (including super class fields)
        Class<?> clazz = bean.getClass();
        while ( clazz != null )
        {
            // Looping on all fields of the class
            Field[] fields = clazz.getDeclaredFields();
            for ( Field field : fields )
            {
                // Looking for the @ConfigurationElement annotation
                ConfigurationElement configurationElement = field.getAnnotation( ConfigurationElement.class );
                if ( configurationElement != null )
                {
                    // Checking if we're have a value  for the attribute type
                    String attributeType = configurationElement.attributeType();
                    if ( ( attributeType != null ) && ( !"".equals( attributeType ) ) )
                    {
                        Attribute attribute = entry.get( attributeType );
                        if ( ( attribute != null ) && ( attribute.size() > 0 ) )
                        {
                            // Making the field accessible (we get an exception if we don't do that)
                            field.setAccessible( true );

                            // loop on the values and inject them in the bean
                            for ( Value value : attribute )
                            {
                                readAttributeValue( bean, field, attribute, value );
                            }
                        }
                    }
                }
            }

            // Switching to the super class
            clazz = clazz.getSuperclass();
        }
    }


    /**
     * Reads the attribute value.
     *
     * @param bean the bean
     * @param field the field
     * @param attribute the attribute
     * @param value the value
     * @throws ConfigurationException
     */
    private static void readAttributeValue( Object bean, Field field, Attribute attribute, Value value )
        throws ConfigurationException
    {
        Class<?> type = field.getType();
        String addMethodName = "add" + Character.toUpperCase( field.getName().charAt( 0 ) )
            + field.getName().substring( 1 );
        String valueStr = value.getValue();

        try
        {
            // String class
            if ( type == String.class )
            {
                Object stringValue = readSingleValue( type, attribute, valueStr );
                if ( stringValue != null )
                {
                    field.set( bean, stringValue );
                }
            }
            // Int primitive type
            else if ( type == int.class )
            {
                Object integerValue = readSingleValue( type, attribute, valueStr );
                if ( integerValue != null )
                {
                    field.setInt( bean, ( ( Integer ) integerValue ).intValue() );
                }
            }
            // Integer class
            else if ( type == Integer.class )
            {
                Object integerValue = readSingleValue( type, attribute, valueStr );
                if ( integerValue != null )
                {
                    field.set( bean, ( Integer ) integerValue );
                }
            }
            // Long primitive type
            else if ( type == long.class )
            {
                Object longValue = readSingleValue( type, attribute, valueStr );
                if ( longValue != null )
                {
                    field.setLong( bean, ( ( Long ) longValue ).longValue() );
                }
            }
            // Long class
            else if ( type == Long.class )
            {
                Object longValue = readSingleValue( type, attribute, valueStr );
                if ( longValue != null )
                {
                    field.setLong( bean, ( Long ) longValue );
                }
            }
            // Boolean primitive type
            else if ( type == boolean.class )
            {
                Object booleanValue = readSingleValue( type, attribute, valueStr );
                if ( booleanValue != null )
                {
                    field.setBoolean( bean, ( ( Boolean ) booleanValue ).booleanValue() );
                }
            }
            // Boolean class
            else if ( type == Boolean.class )
            {
                Object booleanValue = readSingleValue( type, attribute, valueStr );
                if ( booleanValue != null )
                {
                    field.set( bean, ( Boolean ) booleanValue );
                }
            }
            // Dn class
            else if ( type == Dn.class )
            {
                Object dnValue = readSingleValue( type, attribute, valueStr );
                if ( dnValue != null )
                {
                    field.set( bean, dnValue );
                }
            }
            // Set class
            else if ( type == Set.class )
            {
                Type genericFieldType = field.getGenericType();

                if ( genericFieldType instanceof ParameterizedType )
                {
                    ParameterizedType parameterizedType = ( ParameterizedType ) genericFieldType;
                    Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
                    if ( ( fieldArgTypes != null ) && ( fieldArgTypes.length > 0 ) )
                    {
                        Class<?> fieldArgClass = ( Class<?> ) fieldArgTypes[0];

                        Object methodParameter = Array.newInstance( fieldArgClass, 1 );
                        Array.set( methodParameter, 0, readSingleValue( fieldArgClass, attribute, valueStr ) );

                        Method method = bean.getClass().getMethod( addMethodName, methodParameter.getClass() );

                        method.invoke( bean, methodParameter );
                    }
                }
            }
            // List class
            else if ( type == List.class )
            {
                Type genericFieldType = field.getGenericType();

                if ( genericFieldType instanceof ParameterizedType )
                {
                    ParameterizedType parameterizedType = ( ParameterizedType ) genericFieldType;
                    Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
                    if ( ( fieldArgTypes != null ) && ( fieldArgTypes.length > 0 ) )
                    {
                        Class<?> fieldArgClass = ( Class<?> ) fieldArgTypes[0];

                        Object methodParameter = Array.newInstance( fieldArgClass, 1 );
                        Array.set( methodParameter, 0, readSingleValue( fieldArgClass, attribute, valueStr ) );

                        Method method = bean.getClass().getMethod( addMethodName, methodParameter.getClass() );

                        method.invoke( bean, methodParameter );
                    }
                }
            }
        }
        catch ( IllegalArgumentException | IllegalAccessException iae )
        {
            throw new ConfigurationException( "Cannot store '" + valueStr + "' into attribute "
                + attribute.getId() );
        }
        catch ( SecurityException se )
        {
            throw new ConfigurationException( "Cannot access to the class "
                + bean.getClass().getName() );
        }
        catch ( NoSuchMethodException nsme )
        {
            throw new ConfigurationException( "Cannot find a method " + addMethodName
                + " in the class "
                + bean.getClass().getName() );
        }
        catch ( InvocationTargetException ite )
        {
            throw new ConfigurationException( "Cannot invoke the class "
                + bean.getClass().getName() + ", "
                + ite.getMessage() );
        }
        catch ( NegativeArraySizeException nase )
        {
            // No way that can happen...
        }
    }


    /**
     * Reads a single value attribute.
     *
     * @param field the field
     * @param attribute the attribute
     * @param value the value as a String
     * @throws ConfigurationException
     */
    private static Object readSingleValue( Class<?> type, Attribute attribute, String value )
        throws ConfigurationException
    {
        try
        {
            // String class
            if ( type == String.class )
            {
                return value;
            }
            // Int primitive type
            else if ( type == int.class )
            {
                return new Integer( value );
            }
            // Integer class
            else if ( type == Integer.class )
            {
                return new Integer( value );
            }
            // Long class
            else if ( type == long.class )
            {
                return new Long( value );
            }
            // Boolean primitive type
            else if ( type == boolean.class )
            {
                return new Boolean( value );
            }
            // Boolean class
            else if ( type == Boolean.class )
            {
                return new Boolean( value );
            }
            // Dn class
            else if ( type == Dn.class )
            {
                try
                {
                    return new Dn( value );
                }
                catch ( LdapInvalidDnException lide )
                {
                    throw new ConfigurationException( "The Dn '" + value + "' for attribute " + attribute.getId()
                        + " is not a valid Dn" );
                }
            }

            return null;
        }
        catch ( IllegalArgumentException iae )
        {
            throw new ConfigurationException( "Cannot store '" + value + "' into attribute "
                + attribute.getId() );
        }
    }
}
