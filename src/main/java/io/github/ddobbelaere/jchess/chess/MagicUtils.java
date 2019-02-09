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
 * Utility class that provides fast access to attack bitboards of sliding pieces
 * (rooks, bishops and queens), for a given bitboard of occupied squares (note
 * that sliding pieces cannot jump over other pieces).
 * </p>
 *
 * <p>
 * The attack bitboards are stored in precomputed lookup tables that can be
 * accessed quickly using so-called magic bitboards.
 * </p>
 *
 * <p>
 * This implementation uses "fancy" magic bitboards.
 * </p>
 *
 * @see <a href=
 *      "https://www.chessprogramming.org/Magic_Bitboards">https://www.chessprogramming.org/Magic_Bitboards</a>.
 *
 * @author Dieter Dobbelaere
 */
class MagicUtils
{
	/**
	 * Lookup table holding rook attack bitboards.
	 */
	private static long[] rookAttackBitboards = new long[0x19000];

	/**
	 * Lookup table holding bishop attack bitboards.
	 */
	private static long[] bishopAttackBitboards = new long[0x1480];

	/**
	 * This holds all magic parameters (except magic number) for a given square that
	 * are needed to quickly calculate the index to the attack bitboards lookup
	 * table for a given bitboard of occupied squares.
	 */
	private static class MagicParameters
	{
		/**
		 * Mask applied to the bitboard of occupied squares.
		 */
		long mask;

		/**
		 * Base index to lookup table.
		 */
		int baseIndex;

		/**
		 * Number of right shifts applied during the index calculation.
		 */
		byte numShifts;
	}

	/**
	 * Magic numbers used to quickly lookup rook attack bitboards for each possible
	 * board square. These numbers have been determined via trial and error.
	 */
	private static final long[] rookMagicNumbers = { 0x0080006190804000L, 0x0040100020004008L, 0x1080100020008008L,
			0x0080060800801000L, 0x1280080080020400L, 0x0200080C01100200L, 0x4080008002001100L, 0x4180008000204100L,
			0x8000800080400020L, 0x0820400220015000L, 0x9000802000801000L, 0x0000801800100280L, 0x0000800400880081L,
			0x1002800600808400L, 0x00020001140A0008L, 0x0000800040802100L, 0x008000400040A001L, 0x0020088020804000L,
			0x0020010041001020L, 0x0088008010001880L, 0x0002110008000501L, 0x0800808002002400L, 0x1004050100020004L,
			0x0408060004044081L, 0x3000400080008020L, 0x0200200480400080L, 0x8020200080801000L, 0x4000080080100480L,
			0x4804002808008040L, 0x0000040080020080L, 0x008200020008410CL, 0x0400308200104403L, 0x6000C00080801420L,
			0x0020002040401000L, 0x0110001080806000L, 0x2008100080800800L, 0x0000800402800800L, 0x0120201018010440L,
			0x0082000402000108L, 0x8200004082000104L, 0x0000400020808000L, 0x0020100040004020L, 0x0000200100110040L,
			0x0000080010008080L, 0x0204008040080800L, 0x4001002400090002L, 0x0000020128040010L, 0x0200410380420034L,
			0x0000400080102080L, 0x8040400290200280L, 0x00043000A0008080L, 0x0100811000080480L, 0x0008000400088080L,
			0x0008020080040080L, 0x0081000C02000100L, 0x0080800100004080L, 0x0000208001084411L, 0x1004400020881101L,
			0x8010200101104009L, 0x0000090004201001L, 0x0002009084082002L, 0x0083000208040001L, 0x0010100801008204L,
			0x8004008420410402L };

	/**
	 * Magic numbers used to quickly lookup bishop attack bitboards for each
	 * possible board square. These numbers have been determined via trial and
	 * error.
	 */
	private static final long[] bishopMagicNumbers = { 0x01C004040C004010L, 0x0008020800410204L, 0x2204050202000030L,
			0x0208228120000000L, 0x0002021021000000L, 0x0801100804800103L, 0x0000580808080068L, 0x0800140201100802L,
			0x0800400A04010205L, 0x0180022488008300L, 0x0000080200520000L, 0x0800040409800000L, 0x0080045040000000L,
			0x0100808220209000L, 0x0080420201044020L, 0x4000002082482000L, 0x02300420200200A0L, 0x6002140810010200L,
			0x0014080800206200L, 0x4402002020204000L, 0x0000800400A00020L, 0x0000801100600200L, 0x4004003201840408L,
			0x00008042008C0140L, 0x0004200110021000L, 0x0008040002101200L, 0x4208044408020020L, 0x0800802008020020L,
			0x0001010000104000L, 0x0001020101004110L, 0x0004004104021200L, 0x0100408803440400L, 0x80040220800B2000L,
			0x0001242000108100L, 0x0402080200040020L, 0x4000420080480080L, 0x0420820200040090L, 0x1010040020081201L,
			0x0010042040010110L, 0x00620A0821020880L, 0x0000881808004020L, 0x0040410410002000L, 0x0020201402001000L,
			0x0004002018000100L, 0x4000040408208400L, 0x0220581004C02420L, 0x0802100401008384L, 0x0002808400800100L,
			0x0001009010080000L, 0x00090402940C0010L, 0x1000004404040C00L, 0x0012000020880002L, 0x60000030A2020000L,
			0x10000A2008088008L, 0x8011200101020004L, 0x0002100102088000L, 0x0020210101A02004L, 0x3000810841046000L,
			0x0420000100889000L, 0x0101060000A08821L, 0x0800000010621200L, 0x0080000424480200L, 0x0000400408020040L,
			0x0002020801210200L };

	/**
	 * Rook magic parameters.
	 */
	private static MagicParameters[] rookMagicParameters = new MagicParameters[64];

	/**
	 * Bishop magic parameters.
	 */
	private static MagicParameters[] bishopMagicParameters = new MagicParameters[64];

	static
	{
		init();
	}

	/**
	 * Initialize all internal structures.
	 */
	private static void init()
	{
		// Initialize arrays.
		for (int square = 0; square < 64; square++)
		{
			rookMagicParameters[square] = new MagicParameters();
			bishopMagicParameters[square] = new MagicParameters();
		}

		// Initialize masks.
		initMasks();

		// Initialize lookup tables for rooks and bishops.
		initLookupTables(rookMagicNumbers, rookMagicParameters, rookAttackBitboards);
		initLookupTables(bishopMagicNumbers, bishopMagicParameters, bishopAttackBitboards);
	}

	/**
	 * Initialize all masks applied to the bitboard of occupied squares.
	 */
	private static void initMasks()
	{
		// Store the chess board's edges in a temporary bitboard.
		long boardEdgesBitboard = ChessBoard.getRowBitboard(0) | ChessBoard.getRowBitboard(7)
				| ChessBoard.getColBitboard(0) | ChessBoard.getColBitboard(7);

		for (int square = 0; square < 64; square++)
		{
			int row = square / 8;
			int col = square % 8;

			// Rook masks are obtained by adding the row and column bitboards and
			// subtracting the rook square itself and the squares at the row and column's
			// edges.
			rookMagicParameters[square].mask = (ChessBoard.getRowBitboard(row) | ChessBoard.getColBitboard(col))
					& ~(ChessBoard.getSquareBitboard(square) | ChessBoard.getSquareBitboard(0, col)
							| ChessBoard.getSquareBitboard(7, col) | ChessBoard.getSquareBitboard(row, 0)
							| ChessBoard.getSquareBitboard(row, 7));

			// Bishop masks are obtained by/ subtracting the bishop square itself and the
			// squares at the board's edges from the diagonals bitboard.
			bishopMagicParameters[square].mask = ChessBoard.getDiagsBitboard(row, col)
					& ~(ChessBoard.getSquareBitboard(square) | boardEdgesBitboard);
		}
	}

	/**
	 * Initialize the attack bitboards lookup tables for a given sliding piece type.
	 *
	 * @param magicNumbers    Array holding the magic numbers for the sliding piece
	 *                        type.
	 * @param magicParameters Array holding the magic parameters for the sliding
	 *                        piece type.
	 * @param attackBitboards Lookup table of attack bitboards for the sliding piece
	 *                        type.
	 */
	private static void initLookupTables(long[] magicNumbers, MagicParameters[] magicParameters, long[] attackBitboards)
	{
		for (int square = 0; square < 64; square++)
		{

		}
	}

	/**
	 * Get the rook attack bitboard for the given square and the given bitboard of
	 * occupied squares.
	 *
	 * @param square          Given rook square.
	 * @param occupiedSquares Given bitboard of occupied squares.
	 * @return Rook attack bitboard.
	 */
	static long getRookAttackBitboard(final int square, final long occupiedSquaresBitboard)
	{
		MagicParameters params = rookMagicParameters[square];
		return rookAttackBitboards[params.baseIndex
				+ (int) ((rookMagicNumbers[square] * (occupiedSquaresBitboard & params.mask)) >> params.numShifts)];
	}

	/**
	 * Get the bishop attack bitboard for the given square and the given bitboard of
	 * occupied squares.
	 *
	 * @param square          Given bishop square.
	 * @param occupiedSquares Given bitboard of occupied squares.
	 * @return Bishop attack bitboard.
	 */
	static long getBishopAttackBitboard(final int square, final long occupiedSquaresBitboard)
	{
		MagicParameters params = bishopMagicParameters[square];
		return bishopAttackBitboards[params.baseIndex
				+ (int) ((bishopMagicNumbers[square] * (occupiedSquaresBitboard & params.mask)) >> params.numShifts)];
	}

}
