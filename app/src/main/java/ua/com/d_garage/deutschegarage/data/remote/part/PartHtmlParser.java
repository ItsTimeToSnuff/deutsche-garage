package ua.com.d_garage.deutschegarage.data.remote.part;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import ua.com.d_garage.deutschegarage.data.model.part.Part;
import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PartHtmlParser {

    private static final String TAG = PartHtmlParser.class.getSimpleName();
    private static final String NAME_TAG = "dt";
    private static final String VALUE_TAG = "dd";

    private final ExecutorService executorService;
    private final MutableLiveData<List<PartDescriptionField>> partDescriptionFieldLiveData;
    private final MutableLiveData<Part> partLiveData;

    public PartHtmlParser(ExecutorService executorService) {
        this.executorService = executorService;
        partDescriptionFieldLiveData = new MutableLiveData<>();
        partLiveData = new MutableLiveData<>();
    }

    public LiveData<List<PartDescriptionField>> parseDescriptionField(String body) {
        executorService.execute(() -> {
            if (body == null) {
                return;
            }
            List<PartDescriptionField> descriptionFields = new ArrayList<>();
            Document document = Jsoup.parse(body);
            Elements names = document.select(NAME_TAG);
            Elements values = document.select(VALUE_TAG);
            int elements = Math.min(names.size(), values.size());
            for (int i = 0; i < elements; i++) {
                String name = names.get(i).text();
                String value = values.get(i).text();
                descriptionFields.add(new PartDescriptionField(name, value));
            }
            partDescriptionFieldLiveData.postValue(descriptionFields);
        });
        return partDescriptionFieldLiveData;
    }

    public LiveData<Part> parsePart(String body) {
        executorService.execute(() -> {
            if (body == null) {
                return;
            }
            Document document = Jsoup.parse(body);
            Elements values = document.select(VALUE_TAG);
            try {
                Part part = new Part(null, Long.parseLong(values.get(0).text()), values.get(1).text());
                partLiveData.postValue(part);
            } catch (Exception e) {
                Log.e(TAG, "parsePart: failed to parse part values. ", e);
            }
        });
        return partLiveData;
    }

}
