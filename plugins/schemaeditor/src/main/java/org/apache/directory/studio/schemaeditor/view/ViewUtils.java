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
package org.apache.directory.studio.schemaeditor.view;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;


/**
 * This Helper Class contains useful methods used to create the UI.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ViewUtils
{
    /** The Black Color */
    public static final Color COLOR_BLACK = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
        .getDisplay().getSystemColor( SWT.COLOR_BLACK );

    /** The Red Color */
    public static final Color COLOR_RED = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
        .getSystemColor( SWT.COLOR_RED );


    /**
     * Concatenates all aliases in a String format. Aliases are separated with a comma (',')
     *
     * @param aliases
     *      the aliases to concatenate
     * @return
     *      a String representing all aliases
     */
    public static String concateAliases( List<String> aliases )
    {
        StringBuffer sb = new StringBuffer();
        if ( aliases.size() > 0 )
        {
            sb.append( aliases.get( 0 ) );
            for ( int i = 1; i < aliases.size(); i++ )
            {
                sb.append( ", " ); //$NON-NLS-1$
                sb.append( aliases.get( i ) );
            }
        }

        return sb.toString();
    }


    /**
     * Verifies that the given name is syntaxely correct according to the RFC 2252 
     * (Lightweight Directory Access Protocol (v3): Attribute Syntax Definitions).
     *
     * @param name
     *      the name to test
     * @return
     *      true if the name is correct, false if the name is not correct.
     */
    public static boolean verifyName( String name )
    {
        return name.matches( Messages.getString( "ViewUtils.AllowedCharacters" ) ); //$NON-NLS-1$
    }


    /**
     * Displays an Error Message Box with the given title and message.
     *
     * @param title
     *      the title of the window
     * @param message
     *      the message to display
     * @return
     *      the ID of the button that was selected to dismiss 
     *      the message box (e.g. SWT.OK, SWT.CANCEL, etc...)
     */
    public static int displayErrorMessageBox( String title, String message )
    {
        return displayMessageBox( SWT.OK | SWT.ICON_ERROR, title, message );
    }


    /**
     * Displays a Information Message Box with the given title and message.
     *
     * @param title
     *      the title of the window
     * @param message
     *      the message to display
     * @return
     *      the ID of the button that was selected to dismiss 
     *      the message box (e.g. SWT.OK, SWT.CANCEL, etc...)
     */
    public static int displayWarningMessageBox( String title, String message )
    {
        return displayMessageBox( SWT.OK | SWT.ICON_WARNING, title, message );
    }


    /**
     * Displays a Information Message Box with the given title and message.
     *
     * @param title
     *      the title of the window
     * @param message
     *      the message to display
     * @return
     *      the ID of the button that was selected to dismiss 
     *      the message box (e.g. SWT.OK, SWT.CANCEL, etc...)
     */
    public static int displayInformationMessageBox( String title, String message )
    {
        return displayMessageBox( SWT.OK | SWT.ICON_INFORMATION, title, message );
    }


    /**
     * Displays a Information Question Box with the given title and message.
     *
     * @param buttonStyle
     *      the style of the buttons of the dialog (e.g. SWT.OK, SWT.CANCEL, etc...)
     * @param title
     *      the title of the window
     * @param message
     *      the message to display
     * @return
     *      the ID of the button that was selected to dismiss 
     *      the message box (e.g. SWT.OK, SWT.CANCEL, etc...)
     */
    public static int displayQuestionMessageBox( int buttonStyle, String title, String message )
    {
        return displayMessageBox( SWT.ICON_QUESTION | buttonStyle, title, message );
    }


    /**
     * Displays a Message Box with the given style, title and message.
     *
     * @param style
     *      the style of dialog
     * @param title
     *      the title of the window
     * @param message
     *      the message to display
     * @return
     *      the ID of the button that was selected to dismiss 
     *      the message box (e.g. SWT.OK, SWT.CANCEL, etc...)
     */
    private static int displayMessageBox( int style, String title, String message )
    {
        MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), style );
        messageBox.setText( title );
        messageBox.setMessage( message );
        return messageBox.open();
    }

}
