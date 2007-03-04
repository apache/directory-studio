package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;


/**
 * ListContentProposalProvider is a class designed to map a dynamic list of
 * Strings to content proposals.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ListContentProposalProvider implements IContentProposalProvider
{

    /** The dynamic list of proposals */
    private List<String> proposals;


    /**
     * Creates a new instance of ListContentProposalProvider.
     *
     * @param proposals the dynamic list of proposals
     */
    public ListContentProposalProvider( List<String> proposals )
    {
        setProposals( proposals );
    }


    /**
     * Creates a new instance of ListContentProposalProvider.
     *
     * @param proposals the proposals
     */
    public ListContentProposalProvider( String[] proposals )
    {
        setProposals( new ArrayList<String>( Arrays.asList( proposals ) ) );
    }


    /**
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals( String contents, int position )
    {
        String string = contents.substring( 0, position );

        Collections.sort( proposals );

        List<IContentProposal> proposalList = new ArrayList<IContentProposal>();
        for ( int k = 0; k < proposals.size(); k++ )
        {
            final String proposal = proposals.get( k );
            if ( proposal.toUpperCase().startsWith( string.toUpperCase() ) && !proposal.equalsIgnoreCase( string )
                && !"".equals( string ) )
            {
                IContentProposal p = new IContentProposal()
                {
                    public String getContent()
                    {
                        return proposal;
                    }


                    public String getDescription()
                    {
                        return proposal;
                    }


                    public String getLabel()
                    {
                        return proposal;
                    }


                    public int getCursorPosition()
                    {
                        return proposal.length();
                    }
                };
                proposalList.add( p );
            }
        }
        return proposalList.toArray( new IContentProposal[proposalList.size()] );
    }


    /**
     * Sets the possible strings.
     * 
     * @param proposals the possible strings
     */
    public void setProposals( List<String> proposals )
    {
        if ( proposals == null )
        {
            proposals = new ArrayList<String>();
        }

        this.proposals = proposals;
    }

}
