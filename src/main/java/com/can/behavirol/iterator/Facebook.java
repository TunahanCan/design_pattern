package com.can.behavirol.iterator;

import java.util.Objects;

public class Facebook implements SocialNetwork {

    private final SocialGraph socialGraph;

    public Facebook(SocialGraph socialGraph) {
        this.socialGraph = Objects.requireNonNull(socialGraph, "socialGraph cannot be null");
    }

    @Override
    public ProfileIterator createFriendsIterator(String profileId) {
        return new SocialGraphIterator(socialGraph, profileId, RelationType.FRIENDS);
    }

    @Override
    public ProfileIterator createCoworkersIterator(String profileId) {
        return new SocialGraphIterator(socialGraph, profileId, RelationType.COWORKERS);
    }
}
