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
 * Chess move.
 *
 * <p>
 * It is encoded by
 * <ul>
 * <li>Source square of the moved piece.</li>
 * <li>Destination square of the moved piece.</li>
 * <li>Promotion piece type ({@code NONE} if the move is no pawn
 * promotion).</li>
 * </ul>
 *
 * <p>
 * Note that castling moves have no special encoding. E.g. white castling short
 * is encoded by the move e1-g1.
 *
 * @author Dieter Dobbelaere
 */
public class Move
{
    /**
     * Source square of the moved piece.
     */
    private byte fromSquare;

    /**
     * Destination square of the moved piece.
     */
    private byte toSquare;

    /**
     * Promotion piece type.
     */
    private PromotionPieceType promotionPieceType = PromotionPieceType.NONE;

    /**
     * Short castling move (from our perspective).
     */
    final static Move SHORT_CASTLING = new Move("e1g1");

    /**
     * Long castling move (from our perspective).
     */
    final static Move LONG_CASTLING = new Move("e1c1");

    /**
     * Construct with given source and destinations squares and promotion piece
     * type.
     *
     * @param fromSquare         Source square of the moved piece.
     * @param toSquare           Destination square of the moved piece.
     * @param promotionPieceType Promotion piece type.
     */
    Move(int fromSquare, int toSquare, PromotionPieceType promotionPieceType)
    {
        this.fromSquare = (byte) fromSquare;
        this.toSquare = (byte) toSquare;
        this.promotionPieceType = promotionPieceType;
    }

    /**
     * Construct with given source and destination squares (assuming no promotion).
     *
     * @param fromSquare Source square of the moved piece.
     * @param toSquare   Destination square of the moved piece.
     */
    Move(int fromSquare, int toSquare)
    {
        this(fromSquare, toSquare, PromotionPieceType.NONE);
    }

    /**
     * Construct with given string of the form:
     * <ul>
     * <li>Two-character source square (e.g. d2).</li>
     * <li>Two-character destination square (e.g. d4).</li>
     * <li>Optional single-character promotion piece type (B, N, Q or R).</li>
     * </ul>
     *
     * @param moveString Descriptive move string.
     */
    public Move(String moveString)
    {
        // Check move string length.
        int len = moveString.length();

        if (len != 4 && len != 5)
        {
            throw new IllegalArgumentException(
                    "Move string " + moveString + " should contain either 4 or 5 characters.");
        }

        PromotionPieceType promotionPieceType = PromotionPieceType.NONE;

        if (len == 5)
        {
            // Parse promotion piece type character.
            switch (moveString.charAt(4))
            {
            case 'B':
                promotionPieceType = PromotionPieceType.BISHOP;
                break;
            case 'N':
                promotionPieceType = PromotionPieceType.KNIGHT;
                break;
            case 'Q':
                promotionPieceType = PromotionPieceType.QUEEN;
                break;
            case 'R':
                promotionPieceType = PromotionPieceType.ROOK;
                break;
            default:
                throw new IllegalArgumentException("Illegal promotion piece type in move string " + moveString + ".");
            }
        }

        this.fromSquare = (byte) Board.getSquare(moveString.substring(0, 2));
        this.toSquare = (byte) Board.getSquare(moveString.substring(2, 4));
        this.promotionPieceType = promotionPieceType;
    }

    /**
     * @return Source square of the moved piece.
     */
    int getFromSquare()
    {
        return fromSquare;
    }

    /**
     * @return Destination square of the moved piece.
     */
    int getToSquare()
    {
        return toSquare;
    }

    /**
     * @return Promotion piece type.
     */
    PromotionPieceType getPromotionPieceType()
    {
        return promotionPieceType;
    }

    @Override
    public String toString()
    {
        return Board.getSquareName(fromSquare) + Board.getSquareName(toSquare) + promotionPieceType;
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

        Move other = (Move) obj;
        return fromSquare == other.fromSquare && promotionPieceType == other.promotionPieceType
                && toSquare == other.toSquare;
    }
}
