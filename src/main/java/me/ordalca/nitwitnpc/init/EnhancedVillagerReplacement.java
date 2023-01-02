package me.ordalca.nitwitnpc.init;

import com.pixelmonmod.pixelmon.api.replacement.logic.VillagerReplacementLogic;
import com.pixelmonmod.pixelmon.entities.SpawnLocationType;
import com.pixelmonmod.pixelmon.entities.npcs.NPCQuestGiver;
import com.pixelmonmod.pixelmon.entities.npcs.registry.GeneralNPCData;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import me.ordalca.nitwitnpc.ModFile;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class EnhancedVillagerReplacement extends VillagerReplacementLogic {
    @Override
    public void replaceSpawn(EntityJoinWorldEvent event) {
        ModFile.LOGGER.debug("Enhanced");
        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();
            ModFile.LOGGER.debug("is villager");

            if(villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                ModFile.LOGGER.debug("is nitwit");
                createQuestGiver(event);
            }
        } else {
            ModFile.LOGGER.debug("other replace");
            createQuestGiver(event);
        }
    }

    public void createQuestGiver(EntityJoinWorldEvent event) {
        event.setCanceled(true);
        Vector3d position = event.getEntity().position();

        NPCQuestGiver npc = new NPCQuestGiver(event.getWorld());
        GeneralNPCData data = ServerNPCRegistry.villagers.getRandom();
        npc.init(data);
        npc.setCustomSteveTexture(data.getRandomTexture());
        npc.setPos(position.x(), position.y(), position.z());
        npc.setProfession(0);
        npc.initVilagerAI();
        npc.npcLocation = SpawnLocationType.LAND_VILLAGER;
        npc.setPersistenceRequired();
        ThreadTaskExecutor executor = (ThreadTaskExecutor) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        executor.tell(new TickDelayedTask(0, () -> {
            event.getWorld().addFreshEntity(npc);
        }));
    }
}
