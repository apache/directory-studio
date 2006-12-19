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


import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class LimitWidget extends BrowserWidget
{

    private int initialCountLimit;

    private int initialTimeLimit;

    private Group limitGroup;

    private Label countLimitLabel;

    private Text countLimitText;

    private Label timeLimitLabel;

    private Text timeLimitText;


    public LimitWidget( int initialCountLimit, int initialTimeLimit )
    {
        this.initialCountLimit = initialCountLimit;
        this.initialTimeLimit = initialTimeLimit;
    }


    public LimitWidget()
    {
        this.initialCountLimit = 0;
        this.initialTimeLimit = 0;
    }


    public void createWidget( Composite parent )
    {

        limitGroup = BaseWidgetUtils.createGroup( parent, "Limits", 1 );

        GridLayout gl = new GridLayout( 2, false );
        limitGroup.setLayout( gl );

        countLimitLabel = BaseWidgetUtils.createLabel( limitGroup, "&Count Limit:", 1 );
        countLimitText = BaseWidgetUtils.createText( limitGroup, "", 1 );
        countLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
            }
        } );
        countLimitText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        timeLimitLabel = BaseWidgetUtils.createLabel( limitGroup, "&Time Limit:", 1 );
        timeLimitText = BaseWidgetUtils.createText( limitGroup, "", 1 );
        timeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) )
                {
                    e.doit = false;
                }
            }
        } );
        timeLimitText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        this.setCountLimit( this.initialCountLimit );
        this.setTimeLimit( this.initialTimeLimit );
    }


    public void setCountLimit( int countLimit )
    {
        this.initialCountLimit = countLimit;
        this.countLimitText.setText( "" + this.initialCountLimit );
    }


    public void setTimeLimit( int timeLimit )
    {
        this.initialTimeLimit = timeLimit;
        this.timeLimitText.setText( "" + this.initialTimeLimit );
    }


    public int getCountLimit()
    {
        int countLimit;
        try
        {
            countLimit = new Integer( this.countLimitText.getText() ).intValue();
        }
        catch ( NumberFormatException e )
        {
            countLimit = 0;
        }
        return countLimit;
    }


    public int getTimeLimit()
    {
        int timeLimit;
        try
        {
            timeLimit = new Integer( this.timeLimitText.getText() ).intValue();
        }
        catch ( NumberFormatException e )
        {
            timeLimit = 0;
        }
        return timeLimit;
    }


    public void setEnabled( boolean b )
    {
        limitGroup.setEnabled( b );
        countLimitLabel.setEnabled( b );
        countLimitText.setEnabled( b );
        timeLimitLabel.setEnabled( b );
        timeLimitText.setEnabled( b );
    }

}
