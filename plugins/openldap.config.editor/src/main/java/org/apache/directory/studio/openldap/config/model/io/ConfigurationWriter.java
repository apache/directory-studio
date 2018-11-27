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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.editor.OpenLdapServerConfigurationEditorUtils;
import org.apache.directory.studio.openldap.config.model.AuxiliaryObjectClass;
import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcConfig;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;
import org.apache.directory.studio.openldap.config.model.OpenLdapConfiguration;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;


/**
 * This class implements a configuration reader for OpenLDAP.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConfigurationWriter
{
    /** The browserConnection */
    private IBrowserConnection browserConnection;

    /** The configuration */
    private OpenLdapConfiguration configuration;

    /** The list of entries */
    private List<LdifEntry> entries;


    /**
     * Creates a new instance of ConfigWriter.
     *
     * @param browserConnection the browser connection
     * @param configuration the configuration
     */
    public ConfigurationWriter( IBrowserConnection browserConnection, OpenLdapConfiguration configuration )
    {
        this.browserConnection = browserConnection;
        this.configuration = configuration;
    }


    /**
     * Creates a new instance of ConfigWriter.
     *
     * @param configuration the configuration
     */
    public ConfigurationWriter( OpenLdapConfiguration configuration )
    {
        this.configuration = configuration;
    }


    /**
     * Converts the configuration bean to a list of LDIF entries.
     */
    private void convertConfigurationBeanToLdifEntries( Dn configurationDn ) throws ConfigurationException
    {
        try
        {
            if ( entries == null )
            {
                entries = new ArrayList<>();

                // Adding the global configuration
                addConfigurationBean( configuration.getGlobal(), Dn.EMPTY_DN );

                // Adding databases
                for ( OlcDatabaseConfig database : configuration.getDatabases() )
                {
                    LdifEntry entry = addConfigurationBean( database, configurationDn );

                    if ( entry != null )
                    {
                        for ( OlcOverlayConfig overlay : database.getOverlays() )
                        {
                            addConfigurationBean( overlay, entry.getDn() );
                        }
                    }
                }

                // Adding other elements
                for ( OlcConfig configurationBean : configuration.getConfigurationElements() )
                {
                    addConfigurationBean( configurationBean, configurationDn );
                }
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationException( "Unable to convert the configuration beans to LDIF entries", e );
        }
    }


    private LdifEntry addConfigurationBean( OlcConfig configurationBean, Dn parentDn ) throws Exception
    {
        if ( configurationBean != null )
        {
            // Getting the class of the bean
            Class<?> beanClass = configurationBean.getClass();

            // Creating the entry to hold the bean and adding it to the list
            LdifEntry entry = new LdifEntry();
            entry.setDn( getDn( configurationBean, parentDn ) );
            addObjectClassAttribute( entry, getObjectClassNameForBean( beanClass ) );
            entries.add( entry );

            // Checking auxiliary object classes
            List<AuxiliaryObjectClass> auxiliaryObjectClassesList = configurationBean.getAuxiliaryObjectClasses();
            
            if ( ( auxiliaryObjectClassesList != null ) && !auxiliaryObjectClassesList.isEmpty() )
            {
                for ( AuxiliaryObjectClass auxiliaryObjectClass : auxiliaryObjectClassesList )
                {
                    // Getting the bean class for the auxiliary object class
                    Class<?> auxiliaryObjectClassBeanClass = auxiliaryObjectClass.getClass();

                    // Updating the objectClass attribute value
                    addAttributeTypeValue( SchemaConstants.OBJECT_CLASS_AT,
                        getObjectClassNameForBean( auxiliaryObjectClassBeanClass ), entry );

                    // Adding fields of the auxiliary object class to the entry 
                    addFieldsToBean( auxiliaryObjectClass, auxiliaryObjectClassBeanClass, entry );
                }
            }

            // A flag to know when we reached the 'OlcConfig' class when 
            // looping on the class hierarchy of the bean
            boolean olcConfigBeanClassFound = false;

            // Looping until the 'OlcConfig' class has been found
            while ( !olcConfigBeanClassFound )
            {
                // Checking if we reached the 'OlcConfig' class
                if ( beanClass == OlcConfig.class )
                {
                    olcConfigBeanClassFound = true;
                }

                // Adding fields of the bean to the entry 
                addFieldsToBean( configurationBean, beanClass, entry );

                // Moving to the upper class in the class hierarchy
                beanClass = beanClass.getSuperclass();
            }

            return entry;
        }

        return null;
    }


    private void addFieldsToBean( Object configurationBean, Class<?> beanClass, LdifEntry entry ) throws Exception
    {
        if ( ( configurationBean != null ) && ( beanClass != null ) && ( entry != null ) )
        {
            // Looping on all fields of the bean
            for ( Field field : beanClass.getDeclaredFields() )
            {
                // Making the field accessible (we get an exception if we don't do that)
                field.setAccessible( true );

                // Getting the class of the field
                Class<?> fieldClass = field.getType();
                Object fieldValue = field.get( configurationBean );

                if ( fieldValue != null )
                {
                    // Looking for the @ConfigurationElement annotation
                    ConfigurationElement configurationElement = field.getAnnotation( ConfigurationElement.class );
                    
                    if ( configurationElement != null )
                    {
                        // Checking if we have a value for the attribute type
                        String attributeType = configurationElement.attributeType();
                        
                        if ( !Strings.isEmpty( attributeType ) )
                        {
                            // Adding values to the entry, and if it's empty, add the default value
                            addAttributeTypeValues( configurationElement, fieldValue, entry );
                        }
                        
                        else if ( OlcConfig.class.isAssignableFrom( fieldClass ) )
                        {
                            // Checking if we're dealing with a AdsBaseBean subclass type
                            addConfigurationBean( ( OlcConfig ) fieldValue, entry.getDn() );
                        }
                    }
                }
            }
        }
    }


    /**
     * Gets the Dn associated with the configuration bean.
     *
     * @param bean the configuration bean
     * @param parentDn the parent dn
     * @return the Dn associated with the configuration bean based on the given base Dn.
     * @throws LdapInvalidDnException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Dn getDn( OlcConfig bean, Dn parentDn ) throws LdapInvalidDnException, LdapInvalidAttributeValueException, 
        IllegalAccessException
    {
        // Getting the class of the bean
        Class<?> beanClass = bean.getClass();

        // A flag to know when we reached the 'AdsBaseBean' class when 
        // looping on the class hierarchy of the bean
        boolean olcConfigBeanClassFound = false;

        // Looping until the 'OlcConfig' class has been found
        while ( !olcConfigBeanClassFound )
        {
            // Checking if we reached the 'OlcConfig' class
            if ( beanClass == OlcConfig.class )
            {
                olcConfigBeanClassFound = true;
            }

            // Looping on all fields of the bean
            for ( Field field : beanClass.getDeclaredFields() )
            {
                // Making the field accessible (we get an exception if we don't do that)
                field.setAccessible( true );

                // Looking for the @ConfigurationElement annotation and
                // if the field is the Rdn
                ConfigurationElement configurationElement = field.getAnnotation( ConfigurationElement.class );
                
                if ( ( configurationElement != null ) && ( configurationElement.isRdn() ) )
                {
                    Object value = field.get( bean );
                    
                    if ( value == null )
                    {
                        continue;
                    }

                    // Is the value multiple?
                    if ( isMultiple( value.getClass() ) )
                    {
                        Collection<?> values = ( Collection<?> ) value;
                        
                        if ( values.isEmpty() )
                        {
                            String defaultValue = configurationElement.defaultValue();
                            
                            if ( defaultValue != null )
                            {
                                value = defaultValue;
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            value = values.toArray()[0];
                        }
                    }

                    if ( ( bean.getParentDn() != null ) )
                    {
                        return bean.getParentDn()
                            .add( new Rdn( configurationElement.attributeType(), value.toString() ) );
                    }
                    else
                    {
                        return parentDn.add( new Rdn( configurationElement.attributeType(), value.toString() ) );
                    }
                }
            }

            // Moving to the upper class in the class hierarchy
            beanClass = beanClass.getSuperclass();
        }

        return Dn.EMPTY_DN;
    }


    /**
     * Gets the name of the object class to use for the given bean class.
     *
     * @param clazz the bean class
     * @return the name of the object class to use for the given bean class
     */
    private String getObjectClassNameForBean( Class<?> clazz )
    {
        String classNameWithPackage = getClassNameWithoutPackageName( clazz );
        
        return Character.toLowerCase( classNameWithPackage.charAt( 0 ) ) + classNameWithPackage.substring( 1 );
    }


    /**
     * Gets the class name of the given class stripped from its package name.
     *
     * @param clazz the class
     * @return the class name of the given class stripped from its package name
     */
    private String getClassNameWithoutPackageName( Class<?> clazz )
    {
        String className = clazz.getName();

        int firstChar = className.lastIndexOf( '.' ) + 1;
        
        if ( firstChar > 0 )
        {
            return className.substring( firstChar );
        }

        return className;
    }


    /**
     * Writes the configuration bean as LDIF to the given file.
     *
     * @param path the output file path
     * @throws ConfigurationException if an error occurs during the conversion to LDIF
     * @throws IOException if an error occurs when writing the file
     */
    public void writeToPath( String path ) throws ConfigurationException, IOException
    {
        writeToFile( new File( path ) );
    }


    /**
     * Writes the configuration bean as LDIF to the given file.
     *
     * @param file the output file
     * @throws ConfigurationException if an error occurs during the conversion to LDIF
     * @throws IOException if an error occurs when writing the file
     */
    public void writeToFile( File file ) throws ConfigurationException, IOException
    {
        // Writing the file to disk
        try ( FileWriter writer = new FileWriter( file ) )
        {
            writer.append( writeToString() );
        }
    }


    /**
     * Writes the configuration to a String object.
     *
     * @return a String containing the LDIF representation of the configuration
     * @throws ConfigurationException if an error occurs during the conversion to LDIF
     */
    public String writeToString() throws ConfigurationException
    {
        // Converting the configuration bean to a list of LDIF entries
        convertConfigurationBeanToLdifEntries( ConfigurationUtils.getConfigurationDn( browserConnection ) );

        // Building the StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append( "version: 1\n" );
        
        for ( LdifEntry entry : entries )
        {
            sb.append( entry.toString() );
        }

        return sb.toString();
    }


    /**
     * Gets the converted LDIF entries from the configuration bean.
     *
     * @param browserConnection the browserConnection
     * @return the list of converted LDIF entries
     * @throws ConfigurationException if an error occurs during the conversion to LDIF
     */
    public List<LdifEntry> getConvertedLdifEntries() throws ConfigurationException
    {
        // Converting the configuration bean to a list of LDIF entries
        convertConfigurationBeanToLdifEntries( ConfigurationUtils.getConfigurationDn( browserConnection ) );

        // Returning the list of entries
        return entries;
    }


    /**
     * Gets the converted LDIF entries from the configuration bean.
     *
     * @return the list of converted LDIF entries
     * @throws ConfigurationException if an error occurs during the conversion to LDIF
     */
    public List<LdifEntry> getConvertedLdifEntries( Dn configurationDn ) throws ConfigurationException
    {
        // Converting the configuration bean to a list of LDIF entries
        convertConfigurationBeanToLdifEntries( configurationDn );

        // Returning the list of entries
        return entries;
    }


    /**
     * Adds the computed 'objectClass' attribute for the given entry and object class name.
     *
     * @param entry the entry
     * @param objectClass the object class name
     * @throws LdapException
     */
    private void addObjectClassAttribute( LdifEntry entry, String objectClass )
        throws LdapException
    {
        try
        {
            ObjectClass objectClassObject = OpenLdapServerConfigurationEditorUtils.getObjectClass( OpenLdapConfigurationPlugin
                .getDefault().getSchemaManager(), objectClass );

            if ( objectClassObject != null )
            {
                // Building the list of 'objectClass' attribute values
                Set<String> objectClassAttributeValues = new HashSet<>();
                computeObjectClassAttributeValues( objectClassAttributeValues, objectClassObject );

                // Adding values to the entry
                addAttributeTypeValues( SchemaConstants.OBJECT_CLASS_AT, objectClassAttributeValues, entry );
            }
            else
            {
                // TODO: throw an exception 
            }
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }
    }


    /**
     * Recursively computes the 'objectClass' attribute values set.
     *
     * @param schemaManager the schema manager
     * @param objectClassAttributeValues the set containing the values
     * @param objectClass the current object class
     * @throws LdapException
     */
    private void computeObjectClassAttributeValues( Set<String> objectClassAttributeValues, ObjectClass objectClass )
        throws LdapException
    {
        try
        {
            SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();

            ObjectClass topObjectClass = OpenLdapServerConfigurationEditorUtils.getObjectClass( schemaManager,
                SchemaConstants.TOP_OC );

            if ( topObjectClass != null )
            {
                // TODO throw new exception (there should be a top object class 
            }

            if ( topObjectClass.equals( objectClass ) )
            {
                objectClassAttributeValues.add( objectClass.getName() );
            }
            else
            {
                objectClassAttributeValues.add( objectClass.getName() );

                List<String> superiors = objectClass.getSuperiorOids();
                
                if ( ( superiors != null ) && !superiors.isEmpty() )
                {
                    for ( String superior : superiors )
                    {
                        ObjectClass superiorObjectClass = OpenLdapServerConfigurationEditorUtils.getObjectClass( schemaManager,
                            superior );
                        computeObjectClassAttributeValues( objectClassAttributeValues, superiorObjectClass );
                    }
                }
                else
                {
                    objectClassAttributeValues.add( topObjectClass.getName() );
                }
            }
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }
    }


    /**
     * Adds values for an attribute type to the given entry.
     *
     * @param attributeType the attribute type
     * @param value the value
     * @param entry the entry
     * @throws org.apache.directory.api.ldap.model.exception.LdapException
     */
    private void addAttributeTypeValues( ConfigurationElement configurationElement, Object o, LdifEntry entry )
        throws LdapException
    {
        String attributeType = configurationElement.attributeType();
        
        // We don't store a 'null' value
        if ( o != null )
        {
            // Is the value multiple?
            if ( isMultiple( o.getClass() ) )
            {
                // Adding each single value separately
                Collection<?> values = ( Collection<?> ) o;

                if ( values.isEmpty() )
                {
                    if ( !configurationElement.isOptional() )
                    {
                        // Add the default value
                        addAttributeTypeValue( attributeType, configurationElement.defaultValue(), entry );
                    }
                }
                else
                {
                    for ( Object value : values )
                    {
                        addAttributeTypeValue( attributeType, value, entry );
                    }
                }
            }
            else
            {
                // Adding the single value
                addAttributeTypeValue( attributeType, o, entry );
            }
        }
    }


    /**
     * Adds values for an attribute type to the given entry.
     *
     * @param attributeType the attribute type
     * @param value the value
     * @param entry the entry
     * @throws org.apache.directory.api.ldap.model.exception.LdapException
     */
    private void addAttributeTypeValues( String attributeType, Object object, LdifEntry entry )
        throws LdapException
    {
        // We don't store a 'null' value
        if ( object != null )
        {
            // Is the value multiple?
            if ( isMultiple( object.getClass() ) )
            {
                // Adding each single value separately
                Collection<?> values = ( Collection<?> ) object;

                for ( Object value : values )
                {
                    addAttributeTypeValue( attributeType, value, entry );
                }
            }
            else
            {
                // Adding the single value
                addAttributeTypeValue( attributeType, object, entry );
            }
        }
    }


    /**
     * Adds a value, either byte[] or another type (converted into a String 
     * via the Object.toString() method), to the attribute.
     *
     * @param attributeType the attribute type
     * @param value the value
     * @param entry the entry
     */
    private void addAttributeTypeValue( String attributeType, Object value, LdifEntry entry ) throws LdapException
    {
        // We don't store a 'null' value
        if ( value != null )
        {
            // Getting the attribute from the entry
            Attribute attribute = entry.get( attributeType );

            // If no attribute has been found, we need to create it and add it to the entry
            if ( attribute == null )
            {
                attribute = new DefaultAttribute( attributeType );
                entry.addAttribute( attribute );
            }

            // Storing the value to the attribute
            if ( value instanceof byte[] )
            {
                // Value is a byte[]
                attribute.add( ( byte[] ) value );
            }
            // Storing the boolean value in UPPERCASE (TRUE or FALSE) to the attribute
            else if ( value instanceof Boolean )
            {
                // Value is a byte[]
                attribute.add( value.toString().toUpperCase() );
            }
            else
            {
                // Value is another type of object that we store as a String
                // (There will be an automatic translation for primary types like int, long, etc.)
                attribute.add( value.toString() );
            }
        }
    }


    /**
     * Indicates the given type is multiple.
     *
     * @param clazz the class
     * @return <code>true</code> if the given is multiple, <code>false</code> if not.
     */
    private boolean isMultiple( Class<?> clazz )
    {
        return Collection.class.isAssignableFrom( clazz );
    }
}
