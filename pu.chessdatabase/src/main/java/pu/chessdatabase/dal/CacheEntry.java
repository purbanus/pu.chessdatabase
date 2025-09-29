package pu.chessdatabase.dal;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class CacheEntry
{
private PageDescriptor pageDescriptor;
private Page page;
private boolean vuil;
private long generatie;
}
