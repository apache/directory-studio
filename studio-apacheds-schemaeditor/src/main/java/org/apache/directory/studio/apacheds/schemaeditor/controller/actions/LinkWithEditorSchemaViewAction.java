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


import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder.FolderType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This action is used to link the with the view with the frontmost editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LinkWithEditorSchemaViewAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The String for storing the checked state of the action */
    private static final String LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY = LinkWithEditorSchemaViewAction.class.getName()
        + ".dialogsettingkey"; //$NON-NLS-1$

    /** The associated view */
    private SchemaView view;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
            IWorkbenchPart part = partRef.getPart( true );

            if ( part instanceof ObjectClassEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( ObjectClassEditor ) part ).getOriginalObjectClass() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( part instanceof AttributeTypeEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( AttributeTypeEditor ) part ).getOriginalAttributeType() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( part instanceof SchemaEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( SchemaEditor ) part ).getSchema() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }
    };

    /** The listener listening on changes on the view */
    private ISelectionListener viewListener = new ISelectionListener()
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
         *      org.eclipse.jface.viewers.ISelection)
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            ITreeSelection iSelection = ( ITreeSelection ) selection;

            Object selectedObject = iSelection.getFirstElement();

            if ( ( selectedObject instanceof SchemaWrapper ) || ( selectedObject instanceof ObjectClassWrapper )
                || ( selectedObject instanceof AttributeTypeWrapper ) )
            {
                linkEditorWithView( ( TreeNode ) selectedObject );
            }
        }
    };


    /**
     * Creates a new instance of ExportSchemasAsXmlAction.
     */
    public LinkWithEditorSchemaViewAction( SchemaView view )
    {
        super( "Lin&k with Editor", AS_CHECK_BOX );
        setToolTipText( "Link with Editor" );
        setId( PluginConstants.CMD_LINK_WITH_EDITOR_SCHEMA_VIEW );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( false );
        this.view = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY, false );
        }

        // Setting state from the dialog settings
        setChecked( Activator.getDefault().getDialogSettings().getBoolean( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY ) );

        // Enabling the listeners
        if ( isChecked() )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );
            view.getSite().getPage().addPostSelectionListener( viewListener );
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( LINK_WITH_EDITOR_SCHEMAS_VIEW_DS_KEY, isChecked() );

        if ( isChecked() ) // Enabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );

            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
            if ( activeEditor instanceof ObjectClassEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( ObjectClassEditor ) activeEditor ).getOriginalObjectClass() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( activeEditor instanceof AttributeTypeEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( AttributeTypeEditor ) activeEditor ).getOriginalAttributeType() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }
            else if ( activeEditor instanceof SchemaEditor )
            {
                view.getSite().getPage().removePostSelectionListener( viewListener );
                linkViewWithEditor( ( ( SchemaEditor ) activeEditor ).getSchema() );
                view.getSite().getPage().addPostSelectionListener( viewListener );
            }

            view.getSite().getPage().addPostSelectionListener( viewListener );
        }
        else
        // Disabling the listeners
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
            view.getSite().getPage().removePostSelectionListener( viewListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param o
     *      the object
     */
    private void linkViewWithEditor( Object o )
    {
        TreeNode wrapper = null;

        if ( o instanceof AttributeTypeImpl )
        {
            wrapper = findAttributeTypeWrapperInTree( ( AttributeTypeImpl ) o );
        }
        else if ( o instanceof ObjectClassImpl )
        {
            wrapper = findObjectClassWrapperInTree( ( ObjectClassImpl ) o );
        }
        else if ( o instanceof Schema )
        {
            wrapper = findSchemaWrapperInTree( ( ( Schema ) o ).getName() );
        }

        if ( wrapper != null )
        {
            view.getViewer().setSelection( new StructuredSelection( wrapper ) );
        }
    }


    /**
     * Links the editor to the view
     *
     * @param wrapper
     *      the selected element in the view
     */
    private void linkEditorWithView( TreeNode wrapper )
    {
        IEditorReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .getEditorReferences();

        for ( IEditorReference reference : references )
        {
            IWorkbenchPart workbenchPart = reference.getPart( true );

            if ( ( workbenchPart instanceof ObjectClassEditor ) && ( wrapper instanceof ObjectClassWrapper ) )
            {
                ObjectClassEditor editor = ( ObjectClassEditor ) workbenchPart;
                ObjectClassWrapper ocw = ( ObjectClassWrapper ) wrapper;
                if ( editor.getOriginalObjectClass().equals( ocw.getObjectClass() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
            else if ( ( workbenchPart instanceof AttributeTypeEditor ) && ( wrapper instanceof AttributeTypeWrapper ) )
            {
                AttributeTypeEditor editor = ( AttributeTypeEditor ) workbenchPart;
                AttributeTypeWrapper atw = ( AttributeTypeWrapper ) wrapper;
                if ( editor.getOriginalAttributeType().equals( atw.getAttributeType() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
            else if ( ( workbenchPart instanceof SchemaEditor ) && ( wrapper instanceof SchemaWrapper ) )
            {
                SchemaEditor editor = ( SchemaEditor ) workbenchPart;
                SchemaWrapper sw = ( SchemaWrapper ) wrapper;
                if ( editor.getSchema().equals( sw.getSchema() ) )
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( workbenchPart );
                    return;
                }
            }
        }
    }


    /**
     * Finds the corresponding SchemaWrapper in the Tree.
     *
     * @param name
     *      the name of the SchemaWrapper to search
     * @return
     *      the corresponding SchemaWrapper in the Tree
     */
    private SchemaWrapper findSchemaWrapperInTree( String name )
    {
        List<TreeNode> schemaWrappers = ( ( TreeNode ) view.getViewer().getInput() ).getChildren();
        for ( TreeNode sw : schemaWrappers )
        {
            if ( ( ( SchemaWrapper ) sw ).getSchema().getName().toLowerCase().equals( name.toLowerCase() ) )
            {
                return ( SchemaWrapper ) sw;
            }
        }

        return null;
    }


    /**
     * Finds the corresponding AttributeTypeWrapper in the Tree.
     *
     * @param at
     *      the attribute type
     * @return
     *      the corresponding AttributeTypeWrapper in the Tree
     */
    private AttributeTypeWrapper findAttributeTypeWrapperInTree( AttributeTypeImpl at )
    {
        SchemaWrapper schemaWrapper = findSchemaWrapperInTree( at.getSchema() );
        if ( schemaWrapper == null )
        {
            return null;
        }

        // Finding the correct node
        int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        List<TreeNode> children = schemaWrapper.getChildren();
        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
        {
            for ( TreeNode child : children )
            {
                Folder folder = ( Folder ) child;
                if ( folder.getType() == FolderType.ATTRIBUTE_TYPE )
                {
                    for ( TreeNode folderChild : folder.getChildren() )
                    {
                        AttributeTypeWrapper atw = ( AttributeTypeWrapper ) folderChild;
                        if ( atw.getAttributeType().equals( at ) )
                        {
                            return atw;
                        }
                    }
                }
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
        {
            for ( Object child : children )
            {
                if ( child instanceof AttributeTypeImpl )
                {
                    AttributeTypeWrapper atw = ( AttributeTypeWrapper ) child;
                    if ( atw.getAttributeType().equals( at ) )
                    {
                        return atw;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Finds the corresponding ObjectClassWrapper in the Tree.
     *
     * @param oc
     *      the attribute type
     * @return
     *      the corresponding ObjectClassWrapper in the Tree
     */
    private ObjectClassWrapper findObjectClassWrapperInTree( ObjectClassImpl oc )
    {
        SchemaWrapper schemaWrapper = findSchemaWrapperInTree( oc.getSchema() );
        if ( schemaWrapper == null )
        {
            return null;
        }

        // Finding the correct node
        int group = Activator.getDefault().getPreferenceStore().getInt( PluginConstants.PREFS_SCHEMA_VIEW_GROUPING );
        List<TreeNode> children = schemaWrapper.getChildren();
        if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_FOLDERS )
        {
            for ( TreeNode child : children )
            {
                Folder folder = ( Folder ) child;
                if ( folder.getType() == FolderType.OBJECT_CLASS )
                {
                    for ( TreeNode folderChild : folder.getChildren() )
                    {
                        ObjectClassWrapper ocw = ( ObjectClassWrapper ) folderChild;
                        if ( ocw.getObjectClass().equals( oc ) )
                        {
                            return ocw;
                        }
                    }
                }
            }
        }
        else if ( group == PluginConstants.PREFS_SCHEMA_VIEW_GROUPING_MIXED )
        {
            for ( Object child : children )
            {
                if ( child instanceof ObjectClassWrapper )
                {
                    ObjectClassWrapper ocw = ( ObjectClassWrapper ) child;
                    if ( ocw.getObjectClass().equals( oc ) )
                    {
                        return ocw;
                    }
                }
            }
        }

        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
