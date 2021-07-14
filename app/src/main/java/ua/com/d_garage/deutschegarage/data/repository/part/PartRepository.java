package ua.com.d_garage.deutschegarage.data.repository.part;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import ua.com.d_garage.deutschegarage.data.local.db.dao.PartDao;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import ua.com.d_garage.deutschegarage.data.remote.part.PartRemoteDataSource;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class PartRepository {

    private static final String TAG = PartRepository.class.getSimpleName();

    private final ExecutorService executorService;
    private final PartRemoteDataSource remoteDataSource;
    private final PartDao partDao;
    private final MutableLiveData<Part> savePartLiveData;
    private final MutableLiveData<Long> partNumberDescriptionFieldLiveData;
    private final LiveData<List<PartDescriptionField>> partDescriptionLiveData;
    private final MutableLiveData<Long> partNumberPartLiveData;
    private final MediatorLiveData<Part> partLiveData;

    public PartRepository(ExecutorService executorService, PartRemoteDataSource remoteDataSource, PartDao partDao) {
        this.executorService = executorService;
        this.remoteDataSource = remoteDataSource;
        this.partDao = partDao;
        savePartLiveData = new MutableLiveData<>();
        partNumberDescriptionFieldLiveData = new MutableLiveData<>();
        partDescriptionLiveData = Transformations.switchMap(partNumberDescriptionFieldLiveData, this.remoteDataSource::getPartDescriptionField);
        partNumberPartLiveData = new MutableLiveData<>();
        LiveData<Part> remotePart = Transformations.switchMap(partNumberPartLiveData, this.remoteDataSource::getPart);
        partLiveData = new MediatorLiveData<>();
        partLiveData.addSource(remotePart, part -> save(partLiveData, part));
    }

    public LiveData<Part> save(Part part) {
        return save(savePartLiveData, part);
    }

    public LiveData<List<PartDescriptionField>> getPartDescriptionFields(long partNumber) {
        partNumberDescriptionFieldLiveData.postValue(partNumber);
        return partDescriptionLiveData;
    }

    public LiveData<Part> getPart(long partNumber) {
        executorService.execute(() -> {
            Part part = partDao.findPartByPartNumber(partNumber);
            if (part != null) {
                partLiveData.postValue(part);
            } else {
                partNumberPartLiveData.postValue(partNumber);
            }
        });
        return partLiveData;
    }

    @SuppressWarnings("unchecked")
    private <T extends LiveData<Part>> T save(T liveData, Part part) {
        executorService.execute(() -> {
            if (part == null) {
                return;
            }
            Long id = partDao.insert(part);
            Part savePart = new Part(id, part.getPartNumber(), part.getName());
            if (liveData instanceof MutableLiveData) {
                ((MutableLiveData<Part>) liveData).postValue(savePart);
            } else {
                ((MediatorLiveData<Part>) liveData).postValue(savePart);
            }
        });
        return liveData;
    }

}
