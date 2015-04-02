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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
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


public class OpenLdapAclWhatClauseWidget extends BrowserWidget
{
    /** The visual editor composite */
    private OpenLdapAclVisualEditorComposite visualEditorComposite;

    /** The context */
    private OpenLdapAclValueWithContext context;

    /** The what clause */
    private AclWhatClause clause;

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

    private WhatClauseAttributesComposite attributesClauseComposite;

    private WhatClauseFilterComposite filterClauseComposite;

    private WhatClauseDnComposite dnClauseComposite;


    /**
     * Creates a new instance of OpenLdapAclWhatClauseWidget.
     * 
     * @param visualEditorComposite the visual editor composite
     */
    public OpenLdapAclWhatClauseWidget( OpenLdapAclVisualEditorComposite visualEditorComposite )
    {
        this.visualEditorComposite = visualEditorComposite;
    }


    public void create( Composite parent )
    {
        // Creating the widget base composite
        composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Creating the what group
        Group whatGroup = BaseWidgetUtils.createGroup( parent, "Acces to \"What\"", 1 );
        whatGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DN
        dnCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "DN", 1 );
        dnComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );

        // Filter
        filterCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "Filter", 1 );
        filterComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );

        // Attributes
        attributesCheckbox = BaseWidgetUtils.createCheckbox( whatGroup, "Attributes", 1 );
        attributesComposite = BaseWidgetUtils.createColumnContainer( whatGroup, 1, 1 );

        // Adding the listeners to the UI widgets
        addListeners();
    }


    /**
     * Creates the DN composite.
     */
    private void createDnComposite()
    {
        Group dnGroup = BaseWidgetUtils.createGroup( dnComposite, "", 1 );
        dnGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        dnSubComposite = dnGroup;

        dnClauseComposite = new WhatClauseDnComposite( visualEditorComposite );
        dnClauseComposite.createComposite( dnGroup );

        if ( clause.getDnClause() != null )
        {
            dnClauseComposite.setClause( clause.getDnClause() );
        }

        if ( context != null )
        {
            dnClauseComposite.setConnection( context.getConnection() );
        }
    }


    /**
     * Creates the filter composite.
     */
    private void createFilterComposite()
    {
        Group filterGroup = BaseWidgetUtils.createGroup( filterComposite, "", 1 );
        filterGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        filterSubComposite = filterGroup;

        filterClauseComposite = new WhatClauseFilterComposite( visualEditorComposite );
        filterClauseComposite.createComposite( filterGroup );

        if ( clause.getFilterClause() != null )
        {
            filterClauseComposite.setClause( clause.getFilterClause() );
        }

        if ( context != null )
        {
            filterClauseComposite.setConnection( context.getConnection() );
        }
    }


    /**
     * Creates the attributes composite.
     */
    private void createAttributesComposite()
    {
        Group attributesGroup = BaseWidgetUtils.createGroup( attributesComposite, "", 1 );
        attributesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        attributesSubComposite = attributesGroup;

        attributesClauseComposite = new WhatClauseAttributesComposite( visualEditorComposite );
        attributesClauseComposite.createComposite( attributesGroup );

        if ( clause.getAttributesClause() != null )
        {
            attributesClauseComposite.setClause( clause.getAttributesClause() );
        }

        if ( context != null )
        {
            attributesClauseComposite.setConnection( context.getConnection() );
        }
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
     * Adds the listeners to the UI widgets.
     */
    private void addListeners()
    {
        dnCheckbox.addSelectionListener( dnCheckboxListener );
        filterCheckbox.addSelectionListener( filterCheckboxListener );
        attributesCheckbox.addSelectionListener( attributesCheckboxListener );
    }


    /**
     * Sets the input.
     *
     * @param clause the what clause
     */
    public void setInput( AclWhatClause clause )
    {
        this.clause = clause;

        if ( clause != null )
        {
            // DN clause
            AclWhatClauseDn dnClause = clause.getDnClause();
            if ( dnClause != null )
            {
                dnCheckbox.setSelection( true );
                createDnComposite();
            }

            // Filter clause
            AclWhatClauseFilter filterClause = clause.getFilterClause();
            if ( filterClause != null )
            {
                filterCheckbox.setSelection( true );
                createFilterComposite();
            }

            // Attributes clause
            AclWhatClauseAttributes attributesClause = clause.getAttributesClause();
            if ( attributesClause != null )
            {
                attributesCheckbox.setSelection( true );
                createAttributesComposite();
            }
        }
    }


    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( OpenLdapAclValueWithContext context )
    {
        this.context = context;

        if ( attributesClauseComposite != null )
        {
            attributesClauseComposite.setConnection( context.getConnection() );
        }
    }


    /**
     * Gets the what clause.
     *
     * @return the what clause
     */
    public AclWhatClause getClause()
    {
        return clause;
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
