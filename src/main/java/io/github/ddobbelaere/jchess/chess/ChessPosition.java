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
 * <p>
 * Legal chess position that consists of
 * <ul>
 * <li>Board state (how pieces are positioned on the chess board).</li>
 * <li>Color of side to move.</li>
 * <li>Castling availability.</li>
 * <li>En passant capture possibility.</li>
 * <li>Number of plies since the last capture or pawn advance.</li>
 * <li>Number of full moves since the start of the game.</li>
 * </ul>
 * </p>
 * <p>
 * <em>Legal</em> means that
 * <ul>
 * <li>Each side has exactly one king.</li>
 * <li>The king of the side that is not to move is not in check.</li>
 * <li>Castling availability passes obvious sanity checks (e.g., white cannot
 * castle kingside if there is no rook on h1).</li>
 * <li>En passant information passes obvious sanity checks.</li>
 * <li>Pawns cannot be at the back ranks.</li>
 * </ul>
 * </p>
 *
 * @author Dieter Dobbelaere
 */
public class ChessPosition
{
    /**
     * Chess board corresponding to the position.
     */
    ChessBoard board = new ChessBoard();

    /**
     * Active player still has short castling rights.
     */
    boolean weCanCastleShort;

    /**
     * Active player still has long castling rights.
     */
    boolean weCanCastleLong;

    /**
     * Opponent still has short castling rights.
     */
    boolean theyCanCastleShort;

    /**
     * Opponent still has long castling rights.
     */
    boolean theyCanCastleLong;

    /**
     * Square where en passant pawn can be captured. Negative if en passant capture
     * is not possible.
     */
    byte enPassantCaptureSquare;

    /**
     * Number of plies since the last capture or pawn advance.
     */
    int numNoCaptureOrPawnAdvancePlies;

    /**
     * Number of full moves since the start of the game.
     */
    int numGameMoves;

    /**
     * Starting position.
     */
    public static final ChessPosition STARTING = fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    /**
     * Create a legal chess position from a FEN string.
     *
     * @param fen Given FEN string.
     * @return Position corresponding to the given FEN string.
     * @throws IllegalFenException If the FEN string is invalid or represents an
     *                             illegal position.
     */
    public static ChessPosition fromFen(String fen)
    {
        // Split FEN into different parts (assuming whitespace delimiters).
        String[] fenParts = fen.split("\\s+");

        // A FEN string should have at least three parts.
        if (fenParts.length < 3)
        {
            throw new IllegalFenException("FEN string has less than three parts.");
        }

        // Construct returned object.
        ChessPosition position = new ChessPosition();

        // Process piece placement string.
        byte col = 0;
        byte row = 7;
        final String pieceNames = "kqrbnp";

        for (char c : fenParts[0].toCharArray())
        {
            if (c == '/')
            {
                // Next row.
                col = 0;
                row--;
            }
            else if (c >= '1' && c <= '8')
            {
                // Skip empty squares.
                col += c - '0';
            }
            else if (pieceNames.indexOf(Character.toLowerCase(c)) >= 0)
            {
                // Sanity check of square indices.
                if (row < 0 || row > 7 || col < 0 || col > 7)
                {
                    throw new IllegalFenException("Invalid piece placement string.");
                }

                long squareBitboard = ChessBoard.getSquareBitboard(row, col);

                if (Character.isUpperCase(c))
                {
                    position.board.ourPieces |= squareBitboard;
                }
                else
                {
                    position.board.theirPieces |= squareBitboard;
                }

                switch (Character.toLowerCase(c))
                {
                case 'p':
                    position.board.pawns |= squareBitboard;
                    break;
                case 'r':
                    position.board.rooks |= squareBitboard;
                    break;
                case 'b':
                    position.board.bishops |= squareBitboard;
                    break;
                case 'q':
                    position.board.rooks |= squareBitboard;
                    position.board.bishops |= squareBitboard;
                    break;
                case 'k':
                    position.board.kings |= squareBitboard;
                    break;
                }

                // Increment column.
                col++;
            }
            else
            {
                throw new IllegalFenException("Invalid piece placement string.");
            }
        }

        // Process castling availability.
        position.weCanCastleShort = fenParts[2].contains("K");
        position.weCanCastleLong = fenParts[2].contains("Q");
        position.theyCanCastleShort = fenParts[2].contains("k");
        position.theyCanCastleLong = fenParts[2].contains("q");

        // Process (optional) en passant square.
        if (fenParts.length >= 4 && fenParts[3].length() == 2)
        {
            int square = 8 * (fenParts[3].charAt(1) - '1') + (fenParts[3].charAt(0) - 'a');

            // Sanity check on square.
            if (square < 0 || square > 63)
            {
                throw new IllegalFenException("Invalid en passant target square.");
            }

            // Store the square.
            position.enPassantCaptureSquare = (byte) square;
        }

        // Process active color.
        if (fenParts[1].equals("b"))
        {
            // Black to move. Mirror position.
            position.mirror();
        }

        // Process number of plies since last capture or pawn advance (if available).
        if (fenParts.length >= 5)
        {
            try
            {
                position.numNoCaptureOrPawnAdvancePlies = Integer.parseInt(fenParts[4]);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalFenException("Invalid number of plies since the last capture or pawn advance..");
            }
        }

        // Process move number (if available).
        if (fenParts.length >= 6)
        {
            try
            {
                position.numGameMoves = Integer.parseInt(fenParts[5]);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalFenException("Invalid number of full moves since the start of the game.");
            }
        }

        // Check if the position is legal.
        if (!position.isLegal())
        {
            throw new IllegalFenException("Illegal position.");
        }

        return position;
    }

    /**
     * Mirror the position (change side to move).
     */
    void mirror()
    {
        // Mirror the chess board.
        board.mirror();

        // Swap castling availability.
        boolean temp = weCanCastleShort;
        weCanCastleShort = theyCanCastleShort;
        theyCanCastleShort = temp;

        temp = weCanCastleLong;
        weCanCastleLong = theyCanCastleLong;
        theyCanCastleLong = temp;

        // Mirror en passant square.
        if (enPassantCaptureSquare != 0)
        {
            enPassantCaptureSquare = (byte) (8 * (7 - enPassantCaptureSquare / 8) + (enPassantCaptureSquare & 0b111));
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Chess board representation.
        sb.append(board);

        // Color to move. and move numbers.
        sb.append((board.isMirrored ? "black" : "white") + " to move");

        // Castling information.
        if (weCanCastleShort || weCanCastleLong || theyCanCastleShort || theyCanCastleLong)
        {
            sb.append(" - ");
        }

        if (board.isMirrored ? theyCanCastleShort : weCanCastleShort)
        {
            sb.append("K");
        }

        if (board.isMirrored ? theyCanCastleLong : weCanCastleLong)
        {
            sb.append("Q");
        }

        if (board.isMirrored ? weCanCastleShort : theyCanCastleShort)
        {
            sb.append("k");
        }

        if (board.isMirrored ? weCanCastleLong : theyCanCastleLong)
        {
            sb.append("q");
        }

        // Number of game moves.
        sb.append(" - move " + numGameMoves + System.lineSeparator());

        // Number of plies since last capture or pawn advance.
        sb.append(numNoCaptureOrPawnAdvancePlies + " plies since last capture or pawn advance");

        // En passant information.
        if (enPassantCaptureSquare != 0)
        {
            byte mirroredEnPassantCaptureSquare = (byte) (8 * (7 - enPassantCaptureSquare / 8)
                    + (enPassantCaptureSquare & 0b111));
            sb.append(System.lineSeparator() + "e.p. capture square: " + ChessBoard
                    .getSquareName(board.isMirrored ? mirroredEnPassantCaptureSquare : enPassantCaptureSquare));
        }

        // Add line break.
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    /**
     * @return The chess position is legal.
     */
    boolean isLegal()
    {
        // Check that each side has exactly one king.
        if (Long.bitCount(board.kings & board.ourPieces) != 1 || Long.bitCount(board.kings & board.theirPieces) != 1)
        {
            return false;
        }

        // TODO: Check that the king of the side that is not to move is not in check.

        // Castling availability must pass some obvious sanity checks.
        boolean ourKingOnOriginalSquare = (board.kings & board.ourPieces) == ChessBoard.getSquareBitboard("e1");
        boolean theirKingOnOriginalSquare = (board.kings & board.theirPieces) == ChessBoard.getSquareBitboard("e8");

        // Castling is not possible if the king has moved.
        if (((weCanCastleShort || weCanCastleLong) && !ourKingOnOriginalSquare)
                || ((theyCanCastleShort || theyCanCastleLong) && !theirKingOnOriginalSquare))
        {
            return false;
        }

        // Castling is not possible if the rook has moved.
        long ourRooks = board.rooks & ~board.bishops & board.ourPieces;
        long theirRooks = board.rooks & ~board.bishops & board.theirPieces;

        if ((weCanCastleShort && (ourRooks & ChessBoard.getSquareBitboard("h1")) == 0)
                || (weCanCastleLong && (ourRooks & ChessBoard.getSquareBitboard("a1")) == 0)
                || (theyCanCastleShort && (theirRooks & ChessBoard.getSquareBitboard("h8")) == 0)
                || (theyCanCastleLong && (theirRooks & ChessBoard.getSquareBitboard("a8")) == 0))
        {
            return false;
        }

        // En passant information must pass some obvious sanity checks.
        if (enPassantCaptureSquare != 0)
        {
            // Check that the en passant capture square lies on the sixth row.
            if ((enPassantCaptureSquare >> 3) != 5)
            {
                return false;
            }

            // Check that there is an opponent's pawn in front of the capture square.
            if (((ChessBoard.getSquareBitboard(enPassantCaptureSquare) >> 8) & board.pawns & board.theirPieces) == 0)
            {
                return false;
            }
        }

        // Check that pawns are not at the back ranks.
        if ((board.pawns & (ChessBoard.getRowBitboard(0) | ChessBoard.getRowBitboard(7))) != 0)
        {
            return false;
        }

        // If we get here, the position is considered legal.
        return true;
    }
}
