package com.can.structural.proxy;

import java.util.List;
import java.util.Objects;

public class YouTubeManager {

    private final ThirdPartyYouTubeLib service;

    public YouTubeManager(ThirdPartyYouTubeLib service) {
        this.service = Objects.requireNonNull(service, "service cannot be null");
    }

    public String renderVideoPage(String id) {
        String info = service.getVideoInfo(id);
        return "VideoPage => " + info;
    }

    public String renderListPanel() {
        List<String> videos = service.listVideos();
        return "ListPanel => " + String.join(", ", videos);
    }

    public String download(String id) {
        return service.downloadVideo(id);
    }
}
