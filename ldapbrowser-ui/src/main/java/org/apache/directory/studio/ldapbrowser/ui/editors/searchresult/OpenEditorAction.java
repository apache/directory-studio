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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import java.util.Arrays;

import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;


/**
 * The OpenEditorAction is used to edit a value with a specific value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenEditorAction extends AbstractOpenEditorAction
{

    /** The value editor. */
    private IValueEditor valueEditor;


    /**
     * Creates a new instance of OpenEditorAction.
     * 
     * @param viewer the viewer
     * @param cursor the cursor
     * @param valueEditorManager the value editor manager
     * @param valueEditor the value editor
     * @param actionGroup the action group
     */
    public OpenEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, IValueEditor valueEditor, SearchResultEditorActionGroup actionGroup )
    {
        super( viewer, cursor, valueEditorManager, actionGroup );
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
     * {@inheritDoc}
     */
    public void run()
    {
        valueEditorManager.setUserSelectedValueEditor( valueEditor );
        super.run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        valueEditor = null;
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return valueEditor.getValueEditorImageDescriptor();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return valueEditor.getValueEditorName();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getSelectedSearchResults().length == 1 && getSelectedProperties().length == 1
            && viewer.getCellModifier().canModify( getSelectedSearchResults()[0], getSelectedProperties()[0] ) )
        {
            IValueEditor[] alternativeVps;
            if ( getSelectedAttributeHierarchies().length == 0 )
            {
                return false;
            }
            else
            {
                AttributeHierarchy ah = getSelectedAttributeHierarchies()[0];
                alternativeVps = valueEditorManager.getAlternativeValueEditors( ah );
                return Arrays.asList( alternativeVps ).contains( valueEditor ) && valueEditor.getRawValue( ah ) != null;
            }
        }
        else
        {
            return false;
        }
    }

}
