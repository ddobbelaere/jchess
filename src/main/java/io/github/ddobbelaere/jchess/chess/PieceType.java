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
 * Chess piece type.
 *
 * @author Dieter Dobbelaere
 */
enum PieceType
{
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING;

    @Override
    public String toString()
    {
        String s = "";

        switch (this)
        {
        case PAWN:
            s = "";
            break;
        case ROOK:
            s = "R";
            break;
        case KNIGHT:
            s = "N";
            break;
        case BISHOP:
            s = "B";
            break;
        case QUEEN:
            s = "Q";
            break;
        case KING:
            s = "K";
            break;
        }

        return s;
    }
}
