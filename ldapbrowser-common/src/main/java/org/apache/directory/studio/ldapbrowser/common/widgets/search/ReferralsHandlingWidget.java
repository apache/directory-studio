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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * The ReferralsHandlingWidget could be used to select the
 * referrals handling method. It is composed of a group with 
 * two radio buttons.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReferralsHandlingWidget extends BrowserWidget
{

    /** The initial referrals handling method. */
    private Connection.ReferralHandlingMethod initialReferralsHandlingMethod;

    /** The group. */
    private Group group;

    /** The follow manually button. */
    private Button followManuallyButton;

    /** The follow automatically button. */
    private Button followAutomaticallyButton;

    /** The ignore button. */
    private Button ignoreButton;


    /**
     * Creates a new instance of ReferralsHandlingWidget with the given
     * referrals handling method.  
     * 
     * @param initialReferralsHandlingMethod the initial referrals handling method
     */
    public ReferralsHandlingWidget( Connection.ReferralHandlingMethod initialReferralsHandlingMethod )
    {
        this.initialReferralsHandlingMethod = initialReferralsHandlingMethod;
    }


    /**
     * Creates a new instance of ReferralsHandlingWidget with initial 
     * referrals handling method {@link Connection.ReferralHandlingMethod.FOLLOW}.
     */
    public ReferralsHandlingWidget()
    {
        this.initialReferralsHandlingMethod = Connection.ReferralHandlingMethod.FOLLOW;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent, boolean followManuallyVisible )
    {
        group = BaseWidgetUtils.createGroup( parent,
            Messages.getString( "ReferralsHandlingWidget.ReferralsHandling" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 1, 1 );

        if ( followManuallyVisible )
        {
            followManuallyButton = BaseWidgetUtils.createRadiobutton( groupComposite, Messages
                .getString( "ReferralsHandlingWidget.FollowManually" ), 1 ); //$NON-NLS-1$
            followManuallyButton.setToolTipText( Messages.getString( "ReferralsHandlingWidget.FollowManuallyTooltip" ) ); //$NON-NLS-1$
            followManuallyButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    notifyListeners();
                }
            } );
        }

        followAutomaticallyButton = BaseWidgetUtils.createRadiobutton( groupComposite, Messages
            .getString( "ReferralsHandlingWidget.FollowAutomatically" ), 1 ); //$NON-NLS-1$
        followAutomaticallyButton.setToolTipText( Messages
            .getString( "ReferralsHandlingWidget.FollowAutomaticallyTooltip" ) ); //$NON-NLS-1$
        followAutomaticallyButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        ignoreButton = BaseWidgetUtils.createRadiobutton( groupComposite, Messages
            .getString( "ReferralsHandlingWidget.Ignore" ), 1 ); //$NON-NLS-1$
        ignoreButton.setToolTipText( Messages.getString( "ReferralsHandlingWidget.IgnoreTooltip" ) ); //$NON-NLS-1$
        ignoreButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                notifyListeners();
            }
        } );

        setReferralsHandlingMethod( initialReferralsHandlingMethod );
    }


    /**
     * Sets the referrals handling method. 
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public void setReferralsHandlingMethod( Connection.ReferralHandlingMethod referralsHandlingMethod )
    {
        initialReferralsHandlingMethod = referralsHandlingMethod;
        if ( followManuallyButton == null && referralsHandlingMethod == ReferralHandlingMethod.FOLLOW_MANUALLY )
        {
            // fall-back to FOLLOW if manually button is invisible
            initialReferralsHandlingMethod = ReferralHandlingMethod.FOLLOW;
        }

        if ( followManuallyButton != null )
        {
            followManuallyButton
                .setSelection( initialReferralsHandlingMethod == Connection.ReferralHandlingMethod.FOLLOW_MANUALLY );
        }
        followAutomaticallyButton
            .setSelection( initialReferralsHandlingMethod == Connection.ReferralHandlingMethod.FOLLOW );
        ignoreButton.setSelection( initialReferralsHandlingMethod == Connection.ReferralHandlingMethod.IGNORE );
    }


    /**
     * Gets the referrals handling method.
     * 
     * @return the referrals handling method
     */
    public Connection.ReferralHandlingMethod getReferralsHandlingMethod()
    {
        if ( ignoreButton.getSelection() )
        {
            return Connection.ReferralHandlingMethod.IGNORE;
        }
        else if ( followAutomaticallyButton.getSelection() )
        {
            return Connection.ReferralHandlingMethod.FOLLOW;
        }
        else
        {
            return Connection.ReferralHandlingMethod.FOLLOW_MANUALLY;
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
        if ( followManuallyButton != null )
        {
            followManuallyButton.setEnabled( b );
        }
        followAutomaticallyButton.setEnabled( b );
        ignoreButton.setEnabled( b );
    }

}
