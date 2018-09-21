package quaternary.qsilkspawners;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = QSilkSpawners.MODID, name = QSilkSpawners.NAME, version = QSilkSpawners.VERSION)
@Mod.EventBusSubscriber(modid = QSilkSpawners.MODID)
public class QSilkSpawners {
	public static final String MODID = "qsilkspawners";
	public static final String NAME = "Q Silk Spawners";
	public static final String VERSION = "GRADLE:VERSION";
	
	public static final String TAG_SPAWNER_DATA = "SilkSpawnerData";
	
	public static Item mobSpawnerItem = null;
	
	@Mod.EventHandler
	public static void postinit(FMLPostInitializationEvent e) {
		//Yeah I'd rather not look this up every single time a player places a block x)
		mobSpawnerItem = Item.getItemFromBlock(Blocks.MOB_SPAWNER);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBreak(BlockEvent.BreakEvent e) {
		IBlockState state = e.getState();
		if(state == null || !(state.getBlock() instanceof BlockMobSpawner)) return;
		
		World world = e.getWorld();
		if(world.isRemote) return;
		
		TileEntity tile = world.getTileEntity(e.getPos());
		if(!(tile instanceof TileEntityMobSpawner)) return;
		
		EntityPlayer player = e.getPlayer();
		if(player == null) return;
		
		if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItem(player.getActiveHand())) >= 1) {
			e.setExpToDrop(0);
			
			ItemStack drop = new ItemStack(Blocks.MOB_SPAWNER);
			
			NBTTagCompound spawnerData = ((TileEntityMobSpawner)tile).getSpawnerBaseLogic().writeToNBT(new NBTTagCompound());
			spawnerData.removeTag("Delay"); //Noone needs this :D
			
			NBTTagCompound stackTag = new NBTTagCompound();
			stackTag.setTag(TAG_SPAWNER_DATA, spawnerData);
			drop.setTagCompound(stackTag);
			
			Block.spawnAsEntity(world, e.getPos(), drop);
			
			//Kill the block and event early
			//This (along with the high event priority) fixes things like
			//enderio broken spawners being dropped even when you silk touch it
			//This seems kinda hacky and I'm looking for a cleaner solution
			//TODO: does this cause problems w/ block protection?
			world.destroyBlock(e.getPos(), false);
			world.removeTileEntity(e.getPos()); //Just in case? EnderIO does some fun stuff here haha
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlace(BlockEvent.PlaceEvent e) {
		//TileEntityMobSpawner has (sensibly) onlyOpsCanSetNBT, meaning ItemBlock's BlockEntityTag trickery
		//isn't going to work.
		//So let's set the tile entity data here manually, instead!
		EntityPlayer player = e.getPlayer();
		if(player == null) return;
		
		ItemStack stack = player.getHeldItem(player.getActiveHand());
		if(stack.getItem() != mobSpawnerItem) return;
		if(!stack.hasTagCompound()) return;
		
		NBTTagCompound stackTag = stack.getTagCompound(); assert stackTag != null;
		NBTTagCompound spawnerDataNBT = stackTag.getCompoundTag(QSilkSpawners.TAG_SPAWNER_DATA);
		if(spawnerDataNBT.isEmpty()) return;
		
		TileEntity tile = e.getWorld().getTileEntity(e.getPos());
		if(tile instanceof TileEntityMobSpawner) {
			((TileEntityMobSpawner)tile).getSpawnerBaseLogic().readFromNBT(spawnerDataNBT);
		}
	}
}
