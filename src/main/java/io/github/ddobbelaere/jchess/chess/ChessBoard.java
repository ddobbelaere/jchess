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

/**
 * Chess board holding piece placement.
 *
 * @author Dieter Dobbelaere
 */
public class ChessBoard
{
	/**
	 * Active player's pieces bitboard.
	 */
	long ourPieces;

	/**
	 * Opponent's pieces bitboard.
	 */
	long theirPieces;

	/**
	 * Pawns bitboard.
	 */
	long pawns;

	/**
	 * Rooks bitboard (active for rooks and queens).
	 */
	long rooks;

	/**
	 * Bishops bitboard (active for bishops and queens).
	 */
	long bishops;

	/**
	 * Kings bitboard (active for kings).
	 */
	long kings;

	/**
	 * The board is mirrored (black to move).
	 */
	boolean isMirrored;

	/**
	 * @return Active player's pieces bitboard.
	 */
	public final long getOurPieces()
	{
		return ourPieces;
	}

	/**
	 * @return Opponent's pieces bitboard.
	 */
	public final long getTheirPieces()
	{
		return theirPieces;
	}

	/**
	 * Mirror the board.
	 */
	public void mirror()
	{
		// Swap pieces bitboards.
		long temp = ourPieces;
		ourPieces = theirPieces;
		theirPieces = temp;

		// Reverse bytes of all bitboards.
		ourPieces = Long.reverseBytes(ourPieces);
		theirPieces = Long.reverseBytes(theirPieces);
		pawns = Long.reverseBytes(pawns);
		rooks = Long.reverseBytes(rooks);
		bishops = Long.reverseBytes(bishops);
		kings = Long.reverseBytes(kings);

		// Toggle flag.
		isMirrored = !isMirrored;
	}

	/**
	 * Get the bitboard corresponding to a given square.
	 *
	 * @param square Given square (between 0 and 63).
	 * @return Bitboard corresponding to the square.
	 */
	static long getSquareBitboard(final int square)
	{
		return 1L << square;
	}

	/**
	 * Get the bitboard corresponding to a given square.
	 *
	 * @param row Row of the square.
	 * @param row Column of the square.
	 * @return Bitboard corresponding to the square.
	 */
	static long getSquareBitboard(final int row, final int col)
	{
		return 1L << (8 * row + col);
	}

	/**
	 * Get the bitboard corresponding to a given square.
	 *
	 * @param name Square name (e.g. "e4").
	 * @return Bitboard corresponding to the square.
	 */
	static long getSquareBitboard(final String name)
	{
		return getSquareBitboard(name.charAt(1) - '1', name.charAt(0) - 'a');
	}

	/**
	 * Get the bitboard corresponding to a given row.
	 *
	 * @param row Given row (between 0 and 7).
	 * @return Bitboard corresponding to the given row.
	 */
	static long getRowBitboard(final int row)
	{
		return 0xFFL << (8 * row);
	}

	/**
	 * Get the bitboard corresponding to a given row.
	 *
	 * @param row Given row (between '1' and '8').
	 * @return Bitboard corresponding to the given row.
	 */
	static long getRowBitboard(final char row)
	{
		return getRowBitboard(row - '1');
	}

	/**
	 * Get the bitboard corresponding to a given column.
	 *
	 * @param col Given column (between 0 and 7).
	 * @return Bitboard corresponding to the given column.
	 */
	static long getColBitboard(final int col)
	{
		return 0x0101010101010101L << col;
	}

	/**
	 * Get the bitboard corresponding to a given column.
	 *
	 * @param col Given column (between 'a' and 'h').
	 * @return Bitboard corresponding to the given column.
	 */
	static long getColBitboard(final char col)
	{
		return getRowBitboard(col - 'a');
	}

	/**
	 * @return String representation of the chess board.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int row = 7; row >= 0; row--)
		{
			for (int col = 0; col <= 7; col++)
			{
				// Determine square bitboard.
				long squareBitboard;
				if (!isMirrored)
				{
					// The board is from white's perspective.
					squareBitboard = (1L << (8 * row + col));
				}
				else
				{
					// The board is from black's perspective.
					squareBitboard = (1L << (8 * (7 - row) + col));
				}

				// Determine square label.
				char squareLabel = '.';

				if (((ourPieces + theirPieces) & squareBitboard) != 0)
				{
					if ((pawns & squareBitboard) != 0)
					{
						// This is pawn.
						squareLabel = 'p';
					}
					else if ((kings & squareBitboard) != 0)
					{
						// This is king.
						squareLabel = 'k';
					}
					else if ((rooks & squareBitboard) != 0)
					{
						if ((bishops & squareBitboard) != 0)
						{
							// This is queen.
							squareLabel = 'q';
						}
						else
						{
							// This is rook.
							squareLabel = 'r';
						}
					}
					else if ((bishops & squareBitboard) != 0)
					{
						// This is bishop.
						squareLabel = 'b';
					}
					else
					{
						// This is knight.
						squareLabel = 'n';
					}

					// Change label for white pieces to uppercase.
					if ((!isMirrored && ((ourPieces & squareBitboard) != 0))
							|| (isMirrored && ((theirPieces & squareBitboard) != 0)))
					{
						squareLabel = Character.toUpperCase(squareLabel);
					}
				}

				// Append to string.
				sb.append(squareLabel);

				if (col == 7)
				{
					sb.append('\n');
				}
				else
				{
					// Add space between square labels.
					sb.append(' ');
				}
			}
		}

		return sb.toString();
	}
}
