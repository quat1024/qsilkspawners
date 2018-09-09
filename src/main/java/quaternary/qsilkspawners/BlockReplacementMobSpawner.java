package quaternary.qsilkspawners;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quaternary.qsilkspawners.client.DummyMobSpawnerBaseLogic;

import javax.annotation.Nullable;
import java.util.List;

public class BlockReplacementMobSpawner extends BlockMobSpawner {
	public BlockReplacementMobSpawner() {
		setHardness(5f);
		//These 2 are protected so I can't build them from outside, like Block can.
		//Big oof.
		setSoundType(SoundType.METAL);
		disableStats();
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		//A little bit of trickery is needed to get tile entity access in HarvestDropsEvent.
		if(willHarvest) {
			//Don't erase the block just yet.
			return true;
		} else {
			//This method erases the tile entity and the block at once.
			return super.removedByPlayer(state, world, pos, player, willHarvest);
		}
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(world, player, pos, state, te, stack);
		//Set the block to air now (= erase the tile entity), since I skipped doing it above.
		world.setBlockToAir(pos);
	}
	
	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//This causes Block#harvestBlock to fire a HarvestDropsEvent w/ silk touch set to true
		//That event actually *does* provide access to a world and a pos, hurrah!
		return true;
	}
	
	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		//The default implementation drops an Item.getItemFromBlock with the same data value.
		//I'd love to add the silk drop here, but of course there's no access to the tile entity,
		//all I get is an IBlockState.
		//So I will add it later, in HarvestDropsEvent.
		//But setting this to EMPTY makes it so a duplicate dataless spawner isn't dropped by this.
		return ItemStack.EMPTY;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		//TileEntityMobSpawner has (sensibly) onlyOpsCanSetNBT, meaning ItemBlock's BlockEntityTag trickery
		//isn't going to work.
		//So let's set the tile entity data here manually, instead!
		
		if(!stack.hasTagCompound()) return;
		NBTTagCompound stackTag = stack.getTagCompound();
		assert stackTag != null;
		NBTTagCompound spawnerDataNBT = stackTag.getCompoundTag(QSilkSpawners.TAG_SPAWNER_DATA);
		if(spawnerDataNBT.isEmpty()) return; //missing tag
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMobSpawner) {
			((TileEntityMobSpawner)tile).getSpawnerBaseLogic().readFromNBT(spawnerDataNBT);
		}
	}
	
	//Weird hack for tooltip stuff
	//Kinda spaghetti but ok
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag mistake) {
		//TODO write the entity name on the tool tip
		super.addInformation(stack, world, tooltip, mistake);
		
		if(!stack.hasTagCompound()) return;
		NBTTagCompound stackTag = stack.getTagCompound();
		assert stackTag != null;
		NBTTagCompound spawnerDataNBT = stackTag.getCompoundTag(QSilkSpawners.TAG_SPAWNER_DATA);
		if(spawnerDataNBT.isEmpty()) return; //missing tag
		DummyMobSpawnerBaseLogic.SHARED_INST.readFromNBT(spawnerDataNBT);
		Entity ent = DummyMobSpawnerBaseLogic.SHARED_INST.getCachedEntity();
		
		tooltip.add(I18n.translateToLocalFormatted("qsilkspawners.tooltip.entity", ent.getName()));
	}
}
