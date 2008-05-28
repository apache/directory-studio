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
package org.apache.directory.studio.apacheds;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


/**
 * This class is used to store utilities for the preference store.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PreferenceStoreUtils
{
    /**
     * Indicates if the given array of {@link FontData} is italic.
     *
     * @param fontDatas
     *      the array
     * @return
     *      <code>true</code> if the given array of {@link FontData} is italic, <code>false</code> if not.
     */
    public static boolean isItalic( FontData[] fontDatas )
    {
        boolean italic = false;

        for ( FontData fontData : fontDatas )
        {
            if ( ( fontData.getStyle() & SWT.ITALIC ) != SWT.NORMAL )
            {
                italic = true;
            }
        }

        return italic;
    }


    /**
     * Indicates if the given array of {@link FontData} is bold.
     *
     * @param fontDatas
     *      the array
     * @return
     *      <code>true</code> if the given array of {@link FontData} is bold, <code>false</code> if not.
     */
    public static boolean isBold( FontData[] fontDatas )
    {
        boolean bold = false;

        for ( FontData fontData : fontDatas )
        {
            if ( ( fontData.getStyle() & SWT.BOLD ) != SWT.NORMAL )
            {
                bold = true;
            }
        }

        return bold;
    }
}
