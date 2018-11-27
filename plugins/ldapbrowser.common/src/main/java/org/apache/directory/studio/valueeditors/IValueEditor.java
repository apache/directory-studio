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

package org.apache.directory.studio.valueeditors;


import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;


/**
 * A ValueEditor knows how to display and edit values of a LDAP attribute.
 * ValueEditors are used from the entry editor or search result editor 
 * to display and edit values in a user-friendly way.  
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IValueEditor
{

    /**
     * Returns the string representation of the given attribute hierarchy 
     * presented to the user.
     * <p>
     * This method is called from the search result editor. A attribute hierarchy may
     * contain multiple attributes each with multiple values. It is common that
     * a ValueEditor returns a comma-separated list.
     * 
     * @param attributeHierarchy the attribute hierarchy
     * @return the string representation of the attribute hierarchy
     */
    String getDisplayValue( AttributeHierarchy attributeHierarchy );


    /**
     * Returns the string representation of the given value 
     * presented to the user.
     * <p>
     * This method is called from the entry editor.
     * 
     * @param value the value
     * @return the string representation of the value
     */
    String getDisplayValue( IValue value );


    /**
     * Returns the raw value if this value editor can handle the given 
     * attribute hierarchy. The returned value is used as input for 
     * the CellEditor returned by getCellEditor().
     * <p>
     * If this value editor can't handle the given attribute hierarchy
     * it must return null. 
     * <p>
     * Note: It is also possible that the attribute hierarchy doesn't contain
     * a value. This means the value is up to be created.
     * <p>
     * This method is called from the search result editor. It is common
     * to return null if the attribute hierarchy contains more than 
     * one value.
     * 
     * @param attributeHierarchy the attribute hierarchy
     * @return the raw value of the attribute hierarchy or null
     */
    Object getRawValue( AttributeHierarchy attributeHierarchy );


    
    
    /**
     * Tells if there is a value
     * 
     * @return true if there is a value, false if it's null.
     */
    boolean hasValue( IValue value );
    

    /**
     * Returns the raw value if this value editor can handle the given 
     * value. The returned value is used as input for the CellEditor 
     * returned by getCellEditor().
     * <p>
     * If this value editor can't handle the given value it must 
     * return null. 
     * <p>
     * Note: It is also possible that the value is empty!
     * <p>
     * This method is called from the entry editor. 
     * 
     * @param value the value
     * @return the raw value of the value or null
     */
    Object getRawValue( IValue value );


    /**
     * Returns the String or binary byte[] value of the given raw value. 
     * The return value is used to create,  modify or delete the value
     * in directory.
     * <p>
     * This method is called after editing has been finished. The 
     * given rawValue is the one returned by the CellEditor. 
     * 
     * @param rawValue the raw value return from cell editor
     * @return the String or byte[] value
     */
    Object getStringOrBinaryValue( Object rawValue );


    /**
     * Returns the editors name, previously set with
     * setValueEditorName().
     * 
     * @return the editors name
     */
    String getValueEditorName();


    /**
     * Sets the editors name. 
     * 
     * This method is called during initialization of the 
     * value editor, the name specified in value editor
     * extension is assigned. 
     *
     * @param name the editors name
     */
    void setValueEditorName( String name );


    /**
     * Returns the editors image, previously set with
     * setValueEditorImageDescriptor().
     * 
     * @return the editors image
     */
    ImageDescriptor getValueEditorImageDescriptor();


    /**
     * Sets the editors image.
     * 
     * This method is called during initialization of the 
     * value editor, the icon specified in value editor
     * extension is assigned. 
     *
     * @param imageDescriptor the editors image
     */
    void setValueEditorImageDescriptor( ImageDescriptor imageDescriptor );


    /**
     * Creates the control for this value editor under the given parent control.
     * 
     * @param parent the parent control
     */
    void create( Composite parent );


    /**
     * Disposes of this value editor and frees any associated SWT resources.
     */
    void dispose();


    /**
     * Returns the JFace CellEditor that is able to handle values returned by
     * one of the getRawValue() or the getEmptyRawValue() methods.
     * 
     * The object returned by the CellEditor's getValue() method is
     * then sent to the getStringOrBinary() method to get the 
     * directory value.
     * 
     * @return the JFace CellEditor
     * 
     */
    CellEditor getCellEditor();

    // A constant for the emtpy string and null string.
    String EMPTY = ""; //$NON-NLS-1$
    String NULL = ">>> Error, the configured value editor can not handle this value! <<<"; //$NON-NLS-1$
}
