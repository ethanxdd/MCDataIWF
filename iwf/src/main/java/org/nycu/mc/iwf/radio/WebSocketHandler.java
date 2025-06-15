package org.nycu.mc.iwf.radio;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

import okhttp3.*;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class WebSocketHandler extends WebSocketListener {
    private WebSocket webSocket;
    private WebSocketRouter router;
    private OkHttpClient client;

    public WebSocketHandler(WebSocketRouter router) {
        this.router = router;
    }

    public void connect(String wsUrl) {
        client = getUnsafeOkHttpClient(); // 使用信任所有憑證的 Client

        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        client.newWebSocket(request, this);
    }

    public void send(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
        this.webSocket = ws;
        System.out.println("WebSocket 已連線");
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
        router.routeMessage(text);
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response response) {
        System.err.println("WebSocket 錯誤: " + t.getMessage());
    }

    // ==== 允許自簽名憑證的 Client ====
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                    @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
