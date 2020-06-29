package com.traderepublic.quotesaggregator.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
//    public ObjectMapper getObjectMapper() {
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addDeserializer(Instant.class, new MillisecondsToInstantDeserializer());
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.registerModule(new JavaTimeModule());
//        mapper.registerModule(simpleModule);
//        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
//        return mapper;
//    }
//
//    class MillisecondsToInstantDeserializer extends JsonDeserializer<Instant> {
//        @Override
//        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//            return getValue(jsonParser);
//        }
//
//        private Instant getValue(JsonParser parser) throws JsonParseException {
//            try {
//                String milliseconds = parser.getValueAsString();
//                if (milliseconds == "") {
//                    return null;
//                } else {
//                    return Instant.ofEpochMilli(Long.valueOf(milliseconds));
//                }
//            } catch (NumberFormatException | IOException ex) {
//                throw new JsonParseException(parser, "not a valid timestamp representation.");
//            }
//        }
//    }

    @Bean
    public Gson gson() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapterFactory(DateTypeAdapter.FACTORY);
        return gson.create();
    }
}
