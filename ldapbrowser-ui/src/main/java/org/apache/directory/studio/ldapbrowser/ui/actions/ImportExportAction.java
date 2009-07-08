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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportCsvWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportDsmlWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportExcelWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportLdifWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportOdfWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ImportDsmlWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ImportLdifWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;


/**
 * This class implements Import/Export Actions for LDIF, CSV, EXCEL, ODF and DSML.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportExportAction extends BrowserAction
{
    /**
     * LDIF Import Type
     */
    public static final int TYPE_IMPORT_LDIF = 0;

    /**
     * LDIF Export Type
     */
    public static final int TYPE_EXPORT_LDIF = 1;

    /**
     * CSV Export Type
     */
    public static final int TYPE_EXPORT_CSV = 2;

    /**
     * EXCEL Export Type
     */
    public static final int TYPE_EXPORT_EXCEL = 3;

    /**
     * DSML Import Type
     */
    public static final int TYPE_IMPORT_DSML = 4;

    /**
     * DSML Export Type
     */
    public static final int TYPE_EXPORT_DSML = 5;

    /**
     * ODF Export Type
     */
    public static final int TYPE_EXPORT_ODF = 6;

    private int type;


    /**
     * Creates a new instance of ImportExportAction.
     *
     * @param type
     *      the type of Import/Export
     */
    public ImportExportAction( int type )
    {
        super();
        this.type = type;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( this.type == TYPE_IMPORT_LDIF )
        {
            return Messages.getString( "ImportExportAction.LDIFImport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_EXPORT_LDIF )
        {
            return Messages.getString( "ImportExportAction.LDIFExport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_EXPORT_CSV )
        {
            return Messages.getString( "ImportExportAction.CVSExport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_EXPORT_EXCEL )
        {
            return Messages.getString( "ImportExportAction.ExcelExport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_EXPORT_ODF )
        {
            return Messages.getString( "ImportExportAction.OdfExport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_IMPORT_DSML )
        {
            return Messages.getString( "ImportExportAction.DSMLImport" ); //$NON-NLS-1$
        }
        else if ( this.type == TYPE_EXPORT_DSML )
        {
            return Messages.getString( "ImportExportAction.DSMLExport" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "ImportExportAction.Export" ); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( this.type == TYPE_IMPORT_LDIF )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_IMPORT_LDIF );
        }
        else if ( this.type == TYPE_EXPORT_LDIF )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_LDIF );
        }
        else if ( this.type == TYPE_EXPORT_CSV )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_CSV );
        }
        else if ( this.type == TYPE_EXPORT_EXCEL )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_XLS );
        }
        else if ( this.type == TYPE_EXPORT_ODF )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_ODF );
        }
        else if ( this.type == TYPE_IMPORT_DSML )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_IMPORT_DSML );
        }
        else if ( this.type == TYPE_EXPORT_DSML )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_DSML );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return getEntry() != null || getConnection() != null || getSearch() != null || getConnectionInput() != null;

    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IWizard wizard = null;

        if ( this.type == TYPE_IMPORT_LDIF )
        {
            if ( getEntry() != null )
            {
                wizard = new ImportLdifWizard( getEntry().getBrowserConnection() );
            }
            else if ( getSearch() != null )
            {
                wizard = new ImportLdifWizard( getSearch().getBrowserConnection() );
            }
            else if ( getConnectionInput() != null )
            {
                wizard = new ImportLdifWizard( getConnectionInput() );
            }
            else if ( getConnection() != null )
            {
                wizard = new ImportLdifWizard( getConnection() );
            }
        }
        else if ( this.type == TYPE_IMPORT_DSML )
        {
            if ( getEntry() != null )
            {
                wizard = new ImportDsmlWizard( getEntry().getBrowserConnection() );
            }
            else if ( getSearch() != null )
            {
                wizard = new ImportDsmlWizard( getSearch().getBrowserConnection() );
            }
            else if ( getConnectionInput() != null )
            {
                wizard = new ImportDsmlWizard( getConnectionInput() );
            }
            else if ( getConnection() != null )
            {
                wizard = new ImportDsmlWizard( getConnection() );
            }
        }
        else if ( this.type == TYPE_EXPORT_LDIF )
        {
            wizard = new ExportLdifWizard();
        }
        else if ( this.type == TYPE_EXPORT_CSV )
        {
            wizard = new ExportCsvWizard();
        }
        else if ( this.type == TYPE_EXPORT_EXCEL )
        {
            wizard = new ExportExcelWizard();
        }
        else if ( this.type == TYPE_EXPORT_ODF )
        {
            wizard = new ExportOdfWizard();
        }
        else if ( this.type == TYPE_EXPORT_DSML )
        {
            wizard = new ExportDsmlWizard();
        }

        if ( wizard != null )
        {
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            dialog.open();
        }

    }


    /**
     * Gets the selected Entry.
     *
     * @return
     *      the selected Entry
     */
    protected IEntry getEntry()
    {
        IEntry entry = null;
        if ( getSelectedEntries().length > 0 )
        {
            entry = getSelectedEntries()[0];
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            entry = getSelectedSearchResults()[0].getEntry();
        }
        else if ( getSelectedBookmarks().length > 0 )
        {
            entry = getSelectedBookmarks()[0].getEntry();
        }

        return entry != null ? entry : null;
    }


    /**
     * Gets the Connection.
     *
     * @return
     *      the Connection
     */
    protected IBrowserConnection getConnection()
    {
        if ( getSelectedConnections().length > 0
            && getSelectedConnections()[0].getJNDIConnectionWrapper().isConnected() )
        {
            Connection connection = getSelectedConnections()[0];
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnection( connection );
            return browserConnection;
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the Search.
     *
     * @return
     *      the Search
     */
    protected ISearch getSearch()
    {
        return getSelectedSearches().length > 0 ? getSelectedSearches()[0] : null;
    }


    /**
     * Gets the Connection Input.
     *
     * @return
     *      the Connection Input
     */
    protected IBrowserConnection getConnectionInput()
    {

        if ( getInput() != null && ( getInput() instanceof IBrowserConnection ) )
        {
            return ( IBrowserConnection ) getInput();
        }
        else
        {
            return null;
        }
    }
}
