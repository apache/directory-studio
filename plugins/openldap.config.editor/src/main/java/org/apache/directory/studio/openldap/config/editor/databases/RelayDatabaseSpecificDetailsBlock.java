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
package org.apache.directory.studio.openldap.config.editor.databases;


import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.model.database.OlcRelayConfig;


/**
 * This interface represents a block for Relay Specific Details.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RelayDatabaseSpecificDetailsBlock extends AbstractDatabaseSpecificDetailsBlock<OlcRelayConfig>
{
    // UI Widgets
    private EntryWidget relayEntryWidget;


    /**
     * Creates a new instance of NullDatabaseSpecificDetailsBlock.
     * 
     * @param detailsPage the details page
     * @param database the database
     * @param browserConnection the connection
     */
    public RelayDatabaseSpecificDetailsBlock( DatabasesDetailsPage detailsPage, OlcRelayConfig database,
        IBrowserConnection browserConnection )
    {
        super( detailsPage, database, browserConnection );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createBlockContent( Composite parent, FormToolkit toolkit )
    {
        // Composite
        Composite composite = toolkit.createComposite( parent );
        composite.setLayout( new GridLayout( 3, false ) );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Relay
        toolkit.createLabel( composite, "Relay:" );
        relayEntryWidget = new EntryWidget( browserConnection );
        relayEntryWidget.createWidget( composite, toolkit );
        relayEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        removeListeners();

        if ( database != null )
        {
            // Relay
            relayEntryWidget.setInput( database.getOlcRelay() );
        }

        addListeners();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        relayEntryWidget.addWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        relayEntryWidget.removeWidgetModifyListener( dirtyWidgetModifyListener );
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
        if ( database != null )
        {
            // Relay
            Dn relay = relayEntryWidget.getDn();

            if ( ( relay != null ) & ( !Dn.EMPTY_DN.equals( relay ) ) )
            {
                database.setOlcRelay( relay );
            }
            else
            {
                database.setOlcRelay( null );
            }
        }
    }
}
