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

package org.apache.directory.ldapstudio.browser.common.widgets.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.core.jobs.SearchJob;
import org.apache.directory.ldapstudio.browser.core.model.Control;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * The SearchPageWrapper is used to arrange all input elements of a
 * search page. It is used by the search page, the search properties page,
 * the batch operation wizard and the export wizards.
 * 
 * The style is used to specify the invisible and readonly elements. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchPageWrapper extends BrowserWidget
{

    /** The default style */
    public static final int NONE = 0;

    /** Style for invisible name field */
    public static final int NAME_INVISIBLE = 1 << 1;

    /** Style for read-only name field */
    public static final int NAME_READONLY = 1 << 2;

    /** Style for invisible connection field */
    public static final int CONNECTION_INVISIBLE = 1 << 3;

    /** Style for read-only connection field */
    public static final int CONNECTION_READONLY = 1 << 4;

    /** Style for invisible search base field */
    public static final int SEARCHBASE_INVISIBLE = 1 << 5;

    /** Style for read-only search base field */
    public static final int SEARCHBASE_READONLY = 1 << 6;

    /** Style for invisible filter field */
    public static final int FILTER_INVISIBLE = 1 << 7;

    /** Style for read-only filter field */
    public static final int FILTER_READONLY = 1 << 8;

    /** Style for invisible returning attributes field */
    public static final int RETURNINGATTRIBUTES_INVISIBLE = 1 << 9;

    /** Style for read-only returning attributes field */
    public static final int RETURNINGATTRIBUTES_READONLY = 1 << 10;

    /** Style for visible return DN checkbox */
    public static final int RETURN_DN_VISIBLE = 1 << 11;

    /** Style for checked return DN checkbox */
    public static final int RETURN_DN_CHECKED = 1 << 12;

    /** Style for visible return all attributes checkbox */
    public static final int RETURN_ALLATTRIBUTES_VISIBLE = 1 << 13;

    /** Style for checked return all attributes checkbox */
    public static final int RETURN_ALLATTRIBUTES_CHECKED = 1 << 14;

    /** Style for visible return operational attributes checkbox */
    public static final int RETURN_OPERATIONALATTRIBUTES_VISIBLE = 1 << 15;

    /** Style for checked return operational attributes checkbox */
    public static final int RETURN_OPERATIONALATTRIBUTES_CHECKED = 1 << 16;

    /** Style for invisible options */
    public static final int OPTIONS_INVISIBLE = 1 << 21;

    /** Style for read-only scope options */
    public static final int SCOPEOPTIONS_READONLY = 1 << 22;

    /** Style for read-only limit options */
    public static final int LIMITOPTIONS_READONLY = 1 << 23;

    /** Style for read-only alias options */
    public static final int ALIASOPTIONS_READONLY = 1 << 24;

    /** Style for read-only referrals options */
    public static final int REFERRALOPTIONS_READONLY = 1 << 25;

    /** Style for invisible controls fields */
    public static final int CONTROLS_INVISIBLE = 1 << 30;

    /** The style. */
    protected int style;

    /** The search name label. */
    protected Label searchNameLabel;

    /** The search name text. */
    protected Text searchNameText;

    /** The connection label. */
    protected Label connectionLabel;

    /** The connection widget. */
    protected ConnectionWidget connectionWidget;

    /** The search base label. */
    protected Label searchBaseLabel;

    /** The search base widget. */
    protected EntryWidget searchBaseWidget;

    /** The filter label. */
    protected Label filterLabel;

    /** The filter widget. */
    protected FilterWidget filterWidget;

    /** The returning attributes label. */
    protected Label returningAttributesLabel;

    /** The returning attributes widget. */
    protected ReturningAttributesWidget returningAttributesWidget;

    /** The return dn button. */
    protected Button returnDnButton;

    /** The return all attributes button. */
    protected Button returnAllAttributesButton;

    /** The return operational attributes button. */
    protected Button returnOperationalAttributesButton;

    /** The scope widget. */
    protected ScopeWidget scopeWidget;

    /** The limit widget. */
    protected LimitWidget limitWidget;

    /** The aliases dereferencing widget. */
    protected AliasesDereferencingWidget aliasesDereferencingWidget;

    /** The referrals handling widget. */
    protected ReferralsHandlingWidget referralsHandlingWidget;

    /** The control label. */
    protected Label controlLabel;

    /** The subentries control button. */
    protected Button subentriesControlButton;


    /**
     * Creates a new instance of SearchPageWrapper.
     * 
     * @param style the style
     */
    public SearchPageWrapper( int style )
    {
        this.style = style;
    }


    /**
     * Creates the contents.
     * 
     * @param composite the composite
     */
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


    /**
     * Checks if the given style is active.
     * 
     * @param requiredStyle the required style to check
     * 
     * @return true, if the required style is active
     */
    protected boolean isActive( int requiredStyle )
    {
        return ( style & requiredStyle ) != 0;
    }


    /**
     * Creates the search name line.
     * 
     * @param composite the composite
     */
    protected void createSearchNameLine( final Composite composite )
    {
        if ( isActive( NAME_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Creates the connection line.
     * 
     * @param composite the composite
     */
    protected void createConnectionLine( final Composite composite )
    {
        if ( isActive( CONNECTION_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Creates the search base line.
     * 
     * @param composite the composite
     */
    protected void createSearchBaseLine( final Composite composite )
    {
        if ( isActive( SEARCHBASE_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Creates the filter line.
     * 
     * @param composite the composite
     */
    protected void createFilterLine( final Composite composite )
    {
        if ( isActive( FILTER_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Creates the returning attributes line.
     * 
     * @param composite the composite
     */
    protected void createReturningAttributesLine( final Composite composite )
    {
        if ( isActive( RETURNINGATTRIBUTES_INVISIBLE ) )
        {
            return;
        }

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

        // special returning attributes options
        if ( isActive( RETURN_DN_VISIBLE ) || isActive( RETURN_ALLATTRIBUTES_VISIBLE )
            || isActive( RETURN_OPERATIONALATTRIBUTES_VISIBLE ) )
        {
            BaseWidgetUtils.createSpacer( composite, 1 );
            Composite buttonComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );
            if ( isActive( RETURN_DN_VISIBLE ) )
            {
                returnDnButton = BaseWidgetUtils.createCheckbox( buttonComposite, "Export DN", 1 );
                returnDnButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                returnDnButton.setSelection( isActive( RETURN_DN_CHECKED ) );
            }
            if ( isActive( RETURN_ALLATTRIBUTES_VISIBLE ) )
            {
                returnAllAttributesButton = BaseWidgetUtils.createCheckbox( buttonComposite, "All user attributes", 1 );
                returnAllAttributesButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                returnAllAttributesButton.setSelection( isActive( RETURN_ALLATTRIBUTES_CHECKED ) );
            }
            if ( isActive( RETURN_OPERATIONALATTRIBUTES_VISIBLE ) )
            {
                returnOperationalAttributesButton = BaseWidgetUtils.createCheckbox( buttonComposite,
                    "Operational attributes", 1 );
                returnOperationalAttributesButton.addSelectionListener( new SelectionAdapter()
                {
                    public void widgetSelected( SelectionEvent e )
                    {
                        validate();
                    }
                } );
                returnOperationalAttributesButton.setSelection( isActive( RETURN_OPERATIONALATTRIBUTES_CHECKED ) );
            }
        }

        BaseWidgetUtils.createSpacer( composite, 3 );
    }


    /**
     * Creates the options composite, this includes the
     * scope, limit, alias and referral widgets.
     * 
     * @param composite the composite
     */
    protected void createOptionsComposite( final Composite composite )
    {
        if ( isActive( OPTIONS_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Creates the control composite.
     * 
     * @param composite the composite
     */
    protected void createControlComposite( final Composite composite )
    {
        if ( isActive( CONTROLS_INVISIBLE ) )
        {
            return;
        }

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


    /**
     * Validates all elements.
     */
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

        super.notifyListeners();
    }


    /**
     * Checks if the DNs should be returned/exported.
     * 
     * @return true, if DNs should be returnde/exported
     */
    public boolean isReturnDn()
    {
        return returnDnButton != null && returnDnButton.getSelection();
    }


    /**
     * Initializes all search page widgets from the given search.
     * 
     * @param search the search
     */
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

            if ( scopeWidget != null )
            {
                scopeWidget.setScope( search.getScope() );
            }
            if ( limitWidget != null )
            {
                limitWidget.setCountLimit( search.getCountLimit() );
                limitWidget.setTimeLimit( search.getTimeLimit() );
            }
            if ( aliasesDereferencingWidget != null )
            {
                aliasesDereferencingWidget.setAliasesDereferencingMethod( search.getAliasesDereferencingMethod() );
            }
            if ( referralsHandlingWidget != null )
            {
                referralsHandlingWidget.setReferralsHandlingMethod( search.getReferralsHandlingMethod() );
            }
            if ( subentriesControlButton != null )
            {
                Control[] searchControls = search.getControls();
                if ( searchControls != null && searchControls.length > 0 )
                {
                    for ( int i = 0; i < searchControls.length; i++ )
                    {
                        Control c = searchControls[i];
                        if ( Control.SUBENTRIES_CONTROL.equals( c ) )
                        {
                            subentriesControlButton.setSelection( true );
                        }
                    }
                }
            }
        }
    }


    /**
     * Saves all search pages element to the given search.
     * 
     * @param search the search
     * 
     * @return true, if the given search has been modified.
     */
    public boolean saveToSearch( ISearch search )
    {
        boolean searchModified = false;

        if ( searchNameText != null && !searchNameText.getText().equals( search.getName() ) )
        {
            search.getSearchParameter().setName( searchNameText.getText() );
            searchModified = true;
        }
        if ( connectionWidget != null && connectionWidget.getConnection() != null
            && connectionWidget.getConnection() != search.getConnection() )
        {
            search.setConnection( connectionWidget.getConnection() );
            searchModified = true;
        }
        if ( searchBaseWidget != null && searchBaseWidget.getDn() != null
            && !searchBaseWidget.getDn().equals( search.getSearchBase() ) )
        {
            search.getSearchParameter().setSearchBase( searchBaseWidget.getDn() );
            searchModified = true;
            searchBaseWidget.saveDialogSettings();
        }
        if ( filterWidget != null )
        {
            if ( !filterWidget.getFilter().equals( search.getFilter() ) )
            {
                search.getSearchParameter().setFilter( filterWidget.getFilter() );
                searchModified = true;
            }
            filterWidget.saveDialogSettings();
        }

        if ( returningAttributesWidget != null )
        {
            if ( !Arrays.equals( returningAttributesWidget.getReturningAttributes(), search.getReturningAttributes() ) )
            {
                search.getSearchParameter().setReturningAttributes( returningAttributesWidget.getReturningAttributes() );
                searchModified = true;
            }
            returningAttributesWidget.saveDialogSettings();

            if ( returnAllAttributesButton != null || returnOperationalAttributesButton != null )
            {
                List<String> raList = new ArrayList<String>();
                raList.addAll( Arrays.asList( search.getReturningAttributes() ) );
                if ( returnAllAttributesButton != null )
                {
                    if ( returnAllAttributesButton.getSelection() )
                    {
                        raList.add( ISearch.ALL_USER_ATTRIBUTES );
                    }
                    if ( returnAllAttributesButton.getSelection() != isActive( RETURN_ALLATTRIBUTES_CHECKED ) )
                    {
                        searchModified = true;
                    }
                }
                if ( returnOperationalAttributesButton != null )
                {
                    if ( returnOperationalAttributesButton.getSelection() )
                    {
                        AttributeTypeDescription[] opAtds = SchemaUtils
                            .getOperationalAttributeDescriptions( connectionWidget.getConnection().getSchema() );
                        String[] attributeTypeDescriptionNames = SchemaUtils.getAttributeTypeDescriptionNames( opAtds );
                        raList.addAll( Arrays.asList( attributeTypeDescriptionNames ) );
                        raList.add( ISearch.ALL_OPERATIONAL_ATTRIBUTES );
                    }
                    if ( returnOperationalAttributesButton.getSelection() != isActive( RETURN_OPERATIONALATTRIBUTES_CHECKED ) )
                    {
                        searchModified = true;
                    }
                }
                String[] returningAttributes = raList.toArray( new String[raList.size()] );
                search.getSearchParameter().setReturningAttributes( returningAttributes );
            }
        }

        if ( scopeWidget != null )
        {
            int scope = scopeWidget.getScope();;
            if ( scope != search.getScope() )
            {
                search.getSearchParameter().setScope( scope );
                searchModified = true;
            }
        }
        if ( limitWidget != null )
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
        if ( aliasesDereferencingWidget != null )
        {
            int aliasesDereferencingMethod = aliasesDereferencingWidget.getAliasesDereferencingMethod();
            if ( aliasesDereferencingMethod != search.getAliasesDereferencingMethod() )
            {
                search.getSearchParameter().setAliasesDereferencingMethod( aliasesDereferencingMethod );
                searchModified = true;
            }
        }
        if ( referralsHandlingWidget != null )
        {
            int referralsHandlingMethod = referralsHandlingWidget.getReferralsHandlingMethod();
            if ( referralsHandlingMethod != search.getReferralsHandlingMethod() )
            {
                search.getSearchParameter().setReferralsHandlingMethod( referralsHandlingMethod );
                searchModified = true;
            }
        }
        if ( subentriesControlButton != null )
        {
            Control selectedSubControl = subentriesControlButton.getSelection() ? Control.SUBENTRIES_CONTROL : null;
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


    /**
     * Performs the search.
     * 
     * @param search the search
     * 
     * @return true, if perform search
     */
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


    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public boolean isValid()
    {
        if ( connectionWidget != null && connectionWidget.getConnection() == null )
        {
            return false;
        }
        if ( searchBaseWidget != null && searchBaseWidget.getDn() == null )
        {
            return false;
        }
        if ( searchNameText != null && "".equals( searchNameText.getText() ) )
        {
            return false;
        }
        if ( filterWidget != null && "".equals( filterWidget.getFilter() ) )
        {
            return false;
        }

        return true;
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        if ( searchNameText != null )
        {
            searchNameLabel.setEnabled( b );
            searchNameText.setEnabled( b );
        }
        if ( connectionWidget != null )
        {
            connectionLabel.setEnabled( b );
            connectionWidget.setEnabled( b && !isActive( CONNECTION_READONLY ) );
        }
        if ( searchBaseWidget != null )
        {
            searchBaseLabel.setEnabled( b );
            searchBaseWidget.setEnabled( b && !isActive( SEARCHBASE_READONLY ) );
        }
        if ( filterWidget != null )
        {
            filterLabel.setEnabled( b );
            filterWidget.setEnabled( b && !isActive( FILTER_READONLY ) );
        }
        if ( returningAttributesWidget != null )
        {
            returningAttributesLabel.setEnabled( b );
            returningAttributesWidget.setEnabled( b && !isActive( RETURNINGATTRIBUTES_READONLY ) );
        }
        if ( returnDnButton != null )
        {
            returnDnButton.setEnabled( b );
        }
        if ( returnAllAttributesButton != null )
        {
            returnAllAttributesButton.setEnabled( b );
        }
        if ( returnOperationalAttributesButton != null )
        {
            returnOperationalAttributesButton.setEnabled( b );
        }
        if ( scopeWidget != null )
        {
            scopeWidget.setEnabled( b && !isActive( SCOPEOPTIONS_READONLY ) );
        }
        if ( limitWidget != null )
        {
            limitWidget.setEnabled( b && !isActive( LIMITOPTIONS_READONLY ) );
        }
        if ( aliasesDereferencingWidget != null )
        {
            aliasesDereferencingWidget.setEnabled( b && !isActive( ALIASOPTIONS_READONLY ) );
        }
        if ( referralsHandlingWidget != null )
        {
            referralsHandlingWidget.setEnabled( b && !isActive( REFERRALOPTIONS_READONLY ) );
        }
        if ( controlLabel != null )
        {
            controlLabel.setEnabled( b );
            subentriesControlButton.setEnabled( b );
        }
    }

}
