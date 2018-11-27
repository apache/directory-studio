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
package org.apache.directory.studio.common.ui;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.Bundle;


/**
 * This class contains helpful methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CommonUIUtils
{
    /**
     * Opens an Error {@link MessageDialog} with the given message.
     *
     * @param message the message
     */
    public static void openErrorDialog( String message )
    {
        openErrorDialog( Messages.getString( "CommonUIUtils.Error" ), message ); //$NON-NLS-1$
    }


    /**
     * Opens an Error {@link MessageDialog} with the given title and message.
     *
     * @param title the title
     * @param message the message
     */
    public static void openErrorDialog( String title, String message )
    {
        MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, null, message, MessageDialog.ERROR, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Opens an Information {@link MessageDialog} with the given message.
     *
     * @param message the message
     */
    public static void openInformationDialog( String message )
    {
        openInformationDialog( Messages.getString( "CommonUIUtils.Information" ), message ); //$NON-NLS-1$
    }


    /**
     * Opens an Information {@link MessageDialog} with the given title and message.
     *
     * @param title the title
     * @param message the message
     */
    public static void openInformationDialog( String title, String message )
    {
        MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, null, message, MessageDialog.INFORMATION, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Opens an Warning {@link MessageDialog} with the given message.
     *
     * @param message the message
     */
    public static void openWarningDialog( String message )
    {
        openWarningDialog( Messages.getString( "CommonUIUtils.Warning" ), message ); //$NON-NLS-1$
    }


    /**
     * Opens an Warning {@link MessageDialog} with the given title and message.
     *
     * @param title the title
     * @param message the message
     */
    public static void openWarningDialog( String title, String message )
    {
        MessageDialog dialog = new MessageDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, null, message, MessageDialog.WARNING, new String[]
                { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Checks, if this plugins runs in the Eclipse IDE or in RCP environment.
     * This is done by looking if "org.apache.directory.studio.rcp" bundle is installed.
     *
     * @return true if this plugin runs in IDE environment
     */
    public static boolean isIDEEnvironment()
    {
        Bundle bundle = Platform.getBundle( "org.apache.directory.studio.rcp" );
        return bundle == null;
    }
    
    
    /**
     * Create a default Text input, with a label and a ModifyListener.
     * 
     * @param toolkit the toolkit
     * @param composite the Composite
     * @param label the Text label
     * @param value the default value. Default to "" if null
     * @param limit the size limit. Ignored if < 0 
     * @param listener the ModifyListener
     * @return An instance of a Text
     */
    public static Text createText( FormToolkit toolkit, Composite composite, String label, String defaultValue, int limit, ModifyListener listener  )
    {
        toolkit.createLabel( composite, label );
        String value = "";
        
        if ( defaultValue != null )
        {
            value = defaultValue;
        }
        
        Text text = toolkit.createText( composite, value );
        text.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        
        if ( limit >= 0 )
        {
            text.setTextLimit( limit );
        }
        
        // Attach a listener to check the value
        if ( listener != null )
        {
            text.addModifyListener( listener );
        }
        
        return text;
    }
    
    
    /**
     * Create a default Text input, with a label and a ModifyListener.
     * 
     * @param toolkit the toolkit
     * @param composite the Composite
     * @param label the Text label
     * @param value the default value. Default to "" if null
     * @param limit the size limit. Ignored if < 0 
     * @param gridData the GridData
     * @param listener the ModifyListener
     * @return An instance of a Text
     */
    public static Text createText( FormToolkit toolkit, Composite composite, String label, String defaultValue, int limit, GridData gridData, ModifyListener listener  )
    {
        toolkit.createLabel( composite, label );
        String value = "";
        
        if ( defaultValue != null )
        {
            value = defaultValue;
        }
        
        Text text = toolkit.createText( composite, value );
        text.setLayoutData( gridData );
        
        if ( limit >= 0 )
        {
            text.setTextLimit( limit );
        }
        
        // Attach a listener to check the value
        if ( listener != null )
        {
            text.addModifyListener( listener );
        }
        
        return text;
    }
    
    
    /**
     * An utility method that return a String which is never null.
     * 
     * @param value The incoming value, which can be null
     * @return The String if it's not null, "" otherwise
     */
    public static String getTextValue( String value )
    {
        if ( value == null )
        {
            return "";
        }
        else
        {
            return value;
        }
    }
    
    
    /**
     * An utility method that return a String which is never 0.
     * 
     * @param value The incoming value, which can be 0
     * @return The Int value if it's not 0, "" otherwise
     */
    public static String getTextValue( int value )
    {
        if ( value == 0 )
        {
            return "";
        }
        else
        {
            return Integer.toString( value );
        }
    }
}
