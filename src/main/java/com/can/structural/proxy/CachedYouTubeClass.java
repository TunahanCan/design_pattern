package com.can.structural.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedYouTubeClass implements ThirdPartyYouTubeLib {

    private final ThirdPartyYouTubeLib service;
    private List<String> listCache;
    private final Map<String, String> videoInfoCache = new HashMap<>();
    private final Map<String, String> downloadedVideoCache = new HashMap<>();

    public CachedYouTubeClass(ThirdPartyYouTubeLib service) {
        this.service = service;
    }

    @Override
    public List<String> listVideos() {
        if (listCache == null) {
            listCache = service.listVideos();
        }
        return listCache;
    }

    @Override
    public String getVideoInfo(String id) {
        return videoInfoCache.computeIfAbsent(id, service::getVideoInfo);
    }

    @Override
    public String downloadVideo(String id) {
        return downloadedVideoCache.computeIfAbsent(id, service::downloadVideo);
    }

    public void reset() {
        listCache = null;
        videoInfoCache.clear();
        downloadedVideoCache.clear();
    }
}
