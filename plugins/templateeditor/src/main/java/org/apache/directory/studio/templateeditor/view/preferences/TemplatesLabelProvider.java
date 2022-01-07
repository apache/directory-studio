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


import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.view.ColumnsLabelProvider;


/**
 * This class implements a label provider for the table viewer of
 * the Template Entry Editor preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplatesLabelProvider extends ColumnsLabelProvider implements ITableFontProvider, ITableColorProvider
{
    /** The templates manager */
    private PreferencesTemplatesManager manager;

    /** The preference store */
    private IPreferenceStore store;


    /**
     * Creates a new instance of TemplatesLabelProvider.
     */
    public TemplatesLabelProvider( PreferencesTemplatesManager manager )
    {
        this.manager = manager;
        store = EntryTemplatePlugin.getDefault().getPreferenceStore();
    }


    /**
     * {@inheritDoc}
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            if ( columnIndex == 0 )
            {
                if ( element instanceof ObjectClass )
                {
                    return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_OBJECT_CLASS );
                }
                else if ( element instanceof Template )
                {
                    if ( manager.isEnabled( ( Template ) element ) )
                    {
                        return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_TEMPLATE );
                    }
                    else
                    {
                        return EntryTemplatePlugin.getDefault().getImage(
                            EntryTemplatePluginConstants.IMG_TEMPLATE_DISABLED );
                    }
                }
            }
        }
        // Template presentation
        else if ( isTemplatePresentation() )
        {
            if ( columnIndex == 0 )
            {
                if ( element instanceof Template )
                {
                    if ( manager.isEnabled( ( Template ) element ) )
                    {
                        return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_TEMPLATE );
                    }
                    else
                    {
                        return EntryTemplatePlugin.getDefault().getImage(
                            EntryTemplatePluginConstants.IMG_TEMPLATE_DISABLED );
                    }
                }
            }
            else if ( columnIndex == 1 )
            {
                if ( element instanceof Template )
                {
                    return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_OBJECT_CLASS );
                }
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getColumnText( Object element, int columnIndex )
    {
        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            if ( columnIndex == 0 )
            {
                if ( element instanceof ObjectClass )
                {
                    return concatenateObjectClassNames( ( ( ObjectClass ) element ).getNames() );
                }
                else if ( element instanceof Template )
                {
                    Template template = ( Template ) element;
                    if ( manager.isDefaultTemplate( template ) )
                    {
                        return NLS.bind( Messages.getString( "TemplatesLabelProvider.Default" ), template.getTitle() ); //$NON-NLS-1$
                    }
                    else
                    {
                        return template.getTitle();
                    }
                }
            }
        }
        // Template presentation
        else if ( isTemplatePresentation() )
        {
            if ( columnIndex == 0 )
            {
                if ( element instanceof Template )
                {
                    return ( ( Template ) element ).getTitle();
                }
            }
            else if ( columnIndex == 1 )
            {
                if ( element instanceof Template )
                {
                    Template template = ( Template ) element;
                    return concatenateObjectClasses( EntryTemplatePluginUtils
                        .getObjectClassDescriptionFromDefaultSchema( template.getStructuralObjectClass() ), template
                        .getAuxiliaryObjectClasses() );
                }
            }
        }

        return ""; //$NON-NLS-1$
    }


    /**
     * Indicates if the template presentation is selected.
     *
     * @return
     *      <code>true</code> if the template presentation is selected,
     *      <code>false</code> if not
     */
    private boolean isTemplatePresentation()
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
    private boolean isObjectClassPresentation()
    {
        return ( store.getInt( EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION ) == EntryTemplatePluginConstants.PREF_TEMPLATES_PRESENTATION_OBJECT_CLASS );
    }


    /**
     * Concatenates the object classes in a single string.
     *
     * @param objectClass
     *      the object class
     * @param auxiliaryObjectClasses
     *      the list of auxiliary object class names
     * @return
     *      a string containing all the object classes separated 
     *      by <code>", "</code> characters.
     */
    private String concatenateObjectClasses( ObjectClass objectClass,
        List<String> auxiliaryObjectClasses )
    {
        if ( ( objectClass != null ) && ( auxiliaryObjectClasses != null ) )
        {
            StringBuilder sb = new StringBuilder();

            sb.append( concatenateObjectClassNames( objectClass.getNames() ) );

            if ( auxiliaryObjectClasses.size() > 0 )
            {
                sb.append( " <" ); //$NON-NLS-1$

                // Adding each auxiliary object class
                Iterator<String> iterator = auxiliaryObjectClasses.iterator();
                while ( iterator.hasNext() )
                {
                    sb.append( ( String ) iterator.next() );
                    if ( iterator.hasNext() )
                    {
                        sb.append( ", " ); //$NON-NLS-1$
                    }
                }

                sb.append( ">" ); //$NON-NLS-1$
            }

            return sb.toString();
        }

        return ""; //$NON-NLS-1$
    }


    /**
     * Concatenates the object class names in a single string.
     *
     * @param objectClasses
     *      the object classes
     * @return
     *      a string containing all the object classes separated 
     *      by <code>", "</code> characters.
     */
    private String concatenateObjectClassNames( List<String> names )
    {
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            StringBuilder sb = new StringBuilder();

            Iterator<String> iterator = names.iterator();
            while ( iterator.hasNext() )
            {
                sb.append( ( String ) iterator.next() );
                if ( iterator.hasNext() )
                {
                    sb.append( ", " ); //$NON-NLS-1$
                }
            }

            return sb.toString();
        }

        return ""; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Font getFont( Object element, int columnIndex )
    {
        // Object class presentation
        if ( isObjectClassPresentation() )
        {
            if ( element instanceof Template )
            {
                if ( manager.isDefaultTemplate( ( Template ) element ) )
                {
                    // Get the default Bold Font
                    return JFaceResources.getFontRegistry().getBold( JFaceResources.DEFAULT_FONT );
                }
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getForeground( Object element, int columnIndex )
    {
        if ( element instanceof Template )
        {
            if ( !manager.isEnabled( ( Template ) element ) )
            {
                // TODO: get disabled color
                return null;
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getBackground( Object element, int columnIndex )
    {
        return null;
    }
}
