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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.directory.studio.test.integration.junit5.LdapServersSource.Mode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class LdapServersArgumentsProvider implements ArgumentsProvider
{

    @Override
    public Stream<Arguments> provideArguments( ExtensionContext context ) throws Exception
    {
        // Determine possible server types by checking only/except annotation attributes
        LdapServersSource annotation = context.getTestMethod().get().getAnnotation( LdapServersSource.class );
        List<LdapServerType> types = new ArrayList<>();
        types.addAll( Arrays.asList( annotation.only() ) );
        if ( types.isEmpty() )
        {
            types.addAll( Arrays.asList( LdapServerType.values() ) );
        }
        types.removeAll( Arrays.asList( annotation.except() ) );

        // Filter available server types
        List<LdapServerType> available = types.stream().filter( type -> type.getLdapServer().isAvailable() )
            .collect( Collectors.toList() );

        if ( !available.isEmpty() )
        {
            // Pick a random one
            if ( annotation.mode() == Mode.One )
            {
                available = Collections.singletonList( available.get( new Random().nextInt( available.size() ) ) );
            }

            // Prepare the available servers
            for ( LdapServerType type : available )
            {
                try
                {
                    type.getLdapServer().prepare();
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( "Prepare failed for LDAP server type " + type, e );
                }
            }

            // Return the available/picked servers
            return available.stream().map( LdapServerType::getLdapServer ).map( Arguments::of );
        }
        else
        {
            // Return all types even if not available, will be skipped in SkipTestIfLdapServerIsNotAvailableInterceptor
            return types.stream().map( LdapServerType::getLdapServer ).map( Arguments::of );
        }

    }

}
