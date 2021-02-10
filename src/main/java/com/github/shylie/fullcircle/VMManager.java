package com.github.shylie.fullcircle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

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

    public int size() {
        return vms.size();
    }

    public boolean isEmpty() {
        return vms.isEmpty();
    }

    public boolean contains(Object o) {
        return vms.contains(o);
    }

    public Iterator<VM> iterator() {
        return vms.iterator();
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

    public boolean remove(Object o) {
        if (vms.remove(o)) {
            notifyClient((VM)o);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean containsAll(Collection<?> c) {
        return vms.containsAll(c);
    }

    public boolean addAll(Collection<? extends VM> c) {
        return vms.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends VM> c) {
        return vms.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return vms.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return vms.retainAll(c);
    }

    public void clear() {
        vms.clear();
    }

    public VM get(int index) {
        return vms.get(index);
    }

    public VM set(int index, VM element) {
        VM ret = vms.set(index, element);
        notifyClient(ret);
        return ret;
    }

    public void add(int index, VM element) {
        vms.add(index, element);
    }

    public VM remove(int index) {
        VM ret = vms.remove(index);
        notifyClient(ret);
        return ret;
    }

    public int indexOf(Object o) {
        return vms.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return vms.lastIndexOf(o);
    }

    public ListIterator<VM> listIterator() {
        return vms.listIterator();
    }

    public ListIterator<VM> listIterator(int index) {
        return vms.listIterator(index);
    }

    public List<VM> subList(int fromIndex, int toIndex) {
        return vms.subList(fromIndex, toIndex);
    }

    private void notifyClient(VM vm) {
        if (requested.contains(vm.getCaster().getUniqueID())) {
            FCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)vm.getCaster()), new MessageWriteContent(vm.getLog()));
            requested.remove(vm.getCaster().getUniqueID());
        }
    }
}
