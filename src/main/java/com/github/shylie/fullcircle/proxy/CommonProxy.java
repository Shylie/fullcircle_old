package com.github.shylie.fullcircle.proxy;

import java.io.File;

import net.minecraft.util.text.ITextComponent;

public abstract class CommonProxy {
	public static CommonProxy PROXY = null;

	public static final int STRING_CHUNK_LENGTH = 16384;

	public abstract void addToChat(ITextComponent textComponent);
	public abstract File getFCDebugFile();
	public abstract void writeToFCDebugFile(String string, int index, int total, int vmHash);
}
