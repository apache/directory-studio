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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.common.dialogs.preferences.TextFormatsPreferencePage;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the page to select the target CSV file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportCsvToWizardPage extends ExportBaseToPage
{

    /** The extensions used by CSV files */
    private static final String[] EXTENSIONS = new String[]
        {
            Messages.getString( "ExportCsvToWizardPage.0" ), Messages.getString( "ExportCsvToWizardPage.1" ), Messages.getString( "ExportCsvToWizardPage.2" ) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$


    /**
     * Creates a new instance of ExportCsvToWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportCsvToWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_CSV_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        final Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        super.createControl( composite );

        BaseWidgetUtils.createSpacer( composite, 3 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        String text = Messages.getString( "ExportCsvToWizardPage.SeeTextFormats" ); //$NON-NLS-1$
        Link link = BaseWidgetUtils.createLink( composite, text, 2 );
        link.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                PreferencesUtil.createPreferenceDialogOn( getShell(), BrowserUIConstants.PREFERENCEPAGEID_TEXTFORMATS,
                    null, TextFormatsPreferencePage.CSV_TAB ).open();
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    protected String[] getExtensions()
    {
        return EXTENSIONS;
    }


    /**
     * {@inheritDoc}
     */
    protected String getFileType()
    {
        return Messages.getString( "ExportCsvToWizardPage.CVS" ); //$NON-NLS-1$
    }

}
