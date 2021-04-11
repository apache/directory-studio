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


import java.lang.reflect.Method;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;


public class SkipTestIfLdapServerIsNotAvailableInterceptor implements InvocationInterceptor
{

    @Override
    public void interceptTestTemplateMethod( Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext ) throws Throwable
    {
        invocationContext.getArguments().stream()
            .filter( TestLdapServer.class::isInstance )
            .map( TestLdapServer.class::cast )
            .forEach( server -> {
                if ( !server.isAvailable() )
                {
                    invocation.skip();
                    Assumptions.assumeTrue( false,
                        "Skip test because server " + server.getType() + " is not available" );
                }
            } );
        invocation.proceed();
    }

}
