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

package org.apache.directory.ldapstudio.browser.ui.dialogs.properties;


import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenJob;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


public class EntryPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    private Text dnText;

    private Text urlText;

    private Text ctText;

    private Text cnText;

    private Text mtText;

    private Text mnText;

    private Button reloadCmiButton;

    private Text sizeText;

    private Text childrenText;

    private Text attributesText;

    private Text valuesText;

    private Button includeOperationalAttributesButton;

    private Button reloadEntryButton;


    public EntryPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite mainGroup = BaseWidgetUtils.createColumnContainer( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), 2, 1 );
        BaseWidgetUtils.createLabel( mainGroup, "DN:", 1 );
        dnText = BaseWidgetUtils.createWrappedLabeledText( mainGroup, "", 1 );
        BaseWidgetUtils.createLabel( mainGroup, "URL:", 1 );
        urlText = BaseWidgetUtils.createWrappedLabeledText( mainGroup, "", 1 );

        Group cmiGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Create and Modify Information", 1 );
        Composite cmiComposite = BaseWidgetUtils.createColumnContainer( cmiGroup, 3, 1 );

        BaseWidgetUtils.createLabel( cmiComposite, "Create Timestamp:", 1 );
        ctText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 );

        BaseWidgetUtils.createLabel( cmiComposite, "Creators Name:", 1 );
        cnText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 );

        BaseWidgetUtils.createLabel( cmiComposite, "Modify Timestamp:", 1 );
        mtText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 );

        BaseWidgetUtils.createLabel( cmiComposite, "Modifiers Name:", 1 );
        mnText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 1 );

        reloadCmiButton = BaseWidgetUtils.createButton( cmiComposite, "", 1 );
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.BOTTOM;
        gd.horizontalAlignment = SWT.RIGHT;
        reloadCmiButton.setLayoutData( gd );
        reloadCmiButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                reloadOperationalAttributes();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        Group sizingGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            "Sizing Information", 1 );
        Composite sizingComposite = BaseWidgetUtils.createColumnContainer( sizingGroup, 3, 1 );

        BaseWidgetUtils.createLabel( sizingComposite, "Entry Size:", 1 );
        sizeText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 );

        BaseWidgetUtils.createLabel( sizingComposite, "Number of Children:", 1 );
        childrenText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 );

        BaseWidgetUtils.createLabel( sizingComposite, "Number of Attributes:", 1 );
        attributesText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 );

        BaseWidgetUtils.createLabel( sizingComposite, "Number of Values:", 1 );
        valuesText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 );

        includeOperationalAttributesButton = BaseWidgetUtils.createCheckbox( sizingComposite,
            "Include operational attributes", 2 );
        includeOperationalAttributesButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                entryUpdated( getEntry( getElement() ) );
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        reloadEntryButton = BaseWidgetUtils.createButton( sizingComposite, "", 1 );
        gd = new GridData();
        gd.verticalAlignment = SWT.BOTTOM;
        gd.horizontalAlignment = SWT.RIGHT;
        reloadEntryButton.setLayoutData( gd );
        reloadEntryButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                reloadEntry();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        this.entryUpdated( getEntry( getElement() ) );

        return composite;
    }


    private void reloadOperationalAttributes()
    {
        IEntry entry = EntryPropertyPage.getEntry( getElement() );
        InitializeAttributesJob job = new InitializeAttributesJob( new IEntry[]
            { entry }, true );
        RunnableContextJobAdapter.execute( job );

        this.entryUpdated( entry );
    }


    private void reloadEntry()
    {
        IEntry entry = EntryPropertyPage.getEntry( getElement() );
        InitializeChildrenJob job1 = new InitializeChildrenJob( new IEntry[]
            { entry } );
        InitializeAttributesJob job2 = new InitializeAttributesJob( new IEntry[]
            { entry }, true );
        RunnableContextJobAdapter.execute( job1 );
        RunnableContextJobAdapter.execute( job2 );
        this.entryUpdated( entry );
    }


    static IEntry getEntry( Object element )
    {
        IEntry entry = null;
        if ( element instanceof IAdaptable )
        {
            entry = ( IEntry ) ( ( IAdaptable ) element ).getAdapter( IEntry.class );
        }
        return entry;
    }


    public boolean isDisposed()
    {
        return this.dnText.isDisposed();
    }


    public void entryUpdated( EntryModificationEvent event )
    {
        this.entryUpdated( event.getModifiedEntry() );
    }


    private String getNonNullStringValue( IAttribute att )
    {
        String value = null;
        if ( att != null )
        {
            value = att.getStringValue();
        }
        return value != null ? value : "-";
    }


    private void entryUpdated( IEntry entry )
    {

        if ( !this.dnText.isDisposed() )
        {

            this.setMessage( "Entry " + entry.getDn() );

            this.dnText.setText( entry.getDn().toString() );
            this.urlText.setText( entry.getUrl().toString() );
            this.ctText.setText( getNonNullStringValue( entry
                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ) ) );
            this.cnText.setText( getNonNullStringValue( entry
                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_CREATORS_NAME ) ) );
            this.mtText.setText( getNonNullStringValue( entry
                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ) ) );
            this.mnText.setText( getNonNullStringValue( entry
                .getAttribute( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFIERS_NAME ) ) );
            this.reloadCmiButton.setText( "Refresh" );

            int attCount = 0;
            int valCount = 0;
            int bytes = 0;

            IAttribute[] allAttributes = entry.getAttributes();
            if ( allAttributes != null )
            {
                for ( int attIndex = 0; attIndex < allAttributes.length; attIndex++ )
                {
                    if ( !allAttributes[attIndex].isOperationalAttribute()
                        || this.includeOperationalAttributesButton.getSelection() )
                    {
                        attCount++;
                        IValue[] allValues = allAttributes[attIndex].getValues();
                        for ( int valIndex = 0; valIndex < allValues.length; valIndex++ )
                        {
                            if ( !allValues[valIndex].isEmpty() )
                            {
                                valCount++;
                                bytes += allValues[valIndex].getBinaryValue().length;
                            }
                        }
                    }
                }
            }

            this.reloadEntryButton.setText( "Refresh" );
            if ( !entry.isChildrenInitialized() )
            {
                this.childrenText.setText( "Not checked" );
            }
            else
            {
                this.childrenText.setText( "" + entry.getChildrenCount()
                    + ( entry.hasMoreChildren() ? " fetched, may have more" : "" ) );
            }
            this.attributesText.setText( "" + attCount );
            this.valuesText.setText( "" + valCount );
            this.sizeText.setText( Utils.formatBytes( bytes ) );
        }
    }

}
