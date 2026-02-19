package com.can.structural.proxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdPartyYouTubeClass implements ThirdPartyYouTubeLib {

    private int listRequestCount;
    private final Map<String, Integer> infoRequestCount = new HashMap<>();
    private final Map<String, Integer> downloadRequestCount = new HashMap<>();

    @Override
    public List<String> listVideos() {
        listRequestCount++;
        return Arrays.asList("design-patterns-intro", "proxy-pattern", "solid-principles");
    }

    @Override
    public String getVideoInfo(String id) {
        infoRequestCount.merge(id, 1, Integer::sum);
        return "Video[" + id + "] - Proxy pattern anlatımı";
    }

    @Override
    public String downloadVideo(String id) {
        downloadRequestCount.merge(id, 1, Integer::sum);
        return "Downloaded: " + id + ".mp4";
    }

    public int getListRequestCount() {
        return listRequestCount;
    }

    public int getInfoRequestCount(String id) {
        return infoRequestCount.getOrDefault(id, 0);
    }

    public int getDownloadRequestCount(String id) {
        return downloadRequestCount.getOrDefault(id, 0);
    }
}
