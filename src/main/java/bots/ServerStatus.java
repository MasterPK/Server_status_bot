package bots;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ServerStatus extends ListenerAdapter {
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("!server-status"))
        {
            event.getChannel().sendMessage("Online").queue();
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {

    }
}
