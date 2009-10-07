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
package org.apache.directory.studio.aciitemeditor.valueeditors;



import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.swt.widgets.Shell;


/**
 * ACI item editor specific value editor to edit the SubtreeSpecification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SubtreeValueEditor extends AbstractDialogStringValueEditor
{
    static final String EMPTY = ""; //$NON-NLS-1$

    private boolean refinementOrFilterVisible;
    
    private boolean useLocalName;


    /**
     * Default constructor, used by the {@link ValueEditorManager}.
     */
    public SubtreeValueEditor()
    {
        this.refinementOrFilterVisible = true;
        this.useLocalName = true;
    }


    /**
     * Default constructor, used by the {@link ValueEditorManager}.
     *
     * @param refinementOrFilterVisible true if the refinement or filter widget should be visible
     * @param useLocalName true to use local name for the base
     */
    public SubtreeValueEditor( boolean refinementOrFilterVisible, boolean useLocalName )
    {
        this.refinementOrFilterVisible = refinementOrFilterVisible;
        this.useLocalName = useLocalName;
    }


    /**
     * @see org.apache.directory.studio.valueeditors.AbstractDialogValueEditor#openDialog(org.eclipse.swt.widgets.Shell)
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();
        if ( value != null && value instanceof SubtreeSpecificationValueWrapper )
        {
            SubtreeSpecificationValueWrapper wrapper = ( SubtreeSpecificationValueWrapper ) value;

            SubtreeSpecificationDialog dialog = new SubtreeSpecificationDialog( shell, wrapper.connection,
                wrapper.subentryDN, wrapper.subtreeSpecification, refinementOrFilterVisible, useLocalName );
            if ( dialog.open() == TextDialog.OK && dialog.getSubtreeSpecificationValue() != null )
            {
                setValue( dialog.getSubtreeSpecificationValue() );
                return true;
            }
        }
        return false;
    }


    /**
     * @see org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor#getRawValue(org.apache.directory.studio.ldapbrowser.core.model.IValue)
     */
    public Object getRawValue( IValue value )
    {
        Object o = super.getRawValue( value );
        if ( o != null && o instanceof String )
        {
            IBrowserConnection connection = value.getAttribute().getEntry().getBrowserConnection();
            LdapDN dn = value.getAttribute().getEntry().getDn();
            return new SubtreeSpecificationValueWrapper( connection, dn, value.getStringValue() );
        }

        return null;
    }


    /**
     * The SubtreeSpecificationValueWrapper is used to pass contextual
     * information to the opened SubtreeSpecificationDialog.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    private class SubtreeSpecificationValueWrapper
    {
        /** The connection, used in DnDialog to browse for an entry */
        private IBrowserConnection connection;

        /** The subentry's DN */
        private LdapDN subentryDN;

        /** The subtreeSpecification */
        private String subtreeSpecification;


        /**
         * Creates a new instance of SubtreeSpecificationValueWrapper.
         *
         * @param connection
         *      the connection
         * @param subentryDn
         *      the DN of the subentry
         * @param subtreeSpecification
         *      the subtreeSpecification
         */
        private SubtreeSpecificationValueWrapper( IBrowserConnection connection, LdapDN subentryDN, String subtreeSpecification )
        {
            this.connection = connection;
            this.subentryDN = subentryDN;
            this.subtreeSpecification = subtreeSpecification;
        }


        /**
         * {@inheritDoc}
         */
        public String toString()
        {
            return subtreeSpecification == null ? "" : subtreeSpecification; //$NON-NLS-1$
        }

    }
}
