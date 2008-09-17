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
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.osgi.DefaultMaven2OsgiConverter;
import org.apache.maven.shared.osgi.Maven2OsgiConverter;


/**
 * Copy a set of artifacts to a configured output directory by replacing the
 * separator between artifactId and version by an underscore instead of a dash.
 * 
 * @goal copy-eclipse-artifact
 * @description Replace the separator between artifactId and version by an
 *              underscore instead of a dash and copy the artifact to a given
 *              destination directory
 * @requiresProject
 * @requiresDependencyResolution runtime
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioCopyEclipseDependencyMojo extends AbstractStudioMojo
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
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File destinationDirectory;


    /**
     * @param destinationDirectory
     *            the destinationDirectory to set
     */
    public void setDestinationDirectory( File destinationDirectory )
    {
        this.destinationDirectory = destinationDirectory;
    }


    public void execute() throws MojoExecutionException
    {
        final Maven2OsgiConverter maven2OsgiConverter = new DefaultMaven2OsgiConverter();
        completeArtifactItems( artifactItems );

        if ( !destinationDirectory.exists() && !destinationDirectory.mkdirs() )
        {
            throw new MojoExecutionException( "Can't create directory " + destinationDirectory );
        }

        for ( Iterator<ArtifactItem> artifactItem = artifactItems.iterator(); artifactItem.hasNext(); )
        {
            ArtifactItem item = artifactItem.next();
            final File destFile = new File( destinationDirectory.getAbsoluteFile() + File.separator
                + maven2OsgiConverter.getBundleFileName( item.getArtifact() ) );
            getLog().info(
                "Copying artifact " + item.getArtifactId() + " to\n               " + destFile.getAbsolutePath() );
            try
            {
                FileUtils.copyFile( item.getArtifact().getFile(), destFile );
            }
            catch ( IOException ioe )
            {
                throw new MojoExecutionException( "Can't copy file.", ioe );
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
