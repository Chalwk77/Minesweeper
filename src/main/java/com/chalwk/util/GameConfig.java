/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.util;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GameConfig {

    public final int rows;
    public final int cols;
    public final User player;
    public final int MIN_ROWS = 5;
    public final int MAX_ROWS = 10;

    public GameConfig(User player, SlashCommandInteractionEvent event) {
        this.player = player;
        this.rows = event.getOption("rows").getAsInt();
        this.cols = event.getOption("cols").getAsInt();
    }

    public boolean isValidSquare() {
        return rows == cols && rows >= MIN_ROWS && rows <= MAX_ROWS;
    }
}
