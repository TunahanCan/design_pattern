package com.can.behavirol.iterator;

import java.util.List;
import java.util.Map;

public class IteratorPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("3) Iterator");

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

        System.out.println("Ali'nin arkadaşlarına kampanya mesajı:");
        spammer.send(network.createFriendsIterator(ali.id()), "Hafta sonu etkinliği var!");

        System.out.println("Ali'nin iş arkadaşlarına duyuru:");
        spammer.send(network.createCoworkersIterator(ali.id()), "Sprint planning 10:00");
        System.out.println();
    }
}
