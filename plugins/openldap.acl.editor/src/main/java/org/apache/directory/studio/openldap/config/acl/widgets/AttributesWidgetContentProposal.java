/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets;


import org.eclipse.jface.fieldassist.ContentProposal;


/**
 * An abstract content proposal for the attributes widget.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AttributesWidgetContentProposal extends ContentProposal
{
    /** The start position */
    private int startPosition = 0;


    /**
     * Creates a new instance of AttributeWidgetContentProposal.
     *
     * @param content the String representing the content. Should not be <code>null</code>.
     */
    public AttributesWidgetContentProposal( String content )
    {
        super( content );
    }


    /**
     * @return the startPosition
     */
    public int getStartPosition()
    {
        return startPosition;
    }


    /**
     * @param startPosition the startPosition to set
     */
    public void setStartPosition( int startPosition )
    {
        this.startPosition = startPosition;
    }


    /**
     * {@inheritDoc}
     */
    public String getContent()
    {
        String content = super.getContent();
        if ( content != null )
        {
            return content.substring( startPosition );
        }

        return null;
    }
}
