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
        assertThrows(IllegalMoveException.class, () -> game.playMove(new Move("b1c3")));

        // Perform some checks.
        assertEquals(6, game.getMoves().size());
        assertEquals(7, game.getPositions().size());
        assertEquals(true, game.getLegalMoves().contains(new Move("d1c2")));
        assertEquals(false, game.isThreefoldRepetition());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Game#isThreefoldRepetition()}.
     */
    void testIsThreefoldRepetition()
    {
        assertEquals(false, new Game().isThreefoldRepetition());
        assertEquals(false, new Game(Position.fromFen(
                "rnbqkb1r/pppp1ppp/5n2/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 10 3")).isThreefoldRepetition());
        assertEquals(false,
                createGame(Position.STARTING, "d2d4", "d7d5", "g1f3", "g8f6", "f3g1", "f6f8").isThreefoldRepetition());
        assertEquals(true, createGame(Position.STARTING, "d2d4", "d7d5", "g1f3", "g8f6", "f3g1", "f6f8", "g1f3", "g8f6",
                "f3g1", "f6f8").isThreefoldRepetition());
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
            game.playMove(new Move(move));
        }

        return game;
    }

}
