package com.bobocode.demo;

import com.bobocode.bibernate.Orm;
import com.bobocode.demo.entity.Participant;

public class Main {
    public static void main(String[] args) {
        var orm = new Orm("jdbc:postgresql://93.175.203.215:5432/postgres", "bobouser","bobopass");
        var participant = orm.findById(Participant.class, 10);
        System.out.println(participant);
    }
}