package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResetProfession.class)
public class MixinResetProfession {

    @Redirect(method = "lambda$create$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;setVillagerData(Lnet/minecraft/world/entity/npc/VillagerData;)V"))
    private static void neotenet$professionEvent(Villager instance, VillagerData p_35437_, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(instance, CraftVillager.CraftProfession.minecraftToBukkit(VillagerProfession.NONE), VillagerCareerChangeEvent.ChangeReason.LOSING_JOB);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }

        instance.setVillagerData(instance.getVillagerData().setProfession(CraftVillager.CraftProfession.bukkitToMinecraft(event.getProfession())));
        // CraftBukkit end
    }
}
