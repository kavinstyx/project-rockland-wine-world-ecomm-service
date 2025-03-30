package rockland.elysiancrest.com.data_service.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class InMemoryCache<K, V> {
    private final long ttl;
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    public InMemoryCache(long ttl) {
        this.ttl = ttl; // time-to-live in milliseconds
    }

    // Retrieve from cache if valid; otherwise load fresh data using the provided Supplier
    public V get(K key, Supplier<V> dataLoader) {
        long now = System.currentTimeMillis();
        CacheEntry<V> entry = cache.get(key);

        if (entry != null && (now - entry.timestamp < ttl)) {
            // Cache hit and not expired
            return entry.value;
        }

        // Load fresh data
        V newValue = dataLoader.get();
        if (newValue != null) {
            CacheEntry<V> newEntry = new CacheEntry<>(newValue, now);
            cache.put(key, newEntry);
        }
        return newValue;
    }

    public void put(K key, V value) {
        long now = System.currentTimeMillis();
        cache.put(key, new CacheEntry<>(value, now));
    }

    public void invalidate(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private static class CacheEntry<V> {
        V value;
        long timestamp;

        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
