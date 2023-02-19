package me.ordalca.nitwitnpc.network.message;

import me.ordalca.nitwitnpc.EnhancedVillagerReplacement;

import me.ordalca.nitwitnpc.ModFile;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ConvertVillagerMessage {
    public BlockPos position;
    public Vector2f rotation;
    public UUID villagerID;

    public ConvertVillagerMessage(VillagerEntity villager) {
        this.position = villager.blockPosition().mutable();
        this.rotation = villager.getRotationVector();
        this.villagerID = villager.getUUID();
    }
    public ConvertVillagerMessage(BlockPos position, Vector2f rotation, UUID id) {
        this.position = position;
        this.rotation = rotation;
        this.villagerID = id;
    }

    public static void encode(ConvertVillagerMessage message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.position);
        buffer.writeFloat(message.rotation.x);
        buffer.writeFloat(message.rotation.y);
        buffer.writeUUID(message.villagerID);
    }

    public static ConvertVillagerMessage decode(PacketBuffer buffer) {
        BlockPos position = buffer.readBlockPos();
        float rotx = buffer.readFloat();
        float roty = buffer.readFloat();
        Vector2f vector = new Vector2f(rotx, roty);
        UUID id = buffer.readUUID();
        return new ConvertVillagerMessage(position, vector, id);
    }
    public static void handle(ConvertVillagerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                ServerWorld world = player.getLevel();
                EnhancedVillagerReplacement.addQuestGiver(message.position, message.rotation, world);

                VillagerEntity villager = (VillagerEntity) world.getEntity(message.villagerID);
                if (villager != null && villager.blockPosition().getY() > 0) {
                    //kill villager by dropping them into the void (avoiding conflicts with other mods)
                    villager.setPos(villager.getX(), -10, villager.getZ());
                }
            }
        });
        context.setPacketHandled(true);
    }
}
