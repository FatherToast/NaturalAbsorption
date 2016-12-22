package toast.naturalAbsorption.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import toast.naturalAbsorption.ModNaturalAbsorption;
import toast.naturalAbsorption.ShieldManager;

public class MessageSyncShield implements IMessage
{
    private int entityId;
    private float absorption;
    private NBTTagCompound shieldData;

    public MessageSyncShield() { }

    public MessageSyncShield(EntityPlayer player) {
        this.entityId = player.getEntityId();
        this.absorption = player.getAbsorptionAmount();
        this.shieldData = ShieldManager.getShieldData(player);
    }

	@Override
	public void toBytes(ByteBuf bytebuf) {
		PacketBuffer buf = new PacketBuffer(bytebuf);
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeFloat(this.absorption);
        buf.writeNBTTagCompoundToBuffer(this.shieldData);
	}
	@Override
	public void fromBytes(ByteBuf bytebuf) {
		PacketBuffer buf = new PacketBuffer(bytebuf);
        this.entityId = buf.readVarIntFromBuffer();
        this.absorption = buf.readFloat();
        try {
			this.shieldData = buf.readNBTTagCompoundFromBuffer();
		}
		catch (IOException ex) {
            ModNaturalAbsorption.logError("Failed to read shield data tag from server!");
			ex.printStackTrace();
			this.shieldData = new NBTTagCompound();
		}
	}

    @SideOnly(Side.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    @SideOnly(Side.CLIENT)
    public float getAbsorption() {
        return this.absorption;
    }
    @SideOnly(Side.CLIENT)
    public NBTTagCompound getShieldData() {
        return this.shieldData;
    }

    public static class Handler implements IMessageHandler<MessageSyncShield, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncShield message, MessageContext ctx) {
            try {
                new Thread(new SyncShield(message)).start();
            }
            catch (Exception ex) {
                ModNaturalAbsorption.logError("Failed to fetch health and shield update from server!");
                ex.printStackTrace();
            }
            return null;
        }
    }

    private static class SyncShield implements Runnable {

    	private final MessageSyncShield message;

        public SyncShield(MessageSyncShield message) {
			this.message = message;
		}

		@Override
        public void run() {
			try {
	            World world = null;
	            for (int attempts = 0; attempts < 10; attempts++) {
	            	world = FMLClientHandler.instance().getWorldClient();
	            	if (world == null) Thread.sleep(500);
	            	else break;
	        	}
	            if (world == null) return;

	            EntityPlayer player = null;
	            for (int attempts = 0; attempts < 10; attempts++) {
	            	player = (EntityPlayer) world.getEntityByID(this.message.getEntityId());
	            	if (player == null) Thread.sleep(500);
	            	else break;
	        	}
	            if (player == null) return;

                ShieldManager.setShieldData(player, this.message.getShieldData());
                player.setAbsorptionAmount(this.message.getAbsorption());
			}
			catch (Exception ex) { }
        }
    }

}