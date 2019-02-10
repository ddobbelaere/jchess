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

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * MagicUtils test.
 *
 * @author Dieter Dobbelaere
 */
class MagicUtilsTest
{
	/**
	 * Interface of an operator that yield an attack bitboard.
	 */
	private interface GetAttackBitboardOperator
	{
		long apply(int a, long b);
	}

	/**
	 * Test static methods.
	 */
	@Test
	void testStaticMethods()
	{
		// Instantiate class once to get full test coverage.
		MagicUtils magicUtils = new MagicUtils();

		// Test static methods.
		final int[][] rookMovements = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
		final int[][] bishopMovements = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

		testGetAttackBitboardOperator(rookMovements, (a, b) -> MagicUtils.getRookAttackBitboard(a, b));
		testGetAttackBitboardOperator(bishopMovements, (a, b) -> MagicUtils.getBishopAttackBitboard(a, b));
	}

	/**
	 * Test the MagicUtils method for getting the attack bitboard for a certain
	 * sliding piece type (rook or bishop).
	 *
	 * @param pieceMovements    Array holding the possible piece movement
	 *                          directions.
	 * @param functionUnderTest Static MagicUtils function under test.
	 */
	private void testGetAttackBitboardOperator(int[][] pieceMovements, GetAttackBitboardOperator functionUnderTest)
	{
		// Instantiate random number generator.
		Random rng = new Random();

		// Number of random bitboards tested per square.
		final int NUM_RANDOM_BB_PER_SQUARE = 1000;

		for (int square = 0; square < 64; square++)
		{
			final int row = square / 8;
			final int col = square % 8;

			// Test with random bitboards of occupied squares.
			for (int n = 0; n < NUM_RANDOM_BB_PER_SQUARE; n++)
			{
				// Generate random bitboard of occupied squares.
				final long occupiedSquares = rng.nextLong();

				// Calculate the attack bitboard in a straightforward (but slow) way.
				long refAttackBitboard = 0;

				for (final int[] pieceMovement : pieceMovements)
				{
					// Start at the piece square.
					int attackedSquareRow = row;
					int attackedSquareCol = col;

					while (true)
					{
						attackedSquareRow += pieceMovement[0];
						attackedSquareCol += pieceMovement[1];

						if (attackedSquareRow < 0 || attackedSquareRow > 7 || attackedSquareCol < 0
								|| attackedSquareCol > 7)
						{
							// This square falls outside the chess board.
							break;
						}

						// Add the square to the attack bitboard.
						refAttackBitboard |= ChessBoard.getSquareBitboard(attackedSquareRow, attackedSquareCol);

						if ((ChessBoard.getSquareBitboard(attackedSquareRow, attackedSquareCol) & occupiedSquares) != 0)
						{
							// This square is occupied and hides all other following squares from the
							// attack.
							break;
						}
					}
				}

				// Verify that the result of the function under test matches the reference
				// value.
				assertEquals(functionUnderTest.apply(square, occupiedSquares), refAttackBitboard);
			}
		}
	}

}
