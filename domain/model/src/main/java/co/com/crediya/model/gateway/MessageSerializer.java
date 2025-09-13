package co.com.crediya.model.gateway;

public interface MessageSerializer {
    String toJson(Object value);
    <T> T fromJson(String json, Class<T> type);
}
