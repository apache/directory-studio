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

package org.apache.directory.ldapstudio.browser.ui.widgets.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.jobs.SearchJob;
import org.apache.directory.ldapstudio.browser.core.model.Control;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class SearchPageWrapper extends BrowserWidget
{

    public static final int NONE = 0;

    public static final int NAME_INVISIBLE = 1 << 1;

    public static final int NAME_READONLY = 1 << 2;

    public static final int CONNECTION_INVISIBLE = 1 << 3;

    public static final int CONNECTION_READONLY = 1 << 4;

    public static final int SEARCHBASE_INVISIBLE = 1 << 5;

    public static final int SEARCHBASE_READONLY = 1 << 6;

    public static final int FILTER_INVISIBLE = 1 << 7;

    public static final int FILTER_READONLY = 1 << 8;

    public static final int RETURNINGATTRIBUTES_INVISIBLE = 1 << 9;

    public static final int RETURNINGATTRIBUTES_READONLY = 1 << 10;

    public static final int DN_VISIBLE = 1 << 11;

    public static final int DN_CHECKED = 1 << 12;

    public static final int ALLATTRIBUTES_VISIBLE = 1 << 13;

    public static final int ALLATTRIBUTES_CHECKED = 1 << 14;

    public static final int OPERATIONALATTRIBUTES_VISIBLE = 1 << 15;

    public static final int OPERATIONALATTRIBUTES_CHECKED = 1 << 16;

    public static final int OPTIONS_INVISIBLE = 1 << 21;

    public static final int SCOPEOPTIONS_READONLY = 1 << 22;

    public static final int LIMITOPTIONS_READONLY = 1 << 23;

    public static final int ALIASOPTIONS_READONLY = 1 << 24;

    public static final int REFERRALOPTIONS_READONLY = 1 << 25;

    public static final int CONTROLS_INVISIBLE = 1 << 30;

    protected int style;

    protected LdapFilterParser parser;

    protected Label searchNameLabel;

    protected Text searchNameText;

    protected Label connectionLabel;

    protected ConnectionWidget connectionWidget;

    protected Label searchBaseLabel;

    protected EntryWidget searchBaseWidget;

    protected Label filterLabel;

    protected FilterWidget filterWidget;

    protected Label returningAttributesLabel;

    protected ReturningAttributesWidget returningAttributesWidget;

    protected Button dnButton;

    protected Button allAttributesButton;

    protected Button operationalAttributesButton;

    protected ScopeWidget scopeWidget;

    protected LimitWidget limitWidget;

    protected AliasesDereferencingWidget aliasesDereferencingWidget;

    protected ReferralsHandlingWidget referralsHandlingWidget;

    protected Label controlLabel;

    protected Button subentriesControlButton;


    public SearchPageWrapper( int style )
    {
        this.parser = new LdapFilterParser();
        this.style = style;
    }


    public void createContents( final Composite composite )
    {

        // Search Name
        createSearchNameLine( composite );

        // Connection
        createConnectionLine( composite );

        // Search Base
        createSearchBaseLine( composite );

        // Filter
        createFilterLine( composite );

        // Returning Attributes
        createReturningAttributesLine( composite );

        // control
        createControlComposite( composite );

        // scope, limit, alias, referral
        createOptionsComposite( composite );

    }


    protected boolean isActive( int requiredStyle )
    {
        return ( this.style & requiredStyle ) != 0;
    }


    protected void createSearchNameLine( final Composite composite )
    {
        if ( isActive( NAME_INVISIBLE ) )
            return;

        searchNameLabel = BaseWidgetUtils.createLabel( composite, "Search Name:", 1 );
        if ( isActive( NAME_READONLY ) )
        {
            searchNameText = BaseWidgetUtils.createReadonlyText( composite, "", 2 );
        }
        else
        {
            searchNameText = BaseWidgetUtils.createText( composite, "", 2 );
        }
        searchNameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    protected void createConnectionLine( final Composite composite )
    {
        if ( isActive( CONNECTION_INVISIBLE ) )
            return;

        connectionLabel = BaseWidgetUtils.createLabel( composite, "Connection:", 1 );
        connectionWidget = new ConnectionWidget();
        connectionWidget.createWidget( composite );
        connectionWidget.setEnabled( !isActive( CONNECTION_READONLY ) );
        connectionWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    protected void createSearchBaseLine( final Composite composite )
    {
        if ( isActive( SEARCHBASE_INVISIBLE ) )
            return;

        searchBaseLabel = BaseWidgetUtils.createLabel( composite, "Search Base:", 1 );
        searchBaseWidget = new EntryWidget();
        searchBaseWidget.createWidget( composite );
        searchBaseWidget.setEnabled( !isActive( SEARCHBASE_READONLY ) );
        searchBaseWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    protected void createFilterLine( final Composite composite )
    {
        if ( isActive( FILTER_INVISIBLE ) )
            return;

        filterLabel = BaseWidgetUtils.createLabel( composite, "Filter:", 1 );
        filterWidget = new FilterWidget();
        filterWidget.createWidget( composite );
        filterWidget.setEnabled( !isActive( FILTER_READONLY ) );
        filterWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    protected void createReturningAttributesLine( final Composite composite )
    {
        if ( isActive( RETURNINGATTRIBUTES_INVISIBLE ) )
            return;

        BaseWidgetUtils.createLabel( composite, "Returning Attributes:", 1 );
        Composite retComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 2 );
        returningAttributesWidget = new ReturningAttributesWidget();
        returningAttributesWidget.createWidget( retComposite );
        returningAttributesWidget.setEnabled( !isActive( RETURNINGATTRIBUTES_READONLY ) );
        returningAttributesWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        if ( isActive( DN_VISIBLE ) || isActive( ALLATTRIBUTES_VISIBLE ) || isActive( OPERATIONALATTRIBUTES_VISIBLE ) )
        {
            BaseWidgetUtils.createSpacer( composite, 1 );
            Composite buttonComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );
            if ( isActive( DN_VISIBLE ) )
            {
                dnButton = BaseWidgetUtils.createCheckbox( buttonComposite, "Export DN", 1 );
                dnButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                dnButton.setSelection( isActive( DN_CHECKED ) );
            }
            if ( isActive( ALLATTRIBUTES_VISIBLE ) )
            {
                allAttributesButton = BaseWidgetUtils.createCheckbox( buttonComposite, "All user attributes", 1 );
                allAttributesButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                allAttributesButton.setSelection( isActive( ALLATTRIBUTES_CHECKED ) );
            }
            if ( isActive( OPERATIONALATTRIBUTES_VISIBLE ) )
            {
                operationalAttributesButton = BaseWidgetUtils.createCheckbox( buttonComposite,
                    "Operational attributes", 1 );
                operationalAttributesButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                operationalAttributesButton.setSelection( isActive( OPERATIONALATTRIBUTES_CHECKED ) );
            }
        }

        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    protected void createOptionsComposite( final Composite composite )
    {
        if ( isActive( OPTIONS_INVISIBLE ) )
            return;

        Composite optionsComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 3 );
        scopeWidget = new ScopeWidget();
        scopeWidget.createWidget( optionsComposite );
        scopeWidget.setEnabled( !isActive( SCOPEOPTIONS_READONLY ) );
        scopeWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        limitWidget = new LimitWidget();
        limitWidget.createWidget( optionsComposite );
        limitWidget.setEnabled( !isActive( LIMITOPTIONS_READONLY ) );
        limitWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        aliasesDereferencingWidget = new AliasesDereferencingWidget();
        aliasesDereferencingWidget.createWidget( optionsComposite );
        aliasesDereferencingWidget.setEnabled( !isActive( ALIASOPTIONS_READONLY ) );
        aliasesDereferencingWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
        referralsHandlingWidget = new ReferralsHandlingWidget();
        referralsHandlingWidget.createWidget( optionsComposite );
        referralsHandlingWidget.setEnabled( !isActive( REFERRALOPTIONS_READONLY ) );
        referralsHandlingWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );
    }


    protected void createControlComposite( final Composite composite )
    {
        if ( isActive( CONTROLS_INVISIBLE ) )
            return;

        controlLabel = BaseWidgetUtils.createLabel( composite, "Controls:", 1 );

        subentriesControlButton = BaseWidgetUtils.createCheckbox( composite, Control.SUBENTRIES_CONTROL.getName(), 2 );
        subentriesControlButton.addSelectionListener( new SelectionListener()
        {
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }


            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

    }


    protected void validate()
    {

        if ( connectionWidget.getConnection() != null )
        {
            if ( searchBaseWidget.getDn() == null
                || searchBaseWidget.getConnection() != connectionWidget.getConnection() )
            {
                searchBaseWidget.setInput( connectionWidget.getConnection(), null );
            }
        }

        filterWidget.setConnection( connectionWidget.getConnection() );

        // this.fireSearchPageModified();
        super.notifyListeners();
    }


    public boolean isExportDn()
    {
        return dnButton != null && dnButton.getSelection();
    }


    public void loadFromSearch( ISearch search )
    {

        if ( searchNameText != null )
        {
            searchNameText.setText( search.getName() );
        }

        if ( search.getConnection() != null )
        {
            IConnection connection = search.getConnection();
            DN searchBase = search.getSearchBase();

            if ( connectionWidget != null )
            {
                connectionWidget.setConnection( connection );
            }

            if ( searchBase != null )
            {
                searchBaseWidget.setInput( connection, searchBase );
            }

            if ( filterWidget != null )
            {
                filterWidget.setConnection( connection );
                filterWidget.setFilter( search.getFilter() );
            }

            if ( returningAttributesWidget != null )
            {
                returningAttributesWidget.setConnection( connection );
                returningAttributesWidget.setInitialReturningAttributes( search.getReturningAttributes() );
            }

            if ( this.scopeWidget != null )
            {
                scopeWidget.setScope( search.getScope() );
            }
            if ( this.limitWidget != null )
            {
                limitWidget.setCountLimit( search.getCountLimit() );
                limitWidget.setTimeLimit( search.getTimeLimit() );
            }
            if ( this.aliasesDereferencingWidget != null )
            {
                aliasesDereferencingWidget.setAliasesDereferencingMethod( search.getAliasesDereferencingMethod() );
            }
            if ( this.referralsHandlingWidget != null )
            {
                referralsHandlingWidget.setReferralsHandlingMethod( search.getReferralsHandlingMethod() );
            }
            if ( this.subentriesControlButton != null )
            {
                Control[] searchControls = search.getControls();
                if ( searchControls != null && searchControls.length > 0 )
                {
                    for ( int i = 0; i < searchControls.length; i++ )
                    {
                        Control c = searchControls[i];
                        if ( Control.SUBENTRIES_CONTROL.equals( c ) )
                        {
                            this.subentriesControlButton.setSelection( true );
                        }
                    }

                }
            }
        }
    }


    public boolean saveToSearch( ISearch search )
    {
        boolean searchModified = false;

        if ( this.searchNameText != null && !this.searchNameText.getText().equals( search.getName() ) )
        {
            search.getSearchParameter().setName( this.searchNameText.getText() );
            searchModified = true;
        }
        if ( this.connectionWidget != null && this.connectionWidget.getConnection() != null
            && this.connectionWidget.getConnection() != search.getConnection() )
        {
            search.setConnection( this.connectionWidget.getConnection() );
            searchModified = true;
        }
        if ( this.searchBaseWidget != null && this.searchBaseWidget.getDn() != null
            && !this.searchBaseWidget.getDn().equals( search.getSearchBase() ) )
        {
            search.getSearchParameter().setSearchBase( this.searchBaseWidget.getDn() );
            searchModified = true;
            this.searchBaseWidget.saveDialogSettings();
        }
        if ( this.filterWidget != null )
        {
            this.parser.parse( filterWidget.getFilter() );
            if ( !this.parser.getModel().toString().equals( search.getFilter() ) )
            {
                search.getSearchParameter().setFilter( this.parser.getModel().toString() );
                searchModified = true;
            }
            this.filterWidget.saveDialogSettings();
        }

        if ( returningAttributesWidget != null )
        {
            if ( !Arrays.equals( this.returningAttributesWidget.getReturningAttributes(), search
                .getReturningAttributes() ) )
            {
                search.getSearchParameter().setReturningAttributes(
                    this.returningAttributesWidget.getReturningAttributes() );
                searchModified = true;
            }
            this.returningAttributesWidget.saveDialogSettings();

            if ( allAttributesButton != null || operationalAttributesButton != null )
            {
                List raList = new ArrayList();
                raList.addAll( Arrays.asList( search.getReturningAttributes() ) );
                if ( allAttributesButton != null )
                {
                    if ( allAttributesButton.getSelection() )
                    {
                        raList.add( ISearch.ALL_USER_ATTRIBUTES );
                    }
                    if ( allAttributesButton.getSelection() != isActive( ALLATTRIBUTES_CHECKED ) )
                    {
                        searchModified = true;
                    }
                }
                if ( operationalAttributesButton != null )
                {
                    if ( operationalAttributesButton.getSelection() )
                    {
                        AttributeTypeDescription[] opAtds = SchemaUtils
                            .getOperationalAttributeDescriptions( connectionWidget.getConnection().getSchema() );
                        String[] attributeTypeDescriptionNames = SchemaUtils.getAttributeTypeDescriptionNames( opAtds );
                        raList.addAll( Arrays.asList( attributeTypeDescriptionNames ) );
                        raList.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
                    }
                    if ( operationalAttributesButton.getSelection() != isActive( OPERATIONALATTRIBUTES_CHECKED ) )
                    {
                        searchModified = true;
                    }
                }
                String[] returningAttributes = ( String[] ) raList.toArray( new String[raList.size()] );
                search.getSearchParameter().setReturningAttributes( returningAttributes );
            }
        }

        if ( this.scopeWidget != null )
        {
            int scope = scopeWidget.getScope();;
            if ( scope != search.getScope() )
            {
                search.getSearchParameter().setScope( scope );
                searchModified = true;
            }
        }
        if ( this.limitWidget != null )
        {
            int countLimit = limitWidget.getCountLimit();
            int timeLimit = limitWidget.getTimeLimit();
            if ( countLimit != search.getCountLimit() )
            {
                search.getSearchParameter().setCountLimit( countLimit );
                searchModified = true;
            }
            if ( timeLimit != search.getTimeLimit() )
            {
                search.getSearchParameter().setTimeLimit( timeLimit );
                searchModified = true;
            }
        }
        if ( this.aliasesDereferencingWidget != null )
        {
            int aliasesDereferencingMethod = aliasesDereferencingWidget.getAliasesDereferencingMethod();
            if ( aliasesDereferencingMethod != search.getAliasesDereferencingMethod() )
            {
                search.getSearchParameter().setAliasesDereferencingMethod( aliasesDereferencingMethod );
                searchModified = true;
            }
        }
        if ( this.referralsHandlingWidget != null )
        {
            int referralsHandlingMethod = referralsHandlingWidget.getReferralsHandlingMethod();
            if ( referralsHandlingMethod != search.getReferralsHandlingMethod() )
            {
                search.getSearchParameter().setReferralsHandlingMethod( referralsHandlingMethod );
                searchModified = true;
            }
        }
        if ( this.subentriesControlButton != null )
        {
            Control selectedSubControl = this.subentriesControlButton.getSelection() ? Control.SUBENTRIES_CONTROL
                : null;
            Control searchSubentriesControl = null;
            Control[] searchControls = search.getControls();
            if ( searchControls != null && searchControls.length > 0 )
            {
                for ( int i = 0; i < searchControls.length; i++ )
                {
                    Control c = searchControls[i];
                    if ( Control.SUBENTRIES_CONTROL.equals( c ) )
                    {
                        searchSubentriesControl = Control.SUBENTRIES_CONTROL;
                        break;
                    }
                }
            }
            if ( selectedSubControl != searchSubentriesControl )
            {
                if ( selectedSubControl == null )
                {
                    search.getSearchParameter().setControls( null );
                }
                else
                {
                    search.getSearchParameter().setControls( new Control[]
                        { selectedSubControl } );
                }
                searchModified = true;
            }

        }

        return searchModified;
    }


    public boolean performSearch( final ISearch search )
    {
        if ( search.getConnection() != null )
        {
            new SearchJob( new ISearch[]
                { search } ).execute();
            return true;
        }
        else
        {
            return false;
        }
    }


    public boolean isValid()
    {
        if ( this.connectionWidget != null && this.connectionWidget.getConnection() == null )
        {
            return false;
        }
        if ( this.searchBaseWidget != null && this.searchBaseWidget.getDn() == null )
        {
            return false;
        }
        if ( this.searchNameText != null && "".equals( this.searchNameText.getText() ) )
        {
            return false;
        }
        if ( this.filterWidget != null && "".equals( this.filterWidget.getFilter() ) )
        {
            return false;
        }

        return true;
    }


    public void setEnabled( boolean b )
    {
        if ( this.searchNameText != null )
        {
            this.searchNameLabel.setEnabled( b );
            this.searchNameText.setEnabled( b );
        }
        if ( this.connectionWidget != null )
        {
            this.connectionLabel.setEnabled( b );
            this.connectionWidget.setEnabled( b && !isActive( CONNECTION_READONLY ) );
        }
        if ( this.searchBaseWidget != null )
        {
            this.searchBaseLabel.setEnabled( b );
            this.searchBaseWidget.setEnabled( b && !isActive( SEARCHBASE_READONLY ) );
        }
        if ( this.filterWidget != null )
        {
            this.filterLabel.setEnabled( b );
            this.filterWidget.setEnabled( b && !isActive( FILTER_READONLY ) );
        }
        if ( this.returningAttributesWidget != null )
        {
            this.returningAttributesLabel.setEnabled( b );
            this.returningAttributesWidget.setEnabled( b && !isActive( RETURNINGATTRIBUTES_READONLY ) );
        }
        if ( this.dnButton != null )
        {
            this.dnButton.setEnabled( b );
        }
        if ( this.allAttributesButton != null )
        {
            this.allAttributesButton.setEnabled( b );
        }
        if ( this.operationalAttributesButton != null )
        {
            this.operationalAttributesButton.setEnabled( b );
        }
        if ( this.scopeWidget != null )
        {
            this.scopeWidget.setEnabled( b && !isActive( SCOPEOPTIONS_READONLY ) );
        }
        if ( this.limitWidget != null )
        {
            this.limitWidget.setEnabled( b && !isActive( LIMITOPTIONS_READONLY ) );
        }
        if ( this.aliasesDereferencingWidget != null )
        {
            this.aliasesDereferencingWidget.setEnabled( b && !isActive( ALIASOPTIONS_READONLY ) );
        }
        if ( this.referralsHandlingWidget != null )
        {
            this.referralsHandlingWidget.setEnabled( b && !isActive( REFERRALOPTIONS_READONLY ) );
        }
        if ( this.controlLabel != null )
        {
            this.controlLabel.setEnabled( b );
            this.subentriesControlButton.setEnabled( b );
        }

    }

}
