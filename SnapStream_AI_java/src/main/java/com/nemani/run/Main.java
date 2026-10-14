package com.nemani.run;

import com.snapstream.SnapStreamApp;

public class Main {
    public static void main(String[] args) {
        // Der Wrapper ist zwingend nötig, damit Fat-JARs mit JavaFX ab JDK 11 funktionieren!
        SnapStreamApp.main(args);
    }
}
