package pu.chessdatabase.bo;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestBitSets
{
@Test
public void testBitSetOfByte()
{
	BitSet bitSet = bitSetOfByte( (byte) 0x11 );
	//System.out.println( bitSet );
	assertThat( bitSet.get( 0 ), is( true ) );
	assertThat( bitSet.get( 4 ), is( true ) );
	// assertThat doet niet automatisch een toString
	// assertThat( bitSet, is( "{0, 4}" ) );
	assertThat( bitSet.toString(), is( "{0, 4}" ) );
}
@Test
public void testAnd0x88()
{
	BitSet hex88 = bitSetOfInt( 0x88 );
	BitSet hex00 = bitSetOfInt( 0x00 );
	// System.out.println( hex88 );
	for ( int x = 0; x < 8; x++ )
	{
		BitSet bitSet = bitSetOfInt( x );
		bitSet.and( hex88 );
		//System.out.println( bitSet );
		assertThat( bitSet, is( hex00 ) );
	}
}
@Test
public void testBuitenBord()
{
	BitSet BuitenBord = bitSetOfInt( 0x88 );
	BitSet nul = bitSetOfInt( 0x00 );
	List<Integer> goodVelden= new ArrayList<>();
	int [] richting = Gen.Krichting;
	for ( int x = 0; x < 8; x++ )
	{
		int Veld = 0x11;
		int LastGoodVeld = 0xff;
		BitSet veldSet;
		do
		{
			LastGoodVeld = Veld;
			Veld += richting[x];
			veldSet = veldToBitSetAndBuitenBord( BuitenBord, Veld );
		}
		while ( veldSet.equals( nul ) );
		goodVelden.add( LastGoodVeld );
	}
	//printListInHex( goodVelden );
	assertThat( goodVelden, is( List.of( 0x17, 0x77, 0x71, 0x20, 0x10, 0x00, 0x01, 0x02 ) ) );
}
@Test
public void testBuitenBordMetVeld33()
{
	BitSet BuitenBord = bitSetOfInt( 0x88 );
	BitSet nul = bitSetOfInt( 0x00 );
	List<Integer> goodVelden= new ArrayList<>();
	int [] richting = Gen.Trichting;
	for ( int x = 0; x < 4; x++ )
	{
		int Veld = 0x33;
		int LastGoodVeld = 0xff;
		BitSet veldSet;
		do
		{
			LastGoodVeld = Veld;
			Veld += richting[x];
			veldSet = veldToBitSetAndBuitenBord( BuitenBord, Veld );
		}
		while ( veldSet.equals( nul ) );
		goodVelden.add( LastGoodVeld );
	}
	//printListInHex( goodVelden );
	assertThat( goodVelden, is( List.of( 0x37, 0x73, 0x30, 0x03 ) ) );
}
public void printListInHex( List<Integer> goodVelden )
{
	for ( Integer veld : goodVelden )
	{
		System.out.print( Integer.toHexString( veld ) + ", " );
	}
	System.out.println( "goodVelden =" + goodVelden );
}
public BitSet veldToBitSetAndBuitenBord( BitSet BuitenBord, int Veld )
{
	BitSet veldSet = bitSetOfInt( Veld );
	veldSet.and( BuitenBord );
	return veldSet;
}
private BitSet bitSetOfInt( int aInt )
{
	return bitSetOfByte( (byte) aInt );
}
private BitSet bitSetOfByte( byte aByte )
{
	byte [] bytes = new byte [] { aByte };
	return BitSet.valueOf( bytes );
}
@Test
public void test0x56And0x88()
{
	BitSet hex00 = bitSetOfInt( 0x00 );
	BitSet b0x56 = bitSetOfInt( 0x56 );
	BitSet b0x88 = bitSetOfInt( 0x88 );
	b0x56.and( b0x88 );
	assertThat( b0x56, is( hex00 ) );

}
/**
 0  1  2  3  4  5  6  7 
10 11 12 13 14 15 16 17 
20 21 22 23 24 25 26 27 
30 31 32 33 34 35 36 37 
40 41 42 43 44 45 46 47 
50 51 52 53 54 55 56 57 
60 61 62 63 64 65 66 67 
70 71 72 73 74 75 76 77 
 */
/**
00000000 00000001 00000010 00000011 00000100 00000101 00000110 00000111 
00010000 00010001 00010010 00010011 00010100 00010101 00010110 00010111 
00100000 00100001 00100010 00100011 00100100 00100101 00100110 00100111 
00110000 00110001 00110010 00110011 00110100 00110101 00110110 00110111 
01000000 01000001 01000010 01000011 01000100 01000101 01000110 01000111 
01010000 01010001 01010010 01010011 01010100 01010101 01010110*01010111
                                                      10001000 
01100000 01100001 01100010 01100011 01100100 01100101 01100110 01100111 
01110000 01110001 01110010 01110011 01110100 01110101 01110110 01110111 
BuitenBord 0x88 = 10001000
 */
//@Test
public void printHexTabel()
{
	for ( int rij = 0x00; rij < 0x77; rij += 0x10 )
	{
		for ( int kolom = 0; kolom < 8; kolom++ )
		{
			int getal = rij + kolom;
			if ( getal < 10 )
			{
				System.out.print( " " );
			}
			System.out.print( Integer.toHexString( getal ) + " " );
		}
		System.out.println();
	}
}
//@Test
public void printBinaireTabel()
{
	for ( int rij = 0x00; rij < 0x77; rij += 0x10 )
	{
		for ( int kolom = 0; kolom < 8; kolom++ )
		{
			int getal = rij + kolom;
//			if ( getal < 10 )
//			{
//				System.out.print( " " );
//			}
			System.out.print( Integer.toBinaryString( getal ) + " " );
		}
		System.out.println();
	}
	System.out.println( "BuitenBord 0x88 = " + Integer.toBinaryString( 0x88 ) );
}
}

