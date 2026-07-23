package com.can.structural.proxy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ThirdPartyYouTubeClass implements ThirdPartyYouTubeLib {

    private final AtomicInteger listRequestCount = new AtomicInteger();
    private final Map<String, AtomicInteger> infoRequestCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> downloadRequestCount = new ConcurrentHashMap<>();

    @Override
    public List<String> listVideos() {
        listRequestCount.incrementAndGet();
        return List.of("design-patterns-intro", "proxy-pattern", "solid-principles");
    }

    @Override
    public String getVideoInfo(String id) {
        String exactId = requireId(id);
        infoRequestCount.computeIfAbsent(
            exactId,
            ignored -> new AtomicInteger()
        ).incrementAndGet();
        return "Video[" + exactId + "] - Proxy pattern anlatımı";
    }

    @Override
    public String downloadVideo(String id) {
        String exactId = requireId(id);
        downloadRequestCount.computeIfAbsent(
            exactId,
            ignored -> new AtomicInteger()
        ).incrementAndGet();
        return "Downloaded: " + exactId + ".mp4";
    }

    public int getListRequestCount() {
        return listRequestCount.get();
    }

    public int getInfoRequestCount(String id) {
        AtomicInteger count = infoRequestCount.get(id);
        return count == null ? 0 : count.get();
    }

    public int getDownloadRequestCount(String id) {
        AtomicInteger count = downloadRequestCount.get(id);
        return count == null ? 0 : count.get();
    }

    private static String requireId(String id) {
        return Objects.requireNonNull(id, "video id cannot be null");
    }
}
