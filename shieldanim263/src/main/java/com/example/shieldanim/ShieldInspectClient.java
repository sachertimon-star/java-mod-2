package com.example.shieldanim;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

/** Registriert die Inspect-Taste, verwaltet den Fortschritt (0..1) und die Easing-Funktionen. */
public class ShieldInspectClient implements ClientModInitializer {

    public static final String MOD_ID = "shieldanim";

    public static KeyMapping INSPECT_KEY;

    private static float progress = 0f;
    private static float prevProgress = 0f;

    private static final float IN_TICKS  = 8f;  // ~0,4 s hinein
    private static final float OUT_TICKS = 7f;  // ~0,35 s zurück

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        INSPECT_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shieldanim.inspect",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_G,
                category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            prevProgress = progress;
            var p = client.player;
            boolean hasShield = p != null
                    && (p.getMainHandItem().is(Items.SHIELD) || p.getOffhandItem().is(Items.SHIELD));
            // Abbruch bei Blocken oder Schwingen
            boolean want = hasShield && INSPECT_KEY.isDown() && !p.isUsingItem() && !p.swinging;
            progress = Mth.clamp(progress + (want ? 1f / IN_TICKS : -1f / OUT_TICKS), 0f, 1f);
        });
    }

    /** Geglätteter Inspect-Fortschritt, interpoliert mit partialTick. */
    public static float inspect(float partialTick) {
        return easeInOutCubic(Mth.lerp(partialTick, prevProgress, progress));
    }

    public static float easeInOutCubic(float t) {
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }

    /** Überschwingt kurz über 1.0 -> "Aufschwing"-Effekt. */
    public static float easeOutBack(float t) {
        final float c1 = 1.70158f, c3 = c1 + 1f;
        float u = t - 1f;
        return 1f + c3 * u * u * u + c1 * u * u;
    }
}
