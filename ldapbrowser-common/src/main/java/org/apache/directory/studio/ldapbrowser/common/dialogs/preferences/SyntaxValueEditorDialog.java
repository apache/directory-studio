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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueEditorRelation;
import org.apache.directory.studio.valueeditors.ValueEditorManager.ValueEditorExtension;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The SyntaxValueEditorDialog is used to specify
 * value editors for syntaxes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyntaxValueEditorDialog extends Dialog
{

    /** The initial syntax to value editor relation. */
    private SyntaxValueEditorRelation relation;

    /** Map with class name => value editor extension. */
    private SortedMap<String, ValueEditorExtension> class2ValueEditorExtensionMap;

    /** The syntax OIDs. */
    private String[] syntaxOids;

    /** Map with value editor names => class name. */
    private SortedMap<String, String> veName2classMap;

    /** The selected syntax to value editor relation. */
    private SyntaxValueEditorRelation returnRelation;

    /** The OID combo. */
    private Combo oidCombo;

    /** The value editor combo. */
    private Combo valueEditorCombo;


    /**
     * Creates a new instance of SyntaxValueEditorDialog.
     * 
     * @param parentShell the parent shell
     * @param relation the initial syntax to value editor relation
     * @param class2ValueEditorExtensionMap Map with class name => value editor extension
     * @param syntaxOids the syntax OIDs
     */
    public SyntaxValueEditorDialog( Shell parentShell, SyntaxValueEditorRelation relation,
        SortedMap<String, ValueEditorExtension> class2ValueEditorExtensionMap, String[] syntaxOids )
    {
        super( parentShell );
        this.relation = relation;
        this.class2ValueEditorExtensionMap = class2ValueEditorExtensionMap;
        this.syntaxOids = syntaxOids;
        this.returnRelation = null;

        this.veName2classMap = new TreeMap<String, String>();
        for ( ValueEditorExtension vee : class2ValueEditorExtensionMap.values() )
        {
            veName2classMap.put( vee.name, vee.className );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Attribute Value Editor" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnRelation = new SyntaxValueEditorRelation( oidCombo.getText(), ( String ) veName2classMap
            .get( valueEditorCombo.getText() ) );
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Syntax OID:", 1 );
        oidCombo = BaseWidgetUtils.createCombo( c, syntaxOids, -1, 1 );
        if ( relation != null && relation.getSyntaxOID() != null )
        {
            oidCombo.setText( relation.getSyntaxOID() );
        }
        oidCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( c, "Value Editor:", 1 );
        valueEditorCombo = BaseWidgetUtils.createReadonlyCombo( c, veName2classMap.keySet().toArray( new String[0] ),
            -1, 1 );
        if ( relation != null && relation.getValueEditorClassName() != null
            && class2ValueEditorExtensionMap.containsKey( relation.getValueEditorClassName() ) )
        {
            valueEditorCombo.setText( ( class2ValueEditorExtensionMap.get( relation.getValueEditorClassName() ) ).name );
        }
        valueEditorCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        return composite;
    }


    private void validate()
    {
        super.getButton( IDialogConstants.OK_ID ).setEnabled(
            !"".equals( valueEditorCombo.getText() ) && !"".equals( oidCombo.getText() ) );
    }


    /**
     * Gets the selected syntax to value editor relation.
     * 
     * @return the selected syntax to value editor relation
     */
    public SyntaxValueEditorRelation getRelation()
    {
        return returnRelation;
    }

}
