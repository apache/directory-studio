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
package org.apache.directory.studio.templateeditor.model;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.directory.studio.templateeditor.model.widgets.TemplateForm;


/**
 * This abstract class defines the basic implementation for a template.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractTemplate implements Template
{
    /**
     * Indicates if the ID is valid or not.
     *
     * @param id
     *      the id
     * @return
     *      <code>true</code> if the ID is valid,
     *      <code>false</code> if not.
     */
    public static boolean isValidId( String id )
    {
        return Pattern.matches( "[a-zA-Z][a-zA-Z0-9-.]*", id ); //$NON-NLS-1$
    }

    /** The ID */
    private String id;

    /** The title */
    private String title;

    /** The structural object class */
    private String structuralObjectClass;

    /** The list of auxiliary object classes */
    private List<String> auxiliaryObjectClasses;

    /** The form */
    private TemplateForm form;


    /**
     * Creates a new instance of AbstractTemplate.
     */
    public AbstractTemplate()
    {
        init();
    }


    /**
     * Creates a new instance of AbstractTemplate.
     *
     * @param id
     *      the id of the template
     */
    public AbstractTemplate( String id )
    {
        this.id = id;
        init();
    }


    /**
     * {@inheritDoc}
     */
    public boolean addAuxiliaryObjectClass( String objectClass )
    {
        return auxiliaryObjectClasses.add( objectClass );
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getAuxiliaryObjectClasses()
    {
        return auxiliaryObjectClasses;
    }


    /**
     * {@inheritDoc}
     */
    public TemplateForm getForm()
    {
        return form;
    }


    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return id;
    }


    /**
     * Gets the structural object class.
     * 
     * @return
     *      the structural object class
     */
    public String getStructuralObjectClass()
    {
        return structuralObjectClass;
    }


    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * Initializes the fields of the AbstractTemplate.
     */
    private void init()
    {
        auxiliaryObjectClasses = new ArrayList<String>();
    }


    /**
     * {@inheritDoc}
     */
    public boolean removeAuxiliaryObjectClass( String objectClass )
    {
        return auxiliaryObjectClasses.remove( objectClass );
    }


    /**
     * {@inheritDoc}
     */
    public void setAuxiliaryObjectClasses( List<String> objectClasses )
    {
        this.auxiliaryObjectClasses = objectClasses;
    }


    /**
     * {@inheritDoc}
     */
    public void setForm( TemplateForm form )
    {
        this.form = form;
    }


    /**
     * {@inheritDoc}
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Sets the structural object class.
     * 
     * @param objectClass
     *      the structural object class
     */
    public void setStructuralObjectClass( String objectClass )
    {
        structuralObjectClass = objectClass;
    }


    /**
     * {@inheritDoc}
     */
    public void setTitle( String title )
    {
        this.title = title;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return ( title == null ) ? Messages.getString( "AbstractTemplate.UntitledTemplate" ) : title; //$NON-NLS-1$
    }
}
