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
package org.apache.directory.studio.templateeditor.view.preferences;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.TemplatesManager;
import org.apache.directory.studio.templateeditor.TemplatesManagerListener;
import org.apache.directory.studio.templateeditor.model.FileTemplate;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIO;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIOException;


/**
 * This templates manager is to be used in the plugin's preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PreferencesTemplatesManager
{
    /** The templates manager */
    private TemplatesManager manager;

    /** The list containing all the templates */
    private List<Template> templatesList = new ArrayList<Template>();

    /** The map containing the templates based on their IDs */
    private Map<String, Template> templatesByIdMap = new HashMap<String, Template>();

    /** The map containing the default templates */
    private Map<ObjectClass, String> defaultTemplatesMap = new HashMap<ObjectClass, String>();

    /** The set containing *only* the IDs of the disabled templates */
    private List<String> disabledTemplatesList = new ArrayList<String>();

    /** The list of listeners */
    private List<TemplatesManagerListener> listeners = new ArrayList<TemplatesManagerListener>();


    /**
     * Creates a new instance of PreferencesTemplatesManager.
     */
    public PreferencesTemplatesManager( TemplatesManager manager )
    {
        this.manager = manager;

        init();
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
     * Initializes the Preferences manager from the plugin manager.
     */
    private void init()
    {
        // Getting the templates from the plugin manager
        Template[] pluginTemplates = manager.getTemplates();
        for ( Template pluginTemplate : pluginTemplates )
        {
            templatesList.add( pluginTemplate );
            templatesByIdMap.put( pluginTemplate.getId(), pluginTemplate );

            // Is the template enabled?
            if ( !manager.isEnabled( pluginTemplate ) )
            {
                disabledTemplatesList.add( pluginTemplate.getId() );
            }

            // Is it the default template?
            if ( manager.isDefaultTemplate( pluginTemplate ) )
            {
                defaultTemplatesMap.put( EntryTemplatePluginUtils
                    .getObjectClassDescriptionFromDefaultSchema( pluginTemplate.getStructuralObjectClass() ),
                    pluginTemplate.getId() );
            }
        }
    }


    /**
     * Saves the modifications back to the initial manager.
     */
    public boolean saveModifications()
    {
        // Getting original templates
        Template[] originalTemplates = manager.getTemplates();

        // Creating a list of original templates
        List<Template> originalTemplatesList = new ArrayList<Template>();

        // Looping on original templates
        for ( Template originalTemplate : originalTemplates )
        {
            // Checking if the enablement state has been changed
            boolean isEnabled = isEnabled( originalTemplate );
            if ( manager.isEnabled( originalTemplate ) != isEnabled )
            {
                if ( isEnabled )
                {
                    manager.enableTemplate( originalTemplate );
                }
                else
                {
                    manager.disableTemplate( originalTemplate );
                }
            }

            // Checking if the default state has been changed
            boolean isDefaultTemplate = isDefaultTemplate( originalTemplate );
            if ( manager.isDefaultTemplate( originalTemplate ) != isDefaultTemplate )
            {
                if ( isDefaultTemplate )
                {
                    manager.setDefaultTemplate( originalTemplate );
                }
                else
                {
                    manager.unSetDefaultTemplate( originalTemplate );
                }
            }

            // Checking if the original template has been removed
            if ( !templatesList.contains( originalTemplate ) )
            {
                if ( !manager.removeTemplate( ( FileTemplate ) originalTemplate ) )
                {
                    // Creating and opening the error dialog
                    String dialogTitle = Messages.getString( "PreferencesTemplatesManager.UnableToRemoveTheTemplate" ); //$NON-NLS-1$
                    String dialogMessage = MessageFormat
                        .format(
                            Messages.getString( "PreferencesTemplatesManager.TheTemplateCouldNotBeRemoved" ) //$NON-NLS-1$
                                + EntryTemplatePluginUtils.LINE_SEPARATOR
                                + EntryTemplatePluginUtils.LINE_SEPARATOR
                                + Messages.getString( "PreferencesTemplatesManager.SeeTheLogsFileForMoreInformation" ), originalTemplate.getTitle() ); //$NON-NLS-1$
                    MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), dialogTitle, null, dialogMessage, MessageDialog.ERROR, new String[]
                        { IDialogConstants.OK_LABEL }, MessageDialog.OK );
                    dialog.open();
                    return false;
                }
            }

            // Adding the template to the list
            originalTemplatesList.add( originalTemplate );
        }

        // Looping on the new templates list
        for ( Template template : templatesList )
        {
            // Checking if the template has been added
            if ( !originalTemplatesList.contains( template ) )
            {
                // Adding the new template
                if ( !manager.addTemplate( new File( ( ( PreferencesFileTemplate ) template ).getFilePath() ) ) )
                {
                    // Creating and opening the error dialog
                    String dialogTitle = Messages.getString( "PreferencesTemplatesManager.UnableToAddTheTemplate" ); //$NON-NLS-1$
                    String dialogMessage = MessageFormat
                        .format(
                            Messages.getString( "PreferencesTemplatesManager.TheTemplateCouldNotBeAdded" ) //$NON-NLS-1$
                                + EntryTemplatePluginUtils.LINE_SEPARATOR
                                + EntryTemplatePluginUtils.LINE_SEPARATOR
                                + Messages.getString( "PreferencesTemplatesManager.SeeTheLogsFileForMoreInformation" ), template.getTitle() ); //$NON-NLS-1$
                    MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), dialogTitle, null, dialogMessage, MessageDialog.ERROR, new String[]
                        { IDialogConstants.OK_LABEL }, MessageDialog.OK );
                    dialog.open();
                    return false;
                }

                // Setting the enablement state to the new template
                boolean isEnabled = isEnabled( template );
                if ( isEnabled )
                {
                    manager.enableTemplate( template );
                }
                else
                {
                    manager.disableTemplate( template );
                }

                // Setting the default state has been changed
                boolean isDefaultTemplate = isDefaultTemplate( template );
                if ( isDefaultTemplate )
                {
                    manager.setDefaultTemplate( template );
                }
                else
                {
                    manager.unSetDefaultTemplate( template );
                }
            }
        }

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
        // Getting the template
        PreferencesFileTemplate template = getTemplateFromFile( templateFile );
        if ( template == null )
        {
            // If the file is not valid, we simply return
            return false;
        }

        if ( templatesByIdMap.containsKey( template.getId() ) )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeAddedBecauseATemplateWithSameIDAlreadyExist" ), //$NON-NLS-1$
                    templateFile.getAbsolutePath() );
            return false;
        }

        // Adding the template
        templatesList.add( template );
        templatesByIdMap.put( template.getId(), template );

        // If there's no default template, then set this one as default one
        if ( !defaultTemplatesMap.containsKey( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ) ) )
        {
            setDefaultTemplate( template );
        }

        // Firing the event
        fireTemplateAdded( template );

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
    private PreferencesFileTemplate getTemplateFromFile( File templateFile )
    {
        // Checking if the file exists
        if ( !templateFile.exists() )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeAddedBecauseItDoesNotExist" ), templateFile //$NON-NLS-1$
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
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeAddedBecauseItCantBeRead" ), templateFile.getAbsolutePath() ); //$NON-NLS-1$
            return null;
        }

        // Trying to parse the template file
        PreferencesFileTemplate fileTemplate = null;
        try
        {
            InputStream is = new FileInputStream( templateFile );

            fileTemplate = TemplateIO.readAsPreferencesFileTemplate( is );
            fileTemplate.setFilePath( templateFile.getAbsolutePath() );

            is.close();
        }
        catch ( IOException e )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    e,
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeAddedBecauseOfTheFollowingError" ), templateFile //$NON-NLS-1$
                        .getAbsolutePath(), e.getMessage() );
            return null;
        }
        catch ( TemplateIOException e )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    e,
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeAddedBecauseOfTheFollowingError" ), templateFile //$NON-NLS-1$
                        .getAbsolutePath(), e.getMessage() );
            return null;
        }

        // Everything went fine, the file is valid
        return fileTemplate;
    }


    /**
     * Removes a template.
     * 
     * @param template
     *      the template to remove
     * @return
     *      <code>true</code> if the template has been successfully removed,
     *      <code>false</code> if the template file has not been removed
     */
    public boolean removeTemplate( Template template )
    {
        // Checking if the file template exists in the templates set
        if ( !templatesList.contains( template ) )
        {
            // Logging the error
            EntryTemplatePluginUtils
                .logError(
                    null,
                    Messages
                        .getString( "PreferencesTemplatesManager.TheTemplateFileCouldNotBeRemovedBecauseOfTheFollowingError" ) //$NON-NLS-1$
                        + Messages
                            .getString( "PreferencesTemplatesManager.TheTemplateDoesNotExistInTheTemplateManager" ), template.getTitle(), template.getId() ); //$NON-NLS-1$
            return false;
        }

        // Removing the template from the disabled templates list
        if ( disabledTemplatesList.contains( template.getId() ) )
        {
            disabledTemplatesList.remove( template.getId() );
        }

        // Removing the template for the templates list
        templatesList.remove( template );
        templatesByIdMap.remove( template.getId() );

        // Checking if the template is the default one
        if ( isDefaultTemplate( template ) )
        {
            // Unsetting the template as default
            unSetDefaultTemplate( template );

            // Assign another default template.
            setNewAutoDefaultTemplate( template.getStructuralObjectClass() );
        }

        // Firing the event
        fireTemplateRemoved( template );

        return true;
    }


    /**
    * Enables the given template.
    *
    * @param template
    *      the template
    */
    public void enableTemplate( Template template )
    {
        // Removing the id of the template to the list of disabled templates
        disabledTemplatesList.remove( template.getId() );

        // If there's no default template, then set this one as default one
        if ( !defaultTemplatesMap.containsKey( EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ) ) )
        {
            setDefaultTemplate( template );
        }

        // Firing the event
        fireTemplateEnabled( template );
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

            // Checking if the template is the default one
            if ( isDefaultTemplate( template ) )
            {
                // Unsetting the template as default
                unSetDefaultTemplate( template );

                // Assign another default template.
                setNewAutoDefaultTemplate( template.getStructuralObjectClass() );
            }

            // Firing the event
            fireTemplateDisabled( template );
        }
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
        }
    }


    /**
     * Automatically sets a new default template (if one is found) for the given structural object class.
     *
     * @param structuralObjectClass
     *      the structural object class
     */
    public void setNewAutoDefaultTemplate( String structuralObjectClass )
    {
        ObjectClass structuralOcd = EntryTemplatePluginUtils
            .getObjectClassDescriptionFromDefaultSchema( structuralObjectClass );

        for ( Template templateCandidate : templatesList )
        {
            ObjectClass templateCandidateOcd = EntryTemplatePluginUtils
                .getObjectClassDescriptionFromDefaultSchema( templateCandidate.getStructuralObjectClass() );
            if ( structuralOcd.equals( templateCandidateOcd ) )
            {
                if ( isEnabled( templateCandidate ) )
                {
                    // Setting the new value
                    defaultTemplatesMap.put( templateCandidateOcd, templateCandidate.getId() );
                    return;
                }
            }
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

}
