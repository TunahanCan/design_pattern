package com.can.behavirol.iterator;

import java.util.List;
import java.util.Objects;

public class SocialGraphIterator implements ProfileIterator {

    private final SocialGraph socialGraph;
    private final String profileId;
    private final RelationType relationType;

    private int currentPosition;
    private List<Profile> cache;

    public SocialGraphIterator(SocialGraph socialGraph, String profileId, RelationType relationType) {
        this.socialGraph = Objects.requireNonNull(socialGraph, "socialGraph cannot be null");
        this.profileId = Objects.requireNonNull(profileId, "profileId cannot be null");
        this.relationType = Objects.requireNonNull(relationType, "relationType cannot be null");
    }

    @Override
    public boolean hasMore() {
        lazyInit();
        return currentPosition < cache.size();
    }

    @Override
    public Profile getNext() {
        if (!hasMore()) {
            return null;
        }

        Profile profile = cache.get(currentPosition);
        currentPosition++;
        return profile;
    }

    private void lazyInit() {
        if (cache != null) {
            return;
        }

        cache = switch (relationType) {
            case FRIENDS -> socialGraph.getFriendsOf(profileId);
            case COWORKERS -> socialGraph.getCoworkersOf(profileId);
        };
    }
}
