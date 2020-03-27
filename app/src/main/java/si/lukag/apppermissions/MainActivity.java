package si.lukag.apppermissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_ID = 100;
    private static final String sp_key = "permission_denial";
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ask = findViewById(R.id.ask);
        text = findViewById(R.id.func);
        text.setText(R.string.permission_unknown);
        ask.setOnClickListener(v -> askForPermissions());
    }

    private void askForPermissions() {
        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
            text.setText(R.string.permission_granted);
        } else {
            text.setText(R.string.permissions_ask);
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // User has already denied the request
                showReasonForPerms("Reason because you denied", "Just to show how to handle permission requests");
            } else {
                // If shared preferences contain this key, user had previously denied request
                if (this.getPreferences(MODE_PRIVATE).contains(sp_key)) {
                    text.setText(R.string.permission_permanent_denial);
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            "Denied permission. Change that in app info.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("App info", view -> {
                                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(i);
                                    }
                            ).show();
                } else {
                    // Asking for the first time
                    showReasonForPerms("Hello for the first time!", "Just to show how to handle permission requests");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    text.setText(R.string.permission_granted);
                    Toast.makeText(this, "Permission has been granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied
                    // Disable the functionality that depends on this permission
                    text.setText(R.string.permissions_denied);
                    getPreferences(MODE_PRIVATE).edit().putBoolean(sp_key, true).apply();
                    Toast.makeText(this, "Request denied. No fun for you", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void showReasonForPerms(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    // Ask for permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_ID);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
