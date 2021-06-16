package ua.com.d_garage.deutschegarage.data.remote.part;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PartRemoteDataSource {

    private static final String TAG = PartRemoteDataSource.class.getSimpleName();

    private final OkHttpClient client;
   private final MutableLiveData<Response> liveData;

    public PartRemoteDataSource(OkHttpClient client) {
        this.client = client;
        liveData = new MutableLiveData<>();
    }

    public LiveData<Response> loadPartPage(long partNumber) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PartApiConstant.SCHEME)
                .host(PartApiConstant.HOST)
                .addPathSegments(PartApiConstant.ENDPOINT_BMW_PART_SEARCH)
                .addQueryParameter(PartApiConstant.QUERY_PARAMETER_NAME_BMW_PART, String.valueOf(partNumber))
                .build();

        Request request = new Request.Builder().url(httpUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                liveData.postValue(null);
                Log.e(TAG, "Failed to load part page.", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    liveData.postValue(response);
                    return;
                }
                liveData.postValue(null);
                Log.e(TAG, "Response not successful. Code: " +response.code());
            }
        });

        return liveData;
    }

}
