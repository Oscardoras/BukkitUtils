package org.bukkitutils.command.v1_15_V1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkitutils.command.v1_15_V1.Argument.SuggestedCommand;
import org.bukkitutils.command.v1_15_V1.Argument.SuggestionsProvider;
import org.bukkitutils.command.v1_15_V1.CommandRegister.CommandExecutorType;
import org.bukkitutils.command.v1_15_V1.CommandRegister.CommandRunnable;
import org.bukkitutils.command.v1_15_V1.CommandRegister.PerformedCommand;
import org.bukkitutils.command.v1_15_V1.arguments.GreedyStringArgument;
import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sun.istack.internal.NotNull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Contains methods for command reflection. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Reflector {
	private Reflector() {}
	
	
	private static String obcPackageName;
	private static String nmsPackageName;
	
	private static CommandDispatcher dispatcher;
	
	static {
		try {
			if(Package.getPackage("com.mojang.brigadier") == null)
				throw new ClassNotFoundException("Brigadier not found, plugin commands are not compatible with this version");
			
			Object server = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
			nmsPackageName = server.getClass().getPackage().getName();
			obcPackageName = Bukkit.getServer().getClass().getPackage().getName();
			
			dispatcher = (CommandDispatcher) getNmsClass("CommandDispatcher").getDeclaredMethod("a").invoke(getField(getNmsClass("MinecraftServer"), "commandDispatcher").get(server)); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Registration                                                                            //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected static void unregister(String name) {
		try {
			Field children = getField(CommandNode.class, "children");
			Map<String, CommandNode<?>> c = (Map<String, CommandNode<?>>) children.get(dispatcher.getRoot());
			c.remove(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void register(String name, LinkedHashMap<String, Argument<?>> argumentsOriginal, Permission permission, CommandExecutorType executorType, CommandRunnable runnable, String[] aliases) {
		try {
			
			final LinkedHashMap<String, Argument<?>> arguments = (LinkedHashMap<String, Argument<?>>) argumentsOriginal.clone();
			long numGreedyArgs = arguments.values().stream().filter(arg -> arg instanceof GreedyStringArgument).count();
			if(numGreedyArgs >= 1) {
				if(!(arguments.values().toArray(new Argument[arguments.size()])[arguments.size() - 1] instanceof GreedyStringArgument)) {
					throw new IllegalArgumentException("GreedyStringArgument must be declared at the end of a LinkedHashMap");
				}
				if(numGreedyArgs > 1) {
					throw new IllegalArgumentException("GreedyStringArgument must be declared at the end of a LinkedHashMap");
				}
			}
			
			Command command = (context) -> {
				
				Object source = context.getSource();
				
				CommandSender sender = getCommandSender(source);
				CommandSender executor = getCommandExecutor(source);
				if (executor == null) executor = sender;
				
				if (executorType == CommandExecutorType.PLAYER) {
					if (!(executor instanceof Player)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.player");
						sendFailureMessage(source, sender, new BaseComponent[] {message});
						return 0;
					}
				} else if (executorType == CommandExecutorType.ENTITY) {
					if (!(executor instanceof Entity)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.entity");
						sendFailureMessage(source, sender, new BaseComponent[] {message});
						return 0;
					}
				}
				
				Location location = getCommandLocation(source);
							
				//Array for arguments for executor
				List<Object> args = new ArrayList<>();
				for(Entry<String, Argument<?>> entry : arguments.entrySet()) {
					try {
						populateArgs(entry, context, sender, executor, location, args);
					} catch (CommandSyntaxException e) {
						throw e;
					} catch (InvocationTargetException e) {
						Throwable e2 = e.getCause();
						while (e2 instanceof InvocationTargetException) e2 = e2.getCause();
						if (e2 instanceof CommandSyntaxException) throw (CommandSyntaxException) e2;
						else {
							e2.printStackTrace();
							args.add(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
						args.add(null);
					}
				}
				
				//Run
				try {
					return runnable.run(new PerformedCommand(source, sender, executor, location, args.toArray(new Object[args.size()])));
				} catch (CommandSyntaxException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			};
			
			LiteralCommandNode resultantNode;
			if(arguments.isEmpty()) {
				//Link command name to the executor
		        resultantNode = dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(name, permission).executes(command));
		        
		        //Register aliases
		        for(String str : aliases) {
		        	dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(str, permission).redirect(resultantNode));
		        }
			} else {
				
				//List of keys for reverse iteration
		        ArrayList<String> keys = new ArrayList<>(arguments.keySet());
	
		        //Link the last element to the executor
		        ArgumentBuilder inner;
		        //New scope used here to prevent innerArg accidentally being used below
		        {
			        Argument innerArg = arguments.get(keys.get(keys.size() - 1));
			        if(innerArg instanceof LiteralArgument) {
			        	String str = ((LiteralArgument) innerArg).getLiteral();
			        	inner = getLiteralArgumentBuilder(str, innerArg.getPermission()).executes(command);
			        } else {
	        			inner = getRequiredArgumentBuilder(keys.get(keys.size() - 1), innerArg, innerArg.getPermission(), executorType, arguments).executes(command);
					}
		        }
	
		        //Link everything else up, except the first
		        ArgumentBuilder outer = inner;
		        for(int i = keys.size() - 2; i >= 0; i--) {
		        	Argument outerArg = arguments.get(keys.get(i));
		        	if(outerArg instanceof LiteralArgument) {
		        		String str = ((LiteralArgument) outerArg).getLiteral();
		        		outer = getLiteralArgumentBuilder(str, outerArg.getPermission()).then(outer);
		        	} else {
	        			outer = getRequiredArgumentBuilder(keys.get(i), outerArg, outerArg.getPermission(), executorType, arguments).then(outer);
	        		}
		        }        
		        
		        //Link command name to first argument and register        
		        resultantNode = dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(name, permission).then(outer));
			}
			
			//Register aliases
			for(String str : aliases) dispatcher.register((LiteralArgumentBuilder) getLiteralArgumentBuilder(str, permission).redirect(resultantNode));
			
			//Permissions
			try {
				Class<?> craftServer = getObcClass("CraftServer");
				getMethod(craftServer, "setVanillaCommands", boolean.class).invoke(Bukkit.getServer(), false);
				SimpleCommandMap map = (SimpleCommandMap) getMethod(craftServer, "getCommandMap").invoke(Bukkit.getServer());
				map.getCommand(name).setPermission(permission.getName());
				org.bukkit.command.Command cmd = map.getCommand("minecraft:" + name);
				if (cmd != null) cmd.setPermission(permission.getName());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Argument Builders                                                                       //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//Populates arguments list
	private static void populateArgs(Entry<String, Argument<?>> entry, CommandContext context, final CommandSender sender, CommandSender executor, Location location, List<Object> args) throws Exception {
		Argument argument = entry.getValue();
		if (!(argument instanceof LiteralArgument)) {
			Object arg;
			
			String key = entry.getKey();
			Object a = argument.parse(key, context);
			
			if (argument instanceof CustomArgument)
				arg = ((CustomArgument) argument).parse((String) a, new SuggestedCommand(context.getSource(), sender, executor, location, args.toArray(new Object[args.size()])));
			else arg = a;
			
			args.add(arg);
		}
	}
	
	//Gets a LiteralArgumentBuilder for a command name
	private static LiteralArgumentBuilder<?> getLiteralArgumentBuilder(String commandName, Permission permission) {
		return LiteralArgumentBuilder.literal(commandName).requires(source -> {
			return permission == null || getCommandSender(source).hasPermission(permission);
		});
	}
	
	//Gets a RequiredArgumentBuilder for a DynamicSuggestedStringArgument
	private static <T> RequiredArgumentBuilder<?, ?> getRequiredArgumentBuilder(String argumentName, Argument<?> type, Permission permission, CommandExecutorType executorType, final LinkedHashMap<String, Argument<?>> arguments) {
		SuggestionProvider provider = null;
		
		SuggestionsProvider suggestionsProvider = type.getSugesstionsProvider();
		if (suggestionsProvider != null)
			provider = (context, builder) -> {
				Object source = context.getSource();
				
				CommandSender sender = getCommandSender(source);
				CommandSender executor = getCommandExecutor(source);
				if (executor == null) executor = sender;
				
				if (executorType == CommandExecutorType.PLAYER) {
					if (!(executor instanceof Player)) return builder.buildFuture();
				} else if (executorType == CommandExecutorType.ENTITY) {
					if (!(executor instanceof Entity)) return builder.buildFuture();
				}
				
				Location location = getCommandLocation(source);
				
				//Array for arguments for executor
				List<Object> args = new ArrayList<>();
				for(Entry<String, Argument<?>> entry : arguments.entrySet()) {
					try {
						populateArgs(entry, context, sender, executor, location, args);
					} catch (Exception e) {
						args.add(null);
					}
				}
				
				Collection<String> list;
				try {
					list =  suggestionsProvider.run(new SuggestedCommand(source, sender, executor, location, args.toArray(new Object[args.size()])));
				} catch (CommandSyntaxException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					list = null;
				}
				if (list != null) {
					String remaining = builder.getRemaining().toLowerCase();
					for (String suggestion : list) if (suggestion.toLowerCase().startsWith(remaining)) builder.suggest(suggestion);
				}
				return builder.buildFuture();
			};
		
		RequiredArgumentBuilder<?, ?> a = ((RequiredArgumentBuilder<?, ?>) RequiredArgumentBuilder.argument(argumentName, type.getRawType()).requires(source -> {
			return permission == null || getCommandSender(source).hasPermission(permission);
		}));
		if (provider != null) return a.suggests(provider);
		else return a;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: CommandSender                                                                           //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Gets a command sender object.
	 * @param source the source object
	 * @return the command sender object for the source object
	 */
	public static @NotNull CommandSender getCommandSender(@NotNull Object source) {
		try {
			return (CommandSender) getMethod(source.getClass(), "getBukkitSender").invoke(source);
		} catch (InvocationTargetException e) {
			return Bukkit.getConsoleSender();
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** Gets a command executor object.
	 * @param source the source object
	 * @return the command executor object for the source object
	 */
	public static @NotNull CommandSender getCommandExecutor(@NotNull Object source) {
		try {
			Object proxyEntity = getField(source.getClass(), "k").get(source);
			if(proxyEntity != null)
				return (CommandSender) getMethod(getNmsClass("Entity"), "getBukkitEntity").invoke(getNmsClass("Entity").cast(proxyEntity));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Gets a location object.
	 * @param source the source object
	 * @return the location object for the source object
	 */
	public static @NotNull Location getCommandLocation(@NotNull Object source) {
		try {
			Object position = getMethod(source.getClass(), "getPosition").invoke(source);
			double x = getField(position.getClass(), "x").getDouble(position);
			double y = getField(position.getClass(), "y").getDouble(position);
			double z = getField(position.getClass(), "z").getDouble(position);
			
			Object worldServer = getMethod(source.getClass(), "getWorld").invoke(source);
			Object worldData = getField(getNmsClass("World"), "worldData").get(worldServer);
			World world = Bukkit.getWorld((String) getMethod(getNmsClass("WorldData"), "getName").invoke(worldData));
			
			return new Location(world, x, y, z);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Message                                                                                 //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Sends a command message.
	 * @param source the source object to send the message
	 * @param sender the command sender to send the message
	 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
	 * @param list an integer to replace %list% in translatable messages. Set -1 to leave default
	 */
	public static void sendMessage(@NotNull Object source, @NotNull CommandSender sender, @NotNull Object[] message, int list) {
		try {
			Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
			Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
			Method a = getMethod(ChatSerializer, "a", String.class);
			Object object = a.invoke(null, ComponentSerializer.toString(getBaseComponents(TranslatableMessage.getLanguage(sender), message, null, list)));
			getMethod(source.getClass(), "sendMessage", IChatBaseComponent, boolean.class).invoke(source, object, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Broadcasts a command message.
	 * @param source the source object to send the message
	 * @param sender the command sender to send the message
	 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
	 */
	public static void broadcastMessage(@NotNull Object source, @NotNull CommandSender sender, @NotNull Object[] message) {
		boolean found = false;
		for (Object msg : message) if (msg instanceof TranslatableMessage) {
			found = true;
			break;
		}
		
		if (!found) {
			try {
				Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
				Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
				Method a = getMethod(ChatSerializer, "a", String.class);
				Object object = a.invoke(null, ComponentSerializer.toString((BaseComponent[]) message));
				getMethod(source.getClass(), "sendMessage", IChatBaseComponent, boolean.class).invoke(source, object, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sendMessage(source, sender, message, -1);
			
			try {
				Object base = getField(source.getClass(), "base").get(source);
				Method shouldBroadcastCommands = getMethod(getNmsClass("ICommandListener"), "shouldBroadcastCommands");
				Field j = getField(source.getClass(), "j");
				j.setAccessible(true);
				
				if ((boolean) shouldBroadcastCommands.invoke(base) && !j.getBoolean(source)) {
					
					String name = sender instanceof ConsoleCommandSender ? "Server" : sender.getName();
					
					if (Bukkit.getWorlds().get(0).getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
						for (Player player : Bukkit.getOnlinePlayers())
							if (!player.equals(sender) && player.hasPermission("minecraft.admin.command_feedback")) {
								TranslatableComponent component = new TranslatableComponent("chat.type.admin");
								component.setColor(ChatColor.GRAY);
								component.setItalic(true);
								component.addWith(name);
								BaseComponent b = new TextComponent("");
								b.setExtra(Arrays.asList(getBaseComponents(TranslatableMessage.getLanguage(player), message, ChatColor.GRAY, -1)));
								component.addWith(b);
								
								player.spigot().sendMessage(component);
							}
					}
					
					if (!(sender instanceof ConsoleCommandSender) && Bukkit.getWorlds().get(0).getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) {
						if (!Class.forName("org.spigotmc.SpigotConfig").getField("silentCommandBlocks").getBoolean(null)) {
							TranslatableComponent component = new TranslatableComponent("chat.type.admin");
							component.setColor(ChatColor.GRAY);
							component.setItalic(true);
							component.addWith(name);
							BaseComponent b = new TextComponent("");
							b.setExtra(Arrays.asList(getBaseComponents(TranslatableMessage.getLanguage(Bukkit.getConsoleSender()), message, ChatColor.GRAY, -1)));
							component.addWith(b);
							
							Field i = getField(source.getClass(), "i");
							i.setAccessible(true);
							
							Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
							Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
							Method a = getMethod(ChatSerializer, "a", String.class);
							Object object = a.invoke(null, ComponentSerializer.toString(component));
							getMethod(getNmsClass("ICommandListener"), "sendMessage", IChatBaseComponent).invoke(i.get(source), object);
						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Sends a failure message.
	 * @param source the source object to send the message
	 * @param sender the command sender to send the message
	 * @param message an array, which can contain BaseComponent, TranslatableMessage and any other type of object
	 */
	public static void sendFailureMessage(@NotNull Object source, @NotNull CommandSender sender, @NotNull Object[] message) {
		try {
			Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
			Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
			Method a = getMethod(ChatSerializer, "a", String.class);
			Object object = a.invoke(null, ComponentSerializer.toString(getBaseComponents(TranslatableMessage.getLanguage(sender), message, ChatColor.RED, -1)));
			getMethod(source.getClass(), "sendFailureMessage", IChatBaseComponent).invoke(source, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static BaseComponent[] getBaseComponents(String language, Object[] message, ChatColor forceColor, int list) {
		List<BaseComponent> components = new ArrayList<BaseComponent>();
		for (Object msg : message) {
			if (msg instanceof Object[]) components.addAll(Arrays.asList(getBaseComponents(language, (Object[]) msg, forceColor, list)));
			else {
				BaseComponent component;
				if (msg instanceof BaseComponent) component = (BaseComponent) msg;
				else if (msg instanceof TranslatableMessage) {
					String s = ((TranslatableMessage) msg).getMessage(language);
					if (list >= 0) s = s.replaceAll("%list%", ""+list);
					component = new TextComponent(s);
				} else component = new TextComponent(msg.toString());
				if (forceColor != null) component.setColor(forceColor);
				components.add(component);
			}
		}
		return components.toArray(new BaseComponent[components.size()]);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECTION: Reflection                                                                              //
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Gets a constructor.
	 * @param clazz the class where is the constructor
	 * @param parameterTypes the parameter array
	 * @return the Constructor object for the specified constructor in this class
	 * @throws NoSuchMethodException if a constructor with the specified name is not found
	 * @throws SecurityException
	 */
	public static @NotNull Constructor getConstructor(@NotNull Class<?> clazz, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		Constructor result = clazz.getDeclaredConstructor(parameterTypes);
		result.setAccessible(true);
		return result;
	}
	
	/** Gets a method.
	 * @param clazz the class where is the method
	 * @param name the name of the method
	 * @param parameterTypes the parameter array
	 * @return the Method object for the specified method in this class
	 * @throws NoSuchMethodException if a method with the specified name is not found
	 * @throws SecurityException
	 */
	public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String name, @NotNull Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		Method result = clazz.getDeclaredMethod(name, parameterTypes);
		result.setAccessible(true);
		return result;
	}
	
	/** Gets a field.
	 * @param clazz the class where is the field
	 * @param name the name of the field
	 * @return the Field object for the specified field in this class
	 * @throws NoSuchFieldException if a field with the specified name is not found
	 * @throws SecurityException
	 */
	public static @NotNull Field getField(@NotNull Class<?> clazz, @NotNull String name) throws NoSuchFieldException, SecurityException {
		Field result = clazz.getDeclaredField(name);
		result.setAccessible(true);
		return result;
	}
	
	/** Gets a class in the package net.minecraft.server.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static @NotNull Class<?> getNmsClass(@NotNull final String className) throws ClassNotFoundException {
		return Class.forName(nmsPackageName + "." + className);
	}
	
	/** Gets a class in the package org.bukkit.craftbukkit.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static @NotNull Class<?> getObcClass(@NotNull final String className) throws ClassNotFoundException {
		return Class.forName(obcPackageName + "." + className);
	}

	/**
	* Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @return the ArgumentType<?>
	 * @see getNMSArgumentInstance
	 */
	public static @NotNull ArgumentType<?> getNmsArgumentInstance(@NotNull String nmsClassName) {
		return getNmsArgumentInstance(nmsClassName, "a");		
	}
	
	/**
	 * Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @param methodName the name of the method
	 * @return the ArgumentType<?>
	 */
	public static @NotNull ArgumentType<?> getNmsArgumentInstance(@NotNull String nmsClassName, @NotNull String methodName) {
		try {
			Class clazz = getNmsClass(nmsClassName);
			Method method;
			try {
				method = getMethod(clazz, methodName);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				try {
					method = getMethod(clazz, "a");
				} catch (NoSuchMethodException | SecurityException e1) {
					return null;
				}
			}
			
			return (ArgumentType<?>) method.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}