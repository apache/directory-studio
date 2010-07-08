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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.properties;


import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeChildrenRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osgi.util.NLS;
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


/**
 * This page shows some info about the selected Entry.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    /** The dn text. */
    private Text dnText;

    /** The url text. */
    private Text urlText;

    /** The ct text. */
    private Text ctText;

    /** The cn text. */
    private Text cnText;

    /** The mt text. */
    private Text mtText;

    /** The mn text. */
    private Text mnText;

    /** The reload cmi button. */
    private Button reloadCmiButton;

    /** The size text. */
    private Text sizeText;

    /** The children text. */
    private Text childrenText;

    /** The attributes text. */
    private Text attributesText;

    /** The values text. */
    private Text valuesText;

    /** The include operational attributes button. */
    private Button includeOperationalAttributesButton;

    /** The reload entry button. */
    private Button reloadEntryButton;


    /**
     * Creates a new instance of EntryPropertyPage.
     */
    public EntryPropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite mainGroup = BaseWidgetUtils.createColumnContainer( BaseWidgetUtils.createColumnContainer( composite,
            1, 1 ), 2, 1 );
        BaseWidgetUtils.createLabel( mainGroup, Messages.getString( "EntryPropertyPage.DN" ), 1 ); //$NON-NLS-1$
        dnText = BaseWidgetUtils.createWrappedLabeledText( mainGroup, "", 1 ); //$NON-NLS-1$
        GridData dnTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        dnTextGridData.widthHint = 300;
        dnText.setLayoutData( dnTextGridData );

        BaseWidgetUtils.createLabel( mainGroup, Messages.getString( "EntryPropertyPage.URL" ), 1 ); //$NON-NLS-1$
        urlText = BaseWidgetUtils.createWrappedLabeledText( mainGroup, "", 1 ); //$NON-NLS-1$
        GridData urlTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        urlTextGridData.widthHint = 300;
        urlText.setLayoutData( urlTextGridData );

        Group cmiGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "EntryPropertyPage.CreateModifyinformation" ), 1 ); //$NON-NLS-1$
        Composite cmiComposite = BaseWidgetUtils.createColumnContainer( cmiGroup, 3, 1 );

        BaseWidgetUtils.createLabel( cmiComposite, Messages.getString( "EntryPropertyPage.CreateTimestamp" ), 1 ); //$NON-NLS-1$
        ctText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 ); //$NON-NLS-1$
        GridData ctTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        ctTextGridData.widthHint = 300;
        ctText.setLayoutData( ctTextGridData );

        BaseWidgetUtils.createLabel( cmiComposite, Messages.getString( "EntryPropertyPage.CreatorsName" ), 1 ); //$NON-NLS-1$
        cnText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 ); //$NON-NLS-1$
        GridData cnTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        cnTextGridData.widthHint = 300;
        cnText.setLayoutData( cnTextGridData );

        BaseWidgetUtils.createLabel( cmiComposite, Messages.getString( "EntryPropertyPage.ModifyTimestamp" ), 1 ); //$NON-NLS-1$
        mtText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 2 ); //$NON-NLS-1$
        GridData mtTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        mtTextGridData.widthHint = 300;
        mtText.setLayoutData( mtTextGridData );

        BaseWidgetUtils.createLabel( cmiComposite, Messages.getString( "EntryPropertyPage.ModifiersName" ), 1 ); //$NON-NLS-1$
        mnText = BaseWidgetUtils.createLabeledText( cmiComposite, "", 1 ); //$NON-NLS-1$
        GridData mnTextGridData = new GridData( GridData.FILL_HORIZONTAL );
        mnTextGridData.widthHint = 300;
        mnText.setLayoutData( mnTextGridData );

        reloadCmiButton = BaseWidgetUtils.createButton( cmiComposite, "", 1 ); //$NON-NLS-1$
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
            Messages.getString( "EntryPropertyPage.SizingInformation" ), 1 ); //$NON-NLS-1$
        Composite sizingComposite = BaseWidgetUtils.createColumnContainer( sizingGroup, 3, 1 );

        BaseWidgetUtils.createLabel( sizingComposite, Messages.getString( "EntryPropertyPage.EntrySize" ), 1 ); //$NON-NLS-1$
        sizeText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 ); //$NON-NLS-1$
        GridData sizeTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        sizeTextGridData.widthHint = 300;
        sizeText.setLayoutData( sizeTextGridData );

        BaseWidgetUtils.createLabel( sizingComposite, Messages.getString( "EntryPropertyPage.NumberOfChildren" ), 1 ); //$NON-NLS-1$
        childrenText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 ); //$NON-NLS-1$
        GridData childrenTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        childrenTextGridData.widthHint = 300;
        childrenText.setLayoutData( childrenTextGridData );

        BaseWidgetUtils.createLabel( sizingComposite, Messages.getString( "EntryPropertyPage.NumberOfAttributes" ), 1 ); //$NON-NLS-1$
        attributesText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 ); //$NON-NLS-1$
        GridData attributesTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        attributesTextGridData.widthHint = 300;
        attributesText.setLayoutData( attributesTextGridData );

        BaseWidgetUtils.createLabel( sizingComposite, Messages.getString( "EntryPropertyPage.NumberOfValues" ), 1 ); //$NON-NLS-1$
        valuesText = BaseWidgetUtils.createLabeledText( sizingComposite, "", 2 ); //$NON-NLS-1$
        GridData valuesTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        valuesTextGridData.widthHint = 300;
        valuesText.setLayoutData( valuesTextGridData );

        includeOperationalAttributesButton = BaseWidgetUtils.createCheckbox( sizingComposite, Messages
            .getString( "EntryPropertyPage.IncludeoperationalAttributes" ), 2 ); //$NON-NLS-1$
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

        reloadEntryButton = BaseWidgetUtils.createButton( sizingComposite, "", 1 ); //$NON-NLS-1$
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

        entryUpdated( getEntry( getElement() ) );

        return composite;
    }


    /**
     * Reload operational attributes.
     */
    private void reloadOperationalAttributes()
    {
        IEntry entry = EntryPropertyPage.getEntry( getElement() );
        entry.setInitOperationalAttributes( true );
        InitializeAttributesRunnable runnable = new InitializeAttributesRunnable( entry );
        RunnableContextRunner.execute( runnable, null, true );
        entryUpdated( entry );
    }


    /**
     * Reload entry.
     */
    private void reloadEntry()
    {
        IEntry entry = EntryPropertyPage.getEntry( getElement() );
        entry.setInitOperationalAttributes( true );
        InitializeChildrenRunnable runnable1 = new InitializeChildrenRunnable( false, entry );
        InitializeAttributesRunnable runnable2 = new InitializeAttributesRunnable( entry );
        RunnableContextRunner.execute( runnable1, null, true );
        RunnableContextRunner.execute( runnable2, null, true );
        entryUpdated( entry );
    }


    /**
     * Gets the entry.
     * 
     * @param element the element
     * 
     * @return the entry
     */
    static IEntry getEntry( Object element )
    {
        IEntry entry = null;
        if ( element instanceof IAdaptable )
        {
            entry = ( IEntry ) ( ( IAdaptable ) element ).getAdapter( IEntry.class );
        }
        return entry;
    }


    /**
     * Checks if is disposed.
     * 
     * @return true, if is disposed
     */
    public boolean isDisposed()
    {
        return this.dnText.isDisposed();
    }


    /**
     * Gets the non-null string value.
     * 
     * @param att the attribute
     * 
     * @return the non-null string value
     */
    private String getNonNullStringValue( IAttribute att )
    {
        String value = null;
        if ( att != null )
        {
            value = att.getStringValue();
        }
        return value != null ? value : "-"; //$NON-NLS-1$
    }


    /**
     * Updates the text widgets if the entry was updated.
     * 
     * @param entry the entry
     */
    private void entryUpdated( IEntry entry )
    {

        if ( !this.dnText.isDisposed() )
        {
            setMessage( Messages.getString( "EntryPropertyPage.Entry" ) + entry.getDn().getUpName() ); //$NON-NLS-1$

            dnText.setText( entry.getDn().getUpName() );
            urlText.setText( entry.getUrl().toString() );
            ctText.setText( getNonNullStringValue( entry.getAttribute( SchemaConstants.CREATE_TIMESTAMP_AT ) ) );
            cnText.setText( getNonNullStringValue( entry.getAttribute( SchemaConstants.CREATORS_NAME_AT ) ) );
            mtText.setText( getNonNullStringValue( entry.getAttribute( SchemaConstants.MODIFY_TIMESTAMP_AT ) ) );
            mnText.setText( getNonNullStringValue( entry.getAttribute( SchemaConstants.MODIFIERS_NAME_AT ) ) );
            reloadCmiButton.setText( Messages.getString( "EntryPropertyPage.Refresh" ) ); //$NON-NLS-1$

            int attCount = 0;
            int valCount = 0;
            int bytes = 0;

            IAttribute[] allAttributes = entry.getAttributes();
            if ( allAttributes != null )
            {
                for ( int attIndex = 0; attIndex < allAttributes.length; attIndex++ )
                {
                    if ( !allAttributes[attIndex].isOperationalAttribute()
                        || includeOperationalAttributesButton.getSelection() )
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

            reloadEntryButton.setText( Messages.getString( "EntryPropertyPage.Refresh" ) ); //$NON-NLS-1$
            if ( !entry.isChildrenInitialized() )
            {
                childrenText.setText( Messages.getString( "EntryPropertyPage.NotChecked" ) ); //$NON-NLS-1$
            }
            else
            {
                childrenText.setText( ( entry.hasMoreChildren() ? NLS.bind( Messages
                    .getString( "EntryPropertyPage.ChildrenFetched" ), new Object[] { entry.getChildrenCount() } ) //$NON-NLS-1$
                    : Integer.toString( entry.getChildrenCount() ) ) );
            }
            attributesText.setText( "" + attCount ); //$NON-NLS-1$
            valuesText.setText( "" + valCount ); //$NON-NLS-1$
            sizeText.setText( Utils.formatBytes( bytes ) );
        }
    }

}
