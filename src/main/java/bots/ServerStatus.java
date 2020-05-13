package bots;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.Nonnull;
import javax.xml.soap.Text;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that showing server status every minute.
 * @author Petr Křehlík
 */
public class ServerStatus extends ListenerAdapter {
    /**
     * API object.
     */
    private JDA api;

    /**
     * Show immediately server status everywhere on server if user write "!server-status"
     * @param event Event from API.
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("!server-status")) {
            boolean status = pingHost("178.63.23.23", 28061, 5000);
            String test = "Aktualni stav serveru: " + (status ? "Online" : "Offline");
            event.getChannel().sendMessage(test).queue();
        }
    }

    /**
     * Initialize object.
     * When API is ready starts 1 minute timer to update data.
     * @param event
     */
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        this.api = event.getJDA();
        Timer timer = new Timer("1");
        timer.schedule(updateTask, 0, 60000);
    }

    /**
     * Update status in channel "server-status".
     */
    private void updateStatus() {

        boolean status = pingHost("178.63.23.23", 28061, 5000);
        List<TextChannel> textChannels = api.getTextChannelsByName("server-status", true);

        if (textChannels.isEmpty()) {
            return;
        }
        for(TextChannel textChannel:textChannels)
        {
            String lastMessageId;
            try {
                lastMessageId = textChannel.getHistory().retrievePast(1).complete().get(0).getId();
            } catch (Exception e) {
                System.err.println("server-status: Channel has no messages!");
                return;
            }

            String messageId = textChannel.getLatestMessageId();
            Message message = textChannel.getHistory().retrievePast(1).complete().get(0);

            String test = "Aktualni stav serveru: " + (status ? "Online" : "Offline");

            if(message==null)
            {
                textChannel.sendMessage(test).queue();
                return;
            }

            if(message.getContentDisplay().contains("Aktualni"))
            {
                MessageAction messageAction = textChannel.editMessageById(lastMessageId, test);
                messageAction.queue();
            }else {
                textChannel.sendMessage(test).queue();
            }
        }



    }

    /**
     * Timer task. Run update of status.
     */
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            updateStatus();
        }
    };

    /**
     * Ping IP and port.
     * @param host IP to ping.
     * @param port Port to ping.
     * @param timeout Timeout in ms.
     * @return Return true if ping succeed. False if no response in timeout.
     */
    private boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
