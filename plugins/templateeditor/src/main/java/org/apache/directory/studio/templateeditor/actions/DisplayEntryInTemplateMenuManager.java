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
package org.apache.directory.studio.templateeditor.actions;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.editor.TemplateEditorWidget;
import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This class implements the menu manager which is used in the Template Editor to
 * allow to switch templates and open preferences.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DisplayEntryInTemplateMenuManager extends MenuManager implements IMenuListener
{
    /** The associated {@link TemplateEditorWidget} */
    private TemplateEditorWidget templateEditorPage;


    /**
     * Creates a new instance of DisplayEntryInTemplateMenuManager.
     *
     * @param templateEditorPage
     *      the associated editor page
     */
    public DisplayEntryInTemplateMenuManager( TemplateEditorWidget templateEditorPage )
    {
        super(
            Messages.getString( "DisplayEntryInTemplateMenuManager.DiplayEntryIn" ), EntryTemplatePlugin.getDefault().getImageDescriptor( //$NON-NLS-1$
                    EntryTemplatePluginConstants.IMG_SWITCH_TEMPLATE ), null );
        addMenuListener( this );
        this.templateEditorPage = templateEditorPage;
    }


    /**
     * {@inheritDoc}
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        fillInMenuManager( manager, templateEditorPage );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDynamic()
    {
        return true;
    }


    /**
     * Fill the menu manager in with one menu item for each available template.
     *
     * @param menuManager
     *      the menu manager
     * @param templateEditorWidget
     *      the associated editor widget
     */
    protected static void fillInMenuManager( IMenuManager menuManager, TemplateEditorWidget templateEditorWidget )
    {
        // Getting the matching templates and currently selected one from the editor page
        List<Template> matchingTemplates = new ArrayList<Template>( templateEditorWidget.getMatchingTemplates() );
        Template selectedTemplate = templateEditorWidget.getSelectedTemplate();

        // Sorting the list of matching templates by their title
        Collections.sort( matchingTemplates, new Comparator<Template>()
        {
            public int compare( Template o1, Template o2 )
            {
                if ( ( o1 == null ) && ( o2 == null ) )
                {
                    return 0;
                }
                else if ( ( o1 != null ) && ( o2 == null ) )
                {
                    return 1;
                }
                else if ( ( o1 == null ) && ( o2 != null ) )
                {
                    return -1;
                }
                else if ( ( o1 != null ) && ( o2 != null ) )
                {
                    String title1 = o1.getTitle();
                    String title2 = o2.getTitle();
                    if ( ( title1 == null ) && ( title2 == null ) )
                    {
                        return 0;
                    }
                    else if ( ( title1 != null ) && ( title2 == null ) )
                    {
                        return 1;
                    }
                    else if ( ( title1 == null ) && ( title2 != null ) )
                    {
                        return -1;
                    }
                    else if ( ( title1 != null ) && ( title2 != null ) )
                    {
                        return title1.compareTo( title2 );

                    }
                }

                return 0;
            };
        } );

        // As the Menu Manager is dynamic, we need to 
        // remove all the previously added actions
        menuManager.removeAll();

        if ( ( matchingTemplates != null ) && ( matchingTemplates.size() > 0 ) )
        {
            // Looping on the matching templates and creating an action for each one
            for ( Template matchingTemplate : matchingTemplates )
            {
                // Creating the action associated with the entry editor
                menuManager.add( createAction( templateEditorWidget, matchingTemplate, ( matchingTemplate
                    .equals( selectedTemplate ) ) ) );
            }
        }
        else
        {
            // Creating a action that will be disabled when no template is available
            Action noTemplateAction = new Action(
                Messages.getString( "DisplayEntryInTemplateMenuManager.NoTemplate" ), Action.AS_CHECK_BOX ) //$NON-NLS-1$
            {
            };
            noTemplateAction.setEnabled( false );
            menuManager.add( noTemplateAction );
        }

        // Separator
        menuManager.add( new Separator() );

        // Preferences Action
        menuManager.add( new EntryTemplatePreferencePageAction() );
    }


    /**
     * Created the action.
     *
     * @param templateEditorWidget
     *      the template editor widget
     * @param template
     *      the template
     * @param isChecked
     *      <code>true</code> if the action is checked,
     *      <code>false</code> if not
     * @return
     *      the associated action
     */
    private static IAction createAction( TemplateEditorWidget templateEditorWidget, Template template, boolean isChecked )
    {
        Action action = new SwitchTemplateAction( templateEditorWidget, template );
        action.setChecked( isChecked );

        return action;
    }
}
