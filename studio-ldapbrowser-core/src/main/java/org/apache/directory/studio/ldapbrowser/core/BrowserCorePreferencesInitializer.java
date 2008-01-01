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

package org.apache.directory.studio.ldapbrowser.core;


import org.apache.directory.studio.ldapbrowser.core.model.schema.BinaryAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinarySyntax;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;


/**
 * This class is used to set default preference values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserCorePreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();

        store.setDefault( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN, true );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES, false );

        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER, "," );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_VALUEDELIMITER, "|" );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_QUOTECHARACTER, "\"" );
        store
            .setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_LINESEPARATOR, BrowserCoreConstants.LINE_SEPARATOR );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_BINARYENCODING,
            BrowserCoreConstants.BINARYENCODING_IGNORE );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING, BrowserCoreConstants.DEFAULT_ENCODING );

        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_VALUEDELIMITER, "|" );
        store.setDefault( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_BINARYENCODING,
            BrowserCoreConstants.BINARYENCODING_IGNORE );

        store.setDefault( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH, 76 );
        store.setDefault( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR, BrowserCoreConstants.LINE_SEPARATOR );
        store.setDefault( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON, true );

        // default binary attributes
        BinaryAttribute[] defaultBinaryAttributes = new BinaryAttribute[]
            { new BinaryAttribute( "0.9.2342.19200300.100.1.7" ), // photo
                // //$NON-NLS-1$
                new BinaryAttribute( "0.9.2342.19200300.100.1.53" ), // personalSignature
                // //$NON-NLS-1$
                new BinaryAttribute( "0.9.2342.19200300.100.1.55" ), // audio
                // //$NON-NLS-1$
                new BinaryAttribute( "0.9.2342.19200300.100.1.60" ), // jpegPhoto
                // //$NON-NLS-1$
                new BinaryAttribute( "1.3.6.1.4.1.42.2.27.4.1.8" ), // javaSerializedData
                // //$NON-NLS-1$
                new BinaryAttribute( "1.3.6.1.4.1.1466.101.120.35" ), // thumbnailPhoto
                // //$NON-NLS-1$
                new BinaryAttribute( "1.3.6.1.4.1.1466.101.120.36" ), // thumbnailLogo
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.35" ), // userPassword
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.36" ), // userCertificate
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.37" ), // cACertificate
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.38" ), // authorityRevocationList
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.39" ), // certificateRevocationList
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.40" ), // crossCertificatePair
                // //$NON-NLS-1$
                new BinaryAttribute( "2.5.4.45" ), // x500UniqueIdentifier
            // //$NON-NLS-1$
            };
        BrowserCorePlugin.getDefault().getCorePreferences().setDefaultBinaryAttributes( defaultBinaryAttributes );

        // default binary syntaxes
        BinarySyntax[] defaultBinarySyntaxes = new BinarySyntax[]
            { new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.5" ), // Binary
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.8" ), // Certificate
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.9" ), // Certificate
                // List
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.10" ), // Certificate
                // Pair
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.23" ), // Fax
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.28" ), // JPEG
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.40" ), // Octet
                // String
                // //$NON-NLS-1$
                new BinarySyntax( "1.3.6.1.4.1.1466.115.121.1.49" ) // Supported
            // Algorithm
            // //$NON-NLS-1$
            };
        BrowserCorePlugin.getDefault().getCorePreferences().setDefaultBinarySyntaxes( defaultBinarySyntaxes );
    }
}
