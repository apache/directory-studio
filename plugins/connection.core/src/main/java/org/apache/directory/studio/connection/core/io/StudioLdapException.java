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
package org.apache.directory.studio.connection.core.io;


import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.directory.api.ldap.model.exception.LdapContextNotEmptyException;
import org.apache.directory.api.ldap.model.exception.LdapEntryAlreadyExistsException;
import org.apache.directory.api.ldap.model.exception.LdapOperationException;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;


public class StudioLdapException extends Exception
{
    private static final long serialVersionUID = -1L;

    public StudioLdapException( Exception exception )
    {
        super( exception );
    }


    @Override
    public String getMessage()
    {
        String message = "";
        Throwable cause = getCause();
        if ( cause instanceof LdapOperationException )
        {
            LdapOperationException loe = ( LdapOperationException ) cause;
            ResultCodeEnum rc = loe.getResultCode();
            String template = " [LDAP result code %d - %s]"; //$NON-NLS-1$
            message += String.format( Locale.ROOT, template, rc.getResultCode(), rc.getMessage() );
        }
        if ( StringUtils.isNotBlank( cause.getMessage() ) )
        {
            message += " " + cause.getMessage(); //$NON-NLS-1$
        }
        return message;
    }


    public static boolean isEntryAlreadyExistsException( Exception exception )
    {
        return ExceptionUtils.indexOfThrowable( exception, LdapEntryAlreadyExistsException.class ) > -1;
    }


    public static boolean isContextNotEmptyException( Exception exception )
    {
        return ExceptionUtils.indexOfThrowable( exception, LdapContextNotEmptyException.class ) > -1;
    }

}
