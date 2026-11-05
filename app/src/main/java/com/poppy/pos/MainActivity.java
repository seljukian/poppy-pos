package com.poppy.pos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private TextView txtTitle;

    // Paneldeki modüllerin adresleri
    private static final String BASE_URL = "https://panel.poppyapartments.com";
    private static final String URL_LIVE_ORDERS = BASE_URL + "/orders";     // Canlı siparişler & onay
    private static final String URL_QUICK_ORDER = BASE_URL + "/pos/quick";  // Sık kullanılanlar / Hızlı sipariş
    private static final String URL_TABLES      = BASE_URL + "/tables";     // Masa açma
    private static final String URL_PAYMENT     = BASE_URL + "/payments";   // Tahsilat ekranı

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        txtTitle = findViewById(R.id.txtTitle);

        Button btnQuickOrder = findViewById(R.id.btnQuickOrder);
        Button btnLiveOrders = findViewById(R.id.btnLiveOrders);
        Button btnTables = findViewById(R.id.btnTables);
        Button btnPayment = findViewById(R.id.btnPayment);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        // WebView Mobil Uyum ve Sığdırma Ayarları
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setTextZoom(95);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectMobileFix();
            }
        });

        // İlk açılışta Canlı Siparişler gelsin
        loadModule("POPPY POS - Canlı Siparişler", BASE_URL);

        // 1. Kritik Buton: Hızlı Sipariş Al (Sık Kullanılanlar)
        btnQuickOrder.setOnClickListener(v -> 
            loadModule("Hızlı Sipariş (Sık Kullanılanlar)", URL_QUICK_ORDER)
        );

        // 2. Canlı Siparişler & Onaylama
        btnLiveOrders.setOnClickListener(v -> 
            loadModule("Canlı Siparişler", URL_LIVE_ORDERS)
        );

        // 3. Masa Açma
        btnTables.setOnClickListener(v -> 
            loadModule("Masa Aç / Masalar", URL_TABLES)
        );

        // 4. Tahsilat Alma
        btnPayment.setOnClickListener(v -> 
            loadModule("Tahsilat Ekranı", URL_PAYMENT)
        );

        // Yenile Butonu
        btnRefresh.setOnClickListener(v -> webView.reload());
    }

    private void loadModule(String title, String url) {
        txtTitle.setText(title);
        webView.loadUrl(url);
    }

    // Sitedeki gereksiz menüleri gizleyen ve taşan kısımları ekrana sığdıran özel CSS/JS
    private void injectMobileFix() {
        String js = "javascript:(function() {" +
                "var meta = document.querySelector('meta[name=viewport]');" +
                "if (!meta) {" +
                "  meta = document.createElement('meta');" +
                "  meta.name = 'viewport';" +
                "  document.head.appendChild(meta);" +
                "}" +
                "meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';" +
                "var style = document.createElement('style');" +
                "style.innerHTML = '" +
                "  aside, .sidebar, #sidebar, .main-sidebar, .navbar, header, footer { display: none !important; } " +
                "  .content-wrapper, .main-content, main, body, html { margin-left: 0 !important; padding-left: 4px !important; padding-right: 4px !important; width: 100% !important; max-width: 100vw !important; overflow-x: hidden !important; box-sizing: border-box !important; } " +
                "  table { width: 100% !important; display: block !important; overflow-x: auto !important; } " +
                "';" +
                "document.head.appendChild(style);" +
                "})()";
        webView.evaluateJavascript(js, null);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
