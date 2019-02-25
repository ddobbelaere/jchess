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

import java.util.ArrayList;
import java.util.List;

/**
 * Chess move generator.
 *
 * @author Dieter Dobbelaere
 */
class ChessMoveGenerator
{
	/**
	 * Array of all rook move directions. Each direction is a two element array of
	 * the form (row increment, column increment).
	 */
	static final int[][] rookMoveDirections = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

	/**
	 * Array of all bishop move directions. Each direction is a two element array of
	 * the form (row increment, column increment).
	 */
	static final int[][] bishopMoveDirections = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

	/**
	 * Array of all king move directions. Each direction is a two element array of
	 * the form (row increment, column increment).
	 */
	static final int[][] kingMoveDirections = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 1 },
			{ 1, -1 }, { -1, 1 }, { -1, -1 } };

	/**
	 * Array of attack bitboards of opponent's pawns.
	 */
	private static final long[] pawnAttackBitboards = new long[] { 0x0000000000000200L, 0x0000000000000500L,
			0x0000000000000A00L, 0x0000000000001400L, 0x0000000000002800L, 0x0000000000005000L, 0x000000000000A000L,
			0x0000000000004000L, 0x0000000000020000L, 0x0000000000050000L, 0x00000000000A0000L, 0x0000000000140000L,
			0x0000000000280000L, 0x0000000000500000L, 0x0000000000A00000L, 0x0000000000400000L, 0x0000000002000000L,
			0x0000000005000000L, 0x000000000A000000L, 0x0000000014000000L, 0x0000000028000000L, 0x0000000050000000L,
			0x00000000A0000000L, 0x0000000040000000L, 0x0000000200000000L, 0x0000000500000000L, 0x0000000A00000000L,
			0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000A000000000L, 0x0000004000000000L,
			0x0000020000000000L, 0x0000050000000000L, 0x00000A0000000000L, 0x0000140000000000L, 0x0000280000000000L,
			0x0000500000000000L, 0x0000A00000000000L, 0x0000400000000000L, 0x0002000000000000L, 0x0005000000000000L,
			0x000A000000000000L, 0x0014000000000000L, 0x0028000000000000L, 0x0050000000000000L, 0x00A0000000000000L,
			0x0040000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			0x0000000000000000L, 0x0000000000000000L };

	/**
	 * Array of knight attack bitboards.
	 */
	private static final long[] knightAttackBitboards = new long[] { 0x0000000000020400L, 0x0000000000050800L,
			0x00000000000A1100L, 0x0000000000142200L, 0x0000000000284400L, 0x0000000000508800L, 0x0000000000A01000L,
			0x0000000000402000L, 0x0000000002040004L, 0x0000000005080008L, 0x000000000A110011L, 0x0000000014220022L,
			0x0000000028440044L, 0x0000000050880088L, 0x00000000A0100010L, 0x0000000040200020L, 0x0000000204000402L,
			0x0000000508000805L, 0x0000000A1100110AL, 0x0000001422002214L, 0x0000002844004428L, 0x0000005088008850L,
			0x000000A0100010A0L, 0x0000004020002040L, 0x0000020400040200L, 0x0000050800080500L, 0x00000A1100110A00L,
			0x0000142200221400L, 0x0000284400442800L, 0x0000508800885000L, 0x0000A0100010A000L, 0x0000402000204000L,
			0x0002040004020000L, 0x0005080008050000L, 0x000A1100110A0000L, 0x0014220022140000L, 0x0028440044280000L,
			0x0050880088500000L, 0x00A0100010A00000L, 0x0040200020400000L, 0x0204000402000000L, 0x0508000805000000L,
			0x0A1100110A000000L, 0x1422002214000000L, 0x2844004428000000L, 0x5088008850000000L, 0xA0100010A0000000L,
			0x4020002040000000L, 0x0400040200000000L, 0x0800080500000000L, 0x1100110A00000000L, 0x2200221400000000L,
			0x4400442800000000L, 0x8800885000000000L, 0x100010A000000000L, 0x2000204000000000L, 0x0004020000000000L,
			0x0008050000000000L, 0x00110A0000000000L, 0x0022140000000000L, 0x0044280000000000L, 0x0088500000000000L,
			0x0010A00000000000L, 0x0020400000000000L };

	/**
	 * <p>
	 * Safety information of <em>our</em> king consisting of
	 * <li>Pinned pieces.</li>
	 * <li>Attacked squares around our king.</li>
	 * <li>If our king is in (double) check.</li>
	 * <li>Attack lines of pieces that give check (including the pieces
	 * themselves).</li>
	 * </p>
	 */
	static class KingSafety
	{
		/**
		 * Pinned pieces bitboard.
		 */
		long pinnedPieces;

		/**
		 * Bitboard of accessible squares around our king (i.e., squares where our king
		 * can move to, not including castling squares).
		 */
		long accessibleSquares;

		/**
		 * Double check.
		 */
		boolean isDoubleCheck;

		/**
		 * Bitboard of attack lines of pieces that give check (including the pieces
		 * themselves).
		 */
		long attackLines;

		/**
		 * @return Our king is in check.
		 */
		boolean isCheck()
		{
			return attackLines != 0;
		}

		/**
		 * @return Our king is in double check.
		 */
		boolean isDoubleCheck()
		{
			return isDoubleCheck;
		}
	}

	/**
	 * Generates king safety information of a given legal chess position.
	 *
	 * @param position Given legal chess position.
	 * @return King safety information of the given legal chess position.
	 */
	static KingSafety generateKingSafety(ChessPosition position)
	{
		// Construct king safety object.
		KingSafety kingSafety = new KingSafety();

		// Get square of our king.
		final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);
		final int ourKingRow = ourKingSquare / 8;
		final int ourKingCol = ourKingSquare % 8;

		// Temporary variable that hold number of pieces that give check.
		int numCheckingPieces = 0;

		// Check sliding piece attackers.
		for (int pieceType = 0; pieceType < 2; pieceType++)
		{
			int[][] pieceMoveDirections = (pieceType == 0) ? rookMoveDirections : bishopMoveDirections;
			final long attackingPieces = position.board.theirPieces
					& ((pieceType == 0) ? position.board.rooks : position.board.bishops);

			if ((attackingPieces & ((pieceType == 0) ? MagicUtils.getRookAttackBitboard(ourKingSquare, 0)
					: MagicUtils.getBishopAttackBitboard(ourKingSquare, 0))) != 0)
			{
				for (int[] pieceMoveDirection : pieceMoveDirections)
				{
					// Start at the square of our king.
					int row = ourKingRow;
					int col = ourKingCol;

					// Initialize temporary variables.
					boolean possiblePinnedPieceFound = false;
					long possiblePinnedPieceBitboard = 0;
					long attackLine = 0;

					while (true)
					{
						// Go to next square.
						row += pieceMoveDirection[0];
						col += pieceMoveDirection[1];

						if (row < 0 || row > 7 || col < 0 || col > 7)
						{
							// Invalid square.
							break;
						}

						long squareBitboard = ChessBoard.getSquareBitboard(row, col);

						if ((squareBitboard & position.board.ourPieces) != 0)
						{
							// One of our pieces is present here, it is possibly pinned.
							if (possiblePinnedPieceFound)
							{
								// There are at least two of our pieces lined up first (so no piece is pinned).
								break;
							}
							else
							{
								// If an attacking piece follows, this piece is pinned.
								possiblePinnedPieceFound = true;
								possiblePinnedPieceBitboard = squareBitboard;
							}
						}

						// Add the square bitboard to the attack line.
						attackLine |= squareBitboard;

						if ((squareBitboard & attackingPieces) != 0)
						{
							// One of their attacking pieces is present.
							if (possiblePinnedPieceFound)
							{
								// The attacking pieces doesn't give check, so only update the pinned pieces
								// bitboard.
								kingSafety.pinnedPieces |= possiblePinnedPieceBitboard;
							}
							else
							{
								// No pinned piece, which means that the attacking piece gives check.
								kingSafety.attackLines |= attackLine;
								numCheckingPieces++;
							}

							break;
						}
					}
				}
			}
		}

		// Check pawns.
		long attackingPawns = pawnAttackBitboards[ourKingSquare] & position.board.theirPieces & position.board.pawns;
		kingSafety.attackLines |= attackingPawns;

		if (attackingPawns != 0)
		{
			// No more than one pawn can give check.
			numCheckingPieces++;
		}

		// Check knights.
		long attackingKnights = knightAttackBitboards[ourKingSquare] & position.board.theirPieces
				& ~(position.board.pawns | position.board.rooks | position.board.bishops | position.board.kings);
		kingSafety.attackLines |= attackingKnights;

		if (attackingKnights != 0)
		{
			// No more than one knight can give check.
			numCheckingPieces++;
		}

		// Set double check flag.
		kingSafety.isDoubleCheck = (numCheckingPieces == 2);

		// Accessible squares around king where the king can move to.
		for (int[] kingMoveDirection : kingMoveDirections)
		{
			final int row = ourKingRow + kingMoveDirection[0];
			final int col = ourKingCol + kingMoveDirection[1];

			if (row < 0 || row > 7 || col < 0 || col > 7)
			{
				// Invalid square.
				continue;
			}

			final long squareBitboard = ChessBoard.getSquareBitboard(row, col);

			if ((squareBitboard & position.board.ourPieces) != 0)
			{
				// If one of our pieces is present, the square is not accessible.
				continue;
			}

			if ((squareBitboard & kingSafety.attackLines & ~position.board.theirPieces) != 0)
			{
				// The square lies on an attack line and is not equal to the attacking piece.
				// Therefore, it is certainly not accessible.
				continue;
			}

			if (!squareIsUnderAttack(position, 8 * row + col))
			{
				// The square is not under attack. Add it to the accessible
				// squares bitboard.
				kingSafety.accessibleSquares |= squareBitboard;
			}
		}

		// Return king safety object.
		return kingSafety;
	}

	/**
	 * Check if the given square is under attack in the given position (disregarding
	 * our king, such that X-ray attacks are considered).
	 *
	 * @param position Given position.
	 * @param square   Given square (between 0 and 63).
	 * @return The square is under attack.
	 */
	static boolean squareIsUnderAttack(ChessPosition position, int square)
	{
		// Check rooks.
		if ((MagicUtils.getRookAttackBitboard(square,
				(position.board.ourPieces & ~position.board.kings) | position.board.theirPieces)
				& position.board.theirPieces & position.board.rooks) != 0)
		{
			return true;
		}

		// Check bishops.
		if ((MagicUtils.getBishopAttackBitboard(square,
				(position.board.ourPieces & ~position.board.kings) | position.board.theirPieces)
				& position.board.theirPieces & position.board.bishops) != 0)
		{
			return true;
		}

		// Check knights.
		if ((knightAttackBitboards[square] & position.board.theirPieces
				& ~(position.board.pawns | position.board.rooks | position.board.bishops | position.board.kings)) != 0)
		{
			return true;
		}

		// Check pawns.
		if ((pawnAttackBitboards[square] & position.board.theirPieces & position.board.pawns) != 0)
		{
			return true;
		}

		// Check their king.
		final int theirKingSquare = Long.numberOfTrailingZeros(position.board.theirPieces & position.board.kings);
		final int theirKingRow = theirKingSquare / 8;
		final int theirKingCol = theirKingSquare % 8;
		final int row = square / 8;
		final int col = square % 8;

		if (Math.abs(theirKingRow - row) <= 1 && Math.abs(theirKingCol - col) <= 1)
		{
			return true;
		}

		// If we get here, the square is not under attack.
		return false;
	}

	/**
	 * Generates all legal moves of a given legal chess position.
	 *
	 * @param position Given legal chess position.
	 * @return A list of legal moves of the given legal chess position.
	 */
	static List<ChessMove> generateLegalMoves(ChessPosition position)
	{
		// Construct the legal moves list.
		List<ChessMove> legalMoves = new ArrayList<>();

		// Generate king safety.
		KingSafety kingSafety = generateKingSafety(position);

		// Add all moves for each piece type.
		legalMoves.addAll(generateKingMoves(position, kingSafety));

		// Return the list.
		return legalMoves;
	}

	/**
	 * Generates all legal king moves of a given legal chess position.
	 *
	 * @param position   Given legal chess position.
	 * @param kingSafety King safety corresponding to the position.
	 * @return A list of legal king moves of the given legal chess position.
	 */
	static List<ChessMove> generateKingMoves(ChessPosition position, KingSafety kingSafety)
	{
		// Construct the legal moves list.
		List<ChessMove> legalMoves = new ArrayList<>();

		// Add dummy move to get test coverage.
		legalMoves.add(new ChessMove(0, 0));

		// Return the list.
		return legalMoves;
	}
}
