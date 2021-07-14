package ua.com.d_garage.deutschegarage.data.remote.part;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;

import java.io.IOException;
import java.util.List;

public class PartRemoteDataSource {

    private static final String TAG = PartRemoteDataSource.class.getSimpleName();

    private final OkHttpClient client;
    private final PartHtmlParser htmlParser;
    private final MutableLiveData<Response> partResponseLiveData;
    private final LiveData<Part> partLiveData;
    private final MutableLiveData<Response> partDescriptionFieldResponseLiveData;
    private final LiveData<List<PartDescriptionField>> partDescriptionFieldLiveData;

    public PartRemoteDataSource(OkHttpClient client, PartHtmlParser htmlParser) {
        this.client = client;
        this.htmlParser = htmlParser;
        partResponseLiveData = new MutableLiveData<>();
        partLiveData = Transformations.switchMap(partResponseLiveData, r -> this.htmlParser.parsePart(getBody(r)));
        partDescriptionFieldResponseLiveData = new MutableLiveData<>();
        partDescriptionFieldLiveData = Transformations.switchMap(partDescriptionFieldResponseLiveData, r -> this.htmlParser.parseDescriptionField(getBody(r)));
    }

    public LiveData<Part> getPart(long partNumber) {
        loadPartPage(partResponseLiveData, partNumber);
        return partLiveData;
    }

    public LiveData<List<PartDescriptionField>> getPartDescriptionField(long partNumber) {
        loadPartPage(partDescriptionFieldResponseLiveData, partNumber);
        return partDescriptionFieldLiveData;
    }

    private void loadPartPage(MutableLiveData<Response> responseLiveData, long partNumber) {

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
                responseLiveData.postValue(null);
                Log.e(TAG, "Failed to load part page.", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseLiveData.postValue(response);
                    return;
                }
                responseLiveData.postValue(null);
                Log.e(TAG, "Response not successful. Code: " + response.code());
            }
        });

    }

    private String getBody(Response response) {
        if (response != null) {
            ResponseBody body = response.body();
            if (body != null) {
                try {
                    return body.string();
                } catch (Exception e) {
                    Log.e(TAG, "getPart: failed to while reading body. ", e);
                }
            }
        }
        return null;
    }

}
