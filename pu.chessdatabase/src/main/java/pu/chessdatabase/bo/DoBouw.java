package pu.chessdatabase.bo;

public class DoBouw
{

public static void main( String [] args )
{
	new DoBouw().run();
}

public void run()
{
	Bouw bouw = new Bouw();
	bouw.bouwDatabase();
}

}
