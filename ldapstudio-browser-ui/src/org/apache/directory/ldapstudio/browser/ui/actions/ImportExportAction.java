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

package org.apache.directory.ldapstudio.browser.ui.actions;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.wizards.ExportCsvWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.ExportExcelWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.ExportLdifWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.ImportLdifWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;


public class ImportExportAction extends BrowserAction
{

    public static final int TYPE_IMPORT_LDIF = 0;

    public static final int TYPE_EXPORT_LDIF = 1;

    public static final int TYPE_EXPORT_CSV = 2;

    public static final int TYPE_EXPORT_EXCEL = 3;

    private int type;


    public ImportExportAction( int type )
    {
        super();
        this.type = type;
    }


    public String getText()
    {
        if ( this.type == TYPE_IMPORT_LDIF )
        {
            return "LDIF Import...";
        }
        else if ( this.type == TYPE_EXPORT_LDIF )
        {
            return "LDIF Export...";
        }
        else if ( this.type == TYPE_EXPORT_CSV )
        {
            return "CSV Export...";
        }
        else if ( this.type == TYPE_EXPORT_EXCEL )
        {
            return "Excel Export...";
        }
        else
        {
            return "Export...";
        }
    }


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
        else
        {
            return null;
        }
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getEntry() != null || getConnection() != null || getSearch() != null || getConnectionInput() != null;

    }


    public void run()
    {
        IWizard wizard = null;

        if ( this.type == TYPE_IMPORT_LDIF )
        {
            if ( getEntry() != null )
            {
                wizard = new ImportLdifWizard( getEntry().getConnection() );
            }
            else if ( getSearch() != null )
            {
                wizard = new ImportLdifWizard( getSearch().getConnection() );
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

        if ( wizard != null )
        {
            WizardDialog dialog = new WizardDialog( getShell(), wizard );
            dialog.setBlockOnOpen( true );
            dialog.create();
            dialog.open();
        }

    }


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

        return entry != null && entry.getConnection().isOpened() ? entry : null;
    }


    protected IConnection getConnection()
    {
        return getSelectedConnections().length > 0 && getSelectedConnections()[0].isOpened() ? getSelectedConnections()[0]
            : null;
    }


    protected ISearch getSearch()
    {
        return getSelectedSearches().length > 0 && getSelectedSearches()[0].getConnection().isOpened() ? getSelectedSearches()[0]
            : null;
    }


    protected IConnection getConnectionInput()
    {

        if ( getInput() != null && ( getInput() instanceof IConnection ) && ( ( IConnection ) getInput() ).isOpened() )
        {
            return ( IConnection ) getInput();
        }
        else
        {
            return null;
        }
    }

}
