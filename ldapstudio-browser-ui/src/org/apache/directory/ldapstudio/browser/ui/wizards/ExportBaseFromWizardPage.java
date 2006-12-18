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


import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.SearchPageWrapper;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


public abstract class ExportBaseFromWizardPage extends WizardPage implements WidgetModifyListener
{

    protected ExportBaseWizard wizard;

    protected SearchPageWrapper spw;


    public ExportBaseFromWizardPage( String pageName, ExportBaseWizard wizard, SearchPageWrapper spw )
    {
        super( pageName );
        super.setTitle( "Data to Export" );
        super.setDescription( "Please define search parameters for the export." );
        super.setPageComplete( true );

        this.wizard = wizard;
        this.spw = spw;
    }


    public void createControl( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        this.spw.createContents( composite );
        this.spw.loadFromSearch( wizard.getSearch() );
        this.spw.addWidgetModifyListener( this );

        setControl( composite );
        // this.spw.setFocus();
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
    }


    protected void validate()
    {
        setPageComplete( spw.isValid() );
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    public void saveDialogSettings()
    {
        this.spw.saveToSearch( wizard.getSearch() );
    }

}
