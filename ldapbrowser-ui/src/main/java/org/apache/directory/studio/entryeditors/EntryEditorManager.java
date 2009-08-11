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

import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
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

    private Collection<EntryEditorExtension> entryEditorExtensions = new ArrayList<EntryEditorExtension>();


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
}
