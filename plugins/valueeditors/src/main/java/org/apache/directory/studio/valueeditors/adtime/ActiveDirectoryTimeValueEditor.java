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

package org.apache.directory.studio.valueeditors.adtime;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


/**
 * Implementation of IValueEditor for attributes:
 * - pwdLastSet
 * - accountExpires
 * - lastLogoff
 * - lastLogon
 * - lastLogonTimeStamp
 * - badPasswordTime
 * 
 * Currently only the getDisplayXXX() methods are implemented.
 * For modification the raw string must be edited.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ActiveDirectoryTimeValueEditor extends AbstractDialogStringValueEditor
{
    /**
     * {@inheritDoc}
     * 
     * Returns the proper formatted date and time, timezone is 
     * converted to the default locale. 
     */
    public String getDisplayValue( IValue value )
    {
        String displayValue = super.getDisplayValue( value );

        if ( !showRawValues() )
        {
            DateFormat targetFormat = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.LONG );

            try
            {
                long adTimeValue = Long.parseLong( displayValue );
                Date date = ActiveDirectoryTimeUtils.convertToCalendar( adTimeValue ).getTime();
                displayValue = targetFormat.format( date ) + " (" + displayValue + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch ( NumberFormatException e )
            {
                // show the raw value in that case
            }
        }

        return displayValue;
    }


    /**
     * {@inheritDoc}
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof String )
        {
            String s = ( String ) value;
            long adTimeValue = 0;

            if ( !"".equals( s ) ) //$NON-NLS-1$
            {
                // Trying to parse the value
                try
                {
                    adTimeValue = Long.parseLong( s );
                }
                catch ( NumberFormatException pe )
                {
                    // The value could not be parsed correctly

                    // Displaying an error window indicating to the user that the value is bogus
                    // and asking him if he wants to continue to edit the value with current date and time selected
                    if ( MessageDialog.openConfirm( PlatformUI.getWorkbench().getDisplay().getActiveShell(), Messages
                        .getString( "ActiveDirectoryTimeValueEditor.BogusDateAndTimeValue" ), NLS.bind( //$NON-NLS-1$
                        Messages.getString( "ActiveDirectoryTimeValueEditor.TheValueIsBogus" ), new String[] //$NON-NLS-1$
                        { s } ) ) )
                    {
                        // Generating today's date and time
                        adTimeValue = ActiveDirectoryTimeUtils.convertToActiveDirectoryTime( Calendar.getInstance() );
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            else
            {
                // Generating today's date and time
                adTimeValue = ActiveDirectoryTimeUtils.convertToActiveDirectoryTime( Calendar.getInstance() );
            }

            // Creating and opening the dialog
            ActiveDirectoryTimeValueDialog dialog = new ActiveDirectoryTimeValueDialog( shell, adTimeValue );
            if ( dialog.open() == ActiveDirectoryTimeValueDialog.OK )
            {
                setValue( "" + dialog.getValue() );
                return true;
            }
        }

        return false;
    }
}
