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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public class EntryEditorWidgetQuickFilterWidget
{

    private EntryEditorWidgetFilter filter;

    private EntryEditorWidget entryEditorWidget;

    private Composite parent;

    private Composite composite;

    private Composite innerComposite;

    private Text quickFilterAttributeText;

    private Text quickFilterValueText;

    private Button clearQuickFilterButton;


    public EntryEditorWidgetQuickFilterWidget( EntryEditorWidgetFilter filter, EntryEditorWidget entryEditorWidget )
    {
        this.filter = filter;
        this.entryEditorWidget = entryEditorWidget;
    }


    public void createComposite( Composite parent )
    {
        this.parent = parent;

        this.composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        GridLayout gl = new GridLayout();
        gl.marginHeight = 2;
        gl.marginWidth = 2;
        composite.setLayout( gl );

        this.innerComposite = null;
    }


    private void create()
    {
        innerComposite = BaseWidgetUtils.createColumnContainer( this.composite, 3, 1 );

        this.quickFilterAttributeText = new Text( innerComposite, SWT.BORDER );
        this.quickFilterAttributeText.setLayoutData( new GridData( 200 - 14, SWT.DEFAULT ) );
        this.quickFilterAttributeText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                filter.setQuickFilterAttribute( quickFilterAttributeText.getText() );
                clearQuickFilterButton.setEnabled( !"".equals( quickFilterAttributeText.getText() )
                    || !"".equals( quickFilterValueText.getText() ) );
                if ( !"".equals( quickFilterAttributeText.getText() ) )
                {
                    RGB fgRgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                        BrowserUIConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR );
                    RGB bgRgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                        BrowserUIConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR );
                    Color fgColor = BrowserUIPlugin.getDefault().getColor( fgRgb );
                    Color bgColor = BrowserUIPlugin.getDefault().getColor( bgRgb );
                    quickFilterAttributeText.setForeground( fgColor );
                    quickFilterAttributeText.setBackground( bgColor );
                    FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                        .getPreferenceStore(), BrowserUIConstants.PREFERENCE_QUICKFILTER_FONT );
                    Font font = BrowserUIPlugin.getDefault().getFont( fontData );
                    quickFilterAttributeText.setFont( font );
                }
                else
                {
                    quickFilterAttributeText.setBackground( null );
                }
            }
        } );

        this.quickFilterValueText = new Text( innerComposite, SWT.BORDER );
        this.quickFilterValueText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        this.quickFilterValueText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                filter.setQuickFilterValue( quickFilterValueText.getText() );
                clearQuickFilterButton.setEnabled( !"".equals( quickFilterAttributeText.getText() )
                    || !"".equals( quickFilterValueText.getText() ) );
                if ( !"".equals( quickFilterValueText.getText() ) )
                {
                    RGB fgRgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                        BrowserUIConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR );
                    RGB bgRgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                        BrowserUIConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR );
                    Color fgColor = BrowserUIPlugin.getDefault().getColor( fgRgb );
                    Color bgColor = BrowserUIPlugin.getDefault().getColor( bgRgb );
                    quickFilterValueText.setForeground( fgColor );
                    quickFilterValueText.setBackground( bgColor );
                    FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                        .getPreferenceStore(), BrowserUIConstants.PREFERENCE_QUICKFILTER_FONT );
                    Font font = BrowserUIPlugin.getDefault().getFont( fontData );
                    quickFilterValueText.setFont( font );
                }
                else
                {
                    quickFilterValueText.setBackground( null );
                }
            }
        } );

        this.clearQuickFilterButton = new Button( innerComposite, SWT.PUSH );
        this.clearQuickFilterButton.setToolTipText( "Clear Quick Filter" );
        this.clearQuickFilterButton.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_CLEAR ) );
        this.clearQuickFilterButton.setEnabled( false );
        this.clearQuickFilterButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( !"".equals( quickFilterAttributeText.getText() ) )
                    quickFilterAttributeText.setText( "" );
                if ( !"".equals( quickFilterValueText.getText() ) )
                    quickFilterValueText.setText( "" );
            }
        } );

        setEnabled( composite.isEnabled() );

        composite.layout( true, true );
        parent.layout( true, true );
    }


    private void destroy()
    {
        if ( !"".equals( quickFilterAttributeText.getText() ) )
            quickFilterAttributeText.setText( "" );
        if ( !"".equals( quickFilterValueText.getText() ) )
            quickFilterValueText.setText( "" );
        innerComposite.dispose();
        innerComposite = null;

        composite.layout( true, true );
        parent.layout( true, true );
    }


    public void dispose()
    {
        if ( this.filter != null )
        {
            this.quickFilterAttributeText = null;
            this.quickFilterValueText = null;
            this.clearQuickFilterButton = null;
            this.innerComposite = null;
            this.composite.dispose();
            this.composite = null;
            this.parent = null;
            this.filter = null;
        }
    }


    public void setEnabled( boolean enabled )
    {
        if ( this.composite != null && !this.composite.isDisposed() )
        {
            this.composite.setEnabled( enabled );
        }
        if ( this.innerComposite != null && !this.innerComposite.isDisposed() )
        {
            this.innerComposite.setEnabled( enabled );
            this.quickFilterAttributeText.setEnabled( enabled );
            this.quickFilterValueText.setEnabled( enabled );
            this.clearQuickFilterButton.setEnabled( enabled );
        }
    }


    public void setActive( boolean visible )
    {
        if ( visible && this.innerComposite == null && composite != null )
        {
            create();
            this.quickFilterAttributeText.setFocus();
        }
        else if ( !visible && this.innerComposite != null && composite != null )
        {
            destroy();
            this.entryEditorWidget.getViewer().getTree().setFocus();
        }
    }

}
