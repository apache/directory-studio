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
package org.apache.directory.studio.proxy.view;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;


/**
 * This class is a helper class that is used to create widgets.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BaseWidgetUtils
{
    public static Group createGroup( Composite parent, String label, int span )
    {
        Group group = new Group( parent, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.horizontalSpan = span;
        group.setLayoutData( gd );
        group.setText( label );
        group.setLayout( new GridLayout() );
        return group;
    }


    public static Composite createColumnContainer( Composite parent, int columnCount, int span )
    {
        Composite container = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( columnCount, false );
        gl.marginHeight = gl.marginWidth = 0;
        container.setLayout( gl );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        container.setLayoutData( gd );
        return container;
    }


    public static Label createLabel( Composite parent, String text, int span )
    {
        Label l = new Label( parent, SWT.NONE );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        // gd.verticalAlignment = SWT.BEGINNING;
        l.setLayoutData( gd );
        l.setText( text );
        return l;
    }


    public static Label createWrappedLabel( Composite parent, String text, int span )
    {
        Label l = new Label( parent, SWT.WRAP );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        // gd.verticalAlignment = SWT.BEGINNING;
        l.setLayoutData( gd );
        l.setText( text );
        return l;
    }


    public static Text createText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setText( text );
        return t;
    }


    public static Text createText( Composite parent, String text, int textWidth, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        gd.widthHint = 9 * textWidth;
        t.setLayoutData( gd );
        t.setText( text );
        t.setTextLimit( textWidth );
        return t;
    }


    public static Text createPasswordText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER | SWT.PASSWORD );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setText( text );
        return t;
    }


    public static Text createReadonlyPasswordText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER | SWT.PASSWORD | SWT.READ_ONLY );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setEditable( false );
        t.setBackground( parent.getBackground() );
        t.setText( text );
        return t;
    }


    public static Text createLabeledText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setEditable( false );
        t.setBackground( parent.getBackground() );
        t.setText( text );
        return t;
    }


    public static Text createWrappedLabeledText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.WRAP );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        gd.widthHint = 10;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        t.setLayoutData( gd );
        t.setEditable( false );
        t.setBackground( parent.getBackground() );
        t.setText( text );
        return t;
    }


    public static Text createReadonlyText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER | SWT.READ_ONLY );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setEditable( false );
        t.setBackground( parent.getBackground() );
        t.setText( text );
        return t;
    }


    public static String getNonNullString( String s )
    {
        return s == null ? "-" : s;
    }


    public static Combo createCombo( Composite parent, String[] items, int selectedIndex, int span )
    {
        Combo c = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        c.setLayoutData( gd );
        c.setItems( items );
        c.select( selectedIndex );
        c.setVisibleItemCount( 20 );
        return c;
    }


    public static Combo createReadonlyCombo( Composite parent, String[] items, int selectedIndex, int span )
    {
        Combo c = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        c.setLayoutData( gd );
        // c.setBackground(parent.getBackground());
        c.setItems( items );
        c.select( selectedIndex );
        c.setVisibleItemCount( 20 );
        return c;
    }


    public static Combo createReadonlyReadonlyCombo( Composite parent, String[] items, int selectedIndex, int span )
    {
        Combo c = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        c.setLayoutData( gd );
        c.setBackground( parent.getBackground() );
        c.setItems( items );
        c.select( selectedIndex );
        c.setVisibleItemCount( 20 );
        return c;
    }


    public static Button createCheckbox( Composite composite, String text, int span )
    {
        Button checkbox = new Button( composite, SWT.CHECK );
        checkbox.setText( text );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        checkbox.setLayoutData( gd );
        return checkbox;
    }


    public static Button createRadiobutton( Composite composite, String text, int span )
    {
        Button radio = new Button( composite, SWT.RADIO );
        radio.setText( text );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        radio.setLayoutData( gd );
        return radio;
    }


    public static Button createButton( Composite composite, String text, int span )
    {
        GC gc = new GC( composite );
        gc.setFont( JFaceResources.getDialogFont() );
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        Button button = new Button( composite, SWT.PUSH );
        GridData gd = new GridData();
        gd.widthHint = Dialog.convertHorizontalDLUsToPixels( fontMetrics, IDialogConstants.BUTTON_WIDTH );
        button.setLayoutData( gd );
        button.setText( text );
        return button;
    }


    public static void createRadioIndent( Composite composite, int span )
    {
        Label l = new Label( composite, SWT.NONE );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        gd.horizontalIndent = 22;
        l.setLayoutData( gd );
    }


    public static void createSpacer( Composite composite, int span )
    {
        Label l = new Label( composite, SWT.NONE );
        // GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        gd.heightHint = 1;
        l.setLayoutData( gd );
    }


    public static void createSeparator( Composite composite, int span )
    {
        Label l = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        // gd.heightHint = 1;
        l.setLayoutData( gd );
    }


    public static Link createLink( Composite parent, String text, int span )
    {
        Link link = new Link( parent, SWT.NONE );
        link.setText( text );
        GridData gd = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        gd.horizontalSpan = span;
        gd.widthHint = 150;
        link.setLayoutData( gd );
        return link;
    }
}
