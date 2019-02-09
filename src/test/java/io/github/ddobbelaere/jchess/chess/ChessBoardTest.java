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
 * ChessBoard test.
 *
 * @author Dieter Dobbelaere
 */
class ChessBoardTest
{

	/**
	 * Test static methods.
	 */
	@Test
	void testStaticMethods()
	{
		// Check bitboard methods.
		assertEquals(ChessBoard.getSquareBitboard("a1"), 1);
		assertEquals(ChessBoard.getSquareBitboard("c2"), ChessBoard.getSquareBitboard(1, 2));
		assertEquals(ChessBoard.getRowBitboard('8'), ChessBoard.getRowBitboard(7));
		assertEquals(ChessBoard.getColBitboard('h'), ChessBoard.getColBitboard(7));
		assertEquals(ChessBoard.getDiagsBitboard(0, 0), 0x8040201008040201L);
		assertEquals(ChessBoard.getDiagsBitboard(0, 7), Long.reverseBytes(0x8040201008040201L));
		assertEquals(ChessBoard.getBitboardDebugString(1L).contains("x"), true);

		for (int i = 0; i < 8; i++)
		{
			// Clear expected bitboards.
			long rowBitboard = 0;
			long colBitboard = 0;

			for (int j = 0; j < 8; j++)
			{
				// Add square to expected row and column bitboards.
				rowBitboard |= ChessBoard.getSquareBitboard(i, j);
				colBitboard |= ChessBoard.getSquareBitboard(j, i);
			}

			// Check if they match the output of the dedicated static methods.
			assertEquals(rowBitboard, ChessBoard.getRowBitboard(i));
			assertEquals(colBitboard, ChessBoard.getColBitboard(i));
		}
	}

}
