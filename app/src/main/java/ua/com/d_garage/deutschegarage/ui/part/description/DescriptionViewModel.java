package ua.com.d_garage.deutschegarage.ui.part.description;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import ua.com.d_garage.deutschegarage.data.remote.HttpClient;
import ua.com.d_garage.deutschegarage.data.remote.part.PartHtmlParser;
import ua.com.d_garage.deutschegarage.data.remote.part.PartRemoteDataSource;
import ua.com.d_garage.deutschegarage.data.repository.part.PartRepository;
import ua.com.d_garage.deutschegarage.ui.base.BaseViewModel;

import java.util.List;

public class DescriptionViewModel extends BaseViewModel<DescriptionNavigator> {

    private final PartRepository partRepository;

    private MediatorLiveData<List<PartDescriptionField>> descriptionFields;

    public DescriptionViewModel(Application application) {
        super(application);
        //TODO factory..
        this.partRepository = new PartRepository(new PartRemoteDataSource(HttpClient.getClient(), new PartHtmlParser()));
    }

    public LiveData<List<PartDescriptionField>> getDescriptionFields(long id) {
        if (descriptionFields == null) {
            setIsLoading(true);
            descriptionFields = new MediatorLiveData<>();
            descriptionFields.addSource(partRepository.getBMWPartDescription(id), list -> {
                descriptionFields.postValue(list);
                setIsLoading(false);
            });
        }
        return descriptionFields;
    }

    public void close() {
        getNavigator().navigateBackward();
    }

}
