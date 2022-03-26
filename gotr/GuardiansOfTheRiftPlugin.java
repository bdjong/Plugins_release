package net.runelite.client.plugins.gotr;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.gotr.config.GuardiansConfig;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Set;


@PluginDescriptor(
        name = "Guardians of the rift"
)

public class GuardiansOfTheRiftPlugin extends Plugin
{
    private static final Set<Integer> GOTR_MAP_REGIONS = ImmutableSet.of(14227,14228,14483,14484,14739,14740);
    private static final int TIME_TILL_PORTAL_1 = 160; //160
    private static final int TIME_TILL_PORTAL_2_X = 134; //134
    private boolean notifyOnce;
    private boolean inGotr = false;

    @Getter
    private boolean active;

    @Getter
    private GuardiansOfTheRiftTimer currentTimer;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private Client client;

    @Inject
    private GuardiansConfig config;

    @Override
    protected void startUp() throws Exception
    {
        System.out.println("Gotr plugin started!");
        active = true;
    }

    @Override
    protected void shutDown() throws Exception
    {
        System.out.println("Gotr plugin stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            checkInGotr();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {

        if (active && currentTimer != null && currentTimer.cull() && notifyOnce)
        {
            System.out.println("Timer is still active!");
            if(currentTimer.cull())
            {
                removeTimer();
                createTimer(Duration.ofSeconds(TIME_TILL_PORTAL_2_X));
            }
        }
    }

    private boolean checkInGotr()
    {
        System.out.println("Checking if inside GOTR");

        GameState gameState = client.getGameState();
        if (gameState != GameState.LOGGED_IN
                && gameState != GameState.LOADING)
        {
            System.out.println("Not logged in!");
            return false;
        }

        int[] currentMapRegions = client.getMapRegions();

        // Verify that all regions exist in MOTHERLODE_MAP_REGIONS
        for (int region : currentMapRegions)
        {
            System.out.println(currentMapRegions);
            if (!GOTR_MAP_REGIONS.contains(region))
            {
                return false;
            }
        }
        return true;
    }

    private void removeTimer()
    {
        infoBoxManager.removeInfoBox(currentTimer);
        currentTimer = null;
        notifyOnce = false;
    }

    private void createTimer(Duration duration)
    {
        removeTimer();
        BufferedImage image = itemManager.getImage(ItemID.MAHOGANY_PORTAL);
        currentTimer = new GuardiansOfTheRiftTimer(duration, image, this, active && true);
        infoBoxManager.addInfoBox(currentTimer);
        notifyOnce = true;
    }

    private void resetTimer(int duration)
    {
        createTimer(Duration.ofSeconds(duration));
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
//        if (!inGotr || event.getType() != ChatMessageType.SPAM)
//        {
//            System.out.println("Skipping msg");
//            return;
//        }

        String chatMessage = event.getMessage();

        switch (chatMessage)
        {
            case "The rift will become active in 30 seconds.":
                System.out.println("Game starting in 30 sec!");
                removeTimer();
                break;

            case "The rift will become active in 10 seconds.":
                System.out.println("Game starting in 10 sec!");
                removeTimer();
                break;

            case "The rift will become active in 5 seconds.":
                System.out.println("Game starting in 5 sec!");
                removeTimer();
                break;

            case "3...": // Time to start is 3 sec
                System.out.println("Game starting in 3 sec!");
                removeTimer();
                break;
            case "2...": // Time to start is 2 sec
                System.out.println("Game starting in 2 sec!");
                break;
            case "1...": // Time to start is 1 sec
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "New game started, portal timer reset", null);
                if(config.getPortalTimers())
                {
                    createTimer(Duration.ofSeconds(TIME_TILL_PORTAL_1));
                }
                break;
            case "<col=ef1020>The rift becomes active!":
                System.out.println("Rift active!");
                break;
            case "A portal to the huge guardian fragment mine has opened to the south!":
                System.out.println("Portal open!");
                //createTimer(Duration.ofSeconds(TIME_TILL_PORTAL));
                break;
            case "A portal to the huge guardian fragment mine has opened to the north!":
                System.out.println("Portal open!");
                //createTimer(Duration.ofSeconds(TIME_TILL_PORTAL));
                break;
            case "A portal to the huge guardian fragment mine has opened to the south east!":
                System.out.println("Portal open!");
                //createTimer(Duration.ofSeconds(TIME_TILL_PORTAL));
                break;
            case "A portal to the huge guardian fragment mine has opened to the south west!":
                System.out.println("Portal open!");
                //createTimer(Duration.ofSeconds(TIME_TILL_PORTAL));
                break;
            case "<col=ef1020>The Great Guardian was defeated!":
                System.out.println("Lost the game!");
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "THE GUARDIAN IS DEAD :(", null);
                removeTimer();
                break;
            case "T": // Time to start is 1 sec
                if(config.getPortalTimers())
                {
                    createTimer(Duration.ofSeconds(TIME_TILL_PORTAL_1));
                }
                break;
                //Creatures from the Abyss will attack in 120 seconds.
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        String key = event.getKey();
        switch (key)
        {
            case "portalTimers":
                //Do nothing
                break;
        }
    }

    @Provides
    GuardiansConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuardiansConfig.class);
    }
}
