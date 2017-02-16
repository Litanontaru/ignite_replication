package org.apache.ignite.replication.boot;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 10:51 AM
 */
public class DRIgnite {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start("dr-ignite-config.xml");
        System.out.println("Started");
    }
}
