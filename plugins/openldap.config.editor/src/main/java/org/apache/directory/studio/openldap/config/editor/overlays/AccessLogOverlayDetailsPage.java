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
package org.apache.directory.studio.openldap.config.editor.overlays;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.apache.directory.studio.openldap.config.model.OlcAccessLogConfig;


/**
 * This class represents the Details Page of the Server Configuration Editor for the Access Log Overlay type
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AccessLogOverlayDetailsPage implements IDetailsPage
{
    /** The associated Master Details Block */
    private OverlaysMasterDetailsBlock masterDetailsBlock;

    /** The Managed Form */
    private IManagedForm mform;

    /** The dirty flag */
    private boolean dirty = false;

    /** The overlay */
    private OlcAccessLogConfig overlay;

    // UI fields
    private Text databaseDnText;
    private Text filterText;
    private Text attributesText;
    private Button onlyLogSuccessOperationsCheckbox;
    private Button logAllOperationsRadioButton;
    private Button logSpecificOperationsRadioButton;
    private Button logWriteOperationsCheckbox;
    private Button logReadOperationsCheckbox;
    private Button logSessionOperationsCheckbox;
    private Button logAddOperationCheckbox;
    private Button logDeleteOperationCheckbox;
    private Button logModifyOperationCheckbox;
    private Button logModifyRdnOperationCheckbox;
    private Button logCompareOperationCheckbox;
    private Button logSearchOperationCheckbox;
    private Button logAbandonOperationCheckbox;
    private Button logBindOperationCheckbox;
    private Button logUnbindOperationCheckbox;
    private Spinner purgeAgeDaysSpinner;
    private Spinner purgeAgeHoursSpinner;
    private Spinner purgeAgeMinutesSpinner;
    private Spinner purgeAgeSecondsSpinner;
    private Spinner purgeIntervalDaysSpinner;
    private Spinner purgeIntervalHoursSpinner;
    private Spinner purgeIntervalMinutesSpinner;
    private Spinner purgeIntervalSecondsSpinner;


    /**
     * Creates a new instance of PartitionDetailsPage.
     *
     * @param master
     *      the associated Master Details Block
     */
    public AccessLogOverlayDetailsPage( OverlaysMasterDetailsBlock master )
    {
        masterDetailsBlock = master;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents( Composite parent )
    {
        FormToolkit toolkit = mform.getToolkit();
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout( layout );

        createGeneralSettingsSection( parent, toolkit );
        createLogOperationsSettingsSection( parent, toolkit );
        createPurgeSettingsSection( parent, toolkit );
    }


    /**
     * Creates the General Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createGeneralSettingsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Audit Log General Settings" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 2, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Database DN Text
        toolkit.createLabel( composite, "Database DN:" );
        databaseDnText = toolkit.createText( composite, "" );
        databaseDnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Database DN Text
        toolkit.createLabel( composite, "Filter:" );
        filterText = toolkit.createText( composite, "" );
        filterText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Database DN Text
        toolkit.createLabel( composite, "Attributes:" );
        attributesText = toolkit.createText( composite, "" );
        attributesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Only Log Success Operations
        onlyLogSuccessOperationsCheckbox = toolkit.createButton( composite, "Only log success operations", SWT.CHECK );
        onlyLogSuccessOperationsCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );
    }


    /**
     * Creates the Log Operations Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createLogOperationsSettingsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Log Operations Settings" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout();
        composite.setLayout( glayout );
        section.setClient( composite );

        // Log All Operations Radio Button
        logAllOperationsRadioButton = toolkit.createButton( composite, "All Operations", SWT.RADIO );

        // Log Specific Operations Radio Button
        logSpecificOperationsRadioButton = toolkit.createButton( composite, "The following operations:", SWT.RADIO );

        // Specific Operations Composite
        Composite specificOperationsComposite = toolkit.createComposite( composite );
        GridLayout gl = new GridLayout( 3, true );
        gl.marginHeight = gl.marginWidth = 0;
        gl.marginLeft = 20;
        specificOperationsComposite.setLayout( gl );
        specificOperationsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Log Write Operations Checkbox
        logWriteOperationsCheckbox = toolkit.createButton( specificOperationsComposite, "Write Operations", SWT.CHECK );

        // Log Reads Operations Checkbox
        logReadOperationsCheckbox = toolkit.createButton( specificOperationsComposite, "Read Operations", SWT.CHECK );

        // Log Session Operations Checkbox
        logSessionOperationsCheckbox = toolkit.createButton( specificOperationsComposite, "Session Operations",
            SWT.CHECK );

        // Write Operations Composite
        Composite writeOperationsComposite = toolkit.createComposite( specificOperationsComposite );
        gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        gl.marginLeft = 20;
        writeOperationsComposite.setLayout( gl );
        writeOperationsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Log Add Operation Checkbox
        logAddOperationCheckbox = toolkit.createButton( writeOperationsComposite, "Add Operation", SWT.CHECK );

        // Log Delete Operation Checkbox
        logDeleteOperationCheckbox = toolkit.createButton( writeOperationsComposite, "Delete Operation", SWT.CHECK );

        // Log Modify Operation Checkbox
        logModifyOperationCheckbox = toolkit.createButton( writeOperationsComposite, "Modify Operation", SWT.CHECK );

        // Log Modify RDN Operation Checkbox
        logModifyRdnOperationCheckbox = toolkit.createButton( writeOperationsComposite, "Modify RDN Operation",
            SWT.CHECK );

        // Read Operations Composite
        Composite readOperationsComposite = toolkit.createComposite( specificOperationsComposite );
        gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        gl.marginLeft = 20;
        readOperationsComposite.setLayout( gl );
        readOperationsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Log Compare Operation Checkbox
        logCompareOperationCheckbox = toolkit.createButton( readOperationsComposite, "Compare Operation", SWT.CHECK );

        // Log Search Operation Checkbox
        logSearchOperationCheckbox = toolkit.createButton( readOperationsComposite, "Search Operation", SWT.CHECK );

        // Session Operations Composite
        Composite sessionOperationsComposite = toolkit.createComposite( specificOperationsComposite );
        gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        gl.marginLeft = 20;
        sessionOperationsComposite.setLayout( gl );
        sessionOperationsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Log Abandon Operation Checkbox
        logAbandonOperationCheckbox = toolkit.createButton( sessionOperationsComposite, "Abandon Operation", SWT.CHECK );

        // Log Bind Operation Checkbox
        logBindOperationCheckbox = toolkit.createButton( sessionOperationsComposite, "Bind Operation", SWT.CHECK );

        // Log Unbind Operation Checkbox
        logUnbindOperationCheckbox = toolkit.createButton( sessionOperationsComposite, "Unbind Operation", SWT.CHECK );
    }


    /**
     * Creates the Purge Settings Section
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the toolkit to use
     */
    private void createPurgeSettingsSection( Composite parent, FormToolkit toolkit )
    {
        Section section = toolkit.createSection( parent, Section.TITLE_BAR );
        section.marginWidth = 10;
        section.setText( "Purge Settings" );
        TableWrapData td = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP );
        td.grabHorizontal = true;
        section.setLayoutData( td );
        Composite composite = toolkit.createComposite( section );
        toolkit.paintBordersFor( composite );
        GridLayout glayout = new GridLayout( 9, false );
        composite.setLayout( glayout );
        section.setClient( composite );

        // Purge Age
        toolkit.createLabel( composite, "Purge Age:" );
        purgeAgeDaysSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Days" );
        purgeAgeHoursSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Hours" );
        purgeAgeMinutesSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Minutes" );
        purgeAgeSecondsSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Seconds" );

        // Purge Interval
        toolkit.createLabel( composite, "Purge Interval:" );
        purgeIntervalDaysSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Days" );
        purgeIntervalHoursSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Hours" );
        purgeIntervalMinutesSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Minutes" );
        purgeIntervalSecondsSpinner = new Spinner( composite, SWT.BORDER );
        toolkit.createLabel( composite, "Seconds" );
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IFormPart part, ISelection selection )
    {
        IStructuredSelection ssel = ( IStructuredSelection ) selection;
        if ( ssel.size() == 1 )
        {
            overlay = ( OlcAccessLogConfig ) ssel.getFirstElement();
        }
        else
        {
            overlay = null;
        }
        refresh();
    }


    /**
     * {@inheritDoc}
     */
    public void commit( boolean onSave )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void initialize( IManagedForm form )
    {
        this.mform = form;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return dirty;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStale()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        //        idText.setFocus(); // TODO
    }


    /**
     * {@inheritDoc}
     */
    public boolean setFormInput( Object input )
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay == null )
        {
            // Blank out all fields
            // TODO
        }
        else
        {
        }
    }
}
