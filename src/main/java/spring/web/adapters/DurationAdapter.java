package spring.web.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        Long value;
        if (duration != null) {
            value = (long) (duration.getSeconds() + duration.getNano() * 1E-9);
            jsonWriter.value(value);
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return Duration.parse(value);
    }
}