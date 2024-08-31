/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.GameManager;
import com.chalwk.util.GameConfig;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class start implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public start(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Create a new game of Minesweeper.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "rows", "The number of rows", true),
                new OptionData(OptionType.INTEGER, "cols", "The number of cols", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        if (settings.notCorrectChannel(event)) return;

        User player = event.getUser();

        if (gameManager.isInGame(player)) {
            event.reply("## You are already in a game.").setEphemeral(true).queue();
            return;
        }

        GameConfig config = new GameConfig(player, event);
        if (!config.isValidSquare()) {
            String message = "# Invalid board size.\n" +
                    "Please choose a square board size between **" + config.MIN_ROWS + "** x **" + config.MIN_ROWS + "** and **" + config.MAX_ROWS + " x " + config.MAX_ROWS + "**.\n" +
                    "Your current board size is **" + config.rows + "** x **" + config.cols + "**.";
            event.reply(message).setEphemeral(true).queue();
            return;
        }

        gameManager.createGame(config, event);

        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }
}
