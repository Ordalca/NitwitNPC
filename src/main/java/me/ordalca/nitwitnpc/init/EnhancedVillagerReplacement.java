package me.ordalca.nitwitnpc.init;

import com.pixelmonmod.pixelmon.api.replacement.logic.VillagerReplacementLogic;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EnhancedVillagerReplacement extends VillagerReplacementLogic {
    @Override
    public void replaceSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();
            if(villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                super.replaceSpawn(event);
            }
        } else {
            super.replaceSpawn(event);
        }
    }
}
