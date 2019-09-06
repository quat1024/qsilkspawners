package quaternary.qsilkspawners.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quaternary.qsilkspawners.ModConfig;
import quaternary.qsilkspawners.QSilkSpawners;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Mod.EventBusSubscriber(modid = QSilkSpawners.MODID, value = CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void tooltip(ItemTooltipEvent e) {
		if(e.getEntityPlayer() == null || Minecraft.getMinecraft().world == null) return; //During game startup
		
		ItemStack stack = e.getItemStack();
		if(stack.getItem() != QSilkSpawners.mobSpawnerItem) return;
		if(!stack.hasTagCompound()) return;
		
		NBTTagCompound stackTag = stack.getTagCompound();
		assert stackTag != null;
		
		if(ModConfig.SERVERSIDE_ONLY_MODE &&
			stackTag.hasKey("display", Constants.NBT.TAG_COMPOUND) &&
			stackTag.getCompoundTag("display").hasKey("Lore", Constants.NBT.TAG_LIST)) {
			return;
		}
		
		NBTTagCompound spawnerDataNBT = stackTag.getCompoundTag(QSilkSpawners.TAG_SPAWNER_DATA);
		if(spawnerDataNBT.isEmpty()) return; //missing tag
		
		// (spaghetti emoji)
		DummyMobSpawnerBaseLogic.SHARED_INST.readFromNBT(spawnerDataNBT);
		Entity ent = DummyMobSpawnerBaseLogic.SHARED_INST.getCachedEntity();
		
		e.getToolTip().add(I18n.translateToLocalFormatted("qsilkspawners.tooltip.entity", ent.getName()));
	}
}
