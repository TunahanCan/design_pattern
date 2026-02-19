package com.can.structural.proxy;

public class ProxyPatternDemo {

    private ProxyPatternDemo() {
    }

    public static void run() {
        System.out.println("=== Proxy Pattern ===");

        ThirdPartyYouTubeClass realService = new ThirdPartyYouTubeClass();
        ThirdPartyYouTubeLib proxy = new CachedYouTubeClass(realService);
        YouTubeManager manager = new YouTubeManager(proxy);

        System.out.println(manager.renderListPanel());
        System.out.println(manager.renderListPanel());

        System.out.println(manager.renderVideoPage("proxy-pattern"));
        System.out.println(manager.renderVideoPage("proxy-pattern"));

        System.out.println(manager.download("proxy-pattern"));
        System.out.println(manager.download("proxy-pattern"));
        System.out.println();
    }
}
