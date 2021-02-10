package com.github.shylie.fullcircle.proxy;

import java.io.File;

import net.minecraft.util.text.ITextComponent;

public abstract class CommonProxy {
    public static CommonProxy PROXY = null;

    public abstract void addToChat(ITextComponent textComponent);
    public abstract File getFCDebugFile();
}
