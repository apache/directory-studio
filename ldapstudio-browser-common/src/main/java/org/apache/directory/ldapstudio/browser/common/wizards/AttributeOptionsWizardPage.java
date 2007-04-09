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

package org.apache.directory.ldapstudio.browser.common.wizards;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The AttributeOptionsWizardPageprovides input elements for various options
 * and a preview field.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeOptionsWizardPage extends WizardPage
{

    /** The wizard. */
    private AttributeWizard wizard;

    /** The shell */
    private Shell shell;

    /** The possible languages. */
    private String[] possibleLanguages;

    /** The possible language to countries map. */
    private Map<String, String[]> possibleLangToCountriesMap;

    /** The parsed lang list. */
    private List<String> parsedLangList;

    /** The parsed option list. */
    private List<String> parsedOptionList;

    /** The parsed binary option. */
    private boolean parsedBinary;

    /** The language group. */
    private Group langGroup;

    /** The lang line list. */
    private ArrayList<LangLine> langLineList;

    /** The options group. */
    private Group optionsGroup;

    /** The option line list. */
    private ArrayList<OptionLine> optionLineList;

    /** The binary option button. */
    private Button binaryOptionButton;

    /** The preview text. */
    private Text previewText;


    /**
     * Creates a new instance of AttributeOptionsWizardPage.
     * 
     * @param pageName the page name
     * @param initialAttributeDescription the initial attribute description
     * @param wizard the wizard
     */
    public AttributeOptionsWizardPage( String pageName, String initialAttributeDescription, AttributeWizard wizard )
    {
        super( pageName );
        super.setTitle( "Options" );
        super.setDescription( "Optionally you may specify options (e.g. language tags)." );
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ATTRIBUTE_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;

        // init possible languages and countries
        SortedSet<String> languageSet = new TreeSet<String>();
        Map<String, SortedSet<String>> languageToCountrySetMap = new HashMap<String, SortedSet<String>>();
        Locale[] locales = Locale.getAvailableLocales();
        for ( int i = 0; i < locales.length; i++ )
        {
            Locale locale = locales[i];
            languageSet.add( locale.getLanguage() );
            if ( !languageToCountrySetMap.containsKey( locale.getLanguage() ) )
            {
                languageToCountrySetMap.put( locale.getLanguage(), new TreeSet<String>() );
            }
            SortedSet<String> countrySet = languageToCountrySetMap.get( locale.getLanguage() );
            countrySet.add( locale.getCountry() );
        }
        possibleLanguages = languageSet.toArray( new String[languageSet.size()] );
        possibleLangToCountriesMap = new HashMap<String, String[]>();
        for ( Iterator<String> it = languageToCountrySetMap.keySet().iterator(); it.hasNext(); )
        {
            String language = it.next();
            SortedSet<String> countrySet = languageToCountrySetMap.get( language );
            String[] countries = countrySet.toArray( new String[countrySet.size()] );
            possibleLangToCountriesMap.put( language, countries );
        }

        // parse options
        if ( initialAttributeDescription == null )
        {
            initialAttributeDescription = "";
        }
        String[] attributeDescriptionComponents = initialAttributeDescription.split( ";" );
        parsedLangList = new ArrayList<String>();
        parsedOptionList = new ArrayList<String>();
        parsedBinary = false;
        for ( int i = 1; i < attributeDescriptionComponents.length; i++ )
        {
            if ( attributeDescriptionComponents[i].startsWith( "lang-" ) )
            {
                parsedLangList.add( attributeDescriptionComponents[i] );
            }
            else if ( attributeDescriptionComponents[i].equals( "binary" ) )
            {
                parsedBinary = true;
            }
            else
            {
                parsedOptionList.add( attributeDescriptionComponents[i] );
            }
        }
    }


    /**
     * Validates the options.
     */
    private void validate()
    {
        previewText.setText( wizard.getAttributeDescription() );
        setPageComplete( true );
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
        shell = parent.getShell();

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Lang group
        langGroup = BaseWidgetUtils.createGroup( composite, "Language tags", 2 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 2;
        langGroup.setLayoutData( gd );
        Composite langComposite = BaseWidgetUtils.createColumnContainer( langGroup, 6, 1 );
        langLineList = new ArrayList<LangLine>();

        BaseWidgetUtils.createSpacer( composite, 2 );

        // Options group with binary option
        optionsGroup = BaseWidgetUtils.createGroup( composite, "Other options", 2 );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 2;
        optionsGroup.setLayoutData( gd );
        Composite optionsComposite = BaseWidgetUtils.createColumnContainer( optionsGroup, 3, 1 );
        optionLineList = new ArrayList<OptionLine>();
        Composite binaryComposite = BaseWidgetUtils.createColumnContainer( optionsGroup, 1, 1 );
        binaryOptionButton = BaseWidgetUtils.createCheckbox( binaryComposite, "binary option", 1 );
        binaryOptionButton.setSelection( parsedBinary );

        Label la = new Label( composite, SWT.NONE );
        gd = new GridData( GridData.GRAB_VERTICAL );
        gd.horizontalSpan = 2;
        la.setLayoutData( gd );

        // Preview text
        BaseWidgetUtils.createLabel( composite, "Preview:", 1 );
        previewText = BaseWidgetUtils.createReadonlyText( composite, "", 1 );

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
                String l = parsedLangList.get( i );
                String[] ls = l.split( "-", 3 );
                if ( ls.length > 1 )
                {
                    langLineList.get( i ).languageCombo.setText( ls[1] );
                }
                if ( ls.length > 2 )
                {
                    langLineList.get( i ).countryCombo.setText( ls[2] );
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
                optionLineList.get( i ).optionText.setText( parsedOptionList.get( i ) );
            }
        }

        // binary listener
        binaryOptionButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        validate();

        setControl( composite );
    }


    /**
     * Gets the attribute options.
     * 
     * @return the attribute options
     */
    String getAttributeOptions()
    {

        if ( binaryOptionButton == null || binaryOptionButton.isDisposed() )
        {
            return "";
        }

        // attribute type
        StringBuffer sb = new StringBuffer();

        // options
        // sort and unique options
        Comparator<String> comparator = new Comparator<String>()
        {
            public int compare( String s1, String s2 )
            {
                if ( s1 == null || s2 == null )
                {
                    throw new ClassCastException( "Must not be null" );
                }
                return s1.compareToIgnoreCase( s2 );
            }
        };
        SortedSet<String> options = new TreeSet<String>( comparator );
        if ( binaryOptionButton.getSelection() )
        {
            options.add( "binary" );
        }
        for ( int i = 0; i < optionLineList.size(); i++ )
        {
            OptionLine optionLine = optionLineList.get( i );
            if ( !"".equals( optionLine.optionText.getText() ) )
            {
                options.add( optionLine.optionText.getText() );
            }

            if ( optionLineList.size() > 1 )
            {
                optionLine.optionDeleteButton.setEnabled( true );
            }
            else
            {
                optionLine.optionDeleteButton.setEnabled( false );
            }
        }
        for ( int i = 0; i < langLineList.size(); i++ )
        {
            LangLine langLine = langLineList.get( i );
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

            if ( langLineList.size() > 1 )
            {
                langLine.deleteButton.setEnabled( true );
            }
            else
            {
                langLine.deleteButton.setEnabled( false );
            }
        }

        // append options
        for ( Iterator<String> it = options.iterator(); it.hasNext(); )
        {
            String option = it.next();
            sb.append( ';' );
            sb.append( option );
        }

        return sb.toString();
    }


    /**
     * Adds an option line at the given index.
     * 
     * @param optionComposite the option composite
     * @param index the index
     */
    private void addOptionLine( Composite optionComposite, int index )
    {
        OptionLine[] optionLines = optionLineList.toArray( new OptionLine[optionLineList.size()] );

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

        shell.layout( true, true );
    }


    /**
     * Creates the option line.
     * 
     * @param optionComposite the option composite
     * 
     * @return the option line
     */
    private OptionLine createOptionLine( final Composite optionComposite )
    {
        OptionLine optionLine = new OptionLine();

        optionLine.optionText = new Text( optionComposite, SWT.BORDER );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
        optionLine.optionText.setLayoutData( gd );

        optionLine.optionAddButton = new Button( optionComposite, SWT.PUSH );
        optionLine.optionAddButton.setText( "  +   " );
        optionLine.optionAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = optionLineList.size();
                for ( int i = 0; i < optionLineList.size(); i++ )
                {
                    OptionLine optionLine = optionLineList.get( i );
                    if ( optionLine.optionAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }

                addOptionLine( optionComposite, index );

                validate();
            }
        } );

        optionLine.optionDeleteButton = new Button( optionComposite, SWT.PUSH );
        optionLine.optionDeleteButton.setText( "  \u2212  " ); // \u2013
        optionLine.optionDeleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < optionLineList.size(); i++ )
                {
                    OptionLine optionLine = optionLineList.get( i );
                    if ( optionLine.optionDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }

                deleteOptionLine( optionComposite, index );

                validate();
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


    /**
     * Deletes the option line at the given index.
     * 
     * @param optionComposite the option composite
     * @param index the index
     */
    private void deleteOptionLine( Composite optionComposite, int index )
    {
        OptionLine optionLine = optionLineList.remove( index );
        if ( optionLine != null )
        {
            optionLine.optionText.dispose();
            optionLine.optionAddButton.dispose();
            optionLine.optionDeleteButton.dispose();

            if ( !optionComposite.isDisposed() )
            {
                shell.layout( true, true );
            }
        }
    }

    /**
     * The class OptionLine is a wrapper for all input elements of an option.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public class OptionLine
    {
        /** The option text. */
        public Text optionText;

        /** The option add button. */
        public Button optionAddButton;

        /** The option delete button. */
        public Button optionDeleteButton;
    }


    /**
     * Adds a language line at the given index.
     * 
     * @param langComposite the language composite
     * @param index the index
     */
    private void addLangLine( Composite langComposite, int index )
    {
        LangLine[] langLines = langLineList.toArray( new LangLine[langLineList.size()] );

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

        shell.layout( true, true );
    }


    /**
     * Creates a language line.
     * 
     * @param langComposite the language composite
     * 
     * @return the language line
     */
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
        langLine.addButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = langLineList.size();
                for ( int i = 0; i < langLineList.size(); i++ )
                {
                    LangLine langLine = langLineList.get( i );
                    if ( langLine.addButton == e.widget )
                    {
                        index = i + 1;
                    }
                }

                addLangLine( langComposite, index );

                validate();
            }
        } );

        langLine.deleteButton = new Button( langComposite, SWT.PUSH );
        langLine.deleteButton.setText( "  \u2212  " ); // \u2013
        langLine.deleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < langLineList.size(); i++ )
                {
                    LangLine langLine = langLineList.get( i );
                    if ( langLine.deleteButton == e.widget )
                    {
                        index = i;
                    }
                }

                deleteLangLine( langComposite, index );

                validate();
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
                        langLine.countryCombo.setItems( possibleLangToCountriesMap.get( langLine.languageCombo
                            .getText() ) );
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


    /**
     * Deletes the language line at the given index.
     * 
     * @param langComposite the language composite
     * @param index the index
     */
    private void deleteLangLine( Composite langComposite, int index )
    {
        LangLine langLine = langLineList.remove( index );
        if ( langLine != null )
        {
            langLine.langLabel.dispose();
            langLine.languageCombo.dispose();
            langLine.minusLabel.dispose();
            langLine.countryCombo.dispose();
            langLine.addButton.dispose();
            langLine.deleteButton.dispose();

            if ( !langComposite.isDisposed() )
            {
                shell.layout( true, true );
            }
        }
    }

    /**
     * The class LangLine is a wrapper for all input elements of a language tag.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public class LangLine
    {

        /** The lang label. */
        public Label langLabel;

        /** The language combo. */
        public Combo languageCombo;

        /** The minus label. */
        public Label minusLabel;

        /** The country combo. */
        public Combo countryCombo;

        /** The add button. */
        public Button addButton;

        /** The delete button. */
        public Button deleteButton;
    }

}
