package org.bukkitutils.command.v1_14_3_V1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.bukkitutils.command.v1_14_3_V1.Argument.SuggestedCommand;
import org.bukkitutils.command.v1_14_3_V1.Argument.SuggestionsProvider;
import org.bukkitutils.command.v1_14_3_V1.CommandRegister.CommandExecutorType;
import org.bukkitutils.command.v1_14_3_V1.CommandRegister.CommandRunnable;
import org.bukkitutils.command.v1_14_3_V1.CommandRegister.PerformedCommand;
import org.bukkitutils.command.v1_14_3_V1.arguments.GreedyStringArgument;
import org.bukkitutils.io.TranslatableMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/** Contains methods for arguments reflection */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class Reflector {
	private Reflector() {}
	
	
	@SuppressWarnings("unused")
	private static class ClassCache {
		
		private Class<?> clazz;
		private String name;
		
		public ClassCache(Class<?> clazz, String name) {
			this.clazz = clazz;
			this.name = name;
		}
		
		public Class<?> getClazz() {
			return clazz;
		}
		
		public String getName() {
			return name;
		}
		
	}

	//Cache maps
	private static Map<String, Class<?>> nmsClasses;
	private static Map<String, Class<?>> obcClasses;
	private static Map<ClassCache, Constructor> constructors;
	private static Map<ClassCache, Method> methods;
	private static Map<ClassCache, Field> fields;
	
	private static String obcPackageName = null;
	private static String nmsPackageName = null;
	
	private static CommandDispatcher dispatcher;
	
	static {
		try {
			if(Package.getPackage("com.mojang.brigadier") == null) {
				throw new ClassNotFoundException("Brigadier not found, plugin commands are not compatible with this version");
			}
			
			//Setup NMS
			Object server = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
			nmsPackageName = server.getClass().getPackage().getName();
			obcPackageName = Bukkit.getServer().getClass().getPackage().getName();
			
			//Everything from this line will use getNMSClass(), so we initialize our cache here
			nmsClasses = new HashMap<>();
			obcClasses = new HashMap<>();
			constructors = new HashMap<>();
			methods = new HashMap<>();
			fields = new HashMap<>();
			
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
						message.setColor(ChatColor.RED);
						sendFailureMessage(source, message);
						return 0;
					}
				} else if (executorType == CommandExecutorType.ENTITY) {
					if (!(executor instanceof Entity)) {
						TranslatableComponent message = new TranslatableComponent("permissions.requires.entity");
						message.setColor(ChatColor.RED);
						sendFailureMessage(source, message);
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
			
			if (argument instanceof CustomArgument) {
				
				CustomArgument customArgument = (CustomArgument) argument;
				arg = customArgument.parse((String) a, new SuggestedCommand(context.getSource(), sender, executor, location, args.toArray(new Object[args.size()])));
				if (arg == null) {
					TranslatableMessage error = customArgument.getMessage();
					if (error != null) {
						throw new SimpleCommandExceptionType(new com.mojang.brigadier.Message() {
							public String getString() {
								return ChatColor.RED + error.getMessage(sender, (String) a);
							}
						}).create();
					} else throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
				}
				
			} else arg = a;
			
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
					} catch (Exception e) {}
				}
				
				Collection<String> list =  suggestionsProvider.run(new SuggestedCommand(source, sender, executor, location, args.toArray(new Object[args.size()])));
				if (list != null) {
					String remaining = builder.getRemaining().toLowerCase();
					for (String suggestion : list)
						if (suggestion.toLowerCase().startsWith(remaining)) builder.suggest(suggestion);
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
	
	public static CommandSender getCommandSender(Object source) {
		try {
			return (CommandSender) getMethod(source.getClass(), "getBukkitSender").invoke(source);
		} catch (InvocationTargetException e) {
			return Bukkit.getConsoleSender();
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static CommandSender getCommandExecutor(Object source) {
		try {
			Object proxyEntity = getField(source.getClass(), "k").get(source);
			if(proxyEntity != null)
				return (CommandSender) getMethod(getNmsClass("Entity"), "getBukkitEntity").invoke(getNmsClass("Entity").cast(proxyEntity));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Location getCommandLocation(Object source) {
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
	
	public static void sendMessage(Object source, BaseComponent message, boolean broadcast) {
		try {
			Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
			Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
			Method a = getMethod(ChatSerializer, "a", String.class);
			Object object = a.invoke(null, ComponentSerializer.toString(message));
			getMethod(source.getClass(), "sendMessage", IChatBaseComponent, boolean.class).invoke(source, object, broadcast);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void broadcastMessage(Object source, CommandSender sender, TranslatableMessage message, String... args) {
		sendMessage(source, new TextComponent(message.getMessage(sender, args)), false);
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
							component.addWith(message.getMessage(player, args));
							player.spigot().sendMessage(component);
						}
				}
				
				if (!(sender instanceof ConsoleCommandSender) && Bukkit.getWorlds().get(0).getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) {
					if (!Class.forName("org.spigotmc.SpigotConfig").getField("silentCommandBlocks").getBoolean(null)) {
						TranslatableComponent component = new TranslatableComponent("chat.type.admin");
						component.addWith(name);
						component.addWith(message.getMessage(Bukkit.getConsoleSender(), args));
						
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
	
	public static void sendFailureMessage(Object source, BaseComponent message) {
		try {
			message.setColor(ChatColor.RED);
			Class<?> IChatBaseComponent = getNmsClass("IChatBaseComponent");
			Class<?> ChatSerializer = IChatBaseComponent.getDeclaredClasses()[0];
			Method a = getMethod(ChatSerializer, "a", String.class);
			Object object = a.invoke(null, ComponentSerializer.toString(message));
			getMethod(source.getClass(), "sendFailureMessage", IChatBaseComponent).invoke(source, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public static Constructor getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		ClassCache key = new ClassCache(clazz, null);
		if(constructors.containsKey(key)) return constructors.get(key);
		else {
			Constructor result = null;
			result = clazz.getDeclaredConstructor(parameterTypes);
			result.setAccessible(true);
			constructors.put(key, result);
			return result;
		}
	}
	
	/** Gets a method.
	 * @param clazz the class where is the method
	 * @param name the name of the method
	 * @param parameterTypes the parameter array
	 * @return the Method object for the specified method in this class
	 * @throws NoSuchMethodException if a method with the specified name is not found
	 * @throws SecurityException
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		ClassCache key = new ClassCache(clazz, name);
		if(methods.containsKey(key)) return methods.get(key);
		else {
			Method result = null;
			result = clazz.getDeclaredMethod(name, parameterTypes);
			result.setAccessible(true);
			methods.put(key, result);
			return result;
		}
	}
	
	/** Gets a field.
	 * @param clazz the class where is the field
	 * @param name the name of the field
	 * @return the Field object for the specified field in this class
	 * @throws NoSuchFieldException if a field with the specified name is not found
	 * @throws SecurityException
	 */
	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		ClassCache key = new ClassCache(clazz, name);
		if(fields.containsKey(key)) return fields.get(key);
		else {
			Field result = null;
			result = clazz.getDeclaredField(name);
			result.setAccessible(true);
			fields.put(key, result);
			return result;
		}
	}
	
	/** Gets a class in the package net.minecraft.server.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static Class<?> getNmsClass(final String className) throws ClassNotFoundException {
		if (nmsClasses.containsKey(className)) return nmsClasses.get(className);
		else {
			Class<?> clazz = Class.forName(nmsPackageName + "." + className);
			nmsClasses.put(className, clazz);
			return clazz;
		}
	}
	
	/** Gets a class in the package org.bukkit.craftbukkit.v[version].
	 * @param className the name of the class
	 * @return the Class object for the class with the specified name
	 * @throws ClassNotFoundException if the class cannot be located
	 */
	public static Class<?> getObcClass(final String className) throws ClassNotFoundException {
		if (obcClasses.containsKey(className)) return obcClasses.get(className);
		else {
			Class<?> clazz = Class.forName(obcPackageName + "." + className);
			obcClasses.put(className, clazz);
			return clazz;
		}
	}

	/**
	* Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @return the ArgumentType<?>
	 * @see getNMSArgumentInstance
	 */
	public static ArgumentType<?> getNmsArgumentInstance(String nmsClassName) {
		return getNmsArgumentInstance(nmsClassName, "a");		
	}
	
	/**
	 * Gets an instance of an argument in the package net.minecraft.server.v[version].
	 * @param nmsClassName the name of the class
	 * @param methodName the name of the method
	 * @return the ArgumentType<?>
	 */
	public static ArgumentType<?> getNmsArgumentInstance(String nmsClassName, String methodName) {
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