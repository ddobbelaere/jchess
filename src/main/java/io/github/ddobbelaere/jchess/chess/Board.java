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

/**
 * Chess board holding piece placement.
 *
 * @author Dieter Dobbelaere
 */
class Board
{
    /**
     * Active player's pieces bitboard.
     */
    long ourPieces;

    /**
     * Opponent's pieces bitboard.
     */
    long theirPieces;

    /**
     * Pawns bitboard.
     */
    long pawns;

    /**
     * Rooks bitboard (active for rooks and queens).
     */
    long rooks;

    /**
     * Bishops bitboard (active for bishops and queens).
     */
    long bishops;

    /**
     * Kings bitboard (active for kings).
     */
    long kings;

    /**
     * The board is mirrored (black to move).
     */
    boolean isMirrored;

    /**
     * Bitboard representation of the first rank (aka row 0).
     */
    static final long BB_A1H1 = 0xFFL;

    /**
     * Bitboard representation of the fourth rank (aka row 3).
     */
    static final long BB_A4H4 = 0xFF000000L;

    /**
     * Bitboard representation of the a-file (aka column 0).
     */
    static final long BB_A1A8 = 0x0101010101010101L;

    /**
     * Bitboard representation of the a1-square.
     */
    static final long BB_A1 = getSquareBitboard("a1");

    /**
     * Bitboard representation of the c1-square.
     */
    static final long BB_C1 = getSquareBitboard("c1");

    /**
     * Bitboard representation of the d1-square.
     */
    static final long BB_D1 = getSquareBitboard("d1");

    /**
     * Bitboard representation of the f1-square.
     */
    static final long BB_F1 = getSquareBitboard("f1");

    /**
     * Bitboard representation of the g1-square.
     */
    static final long BB_G1 = getSquareBitboard("g1");

    /**
     * Bitboard representation of the h1-square.
     */
    static final long BB_H1 = getSquareBitboard("h1");

    /**
     * Square representation of the c1-square.
     */
    static final int SQUARE_C1 = getSquare("c1");

    /**
     * Square representation of the e1-square.
     */
    static final int SQUARE_E1 = getSquare("e1");

    /**
     * Square representation of the g1-square.
     */
    static final int SQUARE_G1 = getSquare("g1");

    /**
     * Default constructor.
     */
    Board()
    {

    }

    /**
     * Copy constructor.
     *
     * @param board Board that is to be copied.
     */
    Board(Board board)
    {
        // Copy all the fields.
        ourPieces = board.ourPieces;
        theirPieces = board.theirPieces;
        pawns = board.pawns;
        rooks = board.rooks;
        bishops = board.bishops;
        kings = board.kings;
        isMirrored = board.isMirrored;
    }

    /**
     * Mirror the board.
     */
    public void mirror()
    {
        // Swap pieces bitboards.
        long temp = ourPieces;
        ourPieces = theirPieces;
        theirPieces = temp;

        // Reverse bytes of all bitboards.
        ourPieces = Long.reverseBytes(ourPieces);
        theirPieces = Long.reverseBytes(theirPieces);
        pawns = Long.reverseBytes(pawns);
        rooks = Long.reverseBytes(rooks);
        bishops = Long.reverseBytes(bishops);
        kings = Long.reverseBytes(kings);

        // Toggle flag.
        isMirrored = !isMirrored;
    }

    /**
     * Get the bitboard corresponding to a given square.
     *
     * @param square Given square (between 0 and 63).
     * @return Bitboard corresponding to the square.
     */
    static long getSquareBitboard(final int square)
    {
        return 1L << square;
    }

    /**
     * Get the bitboard corresponding to a given square.
     *
     * @param row Row of the square.
     * @param col Column of the square.
     * @return Bitboard corresponding to the square.
     */
    static long getSquareBitboard(final int row, final int col)
    {
        return 1L << (8 * row + col);
    }

    /**
     * Get the bitboard corresponding to a given square name.
     *
     * @param name Square name (e.g. "e4").
     * @return Bitboard corresponding to the square name.
     */
    static long getSquareBitboard(final String name)
    {
        return getSquareBitboard(name.charAt(1) - '1', name.charAt(0) - 'a');
    }

    /**
     * Get the square (between 0 and 63) corresponding to a given square name.
     *
     * @param name Square name (e.g. "e4").
     * @return Square corresponding to the square name.
     */
    static int getSquare(final String name)
    {
        return 8 * (name.charAt(1) - '1') + (name.charAt(0) - 'a');
    }

    /**
     * Get the bitboard corresponding to a given row.
     *
     * @param row Given row (between 0 and 7).
     * @return Bitboard corresponding to the given row.
     */
    static long getRowBitboard(final int row)
    {
        return BB_A1H1 << (8 * row);
    }

    /**
     * Get the bitboard corresponding to a given row.
     *
     * @param row Given row (between '1' and '8').
     * @return Bitboard corresponding to the given row.
     */
    static long getRowBitboard(final char row)
    {
        return getRowBitboard(row - '1');
    }

    /**
     * Get the bitboard corresponding to a given column.
     *
     * @param col Given column (between 0 and 7).
     * @return Bitboard corresponding to the given column.
     */
    static long getColBitboard(final int col)
    {
        return BB_A1A8 << col;
    }

    /**
     * Get the bitboard corresponding to a given column.
     *
     * @param col Given column (between 'a' and 'h').
     * @return Bitboard corresponding to the given column.
     */
    static long getColBitboard(final char col)
    {
        return getColBitboard(col - 'a');
    }

    /**
     * Get the bitboard corresponding to the diagonals that go through a given
     * square.
     *
     * @param row Row of the square.
     * @param col Column of the square.
     * @return Bitboard orresponding to the diagonals that go through the square.
     */
    static long getDiagsBitboard(final int row, final int col)
    {
        // Calculate the diagonals going through the square (row,col).
        long diagsBitboard = 0;

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (Math.abs(row - i) == Math.abs(col - j))
                {
                    // The square (i,j) lies on a diagonal through (row,col).
                    diagsBitboard |= Board.getSquareBitboard(i, j);
                }
            }
        }

        return diagsBitboard;
    }

    /**
     * Get the name corresponding to a given square.
     *
     * @param square Given square (between 0 and 63).
     * @return Name corresponding to the square (i.e. "e4").
     */
    static String getSquareName(final int square)
    {
        return "" + (char) ('a' + (square & 0b111)) + (char) ('1' + (square / 8));
    }

    /**
     * Get the debug string corresponding to a given bitboard.
     *
     * @param bitboard Given bitboard.
     * @return String representation of the given bitboard.
     */
    static String getBitboardDebugString(long bitboard)
    {
        StringBuilder sb = new StringBuilder();

        for (int row = 7; row >= 0; row--)
        {
            for (int col = 0; col <= 7; col++)
            {
                // Determine square bitboard.
                long squareBitboard = (1L << (8 * row + col));

                // Determine square label.
                char squareLabel = '.';

                if ((bitboard & squareBitboard) != 0)
                {
                    squareLabel = 'x';
                }

                // Append to string.
                sb.append(squareLabel);

                if (col == 7)
                {
                    sb.append(System.lineSeparator());
                }
                else
                {
                    // Add space between square labels.
                    sb.append(' ');
                }
            }
        }

        return sb.toString();
    }

    /**
     * @return String representation of the chess board.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int row = 7; row >= 0; row--)
        {
            for (int col = 0; col <= 7; col++)
            {
                // Determine square bitboard.
                long squareBitboard;
                if (!isMirrored)
                {
                    // The board is from white's perspective.
                    squareBitboard = (1L << (8 * row + col));
                }
                else
                {
                    // The board is from black's perspective.
                    squareBitboard = (1L << (8 * (7 - row) + col));
                }

                // Determine square label.
                char squareLabel = '.';

                if (((ourPieces + theirPieces) & squareBitboard) != 0)
                {
                    if ((pawns & squareBitboard) != 0)
                    {
                        // This is pawn.
                        squareLabel = 'p';
                    }
                    else if ((kings & squareBitboard) != 0)
                    {
                        // This is king.
                        squareLabel = 'k';
                    }
                    else if ((rooks & squareBitboard) != 0)
                    {
                        if ((bishops & squareBitboard) != 0)
                        {
                            // This is queen.
                            squareLabel = 'q';
                        }
                        else
                        {
                            // This is rook.
                            squareLabel = 'r';
                        }
                    }
                    else if ((bishops & squareBitboard) != 0)
                    {
                        // This is bishop.
                        squareLabel = 'b';
                    }
                    else
                    {
                        // This is knight.
                        squareLabel = 'n';
                    }

                    // Change label for white pieces to uppercase.
                    if ((!isMirrored && ((ourPieces & squareBitboard) != 0))
                            || (isMirrored && ((theirPieces & squareBitboard) != 0)))
                    {
                        squareLabel = Character.toUpperCase(squareLabel);
                    }
                }

                // Append to string.
                sb.append(squareLabel);

                if (col == 7)
                {
                    sb.append(System.lineSeparator());
                }
                else
                {
                    // Add space between square labels.
                    sb.append(' ');
                }
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Board other = (Board) obj;
        return bishops == other.bishops && isMirrored == other.isMirrored && kings == other.kings
                && ourPieces == other.ourPieces && pawns == other.pawns && rooks == other.rooks
                && theirPieces == other.theirPieces;
    }
}
