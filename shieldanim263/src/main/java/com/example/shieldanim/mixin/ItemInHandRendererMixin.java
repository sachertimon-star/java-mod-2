package com.example.shieldanim.mixin;

import com.example.shieldanim.ShieldInspectClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ab 26.2 heißt die Methode submitArmWithItem (vorher renderArmWithItem).
 * Wir legen zusätzliche Transformationen auf den PoseStack, bevor Vanilla das Schild einreiht.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    // ---- Tuning-Konstanten (im Spiel anpassen!) ----
    // Pull-out
    private static final float PO_YAW   = -60f;
    private static final float PO_ROLL  =  28f;
    private static final float PO_PITCH =  25f;
    private static final float PO_X     = -0.30f;
    private static final float PO_Y     = -0.10f;
    private static final float PO_Z     =  0.20f;
    // Inspect
    private static final float IN_PITCH = -68f;
    private static final float IN_YAW   =  -8f;
    private static final float IN_X     =  0.42f;
    private static final float IN_Y     =  0.22f;
    private static final float IN_Z     =  0.18f;
    private static final float SWAY     =  3.0f;

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void shieldanim$push(AbstractClientPlayer player, float frameInterp, float xRot,
                                 InteractionHand hand, float attack, ItemStack itemStack,
                                 float inverseArmHeight, PoseStack poseStack,
                                 SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (!itemStack.is(Items.SHIELD)) return;
        poseStack.pushPose();

        // +1 = rechte Seite, -1 = linke Seite
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        float side = arm == HumanoidArm.RIGHT ? 1f : -1f;

        // ---------- 1) Pull-out ----------
        // inverseArmHeight: 1 = ganz unten, 0 = fertig ausgerüstet
        float t = 1f - inverseArmHeight;
        float f = 1f - ShieldInspectClient.easeOutBack(Math.min(Math.max(t, 0f), 1f)); // 1 -> 0 mit Überschwingen
        if (Math.abs(f) > 0.0001f) {
            poseStack.translate(side * PO_X * f, PO_Y * f, PO_Z * f);
            poseStack.mulPose(Axis.YP.rotationDegrees(side * PO_YAW * f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(side * PO_ROLL * f));
            poseStack.mulPose(Axis.XP.rotationDegrees(PO_PITCH * f));
        }

        // ---------- 2) Inspect ----------
        float p = ShieldInspectClient.inspect(frameInterp);
        if (p > 0.0001f) {
            float time = player.tickCount + frameInterp;
            float sway = (float) Math.sin(time * 0.12f) * SWAY * p;

            poseStack.translate(-side * IN_X * p, IN_Y * p, IN_Z * p);
            poseStack.mulPose(Axis.XP.rotationDegrees(IN_PITCH * p));
            poseStack.mulPose(Axis.YP.rotationDegrees(side * IN_YAW * p + sway));
            poseStack.mulPose(Axis.ZP.rotationDegrees(sway * 0.5f));
        }
    }

    @Inject(method = "submitArmWithItem", at = @At("RETURN"))
    private void shieldanim$pop(AbstractClientPlayer player, float frameInterp, float xRot,
                                InteractionHand hand, float attack, ItemStack itemStack,
                                float inverseArmHeight, PoseStack poseStack,
                                SubmitNodeCollector collector, int light, CallbackInfo ci) {
        if (itemStack.is(Items.SHIELD)) poseStack.popPose();
    }
}
