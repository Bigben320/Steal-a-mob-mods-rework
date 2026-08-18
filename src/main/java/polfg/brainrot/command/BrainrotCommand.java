package polfg.brainrot.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import polfg.brainrot.entity.BrainrotMob;
import polfg.brainrot.registry.ModEntities;

/**
 * Команды мода на Brigadier.
 *
 * Заменяет шесть реализаций CommandExecutor и объявления команд в plugin.yml.
 * Приятный побочный эффект: появляются автодополнение и типизированные аргументы,
 * которых у Bukkit-команд не было — там всё разбиралось из String[] args руками.
 *
 * Пока здесь только /brainrot spawn для проверки каркаса. Дальше сюда переедут
 * /rebirth, /friend, /shop, /casino, /daily, /code, /brainrotevent, /spawnworld
 * и админские подкоманды.
 */
public final class BrainrotCommand {

    private BrainrotCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("brainrot")
                .then(Commands.literal("spawn")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> spawn(context.getSource()))));
    }

    private static int spawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        // В 1.21.5+ MobSpawnType переименован в EntitySpawnReason — если CI ругнётся
        // на импорт, менять здесь и в импортах.
        BrainrotMob mob = ModEntities.BRAINROT.get().spawn(level, pos, MobSpawnType.COMMAND);

        if (mob == null) {
            source.sendFailure(Component.literal("Не удалось заспавнить брейнрота"));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Брейнрот заспавнен"), true);
        return 1;
    }
}
