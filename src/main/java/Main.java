import bots.ServerStatus;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args)
    {
        JDABuilder jdaBuilder = JDABuilder.createDefault("NzEwMDk1MzI4MTQ1MDQ3Njcy.XrwCcQ.DrqMVkIvaScDIthn1FApYWoL-_c");

        jdaBuilder.addEventListeners(new ServerStatus());
        try {
            jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }


}
