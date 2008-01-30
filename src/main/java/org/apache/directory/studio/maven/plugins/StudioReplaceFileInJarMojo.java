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
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.osgi.DefaultMaven2OsgiConverter;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;


/**
 * Add ro replace file in existing, distributed artifact
 * 
 * @goal replace-file-in-eclipse-artifact
 * @description Add/Replace a file within a given eclipse artifact at a specific
 *              location (zip or jar file)
 * @requiresProject
 * @requiresDependencyResolution runtime
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioReplaceFileInJarMojo extends AbstractStudioMojo
{

    /**
     * Collection of ArtifactItems to work on. (ArtifactItem contains groupId,
     * artifactId, version, type, classifier, location, destFile, markerFile and
     * overwrite.) See "Usage" and "Javadoc" for details.
     * 
     * @parameter
     * @required
     */
    protected ArrayList<ArtifactItem> artifactItems;

    /**
     * FinalName of the jar file
     * 
     * @parameter
     * @required
     */
    private File inputFile;

    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File destinationDirectory;

    /**
     * Tmp work directory for this plugin
     * 
     * @parameter expression="${project.build.directory}/ReplaceFileInJarMojo/"
     * @required
     * @readonly
     */
    private String pluginWorkDir;


    /**
     * @param destinationDirectory
     *            the destinationDirectory to set
     */
    public void setDestinationDirectory( File destinationDirectory )
    {
        this.destinationDirectory = destinationDirectory;
    }


    /**
     * @param inputFile
     *            the inputFile to set
     */
    public void setInputFile( File inputFile )
    {
        this.inputFile = inputFile;
    }


    public void execute() throws MojoExecutionException
    {
        final Maven2OsgiConverter maven2OsgiConverter = new DefaultMaven2OsgiConverter();

        completeArtifactItems( artifactItems );

        // Add file to packed file
        for ( Iterator<ArtifactItem> artifactItem = artifactItems.iterator(); artifactItem.hasNext(); )
        {
            ArtifactItem item = artifactItem.next();
            try
            {
                final File zipFile = new File( destinationDirectory.getAbsoluteFile() + File.separator
                    + maven2OsgiConverter.getBundleFileName( item.getArtifact() ) );
                final File tmpDir = new File( pluginWorkDir + item.getArtifact().getArtifactId() );
                unpackToLocation( tmpDir, zipFile );
                getLog().info( "Adding " + inputFile + " to\n              " + zipFile );
                FileUtils.copyFileToDirectory( inputFile, tmpDir );
                packFromLocation( tmpDir, zipFile );

            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "", e );
            }
        }
    }


    /**
     * @param artifactItems
     *            the artifactItems to set
     */
    public void setArtifactItems( ArrayList<ArtifactItem> artifactItems )
    {
        this.artifactItems = artifactItems;
    }
}
