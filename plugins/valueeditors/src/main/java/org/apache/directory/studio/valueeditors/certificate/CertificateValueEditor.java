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

package org.apache.directory.studio.valueeditors.certificate;


import org.apache.directory.studio.ldapbrowser.common.dialogs.TextDialog;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogBinaryValueEditor;
import org.eclipse.swt.widgets.Shell;


/**
 * Implementation of IValueEditor for syntax 1.3.6.1.4.1.1466.115.121.1.8 
 * (Certificate). 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertificateValueEditor extends AbstractDialogBinaryValueEditor
{

    /**
     * {@inheritDoc}
     * 
     * This implementation opens the CertificateDialog.
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();

        if ( value != null && value instanceof byte[] )
        {
            byte[] currentCertificateData = ( byte[] ) value;

            CertificateDialog dialog = new CertificateDialog( shell, currentCertificateData );
            if ( dialog.open() == TextDialog.OK && dialog.getData() != null )
            {
                setValue( dialog.getData() );
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns the certificate info text created by 
     * CertificateDialog.getCertificateInfo().
     */
    public String getDisplayValue( IValue value )
    {
        if ( showRawValues() )
        {
            return getPrintableString( value );
        }
        else
        {
            if ( value == null )
            {
                return "NULL"; //$NON-NLS-1$
            }
            else if ( value.isBinary() )
            {
                byte[] data = value.getBinaryValue();
                String text = CertificateDialog.getCertificateInfo( data );
                return text;
            }
            else
            {
                return Messages.getString( "CertificateValueEditor.InvalidCertificateData" ); //$NON-NLS-1$
            }
        }
    }

}
