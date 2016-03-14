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
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseDn;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseDnTypeEnum;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhatClauseDnComposite extends AbstractClauseComposite
{
    /** The entry widget */
    private EntryWidget entryWidget;

    /** The modify listener */
    private WidgetModifyListener modifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            context.getAclItem().getWhatClause().getDnClause().setPattern( entryWidget.getDn().toString() );
        }
    };


    public WhatClauseDnComposite( OpenLdapAclValueWithContext context, Composite visualEditorComposite )
    {
        super( context, visualEditorComposite );
        AclWhatClause aclWhatClause = context.getAclItem().getWhatClause();
        
        if ( aclWhatClause.getDnClause() == null )
        {
            aclWhatClause.setDnClause( new AclWhatClauseDn() );
        }
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
                    return (( AclWhatClauseDnTypeEnum ) element).getName();
                }

                return super.getText( element );
            }
        } );
        
        whatClauseDnTypeComboViewer.setInput( AclWhatClauseDnTypeEnum.values() );

        return composite;
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
            if ( context.getAclItem().getWhatClause().getDnClause() != null )
            {
                try
                {
                    entryWidget.setInput( connection, new Dn( context.getAclItem().getWhatClause().getDnClause().getPattern() ) );
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
