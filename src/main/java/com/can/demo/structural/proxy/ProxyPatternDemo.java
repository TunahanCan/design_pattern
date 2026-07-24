package com.can.demo.structural.proxy;

import com.can.structural.proxy.CachedYouTubeClass;
import com.can.structural.proxy.ThirdPartyYouTubeClass;
import com.can.structural.proxy.ThirdPartyYouTubeLib;
import com.can.structural.proxy.YouTubeManager;

/**
 * Executable composition root for the Proxy example.
 *
 * <p>The subject, proxy, real subject, and client remain in
 * {@code com.can.structural.proxy}; this class only wires the object graph.</p>
 */
public final class ProxyPatternDemo {

    private ProxyPatternDemo() {
    }

    public static void main(String[] args) {
        run();
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
