/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseStar;


/**
 * The WhoClause widget builder
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclWhoClausesBuilderWidget
{
    /** The ACL context */
    private OpenLdapAclValueWithContext context;

    /** The visual editor composite */
    protected OpenLdapAclVisualEditorComposite visualEditorComposite;

    /** The list of clause widgets */
    private List<OpenLdapAclWhoClauseWidget> clauseWidgets = new ArrayList<OpenLdapAclWhoClauseWidget>();

    /** The list of separators */
    private List<Label> separatorWidgets = new ArrayList<Label>();

    // UI widgets
    private Group whoGroup;

    
    /**
     * A listener for the WhoClause widget
     */
    private WidgetModifyListener whoClauseModifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            // Getting the source widget
            OpenLdapAclWhoClauseWidget widget = ( OpenLdapAclWhoClauseWidget ) event.getSource();
            List<AclWhoClause> whoClauses = context.getAclItem().getWhoClauses();

            // Updating the clause
            whoClauses.remove( widget.getIndex() );
            whoClauses.add( widget.getIndex(), widget.getClause() );

            // Adjusting the layout of the visual editor composite
            visualEditorComposite.layout( true, true );
        }
    };
    

    /**
     * Creates a new instance of OpenLdapAclWhoClausesBuilderWidget.
     *
     * @param visualEditorComposite the visual editor composite
     */
    public OpenLdapAclWhoClausesBuilderWidget( OpenLdapAclVisualEditorComposite visualEditorComposite, OpenLdapAclValueWithContext context )
    {
        this.visualEditorComposite = visualEditorComposite;
        this.context = context;
    }


    /**
     * Create the UI.
     *
     * @param parent the parent composite
     */
    public void create( Composite parent )
    {
        // Creating the who group
        whoGroup = BaseWidgetUtils.createGroup( parent, "Acces by \"Who\"", 1 );
        whoGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Disposes the clause widgets.
     */
    private void disposeClausesWidgets()
    {
        // Disposing and removing clause widgets
        for ( OpenLdapAclWhoClauseWidget clauseWidget : clauseWidgets.toArray( new OpenLdapAclWhoClauseWidget[0] ) )
        {
            clauseWidget.dispose();
            clauseWidgets.remove( clauseWidget );
        }

        // Disposing and removing separators
        for ( Label separator : separatorWidgets.toArray( new Label[0] ) )
        {
            separator.dispose();
            separatorWidgets.remove( separator );
        }
    }


    /**
     * Creates the clause widgets.
     */
    private void createClauseWidgets()
    {
        // Checking the clauses
        List<AclWhoClause> whoClauses = context.getAclItem().getWhoClauses();
        
        if ( whoClauses.size() == 0 )
        {
            // Adding at least one default clause
            AclWhoClauseStar whoClause = new AclWhoClauseStar();
            
            whoClauses.add( whoClause );
        }

        // Creating a widget for each clause
        boolean isFirst = true;
        int pos = 0;
        
        for ( AclWhoClause whoClause : whoClauses )
        {
            // Creating a separator (except for the first row)
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                Label separator = new Label( whoGroup, SWT.SEPARATOR | SWT.HORIZONTAL );
                separator.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
                separatorWidgets.add( separator );
            }

            // Creating the clause widget
            OpenLdapAclWhoClauseWidget clauseWidget = new OpenLdapAclWhoClauseWidget( this, context, whoClause, pos );
            clauseWidget.create( whoGroup );
            
            clauseWidget.addWidgetModifyListener( whoClauseModifyListener );
            clauseWidgets.add( clauseWidget );
            pos++;
        }

        // Updating button states for specific rows (first, last and the case where there's only one row)
        if ( clauseWidgets.size() == 1 )
        {
            // There's only one row
            OpenLdapAclWhoClauseWidget widget = clauseWidgets.get( 0 );
            widget.getDeleteButton().setEnabled( false );
            widget.getMoveUpButton().setEnabled( false );
            widget.getMoveDownButton().setEnabled( false );
        }
        else
        {
            // There are more than 1 row
            OpenLdapAclWhoClauseWidget firstWidget = clauseWidgets.get( 0 );
            firstWidget.getMoveUpButton().setEnabled( false );
            OpenLdapAclWhoClauseWidget lastWidget = clauseWidgets.get( clauseWidgets.size() - 1 );
            lastWidget.getMoveDownButton().setEnabled( false );
        }
    }


    /**
     * Refreshes the clause widgets.
     */
    private void refreshWhoClauseWidgets()
    {
        // Disposing previous widgets and creating new ones
        disposeClausesWidgets();
        createClauseWidgets();

        // Adjusting the layout of the visual editor composite
        visualEditorComposite.layout( true, true );
    }


    /**
     * This method is called by a OpenLdapAclWhoClauseWidget when a
     * row needs to be added.
     *
     * @param widget the source widget
     */
    protected void addNewClause( OpenLdapAclWhoClauseWidget widget )
    {
        // Adding a new clause underneath the selected widget
        AclWhoClauseStar whoClause = new AclWhoClauseStar();
        context.getAclItem().getWhoClauses().add( whoClause );

        // Refreshing clauses widgets 
        refreshWhoClauseWidgets();
    }


    /**
     * This method is called by a OpenLdapAclWhoClauseWidget when a
     * row needs to be deleted.
     *
     * @param widget the source widget
     */
    protected void deleteClause( OpenLdapAclWhoClauseWidget widget )
    {
        int deletedIndex = widget.getIndex();
        
        // Deleting the selected widget
        context.getAclItem().getWhoClauses().remove( deletedIndex );

        // Refreshing clauses widgets 
        refreshWhoClauseWidgets();
    }


    /**
     * This method is called by a OpenLdapAclWhoClauseWidget when a
     * row needs to be moved up.
     *
     * @param widget the source widget
     */
    protected void moveUpClause( OpenLdapAclWhoClauseWidget widget )
    {
        // Swapping clauses
        int index = widget.getIndex();
        swapClauseIndexes( index, index - 1 );

        // Refreshing clauses widgets 
        refreshWhoClauseWidgets();
    }


    /**
     * This method is called by a OpenLdapAclWhoClauseWidget when a
     * row needs to be moved down.
     *
     * @param widget the source widget
     */
    protected void moveDownClause( OpenLdapAclWhoClauseWidget widget )
    {
        // Swapping clauses
        int index = widget.getIndex();
        swapClauseIndexes( index, index + 1 );

        // Refreshing clauses widgets 
        refreshWhoClauseWidgets();
    }


    /**
     * Swaps (exchanges) the clauses at the given indexes.
     *
     * @param sourceIndex the source index
     * @param destinationIndex the destination index
     */
    private void swapClauseIndexes( int sourceIndex, int destinationIndex )
    {
        // Getting clauses
        List<AclWhoClause> whoClauses = context.getAclItem().getWhoClauses();
        AclWhoClause sourceClause = whoClauses.get( sourceIndex );
        AclWhoClause destinationClause = whoClauses.get( destinationIndex );

        // Swapping clauses
        whoClauses.remove( sourceIndex );
        whoClauses.add(  sourceIndex, destinationClause );

        whoClauses.remove( destinationIndex );
        whoClauses.add(  destinationIndex, sourceClause );
    }
    
    
    /**
     * Sets the input.
     *
     * @param clauses the who clauses
     */
    public void refresh()
    {
        refreshWhoClauseWidgets();
    }


    /**
     * Disposes all UI widgets.
     */
    public void dispose()
    {
        // Disposing the who group
        if ( ( whoGroup != null ) && ( !whoGroup.isDisposed() ) )
        {
            whoGroup.dispose();
        }

        // Disposing the clause widgets
        disposeClausesWidgets();
    }
}
