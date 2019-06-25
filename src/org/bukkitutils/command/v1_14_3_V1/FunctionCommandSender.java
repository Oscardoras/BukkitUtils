package org.bukkitutils.command.v1_14_3_V1;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.chat.BaseComponent;

public class FunctionCommandSender implements CommandSender {
	
	private static FunctionCommandSender functionCommandSender;
	
	static {
		functionCommandSender = new FunctionCommandSender();
	}
	
	
	private FunctionCommandSender() {}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return Bukkit.getConsoleSender().addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return Bukkit.getConsoleSender().addAttachment(plugin, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return Bukkit.getConsoleSender().addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return Bukkit.getConsoleSender().addAttachment(plugin, name, value, ticks);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return Bukkit.getConsoleSender().getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String name) {
		return Bukkit.getConsoleSender().hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return Bukkit.getConsoleSender().hasPermission(perm);
	}

	@Override
	public boolean isPermissionSet(String name) {
		return Bukkit.getConsoleSender().isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return Bukkit.getConsoleSender().isPermissionSet(perm);
	}

	@Override
	public void recalculatePermissions() {
		Bukkit.getConsoleSender().recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		Bukkit.getConsoleSender().removeAttachment(attachment);
	}

	@Override
	public boolean isOp() {
		return Bukkit.getConsoleSender().isOp();
	}

	@Override
	public void setOp(boolean value) {
		Bukkit.getConsoleSender().setOp(value);
	}

	@Override
	public String getName() {
		return Bukkit.getConsoleSender().getName();
	}

	@Override
	public Server getServer() {
		return Bukkit.getConsoleSender().getServer();
	}

	@Override
	public void sendMessage(String message) {}

	@Override
	public void sendMessage(String[] messages) {}

	@Override
	public Spigot spigot() {
		return new Spigot() {
			
			@Override
			public void sendMessage(BaseComponent component) {}

			@Override
			public void sendMessage(BaseComponent... components) {}
			
		};
	}
	
	
	public static FunctionCommandSender getFunctionCommandSender() {
		return functionCommandSender;
	}
	
}