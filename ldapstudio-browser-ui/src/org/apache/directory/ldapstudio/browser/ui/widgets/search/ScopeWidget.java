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


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


public class ScopeWidget extends BrowserWidget
{

    private int initialScope;

    private Group scopeGroup;

    private Button scopeObjectButton;

    private Button scopeOnelevelButton;

    private Button scopeSubtreeButton;


    public ScopeWidget( int initialScope )
    {
        this.initialScope = initialScope;
    }


    public ScopeWidget()
    {
        this.initialScope = 0;
    }


    public void createWidget( Composite parent )
    {

        // Search Scope
        scopeGroup = new Group( parent, SWT.NONE );
        scopeGroup.setText( "Scope" );
        scopeGroup.setLayout( new GridLayout( 1, false ) );
        scopeGroup.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        scopeObjectButton = new Button( scopeGroup, SWT.RADIO );
        scopeObjectButton.setText( "&Object" );
        scopeObjectButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );
        scopeOnelevelButton = new Button( scopeGroup, SWT.RADIO );
        scopeOnelevelButton.setText( "One &Level" );
        scopeOnelevelButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );
        scopeSubtreeButton = new Button( scopeGroup, SWT.RADIO );
        scopeSubtreeButton.setText( "&Subtree" );
        scopeSubtreeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setScope( this.initialScope );
    }


    public void setScope( int scope )
    {
        this.initialScope = scope;

        scopeObjectButton.setSelection( initialScope == ISearch.SCOPE_OBJECT );
        scopeOnelevelButton.setSelection( initialScope == ISearch.SCOPE_ONELEVEL );
        scopeSubtreeButton.setSelection( initialScope == ISearch.SCOPE_SUBTREE );

    }


    public int getScope()
    {
        int scope;

        if ( this.scopeSubtreeButton.getSelection() )
            scope = ISearch.SCOPE_SUBTREE;
        else if ( this.scopeOnelevelButton.getSelection() )
            scope = ISearch.SCOPE_ONELEVEL;
        else if ( this.scopeObjectButton.getSelection() )
            scope = ISearch.SCOPE_OBJECT;
        else
            scope = ISearch.SCOPE_ONELEVEL;

        return scope;
    }


    public void setEnabled( boolean b )
    {
        scopeGroup.setEnabled( b );
        scopeObjectButton.setEnabled( b );
        scopeOnelevelButton.setEnabled( b );
        scopeSubtreeButton.setEnabled( b );
    }

}
