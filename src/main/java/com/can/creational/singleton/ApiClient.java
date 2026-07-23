package com.can.creational.singleton;

import java.net.URI;
import java.util.Objects;

/**
 * Singleton'ı kendi içinde aramak yerine konfigürasyon sözleşmesini constructor'dan alır.
 */
public final class ApiClient {

    private static final String INVALID_BASE_URL_MESSAGE =
            "apiBaseUrl must be an absolute HTTP(S) URL"
                    + " without credentials, query or fragment and with a valid port";

    private final URI apiBaseUri;
    private final int connectionTimeoutMs;

    public ApiClient(ApiClientConfig config) {
        ApiClientConfig validatedConfig = Objects.requireNonNull(config, "config cannot be null");
        this.apiBaseUri = normalizeBaseUrl(validatedConfig.getApiBaseUrl());
        this.connectionTimeoutMs = validatedConfig.getConnectionTimeoutMs();
        if (connectionTimeoutMs <= 0) {
            throw new IllegalArgumentException("connectionTimeoutMs must be positive");
        }
    }

    public ApiRequestPlan planGet(String path) {
        URI relativePath = normalizeRelativePath(path);
        URI resolved = apiBaseUri.resolve(relativePath).normalize();

        if (!hasSameOrigin(apiBaseUri, resolved)
                || !resolved.getPath().startsWith(apiBaseUri.getPath())) {
            throw new IllegalArgumentException("path must stay within apiBaseUrl");
        }

        return new ApiRequestPlan(
                "GET",
                resolved.toString(),
                connectionTimeoutMs
        );
    }

    private static URI normalizeBaseUrl(String baseUrl) {
        String normalized = Objects.requireNonNull(baseUrl, "apiBaseUrl cannot be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("apiBaseUrl cannot be blank");
        }

        URI uri;
        try {
            uri = URI.create(normalized);
        } catch (IllegalArgumentException malformedUri) {
            throw new IllegalArgumentException(INVALID_BASE_URL_MESSAGE, malformedUri);
        }

        boolean supportedScheme = "http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme());
        int port = uri.getPort();
        if (!uri.isAbsolute()
                || uri.getHost() == null
                || !supportedScheme
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null
                || port == 0
                || port > 65_535) {
            throw new IllegalArgumentException(INVALID_BASE_URL_MESSAGE);
        }

        URI normalizedUri = uri.normalize();
        String normalizedText = normalizedUri.toString();
        return normalizedText.endsWith("/")
                ? normalizedUri
                : URI.create(normalizedText + "/");
    }

    private static URI normalizeRelativePath(String path) {
        String normalized = Objects.requireNonNull(path, "path cannot be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("path cannot be blank");
        }

        URI uri;
        try {
            uri = URI.create(normalized);
        } catch (IllegalArgumentException malformedUri) {
            throw new IllegalArgumentException("path must be a valid relative URI", malformedUri);
        }

        if (uri.isAbsolute() || uri.getRawAuthority() != null || normalized.startsWith("//")) {
            throw new IllegalArgumentException("path must be relative to apiBaseUrl");
        }
        if (uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("path must not contain query or fragment");
        }

        String decodedPath = uri.getPath();
        for (String segment : decodedPath.split("/", -1)) {
            if (".".equals(segment) || "..".equals(segment)) {
                throw new IllegalArgumentException("path must stay within apiBaseUrl");
            }
        }

        String relativeText = normalized.startsWith("/")
                ? normalized.substring(1)
                : normalized;
        return URI.create(relativeText);
    }

    private static boolean hasSameOrigin(URI base, URI resolved) {
        return base.getScheme().equalsIgnoreCase(resolved.getScheme())
                && base.getHost().equalsIgnoreCase(resolved.getHost())
                && base.getPort() == resolved.getPort();
    }
}
