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


import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.eclipse.jface.action.Action;

import org.apache.directory.studio.templateeditor.editor.TemplateEditorWidget;
import org.apache.directory.studio.templateeditor.model.Template;


/**
 * This Action switches the template editor widget to the given template.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SwitchTemplateAction extends Action
{
    /** The template editor widget */
    private TemplateEditorWidget templateEditorWidget;

    /** The template */
    private Template template;


    /**
     * Creates a new instance of SwitchTemplateAction.
     *
     * @param templateEditorWidget
     *      the template editor widget
     * @param template
     *      the template
     */
    public SwitchTemplateAction( TemplateEditorWidget templateEditorWidget, Template template )
    {
        super( template.getTitle(), Action.AS_CHECK_BOX );

        this.templateEditorWidget = templateEditorWidget;
        this.template = template;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( ( templateEditorWidget != null ) && ( template != null ) )
        {
            // Switching the template
            templateEditorWidget.switchTemplate( template );

            // Getting the associated editor
            IEntryEditor editor = templateEditorWidget.getEditor();
            if ( editor instanceof SwitchTemplateListener )
            {
                // Calling the listener
                ( ( SwitchTemplateListener ) editor ).templateSwitched( templateEditorWidget, template );
            }
        }
    }
}
