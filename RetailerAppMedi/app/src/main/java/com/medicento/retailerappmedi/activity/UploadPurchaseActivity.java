package com.medicento.retailerappmedi.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;

public class UploadPurchaseActivity extends AppCompatActivity {

    ImageView back;
    RelativeLayout stocks;
    LinearLayout upload_purchase, performa_invoice, download_ll;
    SeekBar seek_bar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_purchase);

        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        stocks = findViewById(R.id.stocks);
        seek_bar = findViewById(R.id.seek_bar);
        upload_purchase = findViewById(R.id.upload_purchase);
        performa_invoice = findViewById(R.id.performa_invoice);
        download_ll = findViewById(R.id.download_ll);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                stocks.setVisibility(View.GONE);
                upload_purchase.setVisibility(View.GONE);
                performa_invoice.setVisibility(View.GONE);
                download_ll.setVisibility(View.GONE);
                if (b) {
                    switch (i) {
                        case 0:
                            stocks.setVisibility(View.VISIBLE);
                            title.setText("Stock Videos & Pics");
                            break;
                        case 1:
                            title.setText("Purchase Order");
                            upload_purchase.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            title.setText("Performa Invoice");
                            performa_invoice.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            title.setText("Delivery LR");
                            download_ll.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
