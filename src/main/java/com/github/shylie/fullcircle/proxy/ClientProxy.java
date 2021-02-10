package com.github.shylie.fullcircle.proxy;

import java.io.File;
import java.io.IOException;

import com.github.shylie.fullcircle.FullCircle;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class ClientProxy extends CommonProxy {
    private File FC_SPELL_DEBUG_FILE;

    public ClientProxy() {
        try {
            File fcdir = new File(Minecraft.getInstance().gameDir, "fullcircle");
            fcdir.mkdir();

            FC_SPELL_DEBUG_FILE = new File(Minecraft.getInstance().gameDir + "/fullcircle/" + FullCircle.FC_SPELL_DEBUG_FILENAME);
            FC_SPELL_DEBUG_FILE.createNewFile();
        }
        catch (IOException ioException) {
        }
        catch (NullPointerException npe) {
            // no minecraft instance.
        }
    }

    @Override
    public void addToChat(ITextComponent textComponent) {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(textComponent);
    }

    @Override
    public File getFCDebugFile() {
        return FC_SPELL_DEBUG_FILE;
    }
}
