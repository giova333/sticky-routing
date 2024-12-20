package io.github.giova333.userprofileservice.infrastructure.storage.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.cdt.MapOperation;
import com.aerospike.client.cdt.MapOrder;
import com.aerospike.client.cdt.MapPolicy;
import com.aerospike.client.cdt.MapWriteMode;
import com.aerospike.client.policy.WritePolicy;
import io.github.giova333.userprofileservice.domain.UserProfile;
import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.storage.UserProfileRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Repository
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class AerospikeUserProfileRepository implements UserProfileRepository {

    private static final String SET_NAME = "user-properties";
    private static final String BIN_NAME = "properties";
    private static final int EXPIRATION = 365 * 24 * 60 * 60; // One year

    AerospikeClient client;
    String namespace;

    WritePolicy writePolicy;
    MapPolicy mapPolicy;

    public AerospikeUserProfileRepository(AerospikeClient client,
                                          @Value("${spring.data.aerospike.namespace}")
                                          String namespace) {
        this.client = client;
        this.namespace = namespace;

        this.writePolicy = new WritePolicy();
        this.writePolicy.expiration = EXPIRATION;
        this.mapPolicy = new MapPolicy(MapOrder.UNORDERED, MapWriteMode.UPDATE);
    }

    @Override
    public UserProfile getUserProfile(String userId) {
        var key = new Key(namespace, SET_NAME, userId);

        try {
            var record = client.operate(writePolicy, key,
                    Operation.touch(),
                    Operation.get(BIN_NAME)
            );

            return toUserProperties(userId, record);
        } catch (AerospikeException e) {
            return UserProfile.empty(userId);
        }
    }

    @Override
    public void collect(UserProperty property) {
        var key = new Key(namespace, SET_NAME, property.userId());

        client.operate(writePolicy, key,
                MapOperation.put(mapPolicy, BIN_NAME, com.aerospike.client.Value.get(property.name()), com.aerospike.client.Value.get(property.value()))
        );
    }

    private UserProfile toUserProperties(String userId, Record record) {
        if (record != null) {
            @SuppressWarnings("unchecked")
            var properties = (Map<String, Object>) record.getValue(BIN_NAME);
            return new UserProfile(userId, properties);
        }
        return UserProfile.empty(userId);
    }
}
