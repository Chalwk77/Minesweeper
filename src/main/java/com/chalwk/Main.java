/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk;

import com.chalwk.bot.BotInitializer;

import java.io.IOException;

/**
 * The main entry point of the Virtual Pets game project.
 * This class initializes the bot using the BotInitializer class.
 */
public class Main {

    /**
     * The main method of the Virtual Pets game project.
     * It calls the initializeBot method to start the bot initialization process.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        try {
            new BotInitializer().initializeBot();
        } catch (IOException e) {
            System.err.println("Error reading token or initializing the bot: " + e.getMessage());
        }
    }
}
