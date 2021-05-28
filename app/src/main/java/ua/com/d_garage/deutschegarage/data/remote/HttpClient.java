package ua.com.d_garage.deutschegarage.data.remote;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class HttpClient {

    private static final long CONNECTION_TIMEOUT = 5;

    private static OkHttpClient client;

    private HttpClient() {}

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return client;
    }

}
