package pu.chessdatabase.bo.speel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Plies
{
private int currentPly = 0;
private int lastPly = 0;
private boolean begonnen = false;
private List<Ply> plies = new ArrayList<>();
}
