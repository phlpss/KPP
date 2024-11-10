package com.example.scheduling_meetings.mappers;

public interface Mapper<A,B> {

    B mapTo(A a);

    A mapFrom(B b);

}