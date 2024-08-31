/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.GameConfig;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.chalwk.bot.BotInitializer.getShardManager;

public class Game {

    public final Board board;
    private final GameManager gameManager;
    private final GameConfig config;
    private String embedID;
    private Date startTime;
    private TimerTask gameEndTask;

    public Game(GameConfig config, GameManager gameManager) {
        this.config = config;
        this.gameManager = gameManager;
        this.board = new Board(config.rows, config.cols);
        startGame();
    }

    public void updateEmbed(BoardState state) {

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("\uD83D\uDCA3\uD83D\uDCA5 MINESWEEPER \uD83D\uDCA5\uD83D\uDCA3")
                .setDescription("Game started by " + this.config.player.getAsMention())
                .addField("Board:", board.buildBoardString(), false)
                .setFooter("Commands: /reveal <row> <col>, /flag <row> <col>")
                .setColor(Color.BLUE);

        if (state == BoardState.ONGOING) {
            // The game is still in progress
            // Do nothing, as the footer and color are already set
        } else if (state == BoardState.WIN) {
            embed.setFooter("Congratulations! You won!").setColor(Color.GREEN);
        } else if (state == BoardState.LOSE) {
            embed.setFooter("Game Over! You hit a mine!").setColor(Color.RED);
        }

        this.config.event.getChannel()
                .retrieveMessageById(getEmbedID())
                .queue(message -> message.editMessageEmbeds(embed.build()).queue());
    }

    public void startGame() {

        this.startTime = new Date();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("\uD83D\uDCA3\uD83D\uDCA5 MINESWEEPER \uD83D\uDCA5\uD83D\uDCA3")
                .setDescription("Game started by " + config.player.getAsMention())
                .addField("Board:", board.buildBoardString(), false)
                .setFooter("Commands: /reveal <row> <col>, /flag <row> <col>")
                .setColor(Color.BLUE);

        config.event.replyEmbeds(embed.build()).queue();

        setMessageID(config.event);
        scheduleGameEndTask();
    }

    public void endGame(User player) {
        cancelGameEndTask();
        gameManager.removeGame(player);
    }

    public User getPlayer() {
        return config.player;
    }

    private void scheduleGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
        }
        gameEndTask = new TimerTask() {
            @Override
            public void run() {
                if (isTimeUp()) {
                    this.cancel();
                    String channelID = GameManager.getChannelID();
                    TextChannel channel = getShardManager().getTextChannelById(channelID);
                    channel.sendMessage("Times up! Game has ended").queue();
                }
            }
        };

        Timer gameEndTimer = new Timer();
        gameEndTimer.scheduleAtFixedRate(gameEndTask, 0, 1000);
    }

    public String getEmbedID() {
        return this.embedID;
    }

    private void setEmbedID(String embedID) {
        this.embedID = embedID;
    }

    private void setMessageID(SlashCommandInteractionEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setEmbedID(event.getChannel().getLatestMessageId());
            }
        }, 500);
    }

    private boolean isTimeUp() {
        long elapsedTime = System.currentTimeMillis() - startTime.getTime();
        return elapsedTime > settings.getDefaultTimeLimit() * 1000L;
    }

    private void cancelGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
            gameEndTask = null;
        }
    }
}