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
package org.apache.directory.studio.apacheds.schemaeditor.controller.actions;


import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.HierarchyView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Link With Editor Action for the Hierarchy View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LinkWithEditorHierarchyViewAction extends Action
{
    /** The String for storing the checked state of the action */
    private static final String LINK_WITH_EDITOR_HIERARCHY_VIEW_DS_KEY = LinkWithEditorHierarchyViewAction.class
        .getName()
        + ".dialogsettingkey"; //$NON-NLS-1$

    /** The associated view */
    private HierarchyView view;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
            IWorkbenchPart part = partRef.getPart( true );

            if ( part instanceof ObjectClassEditor )
            {
                linkViewWithEditor( ( ( ObjectClassEditor ) part ).getOriginalObjectClass() );
            }
            else if ( part instanceof AttributeTypeEditor )
            {
                linkViewWithEditor( ( ( AttributeTypeEditor ) part ).getOriginalAttributeType() );
            }
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }
    };


    /**
     * Creates a new instance of LinkWithEditorSchemasView.
     *
     * @param view
     *      the associated view
     */
    public LinkWithEditorHierarchyViewAction( HierarchyView view )
    {
        super( "Lin&k with Editor", AS_CHECK_BOX );
        setToolTipText( "Link with Editor" );
        setId( PluginConstants.CMD_LINK_WITH_EDITOR_HIERARCHY_VIEW );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( true );
        this.view = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( LINK_WITH_EDITOR_HIERARCHY_VIEW_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_HIERARCHY_VIEW_DS_KEY, false );
        }

        // Setting state from the dialog settings
        setChecked( Activator.getDefault().getDialogSettings().getBoolean( LINK_WITH_EDITOR_HIERARCHY_VIEW_DS_KEY ) );

        // Enabling the listeners
        if ( isChecked() )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_HIERARCHY_VIEW_DS_KEY, isChecked() );

        if ( isChecked() ) // Enabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );

            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
            if ( activeEditor instanceof ObjectClassEditor )
            {
                linkViewWithEditor( ( ( ObjectClassEditor ) activeEditor ).getOriginalObjectClass() );
            }
            else if ( activeEditor instanceof AttributeTypeEditor )
            {
                linkViewWithEditor( ( ( AttributeTypeEditor ) activeEditor ).getOriginalAttributeType() );
            }
        }
        else
        // Disabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param schemaElement
     *      the Schema Element
     */
    private void linkViewWithEditor( SchemaObject schemaElement )
    {
        if ( schemaElement instanceof AttributeTypeImpl )
        {
            view.setInput( schemaElement );
        }
        else if ( schemaElement instanceof ObjectClassImpl )
        {
            view.setInput( schemaElement );
        }
    }
}
