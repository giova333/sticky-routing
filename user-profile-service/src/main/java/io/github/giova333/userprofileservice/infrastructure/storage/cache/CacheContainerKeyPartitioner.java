/*
 * Copyrights 2023 Playtika Ltd., all rights reserved to Playtika Ltd.
 * privacy+e17bb14d-edc1-4d26-930d-486fcc1ab8fe@playtika.com
 */

package io.github.giova333.userprofileservice.infrastructure.storage.cache;

public interface CacheContainerKeyPartitioner {

    int partition(String key);
}
