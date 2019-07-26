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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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
     * Test method for {@link io.github.ddobbelaere.jchess.chess.Position#getFen()}.
     */
    @Test
    void testGetFen()
    {
        // Test legal FEN strings.
        for (String legalFenString : getLegalFenStrings())
        {
            assertEquals(legalFenString, Position.fromFen(legalFenString).getFen());
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
        // Construct a list of move sequences to be tested.
        List<List<Pair<Position, Move>>> moveSequences = new ArrayList<>();

        // First move sequence.
        List<Pair<Position, Move>> moveSequence = Arrays.asList(Pair.of(Position.STARTING, new Move("e2e4")),
                Pair.of(Position.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1"),
                        new Move("c7c5")),
                Pair.of(Position.fromFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2"),
                        new Move("e4e5")),
                Pair.of(Position.fromFen("rnbqkbnr/pp1ppppp/8/2p1P3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2"),
                        new Move("d7d5")),
                Pair.of(Position.fromFen("rnbqkbnr/pp2pppp/8/2ppP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3"),
                        new Move("e5d6")),
                Pair.of(Position.fromFen("rnbqkbnr/pp2pppp/3P4/2p5/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3"),
                        new Move("c8f5")),
                Pair.of(Position.fromFen("rn1qkbnr/pp2pppp/3P4/2p2b2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 4"),
                        new Move("d6e7")),
                Pair.of(Position.fromFen("rn1qkbnr/pp2Pppp/8/2p2b2/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 4"),
                        new Move("b8c6")),
                Pair.of(Position.fromFen("r2qkbnr/pp2Pppp/2n5/2p2b2/8/8/PPPP1PPP/RNBQKBNR w KQkq - 1 5"),
                        new Move("e7d8Q")),
                Pair.of(Position.fromFen("r2Qkbnr/pp3ppp/2n5/2p2b2/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 5"),
                        new Move("a8d8")),
                Pair.of(Position.fromFen("3rkbnr/pp3ppp/2n5/2p2b2/8/8/PPPP1PPP/RNBQKBNR w KQk - 0 6"),
                        new Move("g1f3")),
                Pair.of(Position.fromFen("3rkbnr/pp3ppp/2n5/2p2b2/8/5N2/PPPP1PPP/RNBQKB1R b KQk - 1 6"),
                        new Move("h7h5")),
                Pair.of(Position.fromFen("3rkbnr/pp3pp1/2n5/2p2b1p/8/5N2/PPPP1PPP/RNBQKB1R w KQk - 0 7"),
                        new Move("f1d3")),
                Pair.of(Position.fromFen("3rkbnr/pp3pp1/2n5/2p2b1p/8/3B1N2/PPPP1PPP/RNBQK2R b KQk - 1 7"),
                        new Move("h8h6")),
                Pair.of(Position.fromFen("3rkbn1/pp3pp1/2n4r/2p2b1p/8/3B1N2/PPPP1PPP/RNBQK2R w KQ - 2 8"),
                        new Move("e1g1")),
                Pair.of(Position.fromFen("3rkbn1/pp3pp1/2n4r/2p2b1p/8/3B1N2/PPPP1PPP/RNBQ1RK1 b - - 3 8"),
                        new Move("e8e7")),
                Pair.of(Position.fromFen("3r1bn1/pp2kpp1/2n4r/2p2b1p/8/3B1N2/PPPP1PPP/RNBQ1RK1 w - - 4 9"), null));

        moveSequences.add(moveSequence);

        // Test long castling.
        moveSequence = Arrays.asList(
                Pair.of(Position.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/R3KB1R w KQ - 3 9"),
                        new Move("e1c1")),
                Pair.of(Position.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/2KR1B1R b - - 4 9"), null));

        moveSequences.add(moveSequence);

        // Test promotions to bishop, rook and knight (queen was already check in first
        // move sequence).
        moveSequence = Arrays.asList(Pair.of(Position.fromFen("8/1k1PP3/8/8/8/8/3p2K1/8 w - - 4 9"), new Move("d7d8R")),
                Pair.of(Position.fromFen("3R4/1k2P3/8/8/8/8/3p2K1/8 b - - 0 9"), new Move("d2d1N")),
                Pair.of(Position.fromFen("3R4/1k2P3/8/8/8/8/6K1/3n4 w - - 0 10"), new Move("e7e8B")),
                Pair.of(Position.fromFen("3RB3/1k6/8/8/8/8/6K1/3n4 b - - 0 10"), null));

        moveSequences.add(moveSequence);

        // Test loss of opponent's castling rights because a piece captures the rook on
        // a8 or h8.
        moveSequence = Arrays.asList(
                Pair.of(Position.fromFen("rnbqkbnr/pppppppp/1N4N1/8/8/8/PPPPPPPP/R1BQKB1R w KQkq - 0 1"),
                        new Move("b6a8")),
                Pair.of(Position.fromFen("Nnbqkbnr/pppppppp/6N1/8/8/8/PPPPPPPP/R1BQKB1R b KQk - 0 1"),
                        new Move("e7e5")),
                Pair.of(Position.fromFen("Nnbqkbnr/pppp1ppp/6N1/4p3/8/8/PPPPPPPP/R1BQKB1R w KQk - 0 2"),
                        new Move("g6h8")),
                Pair.of(Position.fromFen("NnbqkbnN/pppp1ppp/8/4p3/8/8/PPPPPPPP/R1BQKB1R b KQ - 0 2"), null));

        moveSequences.add(moveSequence);

        // Test all move sequences.
        for (List<Pair<Position, Move>> moveSequenceUnderTest : moveSequences)
        {
            Position position = null;
            Move prevMove = null;

            for (Pair<Position, Move> moveUnderTest : moveSequenceUnderTest)
            {
                if (position != null)
                {
                    // Test if the position is equal to the asserted one.
                    assertEquals(moveUnderTest.getLeft(), position, "Expected position\n" + moveUnderTest.getLeft()
                            + "\nafter move " + prevMove + " but got\n" + position);
                }

                if (position == null)
                {
                    // This is the starting position of the move sequence.
                    position = moveUnderTest.getLeft();
                }

                if (moveUnderTest.getRight() != null)
                {
                    position = position.applyMove(moveUnderTest.getRight());
                }

                prevMove = moveUnderTest.getRight();
            }
        }

        // Check that an illegal move throws an exception.
        assertThrows(IllegalMoveException.class, () -> Position.STARTING.applyMove(new Move("e2e5")));

        // Test applyMove with SAN string.
        Position.STARTING.applyMove("e4");
    }

    /**
     * Test method for getters.
     */
    @Test
    void testGetters()
    {
        Position position = Position.fromFen("r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R3K2R w Kq - 10 20");

        assertEquals(10, position.getNumNoCaptureOrPawnAdvancePlies());
        assertEquals(20, position.getMoveNumber());
        assertEquals(true, position.isWhiteToMove());
        assertEquals(false, position.isBlackToMove());

        assertEquals(true, position.whiteCanCastleShort());
        assertEquals(false, position.whiteCanCastleLong());
        assertEquals(false, position.blackCanCastleShort());
        assertEquals(true, position.blackCanCastleLong());

        assertEquals(false, position.isCheck());
        assertEquals(false, position.isCheckmate());
        assertEquals(false, position.isStalemate());

        position = Position.fromFen("r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R3K2R b Kq - 10 20");

        assertEquals(true, position.whiteCanCastleShort());
        assertEquals(false, position.whiteCanCastleLong());
        assertEquals(false, position.blackCanCastleShort());
        assertEquals(true, position.blackCanCastleLong());

        position = Position.fromFen("6rk/5Npp/8/8/8/8/8/6K1 b - - 0 2");

        assertEquals(true, position.isCheck());
        assertEquals(true, position.isCheckmate());
        assertEquals(false, position.isStalemate());

        position = Position.fromFen("3Q4/pk6/p7/P2P4/8/8/6K1/8 b - - 0 2");

        assertEquals(false, position.isCheck());
        assertEquals(false, position.isCheckmate());
        assertEquals(true, position.isStalemate());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#equalsIgnoreMoveCounts(Position)}.
     */
    @Test
    void testEqualsIgnoreMoveCounts()
    {
        Position position = Position.STARTING;

        assertEquals(true, position.equalsIgnoreMoveCounts(Position.STARTING));
        assertEquals(true, position.equalsIgnoreMoveCounts(
                Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 20 3")));
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.Position#equals(Object)}.
     */
    @Test
    void testEquals()
    {
        Position position = Position.STARTING;

        assertEquals(true, position.equals(Position.STARTING));
        assertEquals(false, position.equals(null));
        assertEquals(false, position.equals(new Object()));
        assertEquals(true,
                position.equals(Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")));
    }

    /**
     * Get a list of legal FEN strings.
     *
     * @return List of legal FEN strings.
     */
    private List<String> getLegalFenStrings()
    {
        // Construct and return the list.
        return Arrays.asList(
                // En passant capture possible.
                "rnbqkb1r/pppp1ppp/5n2/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 3",

                // En passant capture possible (black to move).
                "r1bq1rk1/pp2ppb1/2np1np1/8/3NP1Pp/1BN1BP2/PPPQ3P/R3K2R b KQ g3 0 11",

                // No castling and en passant capture possible.
                "r1bq1rk1/pp2ppb1/2np1np1/8/3NP1Pp/1BN1BP2/PPPQ3P/R3K2R b - - 3 20");
    }

    /**
     * Get a list of illegal FEN strings.
     *
     * @return List of illegal FEN strings.
     */
    private List<String> getIllegalFenStrings()
    {
        // Construct and return the list.
        return Arrays.asList(
                // No king.
                "rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR w - - 0 1",
                "rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1",

                // Opponent's king is in check.
                "8/1k4R1/8/8/8/8/6K1/8 w - - 0 1",

                // No castling availability.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w",

                // Illegal character in piece placement string.
                "rnbqkbnr+pppppppp+8+8+8+8+PPPPPPPP+RNBQKBNR w KQkq - 0 1",

                // Too many rows in piece placement string.
                "rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",

                // Invalid en passant square.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq h9 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq h8 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq e6 0 1",

                // White cannot castle as the king is not on its original square.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w KQkq - 0 1",

                // Black cannot castle short as the rook is not on its original square.
                "rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 w KQkq - 0 1",

                // White cannot castle long at the rook is not on its original square.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/8 b KQkq - 0 1",

                // Pawns at the back rank.
                "rnbqkbpr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - 0 1",

                // Illegal number of plies since last capture or pawn advance.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - - 1",

                // Illegal number of game moves.
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/8 b KQkq - 0 -");
    }

}
