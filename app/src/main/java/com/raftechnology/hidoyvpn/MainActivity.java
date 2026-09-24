package com.raftechnology.hidoyvpn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ai.bongotech.bongovpn.BongoVpn;

public class MainActivity extends AppCompatActivity {

    BongoVpn bongoVpn = new BongoVpn(this);

    TextView tvText, Tvspeed, tvConnectLabel, tvServerName, tvServerFlag;
    FrameLayout connectvpn;
    CardView cardServerSelector;
    ImageView ivProfile;
    Animation jumpBounce;
    boolean isconnect = false;

    // Currently selected server - defaults to a Bangladesh-friendly auto choice
    String selectedCountry = "Auto (Fastest)";
    String selectedFlag = "🇧🇩";
    String selectedConfigAsset = "newvp.ovpn";

    ActivityResultLauncher<Intent> serverListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    selectedCountry = data.getStringExtra(ServerListActivity.EXTRA_COUNTRY);
                    selectedFlag = data.getStringExtra(ServerListActivity.EXTRA_FLAG);
                    selectedConfigAsset = data.getStringExtra(ServerListActivity.EXTRA_CONFIG_ASSET);
                    tvServerName.setText(selectedCountry);
                    tvServerFlag.setText(selectedFlag);

                    // Reattach the profile so the next Connect uses the newly picked country.
                    // Make sure each configAssetName above matches a real .ovpn file in assets/.
                    if (!isconnect) {
                        bongoVpn.attachFromAsset(selectedConfigAsset, "vpn", "vpn");
                    }
                }
            }
    );

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvText = findViewById(R.id.tvText);
        Tvspeed = findViewById(R.id.speed);
        tvConnectLabel = findViewById(R.id.tvConnectLabel);
        connectvpn = findViewById(R.id.connectvpn);
        ivProfile = findViewById(R.id.ivProfile);
        cardServerSelector = findViewById(R.id.cardServerSelector);
        tvServerName = findViewById(R.id.tvServerName);
        tvServerFlag = findViewById(R.id.tvServerFlag);

        jumpBounce = AnimationUtils.loadAnimation(this, R.anim.jump_bounce);

        bongoVpn.attachFromAsset(selectedConfigAsset, "vpn", "vpn");

        connectvpn.setOnClickListener(v -> {
            connectvpn.setEnabled(false);
            if (isconnect) {
                bongoVpn.stopVpn();
            } else {
                connectvpn();
            }
        });

        cardServerSelector.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServerListActivity.class);
            intent.putExtra(ServerListActivity.EXTRA_COUNTRY, selectedCountry);
            serverListLauncher.launch(intent);
            connectvpn.setEnabled(true);
        });

        if (!bongoVpn.hasNotificationPermission()) {
            bongoVpn.requestNotificationPermission();
        }

        bongoVpn.setVpnListener(new BongoVpn.VpnListener() {
            @Override
            public void onVpnConnected() {
                isconnect = true;
                tvConnectLabel.setText("Disconnect");
                connectvpn.setEnabled(true);
                startProfileAnimation();
            }

            @Override
            public void onVpnStopped() {
                isconnect = false;
                tvConnectLabel.setText("Connect");
                connectvpn.setEnabled(true);
                stopProfileAnimation();
            }

            @Override
            public void onStatusUpdate(String status) {
                tvText.setText(status);
            }

            @Override
            public void onError(String errorMessage) {
                isconnect = false;
                tvText.setText(errorMessage);
                stopProfileAnimation();
                connectvpn.setEnabled(true);
            }

            @Override
            public void onSpeedUpdate(long downloadBytes, long uploadBytes, long downloadSpeed, long uploadSpeed) {
                String speed = bongoVpn.formatSpeed(downloadSpeed);
                String usage = bongoVpn.formatBytes(downloadBytes);
                String uploadB = bongoVpn.formatBytes(uploadBytes);
                String uploadSp = bongoVpn.formatBytes(uploadSpeed);

                Tvspeed.setText("Download: " + speed +
                        "\nDownloaded: " + usage +
                        "\nUploaded: " + uploadB +
                        "\nUpload speed: " + uploadSp);
            }
        });

        bongoVpn.showNotification()
                .showSpeed(true)
                .targetActivity(MainActivity.class)
                .title("Hridoy vpn");
    }

    private void connectvpn() {
        if (bongoVpn.hasVpnPermission()) {
            bongoVpn.startVpn();
        } else {
            bongoVpn.requestVpnPermission();
        }
    }

    private void startProfileAnimation() {
        if (ivProfile != null && jumpBounce != null) {
            ivProfile.startAnimation(jumpBounce);
        }
    }

    private void stopProfileAnimation() {
        if (ivProfile != null) {
            ivProfile.clearAnimation();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bongoVpn != null) {
            bongoVpn.release();
        }
    }

}