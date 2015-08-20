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
package org.apache.directory.studio.openldap.config.acl.widgets.composites;


import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseGroup;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhoClauseGroupComposite extends AbstractWhoClauseComposite<AclWhoClauseGroup>
{
    /** The expansion listener used on expandable composites */
    private ExpansionAdapter expansionListener = new ExpansionAdapter()
    {
        public void expansionStateChanged( ExpansionEvent e )
        {
            // Refreshing the layout of the whole composite
            visualEditorComposite.layout( true, true );
        }
    };


    public WhoClauseGroupComposite( OpenLdapAclValueWithContext context, AclWhoClauseGroup clause, Composite visualEditorComposite )
    {
        super( context, clause, visualEditorComposite );
    }


    public WhoClauseGroupComposite( OpenLdapAclValueWithContext context, Composite visualEditorComposite )
    {
        super( context, new AclWhoClauseGroup(), visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // Group DN
        BaseWidgetUtils.createLabel( composite, "Group DN:", 1 );
        EntryWidget entryWidget = new EntryWidget();
        entryWidget.createWidget( composite );

        ExpandableComposite optionsExpandableComposite = new ExpandableComposite( composite, SWT.NONE,
            ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT );
        optionsExpandableComposite.setLayout( new GridLayout() );
        optionsExpandableComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );
        optionsExpandableComposite.setText( "Options" );
        Composite clientComposite = BaseWidgetUtils.createColumnContainer( optionsExpandableComposite, 1, 1 );
        ( ( GridLayout ) clientComposite.getLayout() ).marginRight = 15;
        optionsExpandableComposite.setClient( clientComposite );
        optionsExpandableComposite.addExpansionListener( expansionListener );

        Group optionsGroup = BaseWidgetUtils.createGroup( clientComposite, "", 1 );
        optionsGroup.setLayout( new GridLayout( 2, false ) );
        optionsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Group Object Class
        BaseWidgetUtils.createLabel( optionsGroup, "Group Object Class", 1 );
        ComboViewer groupObjectClassComboViewer = new ComboViewer( BaseWidgetUtils.createCombo( optionsGroup,
            new String[0], -1, 1 ) );
        groupObjectClassComboViewer.setContentProvider( new ArrayContentProvider() );
        groupObjectClassComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof ObjectClass )
                {
                    // TODO
                }

                return super.getText( element );
            }
        } );
        //        groupObjectClassComboViewer.setInput( dqsdq ); // TODO
        groupObjectClassComboViewer.getCombo().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Group Attribute Class
        BaseWidgetUtils.createLabel( optionsGroup, "Group Attribute Type", 1 );
        ComboViewer groupAttributeTypeComboViewer = new ComboViewer( BaseWidgetUtils.createCombo( optionsGroup,
            new String[0], -1, 1 ) );
        groupAttributeTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        groupAttributeTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof ObjectClass )
                {
                    // TODO
                }

                return super.getText( element );
            }
        } );
        //        groupObjectClassComboViewer.setInput( dqsdq ); // TODO
        groupAttributeTypeComboViewer.getCombo().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        return composite;
    }
}
