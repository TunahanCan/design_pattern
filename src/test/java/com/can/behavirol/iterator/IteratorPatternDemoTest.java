package com.can.behavirol.iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class IteratorPatternDemoTest {

    @Test
    void shouldIterateFriendsAndCoworkersIndependently() {
        Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");
        Profile ayse = new Profile("2", "Ayşe", "ayse@acme.com", "Acme");
        Profile mehmet = new Profile("3", "Mehmet", "mehmet@globex.com", "Globex");
        Profile zeynep = new Profile("4", "Zeynep", "zeynep@acme.com", "Acme");

        SocialGraph graph = new SocialGraph(
                Map.of(
                        ali.id(), ali,
                        ayse.id(), ayse,
                        mehmet.id(), mehmet,
                        zeynep.id(), zeynep
                ),
                Map.of(
                        ali.id(), List.of(ayse.id(), mehmet.id(), zeynep.id())
                ),
                Map.of(
                        ali.id(), List.of(ayse.id(), zeynep.id())
                )
        );

        SocialNetwork network = new Facebook(graph);
        SocialSpammer spammer = new SocialSpammer();

        List<String> friendsEmails = spammer.send(network.createFriendsIterator(ali.id()), "kampanya");
        List<String> coworkersEmails = spammer.send(network.createCoworkersIterator(ali.id()), "duyuru");

        assertEquals(List.of("ayse@acme.com", "mehmet@globex.com", "zeynep@acme.com"), friendsEmails);
        assertEquals(List.of("ayse@acme.com", "zeynep@acme.com"), coworkersEmails);
    }

    @Test
    void shouldReturnNullWhenIteratorIsFinished() {
        Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");

        SocialGraph graph = new SocialGraph(
                Map.of(ali.id(), ali),
                Map.of(ali.id(), List.of()),
                Map.of(ali.id(), List.of())
        );

        ProfileIterator iterator = new Facebook(graph).createFriendsIterator(ali.id());

        assertEquals(false, iterator.hasMore());
        assertNull(iterator.getNext());
    }
}
