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
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseFilter;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhatClauseFilterComposite extends AbstractClauseComposite<AclWhatClauseFilter> implements
    WhatClauseComposite<AclWhatClauseFilter>
{
    /** The filter widget */
    private FilterWidget filterWidget;

    private WidgetModifyListener modifyListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent event )
        {
            getClause().setFilter( filterWidget.getFilter() );
        }
    };


    public WhatClauseFilterComposite( AclWhatClauseFilter clause, Composite visualEditorComposite )
    {
        super( clause, visualEditorComposite );
    }


    public WhatClauseFilterComposite( Composite visualEditorComposite )
    {
        super( new AclWhatClauseFilter(), visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        BaseWidgetUtils.createLabel( composite, "Filter:", 1 );
        filterWidget = new FilterWidget();
        filterWidget.createWidget( composite );
        filterWidget.addWidgetModifyListener( modifyListener );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void setClause( AclWhatClauseFilter clause )
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
        if ( filterWidget != null )
        {
            filterWidget.setBrowserConnection( connection );
            if ( clause != null )
            {
                String filter = clause.getFilter();
                filterWidget.setFilter( ( filter != null ) ? filter : "" );
            }
            else
            {
                filterWidget.setFilter( "" );
            }
        }
    }
}
