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
		// Test legal FEN strings.
		List<String> legalFenStrings = new ArrayList<>();
		// En passant capture possible.
		legalFenStrings.add("rnbqkb1r/pppp1ppp/5n2/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 3");

		for (String legalFenString : legalFenStrings)
		{
			ChessPosition.fromFen(legalFenString);
		}

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
		// Illegal position: black has no king.
		illegalFenStrings.add("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1");
		// Illegal position: black cannot castle short at the rook is missing.
		illegalFenStrings.add("rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1");
		// Illegal position: white cannot castle long at the rook is missing.
		illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/8 b KQkq - 0 1");
		// Illegal position: pawns at the back rank.
		illegalFenStrings.add("rnbqkbpr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - 0 1");

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

		// The mirrored position must be legal.
		assertEquals(position.isLegal(), true, "Mirrored position is illegal.");

		System.out.println(position);
	}

}
