package pu.chessdatabase.dbs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageDescriptor
{ 
private Lokatie waar;
private int cacheNummer; // Ik denk: nummer in de cache
private long schijfAdres;
}
