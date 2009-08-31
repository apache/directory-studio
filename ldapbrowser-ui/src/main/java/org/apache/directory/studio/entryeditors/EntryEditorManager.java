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
package org.apache.directory.studio.entryeditors;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * A EntryEditorManager is used to manage entry editors. It provides methods to get
 * the best or alternative entry editors for a given entry.
 * 
 * The available value editors are specified by the extension point
 * <code>org.apache.directory.studio.entryeditors</code>. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorManager
{
    private static final String ID_ATTR = "id"; //$NON-NLS-1$
    private static final String NAME_ATTR = "name"; //$NON-NLS-1$
    private static final String DESCRIPTION_ATTR = "description"; //$NON-NLS-1$
    private static final String ICON_ATTR = "icon"; //$NON-NLS-1$
    private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
    private static final String EDITOR_ID_ATTR = "editorId"; //$NON-NLS-1$
    private static final String MULTI_WINDOW_ATTR = "multiWindow"; //$NON-NLS-1$
    private static final String PRIORITY_ATTR = "priority"; //$NON-NLS-1$

    /** The priorities separator */
    public static final String PRIORITIES_SEPARATOR = ",";

    /** The list of entry editors */
    private Collection<EntryEditorExtension> entryEditorExtensions = new ArrayList<EntryEditorExtension>();

    /** The comparator for entry editors */
    private Comparator<EntryEditorExtension> entryEditorComparator = new Comparator<EntryEditorExtension>()
    {
        public int compare( EntryEditorExtension o1, EntryEditorExtension o2 )
        {
            if ( o1 == null )
            {
                return ( o2 == null ) ? 0 : -1;
            }

            if ( o2 == null )
            {
                return 1;
            }

            // Getting priorities
            int o1Priority = o1.getPriority();
            int o2Priority = o2.getPriority();

            if ( o1Priority != o2Priority )
            {
                return ( o1Priority > o2Priority ) ? -1 : 1;
            }

            // Getting names
            String o1Name = o1.getName();
            String o2Name = o2.getName();

            if ( o1Name == null )
            {
                return ( o2Name == null ) ? 0 : -1;
            }

            if ( o2 == null )
            {
                return 1;
            }

            return o1Name.compareTo( o2Name );
        }
    };


    /**
     * Creates a new instance of EntryEditorManager.
     */
    public EntryEditorManager()
    {
        initEntryEditorExtensions();
    }


    /**
     * Initializes the entry editors extensions.
     */
    private void initEntryEditorExtensions()
    {
        entryEditorExtensions = new ArrayList<EntryEditorExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( BrowserUIConstants.ENTRY_EDITOR_EXTENSION_POINT );
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();

        // For each extension:
        for ( int m = 0; m < members.length; m++ )
        {
            EntryEditorExtension bean = new EntryEditorExtension();
            entryEditorExtensions.add( bean );

            IConfigurationElement member = members[m];
            IExtension extension = member.getDeclaringExtension();
            String extendingPluginId = extension.getNamespaceIdentifier();

            bean.setId( member.getAttribute( ID_ATTR ) );
            bean.setName( member.getAttribute( NAME_ATTR ) );
            bean.setDescription( member.getAttribute( DESCRIPTION_ATTR ) );
            String iconPath = member.getAttribute( ICON_ATTR );
            ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin( extendingPluginId, iconPath );
            if ( icon == null )
            {
                icon = ImageDescriptor.getMissingImageDescriptor();
            }
            bean.setIcon( icon );
            bean.setClassName( member.getAttribute( CLASS_ATTR ) );
            bean.setEditorId( member.getAttribute( EDITOR_ID_ATTR ) );
            bean.setMultiWindow( "true".equalsIgnoreCase( member.getAttribute( MULTI_WINDOW_ATTR ) ) );
            bean.setPriority( Integer.parseInt( member.getAttribute( PRIORITY_ATTR ) ) );
        }
    }


    /**
     * Gets the entry editor extensions.
     * 
     * @return the entry editor extensions
     */
    public Collection<EntryEditorExtension> getEntryEditorExtensions()
    {
        return entryEditorExtensions;
    }


    /**
     * Gets the sorted entry editor extensions.
     * 
     * @return
     *      the sorted entry editor extensions
     */
    public Collection<EntryEditorExtension> getSortedEntryEditorExtensions()
    {
        boolean useUserPriority = BrowserUIPlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES );

        if ( useUserPriority )
        {
            return getEntryEditorExtensionsSortedByUserPriority();
        }
        else
        {
            return getEntryEditorExtensionsSortedByDefaultPriority();
        }
    }


    /**
     * Gets the entry editor extensions sorted by default priority.
     *
     * @return
     *      the entry editor extensions sorted by default priority
     */
    public Collection<EntryEditorExtension> getEntryEditorExtensionsSortedByDefaultPriority()
    {
        // Getting all entry editors
        Collection<EntryEditorExtension> entryEditorExtensions = getEntryEditorExtensions();

        // Creating the sorted entry editors list
        ArrayList<EntryEditorExtension> sortedEntryEditorsList = new ArrayList<EntryEditorExtension>(
            entryEditorExtensions.size() );

        // Adding the remaining entry editors
        for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
        {
            sortedEntryEditorsList.add( entryEditorExtension );
        }

        // Sorting the remaining entry editors based on their priority
        Collections.sort( sortedEntryEditorsList, entryEditorComparator );

        return sortedEntryEditorsList;
    }


    /**
     * Gets the entry editor extensions sorted by user's priority.
     *
     * @return
     *      the entry editor extensions sorted by user's priority
     */
    public Collection<EntryEditorExtension> getEntryEditorExtensionsSortedByUserPriority()
    {
        // Getting all entry editors
        Collection<EntryEditorExtension> entryEditorExtensions = BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getEntryEditorExtensions();

        // Creating the sorted entry editors list
        Collection<EntryEditorExtension> sortedEntryEditorsList = new ArrayList<EntryEditorExtension>(
            entryEditorExtensions.size() );

        // Getting the user's priorities
        String userPriorities = BrowserUIPlugin.getDefault().getPluginPreferences().getString(
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USER_PRIORITIES );
        if ( ( userPriorities != null ) && ( !"".equals( userPriorities ) ) )
        {

            String[] splittedUserPriorities = userPriorities.split( PRIORITIES_SEPARATOR );
            if ( ( splittedUserPriorities != null ) && ( splittedUserPriorities.length > 0 ) )
            {

                // Creating a map where entry editors are accessible via their ID
                Map<String, EntryEditorExtension> entryEditorsMap = new HashMap<String, EntryEditorExtension>();
                for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
                {
                    entryEditorsMap.put( entryEditorExtension.getId(), entryEditorExtension );
                }

                // Adding the entry editors according to the user's priority
                for ( String entryEditorId : splittedUserPriorities )
                {
                    // Verifying the entry editor is present in the map
                    if ( entryEditorsMap.containsKey( entryEditorId ) )
                    {
                        // Adding it to the sorted list
                        sortedEntryEditorsList.add( entryEditorsMap.get( entryEditorId ) );
                    }
                }
            }

            // If some new plugins have been added recently, their new 
            // entry editors may not be present in the string stored in 
            // the preferences.
            // We are then adding them at the end of the sorted list.

            // Creating a list of remaining entry editors
            List<EntryEditorExtension> remainingEntryEditors = new ArrayList<EntryEditorExtension>();
            for ( EntryEditorExtension entryEditorExtension : entryEditorExtensions )
            {
                // Verifying the entry editor is present in the sorted list
                if ( !sortedEntryEditorsList.contains( entryEditorExtension ) )
                {
                    // Adding it to the remaining list
                    remainingEntryEditors.add( entryEditorExtension );
                }
            }

            // Sorting the remaining entry editors based on their priority
            Collections.sort( remainingEntryEditors, entryEditorComparator );

            // Adding the remaining entry editors
            for ( EntryEditorExtension entryEditorExtension : remainingEntryEditors )
            {
                sortedEntryEditorsList.add( entryEditorExtension );
            }
        }

        return sortedEntryEditorsList;
    }
}
