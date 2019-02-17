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
	byte fromSquare;

	/**
	 * Destination square of the moved piece.
	 */
	byte toSquare;

	/**
	 * Promotion piece type.
	 */
	ChessPromotionPieceType promotionPieceType = ChessPromotionPieceType.NONE;
}
