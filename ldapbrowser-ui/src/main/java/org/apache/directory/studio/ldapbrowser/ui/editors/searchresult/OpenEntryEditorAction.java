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


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.wizards.EditEntryWizard;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;


/**
 * Action to open the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenEntryEditorAction extends AbstractOpenEditorAction
{

    /** The value editor. */
    private IValueEditor valueEditor;


    /**
     * Creates a new instance of OpenEntryEditorAction.
     * 
     * @param viewer the viewer
     * @param cursor the cursor
     * @param valueEditorManager the value editor manager
     * @param valueEditor the value editor
     * @param actionGroup the action group
     */
    public OpenEntryEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
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
        IEntry entry = getSelectedSearchResults().length > 0 ? getSelectedSearchResults()[0].getEntry() : null;
        if ( entry != null )
        {
            // disable action handlers
            actionGroup.deactivateGlobalActionHandlers();

            EditEntryWizard wizard = new EditEntryWizard( entry );
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            dialog.open();

            // enable action handlers
            actionGroup.activateGlobalActionHandlers();
        }
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
        return BrowserCommonConstants.ACTION_ID_EDIT_RECORD;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Edit Entry...";
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }

}
