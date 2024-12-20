package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.giova333.userprofileservice.domain.UserProperty;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CaffeinePartitionedContainer {

    List<Cache<String, CachedUserProfile>> caches;
    CacheContainerKeyPartitioner partitioner;

    public CaffeinePartitionedContainer(CacheContainerKeyPartitioner partitioner,
                                        CacheConfigurationProperties config) {
        this.caches = IntStream.range(0, config.getNumberOfPartitions())
                .mapToObj(i -> caffeine(config))
                .toList();
        this.partitioner = partitioner;
    }

    public Optional<CachedUserProfile> get(String userId) {
        return Optional.ofNullable(cache(userId).getIfPresent(userId));
    }

    public void put(CachedUserProfile profile) {
        var userId = profile.getUserId();

        cache(userId).put(userId, profile);
    }

    public boolean actualize(UserProperty property) {
        var userId = property.userId();

        var cachedUserProfile = cache(userId).getIfPresent(userId);

        if (cachedUserProfile == null) {
            return false;
        }

        cachedUserProfile.addProperty(property);
        return true;
    }

    public void invalidate(int partition) {
        var cachePartition = partition % caches.size();

        log.info("Invalidating cache partition {}", cachePartition);

        caches.get(cachePartition).invalidateAll();
    }

    public long size() {
        var cacheSize = caches.stream()
                .mapToLong(Cache::estimatedSize)
                .sum();
        log.info("Cache size: {}", cacheSize);
        return cacheSize;

    }

    public void invalidateAll(List<Integer> partitions) {
        partitions.forEach(this::invalidate);
    }

    private Cache<String, CachedUserProfile> caffeine(CacheConfigurationProperties config) {
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();

        builder.maximumSize(config.getMaxNumberOfItems());

        if (config.getExpireAfterWrite() != null) {
            builder.expireAfterWrite(config.getExpireAfterWrite());
        }

        return builder.build();
    }

    private Cache<String, CachedUserProfile> cache(String userId) {
        return caches.get(partitioner.partition(userId) % caches.size());
    }
}
