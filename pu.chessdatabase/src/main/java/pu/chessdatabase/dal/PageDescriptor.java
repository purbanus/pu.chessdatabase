package pu.chessdatabase.dal;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class PageDescriptor
{ 
private Lokatie waar;
private int cacheNummer; // Ik denk: nummer in de cache
private long schijfAdres;
}
