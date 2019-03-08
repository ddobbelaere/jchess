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
 * Position test.
 *
 * @author Dieter Dobbelaere.
 */
class PositionTest
{

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#fromFen(java.lang.String)}.
     */
    @Test
    void testFromFen()
    {
        // Test legal FEN strings.
        for (String legalFenString : getLegalFenStrings())
        {
            Position.fromFen(legalFenString);
        }

        // Test illegal FEN strings.
        for (String illegalFenString : getIllegalFenStrings())
        {
            // Check that the right exception is thrown.
            assertThrows(IllegalFenException.class, () -> Position.fromFen(illegalFenString),
                    "Illegal FEN string \"" + illegalFenString + "\" should cause an exception.");
        }
    }

    /**
     * Test method for {@link io.github.ddobbelaere.jchess.chess.Position#mirror()}.
     */
    @Test
    void testMirror()
    {
        // List legal FEN strings.
        for (String legalFenString : getLegalFenStrings())
        {
            Position position = Position.fromFen(legalFenString);

            // Clear en passant information.
            position.enPassantCaptureSquare = 0;

            // Mirror the position.
            position.mirror();

            // The mirrored position must be legal.
            assertEquals(true, position.isLegal(), "Mirrored position \"" + legalFenString + "\" is illegal.");
        }

        // Test with position where black can no longer castle.
        Position position = Position.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/R3KB1R w KQ - 3 9");
        System.out.printf("Position:%n%n%s%n", position);

        // The mirrored position is from black's perspective.
        position.mirror();
        System.out.printf("Mirrored position:%n%n%s%n", position);

        // Black (we) should not be able to castle.
        assertEquals(false, position.weCanCastleShort || position.weCanCastleLong,
                "Invalid mirrored castling information.");
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#toString()}.
     */
    @Test
    void testToString()
    {
        // Test with all legal FEN strings.
        for (String legalFenString : getLegalFenStrings())
        {
            System.out.printf("FEN string: %s%nPosition string representation:%n%n%s%n", legalFenString,
                    Position.fromFen(legalFenString));
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#getLegalMoves()}.
     */
    @Test
    void testGetLegalMoves()
    {
        List<Move> legalMoves = Position.STARTING.getLegalMoves();

        assertEquals(20, legalMoves.size());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#applyMove()}.
     */
    @Test
    void testApplyMove()
    {
        Position position = Position.STARTING.applyMove(new Move("e2e4"));

        assertThrows(IllegalMoveException.class, () -> Position.STARTING.applyMove(new Move("e2e5")));
    }

    /**
     * Get a list of legal FEN strings.
     *
     * @return List of legal FEN strings.
     */
    private List<String> getLegalFenStrings()
    {
        // Construct the list.
        List<String> legalFenStrings = new ArrayList<>();

        // En passant capture possible.
        legalFenStrings.add("rnbqkb1r/pppp1ppp/5n2/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 3");
        // En passant capture possible (black to move).
        legalFenStrings.add("r1bq1rk1/pp2ppb1/2np1np1/8/3NP1Pp/1BN1BP2/PPPQ3P/R3K2R b KQ g3 0 11");

        // Return the list.
        return legalFenStrings;
    }

    /**
     * Get a list of illegal FEN strings.
     *
     * @return List of illegal FEN strings.
     */
    private List<String> getIllegalFenStrings()
    {
        // Construct the list.
        List<String> illegalFenStrings = new ArrayList<>();

        // No king.
        illegalFenStrings.add("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w - - 0 1");
        illegalFenStrings.add("rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1");
        // Opponent's king is in check.
        illegalFenStrings.add("8/1k4R1/8/8/8/8/6K1/8 w - - 0 1");
        // No castling availability.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w");
        // Illegal character in piece placement string.
        illegalFenStrings.add("rnbqkbnr+pppppppp+8+8+8+8+PPPPPPPP+RNBQKBNR w KQkq - 0 1");
        // Too many rows in piece placement string.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        // Invalid en passant square.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq h9 0 1");
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq h8 0 1");
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq e6 0 1");
        // White cannot castle as the king is not on its original square.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w KQkq - 0 1");
        // Black cannot castle short as the rook is not on its original square.
        illegalFenStrings.add("rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1");
        // White cannot castle long at the rook is not on its original square.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/8 b KQkq - 0 1");
        // Pawns at the back rank.
        illegalFenStrings.add("rnbqkbpr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - 0 1");
        // Illegal number of plies since last capture or pawn advance.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - - 1");
        // Illegal number of game moves.
        illegalFenStrings.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - 0 -");

        // Return the list.
        return illegalFenStrings;
    }

}
