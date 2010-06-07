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
package org.apache.directory.studio.connection.ui.widgets;


import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;


/**
 * The ExtendedContentAssistCommandAdapter extends the ContentAssistCommandAdapter
 * and provides public {@link #closeProposalPopup()} and {@link #openProposalPopup()}
 * methods for more controls when proposal popup is opened and closed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedContentAssistCommandAdapter extends ContentAssistCommandAdapter
{

    /**
     * Creates a new instance of ExtendedContentAssistCommandAdapter 
     * with the following settings:
     * <ul>
     * <li>setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE )</li>
     * <li>setFilterStyle( ContentProposalAdapter.FILTER_NONE )</li>
     * <li>setAutoActivationCharacters( null )</li>
     * <li>setAutoActivationDelay( 0 )</li>
     * </ul>
     * 
     * @param control the control
     * @param controlContentAdapter the control content adapter
     * @param proposalProvider the proposal provider
     * @param commandId the command id
     * @param autoActivationCharacters the auto activation characters
     * @param installDecoration the install decoration
     */
    public ExtendedContentAssistCommandAdapter( Control control, IControlContentAdapter controlContentAdapter,
        IContentProposalProvider proposalProvider, String commandId, char[] autoActivationCharacters,
        boolean installDecoration )
    {
        super( control, controlContentAdapter, proposalProvider, commandId, autoActivationCharacters, installDecoration );

        setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );
        setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        setAutoActivationCharacters( null );
        setAutoActivationDelay( 0 );
    }


    @Override
    public void closeProposalPopup()
    {
        super.closeProposalPopup();
    }


    @Override
    public void openProposalPopup()
    {
        super.openProposalPopup();
    }

}
