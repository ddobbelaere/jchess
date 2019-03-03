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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

/**
 * ChessMoveGenerator test.
 *
 * @author Dieter Dobbelaere
 */
class ChessMoveGeneratorTest
{

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKingSafety(io.github.ddobbelaere.jchess.chess.ChessPosition)}.
     */
    @Test
    void testGenerateKingSafety()
    {
        // Instantiate class once to get full test coverage.
        ChessMoveGenerator chessMoveGenerator = new ChessMoveGenerator();
        ChessMoveGenerator.KingSafety kingSafety;

        // Perform some sanity checks on the starting position.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.STARTING);

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(0L, kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Check with some more challenging positions.
        // Rook and bishop give double check.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/8/3r2K1/8 w - -"));

        assertEquals(0x40810203800L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
                        | ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"),
                kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(true, kingSafety.isDoubleCheck());

        // No check, but queen is pinned.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(ChessBoard.getSquareBitboard("f3"), kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
                | ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
                | ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
                | ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Knight and rook give double check.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/8/1r4K1/4n3 w - -"));

        assertEquals(0x3E10L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
                | ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("g3")
                | ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(true, kingSafety.isDoubleCheck());

        // Pawn gives check.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/8/8/5p2/6K1/8 w - -"));

        assertEquals(0x200000L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
                        | ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
                        | ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("f3")
                        | ChessBoard.getSquareBitboard("g3") | ChessBoard.getSquareBitboard("h3"),
                kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/2b5/8/4Q3/5Q2/6K1/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("f1") | ChessBoard.getSquareBitboard("g1")
                | ChessBoard.getSquareBitboard("h1") | ChessBoard.getSquareBitboard("f2")
                | ChessBoard.getSquareBitboard("h2") | ChessBoard.getSquareBitboard("g3")
                | ChessBoard.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces, but king movement restricted due to pawn and
        // opponent's king.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/8/8/3p4/8/4K3/8/4k3 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                ChessBoard.getSquareBitboard("d3") | ChessBoard.getSquareBitboard("d4")
                        | ChessBoard.getSquareBitboard("f3") | ChessBoard.getSquareBitboard("f4"),
                kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces, king in the corner.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("7K/1k6/8/8/8/8/8/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("g7") | ChessBoard.getSquareBitboard("h7")
                | ChessBoard.getSquareBitboard("g8"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and
        // f1.
        kingSafety = ChessMoveGenerator.generateKingSafety(
                ChessPosition.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("e2") | ChessBoard.getSquareBitboard("f1"),
                kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces.
        kingSafety = ChessMoveGenerator.generateKingSafety(ChessPosition.fromFen("8/1k6/8/r3pP1K/8/8/8/8 w - e6"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(ChessBoard.getSquareBitboard("g4") | ChessBoard.getSquareBitboard("g5")
                | ChessBoard.getSquareBitboard("g6") | ChessBoard.getSquareBitboard("h4")
                | ChessBoard.getSquareBitboard("h6"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateLegalMoves(io.github.ddobbelaere.jchess.chess.ChessPosition)}.
     */
    @Test
    void testGenerateLegalMoves()
    {
        ChessMoveGenerator.generateLegalMoves(ChessPosition.STARTING);
    }

    /**
     * Check if the reference move list is equal to the generated move list.
     *
     * @param referenceMoves List of reference moves.
     * @param generatedMoves List of generated moves.
     * @param position       Chess position (used for debug messages).
     * @param moveType       String representation of move type (used for debug
     *                       messages).
     */
    private void checkGeneratedMoves(List<ChessMove> referenceMoves, List<ChessMove> generatedMoves,
            ChessPosition position, String moveType)
    {
        // Check if each reference move is present in the move list.
        for (ChessMove referenceMove : referenceMoves)
        {
            assertEquals(true, generatedMoves.contains(referenceMove),
                    "Expected legal " + moveType + " move " + referenceMove + " in position\n" + position);
        }

        // Check if each move is present in the reference move list.
        for (ChessMove generatedMove : generatedMoves)
        {
            assertEquals(true, referenceMoves.contains(generatedMove),
                    "Illegal generated " + moveType + " move " + generatedMove + "  in position\n" + position);
        }

        // Check that there are no duplicate moves.
        assertEquals(generatedMoves.size(), referenceMoves.size(), "Duplicate moves found.");
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKingMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateKingMoves()
    {
        // Add all test positions to a list.
        List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

        // Starting position, no legal king moves.
        testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] {}));

        // Position after 1. e4 e5. The king can move to e2.
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -"),
                new ChessMove[] { new ChessMove("e1e2") }));

        // Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and f1
        // and castle short.
        testCases.add(Pair.of(ChessPosition.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq -"),
                new ChessMove[] { new ChessMove("e1e2"), new ChessMove("e1f1"), new ChessMove("e1g1") }));

        // Position after 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3
        // O-O 8.Qd2 Nc6.
        // The white king can go to e2, f2 and d1 and castle long.
        testCases.add(Pair.of(ChessPosition.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/R3KB1R w KQ - 3 9"),
                new ChessMove[] { new ChessMove("e1e2"), new ChessMove("e1f2"), new ChessMove("e1d1"),
                        new ChessMove("e1c1") }));

        // Test all positions.
        for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
        {
            ChessPosition position = testCase.getLeft();
            List<ChessMove> generatedMoves = ChessMoveGenerator.generateKingMoves(position,
                    ChessMoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "king");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateKnightMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateKnightMoves()
    {
        // Add all test positions to a list.
        List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] { new ChessMove("b1c3"), new ChessMove("b1a3"),
                new ChessMove("g1f3"), new ChessMove("g1h3") }));

        // We are in check and can either capture the queen or interpose to resolve the
        // check.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/1N6/8/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("b4c6"), new ChessMove("b4d5") }));

        // We are in check but cannot resolve it with a knight move.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/8/1N6/6K1/8 w - -"), new ChessMove[] {}));

        // The knight is pinned and cannot move.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/4N3/8/6K1/8 w - -"), new ChessMove[] {}));

        // Test all positions.
        for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
        {
            ChessPosition position = testCase.getLeft();
            List<ChessMove> generatedMoves = ChessMoveGenerator.generateKnightMoves(position,
                    ChessMoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "knight");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateRookMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateRookMoves()
    {
        // Add all test positions to a list.
        List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] {}));

        // Position after 1. a4 d5 2. h4 e5. The a1-rook can move to a2 and a3. The
        // h1-rook can move to h2 and h3.
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/ppp2ppp/8/3pp3/P6P/8/1PPPPPP1/RNBQKBNR w KQkq -"),
                new ChessMove[] { new ChessMove("a1a2"), new ChessMove("a1a3"), new ChessMove("h1h2"),
                        new ChessMove("h1h3") }));

        // We are in check and can only interpose.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4r1/8/8/8/2R5/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("c3g3") }));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4r1/8/8/8/2Q5/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("c3g3") }));

        // The rook (or queen) is pinned by a bishop and cannot move.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2b5/8/8/5R2/6K1/8 w - -"), new ChessMove[] {}));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - -"), new ChessMove[] {}));

        // The rook is pinned by a rook and has to stay on the same line w.r.t. our
        // king.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4r1/8/8/8/6R1/6K1/8 w - -"), new ChessMove[] {
                new ChessMove("g3g4"), new ChessMove("g3g5"), new ChessMove("g3g6"), new ChessMove("g3g7") }));

        // Test all positions.
        for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
        {
            ChessPosition position = testCase.getLeft();
            List<ChessMove> generatedMoves = ChessMoveGenerator.generateRookMoves(position,
                    ChessMoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "rook");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generateBishopMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateBishopMoves()
    {
        // Add all test positions to a list.
        List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(ChessPosition.STARTING, new ChessMove[] {}));

        // Position after 1. e4 e5. The queen on d1 can move to e2, f3, g4 and h5. The
        // bishop on f1 can move to e2, d3, c4, b5 and a6.
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -"),
                new ChessMove[] { new ChessMove("d1e2"), new ChessMove("d1f3"), new ChessMove("d1g4"),
                        new ChessMove("d1h5"), new ChessMove("f1e2"), new ChessMove("f1d3"), new ChessMove("f1c4"),
                        new ChessMove("f1b5"), new ChessMove("f1a6") }));

        // We are in check and can only interpose.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/8/8/2B3K1/8 w - -"),
                new ChessMove[] { new ChessMove("c2e4") }));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/8/8/2Q3K1/8 w - -"),
                new ChessMove[] { new ChessMove("c2e4") }));

        // The bishop (or queen) is pinned by a rook and cannot move.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4r1/8/8/8/6B1/6K1/8 w - -"), new ChessMove[] {}));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4r1/8/8/8/6Q1/6K1/8 w - -"), new ChessMove[] {}));

        // The bishop is pinned by a bishop and has to stay on the same line w.r.t. our
        // king.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2q5/8/8/5B2/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("f3e4"), new ChessMove("f3d5"), new ChessMove("f3c6") }));

        // Test all positions.
        for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
        {
            ChessPosition position = testCase.getLeft();
            List<ChessMove> generatedMoves = ChessMoveGenerator.generateBishopMoves(position,
                    ChessMoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "bishop");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.ChessMoveGenerator#generatePawnMoves(ChessPosition, io.github.ddobbelaere.jchess.chess.ChessMoveGenerator.KingSafety)}.
     */
    @Test
    void testGeneratePawnMoves()
    {
        // Add all test positions to a list.
        List<Pair<ChessPosition, ChessMove[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(ChessPosition.STARTING,
                new ChessMove[] { new ChessMove("a2a3"), new ChessMove("a2a4"), new ChessMove("b2b3"),
                        new ChessMove("b2b4"), new ChessMove("c2c3"), new ChessMove("c2c4"), new ChessMove("d2d3"),
                        new ChessMove("d2d4"), new ChessMove("e2e3"), new ChessMove("e2e4"), new ChessMove("f2f3"),
                        new ChessMove("f2f4"), new ChessMove("g2g3"), new ChessMove("g2g4"), new ChessMove("h2h3"),
                        new ChessMove("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5.
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/ppp2ppp/8/3pp3/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -"),
                new ChessMove[] { new ChessMove("a2a3"), new ChessMove("a2a4"), new ChessMove("b2b3"),
                        new ChessMove("b2b4"), new ChessMove("c4c5"), new ChessMove("c4d5"), new ChessMove("d4e5"),
                        new ChessMove("e2e3"), new ChessMove("e2e4"), new ChessMove("f2f3"), new ChessMove("f2f4"),
                        new ChessMove("g2g3"), new ChessMove("g2g4"), new ChessMove("h2h3"), new ChessMove("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5. 3. c5 b5 (en passant capture to the left
        // possible).
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/p1p2ppp/8/1pPpp3/3P4/8/PP2PPPP/RNBQKBNR w KQkq b6"),
                new ChessMove[] { new ChessMove("a2a3"), new ChessMove("a2a4"), new ChessMove("b2b3"),
                        new ChessMove("b2b4"), new ChessMove("c5c6"), new ChessMove("c5b6"), new ChessMove("d4e5"),
                        new ChessMove("e2e3"), new ChessMove("e2e4"), new ChessMove("f2f3"), new ChessMove("f2f4"),
                        new ChessMove("g2g3"), new ChessMove("g2g4"), new ChessMove("h2h3"), new ChessMove("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5. 3. c5 b5 4. dxe5 f5 (en passant capture to
        // the right
        // possible).
        testCases.add(Pair.of(ChessPosition.fromFen("rnbqkbnr/p1p3pp/8/1pPpPp2/8/8/PP2PPPP/RNBQKBNR w KQkq f6"),
                new ChessMove[] { new ChessMove("a2a3"), new ChessMove("a2a4"), new ChessMove("b2b3"),
                        new ChessMove("b2b4"), new ChessMove("c5c6"), new ChessMove("e5e6"), new ChessMove("e5f6"),
                        new ChessMove("e2e3"), new ChessMove("e2e4"), new ChessMove("f2f3"), new ChessMove("f2f4"),
                        new ChessMove("g2g3"), new ChessMove("g2g4"), new ChessMove("h2h3"), new ChessMove("h2h4") }));

        // Pawn promotion possible on g8.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k4P1/8/8/8/8/6K1/8 w - -"), new ChessMove[] {
                new ChessMove("g7g8B"), new ChessMove("g7g8N"), new ChessMove("g7g8R"), new ChessMove("g7g8Q") }));

        // Pawn promotion possible by capturing on f8 or h8.
        testCases.add(Pair.of(ChessPosition.fromFen("5bRq/1k4P1/8/8/8/8/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("g7f8B"), new ChessMove("g7f8N"), new ChessMove("g7f8R"),
                        new ChessMove("g7f8Q"), new ChessMove("g7h8B"), new ChessMove("g7h8N"), new ChessMove("g7h8R"),
                        new ChessMove("g7h8Q") }));

        // Pawn is pinned and can only move forward.
        testCases.add(Pair.of(ChessPosition.fromFen("6q1/1k6/8/5b1r/6P1/8/6K1/8 w - -"),
                new ChessMove[] { new ChessMove("g4g5") }));

        // Pawn is pinned and can only capture.
        testCases.add(Pair.of(ChessPosition.fromFen("6q1/1k6/8/5b2/6P1/7K/8/8 w - -"),
                new ChessMove[] { new ChessMove("g4f5") }));
        testCases.add(Pair.of(ChessPosition.fromFen("6q1/1k6/8/7b/6P1/5K2/8/8 w - -"),
                new ChessMove[] { new ChessMove("g4h5") }));

        // En passant capture is not possible as it would leave the king check.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/8/r3pP1K/8/8/8/8 w - e6"),
                new ChessMove[] { new ChessMove("f5f6") }));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/8/r4PpK/8/8/8/8 w - g6"),
                new ChessMove[] { new ChessMove("f5f6") }));

        // En passant capture is possible.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/8/r2bpP1K/8/8/8/8 w - e6"),
                new ChessMove[] { new ChessMove("f5f6"), new ChessMove("f5e6") }));
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/8/r2b1PpK/8/8/8/8 w - g6"),
                new ChessMove[] { new ChessMove("f5f6"), new ChessMove("f5g6") }));

        // Our king is in check by an en passant pawn.
        testCases.add(Pair.of(ChessPosition.fromFen("8/3k4/8/3Pp3/3K4/8/8/8 w - e6"),
                new ChessMove[] { new ChessMove("d5e6") }));

        // No pawn moves can resolve the check.
        testCases.add(Pair.of(ChessPosition.fromFen("8/P2k4/1P6/2P4P/4P1P1/3P1P2/8/2q1K3 w - -"), new ChessMove[] {}));

        // We are in check, the checking piece can be interposed or captured.
        testCases.add(Pair.of(ChessPosition.fromFen("8/1k6/2b5/1P6/8/8/P1PPPPKP/8 w - -"),
                new ChessMove[] { new ChessMove("b5c6"), new ChessMove("e2e4"), new ChessMove("f2f3") }));

        // Test all positions.
        for (final Pair<ChessPosition, ChessMove[]> testCase : testCases)
        {
            ChessPosition position = testCase.getLeft();
            List<ChessMove> generatedMoves = ChessMoveGenerator.generatePawnMoves(position,
                    ChessMoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<ChessMove> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "pawn");
        }
    }

}
