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

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReloadSchemaRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * Property page to shows some meta information of the schema and the 
 * schema cache. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    /** Text field containing the Dn of the schema entry. */
    private Text dnText;

    /** Text field containing the create timestamp of the schema entry. */
    private Text ctText;

    /** Text field containing the modify timestamp of the schema entry. */
    private Text mtText;

    /** Button to reload the scheam. */
    private Button reloadSchemaButton;

    /** Text field containing the path to the schema cache file. */
    private Text cachePathText;

    /** Text field containing last modify date of the schema cache file. */
    private Text cacheDateText;

    /** Text field containing the size of the schema cache file. */
    private Text cacheSizeText;


    /**
     * Instantiates a new schema property page.
     */
    public SchemaPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group infoGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "SchemaPropertyPage.SchemaInformation" ), 1 ); //$NON-NLS-1$
        Composite infoComposite = BaseWidgetUtils.createColumnContainer( infoGroup, 2, 1 );
        Composite infoGroupLeft = BaseWidgetUtils.createColumnContainer( infoComposite, 2, 1 );

        BaseWidgetUtils.createLabel( infoGroupLeft, Messages.getString( "SchemaPropertyPage.SchemaDN" ), 1 ); //$NON-NLS-1$
        dnText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "-", 1, 200 ); //$NON-NLS-1$

        BaseWidgetUtils.createLabel( infoGroupLeft, Messages.getString( "SchemaPropertyPage.CreateTimestamp" ), 1 ); //$NON-NLS-1$
        ctText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "-", 1, 200 ); //$NON-NLS-1$

        BaseWidgetUtils.createLabel( infoGroupLeft, Messages.getString( "SchemaPropertyPage.ModifyTimestamp" ), 1 ); //$NON-NLS-1$
        mtText = BaseWidgetUtils.createWrappedLabeledText( infoGroupLeft, "-", 1, 200 ); //$NON-NLS-1$

        reloadSchemaButton = BaseWidgetUtils.createButton( infoComposite, "-", 1 ); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.BOTTOM;
        reloadSchemaButton.setLayoutData( gd );
        reloadSchemaButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                reloadSchema();
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group cacheGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "SchemaPropertyPage.SchemaCache" ), 1 ); //$NON-NLS-1$
        Composite cacheComposite = BaseWidgetUtils.createColumnContainer( cacheGroup, 2, 1 );

        BaseWidgetUtils.createLabel( cacheComposite, Messages.getString( "SchemaPropertyPage.CacheLocation" ), 1 ); //$NON-NLS-1$
        cachePathText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "-", 1, 200 ); //$NON-NLS-1$

        BaseWidgetUtils.createLabel( cacheComposite, Messages.getString( "SchemaPropertyPage.CacheDate" ), 1 ); //$NON-NLS-1$
        cacheDateText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "-", 1, 200 ); //$NON-NLS-1$

        BaseWidgetUtils.createLabel( cacheComposite, Messages.getString( "SchemaPropertyPage.CacheSize" ), 1 ); //$NON-NLS-1$
        cacheSizeText = BaseWidgetUtils.createWrappedLabeledText( cacheComposite, "-", 1, 200 ); //$NON-NLS-1$

        IBrowserConnection connection = RootDSEPropertyPage.getConnection( getElement() );
        update( connection );

        return composite;
    }


    /**
     * Reloads schema.
     */
    private void reloadSchema()
    {
        final IBrowserConnection browserConnection = RootDSEPropertyPage.getConnection( getElement() );
        ReloadSchemaRunnable runnable = new ReloadSchemaRunnable( browserConnection );
        RunnableContextRunner.execute( runnable, null, true );
        update( browserConnection );
    }


    /**
     * Updates the text fields.
     * 
     * @param browserConnection the connection
     */
    private void update( IBrowserConnection browserConnection )
    {
        if ( !dnText.isDisposed() )
        {
            Schema schema = null;
            if ( browserConnection != null )
            {
                schema = browserConnection.getSchema();
            }

            if ( schema != null && schema.getDn() != null )
            {
                dnText.setText( schema.getDn().toString() );
            }
            else
            {
                dnText.setText( "-" ); //$NON-NLS-1$
            }

            if ( schema != null && schema.getCreateTimestamp() != null )
            {
                ctText.setText( schema.getCreateTimestamp() );
            }
            else
            {
                ctText.setText( "-" ); //$NON-NLS-1$
            }

            if ( schema != null && schema.getModifyTimestamp() != null )
            {
                mtText.setText( schema.getModifyTimestamp() );
            }
            else
            {
                mtText.setText( "-" ); //$NON-NLS-1$
            }

            if ( schema != null )
            {
                reloadSchemaButton.setText( Messages.getString( "SchemaPropertyPage.ReloadSchema" ) ); //$NON-NLS-1$
            }
            else
            {
                reloadSchemaButton.setText( Messages.getString( "SchemaPropertyPage.LoadSchema" ) ); //$NON-NLS-1$
            }

            if ( browserConnection != null )
            {
                String cacheFileName = BrowserConnectionManager.getSchemaCacheFileName( browserConnection
                    .getConnection().getId() );
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
                    cachePathText.setText( "-" ); //$NON-NLS-1$
                    cacheDateText.setText( "-" ); //$NON-NLS-1$
                    cacheSizeText.setText( "-" ); //$NON-NLS-1$
                }
            }

            reloadSchemaButton.setEnabled( true );
        }
    }


    /**
     * Checks if is disposed.
     * 
     * @return true, if is disposed
     */
    public boolean isDisposed()
    {
        return dnText.isDisposed();
    }

}
