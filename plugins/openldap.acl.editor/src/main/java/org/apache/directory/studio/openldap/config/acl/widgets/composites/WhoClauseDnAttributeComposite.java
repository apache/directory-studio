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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseDnAttr;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhoClauseDnAttributeComposite extends AbstractWhoClauseComposite<AclWhoClauseDnAttr>
{
    private Combo dnAttributeCombo;


    public WhoClauseDnAttributeComposite( OpenLdapAclValueWithContext context, AclWhoClauseDnAttr clause, Composite visualEditorComposite )
    {
        super( context, clause, visualEditorComposite );
    }


    public WhoClauseDnAttributeComposite( OpenLdapAclValueWithContext context, Composite visualEditorComposite )
    {
        super( context, new AclWhoClauseDnAttr(), visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // DN
        BaseWidgetUtils.createLabel( composite, "DN Attribute:", 1 );
        dnAttributeCombo = BaseWidgetUtils.createCombo( composite, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        dnAttributeCombo.setLayoutData( gd );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void setClause( AclWhoClauseDnAttr clause )
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
        if ( dnAttributeCombo != null )
        {
            if ( whoClause != null )
            {
                dnAttributeCombo.setText( whoClause.getAttribute() );
            }
            else
            {
                dnAttributeCombo.setText( "" );
            }
        }
    }
}
