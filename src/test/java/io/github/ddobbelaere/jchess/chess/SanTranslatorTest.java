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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

/**
 * SanTranslator test.
 *
 * @author Dieter Dobbelaere
 */
class SanTranslatorTest
{
    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.SanTranslator#fromSan(String, Position)}.
     */
    @Test
    void testFromSan()
    {
        // Instantiate class once to get full test coverage.
        SanTranslator sanTranslator = new SanTranslator();

        for (Triple<String, Move, Position> testCase : getTestCases())
        {
            assertEquals(testCase.getMiddle(), SanTranslator.fromSan(testCase.getLeft(), testCase.getRight()));
        }

        assertThrows(IllegalMoveException.class, () -> SanTranslator.fromSan("", Position.STARTING));
        assertThrows(IllegalMoveException.class, () -> SanTranslator.fromSan("Qxi0", Position.STARTING));
        assertThrows(IllegalMoveException.class, () -> SanTranslator.fromSan("Qxd1", Position.STARTING));
        assertThrows(IllegalMoveException.class,
                () -> SanTranslator.fromSan("Ne3", Position.fromFen("4k3/8/8/8/2N3N1/8/2N5/4K3 w - -")));
    }

    /**
     * Test method for
     * {@link io.github.ddobbelaere.jchess.chess.SanTranslator#toSan(Move, Position)}.
     */
    @Test
    void testToSan()
    {
        for (Triple<String, Move, Position> testCase : getTestCases())
        {
            assertEquals(testCase.getLeft(), SanTranslator.toSan(testCase.getMiddle(), testCase.getRight()));
        }
    }

    /**
     * @return A list of test cases.
     */
    private static List<Triple<String, Move, Position>> getTestCases()
    {
        return Arrays.asList(Triple.of("d4", new Move("d2d4"), Position.STARTING),
                Triple.of("Nf3", new Move("g1f3"), Position.STARTING),
                Triple.of("Nbd7", new Move("b8d7"),
                        Position.fromFen("rnbqk2r/pp2bppp/2p2n2/3p2B1/3P4/2NBP3/PP3PPP/R2QK1NR b KQkq - 1 7")),
                Triple.of("O-O", Move.SHORT_CASTLING_BLACK,
                        Position.fromFen("r1bqk2r/pp1nbppp/2p2n2/3p2B1/3P4/2NBP3/PPQ2PPP/R3K1NR b KQkq - 3 8")),
                Triple.of("O-O-O", Move.LONG_CASTLING_WHITE,
                        Position.fromFen("r1bqr1k1/pp1nbppp/2p2n2/3p2B1/3P4/2NBP3/PPQ1NPPP/R3K2R w KQ - 6 10")),
                Triple.of("Bxf5", new Move("d7f5"),
                        Position.fromFen("r3rnk1/3bbppp/2p2n2/qp1p1NB1/p2P2P1/3BP2P/PPQ1NP2/1K1R3R b - - 5 16")),
                Triple.of("gxf5", new Move("g4f5"),
                        Position.fromFen("r3rnk1/4bppp/2p2n2/qp1p1bB1/p2P2P1/3BP2P/PPQ1NP2/1K1R3R w - - 0 17")),
                Triple.of("dxe6e.p.", new Move("d5e6"),
                        Position.fromFen("rnbqkbnr/ppp2ppp/3p4/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 3")),
                Triple.of("exf7+", new Move("e6f7"),
                        Position.fromFen("rnb1kbnr/ppp1qppp/3pP3/8/8/8/PPP1PPPP/RNBQKBNR w KQkq - 1 4")),
                Triple.of("fxg8=R", new Move("f7g8R"),
                        Position.fromFen("rnbk1bnr/ppp1qPpp/3p4/8/8/8/PPP1PPPP/RNBQKBNR w KQ - 1 5")),
                Triple.of("fxg8=Q", new Move("f7g8Q"),
                        Position.fromFen("rnbk1bnr/ppp1qPpp/3p4/8/8/8/PPP1PPPP/RNBQKBNR w KQ - 1 5")),
                Triple.of("fxg8=N", new Move("f7g8N"),
                        Position.fromFen("rnbk1bnr/ppp1qPpp/3p4/8/8/8/PPP1PPPP/RNBQKBNR w KQ - 1 5")),
                Triple.of("fxg8=B", new Move("f7g8B"),
                        Position.fromFen("rnbk1bnr/ppp1qPpp/3p4/8/8/8/PPP1PPPP/RNBQKBNR w KQ - 1 5")),
                Triple.of("N2e3", new Move("c2e3"), Position.fromFen("4k3/8/8/8/2N5/8/2N5/4K3 w - -")),
                Triple.of("N4e3", new Move("c4e3"), Position.fromFen("4k3/8/8/8/2N5/8/2N5/4K3 w - -")),
                Triple.of("Nc4e3", new Move("c4e3"), Position.fromFen("4k3/8/8/8/2N3N1/8/2N5/4K3 w - -")),
                Triple.of("Nge3", new Move("g4e3"), Position.fromFen("4k3/8/8/8/2N3N1/8/2N5/4K3 w - -")),
                Triple.of("N2e3", new Move("c2e3"), Position.fromFen("4k3/8/8/8/2N3N1/8/2N5/4K3 w - -")),
                Triple.of("Bxf7#", new Move("c4f7"),
                        Position.fromFen("r1bqkbnr/pppppppp/n7/7Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 5 4")),
                Triple.of("Qxf7#", new Move("h5f7"),
                        Position.fromFen("r1bqkbnr/pppppppp/n7/7Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 5 4")),
                Triple.of("Kf1", new Move("e1f1"),
                        Position.fromFen("r1bqkbnr/pppppppp/n7/7Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 5 4")),
                Triple.of("Rg1", new Move("h1g1"),
                        Position.fromFen("r1bqkbnr/pppp1ppp/n3p3/7Q/2B1P3/5N2/PPPP1PPP/RNB1K2R w KQkq - 0 5")),
                Triple.of("exd3e.p.", new Move("e4d3"),
                        Position.fromFen("rnbqkbnr/pppp1ppp/8/8/3Pp3/2N2N2/PPP1PPPP/R1BQKB1R b KQkq d3 0 3")));
    }

}
