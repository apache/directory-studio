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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.TemplatesManagerListener;
import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class implements a content provider for the table viewer of
 * the Template Entry Editor preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplatesContentProvider implements ITreeContentProvider, TemplatesManagerListener
{
    /** The associated page */
    private TemplateEntryEditorPreferencePage page;

    /** The templates manager */
    private PreferencesTemplatesManager manager;

    /** The preference store */
    private IPreferenceStore store;

    /** A flag indicating if the content provider has already been initialized */
    private boolean initialized = false;

    /** The list of templates */
    private List<Template> templates;

    /** The map where templates are organized by object classes */
    private MultiValuedMap<ObjectClass, Template> objectClassesTemplatesMap;


    /**
     * Creates a new instance of TemplatesContentProvider.
     *
     * @param page
     *      the associated page
     * @param manager
     *      the templates manager
     */
    public TemplatesContentProvider( TemplateEntryEditorPreferencePage page, PreferencesTemplatesManager manager )
    {
        this.page = page;
        this.manager = manager;
        manager.addListener( this );
        store = EntryTemplatePlugin.getDefault().getPreferenceStore();
        templates = new ArrayList<Template>();
        objectClassesTemplatesMap = new ArrayListValuedHashMap<>();
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getChildren( Object parentElement )
    {
        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            if ( parentElement instanceof ObjectClass )
            {
                List<Template> templates = ( List<Template> ) objectClassesTemplatesMap
                    .get( ( ObjectClass ) parentElement );

                if ( templates != null )
                {
                    return templates.toArray();
                }
            }
        }
        // Template presentation
        else if ( isTemplatePresentation() )
        {
            // Elements have no children
            return new Object[0];
        }

        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public Object getParent( Object element )
    {
        // Elements have no parent, as they have no children
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren( Object element )
    {
        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            if ( element instanceof ObjectClass )
            {
                return objectClassesTemplatesMap.containsKey( ( ObjectClass ) element );
            }
        }
        // Template presentation
        else if ( isTemplatePresentation() )
        {
            // Elements have no children
            return false;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object inputElement )
    {
        if ( !initialized )
        {
            // Looping on the templates
            for ( Template template : manager.getTemplates() )
            {
                // Adding the template
                templates.add( template );

                // Adding the structural object class
                objectClassesTemplatesMap.put( EntryTemplatePluginUtils
                    .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ), template );
            }

            // Setting the initialized flag to true
            initialized = true;
        }

        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            // Returning the object classes
            return objectClassesTemplatesMap.keySet().toArray();

        }
        // Template presentation
        else if ( isTemplatePresentation() )
        {
            // Returning the templates
            return templates.toArray();
        }

        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        EntryTemplatePlugin.getDefault().getTemplatesManager().removeListener( this );
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do.
    }


    /**
     * Indicates if the template presentation is selected.
     *
     * @return
     *      <code>true</code> if the template presentation is selected,
     *      <code>false</code> if not
     */
    public boolean isTemplatePresentation()
    {
        return ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_TEMPLATE );
    }


    /**
     * Indicates if the object class presentation is selected.
     *
     * @return
     *      <code>true</code> if the object class presentation is selected,
     *      <code>false</code> if not
     */
    public boolean isObjectClassPresentation()
    {
        return ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS );
    }


    /**
     * {@inheritDoc}
     */
    public void templateAdded( Template template )
    {
        // Adding the template
        templates.add( template );

        // Adding the structural object class
        objectClassesTemplatesMap.put( EntryTemplatePluginUtils.getObjectClassDescriptionFromDefaultSchema( template
            .getStructuralObjectClass() ), template );

        // Refreshing the viewer
        page.refreshViewer();
    }


    /**
     * {@inheritDoc}
     */
    public void templateRemoved( Template template )
    {
        // Removing the structural object class
        objectClassesTemplatesMap.removeMapping( EntryTemplatePluginUtils.getObjectClassDescriptionFromDefaultSchema( template
            .getStructuralObjectClass() ), template );

        // Removing the template
        templates.remove( template );

        // Refreshing the viewer
        page.refreshViewer();
    }


    /**
     * {@inheritDoc}
     */
    public void templateDisabled( Template template )
    {
        // Nothing to do 
    }


    /**
     * {@inheritDoc}
     */
    public void templateEnabled( Template template )
    {
        // Nothing to do
    }
}
