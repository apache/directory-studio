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

package org.apache.directory.ldapstudio.browser.ui.valueproviders;


import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;


public interface ValueProvider
{

    /**
     * Returns the String displayed on the table.
     * 
     * @param attributes
     * @return
     */
    public abstract String getDisplayValue( AttributeHierachie ah );


    /**
     * Returns the String displayed on the table.
     * 
     * @param value
     * @return
     */
    public abstract String getDisplayValue( IValue value );


    /**
     * Returns the raw value or null if this value provider can't handle the
     * given attributes.
     * 
     * The returned value must be editable with the cell editor returned by
     * getCellEditor().
     * 
     * @param attributes
     *                The attributes
     * @return The raw value of the attriubtes or null
     */
    public abstract Object getRawValue( AttributeHierachie ah );


    /**
     * Returns the raw value or null if this value provider can't handle the
     * given value.
     * 
     * The returned value must be editable with the cell editor returned by
     * getCellEditor().
     * 
     * @param value
     *                The value object
     * @return The raw value of the value or null
     */
    public abstract Object getRawValue( IValue value );


    /**
     * Returns the raw value or null if this value provider can't handle the
     * given value.
     * 
     * The returned value must be editable with the cell editor returned by
     * getCellEditor().
     * 
     * @param schema
     *                The connection used for context-dependent values, may
     *                be null.
     * @param schema
     *                The schema used for context dependent-values, may be
     *                null.
     * @param value
     *                The value of any type
     * @return The raw value of the value or null
     */
    public abstract Object getRawValue( IConnection connection, Schema schema, Object value );


    /**
     * Creates the attribute with ghe given value at the entry.
     * 
     * It is called from a ICellModifier if no attribute of value exists and
     * the raw value returned by the CellEditor isn't null.
     * 
     * @param entry
     * @param attributeDescription
     * @param newRawValue
     * @throws ModelModificationException
     */
    public abstract void create( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException;


    /**
     * Modifies the attribute and sets the given raw value
     * 
     * It is called from a ICellModfier if the attribute exists and the raw
     * value returned by the CellEditor isn't null.
     * 
     * @param attribute
     * @param newRawValue
     * @throws ModelModificationException
     */
    // public abstract void modify(IAttribute[] attributes, Object
    // newRawValue) throws ModelModificationException;
    /**
     * Modifies the value and sets the given raw value
     * 
     * It is called from a ICellModfier if the value exists and the raw
     * value returned by the CellEditor isn't null.
     * 
     * @param value
     * @param newRawValue
     * @throws ModelModificationException
     */
    public abstract void modify( IValue value, Object newRawValue ) throws ModelModificationException;


    /**
     * Deletes the attributes
     * 
     * It is called from a ICellModfier if the attribute exists and the raw
     * value returned by the CellEditor is null.
     * 
     * @param attributes
     * @throws ModelModificationException
     */
    public abstract void delete( AttributeHierachie ah ) throws ModelModificationException;


    /**
     * Deletes the value
     * 
     * It is called from a ICellModfier if the value exists and the raw
     * value returned by the CellEditor is null.
     * 
     * @param oldValue
     * @throws ModelModificationException
     */
    public abstract void delete( IValue oldValue ) throws ModelModificationException;


    /**
     * Returns the CellEditor to handle the values
     * 
     * The input of the cell editor will be one of the objects return by
     * getEmptyRawValue(IEntry, String), getRawValue(IAttribute) or
     * getRawValue(IValue).
     * 
     * The output of the cell editor is the modified value or null.
     * 
     */
    public abstract CellEditor getCellEditor();


    /**
     * Returns the editors name.
     * 
     * @return the editors name.
     */
    public abstract String getCellEditorName();


    /**
     * Returns the editors image.
     * 
     * @return the editors image.
     */
    public abstract ImageDescriptor getCellEditorImageDescriptor();


    public abstract void dispose();

}
