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

import org.apache.directory.ldapstudio.browser.common.actions.BrowserAction;
import org.apache.directory.ldapstudio.browser.common.actions.CopyAction;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This class implements the Copy Attribute Description Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyAttributeDescriptionAction extends BrowserAction
{

    /**
     * Creates a new instance of CopyAttributeDescriptionAction.
     */
    public CopyAttributeDescriptionAction()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        StringBuffer text = new StringBuffer();
        for ( Iterator iterator = getAttributeNameSet().iterator(); iterator.hasNext(); )
        {
            text.append( iterator.next() );
            if ( iterator.hasNext() )
                text.append( BrowserCoreConstants.LINE_SEPARATOR );
        }

        if ( text.length() > 0 )
        {
            CopyAction.copyToClipboard( new Object[]
                { text.toString() }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
    }


    /**
     * Gets a Set containing all the Attribute Names.
     *
     * @return
     *      a Set containing all the Attribute Names
     */
    private Set getAttributeNameSet()
    {
        Set<String> attributeNameSet = new LinkedHashSet<String>();
        for ( int i = 0; i < getSelectedAttributeHierarchies().length; i++ )
        {
            for ( Iterator it = getSelectedAttributeHierarchies()[i].iterator(); it.hasNext(); )
            {
                IAttribute att = ( IAttribute ) it.next();
                attributeNameSet.add( att.getDescription() );
            }
        }
        for ( int i = 0; i < getSelectedAttributes().length; i++ )
        {
            attributeNameSet.add( getSelectedAttributes()[i].getDescription() );
        }
        for ( int i = 0; i < getSelectedValues().length; i++ )
        {
            attributeNameSet.add( getSelectedValues()[i].getAttribute().getDescription() );
        }
        return attributeNameSet;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getAttributeNameSet().size() > 1 )
        {
            return "Copy Attribute Descriptions";
        }
        else
        {
            return "Copy Attribute Description";
        }
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_ATT );
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
        return getAttributeNameSet().size() > 0;
    }
}
