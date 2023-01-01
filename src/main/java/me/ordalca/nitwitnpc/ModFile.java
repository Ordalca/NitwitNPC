package me.ordalca.nitwitnpc;

import com.pixelmonmod.pixelmon.api.replacement.ReplacementLogicRegistry;
import com.pixelmonmod.pixelmon.entities.SpawnLocationType;
import com.pixelmonmod.pixelmon.entities.npcs.NPCQuestGiver;
import com.pixelmonmod.pixelmon.entities.npcs.registry.GeneralNPCData;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ServerNPCRegistry;
import com.pixelmonmod.pixelmon.items.heldItems.MailItem;
import me.ordalca.nitwitnpc.init.EnhancedVillagerReplacement;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModFile.MOD_ID)
@Mod.EventBusSubscriber(modid = ModFile.MOD_ID)
public class ModFile {

    public static final String MOD_ID = "nitwitnpc";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static ModFile instance;

    public ModFile() {
        instance = this;

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ReplacementLogicRegistry.register(EnhancedVillagerReplacement::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loaded NitwitNPC mod");
    }

    @SubscribeEvent
    public void fireVillager(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if(event.getTarget() instanceof VillagerEntity)
        {
            VillagerEntity villager = (VillagerEntity) event.getTarget();
            if(villager == null || villager.getVillagerData().getProfession() != VillagerProfession.NONE) {
                return;
            }
            PlayerEntity player = event.getPlayer();
            ItemStack usedItem = player.getItemInHand(event.getHand());
            if (usedItem != null && usedItem.getItem() instanceof MailItem) {
                int count = usedItem.getCount();
                usedItem.setCount(count-1);
                createQuestGiverFromVillager(villager, event);

                villager.releasePoi(MemoryModuleType.HOME);
                villager.releasePoi(MemoryModuleType.JOB_SITE);
                villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
                villager.releasePoi(MemoryModuleType.MEETING_POINT);
                villager.remove();
            }
        }
    }

    public void createQuestGiverFromVillager(VillagerEntity villager, PlayerInteractEvent event) {
        NPCQuestGiver npc = new NPCQuestGiver(event.getWorld());
        GeneralNPCData data = ServerNPCRegistry.villagers.getRandom();
        npc.init(data);
        npc.setCustomSteveTexture(data.getRandomTexture());
        npc.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.yRot, villager.xRot);
        npc.setProfession(0);
        npc.initVilagerAI();
        npc.npcLocation = SpawnLocationType.LAND_VILLAGER;
        npc.setPersistenceRequired();

        ThreadTaskExecutor<Runnable> executor = (ThreadTaskExecutor) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        executor.tell(new TickDelayedTask(0, () -> {
            event.getWorld().addFreshEntity(npc);
        }));
    }

    public static ModFile getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
