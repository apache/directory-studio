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


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.SearchPageWrapper;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * This class is a base implementation of the page to select the data to export.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ExportBaseFromWizardPage extends WizardPage implements WidgetModifyListener
{

    /** The wizard. */
    protected ExportBaseWizard wizard;

    /** The search page wrapper. */
    protected SearchPageWrapper spw;


    /**
     * Creates a new instance of ExportBaseFromWizardPage.
     * 
     * @param spw the search page wrapper
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportBaseFromWizardPage( String pageName, ExportBaseWizard wizard, SearchPageWrapper spw )
    {
        super( pageName );
        setTitle( Messages.getString( "ExportBaseFromWizardPage.DataToExport" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "ExportBaseFromWizardPage.PleaseDefineSearchParameters" ) ); //$NON-NLS-1$
        setPageComplete( true );

        this.wizard = wizard;
        this.spw = spw;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        spw.createContents( composite );
        spw.loadFromSearch( wizard.getSearch() );
        spw.addWidgetModifyListener( this );

        setControl( composite );
    }


    /**
     * Validates this page and sets the error message
     * if this page is not valid.
     */
    protected void validate()
    {
        setPageComplete( spw.isValid() );
        setErrorMessage( spw.getErrorMessage() );
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        spw.saveToSearch( wizard.getSearch() );
    }

}
