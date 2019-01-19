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
 * Legal chess position that consists of
 * <ul>
 * <li>Board state (how pieces are positioned on the chess board)</li>
 * <li>Color of side to move</li>
 * <li>Castling availability</li>
 * <li>En passant capture possibility</li>
 * <li>Number of halfmoves since the last capture or pawn advance</li>
 * <li>Number of full moves since the start of the game</li>
 * </ul>
 * </p>
 * <p>
 * By <em>legal</em> we mean:
 * <ul>
 * <li>Each side has exactly one king</li>
 * <li>The king of the side that is not to move is not in check</li>
 * <li>Castling availability passes obvious sanity checks (e.g., white cannot
 * castle kingside if there is no rook on h1)</li>
 * <li>En passant information passes obvious sanity checks</li>
 * </ul>
 * </p>
 *
 * @author Dieter Dobbelaere
 *
 */
public class Position
{
	/**
	 * Create a legal chess position from a FEN string.
	 *
	 * @param fen Given FEN record.
	 * @return Position corresponding to the given FEN string.
	 * @throws InvalidFenException If the FEN string is invalid or represents an
	 *                             illegal position.
	 */
	static Position fromFEN(String fen) throws InvalidFenException
	{
		return null;
	}
}
