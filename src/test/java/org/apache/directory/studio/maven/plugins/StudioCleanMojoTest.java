/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.studio.maven.plugins;


import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.directory.studio.maven.plugins.StudioCleanMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;


/**
 * Test the studio clean mojo.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioCleanMojoTest extends AbstractMojoTestCase
{
    /** {@inheritDoc} */
    protected void setUp() throws Exception
    {
        super.setUp();
    }


    /** {@inheritDoc} */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }


    /**
     * Tests the simple removal of the lib dir and the 2 files 'maven-eclipse.xml' and '.externalToolBuilders'
     *
     * @throws Exception
     */
    public void testBasicClean() throws Exception
    {
        String pluginPom = getBasedir() + "/src/test/resources/unit/basic-clean-test/plugin-pom.xml";

        // safety
        FileUtils.copyDirectory( new File( getBasedir(), "src/test/resources/unit/basic-clean-test" ), new File(
            getBasedir(), "target/test-classes/unit/basic-clean-test" ), null, "**/.svn,**/.svn/**" );

        StudioCleanMojo mojo = ( StudioCleanMojo ) lookupMojo( "clean", pluginPom );
        assertNotNull( mojo );

        mojo.execute();

        assertFalse( "File maven-eclipse.xml exists", checkExists( getBasedir() + "/target/test-classes/unit/"
            + "basic-clean-test/maven-eclipse.xml" ) );
        assertFalse( "File .externalToolBuilder exists", checkExists( getBasedir() + "/target/test-classes/unit/"
            + "basic-clean-test/.externalToolBuilders" ) );
        assertFalse( "Directory exists", checkExists( getBasedir() + "/target/test-classes/unit/basic-clean-test/"
            + "lib" ) );
    }


    /**
     * Tests for exception hanndling - an exception should only be logged, but not be thrown
     *
     * @throws Exception
     */
    public void testBasicCleanException() throws Exception
    {
        String pluginPom = getBasedir() + "/src/test/resources/unit/basic-clean-test/plugin-exception-pom.xml";
        StudioCleanMojo mojo = ( StudioCleanMojo ) lookupMojo( "clean", pluginPom );
        assertNotNull( mojo );

        try
        {
            mojo.execute();
        }
        catch ( Exception e )
        {
            fail( "Exception thrown: " + e.toString() );
        }
    }


    /**
     * @param dir a dir or a file
     * @return true if a file/dir exists, false otherwise
     */
    private boolean checkExists( String dir )
    {
        return FileUtils.fileExists( new File( dir ).getAbsolutePath() );
    }


    /**
     * @param dir a directory
     * @return true if a dir is empty, false otherwise
     */
    private boolean checkEmpty( String dir )
    {
        return FileUtils.sizeOfDirectory( new File( dir ).getAbsolutePath() ) == 0;
    }
}
