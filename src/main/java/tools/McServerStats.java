package tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
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

    private JSONObject xdefconApi;

    private JSONObject minetoolsApi;

    private long playersCount;

    private List<String> playersList;

    private String serverInfo;

    private boolean online;

    private long maxPlayerCount;

    private double latency;


    public String getValue(String key) throws Exception {
        String returnData = (String) xdefconApi.get(key);
        if (returnData != null) {
            return returnData;
        } else {
            throw new Exception("Specified key does not exist!");
        }
    }

    public McServerStats(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
        update();
    }

    private void update() {

        String serverInfo = doHttpRequest("https://mcapi.xdefcon.com/server/" + this.serverAddress + ":" + this.serverPort + "/full/json/");

        if (serverInfo == null) {
            System.err.println("Error while requesting xdefcon.com API!");
            return;
        }

        JSONParser jsonParser = new JSONParser();
        try {
            this.xdefconApi = (JSONObject) jsonParser.parse(serverInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        serverInfo = doHttpRequest("https://api.minetools.eu/ping/" + this.serverAddress + "/" + this.serverPort);

        if (serverInfo == null) {
            System.err.println("Error while requesting minetools.eu API!");
            return;
        }
        try {
            this.minetoolsApi = (JSONObject) jsonParser.parse(serverInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.setOnline();
        this.setVersion();
        this.setPlayersList();
        this.setPlayersCount();
        this.setMaxPlayersCount();
        this.setLatency();
    }

    public long getLatency() {
        return Math.round(latency);
    }

    private void setLatency() {
        this.latency=(double) this.minetoolsApi.get("latency");
    }

    private void setOnline() {
        this.online = this.xdefconApi.get("serverStatus").equals("online");
    }

    private void setVersion() {
        this.serverInfo = (String) this.xdefconApi.get("version");
    }

    private void setPlayersCount() {
        this.playersCount = (long) this.xdefconApi.get("players");
    }

    private void setPlayersList() {
        this.playersList = new ArrayList<>();

        JSONArray onlinePlayers = (JSONArray) ((JSONObject) this.minetoolsApi.get("players")).get("sample");

        for (Object player : onlinePlayers) {
            this.playersList.add((String) ((JSONObject) player).get("name"));
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

    private void setMaxPlayersCount() {
        this.maxPlayerCount = (long) this.xdefconApi.get("maxplayers");
    }

    public long getMaxPlayersCount() {
        return this.maxPlayerCount;
    }

    public void updateData() {
        this.update();
    }

    private String doHttpRequest(String address) {
        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            System.err.println("Invalid address or port!");
            return null;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            System.err.println("Error while opening connection!");
            return null;
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            System.err.println("Unsupported request method!");
            return null;
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
            return null;
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
        return content.toString();
    }
}
