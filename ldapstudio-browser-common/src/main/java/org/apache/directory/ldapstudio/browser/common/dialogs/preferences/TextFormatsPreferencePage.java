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

package org.apache.directory.ldapstudio.browser.common.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BinaryEncodingInput;
import org.apache.directory.ldapstudio.browser.common.widgets.FileEncodingInput;
import org.apache.directory.ldapstudio.browser.common.widgets.LineSeparatorInput;
import org.apache.directory.ldapstudio.browser.common.widgets.OptionsInput;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class TextFormatsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
    WidgetModifyListener, ModifyListener
{

    public static final String LDIF_TAB = "LDIF";

    public static final String CSV_TAB = "CSV";

    public static final String XLS_TAB = "XLS";

    public static final String TABLE_TAB = "TABLE";

    private Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();

    private TabFolder tabFolder;

    private TabItem ldifTab;

    private TabItem tableTab;

    private TabItem csvTab;

    private TabItem xlsTab;

    private Text ldifLineLengthText;

    private Button ldifSpaceAfterColonButton;

    private LineSeparatorInput ldifLineSeparator;

    // private Button ldifSpaceBetweenRDNsButton;

    private OptionsInput tableAttributeDelimiterWidget;

    private OptionsInput tableValueDelimiterWidget;

    private OptionsInput tableQuoteWidget;

    private LineSeparatorInput tableLineSeparator;

    private BinaryEncodingInput tableBinaryEncodingWidget;

    private OptionsInput csvAttributeDelimiterWidget;

    private OptionsInput csvValueDelimiterWidget;

    private OptionsInput csvQuoteWidget;

    private LineSeparatorInput csvLineSeparator;

    private BinaryEncodingInput csvBinaryEncodingWidget;

    private FileEncodingInput csvEncodingWidget;

    private OptionsInput xlsValueDelimiterWidget;

    private OptionsInput xlsBinaryEncodingWidget;


    public TextFormatsPreferencePage()
    {
        super();
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( "Settings for text formats" );
    }


    public void init( IWorkbench workbench )
    {
    }


    public void applyData( Object data )
    {
        if ( data != null && tabFolder != null )
        {
            if ( LDIF_TAB.equals( data ) )
            {
                tabFolder.setSelection( 0 );
            }
            else if ( TABLE_TAB.equals( data ) )
            {
                tabFolder.setSelection( 1 );
            }
            else if ( CSV_TAB.equals( data ) )
            {
                tabFolder.setSelection( 2 );
            }
            else if ( XLS_TAB.equals( data ) )
            {
                tabFolder.setSelection( 3 );
            }
        }
    }


    protected Control createContents( Composite parent )
    {
        BaseWidgetUtils.createSpacer( parent, 1 );
        tabFolder = new TabFolder( parent, SWT.TOP );

        createLdifTab();
        createTableTab();
        createCsvTab();
        createXlsTab();

        updateEnabled();
        validate();

        applyDialogFont( tabFolder );

        return tabFolder;
    }


    private void createTableTab()
    {

        tableTab = new TabItem( tabFolder, SWT.NONE );
        tableTab.setText( "CSV Copy" );

        Composite tableComposite = new Composite( tabFolder, SWT.NONE );
        tableComposite.setLayout( new GridLayout( 1, false ) );
        Composite tableInnerComposite = BaseWidgetUtils.createColumnContainer( tableComposite, 3, 1 );

        BaseWidgetUtils.createLabel( tableInnerComposite, "Select CSV copy format options:", 3 );
        BaseWidgetUtils.createSpacer( tableInnerComposite, 3 );

        tableAttributeDelimiterWidget = new OptionsInput( "Attribute Delimiter", "Tabulator (\\t)", "\t", new String[]
            { "Tabulator (\\t)", "Comma (,)", "Semikolon (;)" }, new String[]
            { "\t", ",", ";" }, getPreferenceStore().getString(
                BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER ), false, true );
        tableAttributeDelimiterWidget.createWidget( tableInnerComposite );
        tableAttributeDelimiterWidget.addWidgetModifyListener( this );

        tableValueDelimiterWidget = new OptionsInput( "Value Delimiter", "Pipe (|)", "|", new String[]
            { "Pipe (|)", "Comma (,)", "Semikolon (;)", "Newline (\\n)" }, new String[]
            { "|", ",", ";", "\n" }, getPreferenceStore().getString(
                BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER ), false, true );
        tableValueDelimiterWidget.createWidget( tableInnerComposite );
        tableValueDelimiterWidget.addWidgetModifyListener( this );

        tableQuoteWidget = new OptionsInput( "Quote Character", "Double Quote (\")", "\"", new String[]
            { "Double Quote (\")", "Single Quote (')" }, new String[]
            { "\"", "'" }, getPreferenceStore().getString( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER ),
            false, true );
        tableQuoteWidget.createWidget( tableInnerComposite );
        tableQuoteWidget.addWidgetModifyListener( this );

        tableLineSeparator = new LineSeparatorInput( getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR ), false );
        tableLineSeparator.createWidget( tableInnerComposite );
        tableLineSeparator.addWidgetModifyListener( this );

        tableBinaryEncodingWidget = new BinaryEncodingInput( getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING ), false );
        tableBinaryEncodingWidget.createWidget( tableInnerComposite );
        tableBinaryEncodingWidget.addWidgetModifyListener( this );

        Composite copyTableHintComposite = BaseWidgetUtils.createColumnContainer( tableInnerComposite, 3, 3 );
        BaseWidgetUtils.createWrappedLabeledText( copyTableHintComposite,
            "Hint: The default settings are suitable to paste the copied data into Excel or OpenOffice.", 1 );

        tableTab.setControl( tableComposite );
    }


    private void createCsvTab()
    {

        csvTab = new TabItem( tabFolder, SWT.NONE );
        csvTab.setText( "CSV Export" );

        Composite csvComposite = new Composite( tabFolder, SWT.NONE );
        csvComposite.setLayout( new GridLayout( 1, false ) );
        Composite csvInnerComposite = BaseWidgetUtils.createColumnContainer( csvComposite, 3, 1 );

        BaseWidgetUtils.createLabel( csvInnerComposite, "Select CSV export file format options:", 3 );
        BaseWidgetUtils.createSpacer( csvInnerComposite, 3 );

        csvAttributeDelimiterWidget = new OptionsInput( "Attribute Delimiter", "Comma (,)", ",", new String[]
            { "Comma (,)", "Semikolon (;)", "Tabulator (\\t)" }, new String[]
            { ",", ";", "\t" }, coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER ),
            false, true );
        csvAttributeDelimiterWidget.createWidget( csvInnerComposite );
        csvAttributeDelimiterWidget.addWidgetModifyListener( this );

        csvValueDelimiterWidget = new OptionsInput( "Value Delimiter", "Pipe (|)", "|", new String[]
            { "Pipe (|)", "Comma (,)", "Semikolon (;)", "Newline (\\n)" }, new String[]
            { "|", ",", ";", "\n" }, coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_VALUEDELIMITER ),
            false, true );
        csvValueDelimiterWidget.createWidget( csvInnerComposite );
        csvValueDelimiterWidget.addWidgetModifyListener( this );

        csvQuoteWidget = new OptionsInput( "Quote Character", "Double Quote (\")", "\"", new String[]
            { "Double Quote (\")", "Single Quote (')" }, new String[]
            { "\"", "'" }, coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_QUOTECHARACTER ), false,
            true );
        csvQuoteWidget.createWidget( csvInnerComposite );
        csvQuoteWidget.addWidgetModifyListener( this );

        csvLineSeparator = new LineSeparatorInput( coreStore
            .getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_LINESEPARATOR ), false );
        csvLineSeparator.createWidget( csvInnerComposite );
        csvLineSeparator.addWidgetModifyListener( this );

        csvBinaryEncodingWidget = new BinaryEncodingInput( coreStore
            .getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_BINARYENCODING ), false );
        csvBinaryEncodingWidget.createWidget( csvInnerComposite );
        csvBinaryEncodingWidget.addWidgetModifyListener( this );

        csvEncodingWidget = new FileEncodingInput( coreStore
            .getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING ), false );
        csvEncodingWidget.createWidget( csvInnerComposite );
        csvEncodingWidget.addWidgetModifyListener( this );

        csvTab.setControl( csvComposite );
    }


    private void createXlsTab()
    {

        xlsTab = new TabItem( tabFolder, SWT.NONE );
        xlsTab.setText( "Excel Export" );

        Composite xlsComposite = new Composite( tabFolder, SWT.NONE );
        xlsComposite.setLayout( new GridLayout( 1, false ) );
        Composite xlsInnerComposite = BaseWidgetUtils.createColumnContainer( xlsComposite, 3, 1 );

        BaseWidgetUtils.createWrappedLabeledText( xlsInnerComposite, "Select Excel export file format options:", 3 );
        BaseWidgetUtils.createSpacer( xlsInnerComposite, 3 );

        xlsValueDelimiterWidget = new OptionsInput( "Value Delimiter", "Pipe (|)", "|", new String[]
            { "Pipe (|)", "Comma (,)", "Semikolon (;)", "Newline (\\n)" }, new String[]
            { "|", ",", ";", "\n" }, coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_VALUEDELIMITER ),
            false, true );
        xlsValueDelimiterWidget.createWidget( xlsInnerComposite );
        xlsValueDelimiterWidget.addWidgetModifyListener( this );

        xlsBinaryEncodingWidget = new BinaryEncodingInput( coreStore
            .getString( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_BINARYENCODING ), false );
        xlsBinaryEncodingWidget.createWidget( xlsInnerComposite );
        xlsBinaryEncodingWidget.addWidgetModifyListener( this );

        xlsTab.setControl( xlsComposite );
    }


    private void createLdifTab()
    {

        ldifTab = new TabItem( tabFolder, SWT.NONE );
        ldifTab.setText( "LDIF" );

        Composite ldifComposite = new Composite( tabFolder, SWT.NONE );
        ldifComposite.setLayout( new GridLayout( 1, false ) );
        Composite ldifInnerComposite = BaseWidgetUtils.createColumnContainer( ldifComposite, 1, 1 );

        BaseWidgetUtils.createLabel( ldifInnerComposite, "Select LDIF format options:", 1 );
        BaseWidgetUtils.createSpacer( ldifInnerComposite, 1 );

        ldifLineSeparator = new LineSeparatorInput( coreStore
            .getString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR ), true );
        ldifLineSeparator.createWidget( ldifInnerComposite );
        ldifLineSeparator.addWidgetModifyListener( this );

        BaseWidgetUtils.createSpacer( ldifInnerComposite, 1 );

        Composite lineLengthComposite = BaseWidgetUtils.createColumnContainer( ldifInnerComposite, 3, 1 );
        BaseWidgetUtils.createLabel( lineLengthComposite, "Line length:", 1 );
        ldifLineLengthText = BaseWidgetUtils.createText( lineLengthComposite, "", 3, 1 );
        ldifLineLengthText.setText( coreStore.getString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH ) );
        ldifLineLengthText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
                if ( "".equals( ldifLineLengthText.getText() ) && e.text.matches( "[0]" ) )
                {
                    e.doit = false;
                }
            }
        } );
        ldifLineLengthText.addModifyListener( this );
        BaseWidgetUtils.createLabel( lineLengthComposite, "characters", 1 );

        ldifSpaceAfterColonButton = BaseWidgetUtils.createCheckbox( ldifInnerComposite, "Space after colon", 1 );
        ldifSpaceAfterColonButton.setSelection( coreStore
            .getBoolean( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON ) );

        // ldifSpaceBetweenRDNsButton =
        // BaseWidgetUtils.createCheckbox(ldifComposite, "Space between RDNs",
        // 1);
        // ldifSpaceBetweenRDNsButton.setSelection(coreStore.getBoolean(BrowserCoreConstants.PREFERENCE_LDIF_SPACE_BETWEEN_RDNS));

        ldifTab.setControl( ldifComposite );
    }


    private void updateEnabled()
    {

    }


    public boolean performOk()
    {

        coreStore.setValue( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH, this.ldifLineLengthText.getText() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR, this.ldifLineSeparator.getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON, this.ldifSpaceAfterColonButton
            .getSelection() );
        // coreStore.setValue(BrowserCoreConstants.PREFERENCE_LDIF_SPACE_BETWEEN_RDNS,
        // this.ldifSpaceBetweenRDNsButton.getSelection());
        BrowserCorePlugin.getDefault().savePluginPreferences();

        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER,
            this.csvAttributeDelimiterWidget.getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_VALUEDELIMITER, this.csvValueDelimiterWidget
            .getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_QUOTECHARACTER, this.csvQuoteWidget
            .getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_LINESEPARATOR, this.csvLineSeparator
            .getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_BINARYENCODING, this.csvBinaryEncodingWidget
            .getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING, this.csvEncodingWidget.getRawValue() );

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER,
            this.tableAttributeDelimiterWidget.getRawValue() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER,
            this.tableValueDelimiterWidget.getRawValue() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER,
            this.tableQuoteWidget.getRawValue() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR,
            this.tableLineSeparator.getRawValue() );
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING,
            this.tableBinaryEncodingWidget.getRawValue() );

        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_VALUEDELIMITER, this.xlsValueDelimiterWidget
            .getRawValue() );
        coreStore.setValue( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_BINARYENCODING, this.xlsBinaryEncodingWidget
            .getRawValue() );

        updateEnabled();
        validate();

        return true;
    }


    protected void performDefaults()
    {

        this.ldifLineLengthText.setText( coreStore.getDefaultString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH ) );
        this.ldifLineSeparator.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR ) );
        this.ldifSpaceAfterColonButton.setSelection( coreStore
            .getDefaultBoolean( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON ) );
        // this.ldifSpaceBetweenRDNsButton.setSelection(coreStore.getDefaultBoolean(BrowserCoreConstants.PREFERENCE_LDIF_SPACE_BETWEEN_RDNS));

        this.csvAttributeDelimiterWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER ) );
        this.csvValueDelimiterWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_VALUEDELIMITER ) );
        this.csvQuoteWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_QUOTECHARACTER ) );
        this.csvLineSeparator.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_LINESEPARATOR ) );
        this.csvBinaryEncodingWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_BINARYENCODING ) );
        this.csvEncodingWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING ) );

        this.tableAttributeDelimiterWidget.setRawValue( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER ) );
        this.tableValueDelimiterWidget.setRawValue( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER ) );
        this.tableQuoteWidget.setRawValue( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER ) );
        this.tableLineSeparator.setRawValue( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR ) );
        this.tableBinaryEncodingWidget.setRawValue( getPreferenceStore().getDefaultString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING ) );

        this.xlsValueDelimiterWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_VALUEDELIMITER ) );
        this.xlsBinaryEncodingWidget.setRawValue( coreStore
            .getDefaultString( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_BINARYENCODING ) );

        updateEnabled();
        validate();

        super.performDefaults();
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        updateEnabled();
        validate();
    }


    public void modifyText( ModifyEvent e )
    {
        updateEnabled();
        validate();
    }


    protected void validate()
    {
        setValid( !"".equals( csvAttributeDelimiterWidget.getRawValue() )
            && !"".equals( csvValueDelimiterWidget.getRawValue() ) && !"".equals( csvQuoteWidget.getRawValue() )
            && !"".equals( csvLineSeparator.getRawValue() ) && !"".equals( csvBinaryEncodingWidget.getRawValue() )
            && !"".equals( csvEncodingWidget.getRawValue() ) &&

            !"".equals( tableAttributeDelimiterWidget.getRawValue() )
            && !"".equals( tableValueDelimiterWidget.getRawValue() ) && !"".equals( tableQuoteWidget.getRawValue() )
            && !"".equals( tableLineSeparator.getRawValue() ) && !"".equals( tableBinaryEncodingWidget.getRawValue() )
            &&

            !"".equals( xlsValueDelimiterWidget.getRawValue() ) && !"".equals( xlsBinaryEncodingWidget.getRawValue() )
            &&

            !"".equals( ldifLineLengthText.getText() ) && !"".equals( ldifLineSeparator.getRawValue() ) );
    }

}
