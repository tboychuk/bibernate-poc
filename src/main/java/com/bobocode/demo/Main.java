package com.bobocode.demo;

import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.demo.entity.Participant;

public class Main {
    public static void main(String[] args) {
        var sessionFactory = new SessionFactoryImpl("jdbc:postgresql://93.175.203.215:5432/postgres", "bobouser", "bobopass");
        // magic
        var session = sessionFactory.openSession();
        
        
        var participant = session.findById(Participant.class, 3);
        System.out.println(participant.getFirstName() + " "+participant.getLastName());

        session.close();
        
        // load skills (SELECT)
        participant.getSkills().forEach(skill -> System.out.println(skill.getTitle()));

        
        // the end of the magic

        
    }
}