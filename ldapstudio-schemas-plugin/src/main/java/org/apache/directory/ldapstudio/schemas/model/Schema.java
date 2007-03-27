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

package org.apache.directory.ldapstudio.schemas.model;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.io.SchemaParser;
import org.apache.directory.ldapstudio.schemas.io.SchemaWriter;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class Schema implements SchemaElementListener
{
    private static Logger logger = Logger.getLogger( Schema.class );

    private String name;
    private URL url;
    //the schema elements stored in highly optimised and strongly typed
    //hash tables (stored by name).
    private Hashtable<String, ObjectClass> objectClassTable;
    private Hashtable<String, AttributeType> attributeTypeTable;
    private ArrayList<SchemaListener> listeners;
    //we change this to true if the schema has been modified since last save
    private boolean hasBeenModified = false;

    /**
     * coreSchema = a schema that can't be deleted, it's a core schema of the ApacheDS server
     * userSchema = any other schema
     */
    public enum SchemaType
    {
        userSchema, coreSchema
    }

    /**
     * The type of this schema
     */
    public SchemaType type;


    /******************************************
     *                 Utility                *
     ******************************************/

    /**
     * Gets the filename WITHOUT EXTENSION of the schema designated by this url
     * @param url url pointing to a .schema file
     * @return the filename or null if bad url
     */
    public static String URLtoFileName( URL url )
    {
        try
        {
            //It's time for sun to provide a standard method to access a file's name !
            String separator = "/"; //$NON-NLS-1$
            //if it's a ressource located in a bundle, we use slashes
            if ( url.getProtocol().equals( "bundleresource" ) ) //$NON-NLS-1$
                separator = "/"; //$NON-NLS-1$
            else
            {
                //if not, let's find this platform specific separator !
                separator = File.separator;

                //no wait ! If it's a backslash, the regex motor will explode. We
                //have to put not 2, not 3 but 4 backslashes !! That's right !
                if ( separator.equals( "\\" ) ) { //$NON-NLS-1$
                    separator = "\\\\"; //$NON-NLS-1$
                }
            }

            String path = url.getPath();
            String[] splFileName = path.split( separator );
            String fileNoPath = splFileName[splFileName.length - 1];

            if ( fileNoPath.endsWith( ".schema" ) ) //$NON-NLS-1$
            {
                String[] fileName = fileNoPath.split( "\\." ); //$NON-NLS-1$
                return fileName[0];
            }
        }
        catch ( Exception e )
        {
            logger.debug( "error when converting " + url + " to filename" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return null;
    }


    /**
     * Converts a local path in a URL object
     * @param path the local path
     * @return the url corresponding to the path
     * @throws SchemaCreationException if error with the given path
     */
    public static URL localPathToURL( String path ) throws SchemaCreationException
    {
        URL tempURL = null;
        try
        {
            tempURL = new URL( "file", "localhost", -1, path ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch ( MalformedURLException e )
        {
            throw new SchemaCreationException( "malformed path:" + path, e ); //$NON-NLS-1$
        }
        return tempURL;

    }


    /**
     * Tests if a given file is a schema file, it does not read the content of the schema
     * but rather tries to determine the extension of the file
     * @param url the input file
     * @return returns true if its a .schema file, if not it returns false
     */
    private static boolean isASchemaFile( URL url )
    {
        return URLtoFileName( url ) != null;
    }


    /******************************************
     *              Constructors              *
     ******************************************/

    /**
     * Creates a new user schema of the specified name
     * @param name the name of the schema
     */
    public Schema( String name )
    {
        this( name, null, SchemaType.userSchema );
    }


    /**
     * Creates a new schema of the specified name and type
     * @param type the type of the schema
     * @param name the name of the schema
     */
    public Schema( SchemaType type, String name )
    {
        this( name, null, type );
    }


    /**
     * Creates a new schema object loaded from a .schema file
     * @param path the path to the .schema file
     * @param type the type of the schema
     * @throws SchemaCreationException if error during parsing of the .schema file
     */
    public Schema( String path, SchemaType type ) throws SchemaCreationException
    {
        this( localPathToURL( path ), type );
    }


    /**
     * Creates a new schema object loaded from a .schema file
     * @param url the URL to the .schema file
     * @param type the type of the schema
     * @throws SchemaCreationException if error during parsing of the .schema file
     */
    public Schema( URL url, SchemaType type ) throws SchemaCreationException
    {
        this( URLtoFileName( url ), url, type );

        //we only load .schema files
        if ( !isASchemaFile( url ) )
            throw new SchemaCreationException( "not a .schema file: " + url, null ); //$NON-NLS-1$

        //launch parsing of the .schema file right now
        try
        {
            read();
        }
        catch ( IOException e )
        {
            throw new SchemaCreationException( "error opening " + url.toString(), e ); //$NON-NLS-1$
        }
        catch ( ParseException e )
        {
            throw new SchemaCreationException( "error during parsing of " + url.toString(), e ); //$NON-NLS-1$
        }
    }


    /**
     * General constructor (called by all the other constructors, ensure correct 
     * initialization of all the instance variables)
     * @param name the name of the schema
     * @param url the url of the schema
     * @param type the type of the schema
     */
    private Schema( String name, URL url, SchemaType type )
    {
        this.name = name;
        this.url = url;
        this.type = type;

        objectClassTable = new Hashtable<String, ObjectClass>();
        attributeTypeTable = new Hashtable<String, AttributeType>();
        listeners = new ArrayList<SchemaListener>();

        //we created a new schema, so it has to be saved later on
        this.modified();
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the name of the schema
     */
    public String getName()
    {
        return name;
    }


    /**
     * @return the url of the schema if it has been specified
     */
    public URL getURL()
    {
        return url;
    }


    /**
     * Accessor to the objectClasses defined by this schema
     * @return as an (name, objectClass) hashtable 
     */
    public Hashtable<String, ObjectClass> getObjectClassesAsHashTable()
    {
        return objectClassTable;
    }


    /**
     * Accessor to the attributeTypes defined by this schema
     * @return as an (name, attributeType) hashtable
     */
    public Hashtable<String, AttributeType> getAttributeTypesAsHashTable()
    {
        return attributeTypeTable;
    }


    /**
     * Accessor to the objectClasses defined by this schema
     * @return as an array
     */
    public ObjectClass[] getObjectClassesAsArray()
    {
        Set<ObjectClass> set = new HashSet<ObjectClass>();
        set.addAll( objectClassTable.values() );
        return set.toArray( new ObjectClass[0] );
    }


    /**
     * Accessor to the attributeTypes defined by this schema
     * @return as an array
     */
    public AttributeType[] getAttributeTypesAsArray()
    {
        Set<AttributeType> set = new HashSet<AttributeType>();
        set.addAll( attributeTypeTable.values() );
        return set.toArray( new AttributeType[0] );
    }


    /******************************************
     *                  Logic                 *
     ******************************************/

    /**
     * Checks if the schema has been modified since last save
     * @return true if modified
     */
    public boolean hasBeenModified()
    {
        return hasBeenModified;
    }


    /**
     * Checks if the schema has pending modifications in editors that have not
     * been already applied
     * @return true if the schema has pending modifications
     */
    public boolean hasPendingModification()
    {
        ObjectClass[] OCs = getObjectClassesAsArray();
        for ( ObjectClass objectClass : OCs )
        {
            if ( objectClass.hasPendingModifications() )
            {
                return true;
            }
        }

        AttributeType[] ATs = getAttributeTypesAsArray();
        for ( AttributeType attributeType : ATs )
        {
            if ( attributeType.hasPendingModifications() )
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Apply the pending modifications to the model (this instance)
     */
    public void applyPendingModifications()
    {
        ObjectClass[] OCs = getObjectClassesAsArray();
        for ( ObjectClass objectClass : OCs )
        {
            if ( objectClass.hasPendingModifications() )
            {
                objectClass.applyPendingModifications();
            }
        }

        AttributeType[] ATs = getAttributeTypesAsArray();
        for ( AttributeType attributeType : ATs )
        {
            if ( attributeType.hasPendingModifications() )
            {
                attributeType.applyPendingModifications();
            }
        }
    }


    /**
     * Close the editors associated to this schema WITHOUT applying the
     * modifications
     */
    public void closeAssociatedEditors()
    {
        ObjectClass[] OCs = getObjectClassesAsArray();
        for ( ObjectClass objectClass : OCs )
        {
            objectClass.closeAssociatedEditor();
        }

        AttributeType[] ATs = getAttributeTypesAsArray();
        for ( AttributeType attributeType : ATs )
        {
            attributeType.closeAssociatedEditor();
        }
    }


    /**
     * Use this method to indicate that the schema has been modified.
     * Surgeons warning: internal use only
     */
    private void modified()
    {
        this.hasBeenModified = true;
    }


    /**
     * Use this method to indicate that the schema has been saved.
     * Surgeons warning: internal use only
     */
    private void saved()
    {
        this.hasBeenModified = false;
    }


    /**
     * Adds the specified objectClass to the schema (overwriting the previous associations)
     * @param oc the objectClass
     */
    public void addObjectClass( ObjectClass oc )
    {
        for ( String alias : oc.getNames() )
            objectClassTable.put( alias, oc );
        oc.addListener( this );
        this.modified();
        notifyChanged( LDAPModelEvent.Reason.OCAdded, null, oc );
    }


    /**
     * Adds the specified attributeType to the schema (overwriting the previous associations)
     * @param at the attributeType
     */
    public void addAttributeType( AttributeType at )
    {
        for ( String alias : at.getNames() )
            attributeTypeTable.put( alias, at );
        at.addListener( this );
        this.modified();
        notifyChanged( LDAPModelEvent.Reason.ATAdded, null, at );
    }


    /**
     * Removes the specified objectClass from the schema
     * @param oc the objectClass
     */
    public void removeObjectClass( ObjectClass oc )
    {
        for ( String alias : oc.getNames() )
            objectClassTable.remove( alias );
        oc.removeListener( this );
        this.modified();
        notifyChanged( LDAPModelEvent.Reason.OCRemoved, oc, null );
    }


    /**
     * Removes the specified attributeType from the schema
     * @param at the attributeType
     */
    public void removeAttributeType( AttributeType at )
    {
        for ( String alias : at.getNames() )
            attributeTypeTable.remove( alias );
        at.removeListener( this );
        this.modified();
        notifyChanged( LDAPModelEvent.Reason.ATRemoved, at, null );
    }


    /******************************************
     *                  I/O                   *
     ******************************************/

    /**
     * Read the schema from the already specified .schema file
     * @throws ParseException if error during parsing of the .schema file
     * @throws IOException if error opening the .schema file
     *
     */
    public void read() throws IOException, ParseException
    {
        SchemaParser parser = null;
        parser = SchemaParser.parserFromURL( url );

        if ( parser == null )
            throw new FileNotFoundException( "Schema model object: no path or url specified !" ); //$NON-NLS-1$

        parser.parse();

        ObjectClassLiteral[] objectClasses = parser.getObjectClasses();
        AttributeTypeLiteral[] attributeTypes = parser.getAttributeTypes();

        for ( AttributeTypeLiteral literal : attributeTypes )
        {
            AttributeType AT = new AttributeType( literal, this );
            AT.addListener( this );
            for ( String alias : literal.getNames() )
                attributeTypeTable.put( alias, AT );
        }

        for ( ObjectClassLiteral literal : objectClasses )
        {
            ObjectClass OC = new ObjectClass( literal, this );
            OC.addListener( this );
            for ( String alias : literal.getNames() )
                objectClassTable.put( alias, OC );
        }

        //this schema has been loaded from a file, so we can consider we are
        //synchronised with the filesystem
        this.saved();
    }


    /**
     * Save the schema on disk, do not ask for confirmation
     * @throws Exception if error during writting of the file
     */
    public void save() throws Exception
    {
        save( false );
    }


    /**
     * Save the schema on disk
     * @param askForConfirmation if true, will ask form confirmation before saving
     * @throws Exception if error during writting of the file
     */
    public void save( boolean askForConfirmation ) throws Exception
    {
        if ( this.type == SchemaType.coreSchema )
            return;

        if ( this.hasPendingModification() )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.YES | SWT.NO | SWT.ICON_QUESTION );
            messageBox
                .setMessage( Messages.getString( "Schema.The_schema" ) + this.getName() + Messages.getString( "Schema.Has_pending_modifications_in_editors_Do_you_want_to_apply_them" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( messageBox.open() == SWT.YES )
            {
                this.applyPendingModifications();
            }
            else
            {
                this.closeAssociatedEditors();
            }
        }

        if ( !this.hasBeenModified() )
            return;

        if ( askForConfirmation )
        {
            MessageBox messageBox = new MessageBox( new Shell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION );
            messageBox
                .setMessage( Messages.getString( "Schema.The_schema" ) + this.getName() + Messages.getString( "Schema.Has_been_modified_Do_you_want_to_save_it" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( messageBox.open() != SWT.YES )
            {
                return;
            }
        }

        String savePath = null;

        if ( this.url == null )
        {
            FileDialog fd = new FileDialog( new Shell(), SWT.SAVE );
            fd.setText( Messages.getString( "Schema.Save_this_schema" ) + this.getName() ); //$NON-NLS-1$
            fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
            fd.setFileName( this.name + ".schema" ); //$NON-NLS-1$
            fd.setFilterExtensions( new String[]
                { "*.schema", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
            fd.setFilterNames( new String[]
                { Messages.getString( "Schema.Schema_files" ), Messages.getString( "Schema.All_files" ) } ); //$NON-NLS-1$ //$NON-NLS-2$
            savePath = fd.open();
            //we now have a specific location to save this schema in the future
            if ( savePath != null )
                this.url = localPathToURL( savePath );
        }
        else
            savePath = url.getPath();

        if ( savePath != null )
        {
            write( savePath );
            //when we have been written, we are synchronised with the filesystem
            this.saved();
            notifyChanged( LDAPModelEvent.Reason.SchemaSaved, this, null );
        }
    }


    /**
     * Save the schema on disk to a specific file 
     * @throws Exception if error during writting of the file
     */
    public void saveas() throws Exception
    {
        if ( this.hasPendingModification() )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.YES | SWT.NO | SWT.ICON_QUESTION );
            messageBox
                .setMessage( Messages.getString( "Schema.The_schema" ) + this.getName() + Messages.getString( "Schema.Has_pending_modifications_in_editors_Do_you_want_to_apply_them" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( messageBox.open() == SWT.YES )
            {
                this.applyPendingModifications();
            }
        }

        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
        fd.setText( Messages.getString( "Schema.Save_this_schema" ) + this.getName() ); //$NON-NLS-1$
        fd.setFilterPath( System.getProperty( "user.home" ) ); //$NON-NLS-1$
        fd.setFileName( this.name + ".schema" ); //$NON-NLS-1$
        fd.setFilterExtensions( new String[]
            { "*.schema", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
        fd.setFilterNames( new String[]
            { Messages.getString( "Schema.Schema_files" ), Messages.getString( "Schema.All_files" ) } ); //$NON-NLS-1$ //$NON-NLS-2$
        String savePath = fd.open();
        //check if cancel has been pressed
        if ( savePath != null )
        {
            URL newURL = localPathToURL( savePath );
            String newName = URLtoFileName( newURL );
            //if it's a bad url (no .schema, bad path) newName will be null
            if ( newName != null )
            {

                if ( SchemaPool.getInstance().getSchema( newName ) != null )
                {
                    MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), SWT.OK | SWT.ICON_ERROR );
                    messageBox.setMessage( Messages
                        .getString( "Schema.A_schema_of_the_same_name_is_already_loaded_in_the_pool" ) ); //$NON-NLS-1$
                    messageBox.open();
                    return;
                }
                //if everything is ok, we update the current instance
                this.name = newName;
                this.url = newURL;

                write( savePath );
                //when we have been written, we are synchronised with the filesystem
                this.saved();
                notifyChanged( LDAPModelEvent.Reason.SchemaSaved, this, null );
            }
        }
    }


    private void write( String path ) throws Exception
    {
        if ( path != null && path != "" ) { //$NON-NLS-1$
            SchemaWriter writer = new SchemaWriter();
            writer.write( this, path );
        }
    }


    /******************************************
     *            Events emmiting             *
     ******************************************/

    public void addListener( SchemaListener listener )
    {
        if ( !listeners.contains( listener ) )
            listeners.add( listener );
    }


    public void removeListener( SchemaListener listener )
    {
        listeners.remove( listener );
    }


    private void notifyChanged( LDAPModelEvent.Reason reason, Object oldValue, Object newValue )
    {
        for ( SchemaListener listener : listeners )
        {
            try
            {
                if ( ( oldValue instanceof ObjectClass ) || ( newValue instanceof ObjectClass ) )
                {
                    listener.schemaChanged( this, new LDAPModelEvent( reason, ( ObjectClass ) oldValue,
                        ( ObjectClass ) newValue ) );
                }
                else if ( ( oldValue instanceof AttributeType ) || ( newValue instanceof AttributeType ) )
                {
                    listener.schemaChanged( this, new LDAPModelEvent( reason, ( AttributeType ) oldValue,
                        ( AttributeType ) newValue ) );
                }
                else
                {
                    listener.schemaChanged( this, new LDAPModelEvent( reason ) );
                }
            }
            catch ( Exception e )
            {
                logger.debug( "error when notifying listener: " + listener ); //$NON-NLS-1$
            }
        }
    }


    /******************************************
     *     Schema Element Listener Impl       *
     ******************************************/

    public void schemaElementChanged( SchemaElement originatingSchemaElement, LDAPModelEvent e )
    {
        //if a binded element has been modified we consider that we have been modified
        this.modified();

        //then we notify our listeners that we have been modified
        for ( SchemaListener listener : listeners )
        {
            listener.schemaChanged( this, e );
        }
    }
}
