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

package org.apache.directory.studio.test.integration.junit5;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class LdapServersArgumentsProvider implements ArgumentsProvider
{

    @Override
    public Stream<Arguments> provideArguments( ExtensionContext context ) throws Exception
    {
        List<LdapServerType> types = Arrays
            .asList( context.getTestMethod().get().getAnnotation( LdapServersSource.class ).types() );

        List<Arguments> arguments = new ArrayList<>();

        for ( LdapServerType type : LdapServerType.values() )
        {
            if ( types.contains( type ) )
            {
                try
                {
                    if ( type.getLdapServer().isAvailable() )
                    {
                        type.getLdapServer().prepare();
                    }
                    arguments.add( Arguments.of( type.getLdapServer() ) );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( "Prepare failed for LDAP server type " + type, e );
                }
            }
        }

        return arguments.stream();
    }

}
