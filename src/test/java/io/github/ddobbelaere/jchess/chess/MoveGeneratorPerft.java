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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

/**
 * MoveGenerator performance test.
 *
 * @author Dieter Dobbelaere
 */
class MoveGeneratorPerft
{
    /**
     * Generate all positions up to the given maximum depth from the given starting
     * position.
     *
     * @param position Given starting position.
     * @param maxDepth Maximum depth.
     * @return Number of generated "leaf" positions.
     */
    static long Perft(Position position, int maxDepth)
    {
        return Perft(position, maxDepth, 0);
    }

    /**
     * Generate all positions up to the given maximum depth from the given starting
     * position and current depth.
     *
     * @param position     Given starting position.
     * @param maxDepth     Maximum depth.
     * @param currentDepth Current depth.
     * @return Number of generated "leaf" positions.
     */
    static long Perft(Position position, int maxDepth, int currentDepth)
    {
        // If we are at maximum depth, this is a leaf position.
        if (currentDepth == maxDepth)
        {
            return 1;
        }

        // Generate all legal moves from the starting position.
        List<Move> legalMoves = position.getLegalMoves();

        long numPositions = 0;

        // Get the number of position resulting from this move.
        if (currentDepth == 0)
        {
            // Parallelize at depth 0.
            List<Callable<Long>> taskList = new ArrayList<>();
            ExecutorService execService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (Move move : legalMoves)
            {
                taskList.add(new Callable<Long>()
                {

                    @Override
                    public Long call() throws Exception
                    {
                        return Perft(position.applyMove(move), maxDepth, currentDepth + 1);
                    }
                });
            }

            List<Future<Long>> resultList;
            try
            {
                resultList = execService.invokeAll(taskList);

                for (Future<Long> result : resultList)
                {
                    numPositions += result.get();
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            for (Move move : legalMoves)
            {
                Position resultingPosition = position.applyMove(move);
                // assertEquals(true, resultingPosition.isLegal(), "Illegal position\n" +
                // resultingPosition
                // + "found after move " + move + " on position\n" + position);
                numPositions += Perft(resultingPosition, maxDepth, currentDepth + 1);
            }
        }

        return numPositions;

    }

    /**
     * Test starting position.
     */
    @Test
    void testStartingPosition()
    {
        Position position = Position.STARTING;

        assertEquals(1, Perft(position, 0));
        assertEquals(20, Perft(position, 1));
        assertEquals(400, Perft(position, 2));
        assertEquals(8902, Perft(position, 3));
        assertEquals(197281, Perft(position, 4));
        assertEquals(4865609, Perft(position, 5));
        // assertEquals(119060324, Perft(position, 6));
        // assertEquals(3195901860L, Perft(position, 7));
    }

    /**
     * Test position 2 (aka Kiwipete) from <a href=
     * "https://www.chessprogramming.org/Perft_Results">https://www.chessprogramming.org/Perft_Results</a>.
     */
    @Test
    void testKiwipete()
    {
        Position position = Position.fromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");

        assertEquals(48, Perft(position, 1));
        assertEquals(2039, Perft(position, 2));
        assertEquals(97862, Perft(position, 3));
        assertEquals(4085603, Perft(position, 4));
        // assertEquals(193690690, Perft(position, 5));
        // assertEquals(8031647685L, Perft(position, 6));
    }

    /**
     * Test position 3 from <a href=
     * "https://www.chessprogramming.org/Perft_Results">https://www.chessprogramming.org/Perft_Results</a>.
     */
    @Test
    void testPosition3()
    {
        Position position = Position.fromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");

        assertEquals(14, Perft(position, 1));
        assertEquals(191, Perft(position, 2));
        assertEquals(2812, Perft(position, 3));
        assertEquals(43238, Perft(position, 4));
        assertEquals(674624, Perft(position, 5));
        // assertEquals(11030083, Perft(position, 6));
        // assertEquals(178633661, Perft(position, 7));
        // assertEquals(3009794393L, Perft(position, 8));
    }

    /**
     * Test position 4 from <a href=
     * "https://www.chessprogramming.org/Perft_Results">https://www.chessprogramming.org/Perft_Results</a>.
     */
    @Test
    void testPosition4()
    {
        Position position = Position.fromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        assertEquals(6, Perft(position, 1));
        assertEquals(264, Perft(position, 2));
        assertEquals(9467, Perft(position, 3));
        assertEquals(422333, Perft(position, 4));
        // assertEquals(15833292, Perft(position, 5));
        // assertEquals(706045033, Perft(position, 6));
    }

    /**
     * Test position 5 from <a href=
     * "https://www.chessprogramming.org/Perft_Results">https://www.chessprogramming.org/Perft_Results</a>.
     */
    @Test
    void testPosition5()
    {
        Position position = Position.fromFen("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        assertEquals(44, Perft(position, 1));
        assertEquals(1486, Perft(position, 2));
        assertEquals(62379, Perft(position, 3));
        assertEquals(2103487, Perft(position, 4));
        // assertEquals(89941194, Perft(position, 5));
    }

    /**
     * Test position 6 from <a href=
     * "https://www.chessprogramming.org/Perft_Results">https://www.chessprogramming.org/Perft_Results</a>.
     */
    @Test
    void testPosition6()
    {
        Position position = Position
                .fromFen("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

        assertEquals(1, Perft(position, 0));
        assertEquals(46, Perft(position, 1));
        assertEquals(2079, Perft(position, 2));
        assertEquals(89890, Perft(position, 3));
        assertEquals(3894594, Perft(position, 4));
        // assertEquals(164075551, Perft(position, 5));
        // assertEquals(6923051137L, Perft(position, 6));
    }

    public static void main(String[] args)
    {
        // Do a performance test on starting position.
        long totalNumPositions = 0;
        for (int i = 0; i <= 6; i++)
        {
            // Count number of positions at depth i.
            long startTime = System.nanoTime();
            long numPositions = Perft(Position.STARTING, i);
            long endTime = System.nanoTime();

            // Add to total number of positions.
            totalNumPositions += numPositions;

            System.out.printf("Perft(%d) = %d (%f s, %f nps)%n", i, numPositions, 1e-9 * (endTime - startTime),
                    totalNumPositions / (1e-9 * (endTime - startTime)));
        }
    }

}
