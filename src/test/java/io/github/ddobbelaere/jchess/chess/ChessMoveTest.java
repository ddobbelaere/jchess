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
 * ChessMove test.
 *
 * @author Dieter Dobbelaere
 */
class ChessMoveTest
{

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessMove#ChessMove(java.lang.String)}.
	 */
	@Test
	void testChessMoveString()
	{
		// Test with normal move.
		ChessMove move = new ChessMove("d2d4");

		assertEquals(11, move.getFromSquare());
		assertEquals(27, move.getToSquare());
		assertEquals(ChessPromotionPieceType.NONE, move.getPromotionPieceType());
		assertEquals("d2d4", move.toString());

		// Test with promotion.
		move = new ChessMove("h7h8Q");

		assertEquals(55, move.getFromSquare());
		assertEquals(63, move.getToSquare());
		assertEquals(ChessPromotionPieceType.QUEEN, move.getPromotionPieceType());
		assertEquals("h7h8Q", move.toString());

		// Test with other promotion piece types.
		move = new ChessMove("h7h8R");

		assertEquals(ChessPromotionPieceType.ROOK, move.getPromotionPieceType());
		assertEquals("h7h8R", move.toString());

		move = new ChessMove("h7h8N");

		assertEquals(ChessPromotionPieceType.KNIGHT, move.getPromotionPieceType());
		assertEquals("h7h8N", move.toString());

		move = new ChessMove("h7h8B");

		assertEquals(ChessPromotionPieceType.BISHOP, move.getPromotionPieceType());
		assertEquals("h7h8B", move.toString());

		// Test with illegal move strings.
		assertThrows(IllegalArgumentException.class, () -> new ChessMove("e4"));
		assertThrows(IllegalArgumentException.class, () -> new ChessMove("h7h8K"));
	}

}
