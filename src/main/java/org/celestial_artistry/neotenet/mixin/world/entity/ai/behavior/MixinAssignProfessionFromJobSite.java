package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AssignProfessionFromJobSite.class)
public class MixinAssignProfessionFromJobSite {

    @Redirect(method = "lambda$create$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;setVillagerData(Lnet/minecraft/world/entity/npc/VillagerData;)V"))
    private static void neotenet$callCareerEvent(Villager instance, VillagerData p_35437_, @Local(argsOnly = true) VillagerProfession p_22464_) {
        // CraftBukkit start - Fire VillagerCareerChangeEvent where Villager gets employed
        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(instance, CraftVillager.CraftProfession.minecraftToBukkit(p_22464_), VillagerCareerChangeEvent.ChangeReason.EMPLOYED);
        if (event.isCancelled()) {
            return;
        }

        instance.setVillagerData(instance.getVillagerData().setProfession(CraftVillager.CraftProfession.bukkitToMinecraft(event.getProfession())));
        // CraftBukkit end
    }
}
