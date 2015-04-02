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


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseDn;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseDnTypeEnum;


public class WhatClauseDnComposite extends AbstractClauseComposite<AclWhatClauseDn> implements
    WhatClauseComposite<AclWhatClauseDn>
{
    /** The array of DN what clause types */
    private static final AclWhatClauseDnTypeEnum[] aclWhatClauseDnTypes = new AclWhatClauseDnTypeEnum[]
        {
            AclWhatClauseDnTypeEnum.BASE,
            AclWhatClauseDnTypeEnum.EXACT,
            AclWhatClauseDnTypeEnum.ONE,
            AclWhatClauseDnTypeEnum.SUBTREE,
            AclWhatClauseDnTypeEnum.CHILDREN,
            AclWhatClauseDnTypeEnum.REGEX
    };

    /** The entry widget */
    private EntryWidget entryWidget;

    /** The modify listener */
    private WidgetModifyListener modifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            getClause().setPattern( entryWidget.getDn().toString() );
        }
    };


    public WhatClauseDnComposite( AclWhatClauseDn clause, Composite visualEditorComposite )
    {
        super( clause, visualEditorComposite );
    }


    public WhatClauseDnComposite( Composite visualEditorComposite )
    {
        super( new AclWhatClauseDn(), visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // DN
        BaseWidgetUtils.createLabel( composite, "DN:", 1 );
        entryWidget = new EntryWidget();
        entryWidget.createWidget( composite );
        entryWidget.addWidgetModifyListener( modifyListener );

        // Type
        BaseWidgetUtils.createLabel( composite, "Type:", 1 );
        ComboViewer whatClauseDnTypeComboViewer = new ComboViewer( BaseWidgetUtils.createReadonlyCombo( composite,
            new String[0], -1, 1 ) );
        whatClauseDnTypeComboViewer.getCombo().setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );
        whatClauseDnTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        whatClauseDnTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof AclWhatClauseDnTypeEnum )
                {
                    AclWhatClauseDnTypeEnum value = ( AclWhatClauseDnTypeEnum ) element;
                    switch ( value )
                    {
                        case BASE:
                            return "Base";
                        case EXACT:
                            return "Exact";
                        case ONE:
                            return "One";
                        case SUBTREE:
                            return "Subtree";
                        case CHILDREN:
                            return "Children";
                        case REGEX:
                            return "Regex";
                    }
                }

                return super.getText( element );
            }
        } );
        whatClauseDnTypeComboViewer.setInput( aclWhatClauseDnTypes );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void setClause( AclWhatClauseDn clause )
    {
        super.setClause( clause );
        setInput();
    }


    /**
     * {@inheritDoc}
     */
    public void setConnection( IBrowserConnection connection )
    {
        super.setConnection( connection );
        setInput();
    }


    private void setInput()
    {
        if ( entryWidget != null )
        {
            if ( clause != null )
            {
                try
                {
                    entryWidget.setInput( connection, new Dn( clause.getPattern() ) );
                }
                catch ( LdapInvalidDnException e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                entryWidget.setInput( connection, null );
            }
        }
    }
}
