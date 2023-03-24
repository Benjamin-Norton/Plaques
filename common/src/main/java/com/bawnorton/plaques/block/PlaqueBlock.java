package com.bawnorton.plaques.block;

import com.bawnorton.plaques.block.entity.PlaqueBlockEntity;
import com.bawnorton.plaques.util.PlaqueType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SignType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class PlaqueBlock extends WallSignBlock {
    private static final List<Item> ACCENTS = List.of(
            Items.IRON_INGOT,
            Items.GOLD_INGOT,
            Items.NETHERITE_INGOT,
            Items.COPPER_INGOT,
            Items.DIAMOND,
            Items.EMERALD,
            Items.LAPIS_LAZULI,
            Items.REDSTONE,
            Items.QUARTZ
    );

    private final PlaqueType plaqueType;

    public PlaqueBlock(Settings settings, PlaqueType plaqueType) {
        super(settings, SignType.OAK);
        this.plaqueType = plaqueType;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        boolean isGlowstone = item == Items.GLOWSTONE_DUST;
        boolean isCoal = item == Items.COAL;
        boolean isAccent = ACCENTS.contains(item);
        boolean canInteract = (isGlowstone || isCoal || isAccent) && player.getAbilities().allowModifyWorld;
        if(world.isClient) {
            return canInteract ? ActionResult.SUCCESS : ActionResult.CONSUME;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof PlaqueBlockEntity plaqueBlockEntity)) {
            return ActionResult.PASS;
        }

        boolean isGlowing = plaqueBlockEntity.isGlowingText();
        if(isGlowing && isGlowstone || !isGlowing && isCoal) {
            return ActionResult.PASS;
        }

        if(canInteract) {
            boolean usedItem;
            if(isGlowstone) {
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                usedItem = plaqueBlockEntity.setGlowingText(true);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
                }
            } else if(isCoal) {
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                usedItem = plaqueBlockEntity.setGlowingText(false);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                usedItem = plaqueBlockEntity.setAccent(item);
            }

            if(usedItem) {
                if(!player.isCreative()) {
                    itemStack.decrement(1);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(item));
            }
        }

        return plaqueBlockEntity.onActivate((ServerPlayerEntity) player) ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    public PlaqueType getPlaqueType() {
        return plaqueType;
    }

    @Override
    public String getTranslationKey() {
        return "block.plaques." + plaqueType.getName() + "_plaque";
    }
}