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

import java.util.List;

/**
 * Standard algebraic notation translator.
 *
 * @author Dieter Dobbelaere
 */
class SanTranslator
{
    /**
     * Convert a move to SAN.
     *
     * @param move     Given move.
     * @param position Given position in which the move is played.
     * @return SAN of the move (e.g. Ngxe2+).
     */
    static String toSan(Move move, Position position)
    {
        return toSan(move, position, position.applyMove(move));
    }

    /**
     * Convert a move to SAN.
     *
     * @param move         Given move.
     * @param position     Given position in which the move is played.
     * @param nextPosition Position after the move has been played.
     * @return SAN of the move (e.g. Ngxe2+).
     */
    static String toSan(Move move, Position position, Position nextPosition)
    {
        StringBuilder san = new StringBuilder();

        if (position.isShortCastlingMove(move))
        {
            san.append("O-O");
        }
        else if (position.isLongCastlingMove(move))
        {
            san.append("O-O-O");
        }
        else
        {
            // Append the piece type name.
            PieceType pieceType = position.getMovePieceType(move);
            san.append(pieceType);

            if (pieceType != PieceType.PAWN && pieceType != PieceType.KING)
            {
                // Handle ambiguities.
                san.append(getDisambiguatingLabel(position, move, position.getLegalMoves(pieceType)));
            }

            // Handle captures.
            if (position.isCapturingMove(move))
            {
                if (pieceType == PieceType.PAWN)
                {
                    san.append((char) ('a' + (move.getFromSquare() % 8)));
                }

                san.append('x');
            }

            // Append the destination square name.
            san.append(Board.getSquareName(move.getToSquare()));

            // Handle en passant capture suffix.
            if (pieceType == PieceType.PAWN && position.isEnPassantCaptureSquare(move.getToSquare()))
            {
                san.append("e.p.");
            }

            // Handle pawn promotion.
            PromotionPieceType promotionPieceType = move.getPromotionPieceType();

            if (promotionPieceType != PromotionPieceType.NONE)
            {
                san.append('=');
                san.append(promotionPieceType);
            }
        }

        // Determine if the move leads to check or checkmate.
        if (nextPosition.isCheck())
        {
            if (nextPosition.isCheckmate())
            {
                san.append('#');
            }
            else
            {
                san.append('+');
            }
        }

        return san.toString();
    }

    /**
     * Get disambiguating label.
     *
     * @param position           Given position.
     * @param move               Given move.
     * @param samePieceTypeMoves List of moves with the same piece type.
     * @return
     */
    private static String getDisambiguatingLabel(Position position, Move move, List<Move> samePieceTypeMoves)
    {
        boolean sameRowPiecePresent = false;
        boolean sameColPiecePresent = false;
        boolean ambiguityPresent = false;

        int row = move.getFromSquare() / 8;
        int col = move.getFromSquare() % 8;

        for (Move otherMove : samePieceTypeMoves)
        {
            if (otherMove.getToSquare() == move.getToSquare() && !otherMove.equals(move))
            {
                int otherRow = otherMove.getFromSquare() / 8;
                int otherCol = otherMove.getFromSquare() % 8;

                ambiguityPresent = true;

                if (row == otherRow)
                {
                    sameRowPiecePresent = true;
                }
                else if (col == otherCol)
                {
                    sameColPiecePresent = true;
                }
            }
        }

        if (ambiguityPresent)
        {
            if (sameColPiecePresent)
            {
                if (sameRowPiecePresent)
                {
                    return Board.getSquareName(move.getFromSquare());
                }
                else
                {
                    return "" + (row + 1);
                }
            }
            else
            {
                return "" + (char) ('a' + col);
            }
        }
        else
        {
            return "";
        }
    }
}
