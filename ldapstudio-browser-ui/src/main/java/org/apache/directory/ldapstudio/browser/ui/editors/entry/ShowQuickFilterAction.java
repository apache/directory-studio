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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidgetQuickFilterWidget;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This action shows/hides the instant search.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ShowQuickFilterAction extends Action
{

    /** The Constant SHOW_QUICKFILTER_DIALOGSETTING_KEY. */
    public static final String SHOW_QUICKFILTER_DIALOGSETTING_KEY = ShowQuickFilterAction.class.getName()
        + ".showQuickFilter";

    /** The quick filter widget. */
    private EntryEditorWidgetQuickFilterWidget quickFilterWidget;


    /**
     * Creates a new instance of ShowQuickFilterAction.
     * 
     * @param quickFilterWidget the quick filter widget
     */
    public ShowQuickFilterAction( EntryEditorWidgetQuickFilterWidget quickFilterWidget )
    {
        super( "Show Quick Filter", AS_CHECK_BOX );
        setToolTipText( "Show Quick Filter" );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER ) );
        setActionDefinitionId( IWorkbenchActionDefinitionIds.FIND_REPLACE );
        setEnabled( true );

        this.quickFilterWidget = quickFilterWidget;

        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( SHOW_QUICKFILTER_DIALOGSETTING_KEY ) == null )
        {
            BrowserUIPlugin.getDefault().getDialogSettings().put( SHOW_QUICKFILTER_DIALOGSETTING_KEY, false );
        }

        // call the super implementation here because the local implementation
        // does nothing.
        super.setChecked( BrowserUIPlugin.getDefault().getDialogSettings().getBoolean(
            SHOW_QUICKFILTER_DIALOGSETTING_KEY ) );
        quickFilterWidget.setActive( isChecked() );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation toggles the checked state and 
     * activates or deactivates the quick filter accordingly. 
     */
    public void run()
    {
        boolean checked = isChecked();
        super.setChecked( !checked );

        BrowserUIPlugin.getDefault().getDialogSettings().put( SHOW_QUICKFILTER_DIALOGSETTING_KEY, isChecked() );

        if ( quickFilterWidget != null )
        {
            quickFilterWidget.setActive( isChecked() );
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation does nothing. Toggling of the checked state is done within the run() method.
     */
    public void setChecked( boolean checked )
    {
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        quickFilterWidget = null;
    }

}
