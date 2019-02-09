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

import org.junit.jupiter.api.Test;

/**
 * MagicUtils test.
 *
 * @author Dieter Dobbelaere
 */
class MagicUtilsTest
{

	/**
	 * Test static methods.
	 */
	@Test
	void testStaticMethods()
	{
		// Instantiate class once to get full test coverage.
		MagicUtils magicUtils = new MagicUtils();

		// Test static methods.
		System.out.println(ChessBoard.getBitboardDebugString(MagicUtils.getRookAttackBitboard(0, 0)));
		System.out.println(ChessBoard.getBitboardDebugString(MagicUtils.getBishopAttackBitboard(0, 0)));
	}

}
