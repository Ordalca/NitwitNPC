package me.ordalca.nitwitnpc;

import com.pixelmonmod.pixelmon.api.replacement.logic.VillagerReplacementLogic;
import com.pixelmonmod.pixelmon.entities.SpawnLocationType;
import com.pixelmonmod.pixelmon.entities.npcs.NPCQuestGiver;
import com.pixelmonmod.pixelmon.entities.npcs.registry.GeneralNPCData;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class EnhancedVillagerReplacement extends VillagerReplacementLogic {
    @Override
    public void replaceSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getEntity();

            if(villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                createQuestGiver(event);
            }
        } else {
            createQuestGiver(event);
        }
    }

    public void createQuestGiver(EntityJoinWorldEvent event) {
        event.setCanceled(true);
        if (!event.getWorld().isClientSide) {
            BlockPos position = event.getEntity().blockPosition();
            addQuestGiver(position, null, event.getWorld());
        }
    }

    public static void addQuestGiver(BlockPos position, Vector2f rotation, World world) {
        NPCQuestGiver npc = new NPCQuestGiver(world);
        GeneralNPCData data = ServerNPCRegistry.villagers.getRandom();
        npc.init(data);
        npc.setCustomSteveTexture(data.getRandomTexture());
        if(rotation != null) {
            npc.moveTo(position.getX(), position.getY(), position.getZ(), rotation.y, rotation.x);
        } else {
            npc.moveTo(position.getX(), position.getY(), position.getZ());
        }
        npc.setProfession(0);
        npc.initVilagerAI();
        npc.npcLocation = SpawnLocationType.LAND_VILLAGER;
        npc.setPersistenceRequired();

        ThreadTaskExecutor<Runnable> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        executor.tell(new TickDelayedTask(0, () -> {
            ModFile.LOGGER.debug("spawning questGiver");
            world.addFreshEntity(npc);
        }));
    }
}
