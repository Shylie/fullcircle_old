package com.github.shylie.fullcircle;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class RedGlazedTerracottaSignTileEntity extends TileEntity {
	private static final String TEXT_TAG = "STORED_TEXT";

	public String text = "";

	public RedGlazedTerracottaSignTileEntity() {
		super(FCTileEntityTypes.RED_GLAZED_TERRACOTTA_SIGN.get());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		final CompoundNBT tag = new CompoundNBT();
		tag.putString(TEXT_TAG, text);
		return new SUpdateTileEntityPacket(this.pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		text = pkt.getNbtCompound().getString(TEXT_TAG);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		text = nbt.getString(TEXT_TAG);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putString(TEXT_TAG, text);
		return compound;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
}
