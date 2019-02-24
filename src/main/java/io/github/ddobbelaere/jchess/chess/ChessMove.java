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
 * <p>
 * Chess move encoded by
 * <ul>
 * <li>Source square of the moved piece.</li>
 * <li>Destination square of the moved piece.</li>
 * <li>Promotion piece type ({@code NONE} if the move is no pawn
 * promotion).</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note that castling moves have no special encoding. E.g. white castling short
 * is encoded by the move e1-g1.
 * </p>
 *
 * @author Dieter Dobbelaere
 */
class ChessMove
{
	/**
	 * Source square of the moved piece.
	 */
	private byte fromSquare;

	/**
	 * Destination square of the moved piece.
	 */
	private byte toSquare;

	/**
	 * Promotion piece type.
	 */
	private ChessPromotionPieceType promotionPieceType = ChessPromotionPieceType.NONE;

	/**
	 * Construct with given source and destinations squares and promotion piece
	 * type.
	 *
	 * @param fromSquare         Source square of the moved piece.
	 * @param toSquare           Destination square of the moved piece.
	 * @param promotionPieceType Promotion piece type.
	 */
	ChessMove(int fromSquare, int toSquare, ChessPromotionPieceType promotionPieceType)
	{
		this.fromSquare = (byte) fromSquare;
		this.toSquare = (byte) toSquare;
		this.promotionPieceType = promotionPieceType;
	}

	/**
	 * Construct with given source and destination squares (assuming no promotion).
	 *
	 * @param fromSquare Source square of the moved piece.
	 * @param toSquare   Destination square of the moved piece.
	 */
	ChessMove(int fromSquare, int toSquare)
	{
		this(fromSquare, toSquare, ChessPromotionPieceType.NONE);
	}

	/**
	 * <p>
	 * Construct with given string of the form:
	 * <li>Two-character source square (e.g. d2).</li>
	 * <li>Two-character destination square (e.g. d4).</li>
	 * <li>Optional single-character promotion piece type (B, N, Q or R).</li>
	 * </p>
	 *
	 * @param moveString Descriptive move string.
	 */
	ChessMove(String moveString)
	{
		// Check move string length.
		int len = moveString.length();

		if (len != 4 && len != 5)
		{
			throw new IllegalArgumentException(
					"Move string " + moveString + " should contain either 4 or 5 characters.");
		}

		ChessPromotionPieceType promotionPieceType = ChessPromotionPieceType.NONE;

		if (len == 5)
		{
			// Parse promotion piece type character.
			switch (moveString.charAt(4))
			{
			case 'B':
				promotionPieceType = ChessPromotionPieceType.BISHOP;
				break;
			case 'N':
				promotionPieceType = ChessPromotionPieceType.KNIGHT;
				break;
			case 'Q':
				promotionPieceType = ChessPromotionPieceType.QUEEN;
				break;
			case 'R':
				promotionPieceType = ChessPromotionPieceType.ROOK;
				break;
			default:
				throw new IllegalArgumentException("Illegal promotion piece type in move string " + moveString + ".");
			}
		}

		this.fromSquare = (byte) ChessBoard.getSquare(moveString.substring(0, 2));
		this.toSquare = (byte) ChessBoard.getSquare(moveString.substring(2, 4));
		this.promotionPieceType = promotionPieceType;
	}

	/**
	 * @return Source square of the moved piece.
	 */
	public int getFromSquare()
	{
		return fromSquare;
	}

	/**
	 * @return Destination square of the moved piece.
	 */
	public int getToSquare()
	{
		return toSquare;
	}

	/**
	 * @return Promotion piece type.
	 */
	public ChessPromotionPieceType getPromotionPieceType()
	{
		return promotionPieceType;
	}
}
