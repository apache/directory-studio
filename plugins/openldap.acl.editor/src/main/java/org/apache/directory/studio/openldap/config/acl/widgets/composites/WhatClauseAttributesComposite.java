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


import java.util.Arrays;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseAttributes;
import org.apache.directory.studio.openldap.config.acl.widgets.AttributesWidget;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhatClauseAttributesComposite extends AbstractClauseComposite<AclWhatClauseAttributes> implements
    WhatClauseComposite<AclWhatClauseAttributes>
{
    /** The attributes widget */
    private AttributesWidget attributesWidget;

    /** The modify listener */
    private WidgetModifyListener modifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            getClause().clearAttributes();
            getClause().addAllAttributes( Arrays.asList( attributesWidget.getAttributes() ) );
        }
    };


    public WhatClauseAttributesComposite( AclWhatClauseAttributes clause, Composite visualEditorComposite )
    {
        super( clause, visualEditorComposite );
    }


    public WhatClauseAttributesComposite( Composite visualEditorComposite )
    {
        super( new AclWhatClauseAttributes(), visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        BaseWidgetUtils.createLabel( composite, "Attributes:", 1 );
        attributesWidget = new AttributesWidget();
        attributesWidget.createWidget( composite );
        attributesWidget.addWidgetModifyListener( modifyListener );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void setClause( AclWhatClauseAttributes clause )
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
        if ( attributesWidget != null )
        {
            attributesWidget.setBrowserConnection( connection );
            if ( clause != null )
            {
                attributesWidget.setInitialAttributes( clause.getAttributes().toArray( new String[0] ) );
            }
            else
            {
                attributesWidget.setInitialAttributes( new String[0] );
            }
        }
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        if ( attributesWidget != null )
        {
            attributesWidget.saveWidgetSettings();
        }
    }
}
