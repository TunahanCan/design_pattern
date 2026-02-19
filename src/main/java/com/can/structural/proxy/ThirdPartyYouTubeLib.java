package com.can.structural.proxy;

import java.util.List;

public interface ThirdPartyYouTubeLib {

    List<String> listVideos();

    String getVideoInfo(String id);

    String downloadVideo(String id);
}
