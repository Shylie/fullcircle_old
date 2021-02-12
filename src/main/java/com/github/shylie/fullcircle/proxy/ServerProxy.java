package com.github.shylie.fullcircle.proxy;

import java.io.File;

import net.minecraft.util.text.ITextComponent;

public class ServerProxy extends CommonProxy {
    @Override
    public void addToChat(ITextComponent textComponent) {
    }

    @Override
    public File getFCDebugFile() {
        return null;
    }

    @Override
    public void writeToFCDebugFile(String string, int index, int total, int vmHash) {
    }
}
