package com.raftechnology.hidoyvpn;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServerListActivity extends AppCompatActivity {

    public static final String EXTRA_COUNTRY = "extra_country";
    public static final String EXTRA_FLAG = "extra_flag";
    public static final String EXTRA_CONFIG_ASSET = "extra_config_asset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_server_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.servera), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        List<Server> servers = buildServerList();

        String currentCountry = getIntent().getStringExtra(EXTRA_COUNTRY);
        int selectedPosition = 0;
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).getCountryName().equals(currentCountry)) {
                selectedPosition = i;
                break;
            }
        }

        RecyclerView rvServers = findViewById(R.id.rvServers);
        rvServers.setLayoutManager(new LinearLayoutManager(this));
        rvServers.setAdapter(new ServerAdapter(servers, selectedPosition, (server, position) -> {
            Intent result = new Intent();
            result.putExtra(EXTRA_COUNTRY, server.getCountryName());
            result.putExtra(EXTRA_FLAG, server.getFlagEmoji());
            result.putExtra(EXTRA_CONFIG_ASSET, server.getConfigAssetName());
            setResult(RESULT_OK, result);
            finish();
        }));
    }

    // 10 country servers - replace configAssetName with your actual .ovpn asset file names
    // and pingMs with a real measured value if you add live ping checks later.
    private List<Server> buildServerList() {
        List<Server> list = new ArrayList<>();
        list.add(new Server("United States", "🇺🇸", "us.ovpn", 180));
        list.add(new Server("Viet Nam", "\uD83C\uDDFB\uD83C\uDDF3", "vt.ovpn", 160));
        list.add(new Server("Thailand", "\uD83C\uDDF9\uD83C\uDDED", "th.ovpn", 150));
        list.add(new Server("Germany", "\uD83C\uDDE9\uD83C\uDDEA", "gy.ovpn", 60));
        list.add(new Server("Japan", "🇯🇵", "newvp.ovpn", 90));
        list.add(new Server("Russian", "\uD83C\uDDF7\uD83C\uDDFA", "ru.ovpn", 40));
        list.add(new Server("Ecuador", "\uD83C\uDDEA\uD83C\uDDE8", "ec.ovpn", 190));
        list.add(new Server("Korea", "\uD83C\uDDF0\uD83C\uDDF7", "ko.ovpn", 155));
        list.add(new Server("France", "🇫🇷", "fr.ovpn", 165));
        list.add(new Server("Lao People's ", "\uD83C\uDDF1\uD83C\uDDE6", "la.ovpn", 130));
        return list;
    }
}