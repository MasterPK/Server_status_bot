package tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class McServerStats {

    private String serverAddress;

    private int serverPort;

    private JSONObject jsonData;

    private long playersCount;

    private List<String> playersList;

    private String serverInfo;

    private boolean online;

    private long maxPlayerCount;


    public String getValue(String key) throws Exception {
        String returnData = (String) jsonData.get(key);
        if (returnData != null) {
            return returnData;
        } else {
            throw new Exception("Specified key does not exist!");
        }
    }

    public McServerStats(String address, int port) {
        this.serverAddress=address;
        this.serverPort=port;
        update();
    }

    private void update(){
        URL url;
        try {
            url = new URL("https://api.mcsrvstat.us/2/" + this.serverAddress + ":" + this.serverPort);
        } catch (MalformedURLException e) {
            System.err.println("Invalid address or port!");
            return;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.err.println("Error while opening connection!");
            return;
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            System.err.println("Unsupported request method!");
            return;
        }

        con.setRequestProperty("Content-Type", "application/json");

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);


        int status = 0;
        try {
            status = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status != 200) {
            return;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = "";
        StringBuilder content = new StringBuilder();
        while (true) {
            try {
                if (in != null && (inputLine = in.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            content.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser();
        try {
            this.jsonData = (JSONObject) jsonParser.parse(content.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.setOnline();
        this.setVersion();
        this.setPlayersList();
        this.setPlayersCount();
        this.setMaxPlayersCount();
    }

    private void setOnline()
    {
        this.online = (boolean) this.jsonData.get("online");
    }

    private void setVersion()
    {
        String version = (String) this.jsonData.get("version");
        String software = (String) this.jsonData.get("software");

        this.serverInfo = version + "/" + software;
    }

    private void setPlayersCount() {
        this.playersCount = this.playersList.size();
    }

    private void setPlayersList() {
        this.playersList = new ArrayList<>();

        JSONObject playersJson = (JSONObject) this.jsonData.get("players");

        JSONArray onlinePlayers = (JSONArray) playersJson.get("list");

        for (Object player : onlinePlayers) {
            this.playersList.add(player.toString());
        }

    }

    public long getOnlinePlayersCount() {
        return this.playersCount;
    }

    public List<String> getOnlinePlayers() {
        return this.playersList;
    }

    public String getVersion() {
        return this.serverInfo;
    }

    public boolean isOnline() {
        return this.online;
    }

    private void setMaxPlayersCount()
    {
        JSONObject playersJson = (JSONObject) this.jsonData.get("players");
        this.maxPlayerCount=(long) playersJson.get("max");
    }

    public long getMaxPlayersCount()
    {
        return this.maxPlayerCount;
    }

    public void updateData()
    {
        this.update();
    }
}
