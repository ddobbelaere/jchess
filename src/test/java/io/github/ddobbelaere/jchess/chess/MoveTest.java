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
 * Move test.
 *
 * @author Dieter Dobbelaere
 */
class MoveTest
{

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Move#ChessMove(java.lang.String)}.
     */
    @Test
    void testChessMoveString()
    {
        // Test with normal move.
        Move move = new Move("d2d4");

        assertEquals(11, move.getFromSquare());
        assertEquals(27, move.getToSquare());
        assertEquals(PromotionPieceType.NONE, move.getPromotionPieceType());
        assertEquals("d2d4", move.toString());

        // Test with promotion.
        move = new Move("h7h8Q");

        assertEquals(55, move.getFromSquare());
        assertEquals(63, move.getToSquare());
        assertEquals(PromotionPieceType.QUEEN, move.getPromotionPieceType());
        assertEquals("h7h8Q", move.toString());

        // Test with other promotion piece types.
        move = new Move("h7h8R");

        assertEquals(PromotionPieceType.ROOK, move.getPromotionPieceType());
        assertEquals("h7h8R", move.toString());

        move = new Move("h7h8N");

        assertEquals(PromotionPieceType.KNIGHT, move.getPromotionPieceType());
        assertEquals("h7h8N", move.toString());

        move = new Move("h7h8B");

        assertEquals(PromotionPieceType.BISHOP, move.getPromotionPieceType());
        assertEquals("h7h8B", move.toString());

        // Test with illegal move strings.
        assertThrows(IllegalArgumentException.class, () -> new Move("e4"));
        assertThrows(IllegalArgumentException.class, () -> new Move("h7h8K"));
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Move#equals(Object)}.
     */
    @Test
    void testEquals()
    {
        Move move = new Move("d7d8");

        assertEquals(true, move.equals(move));
        assertEquals(false, move.equals(null));
        assertEquals(false, move.equals(new Object()));
        assertEquals(false, move.equals(new Move("d6d8")));
        assertEquals(false, move.equals(new Move("d7e8")));
        assertEquals(false, move.equals(new Move("d7d8Q")));
        assertEquals(true, move.equals(new Move("d7d8")));
    }

}
