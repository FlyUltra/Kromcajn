package com.example.modules;

import com.example.gui.Category;
import com.example.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT);
    }


    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.level == null || mc.getConnection() == null) return;

        BlockPos posBelow = mc.player.blockPosition().below();

        if (mc.level.getBlockState(posBelow).isAir() || mc.level.getBlockState(posBelow).liquid()) {

            int blockSlot = findBlockInHotbar();
            if (blockSlot == -1) return;

            int originalSlot = mc.player.getInventory().getSelectedSlot();

            mc.getConnection().send(new ServerboundSetCarriedItemPacket(blockSlot));

            mc.player.setXRot(80.0f);

            Vec3 hitVec = Vec3.atCenterOf(posBelow);
            BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, posBelow, false);

            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult);

            mc.player.swing(InteractionHand.MAIN_HAND);

            mc.getConnection().send(new ServerboundSetCarriedItemPacket(originalSlot));
        }
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }
}