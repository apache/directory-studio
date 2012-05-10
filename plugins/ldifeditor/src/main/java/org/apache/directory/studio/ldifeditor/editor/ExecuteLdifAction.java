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

package org.apache.directory.studio.ldifeditor.editor;


import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectBrowserConnectionDialog;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * This Action executes LDIF code.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExecuteLdifAction extends Action
{
    /** The LDIF Editor */
    private LdifEditor editor;


    /**
     * Creates a new instance of ExecuteLdifAction.
     *
     * @param editor
     *      the attached editor
     */
    public ExecuteLdifAction( LdifEditor editor )
    {
        super(
            Messages.getString( "ExecuteLdifAction.ExecuteLDIF" ), LdifEditorActivator.getDefault().getImageDescriptor( LdifEditorConstants.IMG_EXECUTE ) ); //$NON-NLS-1$
        super.setToolTipText( Messages.getString( "ExecuteLdifAction.ExecuteLDIF" ) ); //$NON-NLS-1$
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IBrowserConnection connection = editor.getConnection();

        // Checking if we already have a connection
        if ( connection == null )
        {
            // Requesting the user to select a connection
            SelectBrowserConnectionDialog dialog = new SelectBrowserConnectionDialog( editor.getSite().getShell(),
                Messages.getString( "ExecuteLdifAction.SelectConnection" ), null ); //$NON-NLS-1$
            if ( dialog.open() == SelectBrowserConnectionDialog.OK )
            {
                connection = dialog.getSelectedBrowserConnection();
                
                if ( connection != null )
                {
                    editor.setConnection( connection, true );
                }
            }

            // Checking a second time if we  have a connection
            if ( connection == null )
            {
                return;
            }
        }
        

        String ldif = editor.getLdifModel().toRawString();

        IPreferenceStore preferenceStore = LdifEditorActivator.getDefault().getPreferenceStore();
        boolean updateIfEntryExistsButton = preferenceStore
            .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS );
        boolean continueOnErrorButton = preferenceStore
            .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR );

        ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( connection, ldif, updateIfEntryExistsButton,
            continueOnErrorButton );
        StudioBrowserJob job = new StudioBrowserJob( runnable );
        job.execute();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return editor != null;
    }
}
