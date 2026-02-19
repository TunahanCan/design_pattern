package com.can.structural.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProxyPatternDemoTest {

    @Test
    void shouldCacheListAndVideoInfoCalls() {
        ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
        CachedYouTubeClass proxy = new CachedYouTubeClass(realService);
        YouTubeManager manager = new YouTubeManager(proxy);

        manager.renderListPanel();
        manager.renderListPanel();

        manager.renderVideoPage("proxy-pattern");
        manager.renderVideoPage("proxy-pattern");

        assertEquals(1, realService.getListRequestCount());
        assertEquals(1, realService.getInfoRequestCount("proxy-pattern"));
    }

    @Test
    void shouldCacheDownloadsAndResetWhenAsked() {
        ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
        CachedYouTubeClass proxy = new CachedYouTubeClass(realService);

        String first = proxy.downloadVideo("proxy-pattern");
        String second = proxy.downloadVideo("proxy-pattern");

        assertEquals(first, second);
        assertEquals(1, realService.getDownloadRequestCount("proxy-pattern"));

        proxy.reset();
        String afterReset = proxy.downloadVideo("proxy-pattern");

        assertTrue(afterReset.contains("proxy-pattern"));
        assertEquals(2, realService.getDownloadRequestCount("proxy-pattern"));
    }
}
