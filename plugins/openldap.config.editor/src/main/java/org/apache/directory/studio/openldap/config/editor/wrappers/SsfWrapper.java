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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.openldap.common.ui.model.SsfEnum;

/**
 * This class wraps the olcSecurity parameter :
 * <pre>
 * olcSecurity ::= (<feature>=<size>)*
 * <feature> ::= 'ssf' | 'transport' | 'tls' | 'sasl' | 'simple_bind' | 
 *               'updates_ssf' | 'updates_transport' | 'updates_rls' | 'updates_sasl'
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SsfWrapper
{
    /** The feature */
    private SsfEnum feature;
    
    /** The number of bits for this feature */
    private int nbBits;
    
    /**
     * Creates an instance using a name of a feature and its size
     * 
     * @param feature The feature to use
     * @param nbBits The number of bits
     */
    public SsfWrapper( String feature, int nbBits )
    {
        this.feature = SsfEnum.getSsf( feature );

        if ( this.feature == SsfEnum.NONE )
        {
            this.nbBits = 0;
        }
        else
        {
            this.nbBits = nbBits;
        }

        if ( nbBits < 0 )
        {
            this.nbBits = 0;
        }

    }
    
    
    /**
     * Creates an instance using a String representation, which format is
     * feature = <nbBits>
     * 
     * @param feature The feature to use
     */
    public SsfWrapper( String feature )
    {
        if ( Strings.isEmpty( feature ) )
        {
            this.feature = SsfEnum.NONE;
            this.nbBits = 0;
        }
        else
        {
            int pos = feature.indexOf( '=' );
            
            if ( pos < 0 )
            {
                this.feature = SsfEnum.NONE;
                this.nbBits = 0;
            }

            String name = Strings.trim( feature.substring( 0, pos ) );
            this.feature = SsfEnum.getSsf( name );
            
            if ( this.feature == SsfEnum.NONE )
            {
                this.nbBits = 0;
            }
            else
            {
                String value = Strings.trim( feature.substring( pos + 1 ) );
                
                try
                {
                    this.nbBits = Integer.parseInt( value );
                }
                catch ( NumberFormatException nfe )
                {
                    this.nbBits = 0;
                }
            }
        }
    }
    
    
    /**
     * Tells if the String is a valid SSF. The format is :
     * feature = <nbBits>
     * 
     * @param feature The feature to check
     * @return true if valid
     */
    public static boolean isValid(String feature )
    {
        if ( Strings.isEmpty( feature ) )
        {
            return false;
        }
        else
        {
            int pos = feature.indexOf( '=' );
            
            if ( pos < 0 )
            {
                return false;
            }
            
            String name = Strings.trim( feature.substring( 0, pos ) );
            SsfEnum ssf = SsfEnum.getSsf( name );
            
            if ( ssf == SsfEnum.NONE )
            {
                return false;
            }
            else
            {
                String value = Strings.trim( feature.substring( pos + 1 ) );
                
                try
                {
                    return Integer.parseInt( value ) >= 0 ;
                }
                catch ( NumberFormatException nfe )
                {
                    return false;
                }
            }
        }
    }


    /**
     * @return the feature
     */
    public SsfEnum getFeature()
    {
        return feature;
    }


    /**
     * @return the nbBits
     */
    public int getNbBits()
    {
        return nbBits;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        if ( feature != SsfEnum.NONE )
        {
            return feature.getText() + '=' + nbBits;
        }
        else
        {
            return "";
        }
    }
}
