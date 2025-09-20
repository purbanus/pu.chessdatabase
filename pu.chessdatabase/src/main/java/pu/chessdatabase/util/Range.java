package pu.chessdatabase.util;

/**
 * De versie in mc.base laat een empty range niet toe, en empty ranges zijn erg handig
 */
public class Range implements Cloneable
{
	public static final Range EMPTY_RANGE = new Range( 1, 0 );

	public final int from;
    public final int to;

/**
 * Creates a new Range from the endpoints. 
 */
public Range( int aFrom, int aTo )
{
    super();
    if ( aFrom > aTo + 1 )
    {
        throw new RuntimeException( "From may not be larger than to: " + aFrom + " " + aTo );
    }
    from = aFrom;
    to = aTo;
}

public int getFrom()
{
	return from;
}

public int getTo()
{
	return to;
}
public int getMinimum()
{
	return from;
}

public int getMaximum()
{
	return to;
}


/**
 * Returns a new copy of this Range.
 */
@Override
public Object clone()
{
    // Aangezien we immutable zijn retourneren we gewoon onszelf! cool!
    return this;
}

/**
 * Returns whether this Range contains the specified value.
 * @param i The value under scrutiny
 */
public boolean contains( int i )
{
    return i >= from && i <= to;
}

/**
 * Returns whether this Range totally contains the other Range.
 * @param aRange The other Range
 */
public boolean contains( Range aRange )
{
	return aRange.from >= from && aRange.to <= to;
}

@Override
public boolean equals( Object aObject )
{
    if ( ! ( aObject instanceof Range ) )
    {
        return false;
    }
    return equals( (Range) aObject );
}

public boolean equals( Range aRange )
{
    return from == aRange.from && to == aRange.to;
}

/**
 * Returns the length of this range. that is the number of values it encompasses.
 */
public int getLength()
{
	return to - from + 1;
}

/**
 * Generates a hash code for the receiver.
 * This method is supported primarily for
 * hash tables, such as those provided in java.util.
 * @return an integer hash code for the receiver
 * @see java.util.Hashtable
 */
@Override
public int hashCode()
{
	// Meestal krijg je goede hashcodes door de data-elementen met elkaar te XORen.
    // HIGH Maar het hangt ook af van welke data er in de praktijk is. Beetje testen nog
	return from ^ to;
}

/**
 * Returns the common part of this Range with another Range.
 * Returns <code>null</code> if the intersection is empty.
 */
public Range intersectWith( Range aRange )
{
	int newFrom = Math.max( from, aRange.from );
	int newTo   = Math.min( to, aRange.to );
	return newFrom > newTo ? null : new Range( newFrom, newTo );
}

/**
 * Returns whether there are values in our range and in the other Range
 */
// HIGH Is dit niet intersectWith( aRange ) != null ?
public boolean overlapsWith( Range aRange )
{
	return aRange.to >= from && aRange.from <= to;
}

@Override
public String toString()
{
    return "Range: " + from + "-" + to;
}
}
