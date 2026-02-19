package com.can.behavirol.iterator;

import java.util.List;
import java.util.Map;

public class SocialGraph {

    private final Map<String, Profile> profiles;
    private final Map<String, List<String>> friendsByProfileId;
    private final Map<String, List<String>> coworkersByProfileId;

    public SocialGraph(Map<String, Profile> profiles,
                       Map<String, List<String>> friendsByProfileId,
                       Map<String, List<String>> coworkersByProfileId) {
        this.profiles = profiles;
        this.friendsByProfileId = friendsByProfileId;
        this.coworkersByProfileId = coworkersByProfileId;
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
}
