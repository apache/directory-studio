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
package org.apache.directory.studio.ldapservers.model;


import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This interface defines a configuration page for an {@link LdapServerAdapter}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractLdapServerAdapterConfigurationPage implements LdapServerAdapterConfigurationPage
{
    /** The id */
    protected String id;

    /** The title */
    protected String title;

    /** The description */
    protected String description;

    /** The image descriptor */
    protected ImageDescriptor imageDescriptor;

    /** The error message */
    protected String errorMessage;

    /** The flag for page completion */
    protected boolean pageComplete = true;

    /** The modify listener */
    protected LdapServerAdapterConfigurationPageModifyListener modifyListener;


    /**
     * {@inheritDoc}
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }


    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return id;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return imageDescriptor;
    }


    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isPageComplete()
    {
        return pageComplete;
    }


    /**
     * Sets the description.
     * 
     * @param description the description to set
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * Sets the error message.
     * 
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
        setPageComplete(  errorMessage == null );
    }


    /**
     * Sets the id.
     * 
     * @param id the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Sets the {@link ImageDescriptor}.
     * 
     * @param imageDescriptor the imageDescriptor to set
     */
    public void setImageDescriptor( ImageDescriptor imageDescriptor )
    {
        this.imageDescriptor = imageDescriptor;
    }


    /**
     * {@inheritDoc}
     */
    public void setModifyListener( LdapServerAdapterConfigurationPageModifyListener modifyListener )
    {
        this.modifyListener = modifyListener;
    }


    /**
     * Sets the page completion flag.
     * 
     * @param pageComplete the pageComplete to set
     */
    public void setPageComplete( boolean pageComplete )
    {
        this.pageComplete = pageComplete;
    }


    /**
     * Sets the title.
     * 
     * @param title the title to set
     */
    public void setTitle( String title )
    {
        this.title = title;
    }


    /**
     * Called when an input field is modified.
     */
    protected final void configurationPageModified()
    {
        validate();
        fireConfigurationPageModified();
    }


    /**
     * Fires a configuration page modified event when the page was modified.
     */
    protected void fireConfigurationPageModified()
    {
        if ( modifyListener != null )
        {
            modifyListener.configurationPageModified();
        }
    }
}
