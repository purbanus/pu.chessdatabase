package pu.chessdatabase.dal;

//====================================================================================================================
//BELANGRIJK
//In Eclipse kan hij de volgende twee imports niet vinden. Deze moet je dus met de hand toevoegen
//===================================================================================================================== 
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pu.chessdatabase.bo.Bouw;

@SpringBootTest
public class TestVmStellingIterator
{
@Autowired private VMStellingIterator vmStellingIterator;
@Autowired private Bouw bouw;

@Test // Dit is goed
public void testCloneArray()
{
	int[] array = {23, 43, 55, 12};
	int[] copiedArray = array.clone();
	assertArrayEquals(copiedArray, array);
	array[0] = 9;
	assertTrue(copiedArray[0] != array[0]);
	//System.out.println( array.length );
}
//@Test // Dit is fout
public void testCloneArrayOfInt()
{
	int [][] array = new int [][] { { 1, 2, 3, 4 },{ 5, 6, 7, 8 } }; 
	int [][] copiedArray = array.clone();
	assertArrayEquals(copiedArray, array);
	array[0][0] = 9;
	assertTrue(copiedArray[0][0] != array[0][0]);
	assertThat( copiedArray[0][0], is( not( array[0][0] ) ) );
}
@Test
public void testCloneLongArray()
{
	int [][] array = new int [][] { { 1, 2, 3, 4 },{ 5, 6, 7, 8 } }; 
	int [][] clonedArray = VMStellingIterator.cloneArray( array );
	array[0][0] = 9;
	assertTrue(clonedArray[0][0] != array[0][0]);
	assertThat( clonedArray[0][0], is( not( array[0][0] ) ) );
}
@Test
public void testSetReport()
{
	int [][] totals = new int [][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
	vmStellingIterator.setTellingen( totals );
	vmStellingIterator.setStellingTeller( 1500 );
	vmStellingIterator.setReportFunction( null );
	vmStellingIterator.setReportFrequency( 0 );
	vmStellingIterator.setReport( 5000, bouw::showTellers, false );
	
	totals = new int [][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
	assertThat( vmStellingIterator.getTellingen(), is( totals ) ); 
	assertThat( vmStellingIterator.getStellingTeller(), is( 0 ) ); 
	// Niet mogelijk assertThat( vmStellingIterator.getReportFunction(), is( bouw::showTellers ) ); 
	assertThat( vmStellingIterator.getReportFunction(), is( notNullValue() ) );
	assertThat( vmStellingIterator.getReportFrequency(), is( 5000 ) ); 
	assertThat( vmStellingIterator.isDoAllPositions(), is( false ) ); 
}
// @@NOG De rest. Er wordt al flink wat getest in Dbs en Bouw
}
