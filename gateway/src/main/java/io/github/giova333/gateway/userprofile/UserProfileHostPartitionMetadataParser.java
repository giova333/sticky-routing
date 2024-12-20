package io.github.giova333.gateway.userprofile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserProfileHostPartitionMetadataParser {

    ObjectMapper objectMapper;

    @SneakyThrows
    Map<Integer, String> parseHostPartitionMetadata(List<InstanceInfo> instances) {
        Map<Integer, String> hostPartitionMetadata = new HashMap<>();

        for (InstanceInfo instance : instances) {
            var instanceMetadata = instance.getMetadata();

            for (Map.Entry<String, String> entry : instanceMetadata.entrySet()) {
                if (entry.getKey().startsWith("partitions")) {
                    var partitions = objectMapper.readValue(entry.getValue(), new TypeReference<List<Integer>>() {
                    });

                    partitions.forEach(partition -> hostPartitionMetadata.put(partition, instance.getHostName() + ":" + instance.getPort()));
                }
            }
        }
        return hostPartitionMetadata;
    }
}