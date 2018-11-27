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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.common.actions.CopyAction;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This class implements the Copy Attribute Description Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
        boolean isFirst = true;
        
        for ( String attributeName : getAttributeNameSet() )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                text.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            text.append( attributeName );
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
     * @return a Set containing all the Attribute Names
     */
    private Set<String> getAttributeNameSet()
    {
        Set<String> attributeNameSet = new LinkedHashSet<String>();
        
        for ( AttributeHierarchy attributeHierarchy : getSelectedAttributeHierarchies() )
        {
            for ( IAttribute attribute : attributeHierarchy )
            {
                attributeNameSet.add( attribute.getDescription() );
            }
        }
        
        for ( IAttribute attribute : getSelectedAttributes() )
        {
            attributeNameSet.add( attribute.getDescription() );
        }
        
        for ( IValue value : getSelectedValues() )
        {
            attributeNameSet.add( value.getAttribute().getDescription() );
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
            return Messages.getString( "CopyAttributeDescriptionAction.CopyAttributeDescriptions" ); //$NON-NLS-1$
        }
        else
        {
            return Messages.getString( "CopyAttributeDescriptionAction.CopyAttributeDescription" ); //$NON-NLS-1$
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
