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
package org.apache.directory.studio.maven.plugins;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * Prepares for jar: Copy artifacts nonscoped "provided" to
 * ${project.build.outputDirectory}/libraryPath
 * 
 * @goal prepare-jar-package
 * @phase process-resources
 * @aggregate
 * @description Prepares for jar: Copy artifacts nonscoped "provided" to
 *              ${project.build.outputDirectory}/libraryPath
 * @requiresProject
 * @requiresDependencyResolution runtime
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioPrepareJarPackageMojo extends AbstractStudioMojo
{

    /**
     * Directory containing the classes.
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @readonly
     * @required
     */
    private File classesOutDir;


    public void execute() throws MojoExecutionException
    {
        try
        {
            // Create list of used artifacts
            final List<Artifact> artifactList = createArtifactList();

            // copy Artifacts
            copyArtifacts( artifactList );

        }
        catch ( Exception e )
        {
            getLog().error( e );
        }
    }


    /**
     * Copy artifacts to ${basedir}/lib
     * 
     * @param list
     * @throws IOException
     */
    private void copyArtifacts( final List<Artifact> list ) throws IOException
    {
        // Only proceed when we have artifacts to process
        if ( !list.isEmpty() )
        {
            final File copyDir = new File( classesOutDir, libraryPath );

            if ( !copyDir.exists() )
                copyDir.mkdirs();

            for ( Artifact artifact : list )
            {
                final File destFile = new File( copyDir, artifact.getFile().getName() );
                FileUtils.copyFile( artifact.getFile(), destFile );
                getLog().info( "Copying " + artifact.getFile() + " to\n               " + destFile );
            }
        }
    }
}
