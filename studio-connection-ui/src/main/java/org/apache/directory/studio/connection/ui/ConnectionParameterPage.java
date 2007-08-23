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
package org.apache.directory.studio.connection.ui;


import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;


/**
 * An IConnectionParameterPage is used to add connection parameter pages
 * to the connection wizard and the connection property page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ConnectionParameterPage
{

    /**
     * Initializes the fields with the given parameters.
     * 
     * @param parameter the parameter
     */
    public void loadParameters( ConnectionParameter parameter );


    /**
     * Save the fields to the parameters.
     * 
     * @param parameter the parameter
     */
    public void saveParameters( ConnectionParameter parameter );


    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public boolean isValid();


    /**
     * Gets an error message that should be displayed
     * to the user. Null means no error message so an 
     * existing error message should be cleared.
     * 
     * @return the error message
     */
    public String getErrorMessage();


    /**
     * Gets a non-error message that should be displayed
     * to the user. Null means no message so an existing
     * message should be cleared.
     * 
     * @return the message
     */
    public String getMessage();


    /**
     * Creates the composite.
     * 
     * @param parent the parent
     */
    public void createComposite( Composite parent );


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings();


    /**
     * Sets the connection parameter page modify listener.
     * 
     * @param listener the connection parameter page modify listener
     */
    public void setConnectionParameterPageModifyListener( ConnectionParameterPageModifyListener listener );


    /**
     * Sets the runnable context.
     * 
     * @param runnableContext the runnable context
     */
    public void setRunnableContext( IRunnableContext runnableContext );


    /**
     * Sets the page name.
     * 
     * @param pageName the page name
     */
    public void setPageName( String pageName );


    /**
     * Gets the page name.
     * 
     * @return the page name
     */
    public String getPageName();
    
    
    /**
     * Sets the page description.
     * 
     * @param pageDescription the page description
     */
    public void setPageDescription( String pageDescription );
    
    
    /**
     * Gets the page description.
     * 
     * @return the page description
     */
    public String getPageDescription();


    /**
     * Sets the focus.
     */
    public void setFocus();

}
