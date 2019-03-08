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
 * MoveGenerator test.
 *
 * @author Dieter Dobbelaere
 */
class MoveGeneratorTest
{

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateKingSafety(io.github.ddobbelaere.jchess.chess.Position)}.
     */
    @Test
    void testGenerateKingSafety()
    {
        // Instantiate class once to get full test coverage.
        MoveGenerator chessMoveGenerator = new MoveGenerator();
        MoveGenerator.KingSafety kingSafety;

        // Perform some sanity checks on the starting position.
        kingSafety = MoveGenerator.generateKingSafety(Position.STARTING);

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(0L, kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Check with some more challenging positions.
        // Rook and bishop give double check.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/2b5/8/8/8/3r2K1/8 w - -"));

        assertEquals(0x40810203800L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                Board.getSquareBitboard("f1") | Board.getSquareBitboard("g1")
                        | Board.getSquareBitboard("g3") | Board.getSquareBitboard("h3"),
                kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(true, kingSafety.isDoubleCheck());

        // No check, but queen is pinned.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(Board.getSquareBitboard("f3"), kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("f1") | Board.getSquareBitboard("g1")
                | Board.getSquareBitboard("h1") | Board.getSquareBitboard("f2")
                | Board.getSquareBitboard("h2") | Board.getSquareBitboard("g3")
                | Board.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Knight and rook give double check.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/8/8/8/8/1r4K1/4n3 w - -"));

        assertEquals(0x3E10L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("f1") | Board.getSquareBitboard("g1")
                | Board.getSquareBitboard("h1") | Board.getSquareBitboard("g3")
                | Board.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(true, kingSafety.isDoubleCheck());

        // Pawn gives check.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/8/8/8/5p2/6K1/8 w - -"));

        assertEquals(0x200000L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                Board.getSquareBitboard("f1") | Board.getSquareBitboard("g1")
                        | Board.getSquareBitboard("h1") | Board.getSquareBitboard("f2")
                        | Board.getSquareBitboard("h2") | Board.getSquareBitboard("f3")
                        | Board.getSquareBitboard("g3") | Board.getSquareBitboard("h3"),
                kingSafety.accessibleSquares);
        assertEquals(true, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/2b5/8/4Q3/5Q2/6K1/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("f1") | Board.getSquareBitboard("g1")
                | Board.getSquareBitboard("h1") | Board.getSquareBitboard("f2")
                | Board.getSquareBitboard("h2") | Board.getSquareBitboard("g3")
                | Board.getSquareBitboard("h3"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces, but king movement restricted due to pawn and
        // opponent's king.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/8/8/3p4/8/4K3/8/4k3 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(
                Board.getSquareBitboard("d3") | Board.getSquareBitboard("d4")
                        | Board.getSquareBitboard("f3") | Board.getSquareBitboard("f4"),
                kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces, king in the corner.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("7K/1k6/8/8/8/8/8/8 w - -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("g7") | Board.getSquareBitboard("h7")
                | Board.getSquareBitboard("g8"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and
        // f1.
        kingSafety = MoveGenerator.generateKingSafety(
                Position.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq -"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("e2") | Board.getSquareBitboard("f1"),
                kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());

        // No checks and no pinned pieces.
        kingSafety = MoveGenerator.generateKingSafety(Position.fromFen("8/1k6/8/r3pP1K/8/8/8/8 w - e6"));

        assertEquals(0L, kingSafety.attackLines);
        assertEquals(0L, kingSafety.pinnedPieces);
        assertEquals(Board.getSquareBitboard("g4") | Board.getSquareBitboard("g5")
                | Board.getSquareBitboard("g6") | Board.getSquareBitboard("h4")
                | Board.getSquareBitboard("h6"), kingSafety.accessibleSquares);
        assertEquals(false, kingSafety.isCheck());
        assertEquals(false, kingSafety.isDoubleCheck());
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateLegalMoves(io.github.ddobbelaere.jchess.chess.Position)}.
     */
    @Test
    void testGenerateLegalMoves()
    {
        MoveGenerator.generateLegalMoves(Position.STARTING);
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
    private void checkGeneratedMoves(List<Move> referenceMoves, List<Move> generatedMoves,
            Position position, String moveType)
    {
        // Check if each reference move is present in the move list.
        for (Move referenceMove : referenceMoves)
        {
            assertEquals(true, generatedMoves.contains(referenceMove),
                    "Expected legal " + moveType + " move " + referenceMove + " in position\n" + position);
        }

        // Check if each move is present in the reference move list.
        for (Move generatedMove : generatedMoves)
        {
            assertEquals(true, referenceMoves.contains(generatedMove),
                    "Illegal generated " + moveType + " move " + generatedMove + "  in position\n" + position);
        }

        // Check that there are no duplicate moves.
        assertEquals(generatedMoves.size(), referenceMoves.size(), "Duplicate moves found.");
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateKingMoves(Position, io.github.ddobbelaere.jchess.chess.MoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateKingMoves()
    {
        // Add all test positions to a list.
        List<Pair<Position, Move[]>> testCases = new ArrayList<>();

        // Starting position, no legal king moves.
        testCases.add(Pair.of(Position.STARTING, new Move[] {}));

        // Position after 1. e4 e5. The king can move to e2.
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -"),
                new Move[] { new Move("e1e2") }));

        // Position after 1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6. The king can move to e2 and f1
        // and castle short.
        testCases.add(Pair.of(Position.fromFen("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq -"),
                new Move[] { new Move("e1e2"), new Move("e1f1"), new Move("e1g1") }));

        // Position after 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6 6.Be3 Bg7 7.f3
        // O-O 8.Qd2 Nc6.
        // The white king can go to e2, f2 and d1 and castle long.
        testCases.add(Pair.of(Position.fromFen("r1bq1rk1/pp2ppbp/2np1np1/8/3NP3/2N1BP2/PPPQ2PP/R3KB1R w KQ - 3 9"),
                new Move[] { new Move("e1e2"), new Move("e1f2"), new Move("e1d1"),
                        new Move("e1c1") }));

        // Test all positions.
        for (final Pair<Position, Move[]> testCase : testCases)
        {
            Position position = testCase.getLeft();
            List<Move> generatedMoves = MoveGenerator.generateKingMoves(position,
                    MoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<Move> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "king");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateKnightMoves(Position, io.github.ddobbelaere.jchess.chess.MoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateKnightMoves()
    {
        // Add all test positions to a list.
        List<Pair<Position, Move[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(Position.STARTING, new Move[] { new Move("b1c3"), new Move("b1a3"),
                new Move("g1f3"), new Move("g1h3") }));

        // We are in check and can either capture the queen or interpose to resolve the
        // check.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/1N6/8/6K1/8 w - -"),
                new Move[] { new Move("b4c6"), new Move("b4d5") }));

        // We are in check but cannot resolve it with a knight move.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/8/1N6/6K1/8 w - -"), new Move[] {}));

        // The knight is pinned and cannot move.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/4N3/8/6K1/8 w - -"), new Move[] {}));

        // Test all positions.
        for (final Pair<Position, Move[]> testCase : testCases)
        {
            Position position = testCase.getLeft();
            List<Move> generatedMoves = MoveGenerator.generateKnightMoves(position,
                    MoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<Move> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "knight");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateRookMoves(Position, io.github.ddobbelaere.jchess.chess.MoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateRookMoves()
    {
        // Add all test positions to a list.
        List<Pair<Position, Move[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(Position.STARTING, new Move[] {}));

        // Position after 1. a4 d5 2. h4 e5. The a1-rook can move to a2 and a3. The
        // h1-rook can move to h2 and h3.
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/ppp2ppp/8/3pp3/P6P/8/1PPPPPP1/RNBQKBNR w KQkq -"),
                new Move[] { new Move("a1a2"), new Move("a1a3"), new Move("h1h2"),
                        new Move("h1h3") }));

        // We are in check and can only interpose.
        testCases.add(Pair.of(Position.fromFen("8/1k4r1/8/8/8/2R5/6K1/8 w - -"),
                new Move[] { new Move("c3g3") }));
        testCases.add(Pair.of(Position.fromFen("8/1k4r1/8/8/8/2Q5/6K1/8 w - -"),
                new Move[] { new Move("c3g3") }));

        // The rook (or queen) is pinned by a bishop and cannot move.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2b5/8/8/5R2/6K1/8 w - -"), new Move[] {}));
        testCases.add(Pair.of(Position.fromFen("8/1k6/2b5/8/8/5Q2/6K1/8 w - -"), new Move[] {}));

        // The rook is pinned by a rook and has to stay on the same line w.r.t. our
        // king.
        testCases.add(Pair.of(Position.fromFen("8/1k4r1/8/8/8/6R1/6K1/8 w - -"), new Move[] {
                new Move("g3g4"), new Move("g3g5"), new Move("g3g6"), new Move("g3g7") }));

        // Test all positions.
        for (final Pair<Position, Move[]> testCase : testCases)
        {
            Position position = testCase.getLeft();
            List<Move> generatedMoves = MoveGenerator.generateRookMoves(position,
                    MoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<Move> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "rook");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generateBishopMoves(Position, io.github.ddobbelaere.jchess.chess.MoveGenerator.KingSafety)}.
     */
    @Test
    void testGenerateBishopMoves()
    {
        // Add all test positions to a list.
        List<Pair<Position, Move[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(Position.STARTING, new Move[] {}));

        // Position after 1. e4 e5. The queen on d1 can move to e2, f3, g4 and h5. The
        // bishop on f1 can move to e2, d3, c4, b5 and a6.
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -"),
                new Move[] { new Move("d1e2"), new Move("d1f3"), new Move("d1g4"),
                        new Move("d1h5"), new Move("f1e2"), new Move("f1d3"), new Move("f1c4"),
                        new Move("f1b5"), new Move("f1a6") }));

        // We are in check and can only interpose.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/8/8/2B3K1/8 w - -"),
                new Move[] { new Move("c2e4") }));
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/8/8/2Q3K1/8 w - -"),
                new Move[] { new Move("c2e4") }));

        // The bishop (or queen) is pinned by a rook and cannot move.
        testCases.add(Pair.of(Position.fromFen("8/1k4r1/8/8/8/6B1/6K1/8 w - -"), new Move[] {}));
        testCases.add(Pair.of(Position.fromFen("8/1k4r1/8/8/8/6Q1/6K1/8 w - -"), new Move[] {}));

        // The bishop is pinned by a bishop and has to stay on the same line w.r.t. our
        // king.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2q5/8/8/5B2/6K1/8 w - -"),
                new Move[] { new Move("f3e4"), new Move("f3d5"), new Move("f3c6") }));

        // Test all positions.
        for (final Pair<Position, Move[]> testCase : testCases)
        {
            Position position = testCase.getLeft();
            List<Move> generatedMoves = MoveGenerator.generateBishopMoves(position,
                    MoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<Move> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "bishop");
        }
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.MoveGenerator#generatePawnMoves(Position, io.github.ddobbelaere.jchess.chess.MoveGenerator.KingSafety)}.
     */
    @Test
    void testGeneratePawnMoves()
    {
        // Add all test positions to a list.
        List<Pair<Position, Move[]>> testCases = new ArrayList<>();

        // Starting position.
        testCases.add(Pair.of(Position.STARTING,
                new Move[] { new Move("a2a3"), new Move("a2a4"), new Move("b2b3"),
                        new Move("b2b4"), new Move("c2c3"), new Move("c2c4"), new Move("d2d3"),
                        new Move("d2d4"), new Move("e2e3"), new Move("e2e4"), new Move("f2f3"),
                        new Move("f2f4"), new Move("g2g3"), new Move("g2g4"), new Move("h2h3"),
                        new Move("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5.
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/ppp2ppp/8/3pp3/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -"),
                new Move[] { new Move("a2a3"), new Move("a2a4"), new Move("b2b3"),
                        new Move("b2b4"), new Move("c4c5"), new Move("c4d5"), new Move("d4e5"),
                        new Move("e2e3"), new Move("e2e4"), new Move("f2f3"), new Move("f2f4"),
                        new Move("g2g3"), new Move("g2g4"), new Move("h2h3"), new Move("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5. 3. c5 b5 (en passant capture to the left
        // possible).
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/p1p2ppp/8/1pPpp3/3P4/8/PP2PPPP/RNBQKBNR w KQkq b6"),
                new Move[] { new Move("a2a3"), new Move("a2a4"), new Move("b2b3"),
                        new Move("b2b4"), new Move("c5c6"), new Move("c5b6"), new Move("d4e5"),
                        new Move("e2e3"), new Move("e2e4"), new Move("f2f3"), new Move("f2f4"),
                        new Move("g2g3"), new Move("g2g4"), new Move("h2h3"), new Move("h2h4") }));

        // Position after 1. d4 d5 2. c4 e5. 3. c5 b5 4. dxe5 f5 (en passant capture to
        // the right
        // possible).
        testCases.add(Pair.of(Position.fromFen("rnbqkbnr/p1p3pp/8/1pPpPp2/8/8/PP2PPPP/RNBQKBNR w KQkq f6"),
                new Move[] { new Move("a2a3"), new Move("a2a4"), new Move("b2b3"),
                        new Move("b2b4"), new Move("c5c6"), new Move("e5e6"), new Move("e5f6"),
                        new Move("e2e3"), new Move("e2e4"), new Move("f2f3"), new Move("f2f4"),
                        new Move("g2g3"), new Move("g2g4"), new Move("h2h3"), new Move("h2h4") }));

        // Pawn promotion possible on g8.
        testCases.add(Pair.of(Position.fromFen("8/1k4P1/8/8/8/8/6K1/8 w - -"), new Move[] {
                new Move("g7g8B"), new Move("g7g8N"), new Move("g7g8R"), new Move("g7g8Q") }));

        // Pawn promotion possible by capturing on f8 or h8.
        testCases.add(Pair.of(Position.fromFen("5bRq/1k4P1/8/8/8/8/6K1/8 w - -"),
                new Move[] { new Move("g7f8B"), new Move("g7f8N"), new Move("g7f8R"),
                        new Move("g7f8Q"), new Move("g7h8B"), new Move("g7h8N"), new Move("g7h8R"),
                        new Move("g7h8Q") }));

        // Pawn is pinned and can only move forward.
        testCases.add(Pair.of(Position.fromFen("6q1/1k6/8/5b1r/6P1/8/6K1/8 w - -"),
                new Move[] { new Move("g4g5") }));

        // Pawn is pinned and can only capture.
        testCases.add(Pair.of(Position.fromFen("6q1/1k6/8/5b2/6P1/7K/8/8 w - -"),
                new Move[] { new Move("g4f5") }));
        testCases.add(Pair.of(Position.fromFen("6q1/1k6/8/7b/6P1/5K2/8/8 w - -"),
                new Move[] { new Move("g4h5") }));

        // En passant capture is not possible as it would leave the king check.
        testCases.add(Pair.of(Position.fromFen("8/1k6/8/r3pP1K/8/8/8/8 w - e6"),
                new Move[] { new Move("f5f6") }));
        testCases.add(Pair.of(Position.fromFen("8/1k6/8/r4PpK/8/8/8/8 w - g6"),
                new Move[] { new Move("f5f6") }));

        // En passant capture is possible.
        testCases.add(Pair.of(Position.fromFen("8/1k6/8/r2bpP1K/8/8/8/8 w - e6"),
                new Move[] { new Move("f5f6"), new Move("f5e6") }));
        testCases.add(Pair.of(Position.fromFen("8/1k6/8/r2b1PpK/8/8/8/8 w - g6"),
                new Move[] { new Move("f5f6"), new Move("f5g6") }));

        // Our king is in check by an en passant pawn.
        testCases.add(Pair.of(Position.fromFen("8/3k4/8/3Pp3/3K4/8/8/8 w - e6"),
                new Move[] { new Move("d5e6") }));

        // No pawn moves can resolve the check.
        testCases.add(Pair.of(Position.fromFen("8/P2k4/1P6/2P4P/4P1P1/3P1P2/8/2q1K3 w - -"), new Move[] {}));

        // We are in check, the checking piece can be interposed or captured.
        testCases.add(Pair.of(Position.fromFen("8/1k6/2b5/1P6/8/8/P1PPPPKP/8 w - -"),
                new Move[] { new Move("b5c6"), new Move("e2e4"), new Move("f2f3") }));

        // Test all positions.
        for (final Pair<Position, Move[]> testCase : testCases)
        {
            Position position = testCase.getLeft();
            List<Move> generatedMoves = MoveGenerator.generatePawnMoves(position,
                    MoveGenerator.generateKingSafety(position));

            // Compare with reference move list.
            List<Move> referenceMoves = Arrays.asList(testCase.getRight());

            // Check generated move list.
            checkGeneratedMoves(referenceMoves, generatedMoves, position, "pawn");
        }
    }

}
