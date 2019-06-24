package org.bukkitutils.command.v1_14_2_V1.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkitutils.command.v1_14_2_V1.Reflector;

import com.mojang.brigadier.context.CommandContext;

/** Represents an entity type argument for a Mojang Brigadier command */
public class EntityTypeArgument extends Argument<EntityType> {
	
	/** Represents an entity type argument for a Mojang Brigadier command */
	public EntityTypeArgument() {
		super(Reflector.getNmsArgumentInstance("ArgumentEntitySummon"));
	}
	
	@Override
	public EntityType getArg(String key, CommandContext<?> context, CommandSender executor, Location location) throws Exception {
		Object minecraftKey = Reflector.getMethod(Reflector.getNmsClass("ArgumentEntitySummon"), "a", CommandContext.class, String.class).invoke(null, context, key);
		Object craftWorld = Reflector.getObcClass("CraftWorld").cast(location.getWorld());
		Object handle = Reflector.getMethod(craftWorld.getClass(), "getHandle").invoke(craftWorld);
		Object minecraftWorld = Reflector.getNmsClass("World").cast(handle);
		
		Object entity = Reflector.getMethod(Reflector.getNmsClass("EntityTypes"), "a", Reflector.getNmsClass("World"), Reflector.getNmsClass("MinecraftKey")).invoke(null, minecraftWorld, minecraftKey);
		Object entityCasted = Reflector.getNmsClass("Entity").cast(entity);
		Object bukkitEntity = Reflector.getMethod(Reflector.getNmsClass("Entity"), "getBukkitEntity").invoke(entityCasted);
		return (EntityType) bukkitEntity.getClass().getDeclaredMethod("getType").invoke(bukkitEntity);
	}
	
}