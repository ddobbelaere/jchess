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

import org.junit.jupiter.api.Test;

/**
 * Board test.
 *
 * @author Dieter Dobbelaere
 */
class BoardTest
{

    /**
     * Test static methods.
     */
    @Test
    void testStaticMethods()
    {
        // Check bitboard methods.
        assertEquals(1, Board.getSquareBitboard("a1"));
        assertEquals(Board.getSquareBitboard("c2"), Board.getSquareBitboard(1, 2));
        assertEquals(Board.getRowBitboard('8'), Board.getRowBitboard(7));
        assertEquals(Board.getColBitboard('h'), Board.getColBitboard(7));
        assertEquals(0x8040201008040201L, Board.getDiagsBitboard(0, 0));
        assertEquals(Long.reverseBytes(0x8040201008040201L), Board.getDiagsBitboard(0, 7));
        assertEquals(true, Board.getBitboardDebugString(1L).contains("x"));

        for (int i = 0; i < 8; i++)
        {
            // Clear expected bitboards.
            long rowBitboard = 0;
            long colBitboard = 0;

            for (int j = 0; j < 8; j++)
            {
                // Add square to expected row and column bitboards.
                rowBitboard |= Board.getSquareBitboard(i, j);
                colBitboard |= Board.getSquareBitboard(j, i);
            }

            // Check if they match the output of the dedicated static methods.
            assertEquals(rowBitboard, Board.getRowBitboard(i));
            assertEquals(colBitboard, Board.getColBitboard(i));
        }
    }

}
