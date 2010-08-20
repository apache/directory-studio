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

package org.apache.directory.studio.ldapbrowser.common.widgets;


import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;


/**
 * The DialogContentAssistant is used to provide content assist and 
 * a proposal popup within a SWT {@link Text}, {@link Combo} or
 * {@link ITextViewer}. 
 * 
 * It provides a special handling of ESC keystrokes: 
 * When the proposal popup is shown ESC is catched and the popup is closed.
 * This ensures that a dialog isn't closed on a ESC keystroke
 * while the proposal popup is opened. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DialogContentAssistant /*extends SubjectControlContentAssistant implements FocusListener
*/{
//
//    /** The control */
//    private Control control;
//
//    /** The handler activation. */
//    private IHandlerActivation handlerActivation;
//
//    /** The possible completions visible. */
//    private boolean possibleCompletionsVisible;
//
//
//    /**
//     * Creates a new instance of DialogContentAssistant.
//     */
//    public DialogContentAssistant()
//    {
//        this.possibleCompletionsVisible = false;
//    }
//
//
//    /**
//     * Installs content assist on the given text.
//     *
//     * @param text the text
//     */
//    public void install( Text text )
//    {
//        control = text;
//        control.addFocusListener( this );
//        super.install( new TextContentAssistSubjectAdapter( text ) );
//    }
//
//
//    /**
//     * Installs content assist on the given combo.
//     *
//     * @param combo the combo
//     */
//    public void install( Combo combo )
//    {
//        control = combo;
//        control.addFocusListener( this );
//        super.install( new ComboContentAssistSubjectAdapter( combo ) );
//    }
//
//
//    /**
//     * Installs content assist on the given text viewer.
//     *
//     * @param viewer the text viewer
//     */
//    public void install( ITextViewer viewer )
//    {
//        control = viewer.getTextWidget();
//        control.addFocusListener( this );
//
//        // stop traversal (ESC) if popup is shown
//        control.addTraverseListener( new TraverseListener()
//        {
//            public void keyTraversed( TraverseEvent e )
//            {
//                if ( possibleCompletionsVisible )
//                {
//                    e.doit = false;
//                }
//            }
//        } );
//
//        super.install( viewer );
//    }
//
//
//    /**
//     * Uninstalls content assist on the control.
//     */
//    public void uninstall()
//    {
//        if ( handlerActivation != null )
//        {
//            IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
//                IHandlerService.class );
//            handlerService.deactivateHandler( handlerActivation );
//            handlerActivation = null;
//        }
//
//        if ( control != null )
//        {
//            control.removeFocusListener( this );
//        }
//
//        super.uninstall();
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    protected Point restoreCompletionProposalPopupSize()
//    {
//        possibleCompletionsVisible = true;
//        return super.restoreCompletionProposalPopupSize();
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    public String showPossibleCompletions()
//    {
//        possibleCompletionsVisible = true;
//        return super.showPossibleCompletions();
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    protected void possibleCompletionsClosed()
//    {
//        possibleCompletionsVisible = false;
//        super.possibleCompletionsClosed();
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    public void focusGained( FocusEvent e )
//    {
//        IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
//            IHandlerService.class );
//        if ( handlerService != null )
//        {
//            IHandler handler = new org.eclipse.core.commands.AbstractHandler()
//            {
//                public Object execute( ExecutionEvent event ) throws org.eclipse.core.commands.ExecutionException
//                {
//                    showPossibleCompletions();
//                    return null;
//                }
//            };
//            handlerActivation = handlerService.activateHandler(
//                ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, handler );
//        }
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    public void focusLost( FocusEvent e )
//    {
//        if ( handlerActivation != null )
//        {
//            IHandlerService handlerService = ( IHandlerService ) PlatformUI.getWorkbench().getAdapter(
//                IHandlerService.class );
//            handlerService.deactivateHandler( handlerActivation );
//            handlerActivation = null;
//        }
//    }

}
