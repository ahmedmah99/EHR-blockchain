package com.app;

import java.lang.reflect.InvocationTargetException;

public class Main {


    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Simulator simulator = new Simulator();
        simulator.run();
    }
}
