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

package org.apache.directory.studio;


import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to get Strings to display in the User Interface
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Messages
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( Messages.class );

    private static final String BUNDLE_NAME = "org.apache.directory.studio.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );


    /**
     * Default constuctor
     */
    private Messages()
    {
    }


    /**
     * Returns a String associated with the key given in parameter
     * @param key the key associated to the String 
     * @return the corresponding String
     */
    public static String getString( String key )
    {
        try
        {
            return RESOURCE_BUNDLE.getString( key );
        }
        catch ( MissingResourceException e )
        {
            logger.warn( "Associated ressource not found for key {}", key ); //$NON-NLS-1$
            return '!' + key + '!';
        }
    }
}
