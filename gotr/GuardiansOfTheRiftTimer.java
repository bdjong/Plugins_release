package net.runelite.client.plugins.gotr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Timer;

public class GuardiansOfTheRiftTimer extends Timer {
    @Getter
    @Setter
    private boolean visible;

    GuardiansOfTheRiftTimer(Duration duration, BufferedImage image, Plugin plugin, boolean visible)
    {
        super(duration.toMillis(), ChronoUnit.MILLIS, image, plugin);
        setTooltip("Time until Portal spawns");
        this.visible = visible;
    }

    @Override
    public Color getTextColor()
    {
        Duration timeLeft = Duration.between(Instant.now(), getEndTime());

        if (timeLeft.getSeconds() < 30)
        {
            return Color.RED.brighter();
        }

        return Color.WHITE;
    }

    @Override
    public boolean render()
    {
        return visible && super.render();
    }
}
