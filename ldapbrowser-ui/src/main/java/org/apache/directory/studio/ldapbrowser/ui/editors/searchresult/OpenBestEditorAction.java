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


import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;


/**
 * The OpenBestEditorAction is used to edit a value with the best value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    /** The best value editor. */
    private IValueEditor bestValueEditor;


    /**
     * Creates a new instance of OpenBestEditorAction.
     * 
     * @param viewer the viewer
     * @param cursor the cursor
     * @param valueEditorManager the value editor manager
     * @param actionGroup the action group
     */
    public OpenBestEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, SearchResultEditorActionGroup actionGroup )
    {
        super( viewer, cursor, valueEditorManager, actionGroup );
    }


    /**
     * Gets the best value editor.
     * 
     * @return the best value editor
     */
    public IValueEditor getBestValueEditor()
    {
        return this.bestValueEditor;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        bestValueEditor = null;
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
        return isEnabled() ? bestValueEditor.getValueEditorImageDescriptor() : null;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return isEnabled() ? bestValueEditor.getValueEditorName() : null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getSelectedSearchResults().length == 1 && getSelectedProperties().length == 1
            && viewer.getCellModifier().canModify( getSelectedSearchResults()[0], getSelectedProperties()[0] ) )
        {
            if ( getSelectedAttributeHierarchies().length == 0 )
            {
                bestValueEditor = valueEditorManager.getCurrentValueEditor( getSelectedSearchResults()[0].getEntry(),
                    getSelectedProperties()[0] );
            }
            else
            {
                bestValueEditor = valueEditorManager.getCurrentValueEditor( getSelectedAttributeHierarchies()[0] );
            }

            super.cellEditor = bestValueEditor.getCellEditor();
            return true;
        }
        else
        {
            super.cellEditor = null;
            return false;
        }
    }

}
