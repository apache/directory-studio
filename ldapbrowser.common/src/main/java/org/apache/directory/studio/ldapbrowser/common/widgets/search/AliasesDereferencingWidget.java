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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The AliasesDereferencingWidget could be used to select the
 * alias dereferencing method. It is composed of a group with 
 * two check boxes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AliasesDereferencingWidget extends BrowserWidget
{

    /** The initial aliases dereferencing method */
    private Connection.AliasDereferencingMethod initialAliasesDereferencingMethod;

    /** The group. */
    private Group group;

    /** The finding button. */
    private Button findingButton;

    /** The search button. */
    private Button searchButton;


    /**
     * Creates a new instance of AliasesDereferencingWidget with the given
     * dereferencing method.
     * 
     * @param initialAliasesDereferencingMethod the initial aliases dereferencing method
     */
    public AliasesDereferencingWidget( Connection.AliasDereferencingMethod initialAliasesDereferencingMethod )
    {
        this.initialAliasesDereferencingMethod = initialAliasesDereferencingMethod;
    }


    /**
     * Creates a new instance of AliasesDereferencingWidget. The initial 
     * dereferencing method is set to {@link Connection.AliasDereferencingMethod.ALWAYS}.
     */
    public AliasesDereferencingWidget()
    {
        this.initialAliasesDereferencingMethod = Connection.AliasDereferencingMethod.ALWAYS;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        group = BaseWidgetUtils.createGroup( parent, Messages
            .getString( "AliasesDereferencingWidget.AliasesDereferencing" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        findingButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "AliasesDereferencingWidget.FindingBaseDN" ), 1 ); //$NON-NLS-1$
        findingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        searchButton = BaseWidgetUtils.createCheckbox( groupComposite, Messages
            .getString( "AliasesDereferencingWidget.Search" ), 1 ); //$NON-NLS-1$
        searchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setAliasesDereferencingMethod( initialAliasesDereferencingMethod );
    }


    /**
     * Sets the aliases dereferencing method.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public void setAliasesDereferencingMethod( Connection.AliasDereferencingMethod aliasesDereferencingMethod )
    {
        initialAliasesDereferencingMethod = aliasesDereferencingMethod;
        findingButton.setSelection( initialAliasesDereferencingMethod == Connection.AliasDereferencingMethod.FINDING
            || initialAliasesDereferencingMethod == Connection.AliasDereferencingMethod.ALWAYS );
        searchButton.setSelection( initialAliasesDereferencingMethod == Connection.AliasDereferencingMethod.SEARCH
            || initialAliasesDereferencingMethod == Connection.AliasDereferencingMethod.ALWAYS );
    }


    /**
     * Gets the aliases dereferencing method.
     * 
     * @return the aliases dereferencing method
     */
    public Connection.AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        if ( findingButton.getSelection() && searchButton.getSelection() )
        {
            return Connection.AliasDereferencingMethod.ALWAYS;
        }
        else if ( findingButton.getSelection() )
        {
            return Connection.AliasDereferencingMethod.FINDING;
        }
        else if ( searchButton.getSelection() )
        {
            return Connection.AliasDereferencingMethod.SEARCH;
        }
        else
        {
            return Connection.AliasDereferencingMethod.NEVER;
        }
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        group.setEnabled( b );
        findingButton.setEnabled( b );
        searchButton.setEnabled( b );
    }

}
