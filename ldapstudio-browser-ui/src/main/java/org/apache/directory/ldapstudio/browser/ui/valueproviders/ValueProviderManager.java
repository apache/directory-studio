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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.swt.widgets.Composite;


public class ValueProviderManager
{

    private Composite parent;

    private ValueProvider userSelectedValueProvider;

    private MultivaluedValueProvider multiValuedValueProvider;

    private InPlaceTextValueProvider defaultStringSingleLineValueProvider;

    private TextValueProvider defaultStringMultiLineValueProvider;

    private HexValueProvider defaultBinaryValueProvider;

    private Map class2ValueProviderMap;


    public ValueProviderManager( Composite parent )
    {
        this.parent = parent;

        this.userSelectedValueProvider = null;
        this.multiValuedValueProvider = new MultivaluedValueProvider( this.parent, this );
        this.defaultStringSingleLineValueProvider = new InPlaceTextValueProvider( this.parent );
        this.defaultStringMultiLineValueProvider = new TextValueProvider( this.parent );
        this.defaultBinaryValueProvider = new HexValueProvider( this.parent );

        this.class2ValueProviderMap = new HashMap();
        ValueProvider[] valueProviders = getValueProviders( parent );
        for ( int i = 0; i < valueProviders.length; i++ )
        {
            this.class2ValueProviderMap.put( valueProviders[i].getClass().getName(), valueProviders[i] );
        }

    }


    public void dispose()
    {
        if ( this.parent != null )
        {
            this.userSelectedValueProvider = null;
            this.multiValuedValueProvider.dispose();
            this.defaultStringSingleLineValueProvider.dispose();
            this.defaultStringMultiLineValueProvider.dispose();
            this.defaultBinaryValueProvider.dispose();

            for ( Iterator it = this.class2ValueProviderMap.values().iterator(); it.hasNext(); )
            {
                ValueProvider vp = ( ValueProvider ) it.next();
                vp.dispose();
            }

            this.parent = null;
        }
    }


    public void setUserSelectedValueProvider( ValueProvider userSelectedValueProvider )
    {
        this.userSelectedValueProvider = userSelectedValueProvider;
    }


    public ValueProvider getCurrentValueProvider( Schema schema, String attributeName )
    {

        // check attribute oid and names
        AttributeTypeDescription atd = schema.getAttributeTypeDescription( attributeName );
        Map attributeValueProviderMap = BrowserUIPlugin.getDefault().getUIPreferences().getAttributeValueProviderMap();
        if ( atd.getNumericOID() != null && attributeValueProviderMap.containsKey( atd.getNumericOID().toLowerCase() ) )
        {
            return ( ValueProvider ) this.class2ValueProviderMap.get( attributeValueProviderMap.get( atd
                .getNumericOID().toLowerCase() ) );
        }
        String[] names = atd.getNames();
        for ( int i = 0; i < names.length; i++ )
        {
            if ( attributeValueProviderMap.containsKey( names[i].toLowerCase() ) )
            {
                return ( ValueProvider ) this.class2ValueProviderMap.get( attributeValueProviderMap.get( names[i]
                    .toLowerCase() ) );
            }
        }

        // check syntax
        LdapSyntaxDescription lsd = atd.getSyntaxDescription();
        if ( lsd != null )
        {
            Map syntaxValueProviderMap = BrowserUIPlugin.getDefault().getUIPreferences().getSyntaxValueProviderMap();
            if ( lsd.getNumericOID() != null && syntaxValueProviderMap.containsKey( lsd.getNumericOID().toLowerCase() ) )
            {
                return ( ValueProvider ) this.class2ValueProviderMap.get( syntaxValueProviderMap.get( lsd
                    .getNumericOID().toLowerCase() ) );
            }
            else if ( lsd.isBinary() )
            {
                return this.defaultBinaryValueProvider;
            }
            else if ( lsd.isString() )
            {
                return this.defaultStringSingleLineValueProvider;
            }
        }

        // 
        return null;

    }


    public ValueProvider getCurrentValueProvider( IEntry entry, String attributeName )
    {

        // check user-selected (forced) value provider
        if ( this.userSelectedValueProvider != null )
        {
            return this.userSelectedValueProvider;
        }

        return getCurrentValueProvider( entry.getConnection().getSchema(), attributeName );

    }


    public ValueProvider getCurrentValueProvider( IValue value )
    {

        ValueProvider vp = this.getCurrentValueProvider( value.getAttribute().getEntry(), value.getAttribute()
            .getDescription() );

        if ( vp == this.defaultStringSingleLineValueProvider )
        {
            if ( value.getStringValue().indexOf( '\n' ) == -1 && value.getStringValue().indexOf( '\r' ) == -1 )
            {
                vp = this.defaultStringSingleLineValueProvider;
            }
            else
            /*
             * if(value.getStringValue().indexOf('\n')>-1 ||
             * value.getStringValue().indexOf('\r')>-1)
             */{
                vp = this.defaultStringMultiLineValueProvider;
            }
        }

        return vp;
    }


    public ValueProvider getCurrentValueProvider( AttributeHierachie ah )
    {
        if ( ah == null )
        {
            return null;
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            return this.getCurrentValueProvider( ah.getAttribute().getEntry(), ah.getAttribute().getDescription() );
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {

            if ( ah.getAttribute().isObjectClassAttribute() )
            {
                return this.multiValuedValueProvider;
            }
            if ( ah.getAttribute().getValues()[0].isRdnPart() )
            {
                return this.multiValuedValueProvider;
            }

            return this.getCurrentValueProvider( ah.getAttribute().getValues()[0] );
        }
        else
        /* if(attribute.getValueSize() > 1) */{
            return this.multiValuedValueProvider;
        }
    }


    public ValueProvider[] getAlternativeValueProvider( IEntry entry, String attributeName )
    {

        Schema schema = entry.getConnection().getSchema();
        return getAlternativeValueProvider( schema, attributeName );

    }


    public ValueProvider[] getAlternativeValueProvider( Schema schema, String attributeName )
    {
        List alternativeList = new ArrayList();

        AttributeTypeDescription atd = schema.getAttributeTypeDescription( attributeName );

        if ( atd.getSyntaxDescription().isBinary() )
        {
            alternativeList.add( this.defaultBinaryValueProvider );
            alternativeList.add( this.defaultStringSingleLineValueProvider );
            alternativeList.add( this.defaultStringMultiLineValueProvider );
        }
        else if ( atd.getSyntaxDescription().isString() )
        {
            alternativeList.add( this.defaultStringSingleLineValueProvider );
            alternativeList.add( this.defaultStringMultiLineValueProvider );
            alternativeList.add( this.defaultBinaryValueProvider );
        }

        alternativeList.add( this.multiValuedValueProvider );

        alternativeList.remove( getCurrentValueProvider( schema, attributeName ) );

        return ( ValueProvider[] ) alternativeList.toArray( new ValueProvider[alternativeList.size()] );
    }


    public ValueProvider[] getAlternativeValueProvider( IValue value )
    {

        List alternativeList = new ArrayList();

        if ( value.isBinary() )
        {
            alternativeList.add( this.defaultBinaryValueProvider );
            alternativeList.add( this.defaultStringSingleLineValueProvider );
            alternativeList.add( this.defaultStringMultiLineValueProvider );
        }
        else if ( value.isString() )
        {
            alternativeList.add( this.defaultStringSingleLineValueProvider );
            alternativeList.add( this.defaultStringMultiLineValueProvider );
            alternativeList.add( this.defaultBinaryValueProvider );
        }

        alternativeList.add( this.multiValuedValueProvider );

        alternativeList.remove( getCurrentValueProvider( value ) );

        return ( ValueProvider[] ) alternativeList.toArray( new ValueProvider[alternativeList.size()] );
    }


    public ValueProvider[] getAlternativeValueProvider( AttributeHierachie ah )
    {
        if ( ah == null )
        {
            return new ValueProvider[0];
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            return this.getAlternativeValueProvider( ah.getAttribute().getEntry(), ah.getAttribute().getDescription() );
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {

            if ( ah.getAttribute().isObjectClassAttribute() )
            {
                return new ValueProvider[0];
            }
            if ( ah.getAttribute().getValues()[0].isRdnPart() )
            {
                return new ValueProvider[0];
            }

            return this.getAlternativeValueProvider( ah.getAttribute().getValues()[0] );
        }
        else
        /* if(attribute.getValueSize() > 1) */{
            return new ValueProvider[0];
        }
    }


    public ValueProvider[] getAllValueProviders()
    {
        Set list = new LinkedHashSet();
        list.add( this.defaultStringSingleLineValueProvider );
        list.add( this.defaultStringMultiLineValueProvider );
        list.add( defaultBinaryValueProvider );
        list.addAll( this.class2ValueProviderMap.values() );
        list.add( this.multiValuedValueProvider );
        return ( ValueProvider[] ) list.toArray( new ValueProvider[list.size()] );
    }


    public ValueProvider getDefaultBinaryValueProvider()
    {
        return defaultBinaryValueProvider;
    }


    public ValueProvider getDefaultStringValueProvider()
    {
        return defaultStringSingleLineValueProvider;
    }


    public MultivaluedValueProvider getMultiValuedValueProvider()
    {
        return multiValuedValueProvider;
    }


    public static ValueProvider[] getValueProviders( Composite parent )
    {
        List vpList = new ArrayList();

        vpList.add( new InPlaceTextValueProvider( parent ) );

        vpList.add( new TextValueProvider( parent ) );
        vpList.add( new AddressValueProvider( parent ) );
        vpList.add( new HexValueProvider( parent ) );
        vpList.add( new ImageValueProvider( parent ) );
        vpList.add( new DnValueProvider( parent ) );
        vpList.add( new PasswordValueProvider( parent ) );
        vpList.add( new ObjectClassValueProvider( parent ) );
        vpList.add( new InPlaceGeneralizedTimeValueProvider( parent ) );
        // vpList.add(new InPlaceObjectClassValueProvider(parent));

        return ( ValueProvider[] ) vpList.toArray( new ValueProvider[vpList.size()] );
    }

}
