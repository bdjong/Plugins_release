package net.runelite.client.plugins.gotr.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Gotr")
public interface GuardiansConfig extends Config
{
    @ConfigSection(
            name = "Timers",
            description = "General timer options",
            position = 0
    )
    String settingList = "settingList";

    @ConfigItem(
            keyName = "portalTimers",
            name = "Portals",
            description = "Show/hide portal timers",
            position = 0,
            section = settingList
    )
    default boolean getPortalTimers()
    {
        return true;
    }
}
