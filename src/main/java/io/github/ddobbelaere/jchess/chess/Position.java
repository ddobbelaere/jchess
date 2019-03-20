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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.ddobbelaere.jchess.chess.MoveGenerator.MoveGeneratorResult;

/**
 * Legal chess position.
 *
 * <p>
 * It consists of
 * <ul>
 * <li>Board state (how pieces are positioned on the chess board).</li>
 * <li>Color of side to move.</li>
 * <li>Castling availability.</li>
 * <li>En passant capture possibility.</li>
 * <li>Number of plies since the last capture or pawn advance.</li>
 * <li>Number of full moves since the start of the game.</li>
 * </ul>
 *
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
 *
 * @author Dieter Dobbelaere
 */
public class Position
{
    /**
     * Chess board corresponding to the position.
     */
    Board board = new Board();

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
     * Move generator result.
     */
    MoveGeneratorResult moveGenResult;

    /**
     * Starting position.
     */
    public static final Position STARTING = fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    /**
     * Default constructor.
     */
    Position()
    {

    }

    /**
     * Copy constructor.
     *
     * @param position Position that is to be copied.
     */
    Position(Position position)
    {
        // Copy all the fields.
        board = new Board(position.board);
        weCanCastleShort = position.weCanCastleShort;
        weCanCastleLong = position.weCanCastleLong;
        theyCanCastleShort = position.theyCanCastleShort;
        theyCanCastleLong = position.theyCanCastleLong;
        numNoCaptureOrPawnAdvancePlies = position.numNoCaptureOrPawnAdvancePlies;
        numGameMoves = position.numGameMoves;
        moveGenResult = position.moveGenResult;
    }

    /**
     * Create a legal chess position from a FEN string.
     *
     * @param fen Given FEN string.
     * @return Position corresponding to the given FEN string.
     * @throws IllegalFenException If the FEN string is invalid or represents an
     *                             illegal position.
     */
    public static Position fromFen(String fen)
    {
        // Split FEN into different parts (assuming whitespace delimiters).
        String[] fenParts = fen.split("\\s+");

        // A FEN string should have at least three parts.
        if (fenParts.length < 3)
        {
            throw new IllegalFenException("FEN string has less than three parts.");
        }

        // Construct returned object.
        Position position = new Position();

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

                long squareBitboard = Board.getSquareBitboard(row, col);

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

        // Invalidate the move generator result.
        moveGenResult = null;
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
            sb.append(System.lineSeparator() + "e.p. capture square: "
                    + Board.getSquareName(board.isMirrored ? mirroredEnPassantCaptureSquare : enPassantCaptureSquare));
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

        // Castling availability must pass some obvious sanity checks.
        final boolean ourKingOnOriginalSquare = (board.kings & board.ourPieces) == Board.getSquareBitboard("e1");
        final boolean theirKingOnOriginalSquare = (board.kings & board.theirPieces) == Board.getSquareBitboard("e8");

        // Castling is not possible if the king has moved.
        if (((weCanCastleShort || weCanCastleLong) && !ourKingOnOriginalSquare)
                || ((theyCanCastleShort || theyCanCastleLong) && !theirKingOnOriginalSquare))
        {
            return false;
        }

        // Castling is not possible if the rook has moved.
        final long ourRooks = board.rooks & ~board.bishops & board.ourPieces;
        final long theirRooks = board.rooks & ~board.bishops & board.theirPieces;

        if ((weCanCastleShort && (ourRooks & Board.getSquareBitboard("h1")) == 0)
                || (weCanCastleLong && (ourRooks & Board.getSquareBitboard("a1")) == 0)
                || (theyCanCastleShort && (theirRooks & Board.getSquareBitboard("h8")) == 0)
                || (theyCanCastleLong && (theirRooks & Board.getSquareBitboard("a8")) == 0))
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
            if (((Board.getSquareBitboard(enPassantCaptureSquare) >> 8) & board.pawns & board.theirPieces) == 0)
            {
                return false;
            }
        }

        // Check that pawns are not at the back ranks.
        if ((board.pawns & (Board.getRowBitboard(0) | Board.getRowBitboard(7))) != 0)
        {
            return false;
        }

        // Now mirror the position.
        mirror();

        // Determine if the their is in check (or, equivalently, if our king is in check
        // in the mirrored position).
        final boolean theirKingInCheck = MoveGenerator.squareIsUnderAttack(this,
                Long.numberOfTrailingZeros(board.kings & board.ourPieces));

        // Mirror the position back to its original state.
        mirror();

        // Check that the king of the side that is not to move is not in check.
        if (theirKingInCheck)
        {
            return false;
        }

        // If we get here, the position is considered legal.
        return true;
    }

    /**
     * @return The move generator result.
     */
    MoveGeneratorResult getMoveGeneratorResult()
    {
        // Lazy initialization.
        if (moveGenResult == null)
        {
            // Cache move generator result.
            moveGenResult = MoveGenerator.generateLegalMoves(this);
        }

        return moveGenResult;
    }

    /**
     * Get a list of all legal moves in the position.
     *
     * @return List of all legal moves in the position.
     */
    public List<Move> getLegalMoves()
    {
        // Return an unmodifiable view of the list.
        return Collections.unmodifiableList(getMoveGeneratorResult().getLegalMoves());
    }

    /**
     * Apply the move and return the resulting position.
     *
     * @param move Given move.
     * @return Resulting position after the given move is applied.
     * @throws IllegalMoveException If the move is illegal.
     */
    public Position applyMove(Move move)
    {
        // Check if the move is legal.
        if (!getLegalMoves().contains(move))
        {
            throw new IllegalMoveException("Move " + move + " is illegal in the position " + this);
        }

        // Construct the returned position.
        Position position = new Position(this);

        // Calculate source and destination square bitboards.
        final long fromSquareBitboard = Board.getSquareBitboard(move.getFromSquare());
        final long toSquareBitboard = Board.getSquareBitboard(move.getToSquare());

        // Check if it's a pawn move.
        final boolean isPawnMove = (position.board.pawns & fromSquareBitboard) != 0;

        // Update number of plies since the last capture or pawn advance.
        if (isPawnMove || (position.board.theirPieces & toSquareBitboard) != 0)
        {
            // This is either a pawn move or a piece capture, reset the counter.
            position.numNoCaptureOrPawnAdvancePlies = 0;
        }
        else
        {
            // Increment counter.
            position.numNoCaptureOrPawnAdvancePlies++;
        }

        // Increment number of moves after black's move.
        if (position.board.isMirrored)
        {
            position.numGameMoves++;
        }

        // Move our piece.
        position.board.ourPieces &= ~fromSquareBitboard;
        position.board.ourPieces |= toSquareBitboard;

        // Remove captured piece.
        position.board.theirPieces &= ~toSquareBitboard;
        position.board.bishops &= ~toSquareBitboard;
        position.board.pawns &= ~toSquareBitboard;
        position.board.rooks &= ~toSquareBitboard;

        // Clear en passant capture square.
        position.enPassantCaptureSquare = 0;

        // Invalidate opponent's castling rights if the piece moves to a8 or h8.
        if (move.getToSquare() == Board.SQUARE_A8)
        {
            position.theyCanCastleLong = false;
        }
        else if (move.getToSquare() == Board.SQUARE_H8)
        {
            position.theyCanCastleShort = false;
        }

        // Handle pawn moves.
        if (isPawnMove)
        {
            // Remove en passant captured pawn.
            if (move.getToSquare() == enPassantCaptureSquare)
            {
                position.board.theirPieces &= ~(toSquareBitboard >> 8);
                position.board.pawns &= ~(toSquareBitboard >> 8);
            }

            // Handle promotion.
            switch (move.getPromotionPieceType())
            {
            case NONE:
                position.board.pawns |= toSquareBitboard;
                break;
            case QUEEN:
                position.board.rooks |= toSquareBitboard;
                position.board.bishops |= toSquareBitboard;
                break;
            case ROOK:
                position.board.rooks |= toSquareBitboard;
                break;
            case BISHOP:
                position.board.bishops |= toSquareBitboard;
                break;
            case KNIGHT:
                break;
            }

            // Set en passant capture square (it there is an opponent's pawn that can
            // capture it).
            if (move.getToSquare() - move.getFromSquare() == 16 && (position.board.theirPieces & position.board.pawns
                    & ((toSquareBitboard << 1) | (toSquareBitboard >> 1)) & Board.BB_A4H4) != 0)
            {
                position.enPassantCaptureSquare = (byte) (move.getFromSquare() + 8);
            }

            // Remove the pawn from its source square.
            position.board.pawns &= ~fromSquareBitboard;
        }

        // Handle king moves.
        if ((position.board.kings & fromSquareBitboard) != 0)
        {
            // Handle castlings.
            if (move.equals(Move.SHORT_CASTLING))
            {
                // Short castling.
                // Move the rook from h1 to f1.
                position.board.rooks &= ~Board.BB_H1;
                position.board.rooks |= Board.BB_F1;

                position.board.ourPieces &= ~Board.BB_H1;
                position.board.ourPieces |= Board.BB_F1;
            }
            else if (move.equals(Move.LONG_CASTLING))
            {
                // Long castling.
                // Move the rook from a1 to d1.
                position.board.rooks &= ~Board.BB_A1;
                position.board.rooks |= Board.BB_D1;

                position.board.ourPieces &= ~Board.BB_A1;
                position.board.ourPieces |= Board.BB_D1;
            }

            // Invalidate castling rights.
            position.weCanCastleShort = false;
            position.weCanCastleLong = false;

            // Perform the actual move.
            position.board.kings &= ~fromSquareBitboard;
            position.board.kings |= toSquareBitboard;
        }

        // Handle rook moves.
        if ((position.board.rooks & fromSquareBitboard) != 0)
        {
            // Invalidate castling rights if a rook (or queen) moves from a1 or h1.
            if (fromSquareBitboard == Board.BB_A1)
            {
                position.weCanCastleLong = false;
            }
            else if (fromSquareBitboard == Board.BB_H1)
            {
                position.weCanCastleShort = false;
            }

            // Perform the actual move.
            position.board.rooks &= ~fromSquareBitboard;
            position.board.rooks |= toSquareBitboard;
        }

        // Handle bishop moves.
        if ((position.board.bishops & fromSquareBitboard) != 0)
        {
            // Perform the actual move.
            position.board.bishops &= ~fromSquareBitboard;
            position.board.bishops |= toSquareBitboard;
        }

        // Note that knight moves require no special actions.

        // Now mirror the position to change the side to move.
        position.mirror();

        // Return resulting position.
        return position;
    }

    /**
     * @return {@code true} if and only if it's white to move.
     */
    public boolean isWhiteToMove()
    {
        return !board.isMirrored;
    }

    /**
     * @return {@code true} if and only if it's black to move.
     */
    public boolean isBlackToMove()
    {
        return board.isMirrored;
    }

    /**
     * @return {@code true} if and only if white can castle short.
     */
    public boolean whiteCanCastleShort()
    {
        if (isWhiteToMove())
        {
            return weCanCastleShort;
        }
        else
        {
            return theyCanCastleShort;
        }
    }

    /**
     * @return {@code true} if and only if black can castle short.
     */
    public boolean blackCanCastleShort()
    {
        if (isWhiteToMove())
        {
            return theyCanCastleShort;
        }
        else
        {
            return weCanCastleShort;
        }
    }

    /**
     * @return {@code true} if and only if white can castle long.
     */
    public boolean whiteCanCastleLong()
    {
        if (isWhiteToMove())
        {
            return weCanCastleLong;
        }
        else
        {
            return theyCanCastleLong;
        }
    }

    /**
     * @return {@code true} if and only if black can castle long.
     */
    public boolean blackCanCastleLong()
    {
        if (isWhiteToMove())
        {
            return theyCanCastleLong;
        }
        else
        {
            return weCanCastleLong;
        }
    }

    /**
     * @return Number of full moves since the start of the game.
     */
    public int getMoveNumber()
    {
        return numGameMoves;
    }

    /**
     * @return Number of plies since the last capture or pawn advance.
     */
    public int getNumNoCaptureOrPawnAdvancePlies()
    {
        return numNoCaptureOrPawnAdvancePlies;
    }

    /**
     * @return {@code true} if and only if it's check.
     */
    public boolean isCheck()
    {
        return getMoveGeneratorResult().isCheck();
    }

    /**
     * @return {@code true} if and only if it's checkmate.
     */
    public boolean isCheckmate()
    {
        return getMoveGeneratorResult().getLegalMoves().isEmpty() && isCheck();
    }

    /**
     * @return {@code true} if and only if it's stalemate.
     */
    public boolean isStalemate()
    {
        return getMoveGeneratorResult().getLegalMoves().isEmpty() && !isCheck();
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

        Position other = (Position) obj;
        return Objects.equals(board, other.board) && enPassantCaptureSquare == other.enPassantCaptureSquare
                && numGameMoves == other.numGameMoves
                && numNoCaptureOrPawnAdvancePlies == other.numNoCaptureOrPawnAdvancePlies
                && theyCanCastleLong == other.theyCanCastleLong && theyCanCastleShort == other.theyCanCastleShort
                && weCanCastleLong == other.weCanCastleLong && weCanCastleShort == other.weCanCastleShort;
    }

}
