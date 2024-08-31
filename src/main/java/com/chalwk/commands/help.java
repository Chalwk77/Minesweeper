/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.GameManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class help implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public help(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Provides information on how to play the game, available commands, and game rules.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        String helpMessage = """
                # **Minesweeper Bot**
                ## How to play:
                - Use the `/start` command to create a new game.
                - Use the `/reveal` command to reveal a cell on the board.
                - Use the `/flag` command to flag a cell on the board.
                - Use the `/stop` command to stop a game.
                ## Game Rules:
                - The game is played on a square board.
                - The board is filled with mines and empty cells.
                - The objective is to reveal all empty cells without revealing a mine.
                - If a mine is revealed, the game ends.
                - Flagging a cell prevents it from being revealed.
                - The game is won when all empty cells are revealed.
                ## Commands:
                - `/start rows cols` - Create a new game.
                - `/reveal rows cols` - Reveal a cell on the board.
                - `/flag rows cols flag` - Flag a cell on the board.
                - `/stop` - Stop a game.
                """;

        event.reply(helpMessage).setEphemeral(true).queue();

        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }
}
