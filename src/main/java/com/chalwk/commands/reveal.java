/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.BoardState;
import com.chalwk.game.Game;
import com.chalwk.game.GameManager;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class reveal implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public reveal(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "reveal";
    }

    @Override
    public String getDescription() {
        return "Reveal a cell on the board.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "rows", "The row number", true),
                new OptionData(OptionType.INTEGER, "cols", "The col number", true)
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

        Game game = gameManager.getGame(player);
        game.board.revealCell(row, col);


        BoardState state;

        if (game.board.isGameWon()) { // player won
            state = BoardState.WON;
            game.board.revealAllMines();
        } else if (game.board.isGameLost(row, col)) { // player lost
            state = BoardState.LOST;
            game.board.revealAllMines();
        } else {
            state = game.board.getState(); // Get the current state from the board
        }

        game.updateEmbed(state);

        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }
}
