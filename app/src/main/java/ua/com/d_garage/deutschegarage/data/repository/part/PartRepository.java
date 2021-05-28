package ua.com.d_garage.deutschegarage.data.repository.part;

import androidx.lifecycle.LiveData;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import ua.com.d_garage.deutschegarage.data.remote.part.PartRemoteDataSource;

import java.util.List;

public class PartRepository {

    private final PartRemoteDataSource remoteDataSource;

    public PartRepository(PartRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public LiveData<List<PartDescriptionField>> getBMWPartDescription(long partNumber) {
        return remoteDataSource.findBMWPart(partNumber);
    }
}
