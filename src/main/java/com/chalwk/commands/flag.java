/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.Game;
import com.chalwk.game.GameManager;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class flag implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public flag(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "flag";
    }

    @Override
    public String getDescription() {
        return "Flag a cell on the board.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "rows", "The row number", true),
                new OptionData(OptionType.INTEGER, "cols", "The col number", true),
                new OptionData(OptionType.BOOLEAN, "flag", "Flag the cell", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        if (settings.notCorrectChannel(event)) return;

        User player = event.getUser();

        if (!gameManager.isInGame(player)) {
            event.reply("## You are not in a game.").setEphemeral(true).queue();
            return;
        }

        int row = event.getOption("rows").getAsInt();
        int col = event.getOption("cols").getAsInt();
        boolean flagged = event.getOption("flag").getAsBoolean();

        Game game = gameManager.getGame(player);
        game.board.flagCell(row, col, flagged);
        game.updateEmbed(game.board.getState());

        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }
}
