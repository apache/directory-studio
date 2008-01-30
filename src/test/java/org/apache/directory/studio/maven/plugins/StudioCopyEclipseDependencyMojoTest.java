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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;


/**
 * Test the studio copy eclipse dependency mojo.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioCopyEclipseDependencyMojoTest extends AbstractMojoTestCase
{
    private Set toDelete = new HashSet();
    private ArtifactRepositoryFactory repoFactory;


    /** {@inheritDoc} */
    protected void setUp() throws Exception
    {
        super.setUp();

        repoFactory = ( ArtifactRepositoryFactory ) lookup( ArtifactRepositoryFactory.ROLE );
    }


    /** {@inheritDoc} */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        for ( Iterator it = toDelete.iterator(); it.hasNext(); )
        {
            File f = ( File ) it.next();

            if ( f.exists() )
            {
                try
                {
                    FileUtils.forceDelete( f );
                }
                catch ( IOException e )
                {
                    //the files on windows can still be locked. They were creaed in a temp directory anyway and will get removed in a subsequent clean.
                    //we can safely ignore this error.
                }
            }
        }
    }


    /**
     * Tests the simple removal of the lib dir and the 2 files 'maven-eclipse.xml' and '.externalToolBuilders'
     *
     * @throws Exception
     */
    public void testCopyEclipseArtifact() throws Exception
    {
        String pluginPom = getBasedir() + "/src/test/resources/unit/basic-copy-test/plugin-pom.xml";
        StudioCopyEclipseDependencyMojo mojo = ( StudioCopyEclipseDependencyMojo ) lookupMojo( "copy-eclipse-artifact",
            pluginPom );
        assertNotNull( mojo );

        File remoteRepoDir = findRemoteRepositoryDirectory();
        File localRepo = createTempDir();

        List<ArtifactRepository> remoteRepositories = new ArrayList<ArtifactRepository>();
        remoteRepositories.add( repoFactory.createArtifactRepository( "central", remoteRepoDir.toURI().toURL()
            .toExternalForm(), new DefaultRepositoryLayout(), null, null ) );
        mojo.setRemoteRepos( remoteRepositories );

        DefaultArtifactRepository localRepository = new DefaultArtifactRepository( "local",
            localRepo.getAbsolutePath(), new DefaultRepositoryLayout() );
        localRepository.setBasedir( localRepo.getAbsolutePath() );
        mojo.setLocal( localRepository );

        mojo.execute();

        assertTrue( "File org.eclipse.core.jobs_3.3.1.R33x_v20070709.jar not exists", checkExists( getBasedir()
            + "/target/test-classes/copy-plugins/" + "org.eclipse.core.jobs_3.3.1.R33x_v20070709.jar" ) );
        assertTrue( "File org.eclipse.core.commands_3.3.0.I20070605_0010.jar not exists", checkExists( getBasedir()
            + "/target/test-classes/copy-plugins/" + "org.eclipse.core.commands_3.3.0.I20070605_0010.jar" ) );
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


    private File findRemoteRepositoryDirectory()
    {
        String classPath = getClass().getPackage().getName().replace( '.', '/' ) + "/test-copy-repo/repo-marker.txt";
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        URL resource = cloader.getResource( classPath );

        if ( resource == null )
        {
            throw new IllegalStateException( "Cannot find repository marker file: " + classPath
                + " in context classloader!" );
        }

        File repoDir = new File( resource.getPath() ).getParentFile();

        return repoDir;
    }


    private File createTempDir() throws IOException
    {
        File dir = File.createTempFile( "DefaultExtensionManagerTest.", ".dir" );
        FileUtils.forceDelete( dir );

        dir.mkdirs();
        toDelete.add( dir );

        return dir;
    }

}
