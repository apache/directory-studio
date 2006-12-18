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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AttributeOptionsWizardPage extends WizardPage
{

    private AttributeWizard wizard;

    private String initialAttributeDescription;

    private String[] possibleLanguages;

    private Map possibleLangToCountriesMap;

    private List parsedLangList;

    private List parsedOptionList;

    private boolean parsedBinary;

    private Group langGroup;

    private ArrayList langLineList;

    private Group optionsGroup;

    private ArrayList optionLineList;

    private Button binaryOptionButton;

    private Text previewText;

    private int langGroupHeight = -1;

    private int optionGroupHeight = -1;


    public AttributeOptionsWizardPage( String pageName, String initialAttributeDescription, AttributeWizard wizard )
    {
        super( pageName );
        super.setTitle( "Options" );
        super.setDescription( "Optionally you may specify options (e.g. language tags)." );
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;
        this.initialAttributeDescription = initialAttributeDescription;

        SortedSet languageSet = new TreeSet();
        Map languageToCountrySetMap = new HashMap();
        Locale[] locales = Locale.getAvailableLocales();
        for ( int i = 0; i < locales.length; i++ )
        {
            Locale locale = locales[i];
            languageSet.add( locale.getLanguage() );
            if ( !languageToCountrySetMap.containsKey( locale.getLanguage() ) )
            {
                languageToCountrySetMap.put( locale.getLanguage(), new TreeSet() );
            }
            SortedSet countrySet = ( SortedSet ) languageToCountrySetMap.get( locale.getLanguage() );
            countrySet.add( locale.getCountry() );
        }
        this.possibleLanguages = ( String[] ) languageSet.toArray( new String[languageSet.size()] );
        this.possibleLangToCountriesMap = new HashMap();
        for ( Iterator it = languageToCountrySetMap.keySet().iterator(); it.hasNext(); )
        {
            String language = ( String ) it.next();
            SortedSet countrySet = ( SortedSet ) languageToCountrySetMap.get( language );
            String[] countries = ( String[] ) countrySet.toArray( new String[countrySet.size()] );
            this.possibleLangToCountriesMap.put( language, countries );
        }

        String attributeDescription = this.initialAttributeDescription;
        if ( attributeDescription == null )
            attributeDescription = "";
        String[] attributeDescriptionComponents = attributeDescription.split( ";" );
        this.parsedLangList = new ArrayList();
        this.parsedOptionList = new ArrayList();
        this.parsedBinary = false;
        for ( int i = 1; i < attributeDescriptionComponents.length; i++ )
        {
            if ( attributeDescriptionComponents[i].startsWith( "lang-" ) )
            {
                this.parsedLangList.add( attributeDescriptionComponents[i] );
            }
            else if ( attributeDescriptionComponents[i].equals( "binary" ) )
            {
                this.parsedBinary = true;
            }
            else
            {
                this.parsedOptionList.add( attributeDescriptionComponents[i] );
            }
        }
    }


    private void validate()
    {
        this.previewText.setText( wizard.getAttributeDescription() );
        setPageComplete( true );
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
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Lang group
        this.langGroup = BaseWidgetUtils.createGroup( composite, "Language tags", 2 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 2;
        langGroup.setLayoutData( gd );
        Composite langComposite = BaseWidgetUtils.createColumnContainer( this.langGroup, 6, 1 );
        this.langLineList = new ArrayList();

        BaseWidgetUtils.createSpacer( composite, 2 );

        // Options group with binary option
        this.optionsGroup = BaseWidgetUtils.createGroup( composite, "Other options", 2 );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 2;
        optionsGroup.setLayoutData( gd );
        Composite optionsComposite = BaseWidgetUtils.createColumnContainer( this.optionsGroup, 3, 1 );
        this.optionLineList = new ArrayList();
        Composite binaryComposite = BaseWidgetUtils.createColumnContainer( this.optionsGroup, 1, 1 );
        this.binaryOptionButton = BaseWidgetUtils.createCheckbox( binaryComposite, "binary option", 1 );
        this.binaryOptionButton.setSelection( parsedBinary );

        Label la = new Label( composite, SWT.NONE );
        gd = new GridData( GridData.GRAB_VERTICAL );
        gd.horizontalSpan = 2;
        la.setLayoutData( gd );

        // Preview text
        /* this.previewLabel = */BaseWidgetUtils.createLabel( composite, "Preview:", 1 );
        this.previewText = BaseWidgetUtils.createReadonlyText( composite, "", 1 );

        // fill lang
        if ( parsedLangList.isEmpty() )
        {
            addLangLine( langComposite, 0 );
        }
        else
        {
            for ( int i = 0; i < parsedLangList.size(); i++ )
            {
                addLangLine( langComposite, i );
                String l = ( String ) parsedLangList.get( i );
                String[] ls = l.split( "-", 3 );
                if ( ls.length > 1 )
                {
                    ( ( LangLine ) langLineList.get( i ) ).languageCombo.setText( ls[1] );
                }
                if ( ls.length > 2 )
                {
                    ( ( LangLine ) langLineList.get( i ) ).countryCombo.setText( ls[2] );
                }
            }
        }

        // fill options
        if ( parsedOptionList.isEmpty() )
        {
            addOptionLine( optionsComposite, 0 );
        }
        else
        {
            for ( int i = 0; i < parsedOptionList.size(); i++ )
            {
                addOptionLine( optionsComposite, i );
                ( ( OptionLine ) optionLineList.get( i ) ).optionText.setText( ( String ) parsedOptionList.get( i ) );
            }
        }

        // binary listener
        this.binaryOptionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        validate();

        setControl( composite );
    }


    String getAttributeOptions()
    {

        if ( this.binaryOptionButton == null || this.binaryOptionButton.isDisposed() )
        {
            return "";
        }

        // attribute type
        StringBuffer sb = new StringBuffer();
        // sb.append(wizard.getAttributeType());

        // options
        // sort and unique options
        Comparator comparator = new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                if ( o1 == null || !( o1 instanceof String ) || o2 == null || !( o2 instanceof String ) )
                {
                    throw new ClassCastException( "Must be String" );
                }
                return ( ( String ) o1 ).compareToIgnoreCase( ( String ) o2 );
            }
        };
        SortedSet options = new TreeSet( comparator );
        if ( this.binaryOptionButton.getSelection() )
        {
            options.add( "binary" );
        }
        for ( int i = 0; i < this.optionLineList.size(); i++ )
        {
            OptionLine optionLine = ( OptionLine ) this.optionLineList.get( i );
            if ( !"".equals( optionLine.optionText.getText() ) )
            {
                options.add( optionLine.optionText.getText() );
            }

            if ( this.optionLineList.size() > 1 )
            {
                optionLine.optionDeleteButton.setEnabled( true );
            }
            else
            {
                optionLine.optionDeleteButton.setEnabled( false );
            }
        }
        for ( int i = 0; i < this.langLineList.size(); i++ )
        {
            LangLine langLine = ( LangLine ) this.langLineList.get( i );
            String l = langLine.languageCombo.getText();
            String c = langLine.countryCombo.getText();

            if ( !"".equals( l ) )
            {
                String s = "lang-" + l;
                if ( !"".equals( c ) )
                {
                    s += "-" + c;
                }
                options.add( s );
            }

            if ( this.langLineList.size() > 1 )
            {
                langLine.deleteButton.setEnabled( true );
            }
            else
            {
                langLine.deleteButton.setEnabled( false );
            }
        }

        // append options
        for ( Iterator it = options.iterator(); it.hasNext(); )
        {
            String option = ( String ) it.next();
            sb.append( ';' );
            sb.append( option );
        }

        return sb.toString();
    }


    private void addOptionLine( Composite optionComposite, int index )
    {

        OptionLine[] optionLines = ( OptionLine[] ) optionLineList.toArray( new OptionLine[optionLineList.size()] );

        if ( optionLines.length > 0 )
        {
            for ( int i = 0; i < optionLines.length; i++ )
            {
                OptionLine oldOptionLine = optionLines[i];

                // remember values
                String oldValue = oldOptionLine.optionText.getText();

                // delete old
                oldOptionLine.optionText.dispose();
                oldOptionLine.optionAddButton.dispose();
                oldOptionLine.optionDeleteButton.dispose();
                optionLineList.remove( oldOptionLine );

                // add new
                OptionLine newOptionLine = createOptionLine( optionComposite );
                optionLineList.add( newOptionLine );

                // restore value
                newOptionLine.optionText.setText( oldValue );

                // check
                if ( index == i + 1 )
                {
                    OptionLine optionLine = createOptionLine( optionComposite );
                    optionLineList.add( optionLine );
                }
            }
        }
        else
        {
            OptionLine optionLine = createOptionLine( optionComposite );
            optionLineList.add( optionLine );
        }
    }


    private OptionLine createOptionLine( final Composite optionComposite )
    {
        OptionLine optionLine = new OptionLine();

        optionLine.optionText = new Text( optionComposite, SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
        optionLine.optionText.setLayoutData( gd );

        optionLine.optionAddButton = new Button( optionComposite, SWT.PUSH );
        optionLine.optionAddButton.setText( "  +   " );
        optionLine.optionAddButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = optionLineList.size();
                for ( int i = 0; i < optionLineList.size(); i++ )
                {
                    OptionLine optionLine = ( OptionLine ) optionLineList.get( i );
                    if ( optionLine.optionAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }
                addOptionLine( optionComposite, index );

                Shell shell = getShell();
                Point shellSize = shell.getSize();
                Point groupSize = optionComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newOptionGroupHeight = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newOptionGroupHeight - optionGroupHeight );
                optionComposite.layout( true, true );
                shell.layout( true, true );
                optionGroupHeight = newOptionGroupHeight;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        optionLine.optionDeleteButton = new Button( optionComposite, SWT.PUSH );
        optionLine.optionDeleteButton.setText( "  \u2212  " ); // \u2013
        optionLine.optionDeleteButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < optionLineList.size(); i++ )
                {
                    OptionLine optionLine = ( OptionLine ) optionLineList.get( i );
                    if ( optionLine.optionDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }
                deleteOptionLine( optionComposite, index );

                Shell shell = getShell();
                Point shellSize = shell.getSize();
                Point groupSize = optionComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newOptionGroupHeight = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newOptionGroupHeight - optionGroupHeight );
                optionComposite.layout( true, true );
                shell.layout( true, true );
                optionGroupHeight = newOptionGroupHeight;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        optionLine.optionText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        return optionLine;
    }


    private void deleteOptionLine( Composite optionComposite, int index )
    {
        OptionLine optionLine = ( OptionLine ) optionLineList.remove( index );
        if ( optionLine != null )
        {
            optionLine.optionText.dispose();
            optionLine.optionAddButton.dispose();
            optionLine.optionDeleteButton.dispose();
        }
    }

    public class OptionLine
    {
        public Text optionText;

        public Button optionAddButton;

        public Button optionDeleteButton;
    }


    private void addLangLine( Composite langComposite, int index )
    {

        LangLine[] langLines = ( LangLine[] ) langLineList.toArray( new LangLine[langLineList.size()] );

        if ( langLines.length > 0 )
        {
            for ( int i = 0; i < langLines.length; i++ )
            {
                LangLine oldLangLine = langLines[i];

                // remember values
                String oldLanguage = oldLangLine.languageCombo.getText();
                String oldCountry = oldLangLine.countryCombo.getText();

                // delete old
                oldLangLine.langLabel.dispose();
                oldLangLine.languageCombo.dispose();
                oldLangLine.minusLabel.dispose();
                oldLangLine.countryCombo.dispose();
                oldLangLine.addButton.dispose();
                oldLangLine.deleteButton.dispose();
                langLineList.remove( oldLangLine );

                // add new
                LangLine newLangLine = createLangLine( langComposite );
                langLineList.add( newLangLine );

                // restore value
                newLangLine.languageCombo.setText( oldLanguage );
                newLangLine.countryCombo.setText( oldCountry );

                // check
                if ( index == i + 1 )
                {
                    LangLine langLine = createLangLine( langComposite );
                    langLineList.add( langLine );
                }
            }
        }
        else
        {
            LangLine langLine = createLangLine( langComposite );
            langLineList.add( langLine );
        }
    }


    private LangLine createLangLine( final Composite langComposite )
    {
        final LangLine langLine = new LangLine();

        langLine.langLabel = BaseWidgetUtils.createLabel( langComposite, "lang-", 1 );

        langLine.languageCombo = BaseWidgetUtils.createCombo( langComposite, possibleLanguages, -1, 1 );

        langLine.minusLabel = BaseWidgetUtils.createLabel( langComposite, "-", 1 );

        langLine.countryCombo = BaseWidgetUtils.createCombo( langComposite, new String[0], -1, 1 );
        langLine.countryCombo.setEnabled( false );

        langLine.addButton = new Button( langComposite, SWT.PUSH );
        langLine.addButton.setText( "  +   " );
        langLine.addButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = langLineList.size();
                for ( int i = 0; i < langLineList.size(); i++ )
                {
                    LangLine langLine = ( LangLine ) langLineList.get( i );
                    if ( langLine.addButton == e.widget )
                    {
                        index = i + 1;
                    }
                }
                addLangLine( langComposite, index );

                Shell shell = getShell();
                Point shellSize = shell.getSize();
                Point groupSize = langComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newLangGroupHeight = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newLangGroupHeight - langGroupHeight );
                langComposite.layout( true, true );
                shell.layout( true, true );
                langGroupHeight = newLangGroupHeight;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        langLine.deleteButton = new Button( langComposite, SWT.PUSH );
        langLine.deleteButton.setText( "  \u2212  " ); // \u2013
        langLine.deleteButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < langLineList.size(); i++ )
                {
                    LangLine langLine = ( LangLine ) langLineList.get( i );
                    if ( langLine.deleteButton == e.widget )
                    {
                        index = i;
                    }
                }
                deleteLangLine( langComposite, index );

                Shell shell = getShell();
                Point shellSize = shell.getSize();
                Point groupSize = langComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newLangGroupHeight = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newLangGroupHeight - langGroupHeight );
                langComposite.layout( true, true );
                shell.layout( true, true );
                langGroupHeight = newLangGroupHeight;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        langLine.languageCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                if ( "".equals( langLine.languageCombo.getText() ) )
                {
                    langLine.countryCombo.setEnabled( false );
                }
                else
                {
                    langLine.countryCombo.setEnabled( true );
                    String oldValue = langLine.countryCombo.getText();
                    if ( possibleLangToCountriesMap.containsKey( langLine.languageCombo.getText() ) )
                    {
                        langLine.countryCombo.setItems( ( String[] ) possibleLangToCountriesMap
                            .get( langLine.languageCombo.getText() ) );
                    }
                    else
                    {
                        langLine.countryCombo.setItems( new String[0] );
                    }
                    langLine.countryCombo.setText( oldValue );
                }
                validate();
            }
        } );
        langLine.countryCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        return langLine;
    }


    private void deleteLangLine( Composite langComposite, int index )
    {
        LangLine langLine = ( LangLine ) langLineList.remove( index );
        if ( langLine != null )
        {
            langLine.langLabel.dispose();
            langLine.languageCombo.dispose();
            langLine.minusLabel.dispose();
            langLine.countryCombo.dispose();
            langLine.addButton.dispose();
            langLine.deleteButton.dispose();
        }
    }

    public class LangLine
    {
        public Label langLabel;

        public Combo languageCombo;

        public Label minusLabel;

        public Combo countryCombo;

        public Button addButton;

        public Button deleteButton;
    }

}
