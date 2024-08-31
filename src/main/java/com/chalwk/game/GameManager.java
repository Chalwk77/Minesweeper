/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.GameConfig;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

import static com.chalwk.util.fileIO.loadChannelID;

/**
 * Manages game-related operations, including creating games, inviting players, and managing pending invites.
 */
public class GameManager {

    private static String channelID = "";
    private final Map<User, Game> games;

    public GameManager() {
        channelID = loadChannelID();
        this.games = new HashMap<>();
    }

    public static String getChannelID() {
        return GameManager.channelID;
    }

    public void setChannelID(String channelID) {
        GameManager.channelID = channelID;
    }

    public boolean isInGame(User player) {
        return games.containsKey(player);
    }

    public Game getGame(User player) {
        return games.get(player);
    }

    public void createGame(GameConfig config) {
        games.put(config.player, new Game(config, this));
    }

    public Map<User, Game> getGames() {
        return games;
    }

    public void removeGame(User player) {
        this.getGames().remove(player);
    }
}
