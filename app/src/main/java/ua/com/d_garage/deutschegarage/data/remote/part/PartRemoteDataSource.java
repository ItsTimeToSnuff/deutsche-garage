package ua.com.d_garage.deutschegarage.data.remote.part;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class PartRemoteDataSource {

    private static final String TAG = PartRemoteDataSource.class.getSimpleName();

    private final OkHttpClient client;
    private final PartHtmlParser htmlParser;
    private final MutableLiveData<List<PartDescriptionField>> bmwPartDescription;

    public PartRemoteDataSource(OkHttpClient client, PartHtmlParser htmlParser) {
        this.client = client;
        this.htmlParser = htmlParser;
        bmwPartDescription = new MutableLiveData<>();
    }

    public LiveData<List<PartDescriptionField>> findBMWPart(long partNumber) {
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
                bmwPartDescription.postValue(null);
                Log.e(TAG, "Failed to load bmw scanner description.", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        List<PartDescriptionField> descriptionFields = htmlParser.parse(body.string());
                        bmwPartDescription.postValue(descriptionFields);
                        return;
                    }
                }
                bmwPartDescription.postValue(null);
            }
        });
        return bmwPartDescription;
    }
}
