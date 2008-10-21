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

package org.apache.directory.studio.ldifeditor.editor.text;


import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEOFPart;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifFile;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifInvalidPart;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifPart;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifLineBase;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifSepLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifValueLineBase;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.parser.LdifParser;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;


public class LdifDoubleClickStrategy implements ITextDoubleClickStrategy
{

    private static final int OFFSET = 0;

    private static final int LENGTH = 1;

    /**
     * Default double click strategy
     */
    private DefaultTextDoubleClickStrategy delegateDoubleClickStrategy;


    public LdifDoubleClickStrategy()
    {
        this.delegateDoubleClickStrategy = new DefaultTextDoubleClickStrategy();
    }


    public void doubleClicked( ITextViewer viewer )
    {

        if ( !LdifEditorActivator.getDefault().getPreferenceStore().getBoolean(
            LdifEditorConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK ) )
        {
            delegateDoubleClickStrategy.doubleClicked( viewer );
        }
        else
        {

            int cursorPos = viewer.getSelectedRange().x;
            if ( cursorPos < 0 )
            {
                return;
            }

            try
            {
                LdifParser parser = new LdifParser();
                IDocument document = viewer.getDocument();
                ITypedRegion partition = document.getPartition( cursorPos );

                // now use position relative to partition
                int offset = partition.getOffset();
                int relativePos = cursorPos - offset;

                // parse partition
                String s = document.get( partition.getOffset(), partition.getLength() );
                LdifFile model = parser.parse( s );
                LdifContainer container = LdifFile.getContainer( model, relativePos );
                if ( container != null )
                {
                    LdifPart part = LdifFile.getContainerContent( container, relativePos );

                    if ( part != null && !( part instanceof LdifSepLine ) && !( part instanceof LdifInvalidPart )
                        && !( part instanceof LdifEOFPart ) )
                    {

                        // calculate selected range
                        int[] range = null;
                        if ( part instanceof LdifValueLineBase )
                        {
                            LdifValueLineBase line = ( LdifValueLineBase ) part;
                            range = getRange( relativePos, part.getOffset(), new String[]
                                { line.getRawLineStart(), line.getRawValueType(), line.getRawValue() } );
                        }
                        else if ( part instanceof LdifLineBase )
                        {
                            LdifLineBase line = ( LdifLineBase ) part;
                            range = new int[]
                                { part.getOffset(), part.getLength() - line.getRawNewLine().length() };
                        }

                        // set range on viewer, add global offset
                        int start = range != null ? range[OFFSET] : part.getOffset();
                        start += offset;
                        int length = range != null ? range[LENGTH] : part.getLength();
                        viewer.setSelectedRange( start, length );
                    }
                    else
                    {
                        // use default double click strategy
                        delegateDoubleClickStrategy.doubleClicked( viewer );
                    }
                }

            }
            catch ( BadLocationException e )
            {
                e.printStackTrace();
            }
        }
    }


    private int[] getRange( int pos, int offset, String[] parts )
    {

        for ( int i = 0; i < parts.length; i++ )
        {
            if ( parts[i] != null )
            {
                if ( pos < offset + parts[i].length() )
                {
                    return new int[]
                        { offset, parts[i].length() };
                }
                offset += parts[i].length();
            }
        }
        return null;
    }

}
