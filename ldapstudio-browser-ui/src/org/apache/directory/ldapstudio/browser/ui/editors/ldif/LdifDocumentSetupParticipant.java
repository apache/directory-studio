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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif;


import org.apache.directory.ldapstudio.browser.ui.editors.ldif.text.LdifPartitionScanner;
import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;


public class LdifDocumentSetupParticipant implements IDocumentSetupParticipant
{

    public final static String LDIF_PARTITIONING = "new.sf.browser.ui.editors.ldif.LdifPartitioning"; //$NON-NLS-1$


    public LdifDocumentSetupParticipant()
    {
    }


    public void setup( IDocument document )
    {

        if ( document instanceof IDocumentExtension3 )
        {
            IDocumentExtension3 extension3 = ( IDocumentExtension3 ) document;
            if ( extension3.getDocumentPartitioner( LdifDocumentSetupParticipant.LDIF_PARTITIONING ) == null )
            {
                IDocumentPartitioner partitioner = createDocumentPartitioner();
                extension3.setDocumentPartitioner( LDIF_PARTITIONING, partitioner );
                partitioner.connect( document );
            }
        }
    }


    private IDocumentPartitioner createDocumentPartitioner()
    {
        IDocumentPartitioner partitioner = new FastPartitioner( new LdifPartitionScanner(), new String[]
            { LdifPartitionScanner.LDIF_RECORD } );
        return partitioner;
    }

}
