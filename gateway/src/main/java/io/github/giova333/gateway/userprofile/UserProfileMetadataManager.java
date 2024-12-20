package io.github.giova333.gateway.userprofile;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaEvent;
import com.netflix.discovery.EurekaEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserProfileMetadataManager implements EurekaEventListener {

    public static final String USER_PROFILE_SERVICE = "user-profile-service";

    private final EurekaClient discoveryClient;
    private final UserProfileHostPartitionMetadataParser userProfileHostPartitionMetadataParser;

    @Getter
    private volatile Map<Integer, String> partitionToHostMap = new HashMap<>();

    public UserProfileMetadataManager(EurekaClient discoveryClient,
                                      UserProfileHostPartitionMetadataParser userProfileHostPartitionMetadataParser) {

        discoveryClient.registerEventListener(this);
        this.discoveryClient = discoveryClient;
        this.userProfileHostPartitionMetadataParser = userProfileHostPartitionMetadataParser;
    }


    @Override
    public void onEvent(EurekaEvent event) {
        rebuildCache();
    }

    private void rebuildCache() {
        var instances = discoveryClient.getInstancesByVipAddress(USER_PROFILE_SERVICE, false);

        this.partitionToHostMap = userProfileHostPartitionMetadataParser.parseHostPartitionMetadata(instances);

        log.info("Rebuilt user profile service metadata cache: [{}]", partitionToHostMap);
    }
}
