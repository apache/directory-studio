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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.actions.DeleteAllValuesAction;
import org.apache.directory.studio.ldapbrowser.common.actions.NewAttributeAction;
import org.apache.directory.studio.ldapbrowser.common.actions.proxy.EntryEditorActionProxy;
import org.apache.directory.studio.utils.ActionUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * This class manages all the actions of the attribute page of the new entry wizard.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorWidgetActionGroupWithAttribute extends EntryEditorWidgetActionGroup
{

    /** The Constant editAttributeDescriptionAction. */
    private static final String EDIT_ATTRIBUTE_DESCRIPTION_ACTION = "editAttributeDescriptionAction"; //$NON-NLS-1$

    /** The Constant newAttributeAction. */
    private static final String NEW_ATTRIBUTE_ACTION = "newAttributeAction"; //$NON-NLS-1$

    /** The Constant deleteAllValuesAction. */
    private static final String DELETE_ALL_VALUES_ACTION = "deleteAllValuesAction"; //$NON-NLS-1$


    /**
     * 
     * Creates a new instance of NewEntryAttributesWizardPageActionGroup.
     *
     * @param mainWidget
     * @param configuration
     */
    public EntryEditorWidgetActionGroupWithAttribute( EntryEditorWidget mainWidget,
        EntryEditorWidgetConfiguration configuration )
    {
        super( mainWidget, configuration );
        TreeViewer viewer = mainWidget.getViewer();

        entryEditorActionMap.put( EDIT_ATTRIBUTE_DESCRIPTION_ACTION, new EntryEditorActionProxy( viewer,
            new EditAttributeDescriptionAction( viewer ) ) );
        entryEditorActionMap.put( NEW_ATTRIBUTE_ACTION, new EntryEditorActionProxy( viewer, new NewAttributeAction() ) );
        entryEditorActionMap.put( DELETE_ALL_VALUES_ACTION, new EntryEditorActionProxy( viewer,
            new DeleteAllValuesAction() ) );
    }


    /**
     * {@inheritDoc}
     */
    public void fillToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( NEW_VALUE_ACTION ) );
        toolBarManager.add( ( IAction ) entryEditorActionMap.get( NEW_ATTRIBUTE_ACTION ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( DELETE_ACTION ) );
        toolBarManager.add( ( IAction ) this.entryEditorActionMap.get( DELETE_ALL_VALUES_ACTION ) );
        toolBarManager.add( new Separator() );
        toolBarManager.add( this.showQuickFilterAction );
        toolBarManager.update( true );
    }


    /**
     * {@inheritDoc}
     */
    protected void contextMenuAboutToShow( IMenuManager menuManager )
    {
        // new
        menuManager.add( ( IAction ) entryEditorActionMap.get( NEW_ATTRIBUTE_ACTION ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( NEW_VALUE_ACTION ) );
        menuManager.add( new Separator() );

        // copy, paste, delete
        menuManager.add( ( IAction ) entryEditorActionMap.get( COPY_ACTION ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( PASTE_ACTION ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( DELETE_ACTION ) );
        menuManager.add( ( IAction ) entryEditorActionMap.get( SELECT_ALL_ACTION ) );
        MenuManager copyMenuManager = new MenuManager( Messages
            .getString( "EntryEditorWidgetActionGroupWithAttribute.Advanced" ) ); //$NON-NLS-1$
        copyMenuManager.add( ( IAction ) entryEditorActionMap.get( DELETE_ALL_VALUES_ACTION ) );
        menuManager.add( copyMenuManager );
        menuManager.add( new Separator() );

        // edit
        menuManager.add( ( IAction ) entryEditorActionMap.get( EDIT_ATTRIBUTE_DESCRIPTION_ACTION ) );
        super.addEditMenu( menuManager );
        menuManager.add( new Separator() );

        // properties
        menuManager.add( ( IAction ) entryEditorActionMap.get( PROPERTY_DIALOG_ACTION ) );
    }


    /**
     * {@inheritDoc}
     */
    public void activateGlobalActionHandlers()
    {
        super.activateGlobalActionHandlers();

        IAction naa = ( IAction ) entryEditorActionMap.get( NEW_ATTRIBUTE_ACTION );
        ActionUtils.activateActionHandler( naa );
        IAction eada = ( IAction ) entryEditorActionMap.get( EDIT_ATTRIBUTE_DESCRIPTION_ACTION );
        ActionUtils.activateActionHandler( eada );
    }


    /**
     * {@inheritDoc}
     */
    public void deactivateGlobalActionHandlers()
    {
        super.deactivateGlobalActionHandlers();

        IAction naa = ( IAction ) entryEditorActionMap.get( NEW_ATTRIBUTE_ACTION );
        ActionUtils.deactivateActionHandler( naa );
        IAction eada = ( IAction ) entryEditorActionMap.get( EDIT_ATTRIBUTE_DESCRIPTION_ACTION );
        ActionUtils.deactivateActionHandler( eada );
    }

}
