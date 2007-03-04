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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.RDNPart;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.EntryWidget;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The DnBuilderWidget provides input elements to select a parent DN
 * and to build a (multivalued) RDN.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DnBuilderWidget extends BrowserWidget implements ModifyListener
{

    /** The attribute names that could be selected from drop-down list. */
    private String[] attributeNames;

    /** The initial RDN. */
    private RDN currentRdn;

    /** The initial parent DN. */
    private DN currentParentDn;

    /** True if the RDN input elements should be shown. */
    private boolean showRDN;

    /** True if the parent DN input elements should be shown. */
    private boolean showParent;

    /** The shell. */
    private Shell shell;

    /** The selected parent DN. */
    private DN parentDn;

    /** The entry widget to enter/select the parent DN. */
    private EntryWidget parentEntryWidget;

    /** The composite that contains the RdnLines. */
    private Composite rdnComposite;

    /** The resulting RDN. */
    private RDN rdn;

    /** The list of RdnLines. */
    private ArrayList<RdnLine> rdnLineList;

    /** The preview text. */
    private Text previewText;


    /**
     * Creates a new instance of DnBuilderWidget.
     * 
     * @param showParent true if the parent DN input elements should be shown
     * @param showRDN true if the RDN input elements should be shown
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
     * @param rdn the initial RDN
     * @param attributeNames the attribute names that could be selected from drop-down list
     * @param connection the connection
     * @param parentDn the initial parent DN
     */
    public void setInput( IConnection connection, String[] attributeNames, RDN rdn, DN parentDn )
    {
        this.attributeNames = attributeNames;
        this.currentRdn = rdn;
        this.currentParentDn = parentDn;

        if ( showRDN )
        {
            for ( int i = 0; i < rdnLineList.size(); i++ )
            {
                RdnLine rdnLine = rdnLineList.get( i );
                String oldName = rdnLine.rdnNameCombo.getText();
                rdnLine.rdnNameCombo.setItems( attributeNames );
                rdnLine.rdnNameCPA.setContentProposalProvider( new ListContentProposalProvider( attributeNames ) );
                if ( Arrays.asList( rdnLine.rdnNameCombo.getItems() ).contains( oldName ) )
                {
                    rdnLine.rdnNameCombo.setText( oldName );
                }
            }
        }

        if ( showRDN )
        {
            while ( !rdnLineList.isEmpty() )
            {
                deleteRdnLine( rdnComposite, 0 );
            }
            if ( currentRdn == null || currentRdn.getParts().length == 0 )
            {
                addRdnLine( rdnComposite, 0 );
                rdnLineList.get( 0 ).rdnNameCombo.setFocus();
            }
            else
            {
                RDNPart[] parts = currentRdn.getParts();
                for ( int i = 0; i < parts.length; i++ )
                {
                    addRdnLine( rdnComposite, i );
                    rdnLineList.get( i ).rdnNameCombo.setText( parts[i].getType() );
                    rdnLineList.get( i ).rdnValueText.setText( parts[i].getUnencodedValue() );
                    if ( i == 0 )
                    {
                        if("".equals(rdnLineList.get( i ).rdnNameCombo))
                        {
                            rdnLineList.get( i ).rdnNameCombo.setFocus();
                        }
                        else
                        {
                            rdnLineList.get( i ).rdnValueText.selectAll();
                            rdnLineList.get( i ).rdnValueText.setFocus();
                        }
                    }
                }
            }
        }

        if ( showParent )
        {
            parentEntryWidget.setInput( connection, currentParentDn );
        }

        validate();
    }


    /**
     * Gets the RDN.
     * 
     * @return the RDN
     */
    public RDN getRdn()
    {
        return rdn;
    }


    /**
     * Gets the parent DN.
     * 
     * @return the parent DN
     */
    public DN getParentDn()
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
            BaseWidgetUtils.createLabel( composite, "Parent:", 1 );
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

        // draw RDN group
        if ( showRDN )
        {
            BaseWidgetUtils.createLabel( composite, "RDN:", 1 );
            rdnComposite = BaseWidgetUtils.createColumnContainer( composite, 5, 2 );
            rdnLineList = new ArrayList<RdnLine>();
            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        // draw dn/rdn preview
        if ( showRDN )
        {
            BaseWidgetUtils.createLabel( composite, showParent ? "DN Preview: " : "RDN Preview: ", 1 );
            previewText = BaseWidgetUtils.createReadonlyText( composite, "", 2 );
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
     * Validate.
     */
    public void validate()
    {

        Exception rdnE = null;
        if ( showRDN )
        {
            try
            {
                // calculate RDN
                String[] rdnNames = new String[rdnLineList.size()];
                String[] rdnValues = new String[rdnLineList.size()];
                for ( int i = 0; i < rdnLineList.size(); i++ )
                {
                    RdnLine rdnLine = ( RdnLine ) rdnLineList.get( i );
                    rdnNames[i] = rdnLine.rdnNameCombo.getText();
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
                rdn = new RDN( rdnNames, rdnValues, false );
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
                // calculate DN
                parentDn = new DN( parentEntryWidget.getDn() );
            }
            catch ( Exception e )
            {
                parentE = e;
                parentDn = null;
            }
        }

        String s = "";
        if ( rdnE != null )
        {
            s += rdnE.getMessage() != null ? rdnE.getMessage() : "Error in RDN ";
        }
        if ( parentE != null )
        {
            s += ", " + parentE.getMessage() != null ? parentE.getMessage() : "Error in Parent DN ";
        }

        if ( previewText != null )
        {
            if ( s.length() > 0 )
            {
                previewText.setText( s );
            }
            else
            {
                DN dn;
                if ( showParent && showRDN )
                {
                    dn = new DN( rdn, parentDn );
                }
                else if ( showParent )
                {
                    dn = new DN( parentDn );
                }
                else if ( showRDN )
                {
                    dn = new DN( rdn );
                }
                else
                {
                    dn = new DN();
                }
                previewText.setText( dn.toString() );
            }
        }

        notifyListeners();
    }


    /**
     * Adds an RDN line at the given index.
     * 
     * @param rdnComposite the RDN composite
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
                String oldName = oldRdnLine.rdnNameCombo.getText();
                String oldValue = oldRdnLine.rdnValueText.getText();

                // delete old
                oldRdnLine.rdnNameComboField.getLayoutControl().dispose();
                oldRdnLine.rdnEqualsLabel.dispose();
                oldRdnLine.rdnValueText.dispose();
                oldRdnLine.rdnAddButton.dispose();
                oldRdnLine.rdnDeleteButton.dispose();
                rdnLineList.remove( oldRdnLine );

                // add new
                RdnLine newRdnLine = createRdnLine( rdnComposite );
                rdnLineList.add( newRdnLine );

                // restore value
                newRdnLine.rdnNameCombo.setText( oldName );
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

        rdnComposite.layout( true, true );
        shell.layout( true, true );
    }


    /**
     * Creates and returns an RDN line.
     * 
     * @param rdnComposite the RDN composite
     * 
     * @return the created RDN line
     */
    private RdnLine createRdnLine( final Composite rdnComposite )
    {
        final RdnLine rdnLine = new RdnLine();

        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
        rdnLine.rdnNameComboField = new DecoratedField( rdnComposite, SWT.NONE, new IControlCreator()
        {
            public Control createControl( Composite parent, int style )
            {
                Combo combo = new Combo( parent, SWT.DROP_DOWN | SWT.BORDER );
                GridData gd = new GridData();
                gd.widthHint = 180;
                combo.setLayoutData( gd );
                combo.setVisibleItemCount( 20 );
                return combo;
            }
        } );
        rdnLine.rdnNameComboField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
        GridData gd = new GridData();
        gd.widthHint = 180;
        rdnLine.rdnNameComboField.getLayoutControl().setLayoutData( gd );
        rdnLine.rdnNameCombo = ( Combo ) rdnLine.rdnNameComboField.getControl();

        rdnLine.rdnNameCPA = new ContentProposalAdapter( rdnLine.rdnNameCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( attributeNames ), null, null );
        rdnLine.rdnNameCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        rdnLine.rdnNameCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

        rdnLine.rdnEqualsLabel = new Label( rdnComposite, SWT.NONE );
        rdnLine.rdnEqualsLabel.setText( "=" );

        rdnLine.rdnValueText = new Text( rdnComposite, SWT.BORDER );
        gd = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
        rdnLine.rdnValueText.setLayoutData( gd );

        rdnLine.rdnAddButton = new Button( rdnComposite, SWT.PUSH );
        rdnLine.rdnAddButton.setText( "  +   " );
        rdnLine.rdnAddButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = rdnLineList.size();
                for ( int i = 0; i < rdnLineList.size(); i++ )
                {
                    RdnLine rdnLine = ( RdnLine ) rdnLineList.get( i );
                    if ( rdnLine.rdnAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }
                addRdnLine( rdnComposite, index );

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        rdnLine.rdnDeleteButton = new Button( rdnComposite, SWT.PUSH );
        rdnLine.rdnDeleteButton.setText( "  \u2212  " ); // \u2013
        rdnLine.rdnDeleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < rdnLineList.size(); i++ )
                {
                    RdnLine rdnLine = ( RdnLine ) rdnLineList.get( i );
                    if ( rdnLine.rdnDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }
                deleteRdnLine( rdnComposite, index );

                validate();
            }
        } );

        if ( attributeNames != null )
        {
            rdnLine.rdnNameCombo.setItems( attributeNames );
        }

        rdnLine.rdnNameCombo.addModifyListener( this );
        rdnLine.rdnValueText.addModifyListener( this );

        return rdnLine;
    }


    /**
     * Delete thd RDN line on the given index.
     * 
     * @param rdnComposite the RDN composite
     * @param index the index
     */
    private void deleteRdnLine( Composite rdnComposite, int index )
    {
        RdnLine rdnLine = ( RdnLine ) rdnLineList.remove( index );
        if ( rdnLine != null )
        {
            rdnLine.rdnNameComboField.getLayoutControl().dispose();
            rdnLine.rdnEqualsLabel.dispose();
            rdnLine.rdnValueText.dispose();
            rdnLine.rdnAddButton.dispose();
            rdnLine.rdnDeleteButton.dispose();

            if ( !rdnComposite.isDisposed() )
            {
                rdnComposite.layout( true, true );
                shell.layout( true, true );
            }
        }
    }

    /**
     * The Class RdnLine.
     */
    public class RdnLine
    {

        /** The rdn name combo. */
        public DecoratedField rdnNameComboField;

        /** The rdn name combo. */
        public Combo rdnNameCombo;

        /** The content proposal adapter */
        public ContentProposalAdapter rdnNameCPA;

        /** The rdn value text. */
        public Text rdnValueText;

        /** The rdn equals label. */
        public Label rdnEqualsLabel;

        /** The rdn add button. */
        public Button rdnAddButton;

        /** The rdn delete button. */
        public Button rdnDeleteButton;
    }

}
