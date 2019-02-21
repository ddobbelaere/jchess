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
 * ChessMoveGenerator test.
 *
 * @author Dieter Dobbelaere
 */
class ChessMoveGeneratorTest
{

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKingSafety(io.github.ddobbelaere.jchess.chess.ChessPosition)}.
	 */
	@Test
	void testGenerateKingSafety()
	{
		// Instantiate class once to get full test coverage.
		ChessMoveGenerator chessMoveGenerator = new ChessMoveGenerator();

		// Perform some sanity checks on the starting position.
		ChessMoveGenerator.KingSafety kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.STARTING);

		assertEquals(kingSafety.attackLines, 0L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares, 0L);
		assertEquals(kingSafety.isCheck(), false);
		assertEquals(kingSafety.isDoubleCheck(), false);

		// Check with some more challenging positions.
		// Rook and bishop give double check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/8/3r2K1/8 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0x40810203800L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"));
		assertEquals(kingSafety.isCheck(), true);
		assertEquals(kingSafety.isDoubleCheck(), true);

		// No check, but queen is pinned.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0L);
		assertEquals(kingSafety.pinnedPieces, ChessBoard.getSquareBitboard("f3"));
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
						| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
						| ChessBoard.getSquareBitboard("h3"));
		assertEquals(kingSafety.isCheck(), false);
		assertEquals(kingSafety.isDoubleCheck(), false);

		// Knight and rook give double check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/8/1r4K1/4n3 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0x3E10L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("g3")
						| ChessBoard.getSquareBitboard("h3"));
		assertEquals(kingSafety.isCheck(), true);
		assertEquals(kingSafety.isDoubleCheck(), true);

		// Pawn gives check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/5p2/6K1/8 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0x200000L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
						| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("f3")
						| ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"));
		assertEquals(kingSafety.isCheck(), true);
		assertEquals(kingSafety.isDoubleCheck(), false);

		// No checks and no pinned pieces.
		kingSafety = ChessMoveGenerator
				.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/4Q3/5Q2/6K1/8 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
						| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
						| ChessBoard.getSquareBitboard("h3"));
		assertEquals(kingSafety.isCheck(), false);
		assertEquals(kingSafety.isDoubleCheck(), false);

		// No checks and no pinned pieces, but king movement restricted due to pawn and
		// opponent's king.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/8/8/3p4/8/4K3/8/4k3 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares,
				ChessBoard.getSquareBitboard("d3") | ChessBoard.getSquareBitboard("d4")
						| ChessBoard.getSquareBitboard("f3") | ChessBoard.getSquareBitboard("f4"));
		assertEquals(kingSafety.isCheck(), false);
		assertEquals(kingSafety.isDoubleCheck(), false);

		// No checks and no pinned pieces, king in the corner.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("7K/1k6/8/8/8/8/8/8 w - - 0 1"));

		assertEquals(kingSafety.attackLines, 0L);
		assertEquals(kingSafety.pinnedPieces, 0L);
		assertEquals(kingSafety.accessibleSquares, ChessBoard.getSquareBitboard("g7")
				| ChessBoard.getSquareBitboard("h7") | ChessBoard.getSquareBitboard("g8"));
		assertEquals(kingSafety.isCheck(), false);
		assertEquals(kingSafety.isDoubleCheck(), false);
	}

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateLegalMoves(io.github.ddobbelaere.jchess.chess.ChessPosition)}.
	 */
	@Test
	void testGenerateLegalMoves()
	{
		ChessMoveGenerator.generateLegalMoves(ChessPosition.STARTING);
	}

}
