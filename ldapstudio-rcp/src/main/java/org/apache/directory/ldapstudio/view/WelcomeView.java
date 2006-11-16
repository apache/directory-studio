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

package org.apache.directory.ldapstudio.view;


import org.apache.directory.ldapstudio.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;


/**
 * This class defines the Welcome View.
 */

public class WelcomeView extends ViewPart
{
    public static final String ID = "org.apache.directory.ldapstudio.view.WelcomeView"; //$NON-NLS-1$
    private Label filler;
    private Label label;


    /**
     * The constructor.
     */
    public WelcomeView()
    {
        // Does Nothing
    }


    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl( Composite parent )
    {
        GridLayout layout = new GridLayout( 1, true );
        parent.setLayout( layout );
        filler = new Label( parent, SWT.CENTER );
        filler.setText( "" ); //$NON-NLS-1$
        filler.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
        label = new Label( parent, SWT.CENTER );
        label.setFont( new Font( null, "Georgia", 13, SWT.BOLD ) ); //$NON-NLS-1$
        label.setText( Messages.getString( "WelcomeView.Welcome_message" ) ); //$NON-NLS-1$
        label.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
    }


    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        // Does nothing
    }
}