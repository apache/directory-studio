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

package org.apache.directory.studio.ldapbrowser.common.widgets;


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
 * This class provides utility methods to create SWT widgets.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BaseWidgetUtils
{

    /**
     * Creates a SWT {@link Group} under the given parent.
     *
     * @param parent the parent
     * @param label the label of the group
     * @param span the horizontal span
     * @return the created group
     */
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


    /**
     * Creates a SWT {@link Composite} under the given parent. 
     * A GridLayout with the given number of columns is used.
     *
     * @param parent the parent
     * @param columnCount the number of columns
     * @param span the horizontal span
     * @return the created composite
     */
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


    /**
     * Creates a SWT {@link Label} under the given parent. 
     *
     * @param parent the parent
     * @param text the label's text
     * @param span the horizontal span
     * @return the created label
     */
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


    /**
     * Creates a SWT {@link Label} under the given parent. 
     * The label is created with the SWT.WRAP style to enable line wrapping.
     *
     * @param parent the parent
     * @param text the label's text
     * @param span the horizontal span
     * @return the created label
     */
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


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is modifyable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setText( text );
        return t;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is modifyable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param textWidth the width of the text control
     * @param span the horizontal span
     * @return the created text
     */
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


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is created with the SWT.PASSWORD style.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createPasswordText( Composite parent, String text, int span )
    {
        Text t = new Text( parent, SWT.NONE | SWT.BORDER | SWT.PASSWORD );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        t.setLayoutData( gd );
        t.setText( text );
        return t;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is created with the SWT.PASSWORD and 
     * SWT.READ_ONLY style. So the created controls is not modifyable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
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


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifyable. 
     * But the text is selectable and could be copied.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
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


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifyable. 
     * But the text is selectable and could be copied.
     * The label is created with the SWT.WRAP style to enable line wrapping.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
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


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The text is not modifyable, but the text is selectable 
     * and could be copied.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
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


    /**
     * Creates a SWT {@link Combo} under the given parent.
     * Beside the selection of an item it is also possible to type
     * free text into the combo.
     *
     * @param parent the parent
     * @param items the initial visible items
     * @param selectedIndex the initial selected item, zero-based
     * @param span the horizontal span
     * @return the created combo
     */
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


    /**
     * Creates a SWT {@link Combo} under the given parent.
     * It is not possible to type free text into the combo, only 
     * selection of predefined items is possible.
     *
     * @param parent the parent
     * @param items the initial visible items
     * @param selectedIndex the initial selected item, zero-based
     * @param span the horizontal span
     * @return the created combo
     */
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


    /**
     * Creates a checkbox under the given parent.
     *
     * @param parent the parent
     * @param text the label of the checkbox 
     * @param span the horizontal span
     * @return the created checkbox
     */
    public static Button createCheckbox( Composite parent, String text, int span )
    {
        Button checkbox = new Button( parent, SWT.CHECK );
        checkbox.setText( text );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        checkbox.setLayoutData( gd );
        return checkbox;
    }


    /**
     * Creates a radio button under the given parent.
     *
     * @param parent the parent
     * @param text the label of the radio button 
     * @param span the horizontal span
     * @return the created radio button
     */
    public static Button createRadiobutton( Composite parent, String text, int span )
    {
        Button radio = new Button( parent, SWT.RADIO );
        radio.setText( text );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        radio.setLayoutData( gd );
        return radio;
    }


    /**
     * Creates a button under the given parent. 
     * The button width is set to the default width.
     *
     * @param parent the parent
     * @param text the label of the button 
     * @param span the horizontal span
     * @return the created button
     */
    public static Button createButton( Composite parent, String text, int span )
    {
        GC gc = new GC( parent );
        gc.setFont( JFaceResources.getDialogFont() );
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        Button button = new Button( parent, SWT.PUSH );
        GridData gd = new GridData();
        gd.widthHint = Dialog.convertHorizontalDLUsToPixels( fontMetrics, IDialogConstants.BUTTON_WIDTH );
        button.setLayoutData( gd );
        button.setText( text );
        return button;
    }


    /**
     * Adds some space to indent radio buttons.
     *
     * @param parent the parent
     * @param span the horizontal span
     */
    public static void createRadioIndent( Composite parent, int span )
    {
        Label l = new Label( parent, SWT.NONE );
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        gd.horizontalIndent = 22;
        l.setLayoutData( gd );
    }


    /**
     * Creates a spacer.
     *
     * @param parent the parent
     * @param span the horizontal span
     */
    public static void createSpacer( Composite parent, int span )
    {
        Label l = new Label( parent, SWT.NONE );
        // GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        gd.heightHint = 1;
        l.setLayoutData( gd );
    }


    /**
     * Creates a separator line.
     *
     * @param parent the parent
     * @param span the horizontal span
     */
    public static void createSeparator( Composite parent, int span )
    {
        Label l = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = span;
        // gd.heightHint = 1;
        l.setLayoutData( gd );
    }


    /**
     * Creates a SWT {@link Link} under the given parent.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
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
