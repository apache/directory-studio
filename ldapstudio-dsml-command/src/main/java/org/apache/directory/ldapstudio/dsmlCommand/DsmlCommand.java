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

package org.apache.directory.ldapstudio.dsmlCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This class implements the DSMLv2 Command Line.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DsmlCommand
{
    /** The options of the command */
    private static Options options = new Options();
    
    // Default values
    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_PORT = 10389;
    private final static String DEFAULT_USER = "uid=admin, ou=system";
    private final static String DEFAULT_PASSWORD = "secret";
    
    // Options fields
    private boolean debug = false;
    private String host;
    private int port;
    private String user;
    private String password;
    private String input;
    private String output;
    
    
    public static void main( String[] args )
    {
        // Configuring log4j
        PropertyConfigurator.configure( DsmlCommand.class.getClassLoader().getResource( "log4j.conf" ) ); //$NON-NLS-1$
        
        if ( args.length == 0 )
        {
            System.err.println( "Type help for usage." );
            System.exit( 1 );
        }
        
        init();
        
        if ( "help".equals( args[0] ) )
        {
            printHelp();
            System.exit( 0 );
        }
        
        DsmlCommand dsmlCommand = new DsmlCommand();
        
        CommandLine cmd = dsmlCommand.getCommandLine( args );
        
        dsmlCommand.processOptions( cmd );
        
        dsmlCommand.processCommand( cmd );
        
        System.exit( 0 );
    }


    /**
     * Processes the given Command Line
     * @param cmd a Command Line
     */
    private void processCommand( CommandLine cmd )
    {
        if ( debug ) 
        {
            System.out.println( "host: " + host );
            System.out.println( "port: " + port );
            System.out.println( "user: " + user );
            System.out.println( "password: " + password );
            System.out.println( "input: " + input );
            if ( output != null )
            {
                System.out.println( "output: " + output );
            }
            else
            {
                System.out.println( "output: console");
            }
            System.out.println( "----------" );
        }
        
        Dsmlv2Engine engine = new Dsmlv2Engine( host, port, user, password);
        
        
        // Processing DSMLv2 input file on the server 
        String response = null;
        try
        {
            response = engine.processDSMLFile( input );
        }
        catch ( FileNotFoundException e )
        {
            System.err.println( "The input file could not been found.");
            System.exit( 1 );
        }
        catch ( XmlPullParserException e )
        {
            System.err.println( "An error ocurred when parsing the input file.");
            System.err.println( "Error: " + e.getMessage() );
            System.exit( 1 );
        }
        
        if ( output == null )
        {
            // If output hasn't be defined, the standard
            System.out.println( response );
        }
        else
        {
            // If an output file has been defined, we write in it 
            File outputFile = new File( output );
            
            FileWriter fw = null;
            try
            {
                fw = new FileWriter( outputFile );

                fw.write( response );
                
                fw.close();
            }
            catch ( IOException e )
            {
                System.err.println( "An error ocurred while writing file to disk." );
                System.err.println( "Error: " + e.getMessage() );
                System.exit( 1 );
            }
            
            System.out.println( "Done" );
        }
    }


    /**
     * Inits the command options.
     */
    private static void init()
    {
        Option op = new Option( "d", "debug", false, "toggle debug mode" );
        op.setRequired( false );
        options.addOption( op );
        op = new Option( "h", "host", true, "the server host: defaults to localhost" );
        op.setRequired( false );
        options.addOption( op );
        op = new Option( "p", "port", true, "the server port: defaults to 10389" );
        op.setRequired( false );
        options.addOption( op );
        op = new Option( "u", "user", true, "the user dn: default to uid=admin, ou=system" );
        op.setRequired( false );
        options.addOption( op );
        op = new Option( "w", "password", true, "the user's password: defaults to secret" );
        op.setRequired( false );
        options.addOption( op );
        op = new Option( "i", "input", true, "the input DSMLv2 file" );
        op.setRequired( true );
        options.addOption( op );
        op = new Option( "o", "output", true, "the output DSMLv2 file: if this option is ommited, the response will be printed out." );
        op.setRequired( false );
        options.addOption( op );
    }
    
    
    /**
     * Converts received args[] into a Command Line
     * @param args an arguments array
     * @return the corresponding Command Line
     */
    private CommandLine getCommandLine( String[] args )
    {
        CommandLineParser parser = new PosixParser();
        CommandLine cmdline = null;
        try
        {
            cmdline = parser.parse( options, args );
        }
        catch ( AlreadySelectedException ase )
        {
            System.err.println( "Command line parsing failed.  Reason: already selected "
                + ase.getMessage() );
            System.exit( 1 );
        }
        catch ( MissingArgumentException mae )
        {
            System.err.println( "Command line parsing failed.  Reason: missing argument "
                + mae.getMessage() );
            System.exit( 1 );
        }
        catch ( MissingOptionException moe )
        {
            System.err.println( "Command line parsing failed.  Reason: missing option "
                + moe.getMessage() );
            System.exit( 1 );
        }
        catch ( UnrecognizedOptionException uoe )
        {
            System.err.println( "Command line parsing failed.  Reason: unrecognized option"
                + uoe.getMessage() );
            System.exit( 1 );
        }
        catch ( ParseException pe )
        {
            System.err.println( "Command line parsing failed.  Reason: " + pe.getClass() );
            System.exit( 1 );
        }

        return cmdline;
    }

    /**
     * Prints Help on the console
     */
    private static void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "<command> [options]", "\nOptions:", options, "" );

    }
    
    
    /**
     * Processes the Options of a given Command Line
     * @param cmd the Command Line
     * @throws NumberFormatException when the port value couldn't be parsed correctly
     */
    private void processOptions(CommandLine cmd) throws NumberFormatException
    {
        // DEBUG
        if ( cmd.hasOption( "d" ) )
        {
            debug = true;
        }
        
        // HOST
        if ( cmd.hasOption( "h" ) )
        {
            host = cmd.getOptionValue( "h" );
        }
        else
        {
            host = DEFAULT_HOST;
        }
        
        // PORT
        if ( cmd.hasOption( "p" ) )
        {
            try
            {
                port = Integer.parseInt( cmd.getOptionValue( "p" ) );
            }
            catch (NumberFormatException e) {
                System.err.println("port must be an integer.");
                System.exit( 1 );
            }
            // TODO add a verification to check if port is between XXX and XXX.
        }
        else
        {
            port = DEFAULT_PORT;
        }
        
        // USER
        if ( cmd.hasOption( "u" ) )
        {
            user = cmd.getOptionValue( "u" );
        }
        else
        {
            user = DEFAULT_USER;
        }
        
        // PASSWORD
        if ( cmd.hasOption( "w" ) )
        {
            password = cmd.getOptionValue( "w" );
        }
        else
        {
            password = DEFAULT_PASSWORD;
        }
        
        // INPUT
        if ( cmd.hasOption( "i" ) )
        {
            input = cmd.getOptionValue( "i" );
        }
        
        // OUTPUT
        if ( cmd.hasOption( "o" ) )
        {
            output = cmd.getOptionValue( "o" );
        }
    }
}
