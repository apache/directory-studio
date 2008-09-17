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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;


/**
 * The abstract studio mojo
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractStudioMojo extends AbstractMojo
{

    /**
     * To look up Archiver/UnArchiver implementations
     * 
     * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
     * @required
     * @readonly
     */
    protected ArchiverManager archiverManager;

    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected String buildDirectory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @parameter expression="${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * Flag if execution shall be skipped. Defaults to true.
     * 
     * @parameter expression="true"
     * @required
     */
    protected boolean skip;

    /**
     * Relativ Path to copy libraries to. Defaults to lib
     * 
     * @parameter expression="lib"
     * @required
     * @readonly
     */
    protected String libraryPath;

    /**
     * Artifact collector, needed to resolve dependencies.
     * 
     * @component role="org.apache.maven.artifact.resolver.ArtifactCollector"
     * @required
     * @readonly
     */
    protected ArtifactCollector artifactCollector;

    /**
     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
     *            hint="maven"
     * @required
     * @readonly
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepos;

    /**
     * POM
     * 
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    /**
     * Output absolute filename for resolved artifacts
     * 
     * @optional
     * @since 2.0
     * @parameter expression="${outputAbsoluteArtifactFilename}"
     *            default-value="false"
     */
    protected boolean outputAbsoluteArtifactFilename;


    /**
     * Unpack a file to a location
     * 
     * @param location
     *            The location to unpack the file to
     * @param file
     *            The file to unpack
     */
    protected void unpackToLocation( final File location, final File file ) throws Exception
    {
        try
        {
            getLog().info( "Unpacking " + file + " to\n                 " + location );
            location.mkdirs();
            UnArchiver unArchiver = archiverManager.getUnArchiver( file );
            unArchiver.setSourceFile( file );
            unArchiver.setDestDirectory( location );
            unArchiver.extract();
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Unknown archiver type", e );
        }
        catch ( ArchiverException e )
        {
            e.printStackTrace();
            throw new MojoExecutionException( "Error unpacking file: " + file + " to: " + location + "\r\n"
                + e.toString(), e );
        }
    }


    /**
     * Pack a given location into a file
     * 
     * @param location
     *            A location to pack
     * @param file
     *            The file to pack the location into
     * @throws Exception
     *             If an error occurs
     */
    protected void packFromLocation( final File location, final File file ) throws MojoExecutionException
    {
        try
        {
            getLog().info( "Packing " + location + " to\n               " + file );
            Archiver archiver = archiverManager.getArchiver( file );
            archiver.setDestFile( file );
            archiver.setIncludeEmptyDirs( true );
            archiver.addDirectory( location );
            archiver.createArchive();
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Unknown archiver type", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating archive", e );
        }
        catch ( ArchiverException e )
        {
            e.printStackTrace();
            throw new MojoExecutionException( "Error packing file: " + file + " to: " + location + "\r\n"
                + e.toString(), e );
        }
    }


    /**
     * Resolves the Artifact from the remote repository if nessessary. If no
     * version is specified, it will be retrieved from the dependency list or
     * from the DependencyManagement section of the pom.
     * 
     * @param artifactItem
     *            containing information about artifact from plugin
     *            configuration.
     * @return Artifact object representing the specified file.
     * 
     * @throws MojoExecutionException
     *             with a message if the version can't be found in
     *             DependencyManagement.
     */
    protected Artifact getArtifact( ArtifactItem artifactItem ) throws MojoExecutionException
    {
        Artifact artifact;

        VersionRange vr;
        try
        {
            vr = VersionRange.createFromVersionSpec( artifactItem.getVersion() );
        }
        catch ( InvalidVersionSpecificationException e1 )
        {
            e1.printStackTrace();
            vr = VersionRange.createFromVersion( artifactItem.getVersion() );
        }

        if ( StringUtils.isEmpty( artifactItem.getClassifier() ) )
        {
            artifact = factory.createDependencyArtifact( artifactItem.getGroupId(), artifactItem.getArtifactId(), vr,
                artifactItem.getType(), null, Artifact.SCOPE_COMPILE );
        }
        else
        {
            artifact = factory.createDependencyArtifact( artifactItem.getGroupId(), artifactItem.getArtifactId(), vr,
                artifactItem.getType(), artifactItem.getClassifier(), Artifact.SCOPE_COMPILE );
        }

        try
        {
            resolver.resolve( artifact, remoteRepos, local );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Unable to resolve artifact.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to find artifact.", e );
        }

        return artifact;
    }


    /**
     * Tries to find missing version from dependancy list and dependency
     * management. If found, the artifact is updated with the correct version.
     * 
     * It will first look for an exact match on
     * artifactId/groupId/classifier/type and if it doesn't find a match, it
     * will try again looking for artifactId and groupId only.
     * 
     * @param artifact
     *            representing configured file.
     * @throws MojoExecutionException
     */
    protected void fillMissingArtifactVersion( ArtifactItem artifact ) throws MojoExecutionException
    {
        if ( !findDependencyVersion( artifact, project.getDependencies(), false )
            && ( project.getDependencyManagement() == null || !findDependencyVersion( artifact, project
                .getDependencyManagement().getDependencies(), false ) )
            && !findDependencyVersion( artifact, project.getDependencies(), true )
            && ( project.getDependencyManagement() == null || !findDependencyVersion( artifact, project
                .getDependencyManagement().getDependencies(), true ) ) )
        {
            throw new MojoExecutionException( "Unable to find artifact version of " + artifact.getGroupId() + ":"
                + artifact.getArtifactId() + " in either dependency list or in project's dependency management." );
        }
    }


    /**
     * Tries to find missing version from a list of dependencies. If found, the
     * artifact is updated with the correct version.
     * 
     * @param artifact
     *            representing configured file.
     * @param list
     *            list of dependencies to search.
     * @param looseMatch
     *            only look at artifactId and groupId
     * @return the found dependency
     */
    protected boolean findDependencyVersion( ArtifactItem artifact, List<Dependency> list, boolean looseMatch )
    {
        boolean result = false;

        for ( int i = 0; i < list.size(); i++ )
        {
            Dependency dependency = list.get( i );
            if ( StringUtils.equals( dependency.getArtifactId(), artifact.getArtifactId() )
                && StringUtils.equals( dependency.getGroupId(), artifact.getGroupId() )
                && ( looseMatch || StringUtils.equals( dependency.getClassifier(), artifact.getClassifier() ) )
                && ( looseMatch || StringUtils.equals( dependency.getType(), artifact.getType() ) ) )
            {

                artifact.setVersion( dependency.getVersion() );
                result = true;
                break;
            }
        }
        return result;
    }


    /**
     * Complete the artifacts in the artifactItems list (e.g. complete with
     * version number)
     * 
     * @throws MojoExecutionException
     */
    protected void completeArtifactItems( List<ArtifactItem> artifactItems ) throws MojoExecutionException
    {
        try
        {
            // Get and complete artifacts
            for ( Iterator<ArtifactItem> artifactItem = artifactItems.iterator(); artifactItem.hasNext(); )
            {
                ArtifactItem item = artifactItem.next();
                // make sure we have a version.
                if ( StringUtils.isEmpty( item.getVersion() ) )
                {
                    fillMissingArtifactVersion( item );
                }
                item.setArtifact( this.getArtifact( item ) );
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "", e );
        }
    }


    /**
     * Delete a directory
     * 
     * @param path
     * @return
     */
    protected static boolean deleteDirectory( File path )
    {
        if ( path.exists() && path.isDirectory() )
        {
            for ( File file : path.listFiles() )
            {
                if ( file.isDirectory() )
                {
                    deleteDirectory( file );
                }
                else
                {
                    file.delete();
                }
            }
        }
        return ( path.delete() );
    }


    /**
     * Return a list of artifacts nonscoped "provided"
     * 
     * @return
     */
    protected List<Artifact> createArtifactList()
    {
        List<Artifact> list = new ArrayList<Artifact>();
        
        // Copying only artifacts with 'provided' scope
        for ( Iterator<Artifact> artifactItem = project.getArtifacts().iterator(); artifactItem.hasNext(); )
        {
            Artifact artifact = ( Artifact ) artifactItem.next();
            if ( !artifact.getScope().equalsIgnoreCase( "provided" ) )
            {
                list.add( artifact );
            }
        }

        // Sorting list before returning it
        Collections.sort( list, new Comparator<Artifact>()
        {
            public int compare( Artifact o1, Artifact o2 )
            {
                String artifactId1 = o1.getArtifactId();
                String artifactId2 = o2.getArtifactId();

                if ( ( artifactId1 != null ) && ( artifactId2 != null ) )
                {
                    return artifactId1.compareToIgnoreCase( artifactId2 );
                }

                // Default
                return o1.toString().compareToIgnoreCase( o2.toString() );
            }
        } );
        
        return list;
    }


    /**
     * @return Returns the factory.
     */
    public org.apache.maven.artifact.factory.ArtifactFactory getFactory()
    {
        return this.factory;
    }


    /**
     * @param factory
     *            The factory to set.
     */
    public void setFactory( org.apache.maven.artifact.factory.ArtifactFactory factory )
    {
        this.factory = factory;
    }


    /**
     * @return Returns the project.
     */
    public MavenProject getProject()
    {
        return this.project;
    }


    /**
     * @return Returns the local.
     */
    public org.apache.maven.artifact.repository.ArtifactRepository getLocal()
    {
        return this.local;
    }


    /**
     * @param local
     *            The local to set.
     */
    public void setLocal( org.apache.maven.artifact.repository.ArtifactRepository local )
    {
        this.local = local;
    }


    /**
     * @return Returns the remoteRepos.
     */
    public List<ArtifactRepository> getRemoteRepos()
    {
        return this.remoteRepos;
    }


    /**
     * @param remoteRepos
     *            The remoteRepos to set.
     */
    public void setRemoteRepos( List<ArtifactRepository> remoteRepos )
    {
        this.remoteRepos = remoteRepos;
    }


    /**
     * @return Returns the resolver.
     */
    public org.apache.maven.artifact.resolver.ArtifactResolver getResolver()
    {
        return this.resolver;
    }


    /**
     * @param resolver
     *            The resolver to set.
     */
    public void setResolver( org.apache.maven.artifact.resolver.ArtifactResolver resolver )
    {
        this.resolver = resolver;
    }


    /**
     * @return Returns the artifactCollector.
     */
    public ArtifactCollector getArtifactCollector()
    {
        return this.artifactCollector;
    }


    /**
     * @param theArtifactCollector
     *            The artifactCollector to set.
     */
    public void setArtifactCollector( ArtifactCollector theArtifactCollector )
    {
        this.artifactCollector = theArtifactCollector;
    }


    /**
     * @return Returns the artifactMetadataSource.
     */
    public ArtifactMetadataSource getArtifactMetadataSource()
    {
        return this.artifactMetadataSource;
    }


    /**
     * @param theArtifactMetadataSource
     *            The artifactMetadataSource to set.
     */
    public void setArtifactMetadataSource( ArtifactMetadataSource theArtifactMetadataSource )
    {
        this.artifactMetadataSource = theArtifactMetadataSource;
    }


    /**
     * @param libraryPath
     *            the libraryPath to set
     */
    public void setLibraryPath( String pLibraryPath )
    {
        this.libraryPath = pLibraryPath;
    }
}