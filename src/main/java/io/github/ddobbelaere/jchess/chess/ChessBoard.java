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
	 * Square of active player's king.
	 */
	byte ourKing;

	/**
	 * Square of opponent's king.
	 */
	byte theirKing;

	/**
	 * {@code true} if and only if the board is mirrored (black to move).
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

		// Swap kings.
		byte tempByte = ourKing;
		ourKing = theirKing;
		theirKing = tempByte;
	}
}
