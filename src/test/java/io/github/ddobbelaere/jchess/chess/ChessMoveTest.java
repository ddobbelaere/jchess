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

		assertEquals(move.getFromSquare(), 11);
		assertEquals(move.getToSquare(), 27);
		assertEquals(move.getPromotionPieceType(), ChessPromotionPieceType.NONE);

		// Test with promotion.
		move = new ChessMove("h7h8Q");

		assertEquals(move.getFromSquare(), 55);
		assertEquals(move.getToSquare(), 63);
		assertEquals(move.getPromotionPieceType(), ChessPromotionPieceType.QUEEN);

		// Test with other promotion piece types.
		move = new ChessMove("h7h8R");

		assertEquals(move.getPromotionPieceType(), ChessPromotionPieceType.ROOK);

		move = new ChessMove("h7h8N");

		assertEquals(move.getPromotionPieceType(), ChessPromotionPieceType.KNIGHT);

		move = new ChessMove("h7h8B");

		assertEquals(move.getPromotionPieceType(), ChessPromotionPieceType.BISHOP);

		// Test with illegal move strings.
		assertThrows(IllegalArgumentException.class, () -> new ChessMove("e4"));
		assertThrows(IllegalArgumentException.class, () -> new ChessMove("h7h8K"));
	}

}
