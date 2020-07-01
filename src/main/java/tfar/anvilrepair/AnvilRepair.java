package tfar.anvilrepair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnvilRepair implements ModInitializer {
	@Override
	public void onInitialize() {
		DispenserBlock.registerBehavior(Items.IRON_BLOCK, (pointer, stack) -> {
			World world = pointer.getWorld();
			if (!world.isClient()) {
				BlockState dispenserState = pointer.getBlockState();
				BlockPos pos = pointer.getBlockPos().offset(dispenserState.get(DispenserBlock.FACING));
				BlockState anvilState = world.getBlockState(pos);
				Block block = anvilState.getBlock();
				boolean repair = false;
				if (block == Blocks.CHIPPED_ANVIL) {
					repair = true;
					world.setBlockState(pos, Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING,
									anvilState.get(AnvilBlock.FACING)));
				} else if (block == Blocks.DAMAGED_ANVIL) {
					repair = true;
					world.setBlockState(pos, Blocks.CHIPPED_ANVIL.getDefaultState().with(AnvilBlock.FACING,
									anvilState.get(AnvilBlock.FACING)));
				}
				if (repair) {
					stack.decrement(1);
				}
			}
			return stack;
		});

		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
			if (!world.isClient) {
				ItemStack stack = playerEntity.getStackInHand(hand);
				BlockState state = world.getBlockState(blockHitResult.getBlockPos());
				Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
				if (stack.getItem() == Items.IRON_BLOCK) {
					boolean repair = false;
					if (block == Blocks.CHIPPED_ANVIL) {
						repair = true;
						world.setBlockState(blockHitResult.getBlockPos(), Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING,
										state.get(AnvilBlock.FACING)));
					} else if (block == Blocks.DAMAGED_ANVIL) {
						repair = true;
						world.setBlockState(blockHitResult.getBlockPos(), Blocks.CHIPPED_ANVIL.getDefaultState().with(AnvilBlock.FACING,
										state.get(AnvilBlock.FACING)));
					}
					if (repair) {
						if (!playerEntity.abilities.creativeMode)
							stack.decrement(1);
						return ActionResult.SUCCESS;
					}
				}
			}
			return ActionResult.PASS;
		});
	}
}
