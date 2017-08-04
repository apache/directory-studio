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

package org.apache.directory.studio.ldapbrowser.ui.views.browser;


import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.editors.entry.EntryEditor;
import org.apache.directory.studio.ldapbrowser.ui.editors.searchresult.SearchResultEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements common aspects for the Link With Editor Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractLinkWithEditorAction extends Action
{
    /** The view */
    private ViewPart viewPart;

    /** The listener listening on opening/closing editors */
    private IPartListener partListener = new IPartListener()
    {

        @Override
        public void partOpened( IWorkbenchPart part )
        {
            registerPropertyListener( part );

        }


        @Override
        public void partClosed( IWorkbenchPart part )
        {
            unregisterPropertyListener( part );
        }


        @Override
        public void partDeactivated( IWorkbenchPart part )
        {
        }


        @Override
        public void partBroughtToTop( IWorkbenchPart part )
        {
        }


        @Override
        public void partActivated( IWorkbenchPart part )
        {
        }
    };

    /** The listener listening on input changes in editors */
    private IPropertyListener propertyListener = new IPropertyListener()
    {
        @Override
        public void propertyChanged( Object source, int propId )
        {
            if ( source instanceof IEditorPart && propId == BrowserUIConstants.INPUT_CHANGED )
            {
                linkViewWithEditor( ( IEditorPart ) source );
            }
        }
    };


    private void registerListeners()
    {
        // register part listeners
        viewPart.getSite().getWorkbenchWindow().getPartService().addPartListener( partListener );

        // register property listener
        IEditorReference[] editorReferences = viewPart.getSite().getPage().getEditorReferences();
        for ( IEditorReference editorReference : editorReferences )
        {
            IEditorPart editor = editorReference.getEditor( false );
            registerPropertyListener( editor );
        }
    }


    private void registerPropertyListener( IWorkbenchPart part )
    {
        if ( part instanceof EntryEditor || part instanceof SearchResultEditor )
        {
            part.addPropertyListener( propertyListener );
        }
    }


    private void unregisterListeners()
    {
        // unregister part listener
        viewPart.getSite().getWorkbenchWindow().getPartService().removePartListener( partListener );

        // unregister property listener
        IEditorReference[] editorReferences = viewPart.getSite().getPage().getEditorReferences();
        for ( IEditorReference editorReference : editorReferences )
        {
            IEditorPart editor = editorReference.getEditor( false );
            unregisterPropertyListener( editor );
        }
    }


    private void unregisterPropertyListener( IWorkbenchPart part )
    {
        if ( part instanceof EntryEditor || part instanceof SearchResultEditor )
        {
            part.removePropertyListener( propertyListener );
        }
    }


    public AbstractLinkWithEditorAction( ViewPart viewPart, String message )
    {
        super( message, AS_CHECK_BOX ); //$NON-NLS-1$
        setImageDescriptor(
            BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( true );
        setChecked( BrowserUIPlugin.getDefault().getPreferenceStore()
            .getBoolean( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );
        this.viewPart = viewPart;
    }


    protected void init()
    {
        // Enable the listeners
        if ( isChecked() )
        {
            registerListeners();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        setChecked( isChecked() );
        BrowserUIPlugin.getDefault().getPreferenceStore()
            .setValue( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR, isChecked() );

        if ( isChecked() )
        {
            // Enable the listener
            registerListeners();

            // link
            IEditorPart activeEditor = viewPart.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
            linkViewWithEditor( activeEditor );
        }
        else
        {
            // Disable the listener
            unregisterListeners();
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param partRef the part
     */
    protected abstract void linkViewWithEditor( IWorkbenchPart part );


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        if ( partListener != null && propertyListener != null )
        {
            unregisterListeners();
            propertyListener = null;
            partListener = null;
        }

        viewPart = null;
    }

}
