package ua.com.d_garage.deutschegarage.data.remote.part;

import ua.com.d_garage.deutschegarage.data.model.part.PartDescriptionField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class PartHtmlParser {

    private static final String NAME_TAG = "dt";
    private static final String VALUE_TAG = "dd";

    public List<PartDescriptionField> parse(String body) {
        if (body == null) {
            return null;
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
        return descriptionFields;
    }

}
