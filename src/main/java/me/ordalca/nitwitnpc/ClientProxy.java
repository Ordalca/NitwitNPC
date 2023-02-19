package me.ordalca.nitwitnpc;

import com.pixelmonmod.pixelmon.items.heldItems.MailItem;
import me.ordalca.nitwitnpc.network.NPCNetwork;
import me.ordalca.nitwitnpc.network.message.ConvertVillagerMessage;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModFile.MOD_ID, value = Dist.CLIENT)
public class ClientProxy {
    @SubscribeEvent
    public static void fireVillager(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) event.getTarget();
            if (villager == null || villager.getVillagerData().getProfession() != VillagerProfession.NONE) {
                return;
            }
            PlayerEntity player = event.getPlayer();
            ItemStack usedItem = player.getItemInHand(event.getHand());
            if (usedItem.getItem() instanceof MailItem) {
                int count = usedItem.getCount();
                usedItem.setCount(count - 1);
                if (event.getPlayer().level.isClientSide()) {
                    NPCNetwork.CHANNEL.sendToServer(new ConvertVillagerMessage(villager));
                }
                villager.setPos(villager.getX(), -10, villager.getZ());
            }
        }
    }
}
