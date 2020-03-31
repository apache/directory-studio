package org.apache.directory.studio.test.integration.ui.bots;


import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;


public class PasswordModifyExtendedOperationDialogBot extends DialogBot
{
    public PasswordModifyExtendedOperationDialogBot()
    {
        super( "Password Modify Extended Operation (RFC 3062)" );
    }


    public String getUserIdentity()
    {
        return bot.comboBox().getText();
    }


    public void setUserIdentity( String text )
    {
        bot.comboBox().setText( text );
    }


    public boolean useBindUserIdentity()
    {
        return bot.checkBox( 0 ).isChecked();
    }


    public void useBindUserIdentity( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 0 ).select();
        }
        else
        {
            bot.checkBox( 0 ).deselect();
        }
    }


    public String getOldPassword()
    {
        return bot.text( 0 ).getText();
    }


    public void setOldPassword( String text )
    {
        bot.text( 0 ).setText( text );
    }


    public boolean noOldPassword()
    {
        return bot.checkBox( 1 ).isChecked();
    }


    public void noOldPassword( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 1 ).select();
        }
        else
        {
            bot.checkBox( 1 ).deselect();
        }
    }


    public String getNewPassword()
    {
        return bot.text( 1 ).getText();
    }


    public void setNewPassword( String text )
    {
        bot.text( 1 ).setText( text );
    }


    public boolean generateNewPassword()
    {
        return bot.checkBox( 2 ).isChecked();
    }


    public void generateNewPassword( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 2 ).select();
        }
        else
        {
            bot.checkBox( 2 ).deselect();
        }
    }


    public ErrorDialogBot clickOkButtonExpectingErrorDialog()
    {
        SWTBotShell shell = BotUtils.shell( new Runnable()
        {
            public void run()
            {
                clickOkButton();
            }
        }, "Error" );
        String shellText = shell.getText();

        return new ErrorDialogBot( shellText );
    }

}
