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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * ChessPosition test.
 *
 * @author Dieter Dobbelaere.
 */
class ChessPositionTest
{

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessPosition#fromFen(java.lang.String)}.
	 */
	@Test
	void testFromFen()
	{
		// Test illegal FEN strings.
		List<String> illegalFenStrings = new ArrayList<>();
		// No castling availability.
		illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w");
		// Illegal character in piece placement string.
		illegalFenStrings.add("rnbqkbnr+pppppppp+8+8+8+8+PPPPPPPP+RNBQKBNR w KQkq - 0 1");
		// Too many rows in piece placement string.
		illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		// Invalid en passant square.
		illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq h9 0 1");

		for (String illegalFenString : illegalFenStrings)
		{
			// Check that the right exception is thrown.
			assertThrows(IllegalFenException.class, () ->
			{
				ChessPosition.fromFen(illegalFenString);
			}, "Illegal FEN string \"" + illegalFenString + "\" should cause an exception.");
		}
	}

	/**
	 * Test method for
	 * {@link io.github.ddobbelaere.jchess.chess.ChessPosition#mirror()}.
	 */
	@Test
	void testMirror()
	{
		// Test with starting position.
		ChessPosition position = ChessPosition.STARTING;
		position.mirror();

		System.out.println(position);
	}

}
