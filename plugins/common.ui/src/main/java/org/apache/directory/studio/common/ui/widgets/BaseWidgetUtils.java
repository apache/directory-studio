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

package org.apache.directory.studio.common.ui.widgets;


import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.CommonUIPlugin;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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
        GridData gridData = new GridData( GridData.FILL_BOTH );
        gridData.horizontalSpan = span;
        group.setLayoutData( gridData );
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
        return createColumnContainer( parent, columnCount, false, span );
    }


    /**
     * Creates a SWT {@link Composite} under the given parent. 
     * A GridLayout with the given number of columns is used.
     *
     * @param parent the parent
     * @param columnCount the number of columns
     * @param makeColumnsEqualWidth if the columns width should be equal
     * @param span the horizontal span
     * @return the created composite
     */
    public static Composite createColumnContainer( Composite parent, int columnCount, boolean makeColumnsEqualWidth,
        int span )
    {
        Composite container = new Composite( parent, SWT.NONE );
        GridLayout gridLayout = new GridLayout( columnCount, makeColumnsEqualWidth );
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        container.setLayout( gridLayout );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        container.setLayoutData( gridData );
        
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
        Label label = new Label( parent, SWT.NONE );
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        label.setLayoutData( gridData );
        label.setText( text );
        
        return label;
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
        Label label = new Label( parent, SWT.WRAP );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        gridData.widthHint = 100;
        label.setLayoutData( gridData );
        label.setText( text );
        
        return label;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is modifiable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createText( Composite parent, String text, int span )
    {
        Text textWidget = new Text( parent, SWT.NONE | SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        textWidget.setLayoutData( gridData );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is modifiable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param textWidth the width of the text control
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createText( Composite parent, String text, int textWidth, int span )
    {
        Text textWidget = new Text( parent, SWT.NONE | SWT.BORDER );
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.widthHint = 9 * textWidth;
        textWidget.setLayoutData( gridData );
        textWidget.setText( text );
        textWidget.setTextLimit( textWidth );
        
        return textWidget;
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
        Text textWidget = new Text( parent, SWT.NONE | SWT.BORDER | SWT.PASSWORD );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        textWidget.setLayoutData( gridData );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control is created with the SWT.PASSWORD and 
     * SWT.READ_ONLY style. So the created controls is not modifiable.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createReadonlyPasswordText( Composite parent, String text, int span )
    {
        Text textWidget = new Text( parent, SWT.NONE | SWT.BORDER | SWT.PASSWORD | SWT.READ_ONLY );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifiable. 
     * But the text is selectable and could be copied.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createLabeledText( Composite parent, String text, int span )
    {
        Text textWidget = new Text( parent, SWT.NONE );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifiable. 
     * But the text is selectable and could be copied.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @param widthHint the width hint
     * @return the created text
     */
    public static Text createLabeledText( Composite parent, String text, int span, int widthHint )
    {
        Text textWidget = new Text( parent, SWT.NONE );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.horizontalSpan = span;
        gridData.widthHint = widthHint;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifiable. 
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
        Text textWidget = new Text( parent, SWT.WRAP );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        gridData.widthHint = 10;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The created text control behaves like a label: it has no border, 
     * a grayed background and is not modifiable. 
     * But the text is selectable and could be copied.
     * The label is created with the SWT.WRAP style to enable line wrapping.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @param widthHint the width hint
     * @return the created text
     */
    public static Text createWrappedLabeledText( Composite parent, String text, int span, int widthHint )
    {
        Text textWidget = new Text( parent, SWT.WRAP );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        gridData.widthHint = widthHint;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
    }


    /**
     * Creates a SWT {@link Text} under the given parent.
     * The text is not modifiable, but the text is selectable 
     * and could be copied.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return the created text
     */
    public static Text createReadonlyText( Composite parent, String text, int span )
    {
        Text textWidget = new Text( parent, SWT.NONE | SWT.BORDER | SWT.READ_ONLY );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        textWidget.setLayoutData( gridData );
        textWidget.setEditable( false );
        textWidget.setBackground( parent.getBackground() );
        textWidget.setText( text );
        
        return textWidget;
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
        Combo combo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        combo.setLayoutData( gridData );
        combo.setItems( items );
        combo.select( selectedIndex );
        combo.setVisibleItemCount( 20 );
        
        return combo;
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
        Combo combo = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        combo.setLayoutData( gridData );
        combo.setItems( items );
        combo.select( selectedIndex );
        combo.setVisibleItemCount( 20 );
        
        return combo;
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
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        checkbox.setLayoutData( gridData );
        
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
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        radio.setLayoutData( gridData );
        
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

        try
        {
            gc.setFont( JFaceResources.getDialogFont() );
            FontMetrics fontMetrics = gc.getFontMetrics();
            Button button = new Button( parent, SWT.PUSH );
            GridData gridData = new GridData();
            gridData.widthHint = Dialog.convertHorizontalDLUsToPixels( fontMetrics, IDialogConstants.BUTTON_WIDTH );
            gridData.horizontalSpan = span;
            button.setLayoutData( gridData );
            button.setText( text );
            
            return button;
        }
        finally
        {
            gc.dispose();
        }

    }


    /**
     * Adds some space to .
     *
     * @param parent the parent
     * @param span the horizontal span
     * @return the create label representing the radio buttons indent
     */
    public static Label createRadioIndent( Composite parent, int span )
    {
        Label label = new Label( parent, SWT.NONE );
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.horizontalIndent = 22;
        label.setLayoutData( gridData );
        
        return label;
    }


    /**
     * Creates a spacer.
     *
     * @param parent the parent
     * @param span the horizontal span
     * @return the create label representing the spacer
     */
    public static Label createSpacer( Composite parent, int span )
    {
        Label label = new Label( parent, SWT.NONE );
        GridData gridData = new GridData();
        gridData.horizontalSpan = span;
        gridData.heightHint = 1;
        label.setLayoutData( gridData );
        
        return label;
    }


    /**
     * Creates a separator.
     *
     * @param parent the parent
     * @param span the horizontal span
     * @return the create label representing the separator
     */
    public static Label createSeparator( Composite parent, int span )
    {
        Label label = new Label( parent, SWT.SEPARATOR | SWT.HORIZONTAL );
        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.horizontalSpan = span;
        label.setLayoutData( gridData );
        
        return label;
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
        GridData gridData = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        gridData.horizontalSpan = span;
        gridData.widthHint = 150;
        link.setLayoutData( gridData );
        
        return link;
    }
    
    
    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @return a Text that is a valid integer
     */
    public static Text createIntegerText( FormToolkit toolkit, Composite parent )
    {
        return createIntegerText( toolkit, parent, null, -1 );
    }

    
    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @param width the size of the input text to use
     * @return a Text that is a valid integer
     */
    public static Text createIntegerText( FormToolkit toolkit, Composite parent, int width )
    {
        return createIntegerText( toolkit, parent, null, width );
    }

    
    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @param description the description that has to be added after the inmput text
     * @return a Text that is a valid integer
     */
    public static Text createIntegerText( FormToolkit toolkit, Composite parent, String description )
    {
        return createIntegerText( toolkit, parent, description, -1 );
    }

    
    /**
     * Creates a Text that can be used to enter an integer.
     *
     * @param toolkit the toolkit
     * @param parent the parent
     * @param description the description that has to be added after the inmput text
     * @param width the size of the input text to use
     * @return a Text that is a valid integer
     */
    public static Text createIntegerText( FormToolkit toolkit, Composite parent, String description, int width )
    {
        Text integerText = toolkit.createText( parent, "" ); //$NON-NLS-1$
        
        integerText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                for ( int i = 0; i < e.text.length(); i++ )
                {
                    if ( !Character.isDigit( e.text.charAt( i ) ) )
                    {
                        e.doit = false;
                        break;
                    }
                }
            }
        } );

        // Add the description, if needed
        if ( ( description != null ) && ( description.length() > 0 ) )
        {
            ControlDecoration monitoringCheckboxDecoration = new ControlDecoration(
                integerText, SWT.CENTER | SWT.RIGHT );
            monitoringCheckboxDecoration.setImage( CommonUIPlugin.getDefault().getImageDescriptor(
                CommonUIConstants.IMG_INFORMATION ).createImage() );
            monitoringCheckboxDecoration.setMarginWidth( 4 );
            monitoringCheckboxDecoration.setDescriptionText( description );
        }
        
        if ( width >= 0 )
        {
            GridData gridData = new GridData();
            gridData.widthHint = width;
            integerText.setLayoutData( gridData );
        }

        return integerText;
    }
}
