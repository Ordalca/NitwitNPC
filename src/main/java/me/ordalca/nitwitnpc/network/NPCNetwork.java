package me.ordalca.nitwitnpc.network;

import me.ordalca.nitwitnpc.ModFile;
import me.ordalca.nitwitnpc.network.message.ConvertVillagerMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NPCNetwork {
    public static final String VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(ModFile.MOD_ID, "npcnetwork"), ()->VERSION, VERSION::equals, VERSION::equals);

    public static void init() {
        CHANNEL.registerMessage(0, ConvertVillagerMessage.class, ConvertVillagerMessage::encode, ConvertVillagerMessage::decode, ConvertVillagerMessage::handle);
    }
}
