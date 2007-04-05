package org.apache.directory.ldapstudio.apacheds.configuration.model;


public class Partition
{

    private String name;


    public Partition( String name )
    {
        this.name = name;
    }


    public String getName()
    {
        return this.name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String toString()
    {
        return name;
    }
}
