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

package org.apache.directory.ldapstudio.browser.ui.actions;


import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.apache.directory.ldapstudio.browser.core.utils.ModelConverter;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


public class CopyValueAction extends BrowserAction
{

    public static final int MODE_UTF8 = 1;

    public static final int MODE_BASE64 = 2;

    public static final int MODE_HEX = 3;

    public static final int MODE_LDIF = 4;

    private int mode;


    public CopyValueAction( int mode )
    {
        this.mode = mode;
    }


    public String getText()
    {
        if ( mode == MODE_UTF8 )
        {
            return getValueSet().size() > 1 ? "Copy Values (UTF-8)" : "Copy Value (UTF-8)";
        }
        else if ( mode == MODE_BASE64 )
        {
            return getValueSet().size() > 1 ? "Copy Values (BASE-64)" : "Copy Value (BASE-64)";
        }
        else if ( mode == MODE_HEX )
        {
            return getValueSet().size() > 1 ? "Copy Values (HEX)" : "Copy Value (HEX)";
        }
        else if ( mode == MODE_LDIF )
        {
            return getValueSet().size() > 1 ? "Copy Name-Value-Pairs as LDIF" : "Copy Name-Value-Pair as LDIF";
        }
        else
        {
            return "Copy Value";
        }
    }


    public ImageDescriptor getImageDescriptor()
    {
        if ( mode == MODE_UTF8 )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_UTF8 );
        }
        else if ( mode == MODE_BASE64 )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_BASE64 );
        }
        else if ( mode == MODE_HEX )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_HEX );
        }
        else if ( mode == MODE_LDIF )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF );
        }
        else
        {
            return null;
        }
    }


    public String getCommandId()
    {
        return null;
    }


    public boolean isEnabled()
    {
        return getValueSet().size() > 0 || getSelectedSearchResults().length > 0;
    }


    public void run()
    {

        StringBuffer text = new StringBuffer();
        Set valueSet = getValueSet();
        if ( !valueSet.isEmpty() )
        {
            for ( Iterator iterator = valueSet.iterator(); iterator.hasNext(); )
            {
                IValue value = ( IValue ) iterator.next();

                if ( mode == MODE_UTF8 )
                {
                    text.append( LdifUtils.utf8decode( value.getBinaryValue() ) );
                    if ( iterator.hasNext() )
                        text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
                else if ( mode == MODE_BASE64 )
                {
                    text.append( LdifUtils.base64encode( value.getBinaryValue() ) );
                    if ( iterator.hasNext() )
                        text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
                else if ( mode == MODE_HEX )
                {
                    text.append( LdifUtils.hexEncode( value.getBinaryValue() ) );
                    if ( iterator.hasNext() )
                        text.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
                else if ( mode == MODE_LDIF )
                {
                    text.append( ModelConverter.valueToLdifAttrValLine( value ).toFormattedString() );
                }

            }
        }
        else if ( getSelectedSearchResults().length > 0 )
        {
            DN dn = getSelectedSearchResults()[0].getDn();

            if ( mode == MODE_UTF8 )
            {
                text.append( LdifUtils.utf8decode( dn.toString().getBytes() ) );
            }
            else if ( mode == MODE_BASE64 )
            {
                text.append( LdifUtils.base64encode( dn.toString().getBytes() ) );
            }
            else if ( mode == MODE_HEX )
            {
                text.append( LdifUtils.hexEncode( dn.toString().getBytes() ) );
            }
            else if ( mode == MODE_LDIF )
            {
                text.append( ModelConverter.dnToLdifDnLine( dn ).toFormattedString() );
            }
        }

        if ( text.length() > 0 )
        {
            CopyAction.copyToClipboard( new Object[]
                { text.toString() }, new Transfer[]
                { TextTransfer.getInstance() } );
        }

    }


    protected Set getValueSet()
    {
        Set valueSet = new LinkedHashSet();
        for ( int i = 0; i < getSelectedAttributeHierarchies().length; i++ )
        {
            for ( Iterator it = getSelectedAttributeHierarchies()[i].iterator(); it.hasNext(); )
            {
                IAttribute att = ( IAttribute ) it.next();
                valueSet.addAll( Arrays.asList( att.getValues() ) );
            }
        }
        for ( int i = 0; i < getSelectedAttributes().length; i++ )
        {
            valueSet.addAll( Arrays.asList( getSelectedAttributes()[i].getValues() ) );
        }
        valueSet.addAll( Arrays.asList( getSelectedValues() ) );
        return valueSet;
    }

}
