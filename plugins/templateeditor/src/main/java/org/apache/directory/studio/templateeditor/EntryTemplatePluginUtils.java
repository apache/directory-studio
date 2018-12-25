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
package org.apache.directory.studio.templateeditor;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.eclipse.core.runtime.Status;

import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class is a helper class for the Entry Template plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryTemplatePluginUtils
{
    /** The line separator */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" ); //$NON-NLS-1$

    /** The default schema */
    private static final Schema DEFAULT_SCHEMA = Schema.DEFAULT_SCHEMA;


    /**
     * Logs the given message and exception with the ERROR status level.
     * 
     * @param exception
     *      the exception, can be <code>null</code>
     * @param message
     *      the message
     * @param args
     *      the arguments to use when formatting the message
     */
    public static void logError( Throwable exception, String message, Object... args )
    {
        EntryTemplatePlugin.getDefault().getLog().log(
            new Status( Status.ERROR, EntryTemplatePlugin.getDefault().getBundle().getSymbolicName(), Status.OK,
                MessageFormat.format( message, args ), exception ) );
    }


    /**
     * Logs the given message and exception with the WARNING status level.
     * 
     * @param exception
     *      the exception, can be <code>null</code>
     * @param message
     *      the message
     * @param args
     *      the arguments to use when formatting the message
     */
    public static void logWarning( Throwable exception, String message, Object... args )
    {
        EntryTemplatePlugin.getDefault().getLog().log(
            new Status( Status.WARNING, EntryTemplatePlugin.getDefault().getBundle().getSymbolicName(), Status.OK,
                MessageFormat.format( message, args ), exception ) );
    }


    /**
     * Logs the given message and exception with the INFO status level.
     * 
     * @param exception
     *      the exception, can be <code>null</code>
     * @param message
     *      the message
     * @param args
     *      the arguments to use when formatting the message
     */
    public static void logInfo( Throwable exception, String message, Object... args )
    {
        EntryTemplatePlugin.getDefault().getLog().log(
            new Status( Status.INFO, EntryTemplatePlugin.getDefault().getBundle().getSymbolicName(), Status.OK,
                MessageFormat.format( message, args ), exception ) );
    }


    /**
     * Logs the given message and exception with the OK status level.
     * 
     * @param exception
     *      the exception, can be <code>null</code>
     * @param message
     *      the message
     * @param args
     *      the arguments to use when formatting the message
     */
    public static void logOk( Throwable exception, String message, Object... args )
    {
        EntryTemplatePlugin.getDefault().getLog().log(
            new Status( Status.OK, EntryTemplatePlugin.getDefault().getBundle().getSymbolicName(), Status.OK,
                MessageFormat.format( message, args ), exception ) );
    }


    /**
     * Copies a file from the given streams.
     *
     * @param source
     *      the source file
     * @param destination
     *      the destination file
     * @throws IOException
     *      if an error occurs when copying the file
     */
    public static void copyFile( File source, File destination ) throws IOException
    {
        copyFile( new FileInputStream( source ), new FileOutputStream( destination ) );
    }


    /**
     * Copies the input stream to the output stream.
     *
     * @param inputStream
     *      the input stream
     * @param outputStream
     *      the output stream
     * @throws IOException
     *      if an error occurs when copying the stream
     */
    public static void copyFile( InputStream inputStream, OutputStream outputStream ) throws IOException
    {
        byte[] buf = new byte[1024];
        int i = 0;
        while ( ( i = inputStream.read( buf ) ) != -1 )
        {
            outputStream.write( buf, 0, i );
        }
    }


    /**
     * Gets a list of templates matching the given entry.
     * 
     * @param entry
     *      the entry
     * @return
     *      a list of templates matching the given entry
     */
    public static List<Template> getMatchingTemplates( IEntry entry )
    {
        if ( entry != null )
        {
            // Looking for the highest (most specialized one) structural object class in the entry
            ObjectClass highestStructuralObjectClass = getHighestStructuralObjectClassFromEntry( entry );
            if ( highestStructuralObjectClass != null )
            {
                // We were able to determine the highest object class in the entry.

                // Based on that information, we will use the entry's schema to retrieve the list of matching templates
                return getTemplatesFromHighestObjectClass( highestStructuralObjectClass, entry.getBrowserConnection()
                    .getSchema() );
            }
            else
            {
                // We were not able to determine the highest object class in the entry.
                // This means that either the schema information we received from the server is not sufficient,
                // or the list of object classes in the entry is not complete.

                // In that case we can't use the schema information to determine the list of templates.
                // Instead we're going to gather all the templates associated with each object class description.
                return getTemplatesFromObjectClassDescriptions( entry.getObjectClassDescriptions() );
            }
        }

        return new ArrayList<Template>();
    }


    /**
     * Gets the highest (most specialized one) object class description of the given entry 
     * if it can be found, or <code>null</code> if not.
     *
     * @param entry
     *      the entry
     * @return
     *      the highest object class description of the given entry if it can be found, 
     *      or <code>null</code> if not
     */
    private static ObjectClass getHighestStructuralObjectClassFromEntry( IEntry entry )
    {
        if ( entry != null )
        {
            if ( ( entry.getBrowserConnection() != null ) && ( entry.getBrowserConnection().getSchema() != null ) )
            {
                // Getting the schema from the entry
                Schema schema = entry.getBrowserConnection().getSchema();

                // Getting object class descriptions
                Collection<ObjectClass> objectClassDescriptions = entry.getObjectClassDescriptions();
                if ( objectClassDescriptions != null )
                {
                    // Creating the candidates list based on the initial list
                    List<ObjectClass> candidatesList = new ArrayList<ObjectClass>();

                    // Adding each structural object class description to the list
                    for ( ObjectClass objectClassDescription : objectClassDescriptions )
                    {
                        if ( objectClassDescription.getType() == ObjectClassTypeEnum.STRUCTURAL )
                        {
                            candidatesList.add( objectClassDescription );
                        }
                    }

                    // Looping on the given collection of ObjectClassDescription until the end of the list, 
                    // or until the candidates list is reduced to one.
                    Iterator<ObjectClass> iterator = objectClassDescriptions.iterator();
                    while ( ( candidatesList.size() > 1 ) && ( iterator.hasNext() ) )
                    {
                        ObjectClass ocd = iterator.next();
                        removeSuperiors( ocd, candidatesList, schema );
                    }

                    // Looking if we've found the highest object class description
                    if ( candidatesList.size() == 1 )
                    {
                        return candidatesList.get( 0 );
                    }
                }
            }
        }

        return null;
    }


    /**
     * Recursively removes superiors of the given object class description from the list.
     *
     * @param ocd
     *      the object class description
     * @param ocdList
     *      the list of object class description
     * @param schema
     *      the schema
     */
    private static void removeSuperiors( ObjectClass ocd, List<ObjectClass> ocdList, Schema schema )
    {
        if ( ocd != null )
        {
            for ( String superior : ocd.getSuperiorOids() )
            {
                // Getting the ObjectClassDescription associated with the superior
                ObjectClass superiorOcd = getObjectClass( superior, schema );

                // Removing it from the list and recursively removing its superiors
                ocdList.remove( superiorOcd );
                removeSuperiors( superiorOcd, ocdList, schema );
            }
        }
    }


    /**
     * Gets the list of matching templates for the given object class description.
     * <p>
     * To do this, we're using a "Breadth First Search" algorithm to go through all
     * the superiors (and the superiors of these superiors, etc.).
     *
     * @param objectClassDescription
     *      the object class description
     * @param schema
     *      the associated schema
     * @return
     *      the list of matching templates for the given object class description
     */
    private static List<Template> getTemplatesFromHighestObjectClass( ObjectClass objectClassDescription,
        Schema schema )
    {
        // Creating a set to hold all the matching templates
        List<Template> matchingTemplates = new ArrayList<Template>();

        // Getting the templates manager
        TemplatesManager manager = EntryTemplatePlugin.getDefault().getTemplatesManager();

        // Getting the list of all the available templates
        Template[] templates = manager.getTemplates();

        // Creating a MultiValueMap that holds the templates ordered by ObjectClassDescription object
        MultiValuedMap<ObjectClass, Template> templatesByOcd = new ArrayListValuedHashMap<>();

        // Populating this map
        for ( Template template : templates )
        {
            templatesByOcd.put( getObjectClass( template.getStructuralObjectClass(), schema ), template );
        }

        // Initializing the LIFO queue with the highest ObjectClassDescription object
        LinkedList<ObjectClass> ocdQueue = new LinkedList<ObjectClass>();
        ocdQueue.add( objectClassDescription );

        // Looking if we need to test a new ObjectClassDescription object
        while ( !ocdQueue.isEmpty() )
        {
            // Dequeuing the last object for testing
            ObjectClass currentOcd = ocdQueue.removeLast();

            // Adds the templates for the current object class description to the list of matching templates
            addTemplatesForObjectClassDescription( currentOcd, matchingTemplates, manager );

            // Adding each superior object to the queue
            List<String> currentOcdSups = currentOcd.getSuperiorOids();
            if ( currentOcdSups != null )
            {
                for ( String currentOcdSup : currentOcdSups )
                {
                    ocdQueue.addFirst( getObjectClass( currentOcdSup, schema ) );
                }
            }
        }

        return matchingTemplates;
    }


    /**
     * Gets the list of matching templates for the given object class descriptions.
     *
     * @param objectClasses
     *      the object classes
     * @return
     *      the list of matching templates for the given object class description
     */
    private static List<Template> getTemplatesFromObjectClassDescriptions(
        Collection<ObjectClass> objectClasses )
    {
        if ( objectClasses != null )
        {
            // Creating a set to hold all the matching templates
            List<Template> matchingTemplates = new ArrayList<Template>();

            // Getting the templates manager
            TemplatesManager manager = EntryTemplatePlugin.getDefault().getTemplatesManager();

            for ( ObjectClass objectClassDescription : objectClasses )
            {
                // Adds the templates for the current object class description to the list of matching templates
                addTemplatesForObjectClassDescription( objectClassDescription, matchingTemplates, manager );
            }

            return matchingTemplates;
        }

        return null;
    }


    /**
     * Adds the templates found for the given object class description to the given templates set.
     *
     * @param ocd
     *      the object class description
     * @param matchingTemplates
     *      the list of matching templates
     * @param manager
     *      the manager
     */
    private static void addTemplatesForObjectClassDescription( ObjectClass ocd,
        List<Template> matchingTemplates, TemplatesManager manager )
    {
        // Creating a list of containing the names and OID of the current ObjectClassDescription object
        List<String> namesAndOid = new ArrayList<String>();
        for ( String name : ocd.getNames() )
        {
            namesAndOid.add( name );
        }
        String currentOcdOid = ocd.getOid();
        if ( ( currentOcdOid != null ) && ( !"".equals( currentOcdOid ) ) ) //$NON-NLS-1$
        {
            namesAndOid.add( currentOcdOid );
        }

        // Looping on the names and OID to find all corresponding templates
        for ( String nameOrOid : namesAndOid )
        {
            // Getting the default template and complete list of templates for the given name or OID
            Template currentOcdDefaultTemplate = manager.getDefaultTemplate( nameOrOid );
            List<Template> currentOcdTemplates = manager.getTemplatesByObjectClass( nameOrOid );

            // Adding the default template
            if ( currentOcdDefaultTemplate != null )
            {
                if ( !matchingTemplates.contains( currentOcdDefaultTemplate ) )
                {
                    matchingTemplates.add( currentOcdDefaultTemplate );
                }
            }

            // Adding the other templates
            if ( currentOcdTemplates != null )
            {
                for ( Template template : currentOcdTemplates )
                {
                    // Adding the template only if it is different from the default one (which is already added)
                    if ( ( !template.equals( currentOcdDefaultTemplate ) ) && ( manager.isEnabled( template ) )
                        && ( !matchingTemplates.contains( template ) ) )
                    {
                        matchingTemplates.add( template );
                    }
                }
            }
        }
    }


    /**
     * Gets the object class description of the given name or OID found in the default schema.
     * <p>
     * If no object class description is found in the default schema, a new object class description
     * is created with the given name or OID and returned.
     *
     * @param nameOrOid
     *      the name or OID
     * @return
     *      the object class description of the given name or OID found in the default schema,
     *      or a new object class description created with the given name or OID if none can be found
     */
    public static ObjectClass getObjectClassDescriptionFromDefaultSchema( String nameOrOid )
    {
        return getObjectClass( nameOrOid, DEFAULT_SCHEMA );
    }


    /**
     * Gets the object class description of the given name or OID found in the given schema.
     * <p>
     * If no object class description is found in the given schema, a new object class description
     * is created with the given name or OID and returned.
     *
     * @param nameOrOid
     *      the name or OID
     * @param schema
     *      the schema
     * @return
     *      the object class description of the given name or OID found in the given schema,
     *      or a new object class description created with the given name or OID if none can be found
     */
    private static ObjectClass getObjectClass( String nameOrOid, Schema schema )
    {
        ObjectClass ocd = null;

        // Looking for the object class description in the given schema
        if ( schema != null )
        {
            ocd = schema.getObjectClassDescription( nameOrOid );
        }

        // Creating a new object class description if none could be found in the given schema
        if ( ocd == null )
        {
            ocd = new ObjectClass( null );
            ocd.setNames( Arrays.asList( new String[]
                { nameOrOid.toLowerCase() } ) );
        }

        return ocd;
    }
}
