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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.model.overlay.OlcSyncProvConfig;


/**
 * This class implements a block for the configuration of the Audit Log overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncProvOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcSyncProvConfig>
{
    // UI widgets
    private Text checkpointOperationsText;
    private Text checkpointMinutesText;
    private Text sessionLogOperationsText;
    private Button skipPresentPhaseButton;
    private Button honorReloadHintFlagButton;


    public SyncProvOverlayConfigurationBlock( OverlayDialog dialog )
    {
        super( dialog );
        setOverlay( new OlcSyncProvConfig() );
    }


    public SyncProvOverlayConfigurationBlock( OverlayDialog dialog, OlcSyncProvConfig overlay )
    {
        super( dialog );
        
        if ( overlay == null )
        {
            setOverlay( new OlcSyncProvConfig() );
        }
        else
        {
            setOverlay( overlay );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        // Checkpoint
        Composite checkpointComposite = BaseWidgetUtils.createColumnContainer( composite, 5, 1 );
        BaseWidgetUtils.createLabel( checkpointComposite, "New checkpoint after", 1 );
        checkpointOperationsText = createIntegerText( checkpointComposite, "", 1 );
        BaseWidgetUtils.createLabel( checkpointComposite, "operations or", 1 );
        checkpointMinutesText = createIntegerText( checkpointComposite, "", 1 );
        BaseWidgetUtils.createLabel( checkpointComposite, "minutes", 1 );

        // Session Log
        Composite sessionLogComposite = BaseWidgetUtils.createColumnContainer( composite, 3, 1 );
        BaseWidgetUtils.createLabel( sessionLogComposite, "Session log holds", 1 );
        sessionLogOperationsText = createIntegerText( sessionLogComposite, "", 1 );
        BaseWidgetUtils.createLabel( sessionLogComposite, "operations", 1 );

        // No Present
        skipPresentPhaseButton = BaseWidgetUtils.createCheckbox( composite, "Skip Present Phase", 1 );

        // Reload Hint
        honorReloadHintFlagButton = BaseWidgetUtils.createCheckbox( composite, "Honor Reload Hint flag", 1 );
    }


    /**
     * Create a Text widget only accepting integers.
     *
     * @param parent the parent
     * @param text the initial text
     * @param span the horizontal span
     * @return a Text widget only accepting integers
     */
    private Text createIntegerText( Composite parent, String text, int span )
    {
        Text integerText = BaseWidgetUtils.createText( parent, text, span );

        integerText.addVerifyListener(  event ->
            {
                if ( !event.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    event.doit = false;
                }
            } );

        GridData gd = new GridData();
        gd.widthHint = 40;
        integerText.setLayoutData( gd );

        return integerText;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            // Checkpoint
            String checkpointConfiguration = overlay.getOlcSpCheckpoint();

            if ( ( checkpointConfiguration != null ) && ( !checkpointConfiguration.isEmpty() ) )
            {
                String[] checkpointConfigurationElements = checkpointConfiguration.split( " " );

                if ( checkpointConfigurationElements.length == 2 )
                {
                    // Checkpoint Operations
                    try
                    {

                        int checkpointOperations = Integer.parseInt( checkpointConfigurationElements[0] );
                        checkpointOperationsText.setText( Integer.toString( checkpointOperations ) );
                    }
                    catch ( NumberFormatException e )
                    {
                        // TODO
                        checkpointOperationsText.setText( "" );
                    }

                    // Checkpoint Minutes
                    try
                    {

                        int checkpointMinutes = Integer.parseInt( checkpointConfigurationElements[1] );
                        checkpointMinutesText.setText( Integer.toString( checkpointMinutes ) );
                    }
                    catch ( NumberFormatException e )
                    {
                        // TODO
                        checkpointMinutesText.setText( "" );
                    }
                }
                else
                {
                    // TODO
                    checkpointOperationsText.setText( "" );
                    checkpointMinutesText.setText( "" );
                }
            }
            else
            {
                // TODO
                checkpointOperationsText.setText( "" );
                checkpointMinutesText.setText( "" );
            }

            // Session Log
            Integer sessionLogOperations = overlay.getOlcSpSessionlog();

            if ( sessionLogOperations != null )
            {
                sessionLogOperationsText.setText( "" + sessionLogOperations );
            }
            else
            {
                // TODO
                sessionLogOperationsText.setText( "" );
            }

            // No Present
            skipPresentPhaseButton.setSelection( overlay.getOlcSpNoPresent() );

            // Reload Hint
            honorReloadHintFlagButton.setSelection( overlay.getOlcSpReloadHint() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            // Checkpoint
            String checkpointOperations = checkpointOperationsText.getText();
            String checkpointMinutes = checkpointMinutesText.getText();

            if ( ( checkpointOperations != null ) && ( !checkpointOperations.isEmpty() )
                && ( checkpointMinutes != null ) && ( !checkpointMinutes.isEmpty() ) )
            {
                overlay.setOlcSpCheckpoint( checkpointOperations + " " + checkpointMinutes );
            }
            else
            {
                overlay.setOlcSpCheckpoint( null );
            }

            // Session Log
            String sessionLogOperations = sessionLogOperationsText.getText();

            if ( ( sessionLogOperations != null ) && ( !sessionLogOperations.isEmpty() ) )
            {
                try
                {
                    overlay.setOlcSpSessionlog( Integer.parseInt( sessionLogOperations ) );
                }
                catch ( NumberFormatException e )
                {
                    overlay.setOlcSpSessionlog( null );
                }
            }
            else
            {
                overlay.setOlcSpSessionlog( null );
            }

            // No Present
            overlay.setOlcSpNoPresent( skipPresentPhaseButton.getSelection() );

            // Reload Hint
            overlay.setOlcSpReloadHint( honorReloadHintFlagButton.getSelection() );
        }
    }
}
