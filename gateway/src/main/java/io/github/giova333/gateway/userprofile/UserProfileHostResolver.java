package io.github.giova333.gateway.userprofile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class UserProfileHostResolver {

    UserProfileMetadataManager userProfileMetadataManager;
    Partitioner partitioner;

    public String resolveHost(String userId) {
        Map<Integer, String> hostPartitionMetadata = userProfileMetadataManager.getPartitionToHostMap();
        var partition = partitioner.partition(userId);
        return hostPartitionMetadata.get(partition);
    }
}
