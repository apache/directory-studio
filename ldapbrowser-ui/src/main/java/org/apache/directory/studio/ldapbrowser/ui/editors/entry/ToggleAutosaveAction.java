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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.action.Action;


/**
 * This action is used to toggle the "auto-save" preference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ToggleAutosaveAction extends Action
{

    private EntryEditor entryEditor;


    /**
     * Creates a new instance of ToggleAutosaveAction.
     */
    public ToggleAutosaveAction( EntryEditor entryEditor )
    {
        super( Messages.getString( "ToggleAutosaveAction.Autosave" ), AS_CHECK_BOX ); //$NON-NLS-1$
        setToolTipText( getText() );
        setEnabled( true );
        this.entryEditor = entryEditor;
    }


    @Override
    public void run()
    {
        BrowserCommonActivator.getDefault().getPreferenceStore().setValue( getConstant(), super.isChecked() );
    }


    private String getConstant()
    {
        boolean multiTab = entryEditor.getEntryEditorInput().getExtension().isMultiWindow();
        if ( multiTab )
        {
            return BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTOSAVE_MULTI_TAB;
        }
        else
        {
            return BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTOSAVE_SINGLE_TAB;
        }
    }


    public void updateSetChecked()
    {
        setChecked( BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean( getConstant() ) );
    }

}
