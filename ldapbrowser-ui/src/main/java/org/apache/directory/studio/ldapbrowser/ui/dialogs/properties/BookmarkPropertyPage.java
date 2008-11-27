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


import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This page shows some info about the selected Bookmark.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BookmarkPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    /** The bookmark. */
    private IBookmark bookmark;

    /** The bookmark name text. */
    private Text bookmarkNameText;

    /** The bookmark entry widget. */
    private EntryWidget bookmarkEntryWidget;


    /**
     * Creates a new instance of BookmarkPropertyPage.
     */
    public BookmarkPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {

        if ( getElement() instanceof IAdaptable )
        {
            bookmark = ( IBookmark ) ( ( IAdaptable ) getElement() ).getAdapter( IBookmark.class );
            super
                .setMessage( Messages.getString( "BookmarkPropertyPage.Bookmark" ) + Utils.shorten( bookmark.getName(), 30 ) ); //$NON-NLS-1$
        }
        else
        {
            bookmark = null;
        }

        Composite innerComposite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        BaseWidgetUtils.createLabel( innerComposite, Messages.getString( "BookmarkPropertyPage.BookmarkName" ), 1 ); //$NON-NLS-1$
        bookmarkNameText = BaseWidgetUtils.createText( innerComposite, bookmark != null ? bookmark.getName() : "", 2 ); //$NON-NLS-1$
        bookmarkNameText.setFocus();
        bookmarkNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( innerComposite, Messages.getString( "BookmarkPropertyPage.BookmarkDN" ), 1 ); //$NON-NLS-1$
        bookmarkEntryWidget = new EntryWidget();
        bookmarkEntryWidget.createWidget( innerComposite );
        if ( bookmark != null )
        {
            bookmarkEntryWidget.setInput( bookmark.getBrowserConnection(), bookmark.getDn() );
        }
        bookmarkEntryWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        return innerComposite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        if ( bookmark != null )
        {
            bookmark.setName( bookmarkNameText.getText() );
            bookmark.setDn( bookmarkEntryWidget.getDn() );
            bookmarkEntryWidget.saveDialogSettings();
        }

        return true;
    }


    /**
     * Validates the input fields.
     */
    private void validate()
    {
        setValid( bookmarkEntryWidget.getDn() != null && !"".equals( bookmarkNameText.getText() ) ); //$NON-NLS-1$

        if ( bookmark != null )
        {
            if ( bookmarkEntryWidget.getDn() == null )
            {
                setValid( false );
                setErrorMessage( Messages.getString( "BookmarkPropertyPage.EnterDN" ) ); //$NON-NLS-1$
            }
            else if ( "".equals( bookmarkNameText.getText() ) ) //$NON-NLS-1$
            {
                setValid( false );
                setErrorMessage( Messages.getString( "BookmarkPropertyPage.EnterName" ) ); //$NON-NLS-1$
            }
            else if ( !bookmark.getName().equals( bookmarkNameText.getText() )
                && bookmark.getBrowserConnection().getBookmarkManager().getBookmark( bookmarkNameText.getText() ) != null )
            {
                setValid( false );
                setErrorMessage( Messages.getString( "BookmarkPropertyPage.ErrorBookmarkExists" ) ); //$NON-NLS-1$
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
