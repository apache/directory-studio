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


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.LdapFilterUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This Action copies the Search Filter
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopySearchFilterAction extends BrowserAction
{

    /**
     * Equals Mode.
     */
    public static final int MODE_EQUALS = 0;

    /**
     * Not Mode.
     */
    public static final int MODE_NOT = 1;

    /**
     * And Mode.
     */
    public static final int MODE_AND = 2;

    /**
     * Or Mode.
     */
    public static final int MODE_OR = 3;

    private int mode;


    /**
     * Creates a new instance of CopySearchFilterAction.
     *
     * @param mode
     *      the copy Mode
     */
    public CopySearchFilterAction( int mode )
    {
        this.mode = mode;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( mode == MODE_EQUALS )
        {
            return "Copy Search Filter";
        }
        else if ( mode == MODE_NOT )
        {
            return "Copy NOT Search Filter";
        }
        else if ( mode == MODE_AND )
        {
            return "Copy AND Search Filter";
        }
        else if ( mode == MODE_OR )
        {
            return "Copy OR Search Filter";
        }
        else
        {
            return "Copy Search Filter";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( mode == MODE_EQUALS )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_EQUALS );
        }
        else if ( mode == MODE_NOT )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_NOT );
        }
        else if ( mode == MODE_AND )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_AND );
        }
        else if ( mode == MODE_OR )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_OR );
        }
        else
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_FILTER_EQUALS );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( mode == MODE_EQUALS || mode == MODE_NOT )
        {
            return getSelectedAttributeHierarchies().length + getSelectedAttributes().length
                + getSelectedValues().length == 1
                && ( getSelectedValues().length == 1
                    || ( getSelectedAttributes().length == 1 && getSelectedAttributes()[0].getValueSize() == 1 ) || ( getSelectedAttributeHierarchies().length == 1
                    && getSelectedAttributeHierarchies()[0].size() == 1 && getSelectedAttributeHierarchies()[0]
                    .getAttribute().getValueSize() == 1 ) );
        }
        else if ( mode == MODE_AND || mode == MODE_OR )
        {
            return getSelectedAttributeHierarchies().length + getSelectedAttributes().length
                + getSelectedValues().length > 0;
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {

        String filter = null;

        if ( mode == MODE_EQUALS )
        {
            filter = getFilter( null );
        }
        else if ( mode == MODE_NOT )
        {
            filter = getFilter( "!" );
        }
        else if ( mode == MODE_AND )
        {
            filter = getFilter( "&" );
        }
        else if ( mode == MODE_OR )
        {
            filter = getFilter( "|" );
        }

        if ( filter != null && filter.length() > 0 )
        {
            CopyAction.copyToClipboard( new Object[]
                { filter }, new Transfer[]
                { TextTransfer.getInstance() } );
        }

    }


    /**
     * Gets the filter
     *
     * @param filterType
     *      the filter type
     * @return
     *      the filter
     */
    private String getFilter( String filterType )
    {
        Set filterSet = new LinkedHashSet();
        for ( int i = 0; i < getSelectedAttributeHierarchies().length; i++ )
        {
            for ( Iterator it = getSelectedAttributeHierarchies()[i].iterator(); it.hasNext(); )
            {
                IAttribute att = ( IAttribute ) it.next();
                IValue[] values = att.getValues();
                for ( int v = 0; v < values.length; v++ )
                {
                    filterSet.add( LdapFilterUtils.getFilter( values[v] ) );
                }
            }
        }
        for ( int a = 0; a < getSelectedAttributes().length; a++ )
        {
            IValue[] values = getSelectedAttributes()[a].getValues();
            for ( int v = 0; v < values.length; v++ )
            {
                filterSet.add( LdapFilterUtils.getFilter( values[v] ) );
            }
        }
        for ( int v = 0; v < getSelectedValues().length; v++ )
        {
            filterSet.add( LdapFilterUtils.getFilter( getSelectedValues()[v] ) );
        }

        StringBuffer filter = new StringBuffer();
        if ( filterType != null )
        {
            filter.append( "(" );
            filter.append( filterType );
            for ( Iterator filterIterator = filterSet.iterator(); filterIterator.hasNext(); )
            {
                filter.append( filterIterator.next() );
            }
            filter.append( ")" );
        }
        else if ( filterSet.size() == 1 )
        {
            filter.append( filterSet.toArray()[0] );
        }

        return filter.toString();
    }
}
