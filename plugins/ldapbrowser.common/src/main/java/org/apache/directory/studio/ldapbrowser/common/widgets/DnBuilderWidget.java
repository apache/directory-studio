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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.EntryWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The DnBuilderWidget provides input elements to select a parent Dn
 * and to build a (multivalued) Rdn.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnBuilderWidget extends BrowserWidget implements ModifyListener
{

    /** The attribute names that could be selected from drop-down list. */
    private String[] attributeNames;

    /** The initial Rdn. */
    private Rdn currentRdn;

    /** The initial parent Dn. */
    private Dn currentParentDn;

    /** True if the Rdn input elements should be shown. */
    private boolean showRDN;

    /** True if the parent Dn input elements should be shown. */
    private boolean showParent;

    /** The shell. */
    private Shell shell;

    /** The selected parent Dn. */
    private Dn parentDn;

    /** The entry widget label. */
    private Label parentEntryLabel;

    /** The entry widget to enter/select the parent Dn. */
    private EntryWidget parentEntryWidget;

    /** The Rdn label */
    private Label rdnLabel;

    /** The composite that contains the RdnLines. */
    private Composite rdnComposite;

    /** The resulting Rdn. */
    private Rdn rdn;

    /** The list of RdnLines. */
    private ArrayList<RdnLine> rdnLineList;

    /** The preview label. */
    private Label previewLabel;

    /** The preview text. */
    private Text previewText;

    // Listeners
    private SelectionListener rdnAddButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            int index = rdnLineList.size();
            for ( int i = 0; i < rdnLineList.size(); i++ )
            {
                RdnLine rdnLine = rdnLineList.get( i );
                if ( rdnLine.rdnAddButton == e.widget )
                {
                    index = i + 1;
                }
            }
            addRdnLine( rdnComposite, index );

            validate();
        }
    };

    private SelectionListener rdnDeleteButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            int index = 0;
            for ( int i = 0; i < rdnLineList.size(); i++ )
            {
                RdnLine rdnLine = rdnLineList.get( i );
                if ( rdnLine.rdnDeleteButton == e.widget )
                {
                    index = i;
                }
            }
            deleteRdnLine( rdnComposite, index );

            validate();
        }
    };


    /**
     * Creates a new instance of DnBuilderWidget.
     * 
     * @param showParent true if the parent Dn input elements should be shown
     * @param showRDN true if the Rdn input elements should be shown
     */
    public DnBuilderWidget( boolean showRDN, boolean showParent )
    {
        this.showRDN = showRDN;
        this.showParent = showParent;
    }


    /**
     * Disposes this widget.
     */
    public void dispose()
    {
    }


    /**
     * Sets the input.
     * 
     * @param rdn the initial Rdn
     * @param attributeNames the attribute names that could be selected from drop-down list
     * @param browserConnection the connection
     * @param parentDn the initial parent Dn
     */
    public void setInput( IBrowserConnection browserConnection, String[] attributeNames, Rdn rdn, Dn parentDn )
    {
        this.attributeNames = attributeNames;
        this.currentRdn = rdn;
        this.currentParentDn = parentDn;

        if ( showRDN )
        {
            for ( int i = 0; i < rdnLineList.size(); i++ )
            {
                RdnLine rdnLine = rdnLineList.get( i );
                String oldName = rdnLine.rdnTypeCombo.getText();
                rdnLine.rdnTypeCombo.setItems( attributeNames );
                rdnLine.rdnNameCPA.setContentProposalProvider( new ListContentProposalProvider( attributeNames ) );
                if ( Arrays.asList( rdnLine.rdnTypeCombo.getItems() ).contains( oldName ) )
                {
                    rdnLine.rdnTypeCombo.setText( oldName );
                }
            }
        }

        if ( showRDN )
        {
            while ( !rdnLineList.isEmpty() )
            {
                deleteRdnLine( rdnComposite, 0 );
            }
            if ( currentRdn == null || currentRdn.size() == 0 )
            {
                addRdnLine( rdnComposite, 0 );
                rdnLineList.get( 0 ).rdnTypeCombo.setFocus();
            }
            else
            {
                int i = 0;
                Iterator<Ava> atavIterator = currentRdn.iterator();
                while ( atavIterator.hasNext() )
                {
                    Ava ava = atavIterator.next();
                    addRdnLine( rdnComposite, i );

                    removeRdnLineListeners( i );

                    rdnLineList.get( i ).rdnTypeCombo.setText( ava.getUpType() );
                    rdnLineList.get( i ).rdnValueText.setText( ava.getNormValue().getString() );

                    addRdnLineListeners( i );

                    if ( i == 0 )
                    {
                        if ( "".equals( rdnLineList.get( i ).rdnTypeCombo ) ) //$NON-NLS-1$
                        {
                            rdnLineList.get( i ).rdnTypeCombo.setFocus();
                        }
                        else
                        {
                            rdnLineList.get( i ).rdnValueText.selectAll();
                            rdnLineList.get( i ).rdnValueText.setFocus();
                        }
                    }
                    i++;
                }
            }
        }

        if ( showParent )
        {
            parentEntryWidget.setInput( browserConnection, currentParentDn );
        }

        validate();
    }


    /**
     * Gets the Rdn.
     * 
     * @return the Rdn
     */
    public Rdn getRdn()
    {
        return rdn;
    }


    /**
     * Gets the parent Dn.
     * 
     * @return the parent Dn
     */
    public Dn getParentDn()
    {
        return parentDn;
    }


    /**
     * Creates the contents.
     * 
     * @param parent the parent composite
     * 
     * @return the created composite
     */
    public Composite createContents( Composite parent )
    {
        this.shell = parent.getShell();

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // draw parent
        if ( showParent )
        {
            parentEntryLabel = BaseWidgetUtils.createLabel( composite,
                Messages.getString( "DnBuilderWidget.Parent" ), 1 ); //$NON-NLS-1$
            parentEntryWidget = new EntryWidget();
            parentEntryWidget.createWidget( composite );
            parentEntryWidget.addWidgetModifyListener( new WidgetModifyListener()
            {
                public void widgetModified( WidgetModifyEvent event )
                {
                    validate();
                }
            } );

            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        // draw Rdn group
        if ( showRDN )
        {
            rdnLabel = BaseWidgetUtils.createLabel( composite, Messages.getString( "DnBuilderWidget.RDN" ), 1 ); //$NON-NLS-1$
            rdnComposite = BaseWidgetUtils.createColumnContainer( composite, 5, 2 );
            rdnLineList = new ArrayList<RdnLine>();
            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        // draw dn/rdn preview
        if ( showRDN )
        {
            previewLabel = BaseWidgetUtils.createLabel( composite, showParent ? Messages
                .getString( "DnBuilderWidget.DNPreview" ) : Messages.getString( "DnBuilderWidget.RDNPreview" ), 1 ); //$NON-NLS-1$ //$NON-NLS-2$
            previewText = BaseWidgetUtils.createReadonlyText( composite, "", 2 ); //$NON-NLS-1$
            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public void modifyText( ModifyEvent e )
    {
        validate();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        if ( parentEntryWidget != null )
        {
            parentEntryWidget.saveDialogSettings();
        }
    }


    /**
     * Validates the input elements.
     */
    public void validate()
    {
        Exception rdnE = null;

        if ( showRDN )
        {
            try
            {
                // calculate Rdn
                String[] rdnTypes = new String[rdnLineList.size()];
                String[] rdnValues = new String[rdnLineList.size()];

                for ( int i = 0; i < rdnLineList.size(); i++ )
                {
                    RdnLine rdnLine = rdnLineList.get( i );
                    rdnTypes[i] = rdnLine.rdnTypeCombo.getText();
                    rdnValues[i] = rdnLine.rdnValueText.getText();

                    if ( rdnLineList.size() > 1 )
                    {
                        rdnLine.rdnDeleteButton.setEnabled( true );
                    }
                    else
                    {
                        rdnLine.rdnDeleteButton.setEnabled( false );
                    }
                }

                rdn = DnUtils.composeRdn( rdnTypes, rdnValues );
            }
            catch ( Exception e )
            {
                rdnE = e;
                rdn = null;
            }
        }

        Exception parentE = null;

        if ( showParent )
        {
            try
            {
                // calculate Dn
                parentDn = parentEntryWidget.getDn();
            }
            catch ( Exception e )
            {
                parentE = e;
                parentDn = null;
            }
        }

        String s = ""; //$NON-NLS-1$

        if ( rdnE != null )
        {
            s += rdnE.getMessage() != null ? rdnE.getMessage() : Messages.getString( "DnBuilderWidget.ErrorInRDN" ); //$NON-NLS-1$
        }

        if ( parentE != null )
        {
            s += ", " + parentE.getMessage() != null ? parentE.getMessage() : Messages.getString( "DnBuilderWidget.ErrorInParentDN" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if ( previewText != null )
        {
            if ( s.length() > 0 )
            {
                previewText.setText( s );
            }
            else
            {
                Dn dn;
                if ( showParent && showRDN )
                {
                    try
                    {
                        dn = parentDn.add( rdn );
                    }
                    catch ( LdapInvalidDnException lide )
                    {
                        // Do nothing
                        dn = Dn.EMPTY_DN;
                    }
                }
                else if ( showParent )
                {
                    dn = parentDn;
                }
                else if ( showRDN )
                {
                    try
                    {
                        dn = new Dn( rdn );
                    }
                    catch ( LdapInvalidDnException lide )
                    {
                        // Do nothing
                        dn = Dn.EMPTY_DN;
                    }
                }
                else
                {
                    dn = Dn.EMPTY_DN;
                }

                previewText.setText( dn.getName() );
            }
        }

        notifyListeners();
    }


    /**
     * Adds an Rdn line at the given index.
     * 
     * @param rdnComposite the Rdn composite
     * @param index the index
     */
    private void addRdnLine( Composite rdnComposite, int index )
    {
        RdnLine[] rdnLines = ( RdnLine[] ) rdnLineList.toArray( new RdnLine[rdnLineList.size()] );

        if ( rdnLines.length > 0 )
        {
            for ( int i = 0; i < rdnLines.length; i++ )
            {
                RdnLine oldRdnLine = rdnLines[i];

                // remember values
                String oldName = oldRdnLine.rdnTypeCombo.getText();
                String oldValue = oldRdnLine.rdnValueText.getText();

                // delete old
                oldRdnLine.rdnTypeCombo.dispose();
                oldRdnLine.rdnEqualsLabel.dispose();
                oldRdnLine.rdnValueText.dispose();
                oldRdnLine.rdnAddButton.dispose();
                oldRdnLine.rdnDeleteButton.dispose();
                rdnLineList.remove( oldRdnLine );

                // add new
                RdnLine newRdnLine = createRdnLine( rdnComposite );
                rdnLineList.add( newRdnLine );

                // restore value
                newRdnLine.rdnTypeCombo.setText( oldName );
                newRdnLine.rdnValueText.setText( oldValue );

                // check
                if ( index == i + 1 )
                {
                    RdnLine rdnLine = createRdnLine( rdnComposite );
                    rdnLineList.add( rdnLine );
                }
            }
        }
        else
        {
            RdnLine rdnLine = createRdnLine( rdnComposite );
            rdnLineList.add( rdnLine );
        }

        shell.layout( true, true );
    }


    /**
     * Creates and returns an Rdn line.
     * 
     * @param rdnComposite the Rdn composite
     * 
     * @return the created Rdn line
     */
    private RdnLine createRdnLine( final Composite rdnComposite )
    {
        final RdnLine rdnLine = new RdnLine();

        rdnLine.rdnTypeCombo = new Combo( rdnComposite, SWT.DROP_DOWN | SWT.BORDER );
        GridData gd = new GridData();
        gd.widthHint = 180;
        rdnLine.rdnTypeCombo.setLayoutData( gd );
        rdnLine.rdnNameCPA = new ExtendedContentAssistCommandAdapter( rdnLine.rdnTypeCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( attributeNames ), null, null, true );

        rdnLine.rdnEqualsLabel = new Label( rdnComposite, SWT.NONE );
        rdnLine.rdnEqualsLabel.setText( "=" ); //$NON-NLS-1$

        rdnLine.rdnValueText = new Text( rdnComposite, SWT.BORDER );
        gd = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
        rdnLine.rdnValueText.setLayoutData( gd );

        rdnLine.rdnAddButton = new Button( rdnComposite, SWT.PUSH );
        rdnLine.rdnAddButton.setText( "  +   " ); //$NON-NLS-1$

        rdnLine.rdnDeleteButton = new Button( rdnComposite, SWT.PUSH );
        rdnLine.rdnDeleteButton.setText( "  \u2212  " ); //$NON-NLS-1$

        if ( attributeNames != null )
        {
            rdnLine.rdnTypeCombo.setItems( attributeNames );
        }

        addRdnLineListeners( rdnLine );

        return rdnLine;
    }


    /**
     * Delete the Rdn line on the given index.
     * 
     * @param rdnComposite the Rdn composite
     * @param index the index
     */
    private void deleteRdnLine( Composite rdnComposite, int index )
    {
        RdnLine rdnLine = rdnLineList.remove( index );
        if ( rdnLine != null )
        {
            rdnLine.rdnTypeCombo.dispose();
            rdnLine.rdnEqualsLabel.dispose();
            rdnLine.rdnValueText.dispose();
            rdnLine.rdnAddButton.dispose();
            rdnLine.rdnDeleteButton.dispose();

            if ( !rdnComposite.isDisposed() )
            {
                shell.layout( true, true );
            }
        }
    }


    /**
     * Adds listeners for the Rdn line at the given index.
     *
     * @param index the index
     */
    private void addRdnLineListeners( int index )
    {
        if ( rdnLineList != null )
        {
            addRdnLineListeners( rdnLineList.get( index ) );
        }
    }


    /**
     * Adds listeners for the Rdn line.
     *
     * @param rdnLine the Rdn line
     */
    private void addRdnLineListeners( RdnLine rdnLine )
    {
        if ( rdnLine != null )
        {
            rdnLine.rdnAddButton.addSelectionListener( rdnAddButtonSelectionListener );
            rdnLine.rdnDeleteButton.addSelectionListener( rdnDeleteButtonSelectionListener );
            rdnLine.rdnTypeCombo.addModifyListener( this );
            rdnLine.rdnValueText.addModifyListener( this );
        }
    }


    /**
     * Removes listeners for the Rdn line at the given index.
     *
     * @param index the index
     */
    private void removeRdnLineListeners( int index )
    {
        if ( rdnLineList != null )
        {
            removeRdnLineListeners( rdnLineList.get( index ) );
        }
    }


    /**
     * Removes listeners for the Rdn line.
     *
     * @param rdnLine the Rdn line
     */
    private void removeRdnLineListeners( RdnLine rdnLine )
    {
        if ( rdnLine != null )
        {
            rdnLine.rdnAddButton.removeSelectionListener( rdnAddButtonSelectionListener );
            rdnLine.rdnDeleteButton.removeSelectionListener( rdnDeleteButtonSelectionListener );
            rdnLine.rdnTypeCombo.removeModifyListener( this );
            rdnLine.rdnValueText.removeModifyListener( this );
        }
    }

    /**
     * The Class RdnLine is a wrapper for all input elements
     * of an Rdn line. It contains a combo for the Rdn attribute,
     * an input field for the Rdn value and + and - buttons
     * to add and remove other Rdn lines. It looks like this:
     * <pre>
     * --------------------------------------------------
     * | attribute type v | = | attribute value | + | - |
     * --------------------------------------------------
     * </pre>
     */
    private class RdnLine
    {

        /** The rdn name combo. */
        private Combo rdnTypeCombo;

        /** The content proposal adapter */
        private ContentProposalAdapter rdnNameCPA;

        /** The rdn value text. */
        private Text rdnValueText;

        /** The rdn equals label. */
        private Label rdnEqualsLabel;

        /** The rdn add button. */
        private Button rdnAddButton;

        /** The rdn delete button. */
        private Button rdnDeleteButton;
    }


    /**
     * Enables or disables this widget.
     * 
     * @param b true to enable, false to disable 
     */
    public void setEnabled( boolean b )
    {
        if ( parentEntryWidget != null )
        {
            parentEntryLabel.setEnabled( b );
            parentEntryWidget.setEnabled( b );
        }
        if ( rdnComposite != null && rdnLineList != null )
        {
            rdnLabel.setEnabled( b );
            rdnComposite.setEnabled( b );
            for ( RdnLine rdnLine : rdnLineList )
            {
                rdnLine.rdnTypeCombo.setEnabled( b );
                rdnLine.rdnEqualsLabel.setEnabled( b );
                rdnLine.rdnValueText.setEnabled( b );
                rdnLine.rdnAddButton.setEnabled( b );
                rdnLine.rdnDeleteButton.setEnabled( b && rdnLineList.size() > 1 );
            }
            if ( b )
            {
                rdnLineList.get( 0 ).rdnValueText.setFocus();
            }
        }
        if ( previewText != null )
        {
            previewLabel.setEnabled( b );
            previewText.setEnabled( b );
        }
    }

}
