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


import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseAttributes;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseDn;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseFilter;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhatClauseAttributesComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhatClauseDnComposite;
import org.apache.directory.studio.openldap.config.acl.widgets.composites.WhatClauseFilterComposite;


/**
 * The WhatClause widget. It coves all the What possible options :
 * <ul>
 * <li>DN</li>
 * <li>Filter</li>
 * <li>Attributes</li>
 * </ul>
 * The three possible options, when selected, will open new composites dynamically.
 * 
 * <pre>
 * </pre>
 * .---------------------------------------------------------.
 * |                                                         |
 * | [ ] DN                                                  |
 * |                                                         |
 * | [ ] Filter                                              |
 * |                                                         |
 * | [ ] Attributes                                          |
 * |                                                         |
 * `---------------------------------------------------------'
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclWhatClauseWidget extends AbstractWidget
{
    /** The visual editor composite */
    private OpenLdapAclVisualEditorComposite visualEditorComposite;

    /** The context */
    private OpenLdapAclValueWithContext context;

    // UI widgets
    private Composite composite;
    private Button dnCheckbox;
    private Composite dnComposite;
    private Composite dnSubComposite;
    private Button filterCheckbox;
    private Composite filterComposite;
    private Composite filterSubComposite;
    private Button attributesCheckbox;
    private Composite attributesComposite;
    private Composite attributesSubComposite;

    private WhatClauseAttributesComposite attributesClauseComposite;

    private WhatClauseFilterComposite filterClauseComposite;

    private WhatClauseDnComposite dnClauseComposite;

    // Listeners
    private SelectionAdapter dnCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
        {
            if ( dnCheckbox.getSelection() )
            {
                createDnComposite();
            }
            else
            {
                disposeComposite( dnSubComposite );
            }

            // Refreshing the layout of the whole composite
            visualEditorComposite.layout( true, true );
        }
    };
    
    
    private SelectionAdapter filterCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
        {
            if ( filterCheckbox.getSelection() )
            {
                createFilterComposite();
            }
            else
            {
                disposeComposite( filterSubComposite );
            }

            // Refreshing the layout of the whole composite
            visualEditorComposite.layout( true, true );
        }
    };
    
    
    /**
     * The listener on the Attributes Checkbox. It creates the Attributes composite
     * when selected, dispose it when unchecked.
     */
    private SelectionAdapter attributesCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( org.eclipse.swt.events.SelectionEvent e )
        {
            if ( attributesCheckbox.getSelection() )
            {
                createAttributesComposite();
            }
            else
            {
                disposeComposite( attributesSubComposite );
            }

            // Refreshing the layout of the whole composite
            visualEditorComposite.layout( true, true );
        }
    };


    /**
     * Creates a new instance of OpenLdapAclWhatClauseWidget. It's just a list of
     * 3 checkboxes which, when selected, open a new composite dynamically created.
     * 
     * <pre>
     * .---------------------------------------------------------.
     * |                                                         |
     * | [ ] DN                                                  |
     * |                                                         |
     * | [ ] Filter                                              |
     * |                                                         |
     * | [ ] Attributes                                          |
     * |                                                         |
     * `---------------------------------------------------------'
     * </pre>
     * 
     * @param visualEditorComposite the visual editor composite
     * @param parent The WhatClause parent's composite
     * @param context the Acl context
     */
    public OpenLdapAclWhatClauseWidget( OpenLdapAclVisualEditorComposite visualEditorComposite, 
        Composite parent, OpenLdapAclValueWithContext context )
    {
        this.visualEditorComposite = visualEditorComposite;
        this.context = context;
        
        // Creating the widget base composite
        composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Creating the what group
        Group whatGroup = BaseWidgetUtils.createGroup( parent, "Acces to \"What\"", 1 );
        whatGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DN
        dnCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "DN", 1 );
        dnComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );
        dnCheckbox.addSelectionListener( dnCheckboxListener );

        // Filter
        filterCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "Filter", 1 );
        filterComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );
        filterCheckbox.addSelectionListener( filterCheckboxListener );

        // Attributes
        attributesCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "Attributes", 1 );
        attributesComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );
        attributesCheckbox.addSelectionListener( attributesCheckboxListener );
    }


    /**
     * Creates the DN composite.
     */
    private void createDnComposite()
    {
        Group dnGroup = BaseWidgetUtils.createGroup( dnComposite, "", 1 );
        dnGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        dnSubComposite = dnGroup;

        dnClauseComposite = new WhatClauseDnComposite( context, visualEditorComposite );
        dnClauseComposite.createComposite( dnGroup );

        /*
        AclWhatClause whatClause = context.getAclItem().getWhatClause();
        
        if ( whatClause.getDnClause() != null )
        {
            dnClauseComposite.setClause( whatClause.getDnClause() );
        }

        if ( context != null )
        {
            dnClauseComposite.setConnection( context.getConnection() );
        }
        */
    }


    /**
     * Creates the filter composite.
     */
    private void createFilterComposite()
    {
        Group filterGroup = BaseWidgetUtils.createGroup( filterComposite, "", 1 );
        filterGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        filterSubComposite = filterGroup;

        filterClauseComposite = new WhatClauseFilterComposite( context, visualEditorComposite );
        filterClauseComposite.createComposite( filterGroup );

        /*
        AclWhatClause whatClause = context.getAclItem().getWhatClause();

        if ( whatClause.getFilterClause() != null )
        {
            filterClauseComposite.setClause( whatClause.getFilterClause() );
        }

        if ( context != null )
        {
            filterClauseComposite.setConnection( context.getConnection() );
        }
        */
    }


    /**
     * Creates the attributes composite.
     */
    private void createAttributesComposite()
    {
        attributesSubComposite = BaseWidgetUtils.createGroup( attributesComposite, "", 1 );
        attributesSubComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        attributesClauseComposite = new WhatClauseAttributesComposite( visualEditorComposite, attributesSubComposite, context );

        /*
        AclWhatClause whatClause = context.getAclItem().getWhatClause();

        if ( whatClause.getAttributesClause() != null )
        {
            attributesClauseComposite.setClause( whatClause.getAttributesClause() );
        }

        if ( context != null )
        {
            attributesClauseComposite.setConnection( context.getConnection() );
        }
        */
    }


    /**
     * Disposes the given composite.
     *
     * @param composite the composite
     */
    private void disposeComposite( Composite composite )
    {
        if ( ( composite != null ) && ( !composite.isDisposed() ) )
        {
            composite.dispose();
        }
    }


    /**
     * Refresh the WhatClause GUI
     */
    public void refresh()
    {
        AclWhatClause whatClause = context.getAclItem().getWhatClause();

        if ( whatClause != null )
        {
            // DN clause
        	if ( whatClause instanceof AclWhatClauseDn )
        	{
                dnCheckbox.setSelection( true );
                createDnComposite();
        	}
        	else if ( whatClause instanceof AclWhatClauseFilter )
        	{
        		// Filter clause
                filterCheckbox.setSelection( true );
                createFilterComposite();
            }
        	else if ( whatClause instanceof AclWhatClauseAttributes )
        	{
        		// Attributes clause
                attributesCheckbox.setSelection( true );
                createAttributesComposite();
            }
        }
    }


    /**
     * Disposes all created SWT widgets.
     */
    public void dispose()
    {
        // Composite
        if ( ( composite != null ) && ( !composite.isDisposed() ) )
        {
            composite.dispose();
        }
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        if ( attributesClauseComposite != null )
        {
            attributesClauseComposite.saveWidgetSettings();
        }
    }
}
