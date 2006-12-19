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


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


public class AliasesDereferencingWidget extends BrowserWidget
{

    private int initialAliasesDereferencingMethod;

    private Group group;

    private Button findingButton;

    private Button searchButton;


    public AliasesDereferencingWidget( int initialAliasesDereferencingMethod )
    {
        this.initialAliasesDereferencingMethod = initialAliasesDereferencingMethod;
    }


    public AliasesDereferencingWidget()
    {
        this.initialAliasesDereferencingMethod = IConnection.DEREFERENCE_ALIASES_NEVER;
    }


    public void createWidget( Composite parent )
    {

        group = BaseWidgetUtils.createGroup( parent, "Aliases Dereferencing", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        findingButton = BaseWidgetUtils.createCheckbox( groupComposite, "Finding Base DN", 1 );
        findingButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        searchButton = BaseWidgetUtils.createCheckbox( groupComposite, "Search", 1 );
        searchButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        this.setAliasesDereferencingMethod( this.initialAliasesDereferencingMethod );
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.initialAliasesDereferencingMethod = aliasesDereferencingMethod;
        findingButton.setSelection( initialAliasesDereferencingMethod == IConnection.DEREFERENCE_ALIASES_FINDING
            || initialAliasesDereferencingMethod == IConnection.DEREFERENCE_ALIASES_ALWAYS );
        searchButton.setSelection( initialAliasesDereferencingMethod == IConnection.DEREFERENCE_ALIASES_SEARCH
            || initialAliasesDereferencingMethod == IConnection.DEREFERENCE_ALIASES_ALWAYS );
    }


    public int getAliasesDereferencingMethod()
    {
        if ( this.findingButton.getSelection() && this.searchButton.getSelection() )
        {
            return IConnection.DEREFERENCE_ALIASES_ALWAYS;
        }
        else if ( this.findingButton.getSelection() )
        {
            return IConnection.DEREFERENCE_ALIASES_FINDING;
        }
        else if ( this.searchButton.getSelection() )
        {
            return IConnection.DEREFERENCE_ALIASES_SEARCH;
        }
        else
        {
            return IConnection.DEREFERENCE_ALIASES_NEVER;
        }
    }


    public void setEnabled( boolean b )
    {
        this.group.setEnabled( b );
        this.findingButton.setEnabled( b );
        this.searchButton.setEnabled( b );
    }

}
