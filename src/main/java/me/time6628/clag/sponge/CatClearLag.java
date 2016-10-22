package me.time6628.clag.sponge;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import me.time6628.clag.sponge.commands.RemoveAllCommand;
import me.time6628.clag.sponge.commands.RemoveHostilesCommand;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.3", description = "DIE LAG, DIE!")
public class CatClearLag {

    public static Logger cclLogger = Logger.getLogger("CCL");
    public Game game = Sponge.getGame();

    private Scheduler scheduler = Sponge.getScheduler();

    public Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[KKMCClearLag]")).build();

    @Listener
    public void onInit(GameInitializationEvent event) {
        CatClearLag.cclLogger.info("Starting plugin...");
        registerCommands();
        Task.Builder builder = scheduler.createTaskBuilder();
        Task task = builder.execute(new ItemClearer(this))
                .async()
                .delay(10, TimeUnit.MINUTES)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);
        Task warningTaskOne = builder.execute(new ItemClearingWarning(60, this))
                .async()
                .delay(540, TimeUnit.SECONDS)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
        Task warningTaskTwo = builder.execute(new ItemClearingWarning(30, this))
                .async()
                .delay(570, TimeUnit.SECONDS)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
    }

    private void registerCommands() {
        CatClearLag.cclLogger.info("Registering commands...");
        CommandSpec cSpec = CommandSpec.builder()
                .description(Text.of("Remove all hostile entities from the server."))
                .permission("catclearlag.command.removehostile")
                .executor(new RemoveHostilesCommand(this))
                .build();
        CommandSpec cSpec2 = CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand(this))
                .build();
        Sponge.getCommandManager().register(this, cSpec, "removehostiles", "rhost");
    }


    public void clearGoundItems() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities(Predicate.isEqual(EntityTypes.ITEM));
            //remove them all
            entities.forEach(Entity::remove);
        });
    }

    public void removeHostile() {
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the hostile entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach((entity) -> {
                if (entity instanceof Hostile) {
                    entity.remove();
                }
            });
        });
    }

    public void removeAll() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach(Entity::remove);
        });
    }
}