package toast.naturalAbsorption.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import toast.naturalAbsorption.CommonProxy;
import toast.naturalAbsorption.ModNaturalAbsorption;


public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		ClientProxy.register(ModNaturalAbsorption.ABSORB_BOOK);
	}

	public static void register(Item item) {
		ClientProxy.register(item, 0);
	}
	public static void register(Item item, int meta) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
	    .register(item, meta, new ModelResourceLocation(Item.REGISTRY.getNameForObject(item), "inventory"));
	}

}
