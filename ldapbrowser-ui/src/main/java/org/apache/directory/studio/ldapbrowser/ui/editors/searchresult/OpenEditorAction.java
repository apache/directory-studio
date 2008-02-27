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


public class OpenEditorAction extends AbstractOpenEditorAction
{

    private IValueEditor valueEditor;


    public OpenEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, IValueEditor valueEditor, SearchResultEditorActionGroup actionGroup )
    {
        super( viewer, cursor, valueEditorManager, actionGroup );
        super.cellEditor = valueEditor.getCellEditor();
        this.valueEditor = valueEditor;
    }


    public IValueEditor getValueEditor()
    {
        return this.valueEditor;
    }


    public void run()
    {
        this.valueEditorManager.setUserSelectedValueEditor( this.valueEditor );
        super.run();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction#dispose()
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
                return Arrays.asList( alternativeVps ).contains( this.valueEditor )
                    && valueEditor.getRawValue( ah ) != null;
            }
        }
        else
        {
            return false;
        }
    }

}
