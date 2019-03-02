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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
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
		ChessMoveGenerator.KingSafety kingSafety;

		// Perform some sanity checks on the starting position.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.STARTING);

		assertEquals(0L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(0L, kingSafety.accessibleSquares);
		assertEquals(false, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());

		// Check with some more challenging positions.
		// Rook and bishop give double check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/8/3r2K1/8 w - - 0 1"));

		assertEquals(0x40810203800L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"),
				kingSafety.accessibleSquares);
		assertEquals(true, kingSafety.isCheck());
		assertEquals(true, kingSafety.isDoubleCheck());

		// No check, but queen is pinned.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - - 0 1"));

		assertEquals(0L, kingSafety.attackLines);
		assertEquals(ChessBoard.getSquareBitboard("f3"), kingSafety.pinnedPieces);
		assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
				| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
				| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
				| ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
		assertEquals(false, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());

		// Knight and rook give double check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/8/1r4K1/4n3 w - - 0 1"));

		assertEquals(0x3E10L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
				| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("g3")
				| ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
		assertEquals(true, kingSafety.isCheck());
		assertEquals(true, kingSafety.isDoubleCheck());

		// Pawn gives check.
		kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/5p2/6K1/8 w - - 0 1"));

		assertEquals(0x200000L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(
				ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
						| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
						| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("f3")
						| ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"),
				kingSafety.accessibleSquares);
		assertEquals(true, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());

		// No checks and no pinned pieces.
		kingSafety = ChessMoveGenerator
				.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/4Q3/5Q2/6K1/8 w - - 0 1"));

		assertEquals(0L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
				| ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
				| ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
				| ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
		assertEquals(false, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());

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

		assertEquals(0L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(ChessBoard.getSquareBitboard("g7") | ChessBoard.getSquareBitboard("h7")
				| ChessBoard.getSquareBitboard("g8"), kingSafety.accessibleSquares);
		assertEquals(false, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());

		// Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and
		// f1.
		kingSafety = ChessMoveGenerator.generateKingSafety(
				ChessPosition.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"));

		assertEquals(0L, kingSafety.attackLines);
		assertEquals(0L, kingSafety.pinnedPieces);
		assertEquals(ChessBoard.getSquareBitboard("e2") | ChessBoard.getSquareBitboard("f1"),
				kingSafety.accessibleSquares);
		assertEquals(false, kingSafety.isCheck());
		assertEquals(false, kingSafety.isDoubleCheck());
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

	/**
	 * Check if the reference move list is equal to the generated move list.
	 *
	 * @param referenceMoves List of reference moves.
	 * @param generatedMoves List of generated moves.
	 * @param position       Chess position (used for debug messages).
	 * @param moveType       String representation of move type (used for debug
	 *                       messages).
	 */
	private void checkGeneratedMoves(List<ChessMove> referenceMoves, List<ChessMove> generatedMoves,
			ChessPosition position, String moveType)
	{
		// Check if each reference move is present in the move list.
		for (ChessMove referenceMove : referenceMoves)
		{
			assertEquals(true, generatedMoves.contains(referenceMove),
					"Expected legal " + moveType + " move " + referenceMove + " in position\n" + position);
		}

		// Check if each move is present in the reference move list.
		for (ChessMove generatedMove : generatedMoves)
		{
			assertEquals(true, referenceMoves.contains(generatedMove),
					"Illegal generated " + moveType + " move " + generatedMove + "  in position\n" + position);
		}

		// Check that there are no duplicate moves.
		Set<ChessMove> uniqueGeneratedMoves = new HashSet<>();
		uniqueGeneratedMoves.addAll(generatedMoves);

		assertEquals(generatedMoves.size(), uniqueGeneratedMoves.size(), "Duplicate moves found.");
	}

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKingMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
	 */
	@Test
	void testGenerateKingMoves()
	{
		// Add all test positions to a list.
		List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

		// Starting position, no legal king moves.
		testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] {}));

		// Position after 1. e4 e5. The king can move to e2.
		testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2"),
				new ChessMove[] { new ChessMove("e1e2") }));

		// Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and f1
		// and castle short.
		testCases.add(
				Pair.of(ChessPosition.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"),
						new ChessMove[] { new ChessMove("e1e2"), new ChessMove("e1f1"), new ChessMove("e1g1") }));

		// Position after 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3
		// O-O 8.Qd2 Nc6.
		// The white king can go to e2, f2 and d1 and castle long.
		testCases.add(Pair.of(ChessPosition.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/R3KB1R w KQ - 3 9"),
				new ChessMove[] { new ChessMove("e1e2"), new ChessMove("e1f2"), new ChessMove("e1d1"),
						new ChessMove("e1c1") }));

		// Test all positions.
		for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
		{
			ChessPosition position = testCase.getLeft();
			List<ChessMove> generatedMoves = ChessMoveGenerator.generateKingMoves(position,
					ChessMoveGenerator.generateKingSafety(position));

			// Compare with reference move list.
			List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

			// Check if generated move list.
			checkGeneratedMoves(referenceMoves, generatedMoves, position, "king");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKnightMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
	 */
	@Test
	void testGenerateKnightMoves()
	{
		// Add all test positions to a list.
		List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

		// Starting position.
		testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] { new ChessMove("b1c3"), new ChessMove("b1a3"),
				new ChessMove("g1f3"), new ChessMove("g1h3") }));

		// We are in check and can either capture the queen or interpose to resolve the
		// check.
		testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/1N6/8/6K1/8 w - - 0 1"),
				new ChessMove[] { new ChessMove("b4c6"), new ChessMove("b4d5") }));

		// We are in check but cannot resolve it with a knight move.
		testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/8/1N6/6K1/8 w - - 0 1"), new ChessMove[] {}));

		// The knight is pinned and cannot move.
		testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/4N3/8/6K1/8 w - - 0 1"), new ChessMove[] {}));

		// Test all positions.
		for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
		{
			ChessPosition position = testCase.getLeft();
			List<ChessMove> generatedMoves = ChessMoveGenerator.generateKnightMoves(position,
					ChessMoveGenerator.generateKingSafety(position));

			// Compare with reference move list.
			List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

			// Check if generated move list.
			checkGeneratedMoves(referenceMoves, generatedMoves, position, "knight");
		}
	}

}
