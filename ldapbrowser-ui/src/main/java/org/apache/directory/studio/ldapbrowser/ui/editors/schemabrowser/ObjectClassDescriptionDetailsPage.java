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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.ObjectClassDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * The ObjectClassDescriptionDetailsPage displays the details of an
 * object class description.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassDescriptionDetailsPage extends SchemaDetailsPage
{

    /** The main section, contains oid, names, desc and kind */
    private Section mainSection;

    /** The numeric oid field */
    private Text numericOidText;

    /** The names field */
    private Text namesText;

    /** The description field */
    private Text descText;

    /** The kind field */
    private Text kindText;

    /** The section with links to superior object classes */
    private Section superclassesSection;

    /** The links to superior object classes */
    private Hyperlink[] superLinks;

    /** The section with links to derived object classes */
    private Section subclassesSection;

    /** The links to derived object classes */
    private Hyperlink[] subLinks;

    /** The section with links to must attribute types */
    private Section mustSection;

    /** The links to must attribute types */
    private Hyperlink[] mustLinks;

    /** The section with links to may attribute types */
    private Section maySection;

    /** The links to may attribute types */
    private Hyperlink[] mayLinks;


    /**
     * Creates a new instance of ObjectClassDescriptionDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    public ObjectClassDescriptionDetailsPage( SchemaPage schemaPage, FormToolkit toolkit )
    {
        super( schemaPage, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( final ScrolledForm detailForm )
    {
        this.detailForm = detailForm;
        detailForm.getBody().setLayout( new GridLayout() );

        // create main section
        mainSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        mainSection.setText( "Details" );
        mainSection.marginWidth = 0;
        mainSection.marginHeight = 0;
        mainSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mainSection );

        // create must section
        mustSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        mustSection.setText( "MUST Attributes" );
        mustSection.marginWidth = 0;
        mustSection.marginHeight = 0;
        mustSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mustSection );
        mustSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create may section
        maySection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        maySection.setText( "MAY Attributes" );
        maySection.marginWidth = 0;
        maySection.marginHeight = 0;
        maySection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( maySection );
        maySection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create superior section
        superclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        superclassesSection.setText( "Superclasses" );
        superclassesSection.marginWidth = 0;
        superclassesSection.marginHeight = 0;
        superclassesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( superclassesSection );
        superclassesSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create subclasses section
        subclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        subclassesSection.setText( "Subclasses" );
        subclassesSection.marginWidth = 0;
        subclassesSection.marginHeight = 0;
        subclassesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( subclassesSection );
        subclassesSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create raw section
        createRawSection();
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( Object input )
    {
        ObjectClassDescription ocd = null;
        if ( input instanceof ObjectClassDescription )
        {
            ocd = ( ObjectClassDescription ) input;
        }

        // create main content
        this.createMainContent( ocd );

        // create contents of dynamic sections
        this.createSuperclassContents( ocd );
        this.createSubclassContents( ocd );
        this.createMustContents( ocd );
        this.createMayContents( ocd );
        super.createRawContents( ocd );

        this.detailForm.reflow( true );
    }


    /**
     * Creates the content of the main section. It is newly created
     * on every input change to ensure a proper layout of 
     * multilined descriptions. 
     *
     * @param ocd the object class description
     */
    private void createMainContent( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( mainSection.getClient() != null )
        {
            mainSection.getClient().dispose();
        }

        // create new client
        Composite mainClient = toolkit.createComposite( mainSection, SWT.WRAP );
        GridLayout mainLayout = new GridLayout( 2, false );
        mainClient.setLayout( mainLayout );
        mainSection.setClient( mainClient );

        // create new content
        if ( ocd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( ocd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Objectclass names:", SWT.NONE );
            namesText = toolkit.createText( mainClient, getNonNullString( ocd.toString() ), SWT.NONE );
            namesText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            namesText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( ocd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );

            String kind = "";
            if ( ocd.isStructural() )
            {
                kind = "structural";
            }
            else if ( ocd.isAbstract() )
            {
                kind = "abstract";
            }
            else if ( ocd.isAuxiliary() )
            {
                kind = "auxiliary";
            }
            if ( ocd.isObsolete() )
            {
                kind += " (obsolete)";
            }
            toolkit.createLabel( mainClient, "Objectclass kind:", SWT.NONE );
            kindText = toolkit.createText( mainClient, getNonNullString( kind ), SWT.NONE );
            kindText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            kindText.setEditable( false );
        }

        mainSection.layout();
    }


    /**
     * Creates the content of the must section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createMustContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( mustSection.getClient() != null )
        {
            mustSection.getClient().dispose();
        }

        // create new client
        Composite mustClient = toolkit.createComposite( mustSection, SWT.WRAP );
        mustClient.setLayout( new GridLayout() );
        mustSection.setClient( mustClient );

        // create new content
        if ( ocd != null )
        {
            String[] names = ocd.getMustAttributeTypeDescriptionNamesTransitive();
            if ( names != null && names.length > 0 )
            {
                mustSection.setText( "MUST Attributes (" + names.length + ")" );
                mustLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasAttributeTypeDescription( names[i] ) )
                    {
                        AttributeTypeDescription mustAtd = ocd.getSchema().getAttributeTypeDescription( names[i] );
                        mustLinks[i] = toolkit.createHyperlink( mustClient, mustAtd.toString(), SWT.WRAP );
                        mustLinks[i].setHref( mustAtd );
                        mustLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mustLinks[i].setUnderlined( true );
                        mustLinks[i].setEnabled( true );
                        mustLinks[i].addHyperlinkListener( this );
                    }
                    else
                    {
                        mustLinks[i] = toolkit.createHyperlink( mustClient, names[i], SWT.WRAP );
                        mustLinks[i].setHref( null );
                        mustLinks[i].setUnderlined( false );
                        mustLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                mustSection.setText( "MUST Attributes (0)" );
                mustLinks = new Hyperlink[0];
                Text mustText = toolkit.createText( mustClient, getNonNullString( null ), SWT.NONE );
                mustText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mustText.setEditable( false );
            }
        }
        else
        {
            mustSection.setText( "MUST Attributes" );
        }

        mustSection.layout();
    }


    /**
     * Creates the content of the may section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createMayContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( maySection.getClient() != null )
        {
            maySection.getClient().dispose();
        }

        // create new client
        Composite mayClient = toolkit.createComposite( maySection, SWT.WRAP );
        mayClient.setLayout( new GridLayout() );
        maySection.setClient( mayClient );

        // create new content
        if ( ocd != null )
        {
            String[] names = ocd.getMayAttributeTypeDescriptionNamesTransitive();
            if ( names != null && names.length > 0 )
            {
                maySection.setText( "MAY Attributes (" + names.length + ")" );
                mayLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasAttributeTypeDescription( names[i] ) )
                    {
                        AttributeTypeDescription mayAtd = ocd.getSchema().getAttributeTypeDescription( names[i] );
                        mayLinks[i] = toolkit.createHyperlink( mayClient, mayAtd.toString(), SWT.WRAP );
                        mayLinks[i].setHref( mayAtd );
                        mayLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mayLinks[i].setUnderlined( true );
                        mayLinks[i].setEnabled( true );
                        mayLinks[i].addHyperlinkListener( this );
                    }
                    else
                    {
                        mayLinks[i] = toolkit.createHyperlink( mayClient, names[i], SWT.WRAP );
                        mayLinks[i].setHref( null );
                        mayLinks[i].setUnderlined( false );
                        mayLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                maySection.setText( "MAY Attributes (0)" );
                mayLinks = new Hyperlink[0];
                Text mayText = toolkit.createText( mayClient, getNonNullString( null ), SWT.NONE );
                mayText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mayText.setEditable( false );
            }
        }
        else
        {
            maySection.setText( "MAY Attributes" );
        }
        maySection.layout();
    }


    /**
     * Creates the content of the must section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createSubclassContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( subclassesSection.getClient() != null )
        {
            subclassesSection.getClient().dispose();
        }

        // create new client
        Composite subClient = toolkit.createComposite( subclassesSection, SWT.WRAP );
        subClient.setLayout( new GridLayout() );
        subclassesSection.setClient( subClient );

        // create new content
        if ( ocd != null )
        {
            ObjectClassDescription[] subOCDs = ocd.getSubObjectClassDescriptions();
            if ( subOCDs != null && subOCDs.length > 0 )
            {
                subclassesSection.setText( "Subclasses (" + subOCDs.length + ")" );
                subLinks = new Hyperlink[subOCDs.length];
                for ( int i = 0; i < subOCDs.length; i++ )
                {
                    subLinks[i] = toolkit.createHyperlink( subClient, subOCDs[i].toString(), SWT.WRAP );
                    subLinks[i].setHref( subOCDs[i] );
                    subLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    subLinks[i].setUnderlined( true );
                    subLinks[i].setEnabled( true );
                    subLinks[i].addHyperlinkListener( this );
                }
            }
            else
            {
                subclassesSection.setText( "Subclasses (0)" );
                subLinks = new Hyperlink[0];
                Text derivedText = toolkit.createText( subClient, getNonNullString( null ), SWT.NONE );
                derivedText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                derivedText.setEditable( false );
            }
        }
        else
        {
            subclassesSection.setText( "Subclasses" );
        }

        subclassesSection.layout();
    }


    /**
     * Creates the content of the must section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createSuperclassContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( superclassesSection.getClient() != null )
        {
            superclassesSection.getClient().dispose();
        }

        // create new client
        Composite superClient = toolkit.createComposite( superclassesSection, SWT.WRAP );
        superClient.setLayout( new GridLayout() );
        superclassesSection.setClient( superClient );

        // craete new content
        if ( ocd != null )
        {
            String[] names = ocd.getSuperiorObjectClassDescriptionNames();
            if ( names != null && names.length > 0 )
            {
                superclassesSection.setText( "Superclasses (" + names.length + ")" );
                Composite supClient = toolkit.createComposite( superClient, SWT.WRAP );
                GridLayout gl = new GridLayout();
                gl.marginWidth = 0;
                gl.marginHeight = 0;
                supClient.setLayout( gl );
                superLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasObjectClassDescription( names[i] ) )
                    {
                        ObjectClassDescription supOcd = ocd.getSchema().getObjectClassDescription( names[i] );
                        superLinks[i] = toolkit.createHyperlink( supClient, supOcd.toString(), SWT.WRAP );
                        superLinks[i].setHref( supOcd );
                        superLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        superLinks[i].setUnderlined( true );
                        superLinks[i].setEnabled( true );
                        superLinks[i].addHyperlinkListener( this );
                    }
                    else
                    {
                        superLinks[i] = toolkit.createHyperlink( supClient, names[i], SWT.WRAP );
                        superLinks[i].setHref( null );
                        superLinks[i].setUnderlined( false );
                        superLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                superclassesSection.setText( "Superclasses (0)" );
                superLinks = new Hyperlink[0];
                Text superText = toolkit.createText( superClient, getNonNullString( null ), SWT.NONE );
                superText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                superText.setEditable( false );
            }
        }
        else
        {
            superclassesSection.setText( "Superclasses" );
        }

        superclassesSection.layout();
    }

}
