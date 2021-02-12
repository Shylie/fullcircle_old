package com.github.shylie.fullcircle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import com.github.shylie.fullcircle.lang.VM;
import com.github.shylie.fullcircle.net.MessageWriteContent;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class VMManager {
    public static VMManager MANAGER = new VMManager();

    private List<VM> vms = new ArrayList<>();
    private Set<UUID> requested = new HashSet<>();

    private VMManager() {
    }

    public void request(UUID uuid) {
        requested.add(uuid);
    }

    public void removeRequest(UUID uuid) {
        requested.remove(uuid);
    }

    public int size() {
        return vms.size();
    }

    public boolean isEmpty() {
        return vms.isEmpty();
    }

    public boolean contains(Object o) {
        return vms.contains(o);
    }

    public Object[] toArray() {
        return vms.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return vms.toArray(a);
    }

    public boolean add(VM e) {
        return vms.add(e);
    }

    public boolean removeIf(Predicate<VM> filter) {
        boolean removed = false;
        for (int i = vms.size() - 1; i >= 0; i--) {
            if (filter.test(vms.get(i))) {
                remove(i);
                removed = true;
            }
        }
        return removed;
    }

    public void clear() {
        vms.clear();
    }

    public VM get(int index) {
        return vms.get(index);
    }

    public void add(int index, VM element) {
        vms.add(index, element);
    }

    public VM remove(int index) {
        VM ret = vms.remove(index);
        notifyClient(ret);
        return ret;
    }

    public boolean remove(Object o) {
        if (vms.remove(o)) {
            notifyClient((VM)o);
            return true;
        }
        else {
            return false;
        }
    }

    private void notifyClient(VM vm) {
        if (requested.contains(vm.getCaster().getUniqueID())) {
            requested.remove(vm.getCaster().getUniqueID());
            List<String> strings = vm.getLog();
            for (int i = 0; i < strings.size(); i++) {
                FCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)vm.getCaster()), new MessageWriteContent(strings.get(i), i, strings.size(), vm.hashCode()));
            }
        }
    }
}
