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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chess game.
 *
 * @author Dieter Dobbelaere
 */
public class Game
{
    /**
     * List of moves.
     */
    private List<Move> moves = new ArrayList<>();

    /**
     * List of positions.
     */
    private List<Position> positions = new ArrayList<>();

    /**
     * Create a game from the standard starting position.
     */
    public Game()
    {
        this(Position.STARTING);
    }

    /**
     * Create a game with the given position as starting position.
     *
     * @param position Starting position.
     */
    public Game(Position position)
    {
        // Just add it to the position list, as Position is immutable (from outside the
        // package).
        positions.add(position);
    }

    /**
     * Play the given move.
     *
     * @param move Given move.
     * @throws IllegalMoveException If the given move is illegal.
     */
    public void playMove(Move move)
    {
        // The next statement possibly throws an IllegalMoveException.
        Position nextPosition = getLastPosition().applyMove(move);

        // If we get here, the move is legal.
        // Add the move and position to the lists.
        positions.add(nextPosition);
        moves.add(move);
    }

    /**
     * @return A reference to an unmodifiable view of the list of moves.
     */
    public List<Move> getMoves()
    {
        return Collections.unmodifiableList(moves);
    }

    /**
     * @return A reference to an unmodifiable view of the list of positions.
     */
    public List<Position> getPositions()
    {
        return Collections.unmodifiableList(positions);
    }

    /**
     * @return The last position in the list of positions.
     */
    private Position getLastPosition()
    {
        return positions.get(positions.size() - 1);
    }

    /**
     * @return A list of legal moves that can be played in the last position of the
     *         game.
     */
    public List<Move> getLegalMoves()
    {
        return getLastPosition().getLegalMoves();
    }

    /**
     * @return {@code true} if and only if the last position is a threefold
     *         repetition.
     */
    public boolean isThreefoldRepetition()
    {
        // Count the number of occurrences of the final position.
        int numOccurences = 0;

        // Loop backwards to speed-up detection in case a threefold repetition is
        // present.
        for (int i = positions.size() - 2; i >= 0; i--)
        {
            Position position = positions.get(i);

            if (positions.get(i).equalsIgnoreMoveCounts(getLastPosition()))
            {
                numOccurences++;
            }

            // Short-circuit if we have reached a threefold.
            if (numOccurences >= 2)
            {
                return true;
            }

            // Short-circuit if the move before was zeroing move.
            if (position.getNumNoCaptureOrPawnAdvancePlies() == 0)
            {
                return false;
            }
        }

        // No threefold if we get here.
        return false;
    }
}
