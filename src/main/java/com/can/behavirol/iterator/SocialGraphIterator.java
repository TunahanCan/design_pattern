package com.can.behavirol.iterator;

import java.util.List;

public class SocialGraphIterator implements ProfileIterator {

    private final SocialGraph socialGraph;
    private final String profileId;
    private final RelationType relationType;

    private int currentPosition;
    private List<Profile> cache;

    public SocialGraphIterator(SocialGraph socialGraph, String profileId, RelationType relationType) {
        this.socialGraph = socialGraph;
        this.profileId = profileId;
        this.relationType = relationType;
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

        cache = relationType == RelationType.FRIENDS
                ? socialGraph.getFriendsOf(profileId)
                : socialGraph.getCoworkersOf(profileId);
    }
}
