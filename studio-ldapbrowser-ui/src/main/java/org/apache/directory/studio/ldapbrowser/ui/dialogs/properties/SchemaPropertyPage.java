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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.properties;


import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemasJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class SchemaPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private Text dnText;

    private Text ctText;

    private Text mtText;

    private Button reloadSchemaButton;

    private Text cachePathText;

    private Text cacheDateText;

    private Text cacheSizeText;


    public SchemaPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group infoGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Schema Information", 1 );
        Composite infoComposite = BaseWidgetUtils.createColumnContainer( infoGroup, 2, 1 );
        Composite infoGroupLeft = BaseWidgetUtils.createColumnContainer( infoComposite, 2, 1 );

        BaseWidgetUtils.createLabel( infoGroupLeft, "Schema DN:", 1 );
        dnText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "", 1 );

        BaseWidgetUtils.createLabel( infoGroupLeft, "Create Timestamp:", 1 );
        ctText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "", 1 );

        BaseWidgetUtils.createLabel( infoGroupLeft, "Modify Timestamp:", 1 );
        mtText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "", 1 );

        reloadSchemaButton = BaseWidgetUtils.createButton( infoComposite, "", 1 );
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.BOTTOM;
        reloadSchemaButton.setLayoutData( gd );
        reloadSchemaButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                reloadSchema();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group cacheGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Schema Cache", 1 );
        Composite cacheComposite = BaseWidgetUtils.createColumnContainer( cacheGroup, 2, 1 );

        BaseWidgetUtils.createLabel( cacheComposite, "Cache Location:", 1 );
        cachePathText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "", 1 );

        BaseWidgetUtils.createLabel( cacheComposite, "Cache Date:", 1 );
        cacheDateText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "", 1 );

        BaseWidgetUtils.createLabel( cacheComposite, "Cache Size:", 1 );
        cacheSizeText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "", 1 );

        IBrowserConnection connection = RootDSEPropertyPage.getConnection( getElement() );
        this.connectionUpdated( connection );

        return composite;
    }


    private void reloadSchema()
    {
        final IBrowserConnection browserConnection = RootDSEPropertyPage.getConnection( getElement() );
        ReloadSchemasJob job = new ReloadSchemasJob( browserConnection );
        RunnableContextJobAdapter.execute( job );
        this.connectionUpdated( browserConnection );
    }


    private void connectionUpdated( IBrowserConnection connection )
    {

        if ( !this.dnText.isDisposed() )
        {
            Schema schema = null;
            if ( connection != null )
            {
                schema = connection.getSchema();
            }

            if ( schema != null && schema.getDn() != null )
            {
                dnText.setText( schema.getDn().toString() );
            }
            else
            {
                dnText.setText( "-" );
            }

            if ( schema != null && schema.getCreateTimestamp() != null )
            {
                ctText.setText( schema.getCreateTimestamp() );
            }
            else
            {
                ctText.setText( "-" );
            }

            if ( schema != null && schema.getModifyTimestamp() != null )
            {
                mtText.setText( schema.getModifyTimestamp() );
            }
            else
            {
                mtText.setText( "-" );
            }

            if ( schema != null )
            {
                reloadSchemaButton.setText( "Reload Schema" );
            }
            else
            {
                reloadSchemaButton.setText( "Load Schema" );
            }

            if ( connection != null )
            {
                String cacheFileName = BrowserConnectionManager.getSchemaCacheFileName( connection );
                File cacheFile = new File( cacheFileName );
                if ( cacheFile.exists() )
                {
                    cachePathText.setText( cacheFile.getPath() );
                    DateFormat format = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM );
                    cacheDateText.setText( format.format( new Date( cacheFile.lastModified() ) ) );
                    cacheSizeText.setText( Utils.formatBytes( cacheFile.length() ) );
                }
                else
                {
                    cachePathText.setText( "-" );
                    cacheDateText.setText( "-" );
                    cacheSizeText.setText( "-" );
                }
            }

            reloadSchemaButton.setEnabled( true );
        }
    }


    public boolean isDisposed()
    {
        return this.dnText.isDisposed();
    }

}
