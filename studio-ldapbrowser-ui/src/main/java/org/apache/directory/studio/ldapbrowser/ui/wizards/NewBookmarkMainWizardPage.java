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


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * The NewBookmarkMainWizardPage is used to specify the bookmark name
 * and the DN of the target entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewBookmarkMainWizardPage extends WizardPage implements WidgetModifyListener
{

    /** The entry. */
    private IEntry entry;

    /** The bookmark name text. */
    private Text bookmarkNameText;

    /** The bookmark entry widget. */
    private EntryWidget bookmarkEntryWidget;


    /**
     * Creates a new instance of NewBookmarkMainWizardPage.
     * 
     * @param pageName the page name
     * @param entry the entry
     * @param wizard the wizard
     */
    public NewBookmarkMainWizardPage( String pageName, IEntry entry, NewBookmarkWizard wizard )
    {
        super( pageName );
        setTitle( "New Bookmark" );
        setDescription( "Please enter the bookmark parameters." );
        // setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        setPageComplete( false );

        this.entry = entry;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();
        bookmarkEntryWidget.removeWidgetModifyListener( this );
    }


    /**
     * Validates this page.
     */
    private void validate()
    {
        if ( bookmarkNameText != null && !bookmarkNameText.isDisposed() )
        {
            setPageComplete( bookmarkEntryWidget.getDn() != null && !"".equals( bookmarkNameText.getText() ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        if ( visible )
        {
            validate();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );

        BaseWidgetUtils.createLabel( innerComposite, "Bookmark Name:", 1 );
        bookmarkNameText = BaseWidgetUtils.createText( innerComposite, entry.getDn().toString(), 2 );
        bookmarkNameText.setFocus();
        bookmarkNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( innerComposite, "Bookmark DN:", 1 );
        bookmarkEntryWidget = new EntryWidget();
        bookmarkEntryWidget.addWidgetModifyListener( this );
        bookmarkEntryWidget.createWidget( innerComposite );
        bookmarkEntryWidget.setInput( entry.getConnection(), entry.getDn() );

        setControl( composite );
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    /**
     * Gets the bookmark dn.
     * 
     * @return the bookmark dn
     */
    public DN getBookmarkDn()
    {
        return bookmarkEntryWidget.getDn();
    }


    /**
     * Gets the bookmark name.
     * 
     * @return the bookmark name
     */
    public String getBookmarkName()
    {
        return bookmarkNameText.getText();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        bookmarkEntryWidget.saveDialogSettings();
    }

}
