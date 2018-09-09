package quaternary.qsilkspawners;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;

@Mod(modid = QSilkSpawners.MODID, name = QSilkSpawners.NAME, version = QSilkSpawners.VERSION)
@Mod.EventBusSubscriber(modid = QSilkSpawners.MODID)
public class QSilkSpawners {
	public static final String MODID = "qsilkspawners";
	public static final String NAME = "Q Silk Spawners";
	public static final String VERSION = "GRADLE:VERSION";
	
	public static final String TAG_SPAWNER_DATA = "SilkSpawnerData";
	
	@SubscribeEvent
	public static void blocks(RegistryEvent.Register<Block> e) {
		IForgeRegistry<Block> reg = e.getRegistry();
		
		//shut up, intellij!
		assert Blocks.MOB_SPAWNER.getRegistryName() != null;
		
		LogManager.getLogger(NAME).info("Registry replacing the mob spawner block - Forge will print a warning, this is expected! Have a nice day");
		reg.register(new BlockReplacementMobSpawner().setRegistryName(Blocks.MOB_SPAWNER.getRegistryName()).setTranslationKey("mobSpawner"));
	}
	
	@SubscribeEvent
	public static void harvestDrops(BlockEvent.HarvestDropsEvent e) {
		World world = e.getWorld();
		if(world.isRemote || !e.isSilkTouching()) return;
		
		TileEntity tile = world.getTileEntity(e.getPos());
		if(tile instanceof TileEntityMobSpawner) {
			ItemStack drop = new ItemStack(Blocks.MOB_SPAWNER);
			
			NBTTagCompound spawnerData = ((TileEntityMobSpawner)tile).getSpawnerBaseLogic().writeToNBT(new NBTTagCompound());
			spawnerData.removeTag("Delay");
			
			NBTTagCompound stackTag = new NBTTagCompound();
			stackTag.setTag(TAG_SPAWNER_DATA, spawnerData);
			drop.setTagCompound(stackTag);
			
			e.getDrops().add(drop);
		}
	}
}
