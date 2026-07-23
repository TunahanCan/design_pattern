package com.can.structural.proxy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CachedYouTubeClass implements ThirdPartyYouTubeLib {

    private final ThirdPartyYouTubeLib service;
    private volatile List<String> listCache;
    private final Map<String, String> videoInfoCache = new ConcurrentHashMap<>();
    private final Map<String, String> downloadedVideoCache = new ConcurrentHashMap<>();

    public CachedYouTubeClass(ThirdPartyYouTubeLib service) {
        this.service = Objects.requireNonNull(service, "service cannot be null");
    }

    @Override
    public List<String> listVideos() {
        List<String> cachedSnapshot = listCache;
        if (cachedSnapshot != null) {
            return cachedSnapshot;
        }

        synchronized (this) {
            if (listCache == null) {
                listCache = List.copyOf(service.listVideos());
            }
            return listCache;
        }
    }

    @Override
    public String getVideoInfo(String id) {
        String exactId = requireId(id);
        return videoInfoCache.computeIfAbsent(exactId, service::getVideoInfo);
    }

    @Override
    public String downloadVideo(String id) {
        String exactId = requireId(id);
        return downloadedVideoCache.computeIfAbsent(exactId, service::downloadVideo);
    }

    public void invalidateVideo(String id) {
        String exactId = requireId(id);
        videoInfoCache.remove(exactId);
        downloadedVideoCache.remove(exactId);
    }

    public void reset() {
        synchronized (this) {
            listCache = null;
        }
        videoInfoCache.clear();
        downloadedVideoCache.clear();
    }

    private static String requireId(String id) {
        return Objects.requireNonNull(id, "video id cannot be null");
    }
}
