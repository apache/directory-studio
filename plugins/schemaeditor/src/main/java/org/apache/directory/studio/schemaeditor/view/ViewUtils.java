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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
     * Displays an error message dialog with the given title and message.
     *
     * @param title the title of the window
     * @param message the message to display
     * @return <code>true</code> if the user presses the OK or Yes button,
     *         <code>false</code> otherwise
     */
    public static boolean displayErrorMessageDialog( String title, String message )
    {
        return displayMessageDialog( MessageDialog.ERROR, title, message );
    }


    /**
     * Displays a warning message dialog with the given title and message.
     *
     * @param title the title of the window
     * @param message the message to display
     * @return <code>true</code> if the user presses the OK or Yes button,
     *         <code>false</code> otherwise
     */
    public static boolean displayWarningMessageDialog( String title, String message )
    {
        return displayMessageDialog( MessageDialog.WARNING, title, message );
    }


    /**
     * Displays a information message dialog with the given title and message.
     *
     * @param title the title of the window
     * @param message the message to display
     * @return <code>true</code> if the user presses the OK or Yes button,
     *         <code>false</code> otherwise
     */
    public static boolean displayInformationMessageDialog( String title, String message )
    {
        return displayMessageDialog( MessageDialog.INFORMATION, title, message );
    }


    /**
     * Displays a Information Question message dialog with the given title and message.
     * 
     * @param title the title of the window
     * @param message the message to display
     * @return <code>true</code> if the user presses the OK or Yes button,
     *         <code>false</code> otherwise
     */
    public static boolean displayQuestionMessageDialog( String title, String message )
    {
        return displayMessageDialog( MessageDialog.QUESTION, title, message );
    }


    /**
     * Displays a message dialog with the given style, title and message.
     *
     * @param kind the kind of dialog
     * @param title the title of the window
     * @param message the message to display
     * @return <code>true</code> if the user presses the OK or Yes button,
     *         <code>false</code> otherwise
     */
    private static boolean displayMessageDialog( int kind, String title, String message )
    {
        return MessageDialog.open( kind, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
            message, SWT.NONE );
    }
}
