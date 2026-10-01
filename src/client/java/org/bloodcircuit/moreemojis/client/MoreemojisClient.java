package org.bloodcircuit.moreemojis.client;

import net.fabricmc.api.ClientModInitializer;
import org.bloodcircuit.moreemojis.client.cr;

public class MoreemojisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        cr.register();
    }
}