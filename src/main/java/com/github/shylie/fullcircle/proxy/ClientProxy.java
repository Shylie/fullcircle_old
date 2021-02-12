package com.github.shylie.fullcircle.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.shylie.fullcircle.FullCircle;
import com.github.shylie.fullcircle.net.MessageLinkSpellLog;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

public class ClientProxy extends CommonProxy {
    private File FC_SPELL_DEBUG_FILE;

    private int received = 0;
    private Map<Integer, String> strings = new HashMap<>();
    private int vmHash = -1;

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

    @Override
    public synchronized void writeToFCDebugFile(String string, int index, int total, int vmHash) {
        if (vmHash != this.vmHash) {
            this.vmHash = vmHash;
            received = 0;
            strings.clear();

            try (FileWriter fw = new FileWriter(getFCDebugFile(), false)) {

            }
            catch (IOException ioException) {

            }
            catch (NullPointerException npe) {

            }
        }

        strings.put(index, string);
        if (++received == total) {
            try (FileWriter fw = new FileWriter(getFCDebugFile(), true)) {
                for (int i = 0; i < total; i++) {
                    fw.write(strings.get(i));
                }
                MessageLinkSpellLog.linkSpellLog();
            }
            catch (IOException ioException) {

            }
            catch (NullPointerException npe) {

            }
        }
    }
}
