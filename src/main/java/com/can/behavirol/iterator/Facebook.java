package com.can.behavirol.iterator;

public class Facebook implements SocialNetwork {

    private final SocialGraph socialGraph;

    public Facebook(SocialGraph socialGraph) {
        this.socialGraph = socialGraph;
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
