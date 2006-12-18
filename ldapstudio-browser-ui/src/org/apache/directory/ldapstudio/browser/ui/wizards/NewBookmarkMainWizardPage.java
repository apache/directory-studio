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


import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class NewBookmarkMainWizardPage extends WizardPage implements WidgetModifyListener
{

    private IEntry entry;

    private Text bookmarkNameText;

    private EntryWidget bookmarkEntryWidget;


    public NewBookmarkMainWizardPage( String pageName, IEntry entry, NewBookmarkWizard wizard )
    {
        super( pageName );
        super.setTitle( "New Bookmark" );
        super.setDescription( "Please enter the bookmark parameters." );
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        super.setPageComplete( false );

        this.entry = entry;
    }


    public void dispose()
    {
        super.dispose();
        this.bookmarkEntryWidget.removeWidgetModifyListener( this );
    }


    private void validate()
    {
        if ( this.bookmarkNameText != null && !this.bookmarkNameText.isDisposed() )
        {
            this.setPageComplete( this.bookmarkEntryWidget.getDn() != null
                && !"".equals( this.bookmarkNameText.getText() ) );
        }
    }


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        if ( visible )
        {
            this.validate();
        }
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );

        BaseWidgetUtils.createLabel( innerComposite, "Bookmark Name:", 1 );
        this.bookmarkNameText = BaseWidgetUtils.createText( innerComposite, this.entry.getDn().toString(), 2 );
        this.bookmarkNameText.setFocus();
        this.bookmarkNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( innerComposite, "Bookmark DN:", 1 );
        this.bookmarkEntryWidget = new EntryWidget();
        this.bookmarkEntryWidget.addWidgetModifyListener( this );
        this.bookmarkEntryWidget.createWidget( innerComposite );
        this.bookmarkEntryWidget.setInput( this.entry.getConnection(), this.entry.getDn() );

        setControl( composite );
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    public DN getBookmarkDn()
    {
        return this.bookmarkEntryWidget.getDn();
    }


    public String getBookmarkName()
    {
        return this.bookmarkNameText.getText();
    }


    public void saveDialogSettings()
    {
        this.bookmarkEntryWidget.saveDialogSettings();
    }

}
