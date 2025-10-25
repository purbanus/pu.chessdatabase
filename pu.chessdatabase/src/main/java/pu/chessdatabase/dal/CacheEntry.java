package pu.chessdatabase.dal;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
public class CacheEntry
{
private PageDescriptor pageDescriptor;
@ToString.Exclude
private Page page;
private boolean vuil;
private long generatie;
}
