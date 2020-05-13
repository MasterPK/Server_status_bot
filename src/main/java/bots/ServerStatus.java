package bots;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

public class ServerStatus extends ListenerAdapter {
    private boolean status = false;
    private JDA api;


    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("!server-status")) {
            event.getChannel().sendMessage("Online").queue();
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        this.api=event.getJDA();
        Timer timer = new Timer("30 sec");
        timer.schedule(updateTask, 0, 60000);
    }

    private void updateStatus() {

        this.status = pingHost("178.63.23.23", 28061, 5000);
        List<TextChannel> textChannels = api.getTextChannelsByName("server-status", true);

        if (textChannels.isEmpty()) {
            return;
        }
        TextChannel textChannel = textChannels.get(0);

        String lastMessageId;
        try {
            lastMessageId = textChannel.getHistory().retrievePast(1).complete().get(0).getId();
        } catch (Exception e) {
            System.err.println("server-status: Channel has no messages!");
            return;
        }



        String test = new String("Aktualni stav serveru: " + (this.status ? "Online" : "Offline"));

        test=new String(test.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        //textChannel.sendMessage(test).queue();

        textChannel.editMessageById(lastMessageId, test).queue();
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            updateStatus();
        }
    };

    private boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
