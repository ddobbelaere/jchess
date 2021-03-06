/*
 * Copyright (C) 2019  Dieter Dobbelaere.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.ddobbelaere.jchess.chess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Game test.
 *
 * @author Dieter Dobbelaere
 */
class GameTest
{

    @Test
    void test()
    {
        // Construct a new game.
        Game game = createGame(Position.STARTING, "d2d4", "g8f6", "c2c4", "e7e6", "b1c3", "f8b4");

        // Try to play an illegal move.
        assertThrows(IllegalMoveException.class, () -> game.playMoves(new Move("b1c3")));

        // Perform some checks.
        assertEquals(6, game.getMoves().size());
        assertEquals(6, game.getMovesSan().size());
        assertEquals(7, game.getPositions().size());
        assertEquals(game.getCurrentPosition(), game.getLastPosition());
        assertEquals(true, game.getLegalMoves().contains(new Move("d1c2")));
        assertEquals(true, game.getLegalMovesSan().contains("Qc2"));
        assertEquals(false, game.isThreefoldRepetition());

        // Test moves from standard algebraic notation.
        String[] moves = new String[] { "d4", "d5", "c4", "e6", "Nc3", "Nf6", "cxd5", "exd5", "Bg5", "Be7", "e3", "c6",
                "Bd3", "Nbd7", "Qc2", "O-O" };
        Game anotherGame = createGameSan(Position.STARTING, moves);
        assertEquals(Arrays.asList(moves), anotherGame.getMovesSan());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Game#isThreefoldRepetition()}.
     */
    @Test
    void testIsThreefoldRepetition()
    {
        assertEquals(false, new Game().isThreefoldRepetition());
        assertEquals(false,
                new Game("rnbqkb1r/pppp1ppp/5n2/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 10 3").isThreefoldRepetition());
        assertEquals(false,
                createGame(Position.STARTING, "d2d4", "d7d5", "g1f3", "g8f6", "f3g1", "f6g8").isThreefoldRepetition());
        assertEquals(true, createGame(Position.STARTING, "d2d4", "d7d5", "g1f3", "g8f6", "f3g1", "f6g8", "g1f3", "g8f6",
                "f3g1", "f6g8").isThreefoldRepetition());
    }

    /**
     * Test method for game info methods.
     */
    @Test
    void testGameInfoMethods()
    {
        Game game = new Game();

        assertEquals(false, game.getWhitePlayerName().isPresent());
        assertEquals(false, game.getBlackPlayerName().isPresent());

        game.setWhitePlayerName("Carlsen, Magnus");
        game.setBlackPlayerName("Kasparov, Garry");

        assertEquals("Carlsen, Magnus", game.getWhitePlayerName().get());
        assertEquals("Kasparov, Garry", game.getBlackPlayerName().get());
    }

    /**
     * Helper function to easily create a game from a given starting position and
     * moves list.
     *
     * @param position Starting position.
     * @param moves    String representation of moves.
     * @return Game from the given starting position with the given moves played.
     * @throws IllegalMoveException If one of the moves is illegal.
     */
    private static Game createGame(Position position, String... moves)
    {
        Game game = new Game(position);

        for (String move : moves)
        {
            game.playMoves(new Move(move));
        }

        return game;
    }

    /**
     * Helper function to easily create a game from a given starting position and
     * moves list in standard algebraic notation.
     *
     * @param position Starting position.
     * @param moves    String representation of moves.
     * @return Game from the given starting position with the given moves played.
     * @throws IllegalMoveException If one of the moves is illegal.
     */
    private static Game createGameSan(Position position, String... moves)
    {
        Game game = new Game(position);

        for (String move : moves)
        {
            game.playMoves(move);
        }

        return game;
    }

}
