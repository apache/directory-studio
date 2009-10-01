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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
    private Map<String, EntryEditorExtension> entryEditorExtensions = new HashMap<String, EntryEditorExtension>();

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
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( partListener );
        EventRegistry
            .addEntryUpdateListener( entryUpdateListener, BrowserCommonActivator.getDefault().getEventRunner() );
    }


    /**
     * Initializes the entry editors extensions.
     */
    private void initEntryEditorExtensions()
    {
        entryEditorExtensions = new HashMap<String, EntryEditorExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint( BrowserUIConstants.ENTRY_EDITOR_EXTENSION_POINT );
        IConfigurationElement[] members = extensionPoint.getConfigurationElements();

        // For each extension:
        for ( int m = 0; m < members.length; m++ )
        {
            EntryEditorExtension bean = new EntryEditorExtension();

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

            entryEditorExtensions.put( bean.getId(), bean );
        }
    }


    public void dispose()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( partListener );
        EventRegistry.removeEntryUpdateListener( entryUpdateListener );
    }


    /**
     * Gets the entry editor extensions.
     * 
     * @return the entry editor extensions
     */
    public Collection<EntryEditorExtension> getEntryEditorExtensions()
    {
        return entryEditorExtensions.values();
    }


    /**
     * Gets the entry editor extension.
     * 
     * @param id the entry editor extension id
     * 
     * @return the entry editor extension, null if none found
     */
    public EntryEditorExtension getEntryEditorExtension( String id )
    {
        return entryEditorExtensions.get( id );
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


    /**
     * Opens an entry editor with the given entry editor extension and one of 
     * the given entries, search results or bookmarks.
     *
     * @param extension
     *      the entry editor extension
     * @param entries
     *      an array of entries
     * @param searchResults
     *      an array of search results
     * @param bookmarks
     *      an arrays of bookmarks
     */
    public void openEntryEditor( EntryEditorExtension extension, IEntry[] entries, ISearchResult[] searchResults,
        IBookmark[] bookmarks )
    {
        IEditorInput input = null;
        if ( entries.length == 1 )
        {
            input = new EntryEditorInput( entries[0], extension );
        }
        else if ( searchResults.length == 1 )
        {
            input = new EntryEditorInput( searchResults[0], extension );
        }
        else if ( bookmarks.length == 1 )
        {
            input = new EntryEditorInput( bookmarks[0], extension );
        }

        String editorId = extension.getEditorId();

        try
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( input, editorId, false );
        }
        catch ( PartInitException e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     * Opens an entry editor with one of the given entries, search results or bookmarks.
     *
     * @param extension
     *      the entry editor extension
     * @param entries
     *      an array of entries
     * @param searchResults
     *      an array of search results
     * @param bookmarks
     *      an arrays of bookmarks
     */
    public void openEntryEditor( IEntry[] entries, ISearchResult[] searchResults, IBookmark[] bookmarks )
    {
        Collection<EntryEditorExtension> entryEditors = getSortedEntryEditorExtensions();
        // TODO: check if the entry editor can "handle" the entry 
        EntryEditorExtension next = entryEditors.iterator().next();
        openEntryEditor( next, entries, searchResults, bookmarks );
    }

    /** The shared reference copies for open-save-close editors; original entry -> reference copy */
    private Map<IEntry, IEntry> oscSharedReferenceCopies = new HashMap<IEntry, IEntry>();

    /** The shared working copies for open-save-close editors; original entry -> working copy */
    private Map<IEntry, IEntry> oscSharedWorkingCopies = new HashMap<IEntry, IEntry>();

    /** The shared reference copies for auto-save editors; original entry -> reference copy */
    private Map<IEntry, IEntry> autoSaveSharedReferenceCopies = new HashMap<IEntry, IEntry>();

    /** The shared working copies for auto-save editors; original entry -> working copy */
    private Map<IEntry, IEntry> autoSaveSharedWorkingCopies = new HashMap<IEntry, IEntry>();


    private void updateOscSharedReferenceCopy( IEntry entry )
    {
        IEntry referenceEntry = oscSharedReferenceCopies.remove( entry );
        EntryEditorUtils.ensureAttributesInitialized( entry );
        EventRegistry.suspendEventFiringInCurrentThread();
        new CompoundModification().replaceAttributes( entry, referenceEntry, this );
        EventRegistry.resumeEventFiringInCurrentThread();
        oscSharedReferenceCopies.put( entry, referenceEntry );
    }


    private void updateOscSharedWorkingCopy( IEntry entry )
    {
        EntryEditorUtils.ensureAttributesInitialized( entry );
        IEntry workingCopy = oscSharedWorkingCopies.get( entry );
        new CompoundModification().replaceAttributes( entry, workingCopy, this );
    }


    private void updateAutoSaveSharedReferenceCopy( IEntry entry )
    {
        EntryEditorUtils.ensureAttributesInitialized( entry );
        IEntry workingCopy = autoSaveSharedReferenceCopies.get( entry );
        EventRegistry.suspendEventFiringInCurrentThread();
        new CompoundModification().replaceAttributes( entry, workingCopy, this );
        EventRegistry.resumeEventFiringInCurrentThread();
    }


    private void updateAutoSaveSharedWorkingCopy( IEntry entry )
    {
        EntryEditorUtils.ensureAttributesInitialized( entry );
        IEntry workingCopy = autoSaveSharedWorkingCopies.get( entry );
        new CompoundModification().replaceAttributes( entry, workingCopy, this );
    }


    private List<IEntryEditor> getOscEditors( IEntry workingCopy )
    {
        List<IEntryEditor> oscEditors = new ArrayList<IEntryEditor>();
        IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getEditorReferences();
        for ( IEditorReference ref : editorReferences )
        {
            IEntryEditor editor = getEntryEditor( ref );
            if ( editor != null && !editor.isAutoSave()
                && ( workingCopy == null || editor.getEntryEditorInput().getSharedWorkingCopy( editor ) == workingCopy ) )
            {
                oscEditors.add( editor );
            }
        }
        return oscEditors;
    }


    private List<IEntryEditor> getAutoSaveEditors( IEntry workingCopy )
    {
        List<IEntryEditor> autoSaveEditors = new ArrayList<IEntryEditor>();
        IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getEditorReferences();
        for ( IEditorReference ref : editorReferences )
        {
            IEntryEditor editor = getEntryEditor( ref );
            if ( editor != null && editor.isAutoSave()
                && editor.getEntryEditorInput().getSharedWorkingCopy( editor ) == workingCopy )
            {
                autoSaveEditors.add( editor );
            }
        }
        return autoSaveEditors;
    }


    private IEntryEditor getEntryEditor( IWorkbenchPartReference partRef )
    {
        IWorkbenchPart part = partRef.getPart( false );
        if ( part != null && part instanceof IEntryEditor )
        {
            IEntryEditor entryEditor = ( IEntryEditor ) part;
            return entryEditor;
        }
        return null;
    }


    IEntry getSharedWorkingCopy( IEntry originalEntry, IEntryEditor editor )
    {
        EntryEditorUtils.ensureAttributesInitialized( originalEntry );
        if ( editor.isAutoSave() )
        {
            if ( !autoSaveSharedReferenceCopies.containsKey( originalEntry ) )
            {
                autoSaveSharedReferenceCopies
                    .put( originalEntry, new CompoundModification().cloneEntry( originalEntry ) );
            }
            if ( !autoSaveSharedWorkingCopies.containsKey( originalEntry ) )
            {
                IEntry referenceCopy = autoSaveSharedReferenceCopies.get( originalEntry );
                autoSaveSharedWorkingCopies.put( originalEntry, new CompoundModification().cloneEntry( referenceCopy ) );
            }
            return autoSaveSharedWorkingCopies.get( originalEntry );
        }
        else
        {
            if ( !oscSharedReferenceCopies.containsKey( originalEntry ) )
            {
                oscSharedReferenceCopies.put( originalEntry, new CompoundModification().cloneEntry( originalEntry ) );
            }
            if ( !oscSharedWorkingCopies.containsKey( originalEntry ) )
            {
                IEntry referenceCopy = oscSharedReferenceCopies.get( originalEntry );
                oscSharedWorkingCopies.put( originalEntry, new CompoundModification().cloneEntry( referenceCopy ) );
            }
            return oscSharedWorkingCopies.get( originalEntry );
        }
    }


    boolean isSharedWorkingCopyDirty( IEntry originalEntry, IEntryEditor editor )
    {
        if ( editor.isAutoSave() )
        {
            return false;
        }
        else
        {
            IEntry referenceCopy = oscSharedReferenceCopies.get( originalEntry );
            IEntry workingCopy = oscSharedWorkingCopies.get( originalEntry );
            if ( referenceCopy != null && workingCopy != null )
            {
                LdifChangeModifyRecord diff = Utils.computeDiff( referenceCopy, workingCopy );
                return diff != null;
            }
            return false;
        }
    }


    IStatus saveSharedWorkingCopy( IEntry originalEntry, boolean handleError, IEntryEditor editor )
    {
        if ( editor == null || !editor.isAutoSave() )
        {
            IEntry referenceCopy = oscSharedReferenceCopies.get( originalEntry );
            IEntry workingCopy = oscSharedWorkingCopies.get( originalEntry );
            if ( referenceCopy != null && workingCopy != null )
            {
                LdifChangeModifyRecord diff = Utils.computeDiff( referenceCopy, workingCopy );
                if ( diff != null )
                {
                    // save by executing the LDIF
                    ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( originalEntry.getBrowserConnection(), diff
                        .toFormattedString( LdifFormatParameters.DEFAULT ), false, false );
                    IStatus status = RunnableContextRunner.execute( runnable, null, handleError );
                    if ( status.isOK() )
                    {
                        updateOscSharedReferenceCopy( originalEntry );
                        updateOscSharedWorkingCopy( originalEntry );
                    }
                    return status;
                }
            }
        }
        return null;
    }


    void resetSharedWorkingCopy( IEntry originalEntry, IEntryEditor editor )
    {
        if ( editor == null || !editor.isAutoSave() )
        {
            IEntry referenceCopy = oscSharedReferenceCopies.get( originalEntry );
            IEntry workingCopy = oscSharedWorkingCopies.get( originalEntry );
            if ( referenceCopy != null && workingCopy != null )
            {
                updateOscSharedReferenceCopy( originalEntry );
                updateOscSharedWorkingCopy( originalEntry );
            }
        }
    }


    private void askUpdateSharedWorkingCopy( IWorkbenchPartReference partRef, IEntry originalEntry,
        IEntry oscSharedWorkingCopy, Object source )
    {
        MessageDialog dialog = new MessageDialog(
            partRef.getPart( false ).getSite().getShell(),
            "Entry Changed",
            null,
            "The entry has been changed in the directory server. Do you want to replace the editor contents with these changes?",
            MessageDialog.QUESTION, new String[]
                { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0 );
        int result = dialog.open();
        if ( result == 0 )
        {
            // update reference copy and working copy
            updateOscSharedReferenceCopy( originalEntry );
            updateOscSharedWorkingCopy( originalEntry );

            // inform all OSC editors
            List<IEntryEditor> oscEditors = getOscEditors( oscSharedWorkingCopy );
            for ( IEntryEditor oscEditor : oscEditors )
            {
                oscEditor.workingCopyModified( source );
            }
        }
        else
        {
            // only update the reference copy
            updateOscSharedReferenceCopy( originalEntry );
        }
    }


    private void cleanupCopies( IWorkbenchPartReference partRef )
    {
        // cleanup unused copies (OSC + auto-save)
        Set<IEntry> oscEntries = new HashSet<IEntry>();
        Set<IEntry> autoSaveEntries = new HashSet<IEntry>();
        IEditorReference[] editorReferences = partRef.getPage().getEditorReferences();
        for ( IEditorReference ref : editorReferences )
        {
            IEntryEditor editor = getEntryEditor( ref );
            if ( editor != null )
            {
                EntryEditorInput input = editor.getEntryEditorInput();
                if ( input != null )
                {
                    IEntry entry = input.getResolvedEntry();
                    if ( editor.isAutoSave() )
                    {
                        autoSaveEntries.add( entry );
                    }
                    else
                    {
                        oscEntries.add( entry );
                    }
                }
            }
        }
        for ( Iterator<IEntry> it = oscSharedReferenceCopies.keySet().iterator(); it.hasNext(); )
        {
            IEntry entry = it.next();
            if ( !oscEntries.contains( entry ) )
            {
                it.remove();
                oscSharedWorkingCopies.remove( entry );
            }
        }
        for ( Iterator<IEntry> it = oscSharedWorkingCopies.keySet().iterator(); it.hasNext(); )
        {
            IEntry entry = it.next();
            if ( !oscEntries.contains( entry ) )
            {
                it.remove();
            }
        }
        for ( Iterator<IEntry> it = autoSaveSharedReferenceCopies.keySet().iterator(); it.hasNext(); )
        {
            IEntry entry = it.next();
            if ( !autoSaveEntries.contains( entry ) )
            {
                it.remove();
            }
        }
        for ( Iterator<IEntry> it = autoSaveSharedWorkingCopies.keySet().iterator(); it.hasNext(); )
        {
            IEntry entry = it.next();
            if ( !autoSaveEntries.contains( entry ) )
            {
                it.remove();
            }
        }
    }

    private IPartListener2 partListener = new IPartListener2()
    {
        public void partActivated( IWorkbenchPartReference partRef )
        {
            cleanupCopies( partRef );

            IEntryEditor editor = getEntryEditor( partRef );
            if ( editor != null )
            {
                EntryEditorInput eei = editor.getEntryEditorInput();
                IEntry originalEntry = eei.getResolvedEntry();
                IEntry oscSharedReferenceCopy = oscSharedReferenceCopies.get( originalEntry );
                IEntry oscSharedWorkingCopy = oscSharedWorkingCopies.get( originalEntry );
                if ( editor.isAutoSave() )
                {
                    // check if the same entry is used in an OSC editor and is dirty -> should save first?
                    if ( oscSharedReferenceCopy != null && oscSharedWorkingCopy != null )
                    {
                        LdifChangeModifyRecord diff = Utils.computeDiff( oscSharedReferenceCopy, oscSharedWorkingCopy );
                        if ( diff != null )
                        {
                            MessageDialog dialog = new MessageDialog( partRef.getPart( false ).getSite().getShell(),
                                "Save Changes", null, "Entry has been modified in another entry editor. Save changes?",
                                MessageDialog.QUESTION, new String[]
                                    { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0 );
                            int result = dialog.open();
                            if ( result == 0 )
                            {
                                saveSharedWorkingCopy( originalEntry, true, null );
                            }
                        }
                    }
                }
                else
                {
                    // check if original entry was updated
                    if ( oscSharedReferenceCopy != null && oscSharedWorkingCopy != null )
                    {
                        LdifChangeModifyRecord refDiff = Utils.computeDiff( originalEntry, oscSharedReferenceCopy );
                        if ( refDiff != null )
                        {
                            // check if we could just update the working copy
                            LdifChangeModifyRecord workDiff = Utils.computeDiff( oscSharedReferenceCopy,
                                oscSharedWorkingCopy );
                            if ( workDiff != null )
                            {
                                askUpdateSharedWorkingCopy( partRef, originalEntry, oscSharedWorkingCopy, null );
                            }
                        }
                    }

                }
            }
        }


        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        public void partClosed( IWorkbenchPartReference partRef )
        {
            cleanupCopies( partRef );
        }


        public void partInputChanged( IWorkbenchPartReference partRef )
        {
            cleanupCopies( partRef );
        }


        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }


        public void partVisible( IWorkbenchPartReference partRef )
        {
        }

    };

    private EntryUpdateListener entryUpdateListener = new EntryUpdateListener()
    {

        public void entryUpdated( EntryModificationEvent event )
        {
            IEntry modifiedEntry = event.getModifiedEntry();
            IBrowserConnection browserConnection = modifiedEntry.getBrowserConnection();
            IEntry originalEntry = browserConnection.getEntryFromCache( modifiedEntry.getDn() );

            if ( modifiedEntry == originalEntry )
            {
                // an original entry has been modified, check if we could update the editors

                // if the OSC editor is not dirty we could update the working copy
                IEntry oscSharedReferenceCopy = oscSharedReferenceCopies.get( originalEntry );
                IEntry oscSharedWorkingCopy = oscSharedWorkingCopies.get( originalEntry );
                if ( oscSharedReferenceCopy != null && oscSharedWorkingCopy != null )
                {
                    LdifChangeModifyRecord refDiff = Utils.computeDiff( originalEntry, oscSharedReferenceCopy );
                    if ( refDiff != null )
                    {
                        // diff between original entry and reference copy
                        LdifChangeModifyRecord workDiff = Utils.computeDiff( oscSharedReferenceCopy,
                            oscSharedWorkingCopy );
                        if ( workDiff == null )
                        {
                            // no changes on working copy, update
                            updateOscSharedReferenceCopy( originalEntry );
                            updateOscSharedWorkingCopy( originalEntry );

                            // inform all OSC editors
                            List<IEntryEditor> oscEditors = getOscEditors( oscSharedWorkingCopy );
                            for ( IEntryEditor editor : oscEditors )
                            {
                                editor.workingCopyModified( event.getSource() );
                            }
                        }
                        else
                        {
                            List<IEntryEditor> oscEditors = getOscEditors( oscSharedWorkingCopy );
                            for ( IEntryEditor entryEditor : oscEditors )
                            {
                                IWorkbenchPart part = ( IWorkbenchPart ) entryEditor;
                                IEditorPart activeEditor = part.getSite().getPage().getActiveEditor();
                                if ( part == activeEditor )
                                {
                                    IWorkbenchPartReference reference = part.getSite().getPage().getReference( part );
                                    askUpdateSharedWorkingCopy( reference, originalEntry, oscSharedWorkingCopy, event
                                        .getSource() );
                                }
                            }
                        }
                    }
                }

                // always update auto-save working copies, if necessary
                IEntry autoSaveSharedReferenceCopy = autoSaveSharedReferenceCopies.get( originalEntry );
                IEntry autoSaveSharedWorkingCopy = autoSaveSharedWorkingCopies.get( originalEntry );
                if ( autoSaveSharedReferenceCopy != null && autoSaveSharedWorkingCopy != null )
                {
                    LdifChangeModifyRecord diff = Utils.computeDiff( originalEntry, autoSaveSharedReferenceCopy );
                    if ( diff != null )
                    {
                        updateAutoSaveSharedReferenceCopy( originalEntry );
                        updateAutoSaveSharedWorkingCopy( originalEntry );
                        List<IEntryEditor> editors = getAutoSaveEditors( autoSaveSharedWorkingCopy );
                        for ( IEntryEditor editor : editors )
                        {
                            editor.workingCopyModified( event.getSource() );
                        }
                    }
                }

                // check all editors: if the input does not exist any more then close the editor
                IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().getEditorReferences();
                for ( IEditorReference ref : editorReferences )
                {
                    IEntryEditor editor = getEntryEditor( ref );
                    if ( editor != null )
                    {
                        IBrowserConnection bc = editor.getEntryEditorInput().getResolvedEntry().getBrowserConnection();
                        LdapDN dn = editor.getEntryEditorInput().getResolvedEntry().getDn();
                        if ( bc.getEntryFromCache( dn ) == null )
                        {
                            ref.getPage().closeEditor( ref.getEditor( false ), false );
                        }
                    }
                }
            }

            else if ( oscSharedWorkingCopies.containsKey( originalEntry )
                && oscSharedWorkingCopies.get( originalEntry ) == modifiedEntry )
            {
                // OSC working copy has been modified: inform OSC editors
                IEntry oscSharedWorkingCopy = oscSharedWorkingCopies.get( originalEntry );
                List<IEntryEditor> oscEditors = getOscEditors( oscSharedWorkingCopy );
                for ( IEntryEditor editor : oscEditors )
                {
                    editor.workingCopyModified( event.getSource() );
                }
            }

            else if ( autoSaveSharedWorkingCopies.containsValue( originalEntry )
                && autoSaveSharedWorkingCopies.get( originalEntry ) == modifiedEntry )
            {
                // auto-save working copy has been modified: save and inform all auto-save editors
                IEntry autoSaveSharedReferenceCopy = autoSaveSharedReferenceCopies.get( originalEntry );
                IEntry autoSaveSharedWorkingCopy = autoSaveSharedWorkingCopies.get( originalEntry );
                LdifChangeModifyRecord diff = Utils
                    .computeDiff( autoSaveSharedReferenceCopy, autoSaveSharedWorkingCopy );
                if ( diff != null )
                {
                    ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( browserConnection, diff
                        .toFormattedString( LdifFormatParameters.DEFAULT ), false, false );
                    RunnableContextRunner.execute( runnable, null, true );
                    // don't care if status is ok or not: always update
                    updateAutoSaveSharedReferenceCopy( originalEntry );
                    updateAutoSaveSharedWorkingCopy( originalEntry );
                }
            }
        }

    };
}
