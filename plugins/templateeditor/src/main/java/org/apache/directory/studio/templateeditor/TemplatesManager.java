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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import org.apache.directory.studio.templateeditor.model.ExtensionPointTemplate;
import org.apache.directory.studio.templateeditor.model.FileTemplate;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIO;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIOException;


/**
 * This class is used to manage the templates.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplatesManager
{
    /** The preference delimiter used for default and disabled templates */
    private static String PREFERENCE_DELIMITER = ";"; //$NON-NLS-1$

    /** The preference sub delimiter used for default templates */
    private static String PREFERENCE_SUB_DELIMITER = ":"; //$NON-NLS-1$

    /** The plugin's preference store */
    private IPreferenceStore preferenceStore;

    /** The list containing all the templates */
    private List<Template> templatesList = new ArrayList<Template>();

    /** The maps containing all the templates by their id */
    private Map<String, Template> templatesByIdMap = new HashMap<String, Template>();

    /** The maps containing all the templates by ObjectClassDescription */
    private MultiValuedMap<ObjectClass, Template> templatesByStructuralObjectClassMap = new ArrayListValuedHashMap<>();

    /** The list containing *only* the IDs of the disabled templates */
    private List<String> disabledTemplatesList = new ArrayList<String>();

    /** The map containing the default templates */
    private Map<ObjectClass, String> defaultTemplatesMap = new HashMap<ObjectClass, String>();

    /** The list of listeners */
    private List<TemplatesManagerListener> listeners = new ArrayList<TemplatesManagerListener>();


    /**
     * Creates a new instance of TemplatesManager.
     *
     * @param preferenceStore
     *      the plugin's preference store
     */
    public TemplatesManager( IPreferenceStore preferenceStore )
    {
        this.preferenceStore = preferenceStore;

        loadDefaultTemplates();
        loadDisabledTemplates();
        loadTemplates();
        setDefaultTemplates();
    }


    /**
     * Adds a listener.
     *
     * @param listener
     *      the listener
     * @return
     *      <code>true</code> (as per the general contract of the
     *      <code>Collection.add</code> method).
     */
    public boolean addListener( TemplatesManagerListener listener )
    {
        return listeners.add( listener );
    }


    /**
     * Removes a listener.
     *
     * @param listener
     *      the listener
     * @return
     *      <code>true</code> if this templates manager contained 
     *      the specified listener.
     */
    public boolean removeListener( TemplatesManagerListener listener )
    {
        return listeners.remove( listener );
    }


    /**
     * Fires a "fireTemplateAdded" event to all the listeners.
     *
     * @param template
     *      the added template
     */
    private void fireTemplateAdded( Template template )
    {
        for ( TemplatesManagerListener listener : listeners.toArray( new TemplatesManagerListener[0] ) )
        {
            listener.templateAdded( template );
        }
    }


    /**
     * Fires a "templateRemoved" event to all the listeners.
     *
     * @param template
     *      the removed template
     */
    private void fireTemplateRemoved( Template template )
    {
        for ( TemplatesManagerListener listener : listeners.toArray( new TemplatesManagerListener[0] ) )
        {
            listener.templateRemoved( template );
        }
    }


    /**
     * Fires a "templateEnabled" event to all the listeners.
     *
     * @param template
     *      the enabled template
     */
    private void fireTemplateEnabled( Template template )
    {
        for ( TemplatesManagerListener listener : listeners.toArray( new TemplatesManagerListener[0] ) )
        {
            listener.templateEnabled( template );
        }
    }


    /**
    * Fires a "templateDisabled" event to all the listeners.
    *
    * @param template
    *      the disabled template
    */
    private void fireTemplateDisabled( Template template )
    {
        for ( TemplatesManagerListener listener : listeners.toArray( new TemplatesManagerListener[0] ) )
        {
            listener.templateDisabled( template );
        }
    }


    /**
     * Loads the templates
     */
    private void loadTemplates()
    {
        // Loading the templates added using the extension point
        loadExtensionPointTemplates();

        // Loading the templates added via files on the disk (added by the user)
        loadFileTemplates();
    }


    /**
     * Loads the templates added using the extension point.
     */
    private void loadExtensionPointTemplates()
    {
        // Getting the extension point
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
            "org.apache.directory.studio.templateeditor.templates" ); //$NON-NLS-1$

        // Getting all the extensions
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();
        if ( members != null )
        {
            // For each extension: load the template
            for ( int m = 0; m < members.length; m++ )
            {
                IConfigurationElement member = members[m];

                // Getting the URL of the file associated with the extension
                String contributorName = member.getContributor().getName();
                String filePathInPlugin = member.getAttribute( "file" ); //$NON-NLS-1$
                URL fileUrl = Platform.getBundle( contributorName ).getResource( filePathInPlugin );

                // Checking if the URL is null
                if ( filePathInPlugin == null )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( new NullPointerException(), Messages
                        .getString( "TemplatesManager.AnErrorOccurredWhenParsingTheTemplate3Params" ), contributorName, //$NON-NLS-1$
                        filePathInPlugin, Messages.getString( "TemplatesManager.URLCreatedForTheTemplateIsNull" ) ); //$NON-NLS-1$
                }

                // Parsing the template and adding it to the templates list
                try
                {
                    InputStream is = fileUrl.openStream();

                    ExtensionPointTemplate template = TemplateIO.readAsExtensionPointTemplate( is );

                    templatesList.add( template );
                    templatesByIdMap.put( template.getId(), template );
                    templatesByStructuralObjectClassMap.put( EntryTemplatePluginUtils
                        .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ), template );

                    is.close();
                }
                catch ( TemplateIOException e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, Messages
                        .getString( "TemplatesManager.AnErrorOccurredWhenParsingTheTemplate3Params" ), //$NON-NLS-1$
                        contributorName, filePathInPlugin, e.getMessage() );
                }
                catch ( IOException e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, Messages
                        .getString( "TemplatesManager.AnErrorOccurredWhenParsingTheTemplate3Params" ), contributorName, //$NON-NLS-1$
                        filePathInPlugin, e.getMessage() );
                }
            }
        }
    }


    /**
     * Loads the templates added via files on the disk (added by the user).
     */
    private void loadFileTemplates()
    {
        // Getting the templates folder
        File templatesFolder = getTemplatesFolder().toFile();

        // If the templates folder does not exist, we exit
        if ( !templatesFolder.exists() )
        {
            return;
        }

        // Loading the templates contained in the templates folder
        String[] templateNames = templatesFolder.list( new FilenameFilter()
        {
            public boolean accept( File dir, String name )
            {
                return name.endsWith( ".xml" ); //$NON-NLS-1$
            }
        } );

        // If there are no templates available, we exit
        if ( ( templateNames == null ) || ( templateNames.length == 0 ) )
        {
            return;
        }

        // Loading each template
        for ( String templateName : templateNames )
        {
            // Creating the template file
            File templateFile = new File( templatesFolder, templateName );

            // Parsing the template and adding it to the templates list
            try
            {
                InputStream is = new FileInputStream( templateFile );

                FileTemplate template = TemplateIO.readAsFileTemplate( is );
                templatesList.add( template );
                templatesByIdMap.put( template.getId(), template );
                templatesByStructuralObjectClassMap.put( EntryTemplatePluginUtils
                    .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ), template );

                is.close();
            }
            catch ( TemplateIOException e )
            {
                // Logging the error
                EntryTemplatePluginUtils.logError( e, Messages
                    .getString( "TemplatesManager.AnErrorOccurredWhenParsingTheTemplate2Params" ), //$NON-NLS-1$
                    templateFile.getAbsolutePath(), e.getMessage() );
            }
            catch ( IOException e )
            {
                // Logging the error
                EntryTemplatePluginUtils.logError( e, Messages
                    .getString( "TemplatesManager.AnErrorOccurredWhenParsingTheTemplate2Params" ), //$NON-NLS-1$
                    templateFile.getAbsolutePath(), e.getMessage() );
            }
        }
    }


    /**
     * Adds a template from a file on the disk.
     *
     * @param templateFile
     *      the template file
     * @return
     *      <code>true</code> if the template file has been successfully added,
     *      <code>false</code> if the template file has not been added
     */
    public boolean addTemplate( File templateFile )
    {
        // Getting the file template
        FileTemplate fileTemplate = getFileTemplate( templateFile );
        if ( fileTemplate == null )
        {
            // If the file is not valid, we simply return
            return false;
        }

        // Verifying if a template with a similar ID does not already exist
        if ( templatesByIdMap.containsKey( fileTemplate.getId() ) )
        {
            // Logging the error
            EntryTemplatePluginUtils.logError( null, Messages
                .getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseATemplateWithSameIDAlreadyExist" ), //$NON-NLS-1$
                templateFile.getAbsolutePath() );
            return false;
        }

        // Verifying the folder containing the templates already exists
        // If not we create it
        File templatesFolder = getTemplatesFolder().toFile();
        if ( !templatesFolder.exists() )
        {
            // The folder does not exist, we need to create it.
            templatesFolder.mkdirs();
        }

        // Copying the template in the plugin's folder
        try
        {
            // Creating the file object where the template will be saved
            File destinationFile = getTemplatesFolder().append( fileTemplate.getId() + ".xml" ).toFile(); //$NON-NLS-1$

            // Checking if the file does not already exist
            if ( destinationFile.exists() )
            {
                // Logging the error
                EntryTemplatePluginUtils
                    .logError(
                        null,
                        Messages
                            .getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseATemplateWithSameIDAlreadyExist" ), //$NON-NLS-1$
                        templateFile.getAbsolutePath() );
                return false;
            }

            // Copying the file
            EntryTemplatePluginUtils.copyFile( templateFile, destinationFile );
        }
        catch ( IOException e )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateFileCouldNotBeCopiedToThePluginsFolder" ), templateFile.getAbsolutePath() ); //$NON-NLS-1$
            return false;
        }

        // Adding the template
        templatesList.add( fileTemplate );
        templatesByIdMap.put( fileTemplate.getId(), fileTemplate );
        templatesByStructuralObjectClassMap.put( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( fileTemplate.getStructuralObjectClass() ), fileTemplate );

        // Firing the event
        fireTemplateAdded( fileTemplate );

        return true;
    }


    /**
     * Get the file template associate with the template file.
     *
     * @param templateFile
     *      the template file
     * @return
     *      the associated file template
     */
    private FileTemplate getFileTemplate( File templateFile )
    {
        // Checking if the file exists
        if ( !templateFile.exists() )
        {
            // Logging the error
            EntryTemplatePluginUtils.logError( null, Messages
                .getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseItDoesNotExist" ), templateFile //$NON-NLS-1$
                .getAbsolutePath() );
            return null;
        }

        // Checking if the file is readable
        if ( !templateFile.canRead() )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseItCantBeRead" ), templateFile.getAbsolutePath() ); //$NON-NLS-1$
            return null;
        }

        // Trying to parse the template file
        FileTemplate fileTemplate = null;
        try
        {
            FileInputStream fis = new FileInputStream( templateFile );
            fileTemplate = TemplateIO.readAsFileTemplate( fis );
        }
        catch ( FileNotFoundException e )
        {
            // Logging the error
            EntryTemplatePluginUtils.logError( e, Messages
                .getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseOfTheFollowingError" ), templateFile //$NON-NLS-1$
                .getAbsolutePath(), e.getMessage() );
            return null;
        }
        catch ( TemplateIOException e )
        {
            // Logging the error
            EntryTemplatePluginUtils.logError( e, Messages
                .getString( "TemplatesManager.TheTemplateFileCouldNotBeAddedBecauseOfTheFollowingError" ), templateFile //$NON-NLS-1$
                .getAbsolutePath(), e.getMessage() );
            return null;
        }

        // Everything went fine, the file is valid
        return fileTemplate;
    }


    /**
     * Removes a template.
     * 
     * @param fileTemplate
     *      the file template to remove
     * @return
     *      <code>true</code> if the file template has been successfully removed,
     *      <code>false</code> if the template file has not been removed
     */
    public boolean removeTemplate( FileTemplate fileTemplate )
    {
        // Checking if the file template is null
        if ( fileTemplate == null )
        {
            return false;
        }

        // Checking if the file template exists in the templates set
        if ( !templatesList.contains( fileTemplate ) )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateCouldNotBeRemovedBecauseOfTheFollowingError" ) //$NON-NLS-1$
                        + Messages.getString( "TemplatesManager.TheTemplateDoesNotExistInTheTemplateManager" ), fileTemplate.getTitle(), fileTemplate //$NON-NLS-1$
                        .getId() );
            return false;
        }

        // Creating the file object associated with the template
        File templateFile = getTemplatesFolder().append( fileTemplate.getId() + ".xml" ).toFile(); //$NON-NLS-1$

        // Checking if the file exists
        if ( !templateFile.exists() )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateCouldNotBeRemovedBecauseOfTheFollowingError" ) //$NON-NLS-1$
                        + Messages.getString( "TemplatesManager.TheFileAssociatedWithTheTemplateCouldNotBeFoundAt" ), fileTemplate.getTitle(), //$NON-NLS-1$
                    fileTemplate.getId(), templateFile.getAbsolutePath() );
            return false;
        }

        // Checking if the file can be written, and thus deleted
        if ( !templateFile.canWrite() )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateCouldNotBeRemovedBecauseOfTheFollowingError" ) //$NON-NLS-1$
                        + Messages.getString( "TemplatesManager.TheFileAssociatedWithTheTemplateCanNotBeModified" ), fileTemplate.getTitle(), //$NON-NLS-1$
                    fileTemplate.getId(), templateFile.getAbsolutePath() );
            return false;
        }

        // Deleting the file
        if ( !templateFile.delete() )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages.getString( "TemplatesManager.TheTemplateCouldNotBeRemovedBecauseOfTheFollowingError" ) //$NON-NLS-1$
                        + Messages
                            .getString( "TemplatesManager.AnErrorOccurredWhenRemovingTheFileAssociatedWithTheTemplate" ), fileTemplate //$NON-NLS-1$
                        .getTitle(), fileTemplate.getId(), templateFile.getAbsolutePath() );
            return false;
        }

        // Removing the template from the disabled templates files
        disabledTemplatesList.remove( fileTemplate );

        // Removing the template for the templates list
        templatesList.remove( fileTemplate );
        templatesByIdMap.remove( fileTemplate.getId() );
        templatesByStructuralObjectClassMap.remove( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( fileTemplate.getStructuralObjectClass() ) );

        // Firing the event
        fireTemplateRemoved( fileTemplate );

        return true;
    }


    /**
     * Gets the templates.
     *
     * @return
     *      the templates
     */
    public Template[] getTemplates()
    {
        return templatesList.toArray( new Template[0] );
    }


    /**
     * Gets the templates folder.
     *
     * @return
     *      the templates folder
     */
    private static IPath getTemplatesFolder()
    {
        return EntryTemplatePlugin.getDefault().getStateLocation().append( "templates" ); //$NON-NLS-1$
    }


    /**
     * Loads the {@link List} of disabled templates from the preference store.
     */
    private void loadDisabledTemplates()
    {
        StringTokenizer tokenizer = new StringTokenizer( preferenceStore
            .getString( EntryTemplatePluginConstants.PREF_DISABLED_TEMPLATES ), PREFERENCE_DELIMITER );
        while ( tokenizer.hasMoreTokens() )
        {
            disabledTemplatesList.add( tokenizer.nextToken() );
        }
    }


    /**
     * Saves the {@link List} of disabled templates to the preference store.
     */
    private void saveDisabledTemplates()
    {
        StringBuffer sb = new StringBuffer();
        for ( String disabledTemplateId : disabledTemplatesList )
        {
            sb.append( disabledTemplateId );
            sb.append( PREFERENCE_DELIMITER );
        }
        preferenceStore.setValue( EntryTemplatePluginConstants.PREF_DISABLED_TEMPLATES, sb.toString() );
    }


    /**
     * Enables the given template.
     *
     * @param template
     *      the template
     */
    public void enableTemplate( Template template )
    {
        if ( disabledTemplatesList.contains( template.getId() ) )
        {
            // Removing the id of the template to the list of disabled templates
            disabledTemplatesList.remove( template.getId() );

            // Saving the disabled templates list
            saveDisabledTemplates();

            // Firing the event
            fireTemplateEnabled( template );
        }
    }


    /**
     * Disables the given template.
     *
     * @param template
     *      the template
     */
    public void disableTemplate( Template template )
    {
        if ( !disabledTemplatesList.contains( template.getId() ) )
        {
            // Adding the id of the template to the list of disabled templates
            disabledTemplatesList.add( template.getId() );

            // Saving the disabled templates list
            saveDisabledTemplates();

            // Firing the event
            fireTemplateDisabled( template );
        }
    }


    /**
     * Indicates if the given template is enabled or not.
     *
     * @param template  
     *      the template
     * @return
     *      <code>true</code> if the template is enabled,
     *      <code>false</code> if the template is disabled
     */
    public boolean isEnabled( Template template )
    {
        return !disabledTemplatesList.contains( template.getId() );
    }


    /**
     * Loads the {@link Map} of default templates from the preference store.
     */
    private void loadDefaultTemplates()
    {
        // Getting each default set
        StringTokenizer tokenizer = new StringTokenizer( preferenceStore
            .getString( EntryTemplatePluginConstants.PREF_DEFAULT_TEMPLATES ), PREFERENCE_DELIMITER );
        while ( tokenizer.hasMoreTokens() )
        {
            String token = tokenizer.nextToken();

            // Splitting the default set
            String[] splittedToken = token.split( ":" ); //$NON-NLS-1$
            if ( splittedToken.length == 2 )
            {
                // Adding the default template value
                defaultTemplatesMap.put( EntryTemplatePluginUtils
                    .getObjectClassDescriptionFromDefaultSchema( splittedToken[0] ), splittedToken[1] );
            }
        }
    }


    /**
     * Saves the {@link Map} of default templates to the preference store.
     */
    private void saveDefaultTemplates()
    {
        StringBuffer sb = new StringBuffer();
        for ( ObjectClass objectClassDescription : defaultTemplatesMap.keySet() )
        {
            sb.append( objectClassDescription.getNames().get( 0 ) );
            sb.append( PREFERENCE_SUB_DELIMITER );
            sb.append( defaultTemplatesMap.get( objectClassDescription ) );
            sb.append( PREFERENCE_DELIMITER );
        }
        preferenceStore.setValue( EntryTemplatePluginConstants.PREF_DEFAULT_TEMPLATES, sb.toString() );
    }


    /**
     * Sets the default templates.
     */
    private void setDefaultTemplates()
    {
        for ( Template template : templatesList )
        {
            if ( isEnabled( template ) )
            {
                String structuralObjectClass = template.getStructuralObjectClass();

                // Checking if a default template is defined
                if ( defaultTemplatesMap.get( EntryTemplatePluginUtils
                    .getObjectClassDescriptionFromDefaultSchema( structuralObjectClass ) ) == null )
                {
                    // Assigning this template as the default one
                    defaultTemplatesMap.put( EntryTemplatePluginUtils
                        .getObjectClassDescriptionFromDefaultSchema( structuralObjectClass ), template.getId() );
                }
            }
        }

        // Saving default templates
        saveDefaultTemplates();
    }


    /**
     * Sets the given template as default for its structural object class.
     *
     * @param template
     *      the template
     */
    public void setDefaultTemplate( Template template )
    {
        if ( isEnabled( template ) )
        {
            // Removing the old value
            defaultTemplatesMap.remove( EntryTemplatePluginUtils.getObjectClassDescriptionFromDefaultSchema( template
                .getStructuralObjectClass() ) );

            // Setting the new value
            defaultTemplatesMap.put( EntryTemplatePluginUtils.getObjectClassDescriptionFromDefaultSchema( template
                .getStructuralObjectClass() ), template.getId() );

            // Saving default templates
            saveDefaultTemplates();
        }
    }


    /**
     * Unsets the given template as default for its structural object class.
     *
     * @param template
     *      the template
     */
    public void unSetDefaultTemplate( Template template )
    {
        if ( isDefaultTemplate( template ) )
        {
            defaultTemplatesMap.remove( EntryTemplatePluginUtils.getObjectClassDescriptionFromDefaultSchema( template
                .getStructuralObjectClass() ) );

            // Saving default template
            saveDefaultTemplates();
        }
    }


    /**
     * Indicates if the given template is the default one 
     *      for its structural object class or not.
     *
     * @param template
     *      the template
     * @return
     *      <code>true</code> if the given template is the default one 
     *      for its structural object class,
     *      <code>false</code> if not
     */
    public boolean isDefaultTemplate( Template template )
    {
        String defaultTemplateID = defaultTemplatesMap.get( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ) );
        if ( defaultTemplateID != null )
        {
            return defaultTemplateID.equalsIgnoreCase( template.getId() );
        }

        return false;
    }


    /**
     * Indicates whether the given name or OID for an object class has a default template.
     *
     * @param nameOrOid
     *      the name or OID
     * @return
     *      <code>true</code> if the given name or OID for an object class has a default template
     */
    public boolean hasDefaultTemplate( String nameOrOid )
    {
        return getDefaultTemplate( nameOrOid ) != null;
    }


    /**
     * Gets the default template associated with given name or OID for an object class.
     *
     * @param nameOrOid
     * @return
     *      the default template associated with given name or OID for an object class,
     *      or <code>null</code> if there's no default template
     */
    public Template getDefaultTemplate( String nameOrOid )
    {
        return getTemplateById( defaultTemplatesMap.get( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( nameOrOid ) ) );
    }


    /**
     * Gets the template identified by the given ID.
     *
     * @param id
     *      the ID
     * @return
     *      the template identified by the given ID
     */
    private Template getTemplateById( String id )
    {
        return templatesByIdMap.get( id );
    }


    /**
     * Gets the list of templates associated with the given name or OID for an object class.
     *
     * @param nameOrOid
     *      the name or OID
     * @return
     *      the list of templates associated with the given name or OID for an object class
     *      or <code>null</code> if there's no associated template
     */
    @SuppressWarnings("unchecked")
    public List<Template> getTemplatesByObjectClass( String nameOrOid )
    {
        return ( List<Template> ) templatesByStructuralObjectClassMap.get( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( nameOrOid ) );
    }

}
