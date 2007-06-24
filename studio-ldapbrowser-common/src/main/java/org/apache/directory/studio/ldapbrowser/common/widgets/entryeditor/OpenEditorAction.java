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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import java.util.Arrays;

import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * The OpenEditorAction is used to edit a value with a specific value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenEditorAction extends AbstractOpenEditorAction
{

    /** The specific value editor. */
    private IValueEditor valueEditor;


    /**
     * Creates a new instance of OpenEditorAction.
     * 
     * @param viewer the viewer
     * @param valueEditorManager the value editor manager
     * @param valueEditor the specific value editor
     */
    public OpenEditorAction( TreeViewer viewer, ValueEditorManager valueEditorManager, IValueEditor valueEditor )
    {
        super( viewer, valueEditorManager );
        super.cellEditor = valueEditor.getCellEditor();
        this.valueEditor = valueEditor;
    }


    /**
     * Gets the value editor.
     * 
     * @return the value editor
     */
    public IValueEditor getValueEditor()
    {
        return valueEditor;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.AbstractOpenEditorAction#run()
     */
    public void run()
    {
        // ensure that the specific value editor is activated 
        valueEditorManager.setUserSelectedValueEditor( valueEditor );

        super.run();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.AbstractOpenEditorAction#dispose()
     */
    public void dispose()
    {
        this.valueEditor = null;
        super.dispose();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getCommandId()
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        return valueEditor.getValueEditorImageDescriptor();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#getText()
     */
    public String getText()
    {
        return valueEditor.getValueEditorName();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#isEnabled()
     */
    public boolean isEnabled()
    {
        if ( getSelectedValues().length == 1
            && getSelectedAttributes().length == 0
            && viewer.getCellModifier().canModify( getSelectedValues()[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {
            IValueEditor[] alternativeVps = valueEditorManager.getAlternativeValueEditors( getSelectedValues()[0] );
            return Arrays.asList( alternativeVps ).contains( valueEditor )
                && valueEditor.getRawValue( getSelectedValues()[0] ) != null;
        }
        else
        {
            return false;
        }
    }

}
