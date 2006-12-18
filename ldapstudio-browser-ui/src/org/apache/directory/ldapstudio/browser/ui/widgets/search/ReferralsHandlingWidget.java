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


public class ReferralsHandlingWidget extends BrowserWidget
{

    private int initialReferralsHandlingMethod;

    private Group group;

    private Button ignoreButton;

    private Button followButton;


    public ReferralsHandlingWidget( int initialReferralsHandlingMethod )
    {
        this.initialReferralsHandlingMethod = initialReferralsHandlingMethod;
    }


    public ReferralsHandlingWidget()
    {
        this.initialReferralsHandlingMethod = IConnection.HANDLE_REFERRALS_IGNORE;
    }


    public void createWidget( Composite parent )
    {

        group = BaseWidgetUtils.createGroup( parent, "Referrals Handling", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        ignoreButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Ignore", 1 );
        ignoreButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        followButton = BaseWidgetUtils.createRadiobutton( groupComposite, "Follow", 1 );
        followButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        this.setReferralsHandlingMethod( this.initialReferralsHandlingMethod );
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.initialReferralsHandlingMethod = referralsHandlingMethod;
        ignoreButton.setSelection( initialReferralsHandlingMethod == IConnection.HANDLE_REFERRALS_IGNORE );
        followButton.setSelection( initialReferralsHandlingMethod == IConnection.HANDLE_REFERRALS_FOLLOW );
    }


    public int getReferralsHandlingMethod()
    {
        if ( this.ignoreButton.getSelection() )
        {
            return IConnection.HANDLE_REFERRALS_IGNORE;
        }
        else
        /* if(this.handleButton.getSelection()) */{
            return IConnection.HANDLE_REFERRALS_FOLLOW;
        }
    }


    public void setEnabled( boolean b )
    {
        this.group.setEnabled( b );
        this.ignoreButton.setEnabled( b );
        this.followButton.setEnabled( b );
    }

}
