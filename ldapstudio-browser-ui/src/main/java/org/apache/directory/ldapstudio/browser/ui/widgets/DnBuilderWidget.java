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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class DnBuilderWidget extends BrowserWidget implements ModifyListener
{

    private IConnection connection;

    private String[] attributeNames;

    private RDN currentRdn;

    private DN currentParentDn;

    private boolean showRDN;

    private boolean showParent;

    private Shell shell;

    private DN parentDn;

    private EntryWidget parentEntryWidget;

    private Composite rdnComposite;

    private RDN rdn;

    private ArrayList rdnLineList;

    // private int rdnGroupHeight = -1;

    private Text dnOrRdnText;


    public DnBuilderWidget( boolean showRDN, boolean showParent )
    {
        this.showRDN = showRDN;
        this.showParent = showParent;
    }


    public void dispose()
    {

    }


    public void setInput( IConnection connection, String[] attributeNames, RDN rdn, DN parentDn )
    {
        this.connection = connection;

        this.attributeNames = attributeNames;
        if ( showRDN )
        {
            for ( int i = 0; i < this.rdnLineList.size(); i++ )
            {
                RdnLine rdnLine = ( RdnLine ) this.rdnLineList.get( i );
                String oldName = rdnLine.rdnNameCombo.getText();
                rdnLine.rdnNameCombo.setItems( attributeNames );
                if ( Arrays.asList( rdnLine.rdnNameCombo.getItems() ).contains( oldName ) )
                {
                    rdnLine.rdnNameCombo.setText( oldName );
                }
            }
        }

        this.currentRdn = rdn;
        if ( showRDN )
        {
            while ( !rdnLineList.isEmpty() )
            {
                deleteRdnLine( this.rdnComposite, 0 );
            }
            if ( this.currentRdn == null || this.currentRdn.getParts().length == 0 )
            {
                addRdnLine( this.rdnComposite, 0 );
            }
            else
            {
                RDNPart[] parts = this.currentRdn.getParts();
                for ( int i = 0; i < parts.length; i++ )
                {
                    addRdnLine( this.rdnComposite, i );
                    ( ( RdnLine ) rdnLineList.get( i ) ).rdnNameCombo.setText( parts[i].getType() );
                    ( ( RdnLine ) rdnLineList.get( i ) ).rdnValueText.setText( parts[i].getUnencodedValue() );
                    if ( i == 0 )
                    {
                        ( ( RdnLine ) rdnLineList.get( i ) ).rdnValueText.setFocus();
                    }
                }
            }
        }

        this.currentParentDn = parentDn;
        if ( showParent )
        {
            parentEntryWidget.setInput( this.connection, this.currentParentDn );
        }

        validate();
    }


    public RDN getRdn()
    {
        return this.rdn;
    }


    public DN getParentDn()
    {
        return this.parentDn;
    }


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
            // parentEntryWidget.setInput(this.connection,
            // this.currentParentDn);
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
            this.rdnComposite = BaseWidgetUtils.createColumnContainer( composite, 5, 2 );
            this.rdnLineList = new ArrayList();
            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        // draw dn/rdn preview
        if ( showRDN )
        {
            BaseWidgetUtils.createLabel( composite, this.showParent ? "DN Preview: " : "RDN Preview: ", 1 );
            this.dnOrRdnText = BaseWidgetUtils.createReadonlyText( composite, "", 2 );
            BaseWidgetUtils.createSpacer( composite, 3 );
        }

        // fill RDN
        // if(showRDN) {
        // setRdn(currentRdn);
        // }

        return composite;
    }


    public void modifyText( ModifyEvent e )
    {
        this.validate();
    }


    public void saveDialogSettings()
    {
        if ( this.parentEntryWidget != null )
        {
            this.parentEntryWidget.saveDialogSettings();
        }
    }


    public void validate()
    {

        Exception rdnE = null;
        if ( showRDN )
        {
            try
            {
                // calculate RDN
                String[] rdnNames = new String[this.rdnLineList.size()];
                String[] rdnValues = new String[this.rdnLineList.size()];
                for ( int i = 0; i < this.rdnLineList.size(); i++ )
                {
                    RdnLine rdnLine = ( RdnLine ) this.rdnLineList.get( i );
                    rdnNames[i] = rdnLine.rdnNameCombo.getText();
                    rdnValues[i] = rdnLine.rdnValueText.getText();

                    if ( this.rdnLineList.size() > 1 )
                    {
                        rdnLine.rdnDeleteButton.setEnabled( true );
                    }
                    else
                    {
                        rdnLine.rdnDeleteButton.setEnabled( false );
                    }
                }
                this.rdn = new RDN( rdnNames, rdnValues, false );
            }
            catch ( Exception e )
            {
                rdnE = e;
                this.rdn = null;
            }
        }

        Exception parentE = null;
        if ( showParent )
        {
            try
            {
                // calculate DN
                this.parentDn = new DN( parentEntryWidget.getDn() );
            }
            catch ( Exception e )
            {
                parentE = e;
                this.parentDn = null;
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

        if ( this.dnOrRdnText != null )
        {
            if ( s.length() > 0 )
            {
                this.dnOrRdnText.setText( s );
            }
            else
            {
                DN dn;
                if ( this.showParent && this.showRDN )
                {
                    dn = new DN( rdn, parentDn );
                }
                else if ( this.showParent )
                {
                    dn = new DN( parentDn );
                }
                else if ( this.showRDN )
                {
                    dn = new DN( rdn );
                }
                else
                {
                    dn = new DN();
                }
                this.dnOrRdnText.setText( dn.toString() );
            }
        }

        notifyListeners();
    }


    private void addRdnLine( Composite rdnGroup, int index )
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
                oldRdnLine.rdnNameCombo.dispose();
                oldRdnLine.rdnEqualsLabel.dispose();
                oldRdnLine.rdnValueText.dispose();
                oldRdnLine.rdnAddButton.dispose();
                oldRdnLine.rdnDeleteButton.dispose();
                rdnLineList.remove( oldRdnLine );

                // add new
                RdnLine newRdnLine = createRdnLine( rdnGroup );
                rdnLineList.add( newRdnLine );

                // restore value
                newRdnLine.rdnNameCombo.setText( oldName );
                newRdnLine.rdnValueText.setText( oldValue );

                // check
                if ( index == i + 1 )
                {
                    RdnLine rdnLine = createRdnLine( rdnGroup );
                    rdnLineList.add( rdnLine );
                }
            }
        }
        else
        {
            RdnLine rdnLine = createRdnLine( rdnGroup );
            rdnLineList.add( rdnLine );
        }

        // Point shellSize = shell.getSize();
        // Point groupSize = rdnGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT,
        // true);
        // int newRdnGroupHeight = groupSize.y;
        // shell.setSize(shellSize.x, shellSize.y + newRdnGroupHeight -
        // rdnGroupHeight);
        rdnGroup.layout( true, true );
        shell.layout( true, true );
        // rdnGroupHeight = newRdnGroupHeight;
    }


    private RdnLine createRdnLine( final Composite rdnGroup )
    {
        final RdnLine rdnLine = new RdnLine();

        rdnLine.rdnNameCombo = new Combo( rdnGroup, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );
        GridData gd = new GridData();
        gd.widthHint = 180;
        rdnLine.rdnNameCombo.setLayoutData( gd );
        rdnLine.rdnNameCombo.setVisibleItemCount( 20 );

        rdnLine.rdnEqualsLabel = new Label( rdnGroup, SWT.NONE );
        rdnLine.rdnEqualsLabel.setText( "=" );

        rdnLine.rdnValueText = new Text( rdnGroup, SWT.BORDER );
        gd = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
        rdnLine.rdnValueText.setLayoutData( gd );

        rdnLine.rdnAddButton = new Button( rdnGroup, SWT.PUSH );
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
                addRdnLine( rdnGroup, index );

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        rdnLine.rdnDeleteButton = new Button( rdnGroup, SWT.PUSH );
        rdnLine.rdnDeleteButton.setText( "  \u2212  " ); // \u2013
        rdnLine.rdnDeleteButton.addSelectionListener( new SelectionListener()
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
                deleteRdnLine( rdnGroup, index );

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        if ( this.attributeNames != null )
        {
            // Subschema subschema = new Subschema(this.attributeNames,
            // this.connection.getSchema());
            rdnLine.rdnNameCombo.setItems( this.attributeNames );
        }

        rdnLine.rdnNameCombo.addModifyListener( this );
        rdnLine.rdnValueText.addModifyListener( this );

        return rdnLine;
    }


    private void deleteRdnLine( Composite rdnGroup, int index )
    {
        RdnLine rdnLine = ( RdnLine ) rdnLineList.remove( index );
        if ( rdnLine != null )
        {
            rdnLine.rdnNameCombo.dispose();
            rdnLine.rdnEqualsLabel.dispose();
            rdnLine.rdnValueText.dispose();
            rdnLine.rdnAddButton.dispose();
            rdnLine.rdnDeleteButton.dispose();

            // Point shellSize = shell.getSize();
            // Point groupSize = rdnGroup.computeSize(SWT.DEFAULT,
            // SWT.DEFAULT,
            // true);
            // int newRdnGroupHeight = groupSize.y;
            // shell.setSize(shellSize.x, shellSize.y + newRdnGroupHeight -
            // rdnGroupHeight);
            rdnGroup.layout( true, true );
            shell.layout( true, true );
            // rdnGroupHeight = newRdnGroupHeight;
        }
    }

    public class RdnLine
    {
        public Combo rdnNameCombo;

        public Text rdnValueText;

        public Label rdnEqualsLabel;

        public Button rdnAddButton;

        public Button rdnDeleteButton;
    }

}
