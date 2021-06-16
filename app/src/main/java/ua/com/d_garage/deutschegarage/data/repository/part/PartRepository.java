package ua.com.d_garage.deutschegarage.data.repository.part;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ua.com.d_garage.deutschegarage.data.local.db.dao.PartDao;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import ua.com.d_garage.deutschegarage.data.model.part.PartRequestData;
import ua.com.d_garage.deutschegarage.data.remote.part.PartHtmlParser;
import ua.com.d_garage.deutschegarage.data.remote.part.PartRemoteDataSource;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class PartRepository {

    private static final String TAG = PartRepository.class.getSimpleName();

    private final PartRemoteDataSource remoteDataSource;
    private final ExecutorService executorService;
    private final PartHtmlParser htmlParser;
    private final PartDao partDao;
    private final LiveData<List<PartDescriptionField>> partDescriptionLiveData;
    private final MutableLiveData<Long> partVinLiveData;
    private final LiveData<Part> partLiveData;
    private final MutableLiveData<PartRequestData> partRequestDataLiveData;

    public PartRepository(PartRemoteDataSource remoteDataSource, PartHtmlParser htmlParser, ExecutorService executorService, PartDao partDao) {
        this.remoteDataSource = remoteDataSource;
        this.htmlParser = htmlParser;
        this.executorService = executorService;
        this.partDao = partDao;
        partVinLiveData = new MutableLiveData<>();
        LiveData<Response> descriptionResponse = Transformations.switchMap(partVinLiveData, remoteDataSource::loadPartPage);
        partDescriptionLiveData = Transformations.switchMap(descriptionResponse, resp -> htmlParser.parseDescriptionField(getBody(resp)));
        partRequestDataLiveData = new MutableLiveData<>();
        LiveData<Response> responseLiveData = Transformations.switchMap(partRequestDataLiveData, data -> remoteDataSource.loadPartPage(data.getVin()));
        partLiveData = Transformations.switchMap(responseLiveData, response -> htmlParser.parsePart(getBody(response), partRequestDataLiveData.getValue().getNoteId()));
    }

    public void save(Part part) {
        executorService.execute(() -> partDao.insert(part));
    }

    public LiveData<List<PartDescriptionField>> getPartDescriptionLiveData() {
        return partDescriptionLiveData;
    }

    public LiveData<Part> getPartLiveData() {
        return partLiveData;
    }

    public void getPartDescriptionFields(long partNumber) {
        partVinLiveData.postValue(partNumber);
    }

    public void getPart(PartRequestData data) {
        partRequestDataLiveData.postValue(data);
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
