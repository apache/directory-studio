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

package org.apache.directory.studio.ldifeditor.dialogs.preferences;


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifFile;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;
import org.apache.directory.studio.ldifeditor.widgets.LdifEditorWidget;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class LdifEditorSyntaxColoringPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
    ILdifEditor
{

    private static final String LDIF_INITIAL = "" + "# Content record" + BrowserCoreConstants.LINE_SEPARATOR
        + "dn: cn=content record" + BrowserCoreConstants.LINE_SEPARATOR + "objectClass: person"
        + BrowserCoreConstants.LINE_SEPARATOR + "cn: content record" + BrowserCoreConstants.LINE_SEPARATOR
        + "cn;lang-ja:: 5Za25qWt6YOo" + BrowserCoreConstants.LINE_SEPARATOR + "" + BrowserCoreConstants.LINE_SEPARATOR

        + "# Add record with control" + BrowserCoreConstants.LINE_SEPARATOR + "dn: cn=add record"
        + BrowserCoreConstants.LINE_SEPARATOR + "control: 1.2.3.4 true: controlValue"
        + BrowserCoreConstants.LINE_SEPARATOR + "changetype: add" + BrowserCoreConstants.LINE_SEPARATOR
        + "objectClass: person" + BrowserCoreConstants.LINE_SEPARATOR + "cn: add record"
        + BrowserCoreConstants.LINE_SEPARATOR + "" + BrowserCoreConstants.LINE_SEPARATOR

        + "# Modify record" + BrowserCoreConstants.LINE_SEPARATOR + "dn: cn=modify record"
        + BrowserCoreConstants.LINE_SEPARATOR + "changetype: modify" + BrowserCoreConstants.LINE_SEPARATOR + "add: cn"
        + BrowserCoreConstants.LINE_SEPARATOR + "cn: modify record" + BrowserCoreConstants.LINE_SEPARATOR + "-"
        + BrowserCoreConstants.LINE_SEPARATOR + "delete: cn" + BrowserCoreConstants.LINE_SEPARATOR + "-"
        + BrowserCoreConstants.LINE_SEPARATOR + "replace: cn" + BrowserCoreConstants.LINE_SEPARATOR
        + "cn: modify record" + BrowserCoreConstants.LINE_SEPARATOR + "-" + BrowserCoreConstants.LINE_SEPARATOR + ""
        + BrowserCoreConstants.LINE_SEPARATOR

        + "# Delete record" + BrowserCoreConstants.LINE_SEPARATOR + "dn: cn=delete record"
        + BrowserCoreConstants.LINE_SEPARATOR + "changetype: delete" + BrowserCoreConstants.LINE_SEPARATOR + ""
        + BrowserCoreConstants.LINE_SEPARATOR

        + "# Modify DN record" + BrowserCoreConstants.LINE_SEPARATOR + "dn: cn=moddn record"
        + BrowserCoreConstants.LINE_SEPARATOR + "changetype: moddn" + BrowserCoreConstants.LINE_SEPARATOR
        + "newrdn: cn=new rdn" + BrowserCoreConstants.LINE_SEPARATOR + "deleteoldrdn: 1"
        + BrowserCoreConstants.LINE_SEPARATOR + "newsuperior: cn=new superior" + BrowserCoreConstants.LINE_SEPARATOR
        + "" + BrowserCoreConstants.LINE_SEPARATOR;

    private LdifEditorWidget ldifEditorWidget;

    private SyntaxItem[] syntaxItems;

    private ColorSelector colorSelector;

    private Button boldCheckBox;

    private Button italicCheckBox;

    private Button underlineCheckBox;

    private Button strikethroughCheckBox;

    private TableViewer syntaxItemViewer;

    private class SyntaxItem
    {
        String displayName;

        String key;

        RGB rgb;

        boolean bold;

        boolean italic;

        boolean strikethrough;

        boolean underline;


        SyntaxItem( String displayName, String key )
        {
            this.displayName = displayName;
            this.key = key;
            loadPreferences();
        }


        int getStyle()
        {
            int style = SWT.NORMAL;
            if ( bold )
                style |= SWT.BOLD;
            if ( italic )
                style |= SWT.ITALIC;
            if ( strikethrough )
                style |= TextAttribute.STRIKETHROUGH;
            if ( underline )
                style |= TextAttribute.UNDERLINE;
            return style;
        }


        void setStyle( int style )
        {
            this.bold = ( style & SWT.BOLD ) != SWT.NORMAL;
            this.italic = ( style & SWT.ITALIC ) != SWT.NORMAL;
            this.strikethrough = ( style & TextAttribute.STRIKETHROUGH ) != SWT.NORMAL;
            this.underline = ( style & TextAttribute.UNDERLINE ) != SWT.NORMAL;
        }


        void loadPreferences()
        {
            IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();
            this.rgb = PreferenceConverter.getColor( store, key
                + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX );
            int style = store.getInt( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX );
            setStyle( style );
        }


        void savePreferences()
        {
            IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();
            PreferenceConverter.setValue( store, key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, rgb );
            store.setValue( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, getStyle() );
        }


        void loadDefaultPreferences()
        {
            IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();
            this.rgb = PreferenceConverter.getDefaultColor( store, key
                + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX );
            int style = store.getDefaultInt( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX );
            setStyle( style );
        }


        public String toString()
        {
            return displayName;
        }
    }


    public LdifEditorSyntaxColoringPreferencePage()
    {
        super( "Syntax Coloring" );
        super.setPreferenceStore( LdifEditorActivator.getDefault().getPreferenceStore() );
        // super.setDescription("");
    }


    public void init( IWorkbench workbench )
    {
    }


    public void dispose()
    {
        ldifEditorWidget.dispose();
        super.dispose();
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        composite.setLayout( layout );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        composite.setLayoutData( gd );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        createSyntaxPage( composite );
        createPreviewer( composite );

        syntaxItems = new SyntaxItem[10];
        syntaxItems[0] = new SyntaxItem( "Comments", LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT );
        syntaxItems[1] = new SyntaxItem( "DN", LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN );
        syntaxItems[2] = new SyntaxItem( "Attribute Descriptions",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE );
        syntaxItems[3] = new SyntaxItem( "Value Types", LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE );
        syntaxItems[4] = new SyntaxItem( "Values", LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE );
        syntaxItems[5] = new SyntaxItem( "Keywords (w/o changetypes)",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD );
        syntaxItems[6] = new SyntaxItem( "Changetype 'add'",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD );
        syntaxItems[7] = new SyntaxItem( "Changetype 'modify'",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY );
        syntaxItems[8] = new SyntaxItem( "Changetype 'delete'",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE );
        syntaxItems[9] = new SyntaxItem( "Changetype 'moddn'",
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN );
        syntaxItemViewer.setInput( syntaxItems );
        syntaxItemViewer.setSelection( new StructuredSelection( syntaxItems[0] ) );

        return composite;
    }


    private void createSyntaxPage( Composite parent )
    {

        BaseWidgetUtils.createLabel( parent, "Element:", 1 );

        Composite editorComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        syntaxItemViewer = new TableViewer( editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER
            | SWT.FULL_SELECTION );
        syntaxItemViewer.setLabelProvider( new LabelProvider() );
        syntaxItemViewer.setContentProvider( new ArrayContentProvider() );
        // colorListViewer.setSorter(new WorkbenchViewerSorter());
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.heightHint = convertHeightInCharsToPixels( 5 );
        syntaxItemViewer.getControl().setLayoutData( gd );

        Composite stylesComposite = BaseWidgetUtils.createColumnContainer( editorComposite, 1, 1 );
        stylesComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        Composite colorComposite = BaseWidgetUtils.createColumnContainer( stylesComposite, 2, 1 );
        BaseWidgetUtils.createLabel( colorComposite, "Color:", 1 );
        colorSelector = new ColorSelector( colorComposite );
        boldCheckBox = BaseWidgetUtils.createCheckbox( stylesComposite, "Bold", 1 );
        italicCheckBox = BaseWidgetUtils.createCheckbox( stylesComposite, "Italic", 1 );
        strikethroughCheckBox = BaseWidgetUtils.createCheckbox( stylesComposite, "Strikethrough", 1 );
        underlineCheckBox = BaseWidgetUtils.createCheckbox( stylesComposite, "Underline", 1 );

        syntaxItemViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                handleSyntaxItemViewerSelectionEvent();
            }
        } );
        colorSelector.addListener( new IPropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent event )
            {
                handleColorSelectorEvent();
            }
        } );
        boldCheckBox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                handleBoldSelectionEvent();
            }
        } );
        italicCheckBox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                handleItalicSelectionEvent();
            }
        } );
        strikethroughCheckBox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                handleStrikethroughSelectionEvent();
            }
        } );
        underlineCheckBox.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                handleUnderlineSelectionEvent();
            }
        } );

    }


    private void handleUnderlineSelectionEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            item.underline = underlineCheckBox.getSelection();
            setTextAttribute( item );
        }
    }


    private void handleStrikethroughSelectionEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            item.strikethrough = strikethroughCheckBox.getSelection();
            setTextAttribute( item );
        }
    }


    private void handleItalicSelectionEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            item.italic = italicCheckBox.getSelection();
            setTextAttribute( item );
        }
    }


    private void handleBoldSelectionEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            item.bold = boldCheckBox.getSelection();
            setTextAttribute( item );
        }
    }


    private void handleColorSelectorEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            item.rgb = colorSelector.getColorValue();
            setTextAttribute( item );
        }
    }


    private void handleSyntaxItemViewerSelectionEvent()
    {
        SyntaxItem item = getSyntaxItem();
        if ( item != null )
        {
            colorSelector.setColorValue( item.rgb );
            boldCheckBox.setSelection( item.bold );
            italicCheckBox.setSelection( item.italic );
            strikethroughCheckBox.setSelection( item.strikethrough );
            underlineCheckBox.setSelection( item.underline );
        }
    }


    private SyntaxItem getSyntaxItem()
    {
        SyntaxItem item = ( SyntaxItem ) ( ( IStructuredSelection ) syntaxItemViewer.getSelection() ).getFirstElement();
        return item;
    }


    private void setTextAttribute( SyntaxItem item )
    {
        ldifEditorWidget.getSourceViewerConfiguration().setTextAttribute( item.key, item.rgb, item.getStyle() );

        int topIndex = ldifEditorWidget.getSourceViewer().getTopIndex();
        // ldifEditorWidget.getSourceViewer().getDocument().set("");
        ldifEditorWidget.getSourceViewer().getDocument().set( LDIF_INITIAL );
        ldifEditorWidget.getSourceViewer().setTopIndex( topIndex );
    }


    private void createPreviewer( Composite parent )
    {

        BaseWidgetUtils.createLabel( parent, "Preview:", 1 );

        ldifEditorWidget = new LdifEditorWidget( null, LDIF_INITIAL, false );
        ldifEditorWidget.createWidget( parent );
        ldifEditorWidget.getSourceViewer().setEditable( false );

        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertWidthInCharsToPixels( 20 );
        gd.heightHint = convertHeightInCharsToPixels( 5 );
        ldifEditorWidget.getSourceViewer().getControl().setLayoutData( gd );

    }


    public IConnection getConnection()
    {
        return ldifEditorWidget.getConnection();
    }


    public LdifFile getLdifModel()
    {
        return ldifEditorWidget.getLdifModel();
    }


    public boolean performOk()
    {
        for ( int i = 0; i < syntaxItems.length; i++ )
        {
            syntaxItems[i].savePreferences();
        }
        return true;
    }


    protected void performDefaults()
    {
        for ( int i = 0; i < syntaxItems.length; i++ )
        {
            syntaxItems[i].loadDefaultPreferences();
            setTextAttribute( syntaxItems[i] );
        }
        handleSyntaxItemViewerSelectionEvent();
        super.performDefaults();
    }


    public Object getAdapter( Class adapter )
    {
        return null;
    }

}
