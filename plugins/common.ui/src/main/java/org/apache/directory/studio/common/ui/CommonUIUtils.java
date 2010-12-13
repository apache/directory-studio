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


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;


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
     * @param message
     *      the message
     */
    public static void openErrorDialog( String message )
    {
        openErrorDialog( "Error!", message );
    }


    /**
     * Opens an Error {@link MessageDialog} with the given title and message.
     *
     * @param title
     *      the title
     * @param message
     *      the message
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
     * @param message
     *      the message
     */
    public static void openInformationDialog( String message )
    {
        openInformationDialog( "Information", message );
    }


    /**
     * Opens an Information {@link MessageDialog} with the given title and message.
     *
     * @param title
     *      the title
     * @param message
     *      the message
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
     * @param message
     *      the message
     */
    public static void openWarningDialog( String message )
    {
        openInformationDialog( "Information", message );
    }


    /**
     * Opens an Warning {@link MessageDialog} with the given title and message.
     *
     * @param title
     *      the title
     * @param message
     *      the message
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
     * This is done by looking for the Resource perspective extensions.
     *
     * @return true if this plugin runs in IDE environment
     */
    public static boolean isIDEEnvironment()
    {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
            "org.eclipse.ui.perspectives" ); //$NON-NLS-1$
        if ( extensionPoint != null )
        {
            IExtension[] extensions = extensionPoint.getExtensions();
            if ( extensions != null )
            {
                for ( int i = 0; i < extensions.length; i++ )
                {
                    IExtension extension = extensions[i];
                    IConfigurationElement[] elements = extension.getConfigurationElements();
                    for ( int j = 0; j < elements.length; j++ )
                    {
                        IConfigurationElement element = elements[j];
                        if ( element.getName().equals( "perspective" ) ) //$NON-NLS-1$
                        {
                            if ( "org.eclipse.ui.resourcePerspective".equals( element.getAttribute( "id" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
