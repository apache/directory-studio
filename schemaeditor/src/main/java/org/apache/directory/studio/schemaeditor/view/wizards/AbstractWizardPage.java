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
package org.apache.directory.studio.schemaeditor.view.wizards;


import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;


/**
 * This abstract class extends {@link WizardPage} and holds common methods
 * for all our {@link WizardPage}s.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractWizardPage extends WizardPage
{
    /**
     * Creates a new wizard page with the given name, title, and image.
     *
     * @param pageName the name of the page
     * @param title the title for this wizard page,
     *   or <code>null</code> if none
     * @param titleImage the image descriptor for the title of this wizard page,
     *   or <code>null</code> if none
     */
    protected AbstractWizardPage( String pageName, String title, ImageDescriptor titleImage )
    {
        super( pageName, title, titleImage );
    }


    /**
     * Creates a new wizard page with the given name, and
     * with no title or image.
     *
     * @param pageName the name of the page
     */
    protected AbstractWizardPage( String pageName )
    {
        super( pageName );
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    protected void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setMessage( null, DialogPage.NONE );
        setPageComplete( message == null );
    }


    /**
     * Displays a warning message and set the page status as complete.
     *
     * @param message
     *      the message to display
     */
    protected void displayWarningMessage( String message )
    {
        setErrorMessage( null );
        setMessage( message, DialogPage.WARNING );
        setPageComplete( true );
    }
}
