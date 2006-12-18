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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.TextFormatsPreferencePage;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;


public class ExportExcelToWizardPage extends ExportBaseToPage
{

    private static final String[] EXTENSIONS = new String[]
        { "*.xls", "*.*" };


    public ExportExcelToWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard );
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_EXPORT_XLS_WIZARD ) );
    }


    public void createControl( Composite parent )
    {

        final Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        super.createControl( composite );

        BaseWidgetUtils.createSpacer( composite, 3 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        String text = "See <a>Text Formats</a> for Excel file format preferences.";
        Link link = BaseWidgetUtils.createLink( composite, text, 2 );
        link.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(), TextFormatsPreferencePage.class.getName(), null,
                    TextFormatsPreferencePage.XLS_TAB ).open();
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 3 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createWrappedLabel( composite,
            "Warning: Excel export is memory intensive! Maximum number of exportable entries is limited to 65000!", 2 );

    }


    protected static char getChar( String s )
    {
        return s != null && s.length() > 0 ? s.charAt( 0 ) : '\u0000';
    }


    protected String[] getExtensions()
    {
        return EXTENSIONS;
    }


    protected String getFileType()
    {
        return "Excel";
    }

}
