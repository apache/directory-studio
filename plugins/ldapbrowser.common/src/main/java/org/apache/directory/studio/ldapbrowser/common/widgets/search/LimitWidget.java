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
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * The LimitWidget could be used to select the limits of a connection
 * or search. It is composed of a group with text input fields.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LimitWidget extends BrowserWidget
{

    /** The initial count limit. */
    private int initialCountLimit;

    /** The initial time limit. */
    private int initialTimeLimit;

    /** The limit group. */
    private Group limitGroup;

    /** The count limit label. */
    private Label countLimitLabel;

    /** The count limit text. */
    private Text countLimitText;

    /** The time limit label. */
    private Label timeLimitLabel;

    /** The time limit text. */
    private Text timeLimitText;


    /**
     * Creates a new instance of LimitWidget.
     * 
     * @param initialTimeLimit the initial time limit
     * @param initialCountLimit the initial count limit
     */
    public LimitWidget( int initialCountLimit, int initialTimeLimit )
    {
        this.initialCountLimit = initialCountLimit;
        this.initialTimeLimit = initialTimeLimit;
    }


    /**
     * Creates a new instance of LimitWidget with no limits.
     */
    public LimitWidget()
    {
        this.initialCountLimit = 0;
        this.initialTimeLimit = 0;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {

        limitGroup = BaseWidgetUtils.createGroup( parent, Messages.getString( "LimitWidget.Limits" ), 1 ); //$NON-NLS-1$
        GridLayout gl = new GridLayout( 2, false );
        limitGroup.setLayout( gl );

        // Count limit
        String countLimitToolTipText = Messages.getString( "LimitWidget.CountLimitTooltip" ); //$NON-NLS-1$
        countLimitLabel = BaseWidgetUtils.createLabel( limitGroup, Messages.getString( "LimitWidget.CountLimit" ), 1 ); //$NON-NLS-1$
        countLimitLabel.setToolTipText( countLimitToolTipText );
        countLimitText = BaseWidgetUtils.createText( limitGroup, "", 1 ); //$NON-NLS-1$
        countLimitText.setToolTipText( countLimitToolTipText );
        countLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
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

        // Time limit
        String timeLimitToolTipText = Messages.getString( "LimitWidget.TimeLimitToolTip" ); //$NON-NLS-1$
        timeLimitLabel = BaseWidgetUtils.createLabel( limitGroup, Messages.getString( "LimitWidget.TimeLimit" ), 1 ); //$NON-NLS-1$
        timeLimitLabel.setToolTipText( timeLimitToolTipText );
        timeLimitText = BaseWidgetUtils.createText( limitGroup, "", 1 ); //$NON-NLS-1$
        timeLimitText.setToolTipText( timeLimitToolTipText );
        timeLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
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

        setCountLimit( initialCountLimit );
        setTimeLimit( initialTimeLimit );
    }


    /**
     * Sets the count limit.
     * 
     * @param countLimit the count limit
     */
    public void setCountLimit( int countLimit )
    {
        initialCountLimit = countLimit;
        countLimitText.setText( Integer.toString( initialCountLimit ) );
    }


    /**
     * Sets the time limit.
     * 
     * @param timeLimit the time limit
     */
    public void setTimeLimit( int timeLimit )
    {
        initialTimeLimit = timeLimit;
        timeLimitText.setText( Integer.toString( initialTimeLimit ) );
    }


    /**
     * Gets the count limit.
     * 
     * @return the count limit
     */
    public int getCountLimit()
    {
        int countLimit;
        try
        {
            countLimit = new Integer( countLimitText.getText() ).intValue();
        }
        catch ( NumberFormatException e )
        {
            countLimit = 0;
        }
        return countLimit;
    }


    /**
     * Gets the time limit.
     * 
     * @return the time limit
     */
    public int getTimeLimit()
    {
        int timeLimit;
        try
        {
            timeLimit = new Integer( timeLimitText.getText() ).intValue();
        }
        catch ( NumberFormatException e )
        {
            timeLimit = 0;
        }
        return timeLimit;
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        limitGroup.setEnabled( b );
        countLimitLabel.setEnabled( b );
        countLimitText.setEnabled( b );
        timeLimitLabel.setEnabled( b );
        timeLimitText.setEnabled( b );
    }

}
