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
import java.util.List;

/**
 * Chess move generator.
 *
 * @author Dieter Dobbelaere
 */
class MoveGenerator
{
    /**
     * Array of all rook move directions. Each direction is a two element array of
     * the form (row increment, column increment).
     */
    static final int[][] rookMoveDirections = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    /**
     * Array of all bishop move directions. Each direction is a two element array of
     * the form (row increment, column increment).
     */
    static final int[][] bishopMoveDirections = new int[][] { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

    /**
     * Array of all king move directions. Each direction is a two element array of
     * the form (row increment, column increment).
     */
    static final int[][] kingMoveDirections = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 1 },
            { 1, -1 }, { -1, 1 }, { -1, -1 } };

    /**
     * Array of attack bitboards of opponent's pawns.
     */
    private static final long[] pawnAttackBitboards = new long[] { 0x0000000000000200L, 0x0000000000000500L,
            0x0000000000000A00L, 0x0000000000001400L, 0x0000000000002800L, 0x0000000000005000L, 0x000000000000A000L,
            0x0000000000004000L, 0x0000000000020000L, 0x0000000000050000L, 0x00000000000A0000L, 0x0000000000140000L,
            0x0000000000280000L, 0x0000000000500000L, 0x0000000000A00000L, 0x0000000000400000L, 0x0000000002000000L,
            0x0000000005000000L, 0x000000000A000000L, 0x0000000014000000L, 0x0000000028000000L, 0x0000000050000000L,
            0x00000000A0000000L, 0x0000000040000000L, 0x0000000200000000L, 0x0000000500000000L, 0x0000000A00000000L,
            0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000A000000000L, 0x0000004000000000L,
            0x0000020000000000L, 0x0000050000000000L, 0x00000A0000000000L, 0x0000140000000000L, 0x0000280000000000L,
            0x0000500000000000L, 0x0000A00000000000L, 0x0000400000000000L, 0x0002000000000000L, 0x0005000000000000L,
            0x000A000000000000L, 0x0014000000000000L, 0x0028000000000000L, 0x0050000000000000L, 0x00A0000000000000L,
            0x0040000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
            0x0000000000000000L, 0x0000000000000000L };

    /**
     * Array of knight attack bitboards.
     */
    private static final long[] knightAttackBitboards = new long[] { 0x0000000000020400L, 0x0000000000050800L,
            0x00000000000A1100L, 0x0000000000142200L, 0x0000000000284400L, 0x0000000000508800L, 0x0000000000A01000L,
            0x0000000000402000L, 0x0000000002040004L, 0x0000000005080008L, 0x000000000A110011L, 0x0000000014220022L,
            0x0000000028440044L, 0x0000000050880088L, 0x00000000A0100010L, 0x0000000040200020L, 0x0000000204000402L,
            0x0000000508000805L, 0x0000000A1100110AL, 0x0000001422002214L, 0x0000002844004428L, 0x0000005088008850L,
            0x000000A0100010A0L, 0x0000004020002040L, 0x0000020400040200L, 0x0000050800080500L, 0x00000A1100110A00L,
            0x0000142200221400L, 0x0000284400442800L, 0x0000508800885000L, 0x0000A0100010A000L, 0x0000402000204000L,
            0x0002040004020000L, 0x0005080008050000L, 0x000A1100110A0000L, 0x0014220022140000L, 0x0028440044280000L,
            0x0050880088500000L, 0x00A0100010A00000L, 0x0040200020400000L, 0x0204000402000000L, 0x0508000805000000L,
            0x0A1100110A000000L, 0x1422002214000000L, 0x2844004428000000L, 0x5088008850000000L, 0xA0100010A0000000L,
            0x4020002040000000L, 0x0400040200000000L, 0x0800080500000000L, 0x1100110A00000000L, 0x2200221400000000L,
            0x4400442800000000L, 0x8800885000000000L, 0x100010A000000000L, 0x2000204000000000L, 0x0004020000000000L,
            0x0008050000000000L, 0x00110A0000000000L, 0x0022140000000000L, 0x0044280000000000L, 0x0088500000000000L,
            0x0010A00000000000L, 0x0020400000000000L };

    /**
     * <p>
     * Safety information of <em>our</em> king consisting of
     * <li>Pinned pieces.</li>
     * <li>Accessible squares around our king.</li>
     * <li>If our king is in (double) check.</li>
     * <li>Attack lines of pieces that give check (including the pieces
     * themselves).</li>
     * </p>
     */
    static class KingSafety
    {
        /**
         * Pinned pieces bitboard.
         */
        long pinnedPieces;

        /**
         * Bitboard of accessible squares around our king (i.e., squares where our king
         * can move to, not including castling squares).
         */
        long accessibleSquares;

        /**
         * Double check.
         */
        boolean isDoubleCheck;

        /**
         * Bitboard of attack lines of pieces that give check (including the pieces
         * themselves).
         */
        long attackLines;

        /**
         * @return Our king is in check.
         */
        boolean isCheck()
        {
            return attackLines != 0;
        }

        /**
         * @return Our king is in double check.
         */
        boolean isDoubleCheck()
        {
            return isDoubleCheck;
        }
    }

    /**
     * <p>
     * Move generator result consisting of
     * <li>King safety information.</li>
     * <li>Legal moves.</li>
     * </p>
     */
    static class MoveGeneratorResult
    {
        /**
         * King safety information.
         */
        private KingSafety kingSafety;

        /**
         * List of legal moves.
         */
        List<Move> legalMoves;

        /**
         * Constructor.
         */
        MoveGeneratorResult(KingSafety kingSafety, List<Move> legalMoves)
        {
            this.kingSafety = kingSafety;
            this.legalMoves = legalMoves;
        }

        /**
         * @return If it's check.
         */
        boolean isCheck()
        {
            return kingSafety.isCheck();
        }

        /**
         * @return List of legal moves.
         */
        List<Move> getLegalMoves()
        {
            return legalMoves;
        }
    }

    /**
     * Generates king safety information of a given legal chess position.
     *
     * @param position Given legal chess position.
     * @return King safety information of the given legal chess position.
     */
    static KingSafety generateKingSafety(Position position)
    {
        // Construct king safety object.
        KingSafety kingSafety = new KingSafety();

        // Get square of our king.
        final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);
        final int ourKingRow = ourKingSquare / 8;
        final int ourKingCol = ourKingSquare % 8;

        // Temporary variable that hold number of pieces that give check.
        int numCheckingPieces = 0;

        // Check sliding piece attackers.
        for (int pieceType = 0; pieceType < 2; pieceType++)
        {
            int[][] pieceMoveDirections = (pieceType == 0) ? rookMoveDirections : bishopMoveDirections;
            final long attackingPieces = position.board.theirPieces
                    & ((pieceType == 0) ? position.board.rooks : position.board.bishops);

            if ((attackingPieces & ((pieceType == 0) ? MagicUtils.getRookAttackBitboard(ourKingSquare, 0)
                    : MagicUtils.getBishopAttackBitboard(ourKingSquare, 0))) != 0)
            {
                for (int[] pieceMoveDirection : pieceMoveDirections)
                {
                    // Start at the square of our king.
                    int row = ourKingRow;
                    int col = ourKingCol;

                    // Initialize temporary variables.
                    boolean possiblePinnedPieceFound = false;
                    long possiblePinnedPieceBitboard = 0;
                    long attackLine = 0;

                    while (true)
                    {
                        // Go to next square.
                        row += pieceMoveDirection[0];
                        col += pieceMoveDirection[1];

                        if (row < 0 || row > 7 || col < 0 || col > 7)
                        {
                            // Invalid square.
                            break;
                        }

                        long squareBitboard = Board.getSquareBitboard(row, col);

                        if ((squareBitboard & position.board.ourPieces) != 0)
                        {
                            // One of our pieces is present here, it is possibly pinned.
                            if (possiblePinnedPieceFound)
                            {
                                // There are at least two of our pieces lined up first (so no piece is pinned).
                                break;
                            }
                            else
                            {
                                // If an attacking piece follows, this piece is pinned.
                                possiblePinnedPieceFound = true;
                                possiblePinnedPieceBitboard = squareBitboard;
                            }
                        }

                        // Add the square bitboard to the attack line.
                        attackLine |= squareBitboard;

                        if ((squareBitboard & position.board.theirPieces) != 0)
                        {
                            if ((squareBitboard & attackingPieces) != 0)
                            {
                                // One of their attacking pieces is present.
                                if (possiblePinnedPieceFound)
                                {
                                    // The attacking pieces doesn't give check, so only update the pinned pieces
                                    // bitboard.
                                    kingSafety.pinnedPieces |= possiblePinnedPieceBitboard;
                                }
                                else
                                {
                                    // No pinned piece, which means that the attacking piece gives check.
                                    kingSafety.attackLines |= attackLine;
                                    numCheckingPieces++;
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }

        // Check pawns.
        long attackingPawns = pawnAttackBitboards[ourKingSquare] & position.board.theirPieces & position.board.pawns;
        kingSafety.attackLines |= attackingPawns;

        if (attackingPawns != 0)
        {
            // No more than one pawn can give check.
            numCheckingPieces++;
        }

        // Check knights.
        long attackingKnights = knightAttackBitboards[ourKingSquare] & position.board.theirPieces
                & ~(position.board.pawns | position.board.rooks | position.board.bishops | position.board.kings);
        kingSafety.attackLines |= attackingKnights;

        if (attackingKnights != 0)
        {
            // No more than one knight can give check.
            numCheckingPieces++;
        }

        // Set double check flag.
        kingSafety.isDoubleCheck = (numCheckingPieces == 2);

        // Accessible squares around king where the king can move to.
        for (int[] kingMoveDirection : kingMoveDirections)
        {
            final int row = ourKingRow + kingMoveDirection[0];
            final int col = ourKingCol + kingMoveDirection[1];

            if (row < 0 || row > 7 || col < 0 || col > 7)
            {
                // Invalid square.
                continue;
            }

            final long squareBitboard = Board.getSquareBitboard(row, col);

            if ((squareBitboard & position.board.ourPieces) != 0)
            {
                // If one of our pieces is present, the square is not accessible.
                continue;
            }

            if ((squareBitboard & kingSafety.attackLines & ~position.board.theirPieces) != 0)
            {
                // The square lies on an attack line and is not equal to the attacking piece.
                // Therefore, it is certainly not accessible.
                continue;
            }

            if (!squareIsUnderAttack(position, 8 * row + col))
            {
                // The square is not under attack. Add it to the accessible
                // squares bitboard.
                kingSafety.accessibleSquares |= squareBitboard;
            }
        }

        // Return king safety object.
        return kingSafety;
    }

    /**
     * Check if the given square is under attack in the given position (disregarding
     * our king, such that X-ray attacks are considered).
     *
     * @param position Given position.
     * @param square   Given square (between 0 and 63).
     * @return The square is under attack.
     */
    static boolean squareIsUnderAttack(Position position, int square)
    {
        // Check rooks.
        if ((MagicUtils.getRookAttackBitboard(square,
                (position.board.ourPieces & ~position.board.kings) | position.board.theirPieces)
                & position.board.theirPieces & position.board.rooks) != 0)
        {
            return true;
        }

        // Check bishops.
        if ((MagicUtils.getBishopAttackBitboard(square,
                (position.board.ourPieces & ~position.board.kings) | position.board.theirPieces)
                & position.board.theirPieces & position.board.bishops) != 0)
        {
            return true;
        }

        // Check knights.
        if ((knightAttackBitboards[square] & position.board.theirPieces
                & ~(position.board.pawns | position.board.rooks | position.board.bishops | position.board.kings)) != 0)
        {
            return true;
        }

        // Check pawns.
        if ((pawnAttackBitboards[square] & position.board.theirPieces & position.board.pawns) != 0)
        {
            return true;
        }

        // Check their king.
        final int theirKingSquare = Long.numberOfTrailingZeros(position.board.theirPieces & position.board.kings);
        final int theirKingRow = theirKingSquare / 8;
        final int theirKingCol = theirKingSquare % 8;
        final int row = square / 8;
        final int col = square % 8;

        if (Math.abs(theirKingRow - row) <= 1 && Math.abs(theirKingCol - col) <= 1)
        {
            return true;
        }

        // If we get here, the square is not under attack.
        return false;
    }

    /**
     * Generates all legal moves of a given legal chess position.
     *
     * @param position Given legal chess position.
     * @return A list of legal moves of the given legal chess position.
     */
    static MoveGeneratorResult generateLegalMoves(Position position)
    {
        // Construct the legal moves list.
        List<Move> legalMoves = new ArrayList<>();

        // Generate king safety.
        KingSafety kingSafety = generateKingSafety(position);

        // Always add king moves.
        generateKingMoves(position, kingSafety, legalMoves);

        if (!kingSafety.isDoubleCheck())
        {
            // Only king moves can resolve a double check, so add non-king moves if it's not
            // double check.

            // Add knight moves.
            generateKnightMoves(position, kingSafety, legalMoves);

            // Add rook moves.
            generateRookMoves(position, kingSafety, legalMoves);

            // Add bishop moves.
            generateBishopMoves(position, kingSafety, legalMoves);

            // Add pawn moves.
            generatePawnMoves(position, kingSafety, legalMoves);
        }

        // If it's black to move, mirror all moves.
        if (position.board.isMirrored)
        {
            for (Move move : legalMoves)
            {
                move.mirror();
            }
        }

        // Return the result.
        return new MoveGeneratorResult(kingSafety, legalMoves);
    }

    /**
     * Generates all legal king moves of a given legal chess position.
     *
     * @param position   Given legal chess position.
     * @param kingSafety King safety corresponding to the position.
     * @param legalMoves List of moves to which the legal king moves are appended.
     */
    static void generateKingMoves(Position position, KingSafety kingSafety, List<Move> legalMoves)
    {
        // Calculate the square of our king.
        final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);

        // Add moves to accessible squares.
        long accessibleSquares = kingSafety.accessibleSquares;

        while (accessibleSquares != 0)
        {
            // Add move to list.
            legalMoves.add(new Move(ourKingSquare, Long.numberOfTrailingZeros(accessibleSquares)));

            // Remove the square from the bitboard.
            accessibleSquares &= accessibleSquares - 1;
        }

        // Add castling moves.
        if (ourKingSquare == Board.SQUARE_E1 && !kingSafety.isCheck())
        {
            // Our king is still on its original square and is not in check.

            // Check if we can castle short.
            if (position.weCanCastleShort)
            {
                // No pieces are allowed to be present on f1 and g1. Moreover, f1 should be an
                // accessible square and g1 should not be under attack.
                // Note that we don't have to check if a rook is still present on h1, because
                // each rook move from h1 invalidates short castling rights.
                if (((Board.BB_F1 | Board.BB_G1) & (position.board.ourPieces | position.board.theirPieces)) == 0
                        && (kingSafety.accessibleSquares & Board.BB_F1) != 0
                        && !squareIsUnderAttack(position, Board.SQUARE_G1))
                {
                    legalMoves.add(new Move(ourKingSquare, Board.SQUARE_G1));
                }
            }

            // Check if we can castle long.
            if (position.weCanCastleLong)
            {
                // No pieces are allowed to be present on b1, c1 and d1. Moreover, d1 should
                // be an accessible square and c1 should not be under attack.
                // Note that we don't have to check if a rook is still present on a1, because
                // each rook move from a1 invalidates long castling rights.
                if (((Board.BB_B1 | Board.BB_C1 | Board.BB_D1)
                        & (position.board.ourPieces | position.board.theirPieces)) == 0
                        && (kingSafety.accessibleSquares & Board.BB_D1) != 0
                        && !squareIsUnderAttack(position, Board.SQUARE_C1))
                {
                    legalMoves.add(new Move(ourKingSquare, Board.SQUARE_C1));
                }
            }
        }
    }

    /**
     * Generates all legal knight moves of a given legal chess position (assuming
     * it's not double check).
     *
     * @param position   Given legal chess position.
     * @param kingSafety King safety corresponding to the position.
     * @param legalMoves List of moves to which the legal knight moves are appended.
     */
    static void generateKnightMoves(Position position, KingSafety kingSafety, List<Move> legalMoves)
    {
        // This function is never called for positions in double check.
        // Loop over all knights that are not pinned (pinned knight can never move).
        long ourNonPinnedKnights = position.board.ourPieces & ~(position.board.bishops | position.board.kings
                | position.board.pawns | position.board.rooks | kingSafety.pinnedPieces);

        while (ourNonPinnedKnights != 0)
        {
            // Calculate the knight source square.
            final int knightFromSquare = Long.numberOfTrailingZeros(ourNonPinnedKnights);

            // Determine destination squares bitboard.
            long knightToSquaresBitboard = knightAttackBitboards[knightFromSquare] & ~position.board.ourPieces;

            // If it's check, only moves to an attack line are allowed (as they certainly
            // resolve the check by either interposing or capturing the only attacking piece
            // because this function assumes it's not double check).
            if (kingSafety.isCheck())
            {
                knightToSquaresBitboard &= kingSafety.attackLines;
            }

            // Add all legal moves.
            while (knightToSquaresBitboard != 0)
            {
                // Add move to list.
                legalMoves.add(new Move(knightFromSquare, Long.numberOfTrailingZeros(knightToSquaresBitboard)));

                // Remove the destination square from the bitboard.
                knightToSquaresBitboard &= knightToSquaresBitboard - 1;
            }

            // Remove the knight from the bitboard.
            ourNonPinnedKnights &= ourNonPinnedKnights - 1;
        }
    }

    /**
     * Generates all legal rook moves of a given legal chess position (assuming it's
     * not double check).
     *
     * @param position   Given legal chess position.
     * @param kingSafety King safety corresponding to the position.
     * @param legalMoves List of moves to which the legal rook moves are appended.
     */
    static void generateRookMoves(Position position, KingSafety kingSafety, List<Move> legalMoves)
    {
        // Cache the occupied squares bitboard.
        final long occupiedSquaresBitboard = position.board.ourPieces | position.board.theirPieces;

        // Generate all non-pinned rook moves.
        {
            // Determine non-pinned rooks.
            long ourNonPinnedRooks = position.board.ourPieces & position.board.rooks & ~kingSafety.pinnedPieces;

            while (ourNonPinnedRooks != 0)
            {
                // Calculate the rook source square.
                final int rookFromSquare = Long.numberOfTrailingZeros(ourNonPinnedRooks);

                // Determine destination squares bitboard.
                long rookToSquaresBitboard = MagicUtils.getRookAttackBitboard(rookFromSquare, occupiedSquaresBitboard)
                        & ~position.board.ourPieces;

                // If it's check, only moves to an attack line are allowed (as they certainly
                // resolve the check by either interposing or capturing the only attacking piece
                // because this function assumes it's not double check).
                if (kingSafety.isCheck())
                {
                    rookToSquaresBitboard &= kingSafety.attackLines;
                }

                // Add all legal moves.
                while (rookToSquaresBitboard != 0)
                {
                    // Add move to list.
                    legalMoves.add(new Move(rookFromSquare, Long.numberOfTrailingZeros(rookToSquaresBitboard)));

                    // Remove the destination square from the bitboard.
                    rookToSquaresBitboard &= rookToSquaresBitboard - 1;
                }

                // Remove the rook from the bitboard.
                ourNonPinnedRooks &= ourNonPinnedRooks - 1;
            }
        }

        // Generate all pinned rook moves.
        // Note that no pinned rook can resolve a check.
        if (!kingSafety.isCheck())
        {
            // Determine pinned rooks.
            long ourPinnedRooks = position.board.ourPieces & position.board.rooks & kingSafety.pinnedPieces;

            // Determine our king's square.
            final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);
            final int ourKingRow = ourKingSquare / 8;
            final int ourKingCol = ourKingSquare % 8;

            while (ourPinnedRooks != 0)
            {
                // Calculate the rook source square.
                final int rookFromSquare = Long.numberOfTrailingZeros(ourPinnedRooks);
                final int rookFromRow = rookFromSquare / 8;
                final int rookFromCol = rookFromSquare % 8;

                if (ourKingCol == rookFromCol || ourKingRow == rookFromRow)
                {
                    // The rook is pinned by a rook-like piece.
                    // If it would have been pinned by a bishop-like piece, it would not be able to
                    // move.
                    final long sameLineWrtKingMask = MagicUtils.getRookAttackBitboard(ourKingSquare, 0L);

                    // Determine destination squares bitboard.
                    // Only consider destination squares that stay on the same line w.r.t. our king
                    // (by applying the precomputed mask).
                    long rookToSquaresBitboard = MagicUtils.getRookAttackBitboard(rookFromSquare,
                            occupiedSquaresBitboard) & ~position.board.ourPieces & sameLineWrtKingMask;

                    // Add all legal moves.
                    while (rookToSquaresBitboard != 0)
                    {
                        // Add move to list.
                        legalMoves.add(new Move(rookFromSquare, Long.numberOfTrailingZeros(rookToSquaresBitboard)));

                        // Remove the destination square from the bitboard.
                        rookToSquaresBitboard &= rookToSquaresBitboard - 1;
                    }
                }

                // Remove the rook from the bitboard.
                ourPinnedRooks &= ourPinnedRooks - 1;
            }
        }
    }

    /**
     * Generates all legal bishop moves of a given legal chess position (assuming
     * it's not double check).
     *
     * @param position   Given legal chess position.
     * @param kingSafety King safety corresponding to the position.
     * @param legalMoves List of moves to which the legal bishop moves are appended.
     */
    static void generateBishopMoves(Position position, KingSafety kingSafety, List<Move> legalMoves)
    {
        // Cache the occupied squares bitboard.
        final long occupiedSquaresBitboard = position.board.ourPieces | position.board.theirPieces;

        // Generate all non-pinned bishop moves.
        {
            // Determine non-pinned bishops.
            long ourNonPinnedBishops = position.board.ourPieces & position.board.bishops & ~kingSafety.pinnedPieces;

            while (ourNonPinnedBishops != 0)
            {
                // Calculate the bishop source square.
                final int bishopFromSquare = Long.numberOfTrailingZeros(ourNonPinnedBishops);

                // Determine destination squares bitboard.
                long bishopToSquaresBitboard = MagicUtils.getBishopAttackBitboard(bishopFromSquare,
                        occupiedSquaresBitboard) & ~position.board.ourPieces;

                // If it's check, only moves to an attack line are allowed (as they certainly
                // resolve the check by either interposing or capturing the only attacking piece
                // because this function assumes it's not double check).
                if (kingSafety.isCheck())
                {
                    bishopToSquaresBitboard &= kingSafety.attackLines;
                }

                // Add all legal moves.
                while (bishopToSquaresBitboard != 0)
                {
                    // Add move to list.
                    legalMoves.add(new Move(bishopFromSquare, Long.numberOfTrailingZeros(bishopToSquaresBitboard)));

                    // Remove the destination square from the bitboard.
                    bishopToSquaresBitboard &= bishopToSquaresBitboard - 1;
                }

                // Remove the bishop from the bitboard.
                ourNonPinnedBishops &= ourNonPinnedBishops - 1;
            }
        }

        // Generate all pinned bishop moves.
        // Note that no pinned bishop can resolve a check.
        if (!kingSafety.isCheck())
        {
            // Determine pinned bishops.
            long ourPinnedBishops = position.board.ourPieces & position.board.bishops & kingSafety.pinnedPieces;

            // Determine our king's square.
            final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);
            final int ourKingRow = ourKingSquare / 8;
            final int ourKingCol = ourKingSquare % 8;

            while (ourPinnedBishops != 0)
            {
                // Calculate the bishop source square.
                final int bishopFromSquare = Long.numberOfTrailingZeros(ourPinnedBishops);
                final int bishopFromRow = bishopFromSquare / 8;
                final int bishopFromCol = bishopFromSquare % 8;

                if (ourKingCol != bishopFromCol && ourKingRow != bishopFromRow)
                {
                    // The bishop is pinned by a bishop-like piece.
                    // If it would have been pinned by a rook-like piece, it would not be able to
                    // move.
                    final long sameLineWrtKingMask = MagicUtils.getBishopAttackBitboard(ourKingSquare, 0L);

                    // Determine destination squares bitboard.
                    // Only consider destination squares that stay on the same line w.r.t. our king
                    // (by applying the precomputed mask).
                    long bishopToSquaresBitboard = MagicUtils.getBishopAttackBitboard(bishopFromSquare,
                            occupiedSquaresBitboard) & ~position.board.ourPieces & sameLineWrtKingMask;

                    // Add all legal moves.
                    while (bishopToSquaresBitboard != 0)
                    {
                        // Add move to list.
                        legalMoves.add(new Move(bishopFromSquare, Long.numberOfTrailingZeros(bishopToSquaresBitboard)));

                        // Remove the destination square from the bitboard.
                        bishopToSquaresBitboard &= bishopToSquaresBitboard - 1;
                    }
                }

                // Remove the bishop from the bitboard.
                ourPinnedBishops &= ourPinnedBishops - 1;
            }
        }
    }

    /**
     * Generates all legal pawn moves of a given legal chess position (assuming it's
     * not double check).
     *
     * @param position   Given legal chess position.
     * @param kingSafety King safety corresponding to the position.
     * @param legalMoves List of moves to which the legal pawn moves are appended.
     */
    static void generatePawnMoves(Position position, KingSafety kingSafety, List<Move> legalMoves)
    {
        // This function is never called for positions in double check.
        // Loop over all pawns.
        long ourPawns = position.board.ourPieces & position.board.pawns;

        // Determine our king's square.
        final int ourKingSquare = Long.numberOfTrailingZeros(position.board.ourPieces & position.board.kings);
        final int ourKingRow = ourKingSquare / 8;
        final int ourKingCol = ourKingSquare % 8;

        // Cache the occupied squares bitboard.
        final long occupiedSquaresBitboard = position.board.ourPieces | position.board.theirPieces;

        // Cache their pieces including the en passant square.
        final long theirPiecesAndEnPassantBitboard = position.board.theirPieces
                | (1L << position.enPassantCaptureSquare);

        while (ourPawns != 0)
        {
            // Calculate the pawn source square.
            final int pawnFromSquare = Long.numberOfTrailingZeros(ourPawns);
            final int pawnFromRow = pawnFromSquare / 8;
            final int pawnFromCol = pawnFromSquare % 8;

            // Cache the pawn source bitboard.
            final long pawnFromBitboard = 1L << pawnFromSquare;

            // Check if the pawn is pinned.
            final boolean isPinned = (pawnFromBitboard & kingSafety.pinnedPieces) != 0;

            // Forward pawn moves.
            // If it is not pinned, it can possibly move.
            // If it is pinned, it has to stay on the same line w.r.t. the king, which means
            // the king has to be on the same column of the pawn.
            if (!isPinned || pawnFromCol == ourKingCol)
            {
                if (((pawnFromBitboard << 8) & occupiedSquaresBitboard) == 0)
                {
                    // No piece is present one square in front of the pawn.
                    final int pawnToSquare = pawnFromSquare + 8;

                    // Only continue if
                    // - It's not check or
                    // - It's check (but not double check, as assumed earlier) and the pawn moves to
                    // an attacking line (either interposing or capturing the only attacking piece).
                    if (!kingSafety.isCheck() || ((pawnFromBitboard << 8) & kingSafety.attackLines) != 0)
                    {
                        if (pawnFromRow != 6)
                        {
                            // Normal move.
                            legalMoves.add(new Move(pawnFromSquare, pawnToSquare));
                        }
                        else
                        {
                            // Promotion.
                            legalMoves.add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.BISHOP));
                            legalMoves.add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.KNIGHT));
                            legalMoves.add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.QUEEN));
                            legalMoves.add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.ROOK));
                        }
                    }

                    // Two moves forward.
                    // If the pawn starts from its initial square, moving two squares forward is
                    // possible if no piece is present on the destination square.
                    if (pawnFromRow == 1 && ((pawnFromBitboard << 16) & occupiedSquaresBitboard) == 0)
                    {
                        // Only continue if
                        // - It's not check or
                        // - It's check (but not double check, as assumed earlier) and the pawn moves to
                        // an attacking line (either interposing or capturing the only attacking piece).
                        if (!kingSafety.isCheck() || ((pawnFromBitboard << 16) & kingSafety.attackLines) != 0)
                        {

                            legalMoves.add(new Move(pawnFromSquare, pawnFromSquare + 16));
                        }

                    }
                }
            }

            // Captures.
            for (final int direction : new int[] { -1, 1 })
            {
                // Cache destination square parameters.
                final int pawnToSquare = pawnFromSquare + 8 + direction;
                final int pawnToCol = pawnFromCol + direction;
                final long pawnToBitboard = Board.getSquareBitboard(pawnToSquare);

                if (pawnToCol >= 0 && pawnToCol <= 7)
                {
                    if ((pawnToBitboard & theirPiecesAndEnPassantBitboard) != 0)
                    {
                        // One of their pieces is present on the destination square (possibly en
                        // passant).
                        // If it is not pinned, it can possibly capture.
                        // If it is pinned, it has to stay on the same line w.r.t. the king, which means
                        // the king has to be on the same diagonal of the pawn's source and destination
                        // square.
                        if (!isPinned || pawnFromCol - ourKingCol == direction * (pawnFromRow - ourKingRow))
                        {
                            boolean enPassantCheckPasses = true;
                            boolean checkingEnPassantPawnCaptured = false;

                            // Handle en passant captures.
                            if (pawnToSquare == position.enPassantCaptureSquare)
                            {
                                // Do a check for en passant captures. It is possible that our pawn is not
                                // pinned, but still cannot capture in the following situation:
                                // K . . . P p . r.
                                // Capturing the en passant pawn would leave the king in check from the
                                // opponent's rook.
                                // Check if we are under check from a rook if the two pawns are removed.
                                if (pawnFromRow == ourKingRow && (MagicUtils.getRookAttackBitboard(ourKingSquare,
                                        occupiedSquaresBitboard
                                                & ~(pawnFromBitboard | Board.getSquareBitboard(pawnFromRow, pawnToCol)))
                                        & position.board.theirPieces & position.board.rooks) != 0)
                                {
                                    enPassantCheckPasses = false;
                                }

                                // Determine if we capture an en passant pawn that gives check.
                                // This is the case if the pawn is part of an attack line (and thus gives
                                // check).
                                if ((kingSafety.attackLines & Board.getSquareBitboard(pawnFromRow, pawnToCol)) != 0)
                                {
                                    checkingEnPassantPawnCaptured = true;
                                }
                            }

                            // Only continue if en passant check passes.
                            if (enPassantCheckPasses)
                            {
                                // Only continue if
                                // - It's not check or
                                // - It's check (but not double check, as assumed earlier) and the pawn moves to
                                // an attacking line (either interposing or capturing the only attacking piece)
                                // or a checking en passant pawn is captured. Note that this pawn can never
                                // expose the king to other checks once removed, as then our king would have
                                // been in check on the previous move.
                                if (!kingSafety.isCheck() || (pawnToBitboard & kingSafety.attackLines) != 0
                                        || checkingEnPassantPawnCaptured)
                                {
                                    if (pawnFromRow != 6)
                                    {
                                        // Normal move.
                                        legalMoves.add(new Move(pawnFromSquare, pawnToSquare));
                                    }
                                    else
                                    {
                                        // Promotion.
                                        legalMoves
                                                .add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.BISHOP));
                                        legalMoves
                                                .add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.KNIGHT));
                                        legalMoves
                                                .add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.QUEEN));
                                        legalMoves.add(new Move(pawnFromSquare, pawnToSquare, PromotionPieceType.ROOK));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Remove the pawn from the bitboard.
            ourPawns &= ourPawns - 1;
        }
    }
}
