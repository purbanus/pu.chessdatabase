package pu.chessdatabase.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Gemaakt omdat de enum StukType ze niet uit Stukken kon halen, omdat ze daar niog null waren.
 * Geen idee waarom dit wel werkt.
 */
public class Richtingen
{
public static final List<Integer> KRICHTING = Arrays.asList( 0x01, 0x11,  0x10,  0x0F, -0x01, -0x11, -0x10, -0x0F );
public static final List<Integer> TRICHTING = Arrays.asList( 0x01, 0x10, -0x01, -0x10 );
public static final List<Integer> LRICHTING = Arrays.asList( 0x11, 0x0F, -0x11, -0x0F );
public static final List<Integer> PRICHTING = Arrays.asList( 0x12, 0x21,  0x1F,  0x0E, -0x12, -0x21, -0x1F, -0x0E );
public static final List<Integer> GRICHTING = new ArrayList<>();
}