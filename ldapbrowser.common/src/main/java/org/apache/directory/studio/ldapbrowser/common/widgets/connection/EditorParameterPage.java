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

package org.apache.directory.studio.ldapbrowser.common.widgets.connection;


import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.LdapURL.Extension;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyMode;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyOrder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * The EditorParameterPage is used the edit the editor specific parameters of a
 * connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorParameterPage extends AbstractConnectionParameterPage
{

    private static final String X_MODIFY_MODE = "X-MODIFY-MODE"; //$NON-NLS-1$

    private static final String X_MODIFY_MODE_NO_EMR = "X-MODIFY-MODE-NO-EMR"; //$NON-NLS-1$

    private static final String X_MODIFY_ORDER = "X-MODIFY-ORDER"; //$NON-NLS-1$

    /** The combo for selecting the modify mode */
    private Combo modifyModeCombo;

    /** The combo for selecting the modify mode of attribute with no equality matching rule */
    private Combo modifyModeNoEMRCombo;

    /** The combo for selecting the modify order */
    private Combo modifyOrderCombo;


    /**
     * Creates a new instance of EditorParameterPage.
     */
    public EditorParameterPage()
    {
    }


    /**
     * Gets the modify mode.
     * 
     * @return the modify mode
     */
    private ModifyMode getModifyMode()
    {
        return ModifyMode.getByOrdinal( modifyModeCombo.getSelectionIndex() );
    }


    /**
     * Gets the modify mode of attribute with no equality matching rule.
     * 
     * @return the modify mode of attribute with no equality matching rule
     */
    private ModifyMode getModifyModeNoEMR()
    {
        return ModifyMode.getByOrdinal( modifyModeNoEMRCombo.getSelectionIndex() );
    }


    /**
     * Gets the modify mode.
     * 
     * @return the modify mode
     */
    private ModifyOrder getModifyOrder()
    {
        return ModifyOrder.getByOrdinal( modifyOrderCombo.getSelectionIndex() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#createComposite(org.eclipse.swt.widgets.Composite)
     */
    protected void createComposite( Composite parent )
    {
        addModifyInput( parent );
    }


    /**
     * Adds the modify input.
     * 
     * @param parent the parent
     */
    private void addModifyInput( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Group group = BaseWidgetUtils.createGroup( composite,
            Messages.getString( "EditorParameterPage.ModifyGroup" ), 1 ); //$NON-NLS-1$
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( group, 2, 1 );

        Label modifyModeLabel = BaseWidgetUtils.createLabel( groupComposite, Messages
            .getString( "EditorParameterPage.ModifyMode" ), 1 ); //$NON-NLS-1$
        modifyModeLabel.setToolTipText( Messages.getString( "EditorParameterPage.ModifyModeTooltip" ) ); //$NON-NLS-1$
        String[] modifyModeItems = new String[]
            { Messages.getString( "EditorParameterPage.ModifyModeDefault" ), //$NON-NLS-1$
                Messages.getString( "EditorParameterPage.ModifyModeReplace" ), //$NON-NLS-1$
                Messages.getString( "EditorParameterPage.ModifyModeAddDel" ) }; //$NON-NLS-1$
        modifyModeCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, modifyModeItems, 0, 1 );
        modifyModeCombo.setToolTipText( Messages.getString( "EditorParameterPage.ModifyModeTooltip" ) ); //$NON-NLS-1$

        Label modifyModeNoEMRLabel = BaseWidgetUtils.createLabel( groupComposite, Messages
            .getString( "EditorParameterPage.ModifyModeNoEMR" ), 1 ); //$NON-NLS-1$
        modifyModeNoEMRLabel.setToolTipText( Messages.getString( "EditorParameterPage.ModifyModeNoEMRTooltip" ) ); //$NON-NLS-1$
        String[] modifyModeNoEMRItems = new String[]
            { Messages.getString( "EditorParameterPage.ModifyModeDefault" ), //$NON-NLS-1$
                Messages.getString( "EditorParameterPage.ModifyModeReplace" ), //$NON-NLS-1$
                Messages.getString( "EditorParameterPage.ModifyModeAddDel" ) }; //$NON-NLS-1$
        modifyModeNoEMRCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, modifyModeNoEMRItems, 0, 1 );
        modifyModeNoEMRCombo.setToolTipText( Messages.getString( "EditorParameterPage.ModifyModeNoEMRTooltip" ) ); //$NON-NLS-1$

        Label modifyOrderLabel = BaseWidgetUtils.createLabel( groupComposite, Messages
            .getString( "EditorParameterPage.ModifyOrder" ), 1 ); //$NON-NLS-1$
        modifyOrderLabel.setToolTipText( Messages.getString( "EditorParameterPage.ModifyOrderTooltip" ) ); //$NON-NLS-1$
        String[] modifyOrderItems = new String[]
            { Messages.getString( "EditorParameterPage.ModifyOrderDelFirst" ), //$NON-NLS-1$
                Messages.getString( "EditorParameterPage.ModifyOrderAddFirst" ) }; //$NON-NLS-1$
        modifyOrderCombo = BaseWidgetUtils.createReadonlyCombo( groupComposite, modifyOrderItems, 0, 1 );
        modifyOrderCombo.setToolTipText( Messages.getString( "EditorParameterPage.ModifyOrderTooltip" ) ); //$NON-NLS-1$
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#validate()
     */
    protected void validate()
    {
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#loadParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    protected void loadParameters( ConnectionParameter parameter )
    {
        this.connectionParameter = parameter;

        int modifyMode = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE );
        modifyModeCombo.select( modifyMode );
        int modifyModeNoEMR = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR );
        modifyModeNoEMRCombo.select( modifyModeNoEMR );
        int modifyOrder = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER );
        modifyOrderCombo.select( modifyOrder );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.AbstractConnectionParameterPage#initListeners()
     */
    protected void initListeners()
    {
        modifyModeCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        modifyModeNoEMRCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );

        modifyOrderCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                connectionPageModified();
            }
        } );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveParameters(org.apache.directory.studio.connection.core.ConnectionParameter)
     */
    public void saveParameters( ConnectionParameter parameter )
    {
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE, getModifyMode()
            .getOrdinal() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR,
            getModifyModeNoEMR().getOrdinal() );
        parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER, getModifyOrder()
            .getOrdinal() );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#saveDialogSettings()
     */
    public void saveDialogSettings()
    {
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#setFocus()
     */
    public void setFocus()
    {
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#areParametersModifed()
     */
    public boolean areParametersModifed()
    {
        int modifyMode = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE );
        int modifyModeNoEMR = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR );
        int modifyOrder = connectionParameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER );

        return modifyMode != getModifyMode().getOrdinal() || modifyModeNoEMR != getModifyModeNoEMR().getOrdinal()
            || modifyOrder != getModifyOrder().getOrdinal();
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPage#isReconnectionRequired()
     */
    public boolean isReconnectionRequired()
    {
        if ( connectionParameter == null )
        {
            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void mergeParametersToLdapURL( ConnectionParameter parameter, LdapURL ldapUrl )
    {
        int modifyMode = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE );
        if ( modifyMode != 0 )
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_MODIFY_MODE, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE ) ) );
        }

        int modifyModeNoEMR = parameter
            .getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR );
        if ( modifyModeNoEMR != 0 )
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_MODIFY_MODE_NO_EMR, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR ) ) );
        }

        int modifyOrder = parameter.getExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER );
        if ( modifyOrder != 0 )
        {
            ldapUrl.getExtensions().add(
                new Extension( false, X_MODIFY_ORDER, parameter
                    .getExtendedProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER ) ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void mergeLdapUrlToParameters( LdapURL ldapUrl, ConnectionParameter parameter )
    {
        // modify mode, DEFAULT if non-numeric or absent 
        String modifyMode = ldapUrl.getExtensionValue( X_MODIFY_MODE );
        try
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE, new Integer(
                modifyMode ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE, ModifyMode.DEFAULT
                .getOrdinal() );
        }

        // modify mode no EMR, DEFAULT if non-numeric or absent 
        String modifyModeNoEMR = ldapUrl.getExtensionValue( X_MODIFY_MODE_NO_EMR );
        try
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR, new Integer(
                modifyModeNoEMR ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_MODE_NO_EMR,
                ModifyMode.DEFAULT.getOrdinal() );
        }

        // modify order, DEL_FIRST if non-numeric or absent 
        String modifyOrder = ldapUrl.getExtensionValue( X_MODIFY_ORDER );
        try
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER, new Integer(
                modifyOrder ).intValue() );
        }
        catch ( NumberFormatException e )
        {
            parameter.setExtendedIntProperty( IBrowserConnection.CONNECTION_PARAMETER_MODIFY_ORDER,
                ModifyOrder.DELETE_FIRST.getOrdinal() );
        }
    }
}
