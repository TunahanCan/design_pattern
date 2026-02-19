package com.can.behavirol.iterator;

import java.util.ArrayList;
import java.util.List;

public class SocialSpammer {

    public List<String> send(ProfileIterator iterator, String message) {
        List<String> sentEmails = new ArrayList<>();

        while (iterator.hasMore()) {
            Profile profile = iterator.getNext();
            String output = "Mail gönderildi -> " + profile.email() + " | Mesaj: " + message;
            System.out.println(output);
            sentEmails.add(profile.email());
        }

        return sentEmails;
    }
}
