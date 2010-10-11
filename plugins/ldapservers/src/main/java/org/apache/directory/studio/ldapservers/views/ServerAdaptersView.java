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
package org.apache.directory.studio.ldapservers.views;


import org.apache.directory.studio.ldapservers.LdapServerAdapterExtensionsManager;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Servers view.
 * <p>
 * It displays the list of Apache Directory Servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ServerAdaptersView extends ViewPart
{
    /** The table viewer */
    private TableViewer tableViewer;


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        tableViewer = new TableViewer( parent );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                if ( element instanceof LdapServerAdapterExtension )
                {
                    LdapServerAdapterExtension extension = ( LdapServerAdapterExtension ) element;

                    return extension.getName() + " " + extension.getVersion(); //$NON-NLS-1$
                }

                // TODO Auto-generated method stub
                return super.getText( element );
            }
        } );
        tableViewer.setInput( LdapServerAdapterExtensionsManager.getDefault().getLdapServerAdapterExtensions()
            .toArray() );
        tableViewer.getTable().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        tableViewer.getTable().setFocus();
    }
}
