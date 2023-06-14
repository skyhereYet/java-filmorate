package spring.web.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        String value;
        if (localDate != null) {
            value = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            jsonWriter.value(value);
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
