package com.can.behavirol.iterator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SocialGraph {

    private final Map<String, Profile> profiles;
    private final Map<String, List<String>> friendsByProfileId;
    private final Map<String, List<String>> coworkersByProfileId;

    public SocialGraph(Map<String, Profile> profiles,
                       Map<String, List<String>> friendsByProfileId,
                       Map<String, List<String>> coworkersByProfileId) {
        this.profiles = Map.copyOf(
                Objects.requireNonNull(profiles, "profiles cannot be null")
        );
        this.friendsByProfileId = copyRelations(
                friendsByProfileId,
                "friendsByProfileId cannot be null"
        );
        this.coworkersByProfileId = copyRelations(
                coworkersByProfileId,
                "coworkersByProfileId cannot be null"
        );
    }

    public List<Profile> getFriendsOf(String profileId) {
        return friendsByProfileId.getOrDefault(profileId, List.of())
                .stream()
                .map(profiles::get)
                .filter(profile -> profile != null)
                .toList();
    }

    public List<Profile> getCoworkersOf(String profileId) {
        return coworkersByProfileId.getOrDefault(profileId, List.of())
                .stream()
                .map(profiles::get)
                .filter(profile -> profile != null)
                .toList();
    }

    private static Map<String, List<String>> copyRelations(
            Map<String, List<String>> source,
            String nullMessage
    ) {
        Objects.requireNonNull(source, nullMessage);
        Map<String, List<String>> snapshot = new LinkedHashMap<>();
        source.forEach((profileId, relations) -> snapshot.put(
                Objects.requireNonNull(profileId, "profileId cannot be null"),
                List.copyOf(Objects.requireNonNull(relations, "relations cannot be null"))
        ));
        return Collections.unmodifiableMap(snapshot);
    }
}
