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


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class BookmarkPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private IBookmark bookmark;

    private Text bookmarkNameText;

    private EntryWidget bookmarkEntryWidget;


    public BookmarkPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    public void dispose()
    {
        super.dispose();
    }


    protected Control createContents( Composite parent )
    {

        if ( getElement() instanceof IAdaptable )
        {
            this.bookmark = ( IBookmark ) ( ( IAdaptable ) getElement() ).getAdapter( IBookmark.class );
            super.setMessage( "Bookmark " + Utils.shorten( bookmark.getName(), 30 ) );
        }
        else
        {
            this.bookmark = null;
        }

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        BaseWidgetUtils.createLabel( innerComposite, "Bookmark Name:", 1 );
        this.bookmarkNameText = BaseWidgetUtils.createText( innerComposite, this.bookmark != null ? this.bookmark
            .getName() : "", 2 );
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
        this.bookmarkEntryWidget.createWidget( innerComposite );
        if ( this.bookmark != null )
        {
            this.bookmarkEntryWidget.setInput( this.bookmark.getBrowserConnection(), this.bookmark.getDn() );
        }
        this.bookmarkEntryWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        return innerComposite;
    }


    public boolean performOk()
    {
        if ( this.bookmark != null )
        {
            this.bookmark.setName( this.bookmarkNameText.getText() );
            this.bookmark.setDn( this.bookmarkEntryWidget.getDn() );
            this.bookmarkEntryWidget.saveDialogSettings();
        }

        return true;
    }


    private void validate()
    {

        setValid( this.bookmarkEntryWidget.getDn() != null && !"".equals( this.bookmarkNameText.getText() ) );

        if ( this.bookmark != null )
        {
            if ( this.bookmarkEntryWidget.getDn() == null )
            {
                setValid( false );
                setErrorMessage( "Please enter a DN." );
            }
            else if ( "".equals( this.bookmarkNameText.getText() ) )
            {
                setValid( false );
                setErrorMessage( "Please enter a name." );
            }
            else if ( !bookmark.getName().equals( this.bookmarkNameText.getText() )
                && bookmark.getBrowserConnection().getBookmarkManager().getBookmark( this.bookmarkNameText.getText() ) != null )
            {
                setValid( false );
                setErrorMessage( "A bookmark with this name already exists." );
            }
            else
            {
                setValid( true );
                setErrorMessage( null );
            }
        }
        else
        {
            setValid( false );
        }
    }

}
