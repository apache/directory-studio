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
 * Base implementation of ConnectionParameterPage.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractConnectionParameterPage implements ConnectionParameterPage
{
    /** The page id. */
    protected String pageId;

    /** The page name. */
    protected String pageName;

    /** The page description. */
    protected String pageDescription;

    /** The page id this page depends on. */
    protected String pageDependsOnId;

    /** The runnable context. */
    protected IRunnableContext runnableContext;

    /** The connection parameter page modify listener. */
    protected ConnectionParameterPageModifyListener connectionParameterPageModifyListener;

    /** The message. */
    protected String message;

    /** The error message. */
    protected String errorMessage;

    /** The info message. */
    protected String infoMessage;

    /** The connection parameter. */
    protected ConnectionParameter connectionParameter;


    /**
     * Sets the connection parameter page modify listener.
     * 
     * @param listener the connection parameter page modify listener
     */
    public void setConnectionParameterPageModifyListener( ConnectionParameterPageModifyListener listener )
    {
        this.connectionParameterPageModifyListener = listener;
    }


    /**
     * Fires a connection page modified event when then page was modified.
     */
    protected void fireConnectionPageModified()
    {
        connectionParameterPageModifyListener.connectionParameterPageModified();
    }


    /**
     * Sets the runnable context.
     * 
     * @param runnableContext the runnable context
     */
    public void setRunnableContext( IRunnableContext runnableContext )
    {
        this.runnableContext = runnableContext;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getPageId()
     */
    public String getPageId()
    {
        return pageId;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setPageId(java.lang.String)
     */
    public void setPageId( String pageId )
    {
        this.pageId = pageId;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getPageName()
     */
    public String getPageName()
    {
        return pageName;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setPageName(java.lang.String)
     */
    public void setPageName( String pageName )
    {
        this.pageName = pageName;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getPageDescription()
     */
    public String getPageDescription()
    {
        return pageDescription;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setPageDescription(java.lang.String)
     */
    public void setPageDescription( String pageDescription )
    {
        this.pageDescription = pageDescription;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getPageDependsOnId()
     */
    public String getPageDependsOnId()
    {
        return pageDependsOnId;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setPageDependsOnId(java.lang.String)
     */
    public void setPageDependsOnId( String pageDependsOnId )
    {
        this.pageDependsOnId = pageDependsOnId;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getErrorMessage()
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getMessage()
     */
    public String getMessage()
    {
        return message;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#isValid()
     */
    public boolean isValid()
    {
        return message == null && errorMessage == null;
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#getInfoMessage()
     */
    public String getInfoMessage()
    {
        return infoMessage;
    }


    /**
     * {@inheritDoc}
     */
    public final void init( Composite parent, ConnectionParameterPageModifyListener listener,
        ConnectionParameter parameter )
    {
        createComposite( parent );

        if ( listener != null )
        {
            setConnectionParameterPageModifyListener( listener );
        }

        if ( parameter != null )
        {
            loadParameters( parameter );
        }

        initListeners();
        connectionPageModified();
    }


    /**
     * Called when an input field was modified.
     */
    protected final void connectionPageModified()
    {
        validate();
        fireConnectionPageModified();
    }


    /**
     * Creates the composite.
     * 
     * @param parent the parent
     */
    protected abstract void createComposite( Composite parent );


    /**
     * Validates the input fields.
     */
    protected abstract void validate();


    /**
     * Initializes the fields with the given parameters.
     * 
     * @param parameter the connection parameter
     */
    protected abstract void loadParameters( ConnectionParameter parameter );


    /**
     * Initializes the listeners.
     */
    protected abstract void initListeners();
}
