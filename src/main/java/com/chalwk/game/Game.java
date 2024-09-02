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
    private Date startTime;
    private TimerTask gameEndTask;
    private String embedID;

    public Game(GameConfig config, GameManager gameManager, SlashCommandInteractionEvent event) {
        this.config = config;
        this.gameManager = gameManager;
        this.board = new Board(config.rows, config.cols);
        startGame(event);
    }

    public String getEmbedID() {
        return this.embedID;
    }

    private void setEmbedID(String embedID) {
        this.embedID = embedID;
    }

    public void updateEmbed(BoardState state, SlashCommandInteractionEvent event) {

        EmbedBuilder embed = createEmbedBuilder();

        if (state == BoardState.ONGOING) {
            embed.setColor(Color.BLUE);
        } else if (state == BoardState.WON) {
            embed.setFooter("CONGRATULATIONS! You won!").setColor(Color.GREEN);
            endGame(config.player);
        } else if (state == BoardState.LOST) {
            embed.setFooter("GAME OVER! You hit a mine!").setColor(Color.RED);
            endGame(config.player);
        }

        event.getChannel().deleteMessageById(getEmbedID()).queue();
        event.replyEmbeds(embed.build()).queue();
        setMessageID(event);
    }

    private EmbedBuilder createEmbedBuilder() {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCA3\uD83D\uDCA5 MINESWEEPER \uD83D\uDCA5\uD83D\uDCA3")
                .setDescription("Game started by " + this.config.player.getAsMention())
                .addField("Board:", this.board.buildBoardString(), false)
                .setFooter("""
                        Commands:
                        - /reveal <row> <col>
                        - /flag <row> <col>
                        """).setColor(Color.BLUE);
    }

    public void startGame(SlashCommandInteractionEvent event) {
        this.startTime = new Date();

        EmbedBuilder embed = createEmbedBuilder();
        event.replyEmbeds(embed.build()).queue();

        setMessageID(event);
        scheduleGameEndTask();
    }

    public void endGame(User player) {
        cancelGameEndTask();
        gameManager.removeGame(player);
    }

    private void setMessageID(SlashCommandInteractionEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setEmbedID(event.getChannel().getLatestMessageId());
            }
        }, 500);
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
                    gameManager.removeGame(config.player);
                }
            }
        };

        Timer gameEndTimer = new Timer();
        gameEndTimer.scheduleAtFixedRate(gameEndTask, 0, 1000);
    }

    private void cancelGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
            gameEndTask = null;
        }
    }

    private boolean isTimeUp() {
        long elapsedTime = System.currentTimeMillis() - startTime.getTime();
        return elapsedTime > settings.getDefaultTimeLimit() * 1000L;
    }
}