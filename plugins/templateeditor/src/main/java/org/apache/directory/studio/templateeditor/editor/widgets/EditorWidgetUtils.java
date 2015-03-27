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
package org.apache.directory.studio.templateeditor.editor.widgets;


import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorWidgetUtils
{
    /**
     * Gets the values of the attribute as a string.
     *
     * @param entry
     *      the entry
     * @param attributeType
     *      the attribute type
     * @return
     *      the values of the attribute as a string
     */
    public static String getConcatenatedValues( IEntry entry, String attributeType )
    {
        if ( ( entry != null ) && ( attributeType != null ) )
        {
            // Getting the requested attribute
            IAttribute attribute = entry.getAttribute( attributeType );
            if ( attribute != null )
            {
                if ( attribute.getValues().length != 0 )
                {
                    // Checking the type of the value(s)
                    if ( attribute.isBinary() )
                    {
                        // Binary value(s)
                        if ( attribute.getBinaryValues().length == 1 )
                        {
                            return Messages.getString( "EditorWidgetUtils.BinaryValue" ); //$NON-NLS-1$
                        }
                        else
                        {
                            return Messages.getString( "EditorWidgetUtils.BinaryValues" ); //$NON-NLS-1$
                        }
                    }
                    else if ( attribute.isString() )
                    {
                        // String value(s)
                        return EditorWidgetUtils.concatenateValues( attribute.getStringValues() );
                    }
                }
            }
        }

        return ""; //$NON-NLS-1$
    }


    /**
     * Concatenates the given values.
     * <p>
     * Giving an array containing the following <code>["a", "b", "c"]</code>
     * produces this string <em>a, b, c</em>.
     *
     * @param values
     *      the values
     * @return
     *      a string with the concatenated values
     */
    private static String concatenateValues( String[] values )
    {
        StringBuilder sb = new StringBuilder();

        if ( values != null )
        {
            for ( String value : values )
            {
                sb.append( value );
                sb.append( ", " ); //$NON-NLS-1$
            }

            sb.delete( sb.length() - 2, sb.length() );
        }

        return sb.toString();
    }
}
