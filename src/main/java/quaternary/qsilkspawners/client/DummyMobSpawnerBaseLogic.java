package quaternary.qsilkspawners.client;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DummyMobSpawnerBaseLogic extends MobSpawnerBaseLogic {
	@Override
	public void broadcastEvent(int id) {
		//No-op
	}
	
	@Override
	public World getSpawnerWorld() {
		return Minecraft.getMinecraft().world;
	}
	
	@Override
	public BlockPos getSpawnerPosition() {
		return new BlockPos(0, 0, 0);
	}
	
	public static final DummyMobSpawnerBaseLogic SHARED_INST = new DummyMobSpawnerBaseLogic();
}
